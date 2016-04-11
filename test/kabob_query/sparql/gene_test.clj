
(ns kabob-query.sparql.gene-test
  (:require [midje.sweet :refer [fact facts]]
            [kabob-query.core :refer [query]]
            [kabob-query.test.util :refer [*result* collect with-data]]))

;; --- by-taxon ------------------------------------------------------------ ;;

(with-data "test-data/sparql/gene/by-taxon.nt"
  (binding [*result* (atom [])]
    (facts "empty result set returned for unrepresented taxon ID"
      (fact (query "sparql/gene/by-taxon" ["9605"] {} collect) => nil)
      (fact @*result* => '[]))))

(with-data "test-data/sparql/gene/by-taxon.nt"
  (binding [*result* (atom [])]
    (facts "result set includes result for only the requested taxon ID"
      (fact (query "sparql/gene/by-taxon" ["9606"] {} collect) => nil)
      (fact @*result* => '[{?/gene kbio/BIO_g01}]))))

(with-data "test-data/sparql/gene/by-taxon.nt"
  (binding [*result* (atom [])]
    (facts "result set includes result for only the requested taxon ID"
      (fact (query "sparql/gene/by-taxon" ["9607"] {} collect) => nil)
      (fact @*result* => '[{?/gene kbio/BIO_g02}]))))

;; ------------------------------------------------------------------------- ;;
