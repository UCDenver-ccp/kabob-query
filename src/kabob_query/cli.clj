
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

(defn- emit-seq->csv
  [s]
  (format *out* "~{~a~^, ~}~%" s))

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
