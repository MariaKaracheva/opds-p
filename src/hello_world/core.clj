(ns hello-world.core
  (:require [ring.util.response :refer [response content-type]]))

;(require ['ring.util.response (:refer [response content-type ])])

(defn handler [request]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (concat "Hello Worldff3" (:query-string request))})

;(defn handler [request]
;  (-> (response "Hello World6")
;      (content-type "text/plain")))