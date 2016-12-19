(ns opsdp.testcatalog
  (:require [clojure.test :refer :all])
  (:require [opdsp.core :refer [opds-p-handler standalone-routes]])
  (:require [ring.adapter.jetty :refer [run-jetty]])
  (:require [clj-http.client :as client])
  (:require [compojure.core :refer :all]
            [compojure.route :as route])
  )

(defmacro PROPFIND "Generate a `PROPFIND` route."
  [path args & body]
  (compile-route :propfind path args body))

(defn webdav-mock [f]
  (let [server (run-jetty (routes
                            (PROPFIND "/*" request
                                      (println "request=" request)
                                      (assert (= "OAuth aaa+ttt" (-> request :headers (get "authorization"))))
                                      (slurp "testsamples/dav-root.xml"))
                            (GET "/*" request
                              (println "request=" request)
                              "dummy pdf file"
                              )
                            )
                          {:port 2999 :join? false})]
    (with-redefs [opdsp.davaccess/webdavserver {
                                                :scheme "http"
                                                :host   "localhost"
                                                :port   2999
                                                }] (f))
    (.stop server)
    ))

(defn mock-settings [f]
  (let [testEntity {
                    :key     "aaa+ttt",
                    :login   "aaa"
                    :catalog {
                              :auth  {
                                      :login    "aaa",
                                      :password "ttt",
                                      }

                              :paths ["books", "technicalBooks"]
                              }
                    }]
    (with-redefs [
                  opdsp.shared/loadUserSettings (fn [_] testEntity)
                  opdsp.shared/loadUserSettingsByCatalogAuth
                  (fn [login password] (if (and (= login (-> testEntity :catalog :auth :login))
                                                (= password (-> testEntity :catalog :auth :password))
                                                ) testEntity))
                  ]
      (f))))

(defn opds-p-server [f]
  (println "start")
  (let [server (run-jetty standalone-routes {:port 3001 :join? false})]
    (f)
    (.stop server)
    )
  (println "stop")
  )

(use-fixtures :once
              webdav-mock
              mock-settings
              opds-p-server
              )


(deftest dirUnautenticated
  (let [response (client/get "http://localhost:3001/opds-p/dir/" {:throw-exceptions false})]
    (is (= 401 (:status response)))
    (is (= "Basic realm=\"opds-p\"" (-> response :headers (get "WWW-Authenticate"))))
    ))


(deftest dirAutenticated
  (let [response (client/get "http://localhost:3001/opds-p/dir/" {:basic-auth "aaa:ttt" :throw-exceptions false})]
    (println "response=" response)
    (is (= 200 (:status response)))
    (is (= "<?xml version=\"1.0\" encoding=\"UTF-8\"?><feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:pse=\"http://vaemendis.net/opds-pse/ns\" xmlns:opds=\"http://opds-spec.org/2010/catalog\" xml:lang=\"en\" xmlns:opensearch=\"http://a9.com/-/spec/opensearch/1.1/\"><title>Books</title><entry><title>books</title><content type=\"html\"></content><link type=\"application/atom+xml; profile=opds-catalog; kind=acquisition\" kind=\"acquisition\" rel=\"subsection\" href=\"/opds-p/dir/books/\"></link></entry><entry><title>technicalBooks</title><content type=\"html\"></content><link type=\"application/atom+xml; profile=opds-catalog; kind=acquisition\" kind=\"acquisition\" rel=\"subsection\" href=\"/opds-p/dir/technicalBooks/\"></link></entry></feed>"
           (:body response))))
  )

(deftest fileAvaliable
  (let [response (client/get "http://localhost:3001/opds-p/file/books/%D0%A3%D0%BF%D1%80%D0%B0%D0%B6%D0%BD%D0%B5%D0%BD%D0%B8%D1%8F%20%D1%88%D0%B5%D0%B8.pdf"
                             {:basic-auth "aaa:ttt" :throw-exceptions false})]
    (is (= 200 (:status response)))
    (is (= "dummy pdf file" (:body response)))
    )
  )
(deftest fileUnautenticated
  (let [response (client/get "http://localhost:3001/opds-p/file/books/%D0%A3%D0%BF%D1%80%D0%B0%D0%B6%D0%BD%D0%B5%D0%BD%D0%B8%D1%8F%20%D1%88%D0%B5%D0%B8.pdf"
                             {:basic-auth "aaa:ttt1" :throw-exceptions false})]
    (is (= 401 (:status response)))
    (is (= "Basic realm=\"null\"" (-> response :headers (get "WWW-Authenticate"))))
    (is (= "" (:body response)))
    )
  )

