
(defproject edu.ucdenver.ccp/kabob-query "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.cli "0.3.3"]
                 [org.slf4j/slf4j-nop  "1.7.20"]
                 [sinistral/mantle "0.2.1"]
                 [stencil "0.5.0"]
                 [edu.ucdenver.ccp/kabob-build "1.3.0-SNAPSHOT"
                  :exclusions [org.slf4j/slf4j-log4j12 potemkin]]
                 [edu.ucdenver.ccp/kabob-query-templates "0.2.0-SNAPSHOT"]
                 [edu.ucdenver.ccp/kr-core "1.4.20-SNAPSHOT"]
                 [edu.ucdenver.ccp/kr-sesame-core "1.4.20-SNAPSHOT"]]

  :profiles {:dev {:dependencies [[midje "1.8.3"]]
                   :plugins [[lein-midje "3.2"]]}
             :uberjar {:aot :all}}

  :main kabob-query.cli)
