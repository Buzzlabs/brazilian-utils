(ns brazilian-utils.currency-test
  "Tests for the Currency module (Brazilian BRL formatting and parsing)."
  (:require
   [clojure.test :refer [deftest is testing]]
   [brazilian-utils.currency :as currency]))

;; ============================================================================
;; Tests: format
;; ============================================================================

(deftest format-test
  (testing "should format currency into BRL with default precision (2)"
    (is (= "0,01" (currency/format-currency 0.01)))
    (is (= "0,10" (currency/format-currency 0.1)))
    (is (= "1,00" (currency/format-currency 1)))
    (is (= "10,00" (currency/format-currency 10)))
    (is (= "10,10" (currency/format-currency 10.1)))
    (is (= "10,01" (currency/format-currency 10.01)))
    (is (= "100,01" (currency/format-currency 100.01)))
    (is (= "1.000,01" (currency/format-currency 1000.01)))
    (is (= "10.000,01" (currency/format-currency 10000.01)))
    (is (= "100.000,01" (currency/format-currency 100000.01)))
    (is (= "1.000.000,01" (currency/format-currency 1000000.01))))

  (testing "should format with different precision"
    (is (= "0,010" (currency/format-currency 0.01 {:precision 3})))
    (is (= "0,100" (currency/format-currency 0.1 {:precision 3})))
    (is (= "1,100" (currency/format-currency 1.1 {:precision 3})))
    (is (= "1,010" (currency/format-currency 1.01 {:precision 3})))
    (is (= "1,001" (currency/format-currency 1.001 {:precision 3})))
    (is (= "10,001" (currency/format-currency 10.001 {:precision 3})))
    (is (= "100,001" (currency/format-currency 100.001 {:precision 3})))
    (is (= "1.000,001" (currency/format-currency 1000.001 {:precision 3})))
    (is (= "10.000,001" (currency/format-currency 10000.001 {:precision 3})))
    (is (= "100.000,001" (currency/format-currency 100000.001 {:precision 3})))
    (is (= "1.000.000,001" (currency/format-currency 1000000.001 {:precision 3})))))

;; ============================================================================
;; Tests: parse
;; ============================================================================

(deftest parse-test
  (testing "should transform formatted currency value into a float"
    (is (= 0.0 (currency/parse "")))
    (is (= 1.0 (currency/parse "R$ 1,00")))
    (is (= 1.1 (currency/parse "R$ 1,10")))
    (is (= 1.01 (currency/parse "R$ 1,01")))
    (is (= 10.01 (currency/parse "R$ 10,01")))
    (is (= 100.01 (currency/parse "R$ 100,01")))
    (is (= 1000.01 (currency/parse "R$ 1.000,01")))
    (is (= 10000.01 (currency/parse "R$ 10.000,01")))
    (is (= 100000.01 (currency/parse "R$ 100.000,01")))
    (is (= 1000000.01 (currency/parse "R$ 1.000.000,01"))))

  (testing "should handle edge cases in parsing"
    (is (= 0.0 (currency/parse nil)))
    (is (= 0.0 (currency/parse "")))
    (is (= 1.0 (currency/parse "100"))))

  (testing "should handle various formatting styles"
    (is (= 1234.56 (currency/parse "1.234,56")))
    (is (= 1234.56 (currency/parse "1234,56")))
    (is (= 1234.56 (currency/parse "1,234.56")))))
