
(ns kabob-query.sparql.id-test
  (:require [kabob-query.kb :refer [sparql-query]]
            [kabob-query.sparql.id :as id])
  (:use [midje.sweet]))

(fact
  (id/query:bioentity ..kb.. "SRC_123_ICE")
  => '[{?/id kbio/BIO_123}]
  (provided (sparql-query ..kb.. anything) => '[{?/id kbio/BIO_123}]))

(facts
  (fact (id/ice-id->entity-id "FOO_P123_ICE") => "P123")
  (fact (id/ice-id->entity-id "FOO_P_123_ICE") => "P_123"))


(facts
  (fact (id/ice-uri->id "http://kabob.ucdenver.edu/iao/uniprot/UNIPROT_A0A0G2KB10_ICE") => "uniprot:A0A0G2KB10")
  (fact (id/ice-uri->id "http://kabob.ucdenver.edu/iao/refseq/REFSEQ_XP_005158717_ICE") => "refseq:XP_005158717"))

(facts "The 'highest' lexographically sorted ID is used as the primary ID."
  (fact "default"
    (#'id/ice-id1 "iao-w/out-method" ["SRC_03_ICE" "SRC_09_ICE" "SRC_01_ICE"])
    => "09"))
