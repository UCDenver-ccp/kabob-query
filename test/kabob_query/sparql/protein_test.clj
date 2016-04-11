
(ns kabob-query.sparql.protein-test
  (:require [edu.ucdenver.ccp.kr.kb :refer [kb open]]
            [kabob-query.core :refer [query]]
            [kabob-query.sparql.protein]
            [kabob-query.test.util :refer [*result* collect]])
  (:use [midje.sweet]))

(fact
  (#'kabob-query.sparql.protein/id->ice-uri "uniprot:p0" {"iaouniprot" "http://kabob/iao/uniprot/"})
  => "http://kabob/iao/uniprot/UNIPROT_P0_ICE")

(fact
  (#'kabob-query.sparql.protein/id->ice-uri "uniprot:p0" {})
  => (throws #"'uniprot' is not a valid IAO"))

(fact
  (#'kabob-query.sparql.protein/bio-id {:ns-map-to-long {"kbio" "http://kabob/bio/"}} ...ice-id...)
  => "http://kabob/bio/BIO_123"
  (provided (#'kabob-query.sparql.protein/query:bioentity anything anything)
            => '[{?/id kbio/BIO_123}]))

(binding [*result* (atom [])]
  (facts
    (fact (query "sparql/protein/cellular-components" ["uniprot:p123"] {} collect)
      => nil)
    (fact @*result*
      => [])
    (against-background
      (#'kabob-query.kb/open-kb-impl {}) => (open (kb :sesame-mem))
      (#'kabob-query.sparql.protein/query:bioentity anything anything) => '[{?/id kbio/BIO_123}])))

(binding [*result* (atom [])]
  (facts
    (fact (query "sparql/protein/processes" ["uniprot:p123"] {} collect)
      => nil)
    (fact @*result*
      => [])
    (against-background
      (#'kabob-query.kb/open-kb-impl {}) => (open (kb :sesame-mem))
      (#'kabob-query.sparql.protein/query:bioentity anything anything) => '[{?/id kbio/BIO_123}])))
