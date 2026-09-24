;; itonami-isco-4 (ISCO major 4: Clerical Support Workers) business bot —
;; decision-free collector.  Measure-only: classifies each member blueprint
;; actor by maturity from its own README and runs ONE frontier actor's nbb test
;; suite.  The agent reads the SCANNED line + member list and reports ONE
;; finding.  Never re-measures inline; never fabricates a test result.
;; Run: nbb scripts/isco4_evidence.cljs

(require '[clojure.string :as str]
         '["node:child_process" :as cp]
         '["node:fs" :as fs]
         '["node:path" :as path]
         '["node:os" :as os])

(def root "~/github/com-junkawasaki")
(def org "orgs/cloud-itonami")

;; ISCO-08 major 4 "Clerical Support Workers" member blueprint repos (29).
(def members
  [4110 4120 4131 4132 4211 4212 4213 4214
   4221 4222 4223 4224 4225 4226 4227 4229
   4311 4312 4313 4321 4322 4323
   4411 4412 4413 4414 4415 4416 4419])

(defn repo-dir [code]
  (path/join root org (str "cloud-itonami-isco-" code)))

(defn classify-maturity [code]
  "Implemented | Scaffold | Unmeasured based only on the repo's own README."
  (let [p (path/join (repo-dir code) "README.md")]
    (when (fs/existsSync p)
      (let [t (str (fs/readFileSync p "utf8"))]
        (cond
          (boolean (re-find #":implemented" t)) "implemented"
          (boolean (re-find #"R0|Status: R0|scaffold" t)) "scaffold"
          :else "present")))))

(defn has-entry [code]
  (let [d (repo-dir code)]
    (cond
      (fs/existsSync (path/join d "run-tests.cljs")) "run-tests.cljs"
      (fs/existsSync (path/join d "run_tests.cljs")) "run_tests.cljs"
      :else nil)))

(defn find-count [dir matcher]
  "Count files under dir matching matcher (find -type f -name).  Returns 0 on
  error — a missing/unreadable tree is reported as 0, never as a bogus high
  number.  One plain command string (no shell interpolation)."
  (try
    (let [cmd (str "find " dir " -type f -name " matcher)
          out (str (cp/execSync cmd #js {:timeout 60000}))]
      (count (remove empty? (str/split out "\n"))))
    (catch :default _ 0)))

(defn lang-counts [code]
  "Count source files by family under the actor repo (exclude node_modules).
  Returns {:kotoba n :clj n}."
  (let [dir (repo-dir code)]
    (if (not (fs/existsSync dir))
      {:kotoba -1 :clj -1}
      {:kotoba (find-count dir "*.kotoba")
       :clj (+ (find-count dir "*.clj")
               (find-count dir "*.cljc")
               (find-count dir "*.cljs"))})))

(defn run-tests [code]
  "Run the frontier actor's nbb suite. execSync returns stdout and throws
  {:status :stderr} on non-zero exit — we record the exit and a short stderr
  tail so a failing actor is reported as failing, never as absent."
  (let [dir (repo-dir code)
        entry (has-entry code)]
    (if (nil? entry)
      {:exit -1 :tail "no nbb test entry - cannot classify as green"}
      (try
        (let [out (cp/execSync "/opt/homebrew/bin/nbb"
                               (str entry)
                               #js {:cwd dir :timeout 180000})]
          {:exit 0 :tail (str/trim (str out))})
        (catch :default e
          (let [st (.-status e)
                tail (subs (or (.-stderr e) (str e)) 0 300)]
            {:exit (if st st -1) :tail tail}))))))

(defn -main []
  (let [rows (map (fn [c]
                    (let [m (or (classify-maturity c) "unmeasured")
                          lc (lang-counts c)]
                      [c m (:kotoba lc) (:clj lc)]))
                  members)
        implemented (filter (fn [[c m _x _y]] (= m "implemented")) rows)
        ;; kotoba-native = at least one .kotoba (what amu can gate).
        kt (filter (fn [[c _x k _y]] (pos? k)) rows)
        ;; migration frontier = smallest .clj* member with no .kotoba yet.
        mig (sort-by (fn [[_x _y _z c]] (if (neg? c) 100000 c))
                     (filter (fn [[c _x k cl]] (and (not (pos? k)) (pos? cl))) rows))
        frontier-row (first mig)]
    (println (str "SCANNED members=" (count rows)
                  ";implemented=" (count implemented)
                  ";kotoba-native=" (count kt)
                  ";migration-candidates=" (count mig)
                  ";frontier=" (if frontier-row (nth frontier-row 0) "none")))
    (doseq [[c m k cl] rows]
      (println (str "member\t" c "\t" m
                    "\tkotoba=" k "\tclj=" cl)))
    (when frontier-row
      (println (str "NEXT-ACTION-RECOMMENDED " (nth frontier-row 0)
                    " — smallest remaining JVM actor (clj=" (nth frontier-row 3)
                    "); migrate its smallest vertical slice to .kotoba and gate with amu check --jvm-free per SOUL.md")))))

(-main)