(ns opdsp.pages
  (:require [hiccup.core :refer :all]
            [hiccup.form :refer :all]
            [hiccup.page :refer [include-css include-js]]
            [ring.util.codec :refer [form-decode]]
            [opdsp.shared :refer :all]))


(defn- head [title & includes] [:head
                                [:meta {:charset "utf-8"}]
                                [:meta {:content "IE=edge", :http-equiv "X-UA-Compatible"}]
                                [:meta
                                 {:content "width=device-width, initial-scale=1", :name "viewport"}]
                                "<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->"
                                [:meta {:content "", :name "description"}]
                                [:meta {:content "", :name "author"}]
                                [:link {:href "../../favicon.ico", :rel "icon"}]
                                [:title title]
                                "<!-- Bootstrap core CSS -->"
                                (include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css")
                                "<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->"
                                [:link
                                 {:rel  "stylesheet",
                                  :href "../../assets/css/ie10-viewport-bug-workaround.css"}]
                                includes
                                "<!-- Just for debugging purposes. Don't actually copy these 2 lines! -->"
                                "<!--[if lt IE 9]><script src=\"../../assets/js/ie8-responsive-file-warning.js\"></script><![endif]-->"
                                [:script {:src "../../assets/js/ie-emulation-modes-warning.js"}]
                                "<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->"
                                "<!--[if lt IE 9]>\n      <script src=\"https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js\"></script>\n      <script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script>\n    <![endif]-->"])

(defn- body-footer [] [:div " "
                       "<!-- /container -->"
                       "<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->"
                       [:script {:src "../../assets/js/ie10-viewport-bug-workaround.js"}]])

(defn login [] (html [:html
                      {:lang "en"}
                      (head "Signin Template for Bootstrap" (include-css "css/signin.css"))
                      [:body
                       [:div.container
                        [:form.form-signin
                         ;[:h2.form-signin-heading "Please sign in"]
                         [:div [:a {:href (str "https://oauth.yandex.ru/authorize?response_type=code&client_id=" (:id (app-settings :yandex-app)))}
                                "Войти через Яндекс"]]
                         ]
                        ]
                       (body-footer)
                       ]]

                     ))

(defn manage [{userSettings :userSettings dirs :rootdirs}]
  (let [enabledPaths (set (-> userSettings :catalog :paths))
        sortedPaths (->> dirs
                         (map (fn [dir] {:dir dir :enabled (contains? enabledPaths dir)}))
                         (sort-by (fn [dir] [(not (:enabled dir))])))]
    (html
      [:html
       {:lang "en"}
       (head "Opds settings" (include-css "css/manage.css"))
       [:body
        [:div.container
         [:div.panel.panel-default
          [:div.panel-heading [:h1.panel-title "Opds settings"]]
          [:div.panel-body
           [:form {:method "post" :action "save"}
            [:div.panel.panel-default
             [:div.panel-heading [:h2.panel-title "Opds login"]]
             [:div.panel-body
              [:div.alert.alert-info "Логин и пароль, которые будут использованы для доступа к opds каталогу по адресу "]
              [:div.form-group
               [:div.row
                [:label.col-sm-2.col-form-label {:for "login"} "Логин"]
                [:div.col-sm-10
                 [:input#login.form-control
                  {:placeholder "login", :name "catalog-login" :type "text"}]]]
               [:div.row
                [:label.col-sm-2.col-form-label
                 {:for "inputPassword"} "Password"]
                [:div.col-sm-10
                 [:input#inputPassword.form-control
                  {:placeholder "Password", :name "catalog-password" :type "password"}]]]
               ]]
             ]
            [:div.panel.panel-default
             [:div.panel-heading [:h2.panel-title "Доступные папки каталога"]]
             [:div.panel-body
              [:div.alert.alert-info "Укажите папке, которые будут доступны через opds-каталог"]
              [:table.table.dirlist
               [:thead [:tr [:th.enabled-checkbox "Доступ"] [:th.folder-path "Папка"]]]
               [:tbody
                (for [dir sortedPaths]
                  [:tr [:td.enabled-checkbox (check-box {} "alloweddir" (:enabled dir) (:dir dir))] [:td.folder-path (form-decode (:dir dir))]])]]]]
            [:div.form-group.row
             [:div.offset-sm-2.col-sm-10
              [:button.btn.btn-primary {:type "submit"} "Сохранить"]]]]]
          ]] (body-footer)]]
      )))