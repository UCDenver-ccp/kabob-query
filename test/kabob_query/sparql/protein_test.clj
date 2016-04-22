
(ns kabob-query.sparql.protein-test
  (:require [edu.ucdenver.ccp.kr.kb :refer [kb open]]
            [kabob-query.core :refer [query]]
            [kabob-query.sparql.id :as id]
            [kabob-query.test.util :refer [*result* collect]])
  (:use [midje.sweet]))

(fact
  (#'id/id->ice-uri "uniprot:p0" {"iaouniprot" "http://kabob/iao/uniprot/"})
  => "http://kabob/iao/uniprot/UNIPROT_P0_ICE")

(fact
  (#'id/id->ice-uri "uniprot:p0" {})
  => (throws #"'uniprot' is not a valid IAO"))

(fact
  (#'id/bio-id {:ns-map-to-long {"kbio" "http://kabob/bio/"}} ..ice-id..)
  => "http://kabob/bio/BIO_123"
  (provided (#'id/query:bioentity anything anything)
            => '[{?/id kbio/BIO_123}]))

(binding [*result* (atom [])]
  (facts
    (fact (query "sparql/protein/cellular-components" ["uniprot:p123"] {} collect)
      => nil)
    (fact @*result*
      => [])
    (against-background
      (#'kabob-query.kb/open-kb-impl {}) => (open (kb :sesame-mem))
      (#'id/query:bioentity anything anything) => '[{?/id kbio/BIO_123}])))

(binding [*result* (atom [])]
  (facts
    (fact (query "sparql/protein/processes" ["uniprot:p123"] {} collect)
      => nil)
    (fact @*result*
      => [])
    (against-background
      (#'kabob-query.kb/open-kb-impl {}) => (open (kb :sesame-mem))
      (#'id/query:bioentity anything anything) => '[{?/id kbio/BIO_123}])))

(facts "The 'highest' lexographically sorted ID is used as the primary ID."
  (fact "uniprot"
    (#'id/ice-id1 "uniprot" ["UNIPROT_P01_ICE" "UNIPROT_P09_ICE" "UNIPROT_P03_ICE"])
    => "P09"))

(facts "UniProt IDs that start with 'P' are preferred."
  (fact "the only P-id is selected"
    (#'id/ice-id1 "uniprot" ["UNIPROT_Q1_ICE" "UNIPROT_P1_ICE" "UNIPROT_R1_ICE"])
    => "P1")
  (fact "the 'highest' P-id is selected"
    (#'id/ice-id1 "uniprot" ["UNIPROT_P9_ICE" "UNIPROT_P1_ICE" "UNIPROT_R1_ICE"])
    => "P9")
  (fact "in the absence of a P-id, the 'highest' ID is selected."
    (#'id/ice-id1 "uniprot" ["UNIPROT_Q1_ICE" "UNIPROT_X1_ICE" "UNIPROT_R1_ICE"])
    => "X1"))
