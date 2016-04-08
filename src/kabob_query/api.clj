
(ns kabob-query.api
  "In which is defined the mechanisms to build the query API.  This namespace
  is not intended to be used by query clients; the external interface is
  defined in the `kabob-query.core` namespace."
  (:require [clojure.string :as s]))

(defmacro define-interface-fn
  [fn-name kb-sym fn-args & body]
  `(do
     (defn ~(symbol (s/join ":" ["query" fn-name]))
       ~(vec (conj (reverse fn-args) kb-sym))
       ~@body)
     (defn ~(symbol (s/join ":" ["api" fn-name]))
       ~(vec fn-args)
       (let [~kb-sym kabob-query.kb/*kb*]
         (if (nil? ~kb-sym)
           (ex-info (mantle.io/fmtstr "~a must be bound before invoking API functions." (quote kabob-query.kb/*kb*)) {})
           (~(symbol (s/join ":" ["query" fn-name])) kabob-query.kb/*kb* ~@fn-args))))))
