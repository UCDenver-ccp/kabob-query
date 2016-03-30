
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
  [["-l" "--list" "List available queries."]
   ["-q" "--query-name NAME" "Query to run."]
   ["-a" "--query-args ARGS" "Argument map for query."
    :parse-fn read-string]
   ["-p" "--backend-params PARAMS" "Parameter map for KB backend."
    :parse-fn read-string]])

(defn- template
  [s]
  (or (resource (str s ".mustache"))
      (throw (ex-info (str "Template query not found: " s) {}))))

(defn- cli-query
  [opts]
  (let [rslt (query (template (:query-name opts))
                    (:query-args opts)
                    (:backend-params opts))
        keys (keys (first rslt))]
    (doseq [r rslt]
      (format *out* "~{~a~^, ~}~%" (select-values r keys)))))

(defn- cli-list
  []
  (format *out* "~{~a~%~}" (line-seq (reader (resource "index")))))

(defn- cli-usage
  [s]
  (format *out* "~{~a~%~}" (line-seq (BufferedReader. (StringReader. s)))))

(defn -main
  [& args]
  (let [spec (parse-opts args cli-opts)
        opts (:options spec)]
    (cond (:list opts) (cli-list)
          (:query-name opts) (cli-query opts)
          :else (cli-usage (:summary spec)))))

;; Example invocation:
;; java -jar target/kabob-query-0.1.0-SNAPSHOT-standalone.jar \
;;   kabob-query/cli \
;;   -q sparql/test/connection \
;;   -a "{:value 3}" \
;;   -p "{:db-url \"http://hostname:10035\" :repository-name \"repo\" :username \"user\" :password \"pass\"}"
