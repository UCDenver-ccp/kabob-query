(ns kabob-query.sparql.process
  (:require [clojure.string :as s]
            [kabob-query.api :refer [define-interface-fn separator separator-re]]
            [kabob-query.kb :refer [sparql-query]]
            [kabob-query.sparql.id :as id]
            [kabob-query.template :refer [render]]))

(define-interface-fn participants kb
  [process-id]
  (let [bio->ext (fn [id_list]
                   (let [ids (s/split id_list separator-re)
                         short_ids (sort (map id/ice-uri->id ids))]
                     (s/join separator short_ids )))]
    (map #(assoc % '?/ext_participant_ids (bio->ext (get % '?/participant_ice_ids)))
         (sparql-query kb
                       (render "sparql/process/participants"
                               {:process-id process-id
                                :separator separator})))))
