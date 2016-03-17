(ns hello-world.davaccess
  (:import (org.apache.http.impl.client BasicResponseHandler HttpClients BasicCredentialsProvider HttpClientBuilder BasicAuthCache)
           (java.net URI)
           (org.apache.http.client.methods HttpRequestBase)
           (org.apache.http.auth AuthScope UsernamePasswordCredentials)
           (org.apache.http.client.protocol HttpClientContext)
           (org.apache.http HttpHost)
           (org.apache.http.impl.auth BasicScheme)))


;// Create AuthCache instance
;AuthCache authCache = new BasicAuthCache();
;// Generate BASIC scheme object and add it to the local
;// auth cache
;BasicScheme basicAuth = new BasicScheme();
;authCache.put(target, basicAuth);
;
;// Add AuthCache to the execution context
;HttpClientContext localContext = HttpClientContext.create();
;localContext.setAuthCache(authCache);
(def localkontext  (doto (HttpClientContext/create)
                       (.setAuthCache (doto (BasicAuthCache.)
                                        (.put (HttpHost. "localhost" 7000) (BasicScheme.))))))

(defn PROPFIND [url] (doto (proxy [HttpRequestBase] []
                     (getMethod
                       []
                       (str "PROPFIND") ) ) (.setURI (URI/create url)) ))


;CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
;credentialsProvider.setCredentials(AuthScope.ANY,
;                                    new UsernamePasswordCredentials("username", "password"));

(defn loadList [path] (
                        with-open [client (.build (.setDefaultCredentialsProvider (HttpClientBuilder/create)
                                                                           (doto (BasicCredentialsProvider.)
                                                                             (.setCredentials AuthScope/ANY (UsernamePasswordCredentials. "username", "password")))))  ]
                        ( let [
                              ;url  (HttpGet. "http://localhost:7000/")
                              ;url  (HttpGet. "http://localhost:7000/")
                              ;get  (HttpGet.  "http://uits-labs.ru/")
                              get (doto (PROPFIND "http://localhost:7000/")
                                    (.addHeader "Depth" "1" )
                                    (.addHeader "Accept", "*/*" ))
                              ]
                         (.execute client get (BasicResponseHandler.) localkontext))) )
