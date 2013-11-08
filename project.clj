(defproject csv-to-json "0.1.2-SNAPSHOT"
  :description "Converts CSV input files to JSON output files"
  :url "https://github.com/olauzon/csv-to-json"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
    [org.clojure/clojure   "1.5.1"]
    [org.clojure/data.csv  "0.1.2"]
    [org.clojure/data.json "0.2.3"]
  ]
  :main csv-to-json.core)
