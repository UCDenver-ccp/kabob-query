
(ns kabob-query.sparql.id
  "In which is defined the mechanisms to map between ICE and BIO entity
  identifiers."
  (:require [clojure.string :as str]
            [mantle.collection :refer [single]]
            [mantle.io :refer [fmtstr]]
            [kabob-query.kb :refer [sparql-query]]
            [kabob-query.template :refer [render]]))

(defn id->ice-uri
  "Translate a source-specific identifier into a knowledge base ICE identifier.
  Source IDs are expected to be of the form `<source>:<id>`, for example:
  `uniprot:p15692`."
  [id ns-map]
  (let [[source id] (str/split id #":")]
    (if-let [uri-ns (get ns-map (str/join ["iao" (str/lower-case source)]))]
      (str/join [uri-ns (str/upper-case (str/join "_" [source id "ice"]))])
      (throw (ex-info (fmtstr "'~a' is not a valid IAO source identifier." source) (or ns-map {}))))))

(defn query:bioentity
  [kb ice-id]
  (sparql-query kb (render "sparql/id/bioentity" {:ice-id ice-id})))

(defn bio-id
  [kb ice-id]
  (let [sym (single (vals (single (query:bioentity kb ice-id))))]
    (str (get (:ns-map-to-long kb) (namespace sym)) (name sym))))
