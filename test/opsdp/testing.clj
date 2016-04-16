(ns opsdp.testing
  (:require [clojure.test :refer :all])
  (:require [opdsp.core :refer [handler standalone-routes]])
  (:require [ring.adapter.jetty :refer [run-jetty]])
  (:require [clj-http.client :as client])
  )

(defn opds-p-server [f]
  (println "start")
  (let [server (run-jetty standalone-routes {:port 3001 :join? false})]
    (f)
    (.stop server)
    )

  (println "stop")

  )

(use-fixtures :each opds-p-server)


(deftest dirUnautenticated
  (is (= 401
         (:status (client/get "http://localhost:3001/opds-p/dir/" {:throw-exceptions false})) ))
  )

(deftest dirAutenticated
  (is (= "<?xml version=\"1.0\" encoding=\"UTF-8\"?><feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:pse=\"http://vaemendis.net/opds-pse/ns\" xmlns:opds=\"http://opds-spec.org/2010/catalog\" xml:lang=\"en\" xmlns:opensearch=\"http://a9.com/-/spec/opensearch/1.1/\"><title>Books</title><entry><title>books</title><content type=\"html\"></content><link type=\"application/atom+xml; profile=opds-catalog; kind=acquisition\" kind=\"acquisition\" rel=\"subsection\" href=\"/opds-p/dir/books/\"></link></entry><entry><title>technicalBooks</title><content type=\"html\"></content><link type=\"application/atom+xml; profile=opds-catalog; kind=acquisition\" kind=\"acquisition\" rel=\"subsection\" href=\"/opds-p/dir/technicalBooks/\"></link></entry></feed>"
         (:body (client/get "http://localhost:3001/opds-p/dir/" {:basic-auth "aaa:ttt"}))))
  )