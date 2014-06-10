(ns user
  (:require [cemerick.austin.repls :refer (browser-connected-repl-js)]
            [net.cgrand.enlive-html :as enlive]
            [compojure.route :refer (resources)]
            [compojure.core :refer (GET defroutes)]
            [ring.adapter.jetty]
            [clojure.java.io :as io]))

(defonce repl-env (reset! cemerick.austin.repls/browser-repl-env
                          (cemerick.austin/repl-env)))

(defn page []
  (enlive/sniptest (slurp (io/resource "index.html"))
                   [:body] (enlive/append
                            (enlive/html [:script (browser-connected-repl-js)]))))

(defroutes site
  (resources "/")
  (GET "/*" req (page)))

(defn run! []
  (defonce ^:private server
    (ring.adapter.jetty/run-jetty #'site {:port 8080 :join? false}))
  server)

(defn cljs! []
  (cemerick.austin.repls/cljs-repl repl-env))
