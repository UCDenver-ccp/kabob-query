
(ns kabob-query.cli
  (:refer-clojure :exclude [format])
  (:require [clojure.pprint :refer [pprint]]
            [clojure.java.io :refer [reader resource]]
            [clojure.tools.cli :refer [parse-opts]]
            [mantle.collection :refer [select-values]]
            [mantle.io :refer [format]]
            [kabob-query.core :refer [query]])
  (:import [java.io BufferedReader StringReader])
  (:gen-class))

(def ^{:private true} cli-opts
  [["-q" "--query-name NAME" "Query to run."]
   ["-a" "--query-args ARGS" "Argument map for query."
    :parse-fn read-string]
   ["-p" "--backend-params PARAMS" "Parameter map for KB backend."
    :parse-fn read-string]])

(defmulti ->str #(class %))

(defmethod ->str :default
  [x]
  (str x))

(defmethod ->str clojure.lang.Symbol
  [x]
  (str (namespace x) ":" (name x)))

(defn- emit-seq->csv
  [s]
  ;; When printing the resultset, remap the namespace separator in all keywords
  ;; from "/" to ":".  Since makes non-IAO elements (e.g. 'obo/GO_00) look like
  ;; our short-form IAO elements (e.g. "uniprot:P_12345").  This allows to
  ;; cheat on queries that take non-IAO elements as input by specifying the
  ;; 'obo:' prefix; should the user simply copy and paste the term from the
  ;; output of another query, everything will work as expected.
  (format *out* "~{~a~^, ~}~%" (map ->str s)))

(str)

(defn- cli-query
  [opts]
  (let [kseq (atom nil)
        r-fn (fn [r]
               (when-not @kseq
                 (let [r-keys (keys r)]
                   (emit-seq->csv (map name r-keys))
                   (swap! kseq (fn [_] r-keys))))
               (emit-seq->csv (select-values r @kseq)))]
    (query (:query-name opts)
           (:query-args opts)
           (:backend-params opts)
           r-fn)))

(defn- cli-usage
  [s]
  (format *out* "~{~a~%~}" (line-seq (BufferedReader. (StringReader. s)))))

(defn -main
  [& args]
  (let [spec (parse-opts args cli-opts)
        opts (:options spec)]
    (cond (:query-name opts) (cli-query opts)
          :else (cli-usage (:summary spec)))))

;; Example invocation:
;; java -jar target/kabob-query-0.1.0-SNAPSHOT-standalone.jar \
;;   kabob-query/cli \
;;   -q sparql/test/connection \
;;   -a "[3]" \
;;   -p "{:db-url \"http://hostname:10035\" :repository-name \"repo\" :username \"user\" :password \"pass\"}"
