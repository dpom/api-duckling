(ns api-duckling.handler
  (:require
   [clojure.pprint :as pp]
   [compojure.core :refer :all]
   [compojure.route :as route]
   [compojure.handler :as handler]
   [ring.middleware.json :as midjson]
   [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
   [clojure.string :as str]
   [environ.core :refer [env]]
   [taoensso.timbre :as log]
   [taoensso.timbre.appenders.core :as appenders]
   [net.cgrand.enlive-html :as h]
   [duckling.core :as p]
   ))

(defn init! []
  (log/set-level! (keyword (env :timbre-level)))
  ;; (log/swap-config! (fn [config] (assoc config :ns-whitelist ["api-duckling.*"])))
  (when-let [logfile (env :log-file)]
    (log/merge-config!
     {:appenders {:spit (appenders/spit-appender {:fname logfile})}}))
  (log/info "Loading modules ...")
  (let [res (p/load!)]
    (log/tracef "Modules loaded:\n %s" res)))

(defn get-entities
  [module text dims]
  (log/debugf "get-entities params: module= %s, dims = |%s|, text = |%s|"
              module dims text)
  (let [dims (if-not (empty? dims)
               (vec (map keyword (str/split dims #",")))
               [])
        res (p/parse (keyword module) text dims)]
    (log/debugf "get-entities res: %s", (first res))
    res))

(defn parser-handler [request]
  (log/debugf "request: %s" request)
  (let [{:keys [q module dims] :or {module "ro$core", q ""}}
        (get request :body {})]
    (log/debugf "q = %s, module = %s, dims= %s" q module dims)
    {:status 200
     :body {:module module
            :dims (str/join "," dims)
            :entities (get-entities module q dims)}}))


(h/deftemplate test-template "public/test.html"
  [module dims q res]
  [:textarea#test_res] (h/content res)
  [:textarea#test_q] (h/content q)
  [:input#test_dims] (h/set-attr :value dims)
  [:select#test_module :option (h/attr= :value module)] (h/set-attr :selected "selected")
  )

(defn test-handler
  [request]
  (let [{:keys [q module dims] :or {module "ro$core", q ""}}
        (get request :params {})
        res (get-entities module q dims)
        pres (with-out-str
               (doseq [entity res]
                 (pp/pprint entity)
                 (newline)))]
    (log/debugf "test params: module= %s, dims = %s, q = |%s|, res = %s"
                module dims q pres)
    (str/join (test-template module dims q pres))))


(defroutes app-routes
  (POST "/message" request (parser-handler request))
  (ANY "/test" request (test-handler request))
  (route/resources "/")
  (route/not-found "Not Found"))


(def app
  (-> (handler/site app-routes)
      (midjson/wrap-json-body {:keywords? true})
      (midjson/wrap-json-response)
      ))
