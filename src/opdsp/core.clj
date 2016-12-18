(ns opdsp.core
  (:require [ring.util.response :refer [response content-type]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.params :refer [wrap-params]]
            )
  (:require [opdsp.davaccess :as davaccess]
            [clojure.string :as string]
            [clojure.walk :refer [keywordize-keys]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])
            [hawk.core :as hawk]
            [opdsp.pages :as pages]
            [clj-http.client :as client]
            [opdsp.shared :refer :all]
            [opdsp.dirrender :refer :all]
            [clojure.data.json :as json]
            [clj-yaml.core :as yaml]
            [ring.util.response :as response])
  (:import (java.io File)))


(defn opds-catalog-authenticate [handler]
  (friend/authenticate handler {
                                :allow-anon?             false
                                :unauthenticated-handler #(workflows/http-basic-deny "opds-p" %)
                                :credential-fn           (fn [input]
                                                           (let [allowed (userIsAllowed input)]
                                                             (if allowed {:identity (:username input)} nil)))
                                :workflows               [(workflows/http-basic)]
                                }))



(defn process-yandex-response [request] (let [code (get (:query-params request) "code")
                                              oaresp (client/post "https://oauth.yandex.ru/token"
                                                                  {:basic-auth       [(:id (app-settings :yandex-app)) (:password (app-settings :yandex-app))]
                                                                   :form-params      {:grant_type "authorization_code"
                                                                                      :code       code}
                                                                   :as               :json
                                                                   :throw-exceptions false})]
                                          (if (= 200 (:status oaresp))
                                            (let [oaResponse (json/read-str (:body oaresp))
                                                  accessTocken (get oaResponse "access_token")
                                                  authList (client/get "https://login.yandex.ru/info"
                                                                       {:query-params     {:format      "json"
                                                                                           :oauth_token accessTocken}
                                                                        :as               :json
                                                                        :throw-exceptions false})
                                                  info (json/read-str (:body authList))
                                                  ]
                                              (keywordize-keys {:oauth oaResponse
                                                                :info  info}))
                                            (throw (Exception. (str "Yandex responed with " oaresp)))
                                            )
                                          ))


(defroutes handler-inner
           (GET "/login" [] (pages/login))
           (GET "/logout" request (-> (response/redirect (str (:context request) "/"))
                                      (assoc :session nil)))
           (GET "/oauth" request (let [yandexData (process-yandex-response request)
                                       session {request :session}
                                       login (-> yandexData :info :login)]
                                   (opdsp.shared/updateUserSettings login {:key (-> yandexData :oauth :access_token)})
                                   (-> (response/redirect (str (:context request) "/manage"))
                                       (assoc-in [:session :login] login))))
           (GET "/" request
             (if-let [login (-> request :session :login)]
               (response/redirect (str (:context request) "/manage"))
               (response/redirect (str (:context request) "/login"))
               )
             )
           (GET "/dir/:path{.*}" [path :as request] (opds-catalog-authenticate (fn [_] (dir request path (:context request)))))
           (GET "/file/:path{.*}" [path :as request] (opds-catalog-authenticate (fn [_] (file request path (:context request)))))
           (route/not-found "<h1>Page not found</h1>"))



(defn wrap-settings [handler]
  (fn [request]
    (binding [opdsp.shared/*userSettings* (opdsp.shared/loadUserSettings (:username (friend/current-authentication request)))]
      (handler request))))

(def opds-p-handler
  (-> handler-inner
      (wrap-settings)
      (wrap-session)
      (wrap-params)
      (wrap-resource "public")
      ; ...required Ring middlewares ...
      ))

(defroutes standalone-routes
           (context "/opds-p" req opds-p-handler)
           (route/not-found "Not Found"))

(def watcher (atom nil))

(defn init []
  (println "init")
  )
(defn destroy [] (println "destroy") (hawk/stop! @watcher))

;(def standalone-app
;  (wrap-defaults standalone-routes site-defaults))


