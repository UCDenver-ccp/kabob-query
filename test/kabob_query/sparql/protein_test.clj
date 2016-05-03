
(ns kabob-query.sparql.protein-test
  (:require [edu.ucdenver.ccp.kr.kb :refer [kb open]]
            [kabob-query.core :refer [query]]
            [kabob-query.kb :refer [sparql-query]]
            [kabob-query.sparql.id :as id]
            [kabob-query.sparql.protein :as p]
            [kabob-query.test.util :refer [*result* collect]])
  (:use [midje.sweet]))

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

(binding [*result* (atom [])]
  (facts
    (fact (query "sparql/protein/binary-interaction-partners" ["uniprot:p123"] {} collect)
      => nil)
    (fact @*result*
      => '[{?/ext_partner_ids ["uniprot:P01" "uniprot:P02" "uniprot:P03"]
            ?/ext_source_id "uniprot:p123"}])
    (against-background
      (#'kabob-query.kb/open-kb-impl {}) => (open (kb :sesame-mem))
      (#'id/query:bioentity anything anything) => '[{?/id kbio/BIO_123}]
      (#'p/bio->ext anything) => ["uniprot:P01" "uniprot:P02" "uniprot:P03"])))

(binding [*result* (atom [])]
  (facts
    (fact (query "sparql/protein/targeted-by-drug" ["uniprot:p123"] {} collect)
      => nil)
    (fact @*result*
      => '[{?/ext_drug_ids ["drugbank:DB01" "drugbank:DB02" "drugbank:DB03"]
            ?/ext_source_id "uniprot:p123"}])
    (against-background
      (#'kabob-query.kb/open-kb-impl {}) => (open (kb :sesame-mem))
      (#'id/query:bioentity anything anything) => '[{?/id kbio/BIO_123}]
      (#'p/bio->ext anything) => ["drugbank:DB01" "drugbank:DB02" "drugbank:DB03"])))
