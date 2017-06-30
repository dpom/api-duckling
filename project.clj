(defproject api-duckling "0.1.0-SNAPSHOT"
  :description "A JSON Web Services for clj-duckling."
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.6.0"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.3.0"]
                 [cheshire "5.7.1"]
                 [environ "1.1.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [clj-duckling "0.4.24"]]
  :plugins [[lein-ancient "0.6.10" :exclusions [org.clojure/clojure]]
            [venantius/ultra "0.5.1" :exclusions [org.clojure/clojure]]
            [lein-ring "0.9.7" :exclusions [org.clojure/clojure]]
            [lein-environ "1.1.0"]]
  :ring {:handler api-duckling.handler/app
         :init api-duckling.handler/init!}
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring/ring-mock "0.3.0"]]
                   :env {:timbre-level "trace"}}
             :test {:env {:timbre-level "info"}}
             :prod {:env {:timbre-level "info"
                          :log-file "log/api_duckling.log"}}
}
  )
