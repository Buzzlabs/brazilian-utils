(ns brazilian-utils.pis-test
  "Tests for the PIS module (Programa de Integração Social)."
  (:require
   [clojure.test :refer [deftest is testing]]
   [brazilian-utils.pis :as pis]))

;; ============================================================================
;; Tests: is-valid?
;; ============================================================================

(deftest is-valid?-test
  (testing "should return false for reserved numbers"
    (is (false? (pis/is-valid? "44444444444"))))

  (testing "should return false for empty string"
    (is (false? (pis/is-valid? ""))))

  (testing "should return false for nil"
    (is (false? (pis/is-valid? nil))))

  (testing "should return false for non-string input"
    (is (false? (pis/is-valid? 12056412847)))
    (is (false? (pis/is-valid? true)))
    (is (false? (pis/is-valid? false)))
    (is (false? (pis/is-valid? {})))
    (is (false? (pis/is-valid? []))))

  (testing "should return false when length is incorrect"
    (is (false? (pis/is-valid? "123456")))
    (is (false? (pis/is-valid? "1234567890")))  ; 10 digits
    (is (false? (pis/is-valid? "123456789012")))) ; 12 digits

  (testing "should return false when contains letters or special characters"
    (is (false? (pis/is-valid? "12056Aabb412847")))
    (is (false? (pis/is-valid? "120.5641@284-7")))
    (is (false? (pis/is-valid? "abcabcabcde"))))

  (testing "should return false for invalid PIS numbers"
    (is (false? (pis/is-valid? "12056412547")))
    (is (false? (pis/is-valid? "12081636639"))))

  (testing "should return true for valid PIS without mask"
    (is (true? (pis/is-valid? "12056412847"))))

  (testing "should return true for valid PIS with mask"
    (is (true? (pis/is-valid? "120.5641.284-7"))))

  (testing "should return true for valid PIS with last digit 0"
    (is (true? (pis/is-valid? "120.1213.266-0")))
    (is (true? (pis/is-valid? "120.7041.469-0")))))

;; ============================================================================
;; Tests: remove-symbols
;; ============================================================================

(deftest remove-symbols-test
  (testing "removes formatting from PIS"
    (is (= "12056412847" (pis/remove-symbols "120.5641.284-7"))))

  (testing "returns unchanged if already clean"
    (is (= "12056412847" (pis/remove-symbols "12056412847"))))

  (testing "handles nil input"
    (is (= "" (pis/remove-symbols nil))))

  (testing "handles empty string"
    (is (= "" (pis/remove-symbols ""))))

  (testing "removes any non-numeric characters"
    (is (= "12056412847" (pis/remove-symbols "120-564-128-47")))
    (is (= "12056412847" (pis/remove-symbols "120 564 128 47")))))
