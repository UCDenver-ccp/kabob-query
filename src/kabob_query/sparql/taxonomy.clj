
(ns kabob-query.sparql.taxonomy
  (:require [kabob-query.api :refer [define-interface-fn]]
            [kabob-query.kb :refer [sparql-query]]
            [kabob-query.sparql.id :refer [bio-id id->ice-uri]]
            [kabob-query.template :refer [render]]))

(define-interface-fn ncbi-id kb
  [source-id]
  (sparql-query kb
                (render "sparql/taxonomy/ncbi-id"
                        {:src-id source-id
                         :bio-id (bio-id kb (id->ice-uri source-id (:iao-namespaces kb)))})))
