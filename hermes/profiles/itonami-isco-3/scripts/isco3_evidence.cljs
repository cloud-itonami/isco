;; itonami-isco-3 (Technicians and Associate Professionals) business bot — decision-free collector.
;; Classifies each member blueprint actor by maturity from its own README and
;; reports the kotoba-native (amu check --jvm-free) readiness of the frontier
;; actor.  The agent reads SCANNED + member list and reports ONE finding.
;; Run: nbb scripts/isco3_evidence.cljs

(require '[clojure.string :as str]
         '["node:child_process" :as cp]
         '["node:fs" :as fs]
         '["node:path" :as path])

(def root "~/github/com-junkawasaki")
(def org "orgs/cloud-itonami")
(def repo-prefix "cloud-itonami-isco-")

;; Technicians and Associate Professionals member blueprint repos (60).
(def members
  [310 3111 3112 3113 3114 3115 3116 3117 3118 3119 3121 3122 3123 3131 3132 3133 3134 3135 3139 3141 3142 3143 3151 3152 3153 3154 3155 3213 3253 3255 3311 3312 3313 3314 3315 3321 3322 3323 3324 3331 3332 3333 3334 3339 3341 3342 3343 3344 3351 3352 3353 3354 3355 3359 3511 3512 3513 3514 3521 3522])

(defn repo-dir [code]
  (path/join root org (str repo-prefix code)))

(defn classify-maturity [code]
  (let [p (path/join (repo-dir code) "README.md")]
    (when (fs/existsSync p)
      (let [t (str (fs/readFileSync p "utf8"))]
        (cond
          (boolean (re-find #":implemented" t)) "implemented"
          (boolean (re-find #"R0|Status: R0|scaffold" t)) "scaffold"
          :else "present")))))

(defn find-count [dir matcher]
  (try
    (let [cmd (str "find " dir " -type f -name " matcher)
          out (str (cp/execSync cmd #js {:timeout 60000}))]
      (count (remove empty? (str/split out "\n"))))
    (catch :default _ 0)))

(defn lang-counts [code]
  (let [dir (repo-dir code)]
    (if (not (fs/existsSync dir))
      {:kotoba -1 :clj -1}
      {:kotoba (find-count dir "*.kotoba")
        :clj (+ (find-count dir "*.clj")
                (find-count dir "*.cljc")
                (find-count dir "*.cljs"))})))

(defn -main []
  (let [rows (map (fn [c]
                    (let [m (or (classify-maturity c) "unmeasured")
                          lc (lang-counts c)]
                      [c m (:kotoba lc) (:clj lc)]))
                  members)
        implemented (filter (fn [[c m _x _y]] (= m "implemented")) rows)
        kt (filter (fn [[c _x k _y]] (pos? k)) rows)
        mig (sort-by (fn [[_x _y _z c]] (if (neg? c) 100000 c))
                     (filter (fn [[c _x k cl]] (and (not (pos? k)) (pos? cl))) rows))
        frontier-row (first mig)]
    (println (str "SCANNED members=" (count rows)
                  ";implemented=" (count implemented)
                  ";kotoba-native=" (count kt)
                  ";migration-candidates=" (count mig)
                  ";frontier=" (if frontier-row (nth frontier-row 0) "none")))
    (doseq [[c m k cl] rows]
      (println (str "member\t" c "\t" m "\tkotoba=" k "\tclj=" cl)))
    (when frontier-row
      (println (str "NEXT-ACTION-RECOMMENDED " (nth frontier-row 0)
                    " — smallest remaining JVM actor (clj=" (nth frontier-row 3)
                    "); migrate its smallest vertical slice to .kotoba and gate with amu check --jvm-free per SOUL.md")))))

(-main)
