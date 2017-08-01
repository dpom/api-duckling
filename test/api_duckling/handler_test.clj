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


  (testing "message route set dimensions"
    (let [response (app (-> (mock/request :post "/message") (json-body {"q" "trei kilometrii si doi litrii", "dims" "volume,distance" })))]
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
             (:entities (json/parse-string (:body response) true) )))))

  (testing "message route set dimensions"
    (let [response (app (-> (mock/request :post "/message")
                            (json-body {"q" "10 lei",
                                        "dims" "number,amount-of-money"})))]
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
              (:entities (json/parse-string (:body response) true) )))))

  (testing "message route dimensions and module"
    (let [response (app (-> (mock/request :post "/message")
                            (json-body {"module" "en$core"
                                        "q" "two degrees",
                                        "dims" "temperature" })))]
      (is (=  200 (:status response)))
      (is (=  [{:body "two degrees",
                :dim "temperature",
                :end 11,
                :start 0,
                :value {:type "value", :unit "degree", :value 2}}]
             (:entities (json/parse-string (:body response) true) )))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= 404 (:status response))))))
