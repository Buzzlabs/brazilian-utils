(ns brazilian-utils.titulo-eleitoral-test
  "Comprehensive tests for Brazilian Título Eleitoral module."
  (:require
   #?(:clj  [clojure.test :refer [deftest is testing]]
      :cljs [cljs.test :refer-macros [deftest is testing]])
   [brazilian-utils.titulo-eleitoral :as titulo-eleitoral]))

;; ============================================================================
;; Test Data
;; ============================================================================

;; Valid voter IDs with correct check digits
(def valid-voter-ids
  ["123145922717"
   "968896730748"
   "337540998708"])  ; generated valid examples

;; Invalid voter IDs
(def invalid-voter-ids
  ["123456789034"    ; Wrong check digits
   "123456789999"    ; Invalid UF code (99)
   "000000000000"    ; All zeros
   "111111111111"    ; All same digit
   "12345678903"     ; Too short
   "1234567890355"   ; Too long
   ""])

;; ============================================================================
;; Tests: is-valid?
;; ============================================================================

(deftest is-valid?-test
  (testing "returns true for valid voter IDs"
    (doseq [voter-id valid-voter-ids]
      (is (true? (titulo-eleitoral/is-valid? voter-id))
          (str "Expected " voter-id " to be valid"))))

  (testing "returns false for invalid voter IDs"
    (doseq [voter-id invalid-voter-ids]
      (is (false? (titulo-eleitoral/is-valid? voter-id))
          (str "Expected " voter-id " to be invalid"))))

  (testing "returns false for nil"
    (is (false? (titulo-eleitoral/is-valid? nil))))

  (testing "returns false for non-string types"
    (is (false? (titulo-eleitoral/is-valid? 123456789035)))
    (is (false? (titulo-eleitoral/is-valid? true)))
    (is (false? (titulo-eleitoral/is-valid? {})))
    (is (false? (titulo-eleitoral/is-valid? []))))

  (testing "handles formatted voter IDs"
    (is (true? (titulo-eleitoral/is-valid? "1231 4592 2717"))))

  (testing "rejects invalid UF codes"
    (is (false? (titulo-eleitoral/is-valid? "123456789099"))) ; UF 99 invalid
    (is (false? (titulo-eleitoral/is-valid? "123456789030"))) ; UF 30 invalid
    (is (false? (titulo-eleitoral/is-valid? "123456789000"))) ; UF 00 invalid
    (is (false? (titulo-eleitoral/is-valid? "123456789054")))) ; UF 54 invalid

  (testing "rejects all same digits"
    (is (false? (titulo-eleitoral/is-valid? "000000000000")))
    (is (false? (titulo-eleitoral/is-valid? "111111111111")))
    (is (false? (titulo-eleitoral/is-valid? "999999999999")))))

;; ============================================================================
;; Tests: validation-errors
;; ============================================================================

(deftest validation-errors-test
  (testing "returns empty vector for valid voter IDs"
    (doseq [voter-id valid-voter-ids]
      (is (= [] (titulo-eleitoral/validation-errors voter-id))
          (str "Expected no errors for " voter-id))))

  (testing "returns error messages for invalid voter IDs"
    (let [errors (titulo-eleitoral/validation-errors "")]
      (is (vector? errors))
      (is (pos? (count errors))))

    (let [errors (titulo-eleitoral/validation-errors "123456789099")]
      (is (some #(re-find #"UF code" %) errors)))

    (let [errors (titulo-eleitoral/validation-errors "000000000000")]
      (is (some #(re-find #"same digit" %) errors))))

  (testing "handles nil gracefully"
    (let [errors (titulo-eleitoral/validation-errors nil)]
      (is (vector? errors))
      (is (pos? (count errors))))))

;; ============================================================================
;; Tests: remove-symbols
;; ============================================================================

(deftest remove-symbols-test
  (testing "removes spaces and special characters"
    (is (= "123456789035" (titulo-eleitoral/remove-symbols "1234 5678 9035")))
    (is (= "123456789035" (titulo-eleitoral/remove-symbols "1234-5678-9035")))
    (is (= "123456789035" (titulo-eleitoral/remove-symbols "1234.5678.9035"))))

  (testing "keeps only numbers"
    (is (= "123456789035" (titulo-eleitoral/remove-symbols "abc1234def5678ghi9035"))))

  (testing "handles already clean voter IDs"
    (is (= "123456789035" (titulo-eleitoral/remove-symbols "123456789035"))))

  (testing "returns empty string for nil"
    (is (= "" (titulo-eleitoral/remove-symbols nil))))

  (testing "returns empty string for non-numeric input"
    (is (= "" (titulo-eleitoral/remove-symbols "abcdefghijkl")))))

;; ============================================================================
;; Tests: get-uf-code
;; ============================================================================

(deftest get-uf-code-test
  (testing "extracts UF code from voter ID"
    (is (= "35" (titulo-eleitoral/get-uf-code "123456789035"))) ; SP
    (is (= "01" (titulo-eleitoral/get-uf-code "123456789001"))) ; Valid code
    (is (= "53" (titulo-eleitoral/get-uf-code "123456789053")))) ; DF

  (testing "handles formatted voter IDs"
    (is (= "35" (titulo-eleitoral/get-uf-code "1234 5678 9035"))))

  (testing "returns nil for invalid input"
    (is (nil? (titulo-eleitoral/get-uf-code "")))
    (is (nil? (titulo-eleitoral/get-uf-code "123")))
    (is (nil? (titulo-eleitoral/get-uf-code nil))))

  (testing "returns nil for non-string input"
    (is (nil? (titulo-eleitoral/get-uf-code 123456789035)))))

;; ============================================================================
;; Tests: Check Digit Calculation
;; ============================================================================

(deftest check-digit-calculation-test
  (testing "validates correct check digits"
    ;; Using known valid examples
    (is (true? (titulo-eleitoral/is-valid? "123145922717"))))

  (testing "rejects incorrect check digits"
    (is (false? (titulo-eleitoral/is-valid? "123145922700"))) ; Wrong DVs
    (is (false? (titulo-eleitoral/is-valid? "123145922718")))))

;; ============================================================================
;; Tests: generate
;; ============================================================================

(deftest generate-test
  (testing "generates valid voter IDs"
    (let [generated (titulo-eleitoral/generate)]
      (is (string? generated))
      (is (= 12 (count generated)))
      (is (titulo-eleitoral/is-valid? generated))))

  (testing "respects provided UF code"
    (let [generated (titulo-eleitoral/generate {:uf-code 35})]
      (is (titulo-eleitoral/is-valid? generated))
      (is (= "35" (titulo-eleitoral/get-uf-code generated)))))

  (testing "returns nil for invalid UF code"
    (is (nil? (titulo-eleitoral/generate {:uf-code 99})))))
