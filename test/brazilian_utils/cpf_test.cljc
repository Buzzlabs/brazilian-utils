(ns brazilian-utils.cpf-test
  (:require #?(:clj [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros [deftest is testing]])
            [brazilian-utils.cpf :as cpf]
            [brazilian-utils.states :as states]))

(def cpf-length 11)

;; ============================================================================
;; Test clean
;; ============================================================================

(deftest test-clean
  (testing "should remove all non-numeric characters"
    (is (= "12345678909" (cpf/clean "123.456.789-09")))
    (is (= "12345678909" (cpf/clean "123-456-789-09")))
    (is (= "12345678909" (cpf/clean "123 456 789 09")))
    (is (= "12345678909" (cpf/clean "abc123def456ghi789jkl09")))
    (is (= "12345678909" (cpf/clean "!@#123$%^456&*(789)09"))))

  (testing "should return empty string for nil or empty input"
    (is (= "" (cpf/clean nil)))
    (is (= "" (cpf/clean ""))))

  (testing "should handle already clean CPF"
    (is (= "12345678909" (cpf/clean "12345678909"))))

  (testing "should handle numbers"
    (is (= "12345678909" (cpf/clean 12345678909))))

  (testing "should extract only digits from mixed content"
    (is (= "12345678909" (cpf/clean "CPF: 123.456.789-09")))
    (is (= "12345678909" (cpf/clean "foo123.456.789-09bar")))
    (is (= "00000000000" (cpf/clean "000.000.000-00")))))

;; ============================================================================
;; Test format-cpf
;; ============================================================================

(deftest test-format-cpf
  (testing "should format CPF with mask"
    (is (= "" (cpf/format-cpf "")))
    (is (= "9" (cpf/format-cpf "9")))
    (is (= "94" (cpf/format-cpf "94")))
    (is (= "943" (cpf/format-cpf "943")))
    (is (= "943.8" (cpf/format-cpf "9438")))
    (is (= "943.89" (cpf/format-cpf "94389")))
    (is (= "943.895" (cpf/format-cpf "943895")))
    (is (= "943.895.7" (cpf/format-cpf "9438957")))
    (is (= "943.895.75" (cpf/format-cpf "94389575")))
    (is (= "943.895.751" (cpf/format-cpf "943895751")))
    (is (= "943.895.751-0" (cpf/format-cpf "9438957510")))
    (is (= "943.895.751-04" (cpf/format-cpf "94389575104"))))

  (testing "should format number CPF with mask"
    (is (= "9" (cpf/format-cpf 9)))
    (is (= "94" (cpf/format-cpf 94)))
    (is (= "943" (cpf/format-cpf 943)))
    (is (= "943.8" (cpf/format-cpf 9438)))
    (is (= "943.89" (cpf/format-cpf 94389)))
    (is (= "943.895" (cpf/format-cpf 943895)))
    (is (= "943.895.7" (cpf/format-cpf 9438957)))
    (is (= "943.895.75" (cpf/format-cpf 94389575)))
    (is (= "943.895.751" (cpf/format-cpf 943895751)))
    (is (= "943.895.751-0" (cpf/format-cpf 9438957510)))
    (is (= "943.895.751-04" (cpf/format-cpf 94389575104))))

  (testing "should format CPF with mask filling zeroes"
    (is (= "000.000.000-00" (cpf/format-cpf "" {:pad true})))
    (is (= "000.000.000-09" (cpf/format-cpf "9" {:pad true})))
    (is (= "000.000.000-94" (cpf/format-cpf "94" {:pad true})))
    (is (= "000.000.009-43" (cpf/format-cpf "943" {:pad true})))
    (is (= "000.000.094-38" (cpf/format-cpf "9438" {:pad true})))
    (is (= "000.000.943-89" (cpf/format-cpf "94389" {:pad true})))
    (is (= "000.009.438-95" (cpf/format-cpf "943895" {:pad true})))
    (is (= "000.094.389-57" (cpf/format-cpf "9438957" {:pad true})))
    (is (= "000.943.895-75" (cpf/format-cpf "94389575" {:pad true})))
    (is (= "009.438.957-51" (cpf/format-cpf "943895751" {:pad true})))
    (is (= "094.389.575-10" (cpf/format-cpf "9438957510" {:pad true})))
    (is (= "943.895.751-04" (cpf/format-cpf "94389575104" {:pad true}))))

  (testing "should format number CPF with mask filling zeroes"
    (is (= "000.000.000-09" (cpf/format-cpf 9 {:pad true})))
    (is (= "000.000.000-94" (cpf/format-cpf 94 {:pad true})))
    (is (= "000.000.009-43" (cpf/format-cpf 943 {:pad true})))
    (is (= "000.000.094-38" (cpf/format-cpf 9438 {:pad true})))
    (is (= "000.000.943-89" (cpf/format-cpf 94389 {:pad true})))
    (is (= "000.009.438-95" (cpf/format-cpf 943895 {:pad true})))
    (is (= "000.094.389-57" (cpf/format-cpf 9438957 {:pad true})))
    (is (= "000.943.895-75" (cpf/format-cpf 94389575 {:pad true})))
    (is (= "009.438.957-51" (cpf/format-cpf 943895751 {:pad true})))
    (is (= "094.389.575-10" (cpf/format-cpf 9438957510 {:pad true})))
    (is (= "943.895.751-04" (cpf/format-cpf 94389575104 {:pad true}))))

  (testing "should NOT add digits after CPF length"
    (is (= "943.895.751-04" (cpf/format-cpf "94389575104000000"))))

  (testing "should remove all non-numeric characters"
    (is (= "943.895.751-04" (cpf/format-cpf "943.?ABC895.751-04abc")))))

