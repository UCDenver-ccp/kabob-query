
(ns kabob-query.sparql.id-test
  (:require [kabob-query.kb :refer [sparql-query]]
            [kabob-query.sparql.id :as id])
  (:use [midje.sweet]))

(fact
  (id/query:bioentity ..kb.. "SRC_123_ICE")
  => '[{?/id kbio/BIO_123}]
  (provided (sparql-query ..kb.. anything) => '[{?/id kbio/BIO_123}]))

(fact
  (#'id/bio-id {:ns-map-to-long {"kbio" "http://kabob/bio/"}} ..ice-id..)
  => "http://kabob/bio/BIO_123"
  (provided (#'id/query:bioentity anything anything)
            => '[{?/id kbio/BIO_123}]))

(facts
  (fact (id/ice-id->entity-id "FOO_P123_ICE") => "P123")
  (fact (id/ice-id->entity-id "FOO_P_123_ICE") => "P_123"))

(facts
  (fact
    (#'id/id->ice-uri "uniprot:p0" {"iaouniprot" "http://kabob/iao/uniprot/"})
    => "http://kabob/iao/uniprot/UNIPROT_P0_ICE")
  (fact
    (#'id/id->ice-uri "uniprot:p0" {})
    => (throws #"'uniprot' is not a valid IAO")))

(facts
  (fact
    (id/ice-uri->id "http://kabob.ucdenver.edu/iao/uniprot/UNIPROT_A0A0G2KB10_ICE")
    => "uniprot:A0A0G2KB10")
  (fact
    (id/ice-uri->id "http://kabob.ucdenver.edu/iao/refseq/REFSEQ_XP_005158717_ICE")
    => "refseq:XP_005158717"))
