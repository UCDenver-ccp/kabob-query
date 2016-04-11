
(ns kabob-query.test.util
  (:refer-clojure :exclude [load])
  (:require [clojure.java.io :refer [reader resource]]
            [midje.sweet])
  (:import [org.openrdf.model Resource]
           [org.openrdf.rio RDFFormat]))

(def ^{:dynamic true} *result* nil)

(defn collect
  [r]
  (swap! *result* #(conj % r)))

(defn load
  [kb f]
  (let [load-file (resource f)]
    (.add (:connection kb)
          (reader load-file) (str (.toURI load-file)) RDFFormat/NTRIPLES (make-array Resource 0))
    (.commit (:connection kb)))
  kb)

(defmacro with-data
  [dataset & body]
  `(let [kb# (edu.ucdenver.ccp.kr.kb/open
              (edu.ucdenver.ccp.kr.kb/kb :sesame-mem))]
     (midje.sweet/with-state-changes [(midje.sweet/before :contents (kabob-query.test.util/load kb# ~dataset))]
       (midje.sweet/facts
         ~@body
         (midje.sweet/against-background (#'kabob-query.kb/open-kb-impl {}) midje.sweet/=> kb#)))))
