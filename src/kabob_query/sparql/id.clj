
(ns kabob-query.sparql.id
  "In which is defined the mechanisms to map between ICE and BIO entity
  identifiers."
  (:require [clojure.string :as s]
            [mantle.collection :refer [single]]
            [mantle.io :refer [fmtstr]]
            [kabob-query.kb :refer [sparql-query]]
            [kabob-query.template :refer [render]])
  (:import java.util.regex.Pattern))

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

(defn ice-uri->id
  "Translate an ICE URI into a source-specific identifier of the form
  `<source>:<id>`, for example: `uniprot:p15692`."
  [ice-id]
  (let [ice-re #"http://kabob.ucdenver.edu/iao/([^/]+)/(.+)_ICE"
        [match nmspc ns_id] (re-matches ice-re ice-id)
        id (s/replace-first ns_id
                            (Pattern/compile (str (s/upper-case nmspc) "_"))
                            "")]
    (s/join ":" [nmspc id])))

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
