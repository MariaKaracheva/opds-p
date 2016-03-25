(ns hello-world.core
  (:require [ring.util.response :refer [response content-type]])
  (:require [hello-world.davaccess :as davaccess]
            [clojure.string :as string]
            [clj-xpath.core :as xp]
            )
  )


(defn respEntry [respnode]
  {
   :href        (xp/$x:text "./href" respnode)
   :displayname (xp/$x:text "./propstat/prop/displayname" respnode)
   :collection  (not (nil? (xp/$x:tag? "./propstat/prop/resourcetype/collection" respnode)))
   })


(defn handler [request]
  {:status  200
   :headers {"Content-Type" "text/xml; charset=utf-8"}
   ;:body    (concat "Hello Worldff3" (:query-string request) (davaccess/loadList ""))
   ;:body    (let [davxml (slurp "yandex.xml") parsed (xp/$x "*//prop" davxml)]
   ;           (string/join "\n" (->> parsed
   ;                                  (map #(:node %))
   ;                                  (map #(xp/$x:text "./displayname" %))
   ;                                  ;(map #(apply :text %))
   ;                                  )))
   :body    (let [davxml (slurp "yandex.xml")
                  parsed (xp/$x "*//response" davxml)]
              (string/join "\n" (->> parsed
                                     (map respEntry)
                                     ;(map #(xp/$x:text "./displayname" %))
                                     ;(map #(apply :text %))
                                     )))
   })


