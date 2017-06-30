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
    (let [response (app (-> (mock/request :post "/parse") (json-body {"text" "trei kilometrii si doi litrii"})))]
      (is (=  200 (:status response)))
      (is (= [{:body "trei",
            :dim "number",
            :end 4,
            :start 0,
            :value {:type "value", :value 3}}
           {:body "doi",
            :dim "number",
            :end 22,
            :start 19,
            :value {:type "value", :value 2}}
           {:body "trei",
            :dim "time",
            :end 4,
            :latent true,
            :start 0,
            :value {:grain "hour",
                    :type "value",
                    :value "2017-06-30T15:00:00.000+03:00",
                    :values [{:grain "hour",
                              :type "value",
                              :value "2017-06-30T15:00:00.000+03:00"}
                             {:grain "hour",
                              :type "value",
                              :value "2017-07-01T03:00:00.000+03:00"}
                             {:grain "hour",
                              :type "value",
                              :value "2017-07-01T15:00:00.000+03:00"}]}}
           {:body "doi",
            :dim "time",
            :end 22,
            :latent true,
            :start 19,
            :value {:grain "hour",
                    :type "value",
                    :value "2017-07-01T02:00:00.000+03:00",
                    :values [{:grain "hour",
                              :type "value",
                              :value "2017-07-01T02:00:00.000+03:00"}
                             {:grain "hour",
                              :type "value",
                              :value "2017-07-01T14:00:00.000+03:00"}
                             {:grain "hour",
                              :type "value",
                              :value "2017-07-02T02:00:00.000+03:00"}]}}
           {:body "trei",
            :dim "temperature",
            :end 4,
            :latent true,
            :start 0,
            :value {:type "value", :value 3}}
           {:body "doi",
            :dim "temperature",
            :end 22,
            :latent true,
            :start 19,
            :value {:type "value", :value 2}}
           {:body "trei",
            :dim "distance",
            :end 4,
            :latent true,
            :start 0,
            :value {:type "value", :value 3}}
           {:body "doi",
            :dim "distance",
            :end 22,
            :latent true,
            :start 19,
            :value {:type "value", :value 2}}
           {:body "trei",
            :dim "volume",
            :end 4,
            :latent true,
            :start 0,
            :value {:type "value", :value 3}}
           {:body "doi",
            :dim "volume",
            :end 22,
            :latent true,
            :start 19,
            :value {:type "value", :value 2}}]
              (:tokens (json/parse-string (:body response) true) )))))

  (testing "parse route set dimensions"
    (let [response (app (-> (mock/request :post "/parse") (json-body {"text" "trei kilometrii si doi litrii", "dims" "volume,distance" })))]
      (is (=  200 (:status response)))
      (is (= [{:body "trei",
                   :dim "distance",
                   :end 4,
                   :latent true,
                   :start 0,
                   :value {:type "value", :value 3}}
                  {:body "doi",
                   :dim "distance",
                   :end 22,
                   :latent true,
                   :start 19,
                   :value {:type "value", :value 2}}
                  {:body "trei",
                   :dim "volume",
                   :end 4,
                   :latent true,
                   :start 0,
                   :value {:type "value", :value 3}}
                  {:body "doi",
                   :dim "volume",
                   :end 22,
                   :latent true,
                   :start 19,
                   :value {:type "value", :value 2}}]
             (:tokens (json/parse-string (:body response) true) )))))

  (testing "parse route set dimensions"
    (let [response (app (-> (mock/request :post "/parse") (json-body {"text" "10 lei", "dims" "number,amount-of-money" })))]
      (is (=  200 (:status response)))
      (is (=   [{:body "10 lei",
                 :dim "amount-of-money",
                 :end 6,
                 :start 0,
                 :value {:type "value", :unit "RON", :value 10}}
                {:body "10 lei",
                 :dim "amount-of-money",
                 :end 6,
                 :start 0,
                 :value {:type "value", :unit "RON", :value 10}}]
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
