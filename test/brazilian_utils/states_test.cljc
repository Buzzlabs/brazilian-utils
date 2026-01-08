(ns brazilian-utils.states-test
  "Comprehensive tests for the Brazilian states module."
  (:require
   [clojure.test :refer [deftest is testing]]
   [brazilian-utils.states :as states]
   [brazilian-utils.data :as data]))

;; ============================================================================
;; Tests: valid-uf?
;; ============================================================================

(deftest valid-uf?-test
  (testing "returns true for existing states by UF keyword"
    (is (true? (states/valid-uf? :SP)))
    (is (true? (states/valid-uf? :RJ)))
    (is (true? (states/valid-uf? :AC)))
    (is (true? (states/valid-uf? :TO))))

  (testing "returns false for non-existing states"
    (is (false? (states/valid-uf? :XX)))
    (is (false? (states/valid-uf? :ZZ)))
    (is (false? (states/valid-uf? nil))))

  (testing "returns false for invalid types"
    (is (false? (states/valid-uf? "SP")))
    (is (false? (states/valid-uf? :sp)))
    (is (false? (states/valid-uf? 123)))
    (is (false? (states/valid-uf? [])))
    (is (false? (states/valid-uf? {}))))

  (testing "handles all 27 brazilian states by UF"
    (doseq [uf [:AC :AL :AP :AM :BA :CE :DF :ES :GO :MA :MG :MT :MS
                :PA :PB :PE :PI :PR :RJ :RN :RO :RS :RR :SC :SE :SP :TO]]
      (is (true? (states/valid-uf? uf))))))

;; ============================================================================
;; Tests: uf->state-name
;; ============================================================================

(deftest uf->state-name-test
  (testing "returns the correct state name"
    (is (= "São Paulo" (states/uf->state-name :SP)))
    (is (= "Rio de Janeiro" (states/uf->state-name :RJ)))
    (is (= "Minas Gerais" (states/uf->state-name :MG))))

  (testing "returns nil for invalid or unknown states"
    (is (nil? (states/uf->state-name :XX)))
    (is (nil? (states/uf->state-name nil))))

  (testing "all states return non-nil names"
    (doseq [uf (keys data/states-map)]
      (is (some? (states/uf->state-name uf))))))

;; ============================================================================
;; Tests: uf->ie-length
;; ============================================================================

(deftest uf->ie-length-test
  (testing "returns correct IE length for states"
    (let [result (states/uf->ie-length :RN)]
      (is (or (integer? result) (vector? result)))
      (is (or (= 9 result) (= [9 10] result)))))


  (testing "returns vector for states with multiple IE lengths"
    (is (= [8 9] (states/uf->ie-length :BA)))
    (is (= [9 10] (states/uf->ie-length :RN)))
    (is (= [9 11] (states/uf->ie-length :TO))))

  (testing "returns nil for invalid states"
    (is (nil? (states/uf->ie-length :XX)))
    (is (nil? (states/uf->ie-length nil)))))

;; ============================================================================
;; Tests: uf->area-codes
;; ============================================================================

(deftest uf->area-codes-test
  (testing "returns area codes for state"
    (is (= [11 12 13 14 15 16 17 18 19] (states/uf->area-codes :SP)))
    (is (= [21 22 24] (states/uf->area-codes :RJ)))
    (is (= [68] (states/uf->area-codes :AC))))

  (testing "returns nil for invalid states"
    (is (nil? (states/uf->area-codes :XX)))
    (is (nil? (states/uf->area-codes nil))))

  (testing "all states have area codes"
    (doseq [uf (keys data/states-map)]
      (let [codes (states/uf->area-codes uf)]
        (is (some? codes))
        (is (coll? codes))
        (is (pos? (count codes)))))))


;; ============================================================================
;; Tests: all-ufs and all-state-names
;; ============================================================================

(deftest all-ufs-test
  (testing "returns 27 UF keywords sorted alphabetically"
    (let [ufs (states/all-ufs)]
      (is (= 27 (count ufs)))
      (is (every? keyword? ufs))
      (is (= ufs (sort ufs))))))

(deftest all-state-names-test
  (testing "returns 27 state names sorted alphabetically"
    (let [names (states/all-state-names)
          expected (->> data/states-map vals (map :name) sort vec)]
      (is (= 27 (count names)))
      (is (every? string? names))
      (is (= expected names)))))

;; ============================================================================
;; Tests: Data Consistency
;; ============================================================================

(deftest data-consistency-test
  (testing "states-map has 27 entries"
    (is (= 27 (count data/states-map))))

  (testing "all states have required fields"
    (doseq [[code state] data/states-map]
      (is (keyword? code))
      (is (string? (:code state)) (str "Missing :code for " code))
      (is (string? (:name state)) (str "Missing :name for " code))
      (is (vector? (:area-codes state)) (str "Missing :area-codes for " code))
      (is (some? (:ie-length state)) (str "Missing :ie-length for " code))))

  (testing "area codes are unique across states"
    (let [all-area-codes (mapcat :area-codes (vals data/states-map))
          unique-count (count (set all-area-codes))]
      (is (= (count all-area-codes) unique-count))))

  (testing "all state names are unique"
    (let [names (map :name (vals data/states-map))
          unique-count (count (set names))]
      (is (= (count names) unique-count)))))

;; ============================================================================
;; Tests: Edge Cases
;; ============================================================================

(deftest edge-cases-test
  (testing "valid-uf? handles keyword correctly"
    (is (true? (states/valid-uf? :SP)))
    (is (false? (states/valid-uf? "SP")))
    (is (false? (states/valid-uf? :sp))))

  (testing "uf->state-name function handles different input types"
    (is (= "São Paulo" (states/uf->state-name :SP)))
    (is (nil? (states/uf->state-name "SP")))
    (is (nil? (states/uf->state-name :sp)))))

;; ============================================================================
;; Tests: Schema Validation
;; ============================================================================

(deftest schema-validation-test
  (testing "valid-uf? validates correctly"
    (is (true? (states/valid-uf? :SP)))
    (is (false? (states/valid-uf? :XX)))
    (is (false? (states/valid-uf? "SP")))
    (is (false? (states/valid-uf? :sp)))
    (is (false? (states/valid-uf? nil))))

  (testing "uf->state-name returns nil for invalid state"
    (is (nil? (states/uf->state-name :XX)))
    (is (nil? (states/uf->state-name "SP")))
    (is (nil? (states/uf->state-name :sp)))
    (is (nil? (states/uf->state-name nil))))

  (testing "uf->area-codes returns nil for invalid state"
    (is (nil? (states/uf->area-codes :XX)))
    (is (nil? (states/uf->area-codes "SP")))
    (is (nil? (states/uf->area-codes nil))))

  (testing "uf->ie-length returns nil for invalid state"
    (is (nil? (states/uf->ie-length :XX)))
    (is (nil? (states/uf->ie-length "SP")))
    (is (nil? (states/uf->ie-length nil)))))

(deftest valid-states-behavior-test
  (testing "all valid states pass validation"
    (doseq [uf [:AC :AL :AP :AM :BA :CE :DF :ES :GO :MA :MG :MT :MS
                :PA :PB :PE :PI :PR :RJ :RN :RO :RS :RR :SC :SE :SP :TO]]
      (is (some? (states/uf->state-name uf)))
      (is (some? (states/uf->area-codes uf)))
      (is (some? (states/uf->ie-length uf)))))

  (testing "all valid states return appropriate types after validation"
    (doseq [uf [:SP :RJ :MG :BA]]
      (let [codes (states/uf->area-codes uf)]
        (is (vector? codes))
        (is (every? integer? codes))))))
