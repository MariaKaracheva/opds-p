(ns hello-world.core
  (:require [ring.util.response :refer [response content-type]])
  (:require [hello-world.davaccess :as davaccess]
            [clojure.string :as string]
            [clojure.data.xml :as xml]
            [clj-xpath.core :as xp]
            [hello-world.opds :as opds]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])
            [hawk.core :as hawk]
            )
  )

(defn allowedDirs [] (:paths (first @davaccess/settings)))
(println "allowedDirs=" (allowedDirs))

(defn userIsAllowed [input] (some #(and (= (:login %) (:username input)) (= (:password %) (:password input))) @davaccess/settings))

(defn removePrifix [str prefix] (if (string/starts-with? str prefix) (subs str (count prefix)) str))

(defn allowedPath [path] (some #(string/starts-with? (removePrifix path "/") %) (allowedDirs)))

(defn respEntry [respnode]
  {
   :href        (xp/$x:text "./href" respnode)
   :displayname (xp/$x:text "./propstat/prop/displayname" respnode)
   :collection  (not (nil? (xp/$x:tag? "./propstat/prop/resourcetype/collection" respnode)))
   })

(defn file [request path context]
  (do (println "uri " (:uri request) path)
      (if (allowedPath path)
        {:status  200
         :headers {}
         :body    (io/input-stream (davaccess/loadFile path))}
        {:status 404
         :body   "Not Found"}
        )
      ))

(defn dir [request path context]
  (do (println "uri " (:uri request) path context)
      {:status  200
       :headers {"Content-Type" "text/xml; charset=utf-8"}
       ;:body    (concat "Hello Worldff3" (:query-string request) (davaccess/loadList ""))
       ;:body    (let [davxml (slurp "yandex.xml") parsed (xp/$x "*//prop" davxml)]
       ;           (string/join "\n" (->> parsed
       ;                                  (map #(:node %))
       ;                                  (map #(xp/$x:text "./displayname" %))
       ;                                  ;(map #(apply :text %))
       ;                                  )))
       :body    (binding [opds/pathPrefix ""]
                  (let [
                      ;davxml (slurp "yandex.xml")
                        davxml (davaccess/loadList path)
                      parsed (xp/$x "*//response" davxml)]
                    (binding [opds/pathPrefix context]
                      (xml/emit-str (opds/documentTagData (->> parsed
                                                               (map respEntry)
                                                               (filter #(allowedPath (% :href)))
                                                               ))))))
       }))

(defroutes handler-inner
           (GET "/dir/:path{.*}" [path :as request] (dir request path (:context request)))
           (GET "/file/:path{.*}" [path :as request] (file request path (:context request)))
           (route/not-found "<h1>Page not found</h1>"))

(def handler
  (-> handler-inner
      (friend/authenticate {
                            :allow-anon?             false
                            :unauthenticated-handler #(workflows/http-basic-deny "opds-p" %)
                            :credential-fn           (fn [input]
                                             (let [allowed (userIsAllowed input)]
                                               (if allowed {:identity (:username input)} nil)))
                            :workflows               [(workflows/http-basic)]
                            })
      ; ...required Ring middlewares ...
      ))

(defroutes standalone-routes
           (context "/opds-p" req handler)
           (route/not-found "Not Found"))

(def watcher (atom nil))

(defn init []
  (println "init")
  (reset! watcher
          (hawk/watch! [
                        {:paths   [davaccess/settingsPath]
                         :handler (fn [ctx e]
                                    (reset! davaccess/settings (davaccess/loadSettings))
                                    ctx)}])))
(defn destroy [] (println "destroy") (hawk/stop! @watcher))

;(def standalone-app
;  (wrap-defaults standalone-routes site-defaults))


