
(ns kabob-query.sparql.drug-test
  (:require [edu.ucdenver.ccp.kr.kb :refer [kb open]]
            [kabob-query.core :refer [query]]
            [kabob-query.kb :refer [sparql-query]]
            [kabob-query.sparql.id :as id]
            [kabob-query.sparql.drug :as p]
            [kabob-query.test.util :refer [*result* collect]])
  (:use [midje.sweet]))

(binding [*result* (atom [])]
  (facts
    (fact (query "sparql/drug/protein-targets" ["drugbank:db123"] {} collect)
      => nil)
    (fact @*result*
      => '[{?/ext_target_ids ["uniprot:P01" "uniprot:P02" "uniprot:P03"]
            ?/ext_source_id "drugbank:db123"}])
    (against-background
      (#'kabob-query.kb/open-kb-impl {}) => (open (kb :sesame-mem))
      (#'id/query:bioentity anything anything) => '[{?/id kbio/BIO_123}]
      (#'p/bio->ext anything) => ["uniprot:P01" "uniprot:P02" "uniprot:P03"])))
