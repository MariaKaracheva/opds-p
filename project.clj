

(defproject opdsp "1.0.0-SNAPSHOT"
  :description "FIXME: write"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.5.0"]
                 [ring/ring-defaults "0.2.0"]
                 [org.apache.httpcomponents/httpclient "4.5.2"]
                 [lock-key "1.4.1"]
                 [com.github.kyleburton/clj-xpath "1.4.5"]
                 [org.clojure/data.xml "0.0.8"]
                 [compojure "1.5.0"]
                 [clj-yaml "0.4.0"]
                 [clj-http "3.0.1"]
                 [com.cemerick/friend "0.2.1"]
                 [hawk "0.2.10"]
                 [com.novemberain/monger "3.1.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/data.json "0.2.6"]
                 ]

  :plugins [[lein-ring "0.9.7"] [lein-auto "0.1.2"]]
  :ring {:handler opdsp.core/standalone-routes
         :init opdsp.core/init
         :destroy opdsp.core/destroy
         :auto-reload? true
         :auto-refresh? true
         :reload-paths ["src/"]}
  :profiles {
             :uberjar {:ring {:handler opdsp.core/opds-p-handler}}
             :test {
                    :dependencies [
                                   [ring/ring-jetty-adapter "1.4.0"]
                                   ]
                    }

             }
  )
