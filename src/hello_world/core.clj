(ns hello-world.core
  (:require [ring.util.response :refer [response content-type]])
  (:require [hello-world.davaccess :as davaccess]
            [clojure.string :as string]
            [clojure.data.xml :as xml]
            [clj-xpath.core :as xp]
            [hello-world.opds :as opds]
            [compojure.core :refer :all]
            [compojure.route :as route]
            )
  )


(defn respEntry [respnode]
  {
   :href        (xp/$x:text "./href" respnode)
   :displayname (xp/$x:text "./propstat/prop/displayname" respnode)
   :collection  (not (nil? (xp/$x:tag? "./propstat/prop/resourcetype/collection" respnode)))
   })


(defn dir [request path]
  (do (println "uri " (:uri request) path)
      {:status  200
       :headers {"Content-Type" "text/xml; charset=utf-8"}
       ;:body    (concat "Hello Worldff3" (:query-string request) (davaccess/loadList ""))
       ;:body    (let [davxml (slurp "yandex.xml") parsed (xp/$x "*//prop" davxml)]
       ;           (string/join "\n" (->> parsed
       ;                                  (map #(:node %))
       ;                                  (map #(xp/$x:text "./displayname" %))
       ;                                  ;(map #(apply :text %))
       ;                                  )))
       :body    (binding [opds/pathPrefix "/dir"]
                  (let [
                      ;davxml (slurp "yandex.xml")
                        davxml (davaccess/loadList path)
                      parsed (xp/$x "*//response" davxml)]
                    (xml/emit-str (opds/documentTagData (->> parsed
                                                             (map respEntry)
                                                             )))))
       }))

(defroutes handler
           (GET "/dir/:path{.*}" [path :as request] (dir request path))
           (route/not-found "<h1>Page not found</h1>"))


