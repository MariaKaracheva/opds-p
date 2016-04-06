;(defproject my-stuff "0.1.0-SNAPSHOT"
;  :description "FIXME: write description"
;  :url "http://example.com/FIXME"
;  :license {:name "Eclipse Public License"
;            :url "http://www.eclipse.org/legal/epl-v10.html"}
;  :dependencies [[org.clojure/clojure "1.8.0"]]
;  :main ^:skip-aot my-stuff.core
;  :target-path "target/%s"
;  :profiles {:uberjar {:aot :all}})

(defproject hello-world "1.0.0-SNAPSHOT"
  :description "FIXME: write"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring/ring-defaults "0.2.0"]
                 [org.apache.httpcomponents/httpclient "4.5.2"]
                 [lock-key "1.4.1"]
                 [com.github.kyleburton/clj-xpath "1.4.5"]
                 [org.clojure/data.xml "0.0.8"]
                 [compojure "1.5.0"]
                 [clj-yaml "0.4.0"]
                 [com.cemerick/friend "0.2.1"]
                 [hawk "0.2.10"]
                 ]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler hello-world.core/standalone-routes :init hello-world.core/init :destroy hello-world.core/destroy}
  :profiles { :uberjar {:ring {:handler hello-world.core/handler}}}
  )
