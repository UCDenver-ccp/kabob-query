
(ns kabob-query.sparql.label
  (:require [kabob-query.api :refer [define-interface-fn]]
            [kabob-query.kb :refer [sparql-query]]
            [kabob-query.sparql.id :refer [bio-id id->ice-uri]]
            [kabob-query.template :refer [render]]))

(define-interface-fn bioentity-label kb
  [source-id]
  (sparql-query kb
                (render "sparql/label/bioentity-label"
                        {:src-id source-id
                         :bio-id (bio-id kb (id->ice-uri source-id (:iao-namespaces kb)))})))
