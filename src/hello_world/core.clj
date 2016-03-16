(ns hello-world.core
  (:require [ring.util.response :refer [response content-type]])
  (:import (java.net URL HttpURLConnection URI)
           (org.apache.http.impl.client HttpClients BasicResponseHandler)
           (org.apache.http.client.methods HttpGet HttpRequestBase)))


;(defn loadList [path] (let [
;                            url  (URL.  "http://localhost:7000/")
;                            ;url  (URL.  "http://uits-labs.ru/")
;                            conn (cast HttpURLConnection (.openConnection url))
;                            ]
;                        (do
;                          (.setRequestMethod conn "PROPFIND")
;                            (slurp (.getInputStream conn)))))


(defn loadList [path] (let [
                            client (HttpClients/createDefault )
                            ;url  (HttpGet. "http://localhost:7000/")
                            ;url  (HttpGet. "http://localhost:7000/")
                            ;get  (HttpGet.  "http://uits-labs.ru/")
                            get (doto (proxy [HttpRequestBase] []
                                   (getMethod
                                     []
                                     (str "PROPFIND") ) ) (.setURI (URI/create "http://localhost:7000/")))
                            ]
                        (.execute client get (BasicResponseHandler.))) )


(defn handler [request]
  {:status  200
   :headers {"Content-Type" "text/plain; charset=utf-8"}
   :body    (concat "Hello Worldff3" (:query-string request) (loadList "" ))})

;(defn handler [request]
;  (-> (response "Hello World6")
;      (content-type "text/plain")))

