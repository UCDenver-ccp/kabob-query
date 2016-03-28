
(ns kabob-query.core
  (:require [clojure.java.io
             :refer [resource]]
            [stencil.core
             :refer [render-string]]
            [edu.ucdenver.ccp.kr.kb
             :refer [kb open]]
            [edu.ucdenver.ccp.kr.sesame.kb
             :refer [*default-server* *repository-name* *username* *password*]]
            [edu.ucdenver.ccp.kr.sparql
             :refer [sparql-query]])
  (:import [org.openrdf.repository.http HTTPRepository]))

(defn open-kb
  [params]
  (binding [*default-server* (:db-url params)
            *repository-name* (:repository-name params)
            *username* (:username params)
            *password* (:password params)]
    (open (kb HTTPRepository))))

(defn query
  [query-name query-args kb-params]
  (with-open [kb (open-kb kb-params)]
    (sparql-query kb (render-string (slurp (resource query-name)) query-args))))
