(ns hello-world.core
  (:require [ring.util.response :refer [response content-type]])
  (:import (java.net URL HttpURLConnection)))


(defn loadList [path] (let [
                            url  (URL.  "http://localhost:7000/")
                            ;url  (URL.  "http://uits-labs.ru/")
                            conn (cast HttpURLConnection (.openConnection url))
                            ]
                        (do
                          (.setRequestMethod conn "PROPFIND")
                            (slurp (.getInputStream conn)))))

(defn handler [request]
  {:status  200
   :headers {"Content-Type" "text/plain; charset=utf-8"}
   :body    (concat "Hello Worldff3" (:query-string request) (loadList "" ))})

;(defn handler [request]
;  (-> (response "Hello World6")
;      (content-type "text/plain")))

