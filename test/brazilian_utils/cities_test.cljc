(ns brazilian-utils.cities-test
  "Tests for the cities module."
  (:require
  #?(:clj  [clojure.test :refer [deftest is testing]]
    :cljs [cljs.test :refer-macros [deftest is testing]])
  [brazilian-utils.cities :as cities]))

;; ============================================================================
;; Tests: cities-of
;; ============================================================================

(deftest cities-of-test
  (testing "returns vector for valid state"
    (let [result (cities/cities-of :GO)]
      (is (vector? result))))

  (testing "returns empty vector for invalid state"
    (is (= [] (cities/cities-of :XX)))
    (is (= [] (cities/cities-of nil))))

  (testing "cities-of! returns vector for valid state"
    (let [result (cities/cities-of! :GO)]
      (is (vector? result))))

  (testing "cities-of! throws on invalid state"
    #?(:clj
       (try
         (cities/cities-of! :XX)
         (is false "expected ExceptionInfo for invalid state")
         (catch clojure.lang.ExceptionInfo e
           (is (= "Invalid state" (.getMessage e)))
           (is (= {:state :XX} (ex-data e)))))

       :cljs
       (try
         (cities/cities-of! :XX)
         (is false "expected ExceptionInfo for invalid state")
         (catch js/Error e
           (is (= "Invalid state" (.-message e)))
           (is (= {:state :XX} (ex-data e))))))))

;; ============================================================================
;; Tests: all-cities
;; ============================================================================

(deftest all-cities-test
  (testing "returns vector of all cities with state context"
    (let [result (cities/all-cities)]
      (is (vector? result))
      (is (pos? (count result)))
      (is (every? #(and (contains? % :state) (contains? % :city)) result))))

  (testing "returns flat vector of names from all-city-names"
    (let [result (cities/all-city-names)]
      (is (vector? result))
      (is (pos? (count result)))
      (is (every? string? result)))))

;; ============================================================================
;; Tests: Schema Validation
;; ============================================================================

(deftest schema-validation-cities-test
  ;; valid-state? is internal; behavior validated via cities-of tests

  (testing "all valid states return non-empty vectors or empty for no cities"
    (doseq [uf [:AC :AL :AP :AM :BA :CE :DF :ES :GO :MA :MG :MT :MS
                :PA :PB :PE :PI :PR :RJ :RN :RO :RS :RR :SC :SE :SP :TO]]
      (let [result (cities/cities-of uf)]
        (is (vector? result))
        (when (seq result)
          (is (every? string? result)))))))
