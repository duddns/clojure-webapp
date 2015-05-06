(ns my-webapp.db
  (:require [clojure.java.jdbc :as sql]))

(def db-spec {:classname "org.h2.Driver"
              :subprotocol "h2:file"
              :subname "db/my-webapp"})

(defn add-location-to-db
  [x y]
  (let [results (sql/with-connection db-spec
                                     (sql/insert-record :locations
                                                        {:x x :y y}))]
    (assert (= (count results) 1))
    (first (vals results))))

(defn update-location
  [loc-id x y]
  (let [results (sql/with-connection db-spec
                                     (sql/update-values :locations ["id=?" loc-id] {:x x :y y}))]
    results))

(defn delete-location
  [loc-id]
  (let [results (sql/with-connection db-spec
                                     (sql/delete-rows :locations ["id=?" loc-id]))]
    results))

(defn get-xy
  [loc-id]
  (let [results (sql/with-connection db-spec
                                     (sql/with-query-results res
                                                             ["select x, y from locations where id = ?" loc-id]
                                                             (doall res)))]
    (assert (= (count results) 1))
    (first results)))

(defn get-all-locations
  []
  (let [results (sql/with-connection db-spec
                                     (sql/with-query-results res
                                                             ["select id, x, y from locations"]
                                                             (doall res)))]
    results))