
(set-env! :resource-paths
          #{"resources"})

(task-options! pom
               {:project 'edu.ucdenver.ccp/kabob-query-templates
                :version "0.1.0-SNAPSHOT"}
               target
               {:dir #{"target"}})

(deftask build
  []
  (comp (pom) (jar) (target)))

;; Use `boot build -- install` to install the jar into the local maven
;; repository.
