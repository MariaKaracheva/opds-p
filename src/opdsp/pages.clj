(ns opdsp.pages (:require [hiccup.core :refer :all]
                          [hiccup.page :refer [include-css include-js]]
                          [opdsp.shared :refer :all]))


(defn login [] (html [:html
                      {:lang "en"}
                      [:head
                       [:meta {:charset "utf-8"}]
                       [:meta {:content "IE=edge", :http-equiv "X-UA-Compatible"}]
                       [:meta
                        {:content "width=device-width, initial-scale=1", :name "viewport"}]
                       "<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->"
                       [:meta {:content "", :name "description"}]
                       [:meta {:content "", :name "author"}]
                       [:link {:href "../../favicon.ico", :rel "icon"}]
                       [:title "Signin Template for Bootstrap"]
                       "<!-- Bootstrap core CSS -->"
                       (include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css")
                       "<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->"
                       [:link
                        {:rel  "stylesheet",
                         :href "../../assets/css/ie10-viewport-bug-workaround.css"}]
                       (include-css "css/signin.css")
                       "<!-- Just for debugging purposes. Don't actually copy these 2 lines! -->"
                       "<!--[if lt IE 9]><script src=\"../../assets/js/ie8-responsive-file-warning.js\"></script><![endif]-->"
                       [:script {:src "../../assets/js/ie-emulation-modes-warning.js"}]
                       "<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->"
                       "<!--[if lt IE 9]>\n      <script src=\"https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js\"></script>\n      <script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script>\n    <![endif]-->"]
                      [:body
                       [:div.container
                        [:form.form-signin
                         ;[:h2.form-signin-heading "Please sign in"]
                         [:div [:a {:href (str "https://oauth.yandex.ru/authorize?response_type=code&client_id=" (:id (app-settings :yandex-app)))}
                                "Войти через Яндекс"]]
                         ;[:label.sr-only {:for "inputEmail"} "Email address"]
                         ;[:input#inputEmail.form-control
                         ; {:autofocus   "autofocus",
                         ;  :required    "required",
                         ;  :placeholder "Email address",
                         ;  :type        "email"}]
                         ;[:label.sr-only {:for "inputPassword"} "Password"]
                         ;[:input#inputPassword.form-control
                         ; {:required    "required",
                         ;  :placeholder "Password",
                         ;  :type        "password"}]
                         ;[:div.checkbox
                         ; [:label
                         ;  [:input {:value "remember-me", :type "checkbox"}]
                         ;  " Remember me\n          "]]
                         ;[:button.btn.btn-lg.btn-primary.btn-block
                         ; {:type "submit"}
                         ; "Sign in"]
                         ]
                         ]
                       " "
                       "<!-- /container -->"
                       "<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->"
                       [:script {:src "../../assets/js/ie10-viewport-bug-workaround.js"}]]]

                     ))