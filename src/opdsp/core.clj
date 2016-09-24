(ns opdsp.core
  (:require [ring.util.response :refer [response content-type]])
  (:require [opdsp.davaccess :as davaccess]
            [clojure.string :as string]
            [clojure.data.xml :as xml]
            [clj-xpath.core :as xp]
            [opdsp.opds :as opds]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])
            [hawk.core :as hawk]
            )
  )

(defn allowedDirs [] (:paths davaccess/*settings*))

(defn userIsAllowed [input]
  (println "userIsAllowed=" input)
  (let [settings
        (davaccess/loadSettings (:username input))
        ;davaccess/*settings*
        allowed (= (:password settings) (:password input))]
    (println "Settings=" settings)
    (println "passwrd=" (:password settings) (:password input))
    (println "allowed=" allowed)
    allowed))

(defn removePrifix [str prefix] (if (string/starts-with? str prefix) (subs str (count prefix)) str))

(defn allowedPath [path] (some #(string/starts-with? (removePrifix path "/") %) (allowedDirs)))

(defn respEntry [respnode]
  {
   :href        (xp/$x:text "./href" respnode)
   :displayname (xp/$x:text "./propstat/prop/displayname" respnode)
   :collection  (not (nil? (xp/$x:tag? "./propstat/prop/resourcetype/collection" respnode)))
   })

(defn file [request path context]
  (do (println "uri " (:uri request) path request)
      (if (allowedPath path)
        {:status  200
         :headers {}
         :body    (io/input-stream (davaccess/loadFile path))}
        {:status 404
         :body   "Not Found"}
        )
      ))

(defn dir [request path context]
  (do (println "uri " (:uri request) path context request)
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
                    ;(spit "testsamples/dav-0.xml" davxml)
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



(defn wrap-settings [handler]
  (fn [request]
    (binding [davaccess/*settings* (davaccess/loadSettings (:username (friend/current-authentication request)))]
      (handler request))))

(def opds-p-handler
  (-> handler-inner
      (wrap-settings)
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
           (context "/opds-p" req opds-p-handler)
           (route/not-found "Not Found"))

(def watcher (atom nil))

(defn init []
  (println "init")
  )
(defn destroy [] (println "destroy") (hawk/stop! @watcher))

;(def standalone-app
;  (wrap-defaults standalone-routes site-defaults))


