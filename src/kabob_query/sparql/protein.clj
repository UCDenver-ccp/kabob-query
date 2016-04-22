
(ns kabob-query.sparql.protein
  (:require [clojure.string :as s]
            [mantle.collection :refer [single]]
            [kabob-query.api :refer [define-interface-fn]]
            [kabob-query.kb :refer [sparql-query]]
            [kabob-query.sparql.id :as id :refer [ice-id1]]
            [kabob-query.template :refer [render]]))

;; ------------------------------------------------------------- utility --- ;;

(defmethod ^{:private true} ice-id1 "uniprot" ice-id1:uniprot
  [iao ids]
  (let [entry-ids (sort (map id/ice-id->entity-id ids))]
    (or (last (reduce #(if (.startsWith %2 "P") (conj %1 %2) %1) [] entry-ids))
        (last entry-ids))))

;; ----------------------------------------------------------------- API --- ;;

(define-interface-fn cellular-components kb
  [source-id]
  (let [ice-id (id/id->ice-uri source-id (:iao-namespaces kb))]
    (sparql-query kb
                  (render "sparql/protein/cellular-components"
                          {:src-id source-id
                           :ice-id ice-id
                           :bio-id (id/bio-id kb ice-id)}))))

(define-interface-fn processes kb
  [source-id]
  (let [ice-id (id/id->ice-uri source-id (:iao-namespaces kb))]
    (sparql-query kb
                  (render "sparql/protein/processes"
                          {:src-id source-id
                           :ice-id ice-id
                           :bio-id (id/bio-id kb ice-id)}))))

(define-interface-fn binary-interaction-partners kb
  [source-id]
  (let [ice-uri (id/id->ice-uri source-id (:iao-namespaces kb))
        [iao eid] (id/ext-id->parts source-id)
        bio->ext (fn [id]
                   (let [fqbn (id/fq kb id)
                         enid (id/ice-id kb :protein iao fqbn)]
                     (id/parts->ext-id iao enid)))]
    ;; We enrich the resultset that is returned with our best guess at the
    ;; 'primary' external ID for the partner proteins.  Unfortunately, this is
    ;; quite and expensive operation, since it requires us to go back to the
    ;; store to retrieve all of the source-specific IDs that may denote the BIO
    ;; entity.
    ;;
    ;; Ideally, we'd want to group by the `partner_bio_id` and use something
    ;; like MySQL's `group_concat` to collapse the all of the records for each
    ;; partner ID into a single row.
    (map #(assoc % '?/ext_partner_id (bio->ext (get % '?/partner_bio_id)))
         (sparql-query kb
                       (render "sparql/protein/binary-interaction-partners"
                               {:src-id source-id
                                :ice-id ice-uri
                                :bio-id (id/bio-id kb ice-uri)})))))
