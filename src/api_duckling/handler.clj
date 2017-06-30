(ns api-duckling.handler
  (:require
   [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :as midjson]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [duckling.core :as p]
))

(defn init! []
  (log/info "Loading modules ...")
  (let [res (p/load!)]
    (log/debugf "Modules loaded:\n %s" res)))

(defn duckling-handler [request]
  (log/debugf "request: %s" request)
  (let [{:keys [text module dims] :or {module "ro$core", text ""}} (get request :body {})
        dims (if dims (into [] (map keyword (str/split dims #","))) [])]
    (log/debugf "text = %s, module = %s, dims= %s" text module dims)
    {:status 200
     :body {:tokens (p/parse (keyword module) text dims)}}))

(defroutes app-routes
  (POST "/parse" request (duckling-handler request))
  ;; (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      (midjson/wrap-json-body {:keywords? true})
      (midjson/wrap-json-response)
      ;; (wrap-defaults  site-defaults)
      ))
