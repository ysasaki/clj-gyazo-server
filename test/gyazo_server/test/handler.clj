(ns gyazo-server.test.handler
  (:require [clojure.test :refer :all]
            [gyazo-server.handler :as gs :refer :all]
            [ring.mock.request :as mock]
            [clj-time.core :as t]
            [clj-time.format :as f]))

(deftest test-create-newid
  (is (=
       (create-newid "10.0.0.1" "2014-07-02 21:50:59 +0900")
       "44dd614ba8fa168440cddd8a2e7ca2af")))

(test-create-newid)

;; (deftest test-app
;;   (testing "main route"
;;     (let [resonse (app (mock/request :get "/"))]
;;       (is (= (:status response) 200))
;;       (is (= (:body response) "Hello World"))))

;;   (testing "not-found route"
;;     (let [response (app (mock/request :get "/invalid"))]
;;       (is (= (:status response) 404)))))
