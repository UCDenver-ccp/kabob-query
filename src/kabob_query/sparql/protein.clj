
(ns kabob-query.sparql.protein
  (:require [kabob-query.api :refer [define-interface-fn]]
            [kabob-query.kb :refer [sparql-query]]
            [kabob-query.sparql.id :refer [bio-id id->ice-uri]]
            [kabob-query.template :refer [render]]))

(define-interface-fn cellular-components kb
  [source-id]
  (let [ice-id (id->ice-uri source-id (:iao-namespaces kb))]
    (sparql-query kb
                  (render "sparql/protein/cellular-components"
                          {:src-id source-id
                           :ice-id ice-id
                           :bio-id (bio-id kb ice-id)}))))

(define-interface-fn processes kb
  [source-id]
  (let [ice-id (id->ice-uri source-id (:iao-namespaces kb))]
    (sparql-query kb
                  (render "sparql/protein/processes"
                          {:src-id source-id
                           :ice-id ice-id
                           :bio-id (bio-id kb ice-id)}))))

(define-interface-fn binary-interaction-partners kb
  [source-id]
  (let [ice-id (id->ice-uri source-id (:iao-namespaces kb))]
    (sparql-query kb
                  (render "sparql/protein/binary-interaction-partners"
                          {:src-id source-id
                           :ice-id ice-id
                           :bio-id (bio-id kb ice-id)}))))
