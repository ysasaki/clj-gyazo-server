(ns gyazo-server.test.handler
  (:require [clojure.test :refer :all]
            [gyazo-server.handler :as hdl :refer :all]
            [ring.mock.request :as mock]))

(deftest test-local-now
  (is (re-find
       #"^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2} [+-]\d{2}:\d{2}$"
       (#'hdl/local-now))))

(deftest test-store-id!
  (is (let [db-store (atom {})
            id "test-id"
            hash "test-hash"]
        (#'hdl/store-id! db-store id hash)
        (= (get @db-store hash) id))))

(deftest test-create-newid
  (is (=
       (#'hdl/create-newid "10.0.0.1" "2014-07-02 21:50:59 +0900")
       "44dd614ba8fa168440cddd8a2e7ca2af")))

;; (run-all-tests)

;; (deftest test-app
;;   (testing "main route"
;;     (let [resonse (app (mock/request :get "/"))]
;;       (is (= (:status response) 200))
;;       (is (= (:body response) "Hello World"))))

;;   (testing "not-found route"
;;     (let [response (app (mock/request :get "/invalid"))]
;;       (is (= (:status response) 404)))))
