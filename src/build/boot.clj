
(ns build.boot
  (:require [boot.core :as boot]
            [boot.task.built-in :as task]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [mantle.io :refer [fmtstr]]))

(defn- template-name
  [f]
  (first (s/split (boot/tmp-path f) #"\.")))

(defn- write-index
  [index templs]
  (spit index (fmtstr "~{~a~%~}" (sort (map template-name templs)))))

(boot/deftask index
  "Create an index file that lists the available queries.  This simplifies
  discovery by providing a constant marker file."
  []
  (let [tmp-dir (boot/tmp-dir!)]
    (fn middleware [next-handler]
      (fn handler [fileset]
        (boot/empty-dir! tmp-dir)
        (let [input-files (boot/input-files fileset)
              templ-files (boot/by-ext [".mustache"] input-files)
              index-file  (io/file tmp-dir "index")]
          (io/make-parents index-file)
          (write-index index-file templ-files))
        (-> fileset
            (boot/add-resource tmp-dir)
            (boot/commit!)
            (next-handler))))))

(boot/deftask build
  []
  (comp (task/pom) (index) (task/jar) (task/target)))
