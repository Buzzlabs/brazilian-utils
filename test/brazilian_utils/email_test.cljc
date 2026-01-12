(ns brazilian-utils.email-test
  "Tests for the email validation module."
  (:require
   [clojure.test :refer [deftest is testing]]
   [brazilian-utils.email :as email]))

;; ============================================================================
;; Valid Email Tests
;; ============================================================================

(deftest is-valid?-test-valid-emails
  (testing "returns true for valid emails"
    (is (true? (email/is-valid? "john.doe@hotmail.com")))
    (is (true? (email/is-valid? "john_doe@myenterprise.com.br")))
    (is (true? (email/is-valid? "john.doe@gmail.com")))))

;; ============================================================================
;; Invalid Email Tests
;; ============================================================================

(deftest is-valid?-test-invalid-inputs
  (testing "returns false for invalid input types"
    (is (false? (email/is-valid? "")))        ; empty string
    (is (false? (email/is-valid? nil)))       ; null
    (is (false? (email/is-valid? true)))      ; boolean true
    (is (false? (email/is-valid? false)))     ; boolean false
    (is (false? (email/is-valid? {})))        ; object
    (is (false? (email/is-valid? [])))))      ; array

(deftest is-valid?-test-email-format-issues
  (testing "returns false for email format issues"
    (is (false? (email/is-valid? "john.doe.teste.com.br")))  ; without at symbol
    
    ;; Email too long - 64 chars recipient + @ + repeated domain to exceed 318 chars
    (let [too-long-email (str (apply str (repeat 64 "a")) "@" (apply str (repeat 28 "test.co.uk")))]
      (is (false? (email/is-valid? too-long-email))))))

(deftest is-valid?-test-recipient-issues
  (testing "returns false for recipient name issues"
    (is (false? (email/is-valid? "@teste.com.br")))           ; length equal to 0
    
    ;; More than 64 characters
    (let [email-65-char-recipient (str (apply str (repeat 65 "a")) "@teste.com.br")]
      (is (false? (email/is-valid? email-65-char-recipient))))
    
    (is (false? (email/is-valid? "(johndoe)@test.com.br")))   ; invalid character
    (is (false? (email/is-valid? "john..doe@teste.com.br")))  ; 2 special characters consecutively
    (is (false? (email/is-valid? ".john.doe@teste.com.br")))  ; start with unallowed special characters
    (is (false? (email/is-valid? "jóhn.doe@teste.com.br")))))  ; contains accentuation

(deftest is-valid?-test-domain-issues
  (testing "returns false for domain name issues"
    (is (false? (email/is-valid? "johndoe@")))                ; length equal to 0
    
    ;; More than 253 characters - creating a domain with 254+ chars
    (let [domain-254-length (str "ab" (apply str (repeat 21 "teste.com.br")))]
      (is (false? (email/is-valid? (str "johndoe@" domain-254-length)))))
    
    (is (false? (email/is-valid? "johndoe@téste.com.br")))    ; contains accentuation
    (is (false? (email/is-valid? "johndoe@test.com.")))))     ; hasn't top level domain

;; ============================================================================
;; Validation Errors Function Tests
;; ============================================================================

(deftest validation-errors-test
  (testing "returns empty vector for valid emails"
    (is (= [] (email/validation-errors "john.doe@hotmail.com")))
    (is (= [] (email/validation-errors "john_doe@myenterprise.com.br"))))

  (testing "returns vector of error messages for invalid emails"
    ;; Test blank email
    (let [result (email/validation-errors "")]
      (is (vector? result))
      (is (seq result))
      (is (every? string? result)))
    
    ;; Test nil
    (let [result (email/validation-errors nil)]
      (is (vector? result))
      (is (seq result))
      (is (every? string? result)))
    
    ;; Test format error
    (let [result (email/validation-errors "john.doe.teste.com.br")]
      (is (vector? result))
      (is (seq result))
      (is (every? string? result)))
    
    ;; Test length limits
    (let [long-recipient (apply str (repeat 65 "a"))
          result (email/validation-errors (str long-recipient "@example.com"))]
      (is (vector? result))
      (is (seq result))
      (is (every? string? result))))

  (testing "error messages are informative strings"
    (let [result (email/validation-errors "")]
      (is (every? #(and (string? %) (seq %)) result)))))
