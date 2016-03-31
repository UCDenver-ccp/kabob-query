
(ns kabob-query.test.util
  (:refer-clojure :exclude [load])
  (:require [clojure.java.io :refer [reader resource]])
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
    (.commit (:connection kb))))
