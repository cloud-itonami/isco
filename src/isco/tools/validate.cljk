(ns isco.tools.validate
  (:require [clojure.edn :as edn]
            [clojure.set :as set]))

(defn validation-result [document]
  (let [attrs (:attributes document)
        code-attr (:db/ident (first (filter #(= (:db/unique %) :db.unique/identity) attrs)))
        code-ns (namespace code-attr)
        name-attr (keyword code-ns "name")
        attr-by-suffix (fn [suffix]
                         (some #(when (= (name %) suffix) %) (map :db/ident attrs)))
        parent-attr (or (attr-by-suffix "parent") (keyword code-ns "parent"))
        level-attr (or (attr-by-suffix "level") (keyword code-ns "level"))
        sourcing-attr (or (attr-by-suffix "sourcing") (keyword code-ns "sourcing"))
        seed (:seed document)
        ids (set (keep :db/id seed))
        declared (set (map :db/ident attrs))
        used-namespaced (into #{} (filter namespace) (mapcat keys seed))
        checks
        {:unique-codes (let [codes (map #(get % code-attr) seed)]
                         (= (count codes) (count (set codes))))
         :complete-rows (every? #(and (map? %) (:db/id %) (get % code-attr) (get % name-attr)) seed)
         :parent-resolution (every? #(let [parent (get % parent-attr)]
                                       (or (nil? parent) (contains? ids parent))) seed)
         :declared-attributes (set/subset? (disj used-namespaced :db/id) declared)
         :sourcing-tagged (every? #(contains? #{:authoritative :representative :synthesized}
                                              (get % sourcing-attr)) seed)}]
    {:valid? (every? true? (vals checks))
     :checks checks
     :count (count seed)
     :levels (frequencies (map #(get % level-attr) seed))}))

(defn validate-file [path]
  (validation-result (edn/read-string (slurp path))))
