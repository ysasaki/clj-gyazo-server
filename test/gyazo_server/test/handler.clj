(ns gyazo-server.test.handler
  (:import java.io.File)
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [gyazo-server.handler :as hdl :refer :all]
            [ring.mock.request :as mock]))

;; private
(defn- create-temp-file [file content]
  (spit file content)
  file)

;; tests
(deftest test-local-now
  (is (re-find
       #"^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2} [+-]\d{4}$"
       (#'hdl/local-now))))

(deftest test-store-id!
  (is (let [db-store (atom {})
            id "test-id"
            hash "test-hash"]
        (#'hdl/store-id! db-store id hash)
        (= (get @db-store hash) id))))

(deftest test-save-file!
  (is
   (let [
         tempdir (System/getProperty "java.io.tmpdir")
         tempfile (create-temp-file
                   (File/createTempFile "test" ".txt") "content")]
     (#'hdl/save-file! tempdir tempfile))
   "9a0364b9e99bb480dd25e1f0284c8555"))

(deftest test-create-newid
  (is (=
       (#'hdl/create-newid "10.0.0.1" "2014-07-02 21:50:59 +0900")
       "44dd614ba8fa168440cddd8a2e7ca2af")))

(deftest test-app
  (testing "POST /upload.cgi"
    (let [file (create-temp-file
                (File/createTempFile "test" ".png") "imagedata")
          imagedata {:tempfile file
                     :content-type "image/png"
                     :filename "test.png"}
          request (assoc
                    (mock/request :post "/upload.cgi")
                    :params {:id "test-id" :imagedata imagedata}
                    :multipart-params {:imagedata imagedata})
          response (hdl/app request)]
      (is (= (:status response) 200))
      (is (= (:body response) "http://localhost/a05c41e120e6a1deee2ff0feb83fabd5.png"))))

  (testing "GET /a05c41e120e6a1deee2ff0feb83fabd5.png"
    (let [response (hdl/app
                    (mock/request :get "/a05c41e120e6a1deee2ff0feb83fabd5.png"))]
      (is (= (:status response) 200))
      (is (= (-> (:headers response) (get "Content-Type")) "image/png"))
      (is (= (-> (:body response) slurp) "imagedata"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))

;; (run-all-tests)
