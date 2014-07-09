(ns gyazo-server.handler
  (:gen-class)
  (:require [clojure.java.io :as io]
            [ring.util.response :as response]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [clj-time.core :as t]
            [digest]
            [org.httpkit.server :as server]
            [gyazo-server.view :as v]))

(def image-dir "data")
(def db-store (atom {}))

;; utils
(defn- local-now []
  (-> (t/now)
      (t/to-time-zone (t/time-zone-for-offset +9))
      (.toString "yyyy-MM-dd HH:mm:ss Z")))

(defn- save-file! [image-dir tempfile]
  (let [hash (digest/md5 tempfile)]
    (io/copy tempfile (io/file (str image-dir "/" hash ".png")))
    hash))

(defn- store-id! [db-store id hash]
   (swap! db-store assoc hash id))

(defn- create-newid [remote_addr dt]
  (digest/md5 (str remote_addr dt)))


;; handlers
(defn upload
  [{{{tempfile :tempfile} :imagedata id :id} :params
    {host "host"} :headers
    scheme :scheme
    remote-addr :remote-addr}]
  (let [hash (save-file! image-dir tempfile)
        newid (create-newid remote-addr (local-now))
        res (response/content-type
             (response/response (str (name scheme) "://" host "/" hash ".png"))
             "text/plain")]
    (store-id! db-store (if (empty? id) newid id) hash)
    (if (empty? id)
      (response/header res "X-Gyazo-Id" newid)
      res)))

(defn serve-png [hash]
  (if (get @db-store hash)
    (response/content-type
     (response/file-response (str image-dir "/" hash ".png"))
     "image/png")
    (response/not-found "Not Found")))

;; routes
(defroutes app-routes
  (GET "/" [] (v/index-view))
  (GET "/:hash.png" {{hash :hash} :params} (serve-png hash))
  (POST "/upload" request (upload request))
  (route/not-found "Not Found"))

(def app (handler/site app-routes))

(defn -main [port]
  (server/run-server app {:port (Integer. port)})
  (println (str "Starting server on port " port)))
