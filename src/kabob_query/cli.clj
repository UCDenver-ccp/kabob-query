
(ns kabob-query.cli
  (:refer-clojure :exclude [format])
  (:require [clojure.java.io :refer [file resource]]
            [clojure.tools.cli :refer [parse-opts]]
            [mantle.collection :refer [select-values]]
            [mantle.io :refer [format]]
            [kabob-query.core :refer [query]])
  (:import [java.io PushbackReader StringReader])
  (:gen-class))

(def cli-opts [["-q" "--query-name NAME" "Query to run."]
               ["-a" "--query-args ARGS" "Argument map for query."
                :parse-fn read-string]
               ["-p" "--backend-params PARAMS" "Parameter map for KB backend."
                :parse-fn read-string]])

(defn template
  [s]
  (or (resource (str s ".mustache"))
      (throw (ex-info (str "Template query not found: " s) {}))))

(defn -main
  [& args]
  (let [opts (:options (parse-opts args cli-opts))
        rslt (query (template (:query-name opts))
                    (:query-args opts)
                    (:backend-params opts))
        keys (keys (first rslt))]
    (doseq [r rslt]
      (format *out* "~{~a~^, ~}~%" (select-values r keys)))))

;; Example invocation:
;; java -jar target/kabob-query-0.1.0-SNAPSHOT-standalone.jar \
;;   kabob-query/cli \
;;   -q sparql/test/connection \
;;   -a "{:value 3}" \
;;   -p "{:db-url \"http://hostname:10035\" :repository-name \"repo\" :username \"user\" :password \"pass\"}"
