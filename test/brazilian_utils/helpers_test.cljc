(ns brazilian-utils.helpers-test
  "Tests for the helpers module."
  (:require
   [clojure.test :refer [deftest is testing]]
   [brazilian-utils.helpers :as helpers]))

;; ============================================================================
;; Tests: only-numbers
;; ============================================================================

(deftest only-numbers-test
  (testing "removes all non-numeric characters"
    (is (= "12345678900" (helpers/only-numbers "123.456.789-00")))
    (is (= "01310100" (helpers/only-numbers "01310-100")))
    (is (= "12345" (helpers/only-numbers "abc123def45")))
    (is (= "0123456789" (helpers/only-numbers "(01) 23456-789"))))

  (testing "handles strings with only numbers"
    (is (= "12345678" (helpers/only-numbers "12345678")))
    (is (= "0" (helpers/only-numbers "0"))))

  (testing "handles strings with no numbers"
    (is (= "" (helpers/only-numbers "abcdef")))
    (is (= "" (helpers/only-numbers "!@#$%^&*()"))))

  (testing "handles edge cases"
    (is (= "" (helpers/only-numbers "")))
    (is (= "" (helpers/only-numbers nil)))
    (is (= "" (helpers/only-numbers "   ")))))

;; ============================================================================
;; Tests: repeated-digits?
;; ============================================================================

(deftest repeated-digits?-test
  (testing "returns true for strings with all same digits - with length check"
    (is (true? (helpers/repeated-digits? "00000000" 8)))
    (is (true? (helpers/repeated-digits? "11111111" 8)))
    (is (true? (helpers/repeated-digits? "22222222" 8)))
    (is (true? (helpers/repeated-digits? "99999999" 8))))
  
  (testing "returns true for formatted strings with all same digits - with length check"
    (is (true? (helpers/repeated-digits? "00000-000" 8)))
    (is (true? (helpers/repeated-digits? "11111-111" 8)))
    (is (true? (helpers/repeated-digits? "55555-555" 8)))
    (is (true? (helpers/repeated-digits? "99999-999" 8))))
  
  (testing "returns false for strings with different digits"
    (is (false? (helpers/repeated-digits? "01310100" 8)))
    (is (false? (helpers/repeated-digits? "12345678" 8)))
    (is (false? (helpers/repeated-digits? "01310-100" 8)))
    (is (false? (helpers/repeated-digits? "12345-678" 8))))
  
  (testing "returns false for invalid inputs with length check"
    (is (false? (helpers/repeated-digits? "0000000" 8)))   ;; 7 digits
    (is (false? (helpers/repeated-digits? "000000000" 8))) ;; 9 digits  
    (is (false? (helpers/repeated-digits? "" 8)))          ;; empty
    (is (false? (helpers/repeated-digits? nil 8))))        ;; nil

  (testing "returns true for repeated digits without length check"
    (is (true? (helpers/repeated-digits? "0000")))
    (is (true? (helpers/repeated-digits? "111")))
    (is (true? (helpers/repeated-digits? "22"))))

  (testing "returns false for mixed digits without length check"
    (is (false? (helpers/repeated-digits? "0123")))
    (is (false? (helpers/repeated-digits? "112")))
    (is (false? (helpers/repeated-digits? "23"))))

  (testing "handles edge cases without length check"
    (is (false? (helpers/repeated-digits? "")))
    (is (false? (helpers/repeated-digits? nil)))
    (is (true? (helpers/repeated-digits? "5")))))

;; ============================================================================
;; Tests: char->digit
;; ============================================================================

(deftest char->digit-test
  (testing "converts digit characters to numeric values"
    (is (= 0 (helpers/char->digit \0)))
    (is (= 1 (helpers/char->digit \1)))
    (is (= 5 (helpers/char->digit \5)))
    (is (= 9 (helpers/char->digit \9))))

  (testing "handles string digits"
    (is (= 0 (helpers/char->digit "0")))
    (is (= 3 (helpers/char->digit "3")))
    (is (= 7 (helpers/char->digit "7")))
    (is (= 9 (helpers/char->digit "9"))))

  (testing "preserves digit value"
    (is (= 2 (helpers/char->digit \2)))
    (is (= 4 (helpers/char->digit \4)))
    (is (= 6 (helpers/char->digit \6)))
    (is (= 8 (helpers/char->digit \8)))))