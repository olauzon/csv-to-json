(ns csv-to-json.core
  (require [clojure.string    :as string]
           [clojure.java.io   :as io]
           [clojure.data.csv  :as csv]
           [clojure.data.json :as json]))

(defn make-num
  [input]
  (cond
    (integer? input) input
    (empty?   input) 0
    (string?  input) (Long. input)
    :else            0))

(defn make-bool
  [input]
  (boolean (Boolean/valueOf input)))

(defn type-fn-fn
  [type-key]
  (cond 
    (= type-key "int")  make-num
    (= type-key "bool") make-bool
    :else str))

(def type-fn
  (memoize type-fn-fn))

(defn extract-key
  [path]
  (string/split path #"\."))

(defn mappings-fn
  [config-paths]
  (map #(assoc-in % [:key-path] (extract-key (:key-path %)))
    (map #(update-in % [:col] dec)
      (map #(update-in % [:col] make-num)
        (map #(zipmap [:col :key-path :type] %)
          (map #(string/split % #":")
                (string/split (string/trim config-paths) #",")))))))

(def mappings
  (memoize mappings-fn))

(defn add-cell
  [curr-map input-row cell-mapping]
  (assoc-in curr-map
            (:key-path cell-mapping)
            ((type-fn (:type cell-mapping))
                        (nth input-row (:col cell-mapping) nil))))

(defn map-row
  [input-row row-mappings]
  (let [len (count row-mappings)]
    (loop [curr   0
           result {}]
      (if (>= curr len) result
        (recur
          (inc curr)
          (add-cell result input-row (nth row-mappings curr)))))))

(defn run!
  "Run CSV to JSON transformation"
  [input output json-paths & [s]]
  (let [s (or s "c")
        s (if (= s "c") (char 44) (char 9))]
    (do
      (with-open [rdr (io/reader input)]
        (with-open [wtr (io/writer output :append true)]
          (doseq [line (line-seq rdr)]
            (let [json-map (first (csv/read-csv line :separator s))
                  mapped-row (map-row json-map (mappings json-paths))]
              (.write wtr (str (json/write-str mapped-row) "\n")))))))))

(defn -main
  [input output json-paths & [s]]
  (time (run! input output json-paths s)))
