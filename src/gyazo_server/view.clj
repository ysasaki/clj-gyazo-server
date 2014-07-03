(ns gyazo-server.view
  (:require [hiccup.core :as html]
            [hiccup.form :as form]
            [hiccup.page :as page]))

(defn- layout [body]
  (page/html5
   [:head [:title "Gyazo Uploader Test"]]
   [:body body]))

(defn index-view []
  (layout
   (form/form-to
    {:enctype "multipart/form-data"}
    [:post "/upload"]
    (form/text-field "id")
    (form/file-upload "imagedata")
    (form/submit-button "Upload"))))