;; ============================================================================
;; Test generate
;; ============================================================================

(deftest test-generate
  (testing "should have the right length without mask"
    (is (= cpf-length (count (cpf/generate)))))

  (testing "should return valid CPF"
    ;; iterate 100 times to ensure random generated CPF is valid
    (dotimes [_ 100]
      (is (true? (cpf/is-valid? (cpf/generate))))))

  (testing "should return a valid CPF for each Brazilian state"
    (doseq [state (states/all-ufs)]
      (let [generated (cpf/generate state)
            state-digit (subs generated 8 9)
            expected-code (states/uf->code state)]
        (is (= state-digit expected-code)
            (str "CPF for " (name state) " should have digit " expected-code " at position 8"))))))

;; ============================================================================
;; Test is-valid?
;; ============================================================================

(deftest test-is-valid?
  (testing "returns false"
    (testing "when it is an empty string"
      (is (false? (cpf/is-valid? ""))))

    (testing "when it is nil"
      (is (false? (cpf/is-valid? nil))))

    (testing "when it is a boolean"
      (is (false? (cpf/is-valid? true)))
      (is (false? (cpf/is-valid? false))))

    (testing "when it is an object"
      (is (false? (cpf/is-valid? {}))))

    (testing "when it is an array"
      (is (false? (cpf/is-valid? []))))

    (testing "when it doesn't match CPF length"
      (is (false? (cpf/is-valid? "123456"))))

    (testing "when contains only letters or special characters"
      (is (false? (cpf/is-valid? "abcabcabcde"))))

    (testing "when is an invalid CPF"
      (is (false? (cpf/is-valid? "11257245286"))))

    (testing "when is an invalid CPF with letters"
      (is (false? (cpf/is-valid? "foo391.838.38test0-66")))))

  (testing "returns true"
    (testing "when is a valid CPF without mask"
      (is (true? (cpf/is-valid? "40364478829"))))

    (testing "when is a valid CPF with mask"
      (is (true? (cpf/is-valid? "962.718.458-60"))))))