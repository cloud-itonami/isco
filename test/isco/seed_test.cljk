(ns isco.seed-test
  (:require [clojure.edn :as edn]
            [clojure.test :refer [deftest is testing]]
            [isco.tools.validate :as validate]))

(def seed-path "data/isco-occupations.edn")
(def document (edn/read-string (slurp seed-path)))
(def result (validate/validation-result document))

(deftest complete-isco-08-seed
  (is (:valid? result) (:checks result))
  (is (= 619 (:count result)))
  (is (= {:major 10 :sub-major 43 :minor 130 :unit 436} (:levels result))))

(deftest every-seed-invariant-is-explicitly-green
  (doseq [[check passed?] (:checks result)]
    (testing (name check) (is (true? passed?)))))

(deftest open-isco-sample-is-canonical-edn
  (let [sample (edn/read-string (slurp "open-isco-sample.edn"))]
    (is (sequential? sample))
    (is (seq sample))))
