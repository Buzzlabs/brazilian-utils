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

;; ============================================================================
;; Tests: find-city-by-name
;; ============================================================================

(deftest find-city-by-name-test
  (testing "finds city with exact match"
    (let [result (cities/find-city-by-name "São Paulo")]
      (is (vector? result))
      (is (some #(and (= (:state %) :SP) (= (:city %) "São Paulo")) result))))

  (testing "is case-insensitive"
    (let [result1 (cities/find-city-by-name "são paulo")
          result2 (cities/find-city-by-name "SÃO PAULO")]
      (is (seq result1))
      (is (seq result2))))

  (testing "returns empty vector for non-existent city"
    (is (= [] (cities/find-city-by-name "Cidade Inexistente"))))

  (testing "returns nil for nil input"
    (is (nil? (cities/find-city-by-name nil))))

  (testing "returns nil for non-string input"
    (is (nil? (cities/find-city-by-name 123)))))

;; ============================================================================
;; Tests: city-exists?
;; ============================================================================

(deftest city-exists?-test
  (testing "returns true for existing city in state"
    (is (true? (cities/city-exists? :SP "São Paulo")))
    (is (true? (cities/city-exists? :RJ "Rio de Janeiro"))))

  (testing "is case-insensitive"
    (is (true? (cities/city-exists? :SP "são paulo")))
    (is (true? (cities/city-exists? :SP "SÃO PAULO"))))

  (testing "returns false for city in wrong state"
    (is (false? (cities/city-exists? :SP "Rio de Janeiro")))
    (is (false? (cities/city-exists? :RJ "São Paulo"))))

  (testing "returns false for non-existent city"
    (is (false? (cities/city-exists? :SP "Cidade Inexistente"))))

  (testing "returns nil for invalid state"
    (is (nil? (cities/city-exists? :XX "São Paulo"))))

  (testing "returns nil for nil inputs"
    (is (nil? (cities/city-exists? nil "São Paulo")))
    (is (nil? (cities/city-exists? :SP nil)))))
