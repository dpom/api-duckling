(ns api-duckling.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [api-duckling.handler :refer :all]))

(use-fixtures :once
  (fn [tests]
    (println "============== start tests")
    (init!)
    (tests)
    (println "============== stop tests")
    ))


(defn json-body [request data]
  (-> request
      (mock/content-type "application/json")
      (mock/body (json/generate-string data))))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= 404 (:status response) ))))

  (testing "parse route default dimensions"
    (let [response (app (-> (mock/request :post "/parse") (json-body {"text" "peste doua ore"})))]
      (is (=  200 (:status response)))
      (is (=  [{:body "doua",
                :dim "number",
                :end 10,
                :start 6,
                :value {:type "value", :value 2}}
               {:body "doua",
                :dim "time",
                :end 10,
                :latent true,
                :start 6,
                :value {:grain "hour",
                        :type "value",
                        :value "2017-06-30T02:00:00.000+03:00",
                        :values [{:grain "hour",
                                  :type "value",
                                  :value "2017-06-30T02:00:00.000+03:00"}
                                 {:grain "hour",
                                  :type "value",
                                  :value "2017-06-30T14:00:00.000+03:00"}
                                 {:grain "hour",
                                  :type "value",
                                  :value "2017-07-01T02:00:00.000+03:00"}]}}
               {:body "doua",
                :dim "temperature",
                :end 10,
                :latent true,
                :start 6,
                :value {:type "value", :value 2}}
               {:body "doua",
                :dim "distance",
                :end 10,
                :latent true,
                :start 6,
                :value {:type "value", :value 2}}
               {:body "doua",
                :dim "volume",
                :end 10,
                :latent true,
                :start 6,
                :value {:type "value", :value 2}}]
              (:tokens (json/parse-string (:body response) true) )))))

  (testing "parse route dimensions"
    (let [response (app (-> (mock/request :post "/parse") (json-body {"text" "peste doua ore", "dims" "number,time" })))]
      (is (=  200 (:status response)))
      (is (=  [{:body "doua",
                :dim "number",
                :end 10,
                :start 6,
                :value {:type "value", :value 2}}
               {:body "doua",
                :dim "time",
                :end 10,
                :latent true,
                :start 6,
                :value {:grain "hour",
                        :type "value",
                        :value "2017-06-30T02:00:00.000+03:00",
                        :values [{:grain "hour",
                                  :type "value",
                                  :value "2017-06-30T02:00:00.000+03:00"}
                                 {:grain "hour",
                                  :type "value",
                                  :value "2017-06-30T14:00:00.000+03:00"}
                                 {:grain "hour",
                                  :type "value",
                                  :value "2017-07-01T02:00:00.000+03:00"}]}}]
              (:tokens (json/parse-string (:body response) true) )))))

  (testing "parse route dimensions and module"
    (let [response (app (-> (mock/request :post "/parse") (json-body {"module" "en$core" "text" "two degrees", "dims" "temperature" })))]
      (is (=  200 (:status response)))
      (is (=  [{:body "two degrees",
                :dim "temperature",
                :end 11,
                :start 0,
                :value {:type "value", :unit "degree", :value 2}}]
             (:tokens (json/parse-string (:body response) true) )))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= 404 (:status response))))))
