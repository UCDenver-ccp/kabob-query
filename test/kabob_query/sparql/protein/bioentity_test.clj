
(ns kabob-query.sparql.protein.bioentity-test
  (:require [clojure.java.io :refer [resource]]
            [edu.ucdenver.ccp.kr.kb :refer [open kb]]
            [kabob-query.core :refer [query]]
            [kabob-query.test.util :refer [*result* collect]])
  (:use [midje.sweet]))

(background (#'kabob-query.core/open-kb {}) => (open (kb :sesame-mem)))
