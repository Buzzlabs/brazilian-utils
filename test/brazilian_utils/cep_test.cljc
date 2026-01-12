(ns brazilian-utils.cep-test
  "Tests for the CEP (postal code) module."
  (:require
   [clojure.test :refer [deftest is testing]]
   [brazilian-utils.cep :as cep]))

;; ============================================================================
;; Tests: is-valid?
;; ============================================================================

(deftest is-valid?-test
  (testing "returns true for valid CEPs without formatting"
    (is (true? (cep/is-valid? "01310100")))
    (is (true? (cep/is-valid? "12345678")))
    (is (true? (cep/is-valid? "98765432"))))

  (testing "returns true for valid CEPs with formatting"
    (is (true? (cep/is-valid? "01310-100")))
    (is (true? (cep/is-valid? "12345-678")))
    (is (true? (cep/is-valid? "98765-432"))))

  (testing "returns false for CEPs with repeated digits"
    (is (false? (cep/is-valid? "00000000")))   ;; all zeros
    (is (false? (cep/is-valid? "11111111")))   ;; all ones
    (is (false? (cep/is-valid? "22222222")))   ;; all twos
    (is (false? (cep/is-valid? "99999999")))   ;; all nines
    (is (false? (cep/is-valid? "00000-000")))  ;; formatted repeated
    (is (false? (cep/is-valid? "11111-111")))  ;; formatted repeated
    (is (false? (cep/is-valid? "55555-555")))) ;; formatted repeated

  (testing "returns false for invalid CEPs"
    (is (false? (cep/is-valid? "0131010")))    ;; 7 digits
    (is (false? (cep/is-valid? "013101000")))  ;; 9 digits
    (is (false? (cep/is-valid? "01310-10")))   ;; incomplete formatted
    (is (false? (cep/is-valid? "abcdefgh")))   ;; non-numeric
    (is (false? (cep/is-valid? "")))           ;; empty
    (is (false? (cep/is-valid? nil)))          ;; nil
    (is (false? (cep/is-valid? "01310 100")))) ;; spaces (invalid format)

  (testing "handles mixed characters by rejecting them"
    (is (false? (cep/is-valid? "CEP: 01310-100")))) ;; mixed chars invalid

  (testing "handles different types"
    (is (false? (cep/is-valid? 123)))
    (is (false? (cep/is-valid? [])))
    (is (false? (cep/is-valid? {})))))

;; ============================================================================
;; Tests: format-cep
;; ============================================================================

(deftest format-cep-test
  (testing "formats valid CEP correctly"
    (is (= "01310-100" (cep/format-cep "01310100")))
    (is (= "12345-678" (cep/format-cep "12345678")))
    (is (= "00000-000" (cep/format-cep "00000000"))))

  (testing "formats CEP that already has formatting"
    (is (= "01310-100" (cep/format-cep "01310-100")))
    (is (= "12345-678" (cep/format-cep "12345-678"))))

  (testing "handles partial input"
    (is (= "01310-1" (cep/format-cep "013101")))
    (is (= "013" (cep/format-cep "013")))
    (is (= "01310" (cep/format-cep "01310"))))

  (testing "removes non-numeric characters"
    (is (= "01310-100" (cep/format-cep "01310.100")))
    (is (= "01310-100" (cep/format-cep "01310 100")))
    (is (= "01310-100" (cep/format-cep "CEP: 01310-100"))))

  (testing "handles empty input"
    (is (= "" (cep/format-cep ""))))

  (testing "handles nil by converting to empty string"
    (is (= "" (cep/format-cep nil))))

  (testing "truncates to 8 digits"
    (is (= "01310-100" (cep/format-cep "0131010099999")))))