
(ns kabob-query.template
  (:require [clojure.java.io :refer [resource]]
            [mantle.io :refer [fmtstr]]
            [stencil.core :refer [render-string]]))

(defn render
  [name args]
  (if-let [res (resource (str name ".mustache"))]
    (render-string (slurp res) args)
    (throw (ex-info (fmtstr "Unknown query template: ~a" name) {}))))
