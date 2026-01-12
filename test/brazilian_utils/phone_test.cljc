(ns brazilian-utils.phone-test
  "Tests for the phone validation module."
  (:require
   [clojure.test :refer [deftest is testing]]
   [brazilian-utils.phone :as phone]))


;; ============================================================================
;; Invalid Phone Tests
;; ============================================================================

(deftest is-valid?-test-invalid-inputs
  (testing "returns false for invalid input types"
    ;; Empty string
    (is (false? (phone/is-valid? "")))
    (is (false? (phone/is-valid-mobile? "")))
    (is (false? (phone/is-valid-landline? "")))
    
    ;; Null/nil
    (is (false? (phone/is-valid? nil)))
    (is (false? (phone/is-valid-mobile? nil)))
    (is (false? (phone/is-valid-landline? nil)))
    
    ;; Booleans
    (is (false? (phone/is-valid? true)))
    (is (false? (phone/is-valid? false)))
    (is (false? (phone/is-valid-mobile? false)))
    (is (false? (phone/is-valid-landline? true)))
    (is (false? (phone/is-valid-mobile? false)))
    (is (false? (phone/is-valid-landline? true)))
    
    ;; Objects and arrays
    (is (false? (phone/is-valid? {})))
    (is (false? (phone/is-valid-mobile? {})))
    (is (false? (phone/is-valid-landline? {})))
    
    (is (false? (phone/is-valid? [])))
    (is (false? (phone/is-valid-mobile? [])))
    (is (false? (phone/is-valid-landline? [])))))

(deftest is-valid?-test-invalid-formats
  (testing "returns false for invalid phone formats"
    ;; Mobile phone with mask and invalid area code
    (is (false? (phone/is-valid? "(00) 3 0000-0000")))
    (is (false? (phone/is-valid-mobile? "(00) 3 0000-0000")))
    
    ;; Landline with mask and invalid area code  
    (is (false? (phone/is-valid? "(11) 9000-0000")))
    (is (false? (phone/is-valid-landline? "(11) 9000-0000")))
    
    ;; Mobile phone invalid with mask (wrong first digit)
    (is (false? (phone/is-valid? "(11) 3 0000-0000")))
    (is (false? (phone/is-valid-mobile? "(11) 3 0000-0000")))
    
    ;; Landline invalid with mask (wrong first digit)
    (is (false? (phone/is-valid? "(11) 9000-0000")))
    (is (false? (phone/is-valid-landline? "(11) 9000-0000")))))

(deftest is-valid?-test-invalid-lengths
  (testing "returns false for invalid phone lengths"
    ;; Too short (less than min length)
    (is (false? (phone/is-valid? "11")))
    (is (false? (phone/is-valid-mobile? "11")))
    (is (false? (phone/is-valid-landline? "11")))
    
    ;; Too long (more than max length) 
    (is (false? (phone/is-valid? "11300000001130000000")))
    (is (false? (phone/is-valid-mobile? "11300000001130000000")))
    (is (false? (phone/is-valid-landline? "11300000001130000000")))))

;; ============================================================================
;; Valid Phone Tests  
;; ============================================================================

(deftest is-valid?-test-valid-area-codes
  (testing "returns true when DDD is valid"
    ;; Test a few key area codes with mobile format
    (is (true? (phone/is-valid? "(11) 9 0000-0000"))) ; São Paulo
    (is (true? (phone/is-valid? "(21) 9 0000-0000"))) ; Rio de Janeiro
    (is (true? (phone/is-valid? "(31) 9 0000-0000"))) ; Minas Gerais
    (is (true? (phone/is-valid? "(41) 9 0000-0000"))) ; Paraná
    (is (true? (phone/is-valid? "(51) 9 0000-0000"))) ; Rio Grande do Sul
    (is (true? (phone/is-valid? "(61) 9 0000-0000"))) ; Distrito Federal
    (is (true? (phone/is-valid? "(71) 9 0000-0000"))) ; Bahia
    (is (true? (phone/is-valid? "(81) 9 0000-0000"))) ; Pernambuco
    (is (true? (phone/is-valid? "(85) 9 0000-0000"))) ; Ceará
    (is (true? (phone/is-valid? "(91) 9 0000-0000"))))) ; Pará

(deftest is-valid?-test-valid-mobile-phones
  (testing "returns true for valid mobile phones"
    ;; Mobile with mask
    (is (true? (phone/is-valid? "(11) 9 0000-0000")))
    (is (true? (phone/is-valid-mobile? "(11) 9 0000-0000")))
    
    ;; Mobile without mask
    (is (true? (phone/is-valid? "11900000000")))
    (is (true? (phone/is-valid-mobile? "11900000000")))
    
    ;; Test different valid first digits for mobile (6,7,8,9)
    (is (true? (phone/is-valid-mobile? "11600000000")))
    (is (true? (phone/is-valid-mobile? "11700000000")))
    (is (true? (phone/is-valid-mobile? "11800000000")))
    (is (true? (phone/is-valid-mobile? "11900000000")))))

(deftest is-valid?-test-valid-landline-phones
  (testing "returns true for valid landline phones"
    ;; Landline with mask
    (is (true? (phone/is-valid? "(11) 3000-0000")))
    (is (true? (phone/is-valid-landline? "(11) 3000-0000")))
    
    ;; Landline without mask
    (is (true? (phone/is-valid? "1130000000")))
    (is (true? (phone/is-valid-landline? "1130000000")))
    
    ;; Test different valid first digits for landline (2,3,4,5)
    (is (true? (phone/is-valid-landline? "1120000000")))
    (is (true? (phone/is-valid-landline? "1130000000")))
    (is (true? (phone/is-valid-landline? "1140000000")))
    (is (true? (phone/is-valid-landline? "1150000000")))))

;; ============================================================================
;; Validation Errors Tests
;; ============================================================================

(deftest validation-errors-test
  (testing "returns empty vector for valid phones"
    (is (= [] (phone/validation-errors "(11) 9 0000-0000")))
    (is (= [] (phone/validation-errors "(11) 3000-0000")))
    (is (= [] (phone/mobile-validation-errors "11900000000")))
    (is (= [] (phone/landline-validation-errors "1130000000"))))

  (testing "returns vector of error messages for invalid phones"
    ;; Test blank phone
    (let [result (phone/validation-errors "")]
      (is (vector? result))
      (is (seq result))
      (is (every? string? result)))
    
    ;; Test nil
    (let [result (phone/validation-errors nil)]
      (is (vector? result))
      (is (seq result))
      (is (every? string? result)))
    
    ;; Test format error
    (let [result (phone/validation-errors "123")]
      (is (vector? result))
      (is (seq result))
      (is (every? string? result)))
    
    ;; Test invalid area code
    (let [result (phone/validation-errors "00900000000")]
      (is (vector? result))
      (is (seq result))
      (is (every? string? result))))

  (testing "error messages are informative strings"
    (let [result (phone/validation-errors "")]
      (is (every? #(and (string? %) (seq %)) result)))))