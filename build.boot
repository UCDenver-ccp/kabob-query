
(set-env! :resource-paths
          #{"resources" "src"}
          :dependencies '[[sinistral/mantle "0.2.1"]])

(task-options! pom
               {:project 'edu.ucdenver.ccp/kabob-query-templates
                :version "0.1.0-SNAPSHOT"}
               target
               {:dir #{"target"}})

(require '[build.boot :refer :all])

;; Use `boot build -- install` to install the jar into the local maven
;; repository.
