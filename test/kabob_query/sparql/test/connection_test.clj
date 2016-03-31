
(ns kabob-query.sparql.test.connection_test
  (:require [clojure.java.io :refer [resource]]
            [edu.ucdenver.ccp.kr.kb :refer [open kb]]
            [kabob-query.core :refer [query]]
            [kabob-query.test.util :refer [*result* collect]])
  (:use [midje.sweet]))

(background (#'kabob-query.core/open-kb {}) => (open (kb :sesame-mem)))

(facts
  (binding [*result* (atom [])]
    (fact (query (resource "sparql/test/connection.mustache") {:value 1} {} collect)
      => nil)
    (fact @*result*
      => '[{?/a 1}])))
