(ns brazilian-utils.processo-juridico-test
  "Tests for the Processo Jurídico module (Brazilian court case)."
  (:require
   [clojure.test :refer [deftest is testing]]
   [brazilian-utils.processo-juridico :as pj]))

;; ============================================================================
;; Tests: format-processo
;; ============================================================================

(deftest format-processo-test
  (testing "should format processo juridico with mask incrementally"
    (is (= "" (pj/format-processo "")))
    (is (= "0" (pj/format-processo "0")))
    (is (= "00" (pj/format-processo "00")))
    (is (= "000" (pj/format-processo "000")))
    (is (= "0002" (pj/format-processo "0002")))
    (is (= "00020" (pj/format-processo "00020")))
    (is (= "000208" (pj/format-processo "000208")))
    (is (= "0002080" (pj/format-processo "0002080")))
    (is (= "0002080-2" (pj/format-processo "00020802")))
    (is (= "0002080-25" (pj/format-processo "000208025")))
    (is (= "0002080-25.2" (pj/format-processo "0002080252")))
    (is (= "0002080-25.20" (pj/format-processo "00020802520")))
    (is (= "0002080-25.201" (pj/format-processo "000208025201")))
    (is (= "0002080-25.2012" (pj/format-processo "0002080252012")))
    (is (= "0002080-25.2012.5" (pj/format-processo "00020802520125")))
    (is (= "0002080-25.2012.51" (pj/format-processo "000208025201251")))
    (is (= "0002080-25.2012.515" (pj/format-processo "0002080252012515")))
    (is (= "0002080-25.2012.515.0" (pj/format-processo "00020802520125150")))
    (is (= "0002080-25.2012.515.00" (pj/format-processo "000208025201251500")))
    (is (= "0002080-25.2012.515.004" (pj/format-processo "0002080252012515004")))
    (is (= "0002080-25.2012.515.0049" (pj/format-processo "00020802520125150049"))))

  (testing "should NOT add digits after processo juridico length"
    (is (= "0002080-25.2012.515.0049" (pj/format-processo "00020802520125150049123123"))))

  (testing "should remove all non numeric characters"
    (is (= "0002080-25.2012.515.0049" (pj/format-processo "0002080@$25201%!@2515.%0049123123")))))

;; ============================================================================
;; Tests: is-valid?
;; ============================================================================

(deftest is-valid?-test
  (testing "should return true for valid Processo Jurídico"
    (is (true? (pj/is-valid? "00020802520125150049")))
    (is (true? (pj/is-valid? "00020854720125150049")))
    (is (true? (pj/is-valid? "0002080-25.2012.515.0049"))))

  (testing "should return false for invalid Processo Jurídico"
    (is (false? (pj/is-valid? "00020854220125150049"))))

  (testing "should return false for incorrect length"
    (is (false? (pj/is-valid? "00020854220125150049123123")))
    (is (false? (pj/is-valid? "123123"))))

  (testing "should return false for non-numeric content"
    (is (false? (pj/is-valid? "abcd123qweasdsdasdds"))))

  (testing "should return false for empty string"
    (is (false? (pj/is-valid? ""))))

  (testing "should return false for nil"
    (is (false? (pj/is-valid? nil))))

  (testing "should return false for non-string input"
    (is (false? (pj/is-valid? 12345678901234567890)))
    (is (false? (pj/is-valid? {})))
    (is (false? (pj/is-valid? [])))))

;; ============================================================================
;; Tests: remove-symbols
;; ============================================================================

(deftest remove-symbols-test
  (testing "removes formatting from Processo Jurídico"
    (is (= "00020802520125150049" (pj/remove-symbols "0002080-25.2012.515.0049"))))

  (testing "returns unchanged if already clean"
    (is (= "00020802520125150049" (pj/remove-symbols "00020802520125150049"))))

  (testing "handles nil input"
    (is (= "" (pj/remove-symbols nil))))

  (testing "handles empty string"
    (is (= "" (pj/remove-symbols ""))))

  (testing "removes any non-numeric characters"
    (is (= "00020802520125150049" (pj/remove-symbols "0002080@$25201%!@2515.%0049")))))
