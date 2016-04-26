(ns kabob-query.sparql.process
  (:require [clojure.string :as s]
            [clojure.pprint :refer [pprint]]
            [mantle.collection :refer [single]]
            [kabob-query.api :refer [define-interface-fn]]
            [kabob-query.kb :refer [sparql-query]]
            [kabob-query.sparql.id :as id :refer [ice-id1]]
            [kabob-query.template :refer [render]]))

(define-interface-fn participants kb
  [process-id]
  ;; the query uses group_concat to return a semi-colon-delimited list of
  ;; ICE URIs for the interaction partners.
  (let [bio->ext (fn [id_list]
                   (let [ids (s/split id_list #";")
                         short_ids (sort (map id/ice-uri->id ids))]
                     (s/join ";" short_ids )))]
    (map #(assoc % '?/ext_participant_ids (bio->ext (get % '?/participant_ice_ids)))
         (sparql-query kb
                       (render "sparql/process/participants"
                               {:process-id process-id})))))
