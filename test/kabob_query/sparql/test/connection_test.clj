
(ns kabob-query.sparql.test.connection-test
  (:require [edu.ucdenver.ccp.kr.kb :refer [open kb]]
            [kabob-query.core :refer [query]]
            [kabob-query.kb :refer [open-kb]]
            [kabob-query.test.util :refer [*result* collect]])
  (:use [midje.sweet]))

(background (#'kabob-query.kb/open-kb {}) => (open (kb :sesame-mem)))

(facts
  (binding [*result* (atom [])]
    (fact (query "sparql/test/connection" ["1"] {} collect)
      => nil)
    (fact @*result*
      => '[{?/a 1}])))
