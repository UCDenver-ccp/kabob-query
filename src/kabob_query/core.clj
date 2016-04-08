
(ns kabob-query.core
  (:require [clojure.string :as s]
            [mantle.io :refer [fmtstr]]
            [kabob-query.kb :refer [*kb* open-kb]]))

(def +project-name+ "kabob-query")

(defn resolve-query
  "Resolve a query name to a namespace and function."
  [qname]
  (let [parts (s/split qname #"/")
        ns-str (s/replace (s/join "/" (concat [+project-name+] (butlast parts))) "/" ".")
        fn-str (s/join ":" ["api" (last parts)])]
    (require (symbol ns-str))
    (when-let [ns (find-ns (symbol ns-str))]
      (when-let [fn (get (ns-interns ns) (symbol fn-str))]
        (and (fn? (deref fn)) fn)))))

(defn query
  [q-name q-args kb-params r-fn]
  (if-let [q (resolve-query q-name)]
    (with-open [kb (open-kb kb-params)]
      (binding [*kb* kb]
        (doseq [r (apply q q-args)]
          (r-fn r))))
    (throw (ex-info (fmtstr "Unknown query: ~a" q-name) {}))))
