(ns normalize.core-test
  (:require [clojure.test :refer :all]
            [normalize.core :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))

(def runs 100)

(defn longest-match [s chr]
   (or (some->> (re-seq (re-pattern (str chr "+")) s)
              (map count)
              (apply max)) 0))

(defspec  contains-no-norwegian runs
  (prop/for-all [v gen/string]
                (and (= (.indexOf (normalize v) "Å") -1)
                     (= (.indexOf (normalize v) "å") -1)
                     (= (.indexOf (normalize v) "Æ") -1)
                     (= (.indexOf (normalize v) "æ") -1)
                     (= (.indexOf (normalize v) "Ø") -1)
                     (= (.indexOf (normalize v) "ø") -1))))

(defspec is-lower-case runs
  (prop/for-all [v gen/string]
                (= (.toLowerCase (normalize v)) (normalize v))))

(defspec only-one-dash runs
  (prop/for-all [v gen/string]
                (< (longest-match (normalize v) \-) 2)))

(defspec doesnt-end-with-dash runs
  (prop/for-all [v gen/string]
                (not (.endsWith (normalize v) "-"))))

(defspec doesnt-start-with-dash runs
  (prop/for-all [v gen/string]
                (not (.startsWith (normalize v) "-"))))

