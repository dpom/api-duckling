(ns api-duckling.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :as midjson]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
            [clojure.tools.logging :as log]
            [duckling.core :as p]
))

(defn init []
  (log/info "Loading modules ...")
  (p/load!))

(defn duckling-handler [request]
  (let [name (or (get-in request [:params :name])
                 (get-in request [:body :name])
                 "John Doe")]
    {:status 200
     :body {:name name
            :desc (str "The name you sent to me was " name)}})
  )

(defroutes app-routes
  (POST "/" request (duckling-handler request))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      (midjson/wrap-json-body {:keywords? true})
      (midjson/wrap-json-response)
      ;; (wrap-defaults  site-defaults)
      ))
