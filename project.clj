(defproject gyazo-server "0.1.0-SNAPSHOT"
  :description "Gyazo Server written in Clojure"
  :url "https://github.com/ysasaki/gyazo-server"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring "1.3.0"]
                 [compojure "1.1.8"]
                 [hiccup "1.0.5"]
                 [digest "1.4.4"]
                 [clj-time "0.7.0"]
                 [http-kit "2.1.16"]]
  :min-lein-version "2.0.0"
  :uberjar-name "gyazo-server-standalone.jar"
  :plugins [[lein-ring "0.8.11"]]
  :ring {:handler gyazo-server.handler/app}
  :main gyazo-server.handler
  :aot :all
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}
   :uberjar {:main gyazo-server.handler :aot :all}})
