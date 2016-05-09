
(ns kabob-query.sparql.label-test
  (:require [edu.ucdenver.ccp.kr.kb :refer [kb open]]
            [kabob-query.core :refer [query]]
            [kabob-query.kb :refer [sparql-query]]
            [kabob-query.sparql.id :as id]
            [kabob-query.sparql.label :as p]
            [kabob-query.test.util :refer [*result* collect]])
  (:use [midje.sweet]))

(binding [*result* (atom [])]
  (facts
    (fact (query "sparql/label/bioentity-label" ["uniprot:p123"] {} collect)
      => nil)
    (fact @*result*
      => [])
    (against-background
      (#'kabob-query.kb/open-kb-impl {}) => (open (kb :sesame-mem))
      (#'id/query:bioentity anything anything) => '[{?/id kbio/BIO_123}])))
