(defproject minesweeper "0.1.0-SNAPSHOT"
  :source-paths ["src" "dev"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [org.clojure/core.async "0.1.278.0-76b25b-alpha"]
                 [om "0.5.3"]
                 [ring "1.2.2"]
                 [compojure "1.1.6"]
                 [enlive "1.1.5"]]
  :profiles {:dev {:plugins [[com.cemerick/austin "0.1.4"]
                             [lein-cljsbuild "1.0.3"]]
                   :cljsbuild {:builds [{:id "minesweeper"
                                         :source-paths ["src" "dev"]
                                         :compiler {:output-to "target/classes/public/minesweeper.js"
                                                    :optimizations :whitespace
                                                    :output-dir "target/classes/public/out"
                                                    :source-map "target/classes/public/minesweeper.js.map"}}]}}})
