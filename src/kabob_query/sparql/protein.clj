
(ns kabob-query.sparql.protein
  (:require [clojure.string :as s]
            [kabob-query.api :refer [define-interface-fn separator separator-re]]
            [kabob-query.kb :refer [sparql-query]]
            [kabob-query.sparql.id :as id]
            [kabob-query.template :refer [render]]))

(define-interface-fn cellular-components kb
  [source-id]
  (let [ice-id (id/id->ice-uri source-id (:iao-namespaces kb))]
    (sparql-query kb
                  (render "sparql/protein/cellular-components"
                          {:src-id source-id
                           :bio-id (id/bio-id kb ice-id)}))))

(define-interface-fn processes kb
  [source-id]
  (let [ice-id (id/id->ice-uri source-id (:iao-namespaces kb))]
    (sparql-query kb
                  (render "sparql/protein/processes"
                          {:src-id source-id
                           :bio-id (id/bio-id kb ice-id)}))))

(defn ^{:private true} bio->ext
  [id-list]
  (let [ids (s/split id-list separator-re)
        short-ids (sort (map id/ice-uri->id ids))]
    (s/join separator short-ids)))

(define-interface-fn binary-interaction-partners kb
  [source-id]
  (let [ice-uri (id/id->ice-uri source-id (:iao-namespaces kb))
        [iao eid] (id/ext-id->parts source-id)]
    (map #(dissoc (assoc % '?/ext_partner_ids (bio->ext (get % '?/partner_ice_ids)))
                  '?/partner_ice_ids)
         (sparql-query kb
                       (render "sparql/protein/binary-interaction-partners"
                               {:src-id source-id
                                :bio-id (id/bio-id kb ice-uri)
                                :separator separator})))))
