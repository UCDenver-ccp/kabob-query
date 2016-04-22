
(ns kabob-query.sparql.id
  "In which is defined the mechanisms to map between ICE and BIO entity
  identifiers."
  (:require [clojure.string :as s]
            [mantle.collection :refer [single]]
            [mantle.io :refer [fmtstr]]
            [kabob-query.kb :refer [sparql-query]]
            [kabob-query.template :refer [render]]))

(defn ext-id->parts
  "Split into parts the external identifier that a user might specify to
  describe the ID and the source from which it hails, e.g. \"uniprot:P12345\"."
  [id]
  (s/split id #":"))

(defn parts->ext-id
  "Construct from parts the external identifier that a user might specify to
  describe the ID and the source from which it hails, e.g. \"uniprot:P12345\"."
  [iao eid]
  (str iao ":" eid))

(defn fq
  "Fully qualify the short names returned by KR."
  ;; FIXME: We *really* shouldn't need to do this.  KR shouldn't default to
  ;; short names.
  ;;
  ;; FIXME: Taking the kb as parameter is convenient, but I fear that it is the
  ;; wrong thing to do (since all we actually need is the namespace map), and
  ;; that it creates the wrong impression (that a call to the KB is required).
  ;; On the other hand, requiring only the map puts a greater burden on the
  ;; caller to know the detail of the KB structure.
  [kb sym]
  (str (get (:ns-map-to-long kb) (namespace sym)) (name sym)))

(defn id->ice-uri
  "Translate a source-specific identifier into a knowledge base ICE identifier.
  Source IDs are expected to be of the form `<source>:<id>`, for example:
  `uniprot:p15692`."
  [id ns-map]
  (let [[source id] (ext-id->parts id)]
    (if-let [uri-ns (get ns-map (s/join ["iao" (s/lower-case source)]))]
      (s/join [uri-ns (s/upper-case (s/join "_" [source id "ice"]))])
      (throw (ex-info (fmtstr "'~a' is not a valid IAO source identifier." source) (or ns-map {}))))))

(defn query:bioentity
  [kb ice-id]
  (sparql-query kb (render "sparql/id/bioentity" {:ice-id ice-id})))

(defn bio-id
  [kb ice-id]
  (fq kb (single (vals (single (query:bioentity kb ice-id))))))

(defn query:ice
  "Returns a resultset containing the ICE identifiers that denote the specified
  BIO identifier."
  [kb bio-id]
  (sparql-query kb (render "sparql/id/ice" {:bio-id bio-id})))

(defn ice-id->entity-id
  "Return the source-specific identifier from which the KB's ICE identifier was
  constructed; e.g. for \"UNIPROT_P12345_ICE\", return \"P12345\"."
  [id]
  (s/join "_" (butlast (rest (s/split (name id) #"_")))))

(defmulti ice-id
  "Return the primary (for some definition of 'primary') ICE identifier for a
  BIO entity of a particular type, e.g. `:protein`, `:gene`, etc."
  (fn [kb entity-type iao bio-id] entity-type))

(defmulti ice-id1
  "Since it is possible for a BIO entity to be denoted by multiple ICE
  identifiers, we need some way to select one of these as *the* identifier to
  which it should be mapped.  The method by which the primary identifier is
  determined may vary by source (IAO)."
  (fn [iao ids] iao))

(defmethod ice-id1 :default ice-id1:default
  [_ ids]
  (last (sort (map ice-id->entity-id ids))))

(defmethod ice-id :default ice-id:default
  [kb _ iao bio-id]
  (ice-id1 iao (filter #(= (str "iao" iao) (namespace %))
                       (map #(single (vals %)) (query:ice kb bio-id)))))
