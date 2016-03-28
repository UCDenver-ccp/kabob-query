
(defproject edu.ucdenver.ccp/kabob-query "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [stencil "0.5.0"]
                 [edu.ucdenver.ccp/kr-core "1.4.20-SNAPSHOT"] ;; [1]
                 [edu.ucdenver.ccp/kabob-query-templates "0.1.0-SNAPSHOT"]]
  :profiles {:dev {:dependencies [[edu.ucdenver.ccp/kr-sesame-core "1.4.20-SNAPSHOT"]]}})

;; [1] Note: If you change this version, be sure to also change the
;;           version of the implementation package (kr-{sesame,jena}-core)
