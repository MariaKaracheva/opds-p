

(defproject opdsp "1.0.0-SNAPSHOT"
  :description "FIXME: write"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring/ring-core "1.8.0"]
                 [ring/ring-defaults "0.3.2"]
                 [org.apache.httpcomponents/httpclient "4.5.2"]
                 [lock-key "1.5.0"]
                 [com.github.kyleburton/clj-xpath "1.4.11"]
                 [org.clojure/data.xml "0.0.8"]
                 [compojure "1.6.1"]
                 [clj-commons/clj-yaml "0.7.0"]
                 [clj-http "3.10.0"]
                 [com.cemerick/friend "0.2.3"]
                 [com.novemberain/monger "3.5.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/data.json "0.2.7"]
                 ]

  :plugins [[lein-ring "0.12.5"] [lein-auto "0.1.3"]]
  :ring {:handler opdsp.core/standalone-routes
         :auto-reload? true
         :auto-refresh? true
         :reload-paths ["src/"]}
  :profiles {
             :uberjar {:ring {:handler opdsp.core/opds-p-handler}}
             :test {
                    :dependencies [
                                   [ring/ring-jetty-adapter "1.8.0"]
                                   ]
                    }

             }
  )
