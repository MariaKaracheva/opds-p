(ns hello-world.davaccess
  (:import (org.apache.http.impl.client BasicResponseHandler HttpClients BasicCredentialsProvider HttpClientBuilder BasicAuthCache LaxRedirectStrategy)
           (java.net URI)
           (org.apache.http.client.methods HttpRequestBase)
           (org.apache.http.auth AuthScope UsernamePasswordCredentials)
           (org.apache.http.client.protocol HttpClientContext)
           (org.apache.http HttpHost)
           (org.apache.http.impl.auth BasicScheme)))

(require ['lock-key.core :refer ['decrypt 'decrypt-as-str 'decrypt-from-base64]])


(defn basicContextFor [^String host ^Integer port] (doto (HttpClientContext/create)
                                                     (.setAuthCache (doto (BasicAuthCache.)
                                                                      (.put (HttpHost. host port) (BasicScheme.))))))

(def key (let [file (clojure.string/join "/" [(java.lang.System/getenv "HOME") ".opds-p" "key"])]
           (decrypt-from-base64 (slurp file) "9qPBq1kFkOfPy5w9")
           ))

(defn PROPFIND [url] (doto (proxy [HttpRequestBase] []
                             (getMethod
                               []
                               (str "PROPFIND"))) (.setURI (URI/create url))))


;CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
;credentialsProvider.setCredentials(AuthScope.ANY,
;                                    new UsernamePasswordCredentials("username", "password"));

(defn loadList [path] (
                        with-open [client (.build (doto (HttpClientBuilder/create)
                                                    ;(.setDefaultCredentialsProvider (doto (BasicCredentialsProvider.)
                                                    ;                                  (.setCredentials AuthScope/ANY (UsernamePasswordCredentials. "lkuka", "Ap7phei:x"))))
                                                    ;(.setRedirectStrategy (LaxRedirectStrategy.))
                                                    ))]
                        (let [
                              ;url  (HttpGet. "http://localhost:7000/")
                              ;url  (HttpGet. "http://localhost:7000/")
                              ;get  (HttpGet.  "http://uits-labs.ru/")
                              get (doto (PROPFIND (str "https://webdav.yandex.ru/" path))
                                    (.addHeader "Depth" "1")
                                    (.addHeader "Accept", "*/*")
                                    (.addHeader "Authorization" (str "OAuth " key))
                                    )
                              ]
                          (.execute client get (BasicResponseHandler.)))))

(defn filesList [body] ())
