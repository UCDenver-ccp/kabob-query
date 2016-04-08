
(ns kabob-query.kb
  "In which is defined the mechanisms for interacting with (and specifically:
  querying) a knowledge base instance."
  (:require [edu.ucdenver.ccp.kr.kb
             :refer [kb open]]
            [edu.ucdenver.ccp.kr.rdf
             :refer [*use-inference* synch-ns-mappings]]
            [edu.ucdenver.ccp.kr.sesame.kb
             :refer [*default-server* *repository-name* *username* *password*]]
            [edu.ucdenver.ccp.kr.sparql :as krs])
  (:import [org.openrdf.repository.http HTTPRepository]))

(def ^{:dynamic true} *kb*
  "A global variable to store the instance of the knowledge base that is being
  used for the processing of an API call.  Please do not set the root binding
  of this variable!  It exists only to make it possible to avoid having the
  `kb` argument on API functions.  The expected pattern of use is to wrap the
  invocation of an API function with a `binding` to the appropriate open
  store."
  nil)

(defn- select-iao-namespaces
  [kb-ns-map]
  (select-keys kb-ns-map
               (reduce #(conj %1 (first %2))
                       []
                       (filter #(> (.indexOf (second %) "/iao/") 0)
                               kb-ns-map))))

(defn open-kb
  [params]
  ;; FIXME: The binding form below points to a deficiency in KR: having to bind
  ;; these global variables and forces the immediate client to be aware of the
  ;; type of repository being created, even if it is not responsible for making
  ;; that selection (in this case that determination is made by the user
  ;; decidiing on the store against which queries are to be run).  KR should
  ;; provide a factory method that can return an initialized KB instance based
  ;; on some configuration data that can be treated as an opaque blob by the
  ;; immediate client.
  ;; TODO: Open an issue for this!
  (binding [*default-server* (:db-url params)
            *repository-name* (:repository-name params)
            *username* (:username params)
            *password* (:password params)]
    (let [kb-instance (synch-ns-mappings (open (kb HTTPRepository)))
          ;; Attach the IAO namespaces to the KB.  These are identified
          ;; separately to enable the query chaining and translation functions
          ;; to render the identifiers as full URIs and not the shortened
          ;; prefix form returned by KR.  Giving these functions the ability to
          ;; map between short and long forms relieves the query template
          ;; author from having to specify the short form PREFIXs for every
          ;; possible source in the template.
          iao-nspaces (select-iao-namespaces (:ns-map-to-long kb-instance))]
      (if (seq iao-nspaces)
        (assoc kb-instance :iao-namespaces iao-nspaces)
        (ex-info "No IAO namespaces registered in KB" {:registered-namespaces (:ns-map-to-long kb-instance)})))))

(defn sparql-query
  [kb query-string]
  (binding [*use-inference* false]
    (krs/sparql-query kb query-string)))
