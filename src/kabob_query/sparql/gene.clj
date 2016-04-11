
(ns kabob-query.sparql.gene
  (:require [kabob-query.api :refer [define-interface-fn]]
            [kabob-query.kb :refer [sparql-query]]
            [kabob-query.template :refer [render]]))

(define-interface-fn by-taxon kb
  [taxon-id]
  (sparql-query kb (render "sparql/gene/by-taxon" {:ncbi-taxon-id taxon-id})))
