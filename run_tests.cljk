(require '[clojure.test :as t])

(doseq [ns-sym '[isco.murakumo-test isco.seed-test isco.contract-test]]
  (require ns-sym))

(let [result (apply t/run-tests '[isco.murakumo-test isco.seed-test isco.contract-test])]
  (System/exit (if (zero? (+ (:fail result) (:error result))) 0 1)))
