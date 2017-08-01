(defproject api-duckling "1.1-dev06"
  :description "A JSON Web Services for clj-duckling."
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.6.0"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.3.1"]
                 [cheshire "5.7.1"]
                 [environ "1.1.0"]
                 [enlive "1.1.6"]
                 [com.taoensso/timbre "4.10.0"]
                 [dpom/clj-duckling "0.4.25-dev01"]]
  :local-repo "repo"
  :plugins [[lein-ring "0.9.7" :exclusions [org.clojure/clojure]]
            [lein-environ "1.1.0"]]
  :ring {:handler api-duckling.handler/app
         :init api-duckling.handler/init!}
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring/ring-mock "0.3.1"]]
                   :env {:timbre-level "trace"}}
             :test {:env {:timbre-level "info"}}
             :prod {:jvm-opts ["-Xss512k -Xmx512m"]
                    :env {:timbre-level "info"
                          :log-file "/var/log/api_duckling/prod.log"}}
}
  )
