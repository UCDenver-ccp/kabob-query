
(ns kabob-query.sparql.test
  (:require [kabob-query.api :refer [define-interface-fn]]
            [kabob-query.kb :refer [sparql-query]]
            [kabob-query.template :refer [render]]))

(define-interface-fn connection kb
  [value]
  (sparql-query kb (render "sparql/test/connection" {:value value})))
