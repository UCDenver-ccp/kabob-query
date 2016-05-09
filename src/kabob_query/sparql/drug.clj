
(ns kabob-query.sparql.drug
  (:require [clojure.string :as s]
            [kabob-query.api :refer [define-interface-fn separator separator-re]]
            [kabob-query.kb :refer [sparql-query]]
            [kabob-query.sparql.id :as id]
            [kabob-query.template :refer [render]]))

(defn ^{:private true} bio->ext
  [id-list]
  (let [ids (s/split id-list separator-re)
        short-ids (sort (map id/ice-uri->id ids))]
    (s/join separator short-ids)))

(define-interface-fn protein-targets kb
  [source-id]
  (let [ice-uri (id/id->ice-uri source-id (:iao-namespaces kb))
        [iao eid] (id/ext-id->parts source-id)]
    (map #(dissoc (assoc % '?/ext_target_ids (bio->ext (get % '?/target_ice_ids)))
                  '?/target_ice_ids)
         (sparql-query kb
                       (render "sparql/drug/protein-targets"
                               {:src-id source-id
                                :bio-id (id/bio-id kb ice-uri)
                                :separator separator})))))
