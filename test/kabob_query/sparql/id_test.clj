
(ns kabob-query.sparql.id-test
  (:require [kabob-query.kb :refer [sparql-query]]
            [kabob-query.sparql.id :refer [query:bioentity bio-id]])
  (:use [midje.sweet]))

(fact
  (query:bioentity ..kb.. "SRC_123_ICE")
  => '[{?/id kbio/BIO_123}]
  (provided (sparql-query ..kb.. anything) => '[{?/id kbio/BIO_123}]))
