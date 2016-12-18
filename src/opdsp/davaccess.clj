(ns opdsp.davaccess
  (:import (org.apache.http.impl.client BasicResponseHandler HttpClients BasicCredentialsProvider HttpClientBuilder BasicAuthCache LaxRedirectStrategy CloseableHttpClient)
           (java.net URI)
           (org.apache.http.client.methods HttpRequestBase HttpGet)
           (org.apache.http.auth AuthScope UsernamePasswordCredentials)
           (org.apache.http.client.protocol HttpClientContext)
           (org.apache.http HttpHost)
           (org.apache.http.impl.auth BasicScheme)
           (org.apache.http.client ResponseHandler)
           (org.apache.http.util EntityUtils)
           (java.io File))
  (:require [clj-yaml.core :as yaml]
            [opdsp.shared :refer :all]))


(defn basicContextFor [String host ^Integer port] (doto (HttpClientContext/create)
                                                    (.setAuthCache (doto (BasicAuthCache.)
                                                                     (.put (HttpHost. host port) (BasicScheme.))))))




(def webdavserver {
                   :scheme "https"
                   :host "webdav.yandex.ru"
                   :port 443
                   })


(defn PROPFIND [^URI uri] (doto (proxy [HttpRequestBase] []
                             (getMethod
                               []
                               (str "PROPFIND"))) (.setURI uri)))


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
                              uri (URI. (webdavserver :scheme) nil (webdavserver :host) (webdavserver :port) (str "/" path) nil nil)
                              get (doto (PROPFIND uri)
                                    (.addHeader "Depth" "1")
                                    (.addHeader "Accept", "*/*")
                                    (.addHeader "Authorization" (str "OAuth " (yandex-key)))
                                    )
                              ]
                          (.execute client get (BasicResponseHandler.)))))

(defn loadFile [path] (
                        with-open [^CloseableHttpClient client (.build (doto (HttpClientBuilder/create)))]
                        (let [
                              url (URI. (webdavserver :scheme) nil (webdavserver :host) (webdavserver :port) (str "/" path) nil nil)
                              get (doto (HttpGet. url)
                                    (.addHeader "Accept", "*/*")
                                    (.addHeader "Authorization" (str "OAuth " (yandex-key)))
                                    )
                              ]
                          (.execute client get (doto (proxy [ResponseHandler] []
                                                       (handleResponse
                                                         [response]
                                                         (EntityUtils/toByteArray (.getEntity response)))))))))

