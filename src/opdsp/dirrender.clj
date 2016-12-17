(ns opdsp.dirrender
  (:require [clojure.string :as string]
            [opdsp.davaccess :as davaccess]
            [clj-xpath.core :as xp]
            [clojure.java.io :as io]
            [opdsp.shared :refer :all]
            [opdsp.opds :as opds]
            [clojure.data.xml :as xml]
            ))

(defn allowedDirs [] (:paths opdsp.shared/*settings*))

(defn userIsAllowed [input]
  (println "userIsAllowed=" input)
  (let [settings
        (opdsp.shared/loadSettings (:username input))
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

