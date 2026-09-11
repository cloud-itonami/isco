(ns isco.mesh-manifest-test
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.test :refer [deftest is testing]]))

(deftest mesh-manifest-points-to-present-component
  (let [manifest (edn/read-string (slurp (io/file "kotoba.app.edn")))
        component (first (:kotoba.app/components manifest))]
    (testing "independent repo owns the mesh entry point"
      (is (= "isco" (:kotoba.app/name manifest)))
      (is (= "methods/mesh.clj" (:src component)))
      (is (.isFile (io/file (:src component)))))
    (testing "deployment contract remains the observatory KSE surface"
      (is (= #{:cap/kqe} (:requires component)))
      (is (= [{:type :kse :topic "etzhayyim/actor/isco"}]
             (:triggers component))))))
