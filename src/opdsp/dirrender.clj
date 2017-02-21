(ns opdsp.dirrender
  (:require [clojure.string :as string]
            [opdsp.davaccess :as davaccess]
            [clj-xpath.core :as xp]
            [clojure.java.io :as io]
            [opdsp.shared :refer :all]
            [opdsp.opds :as opds]
            [clojure.data.xml :as xml]
            ))

(defn allowedDirs [] (-> opdsp.shared/*userSettings* :catalog :paths))


(defn removePrifix [str prefix] (if (string/starts-with? str prefix) (subs str (count prefix)) str))

(defn allowedPath [path] (some #(string/starts-with? (removePrifix path "/") %) (allowedDirs)))

(defn respEntry [respnode]
  {
   :href        (xp/$x:text "./href" respnode)
   :displayname (xp/$x:text "./propstat/prop/displayname" respnode)
   :collection  (not (nil? (xp/$x:tag? "./propstat/prop/resourcetype/collection" respnode)))
   })

(defn file [request path context]
  (if (allowedPath path)
    {:status  200
     :headers {}
     :body    (io/input-stream (davaccess/loadFile path))}
    {:status 404
     :body   "Not Found"}
    ))

(defn dirEntriesList [path] (binding [opds/*pathPrefix* ""]
                              (->> (davaccess/loadList path)
                                   (xp/$x "*//response" )
                                   (map respEntry ))))

(defn dir [request path context]
  {:status  200
   :headers {"Content-Type"  "text/xml; charset=utf-8"
             "Cache-Control" "no-cache, no-store, must-revalidate"
             "Pragma"        "no-cache"
             "Expires"       "0"}
   :body    (let [entries (dirEntriesList path)]
              (binding [opds/*pathPrefix* context]
                (xml/emit-str (opds/documentTagData (->> entries
                                                         (filter #(allowedPath (% :href)))
                                                         ))))
              )})

