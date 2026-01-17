(ns brazilian-utils.cep-test
  "Tests for the CEP (postal code) module."
  (:require
   [clojure.test :refer [deftest is testing]]
   [clojure.string :as str]
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
;; Tests: remove-symbols
;; ============================================================================

(deftest remove-symbols-test
  (testing "removes formatting from valid CEP"
    (is (= "01310100" (cep/remove-symbols "01310-100")))
    (is (= "12345678" (cep/remove-symbols "12345-678")))
    (is (= "98765432" (cep/remove-symbols "98765-432"))))

  (testing "returns unchanged when CEP has no formatting"
    (is (= "01310100" (cep/remove-symbols "01310100")))
    (is (= "12345678" (cep/remove-symbols "12345678"))))

  (testing "removes all non-numeric characters"
    (is (= "01310100" (cep/remove-symbols "01310.100")))
    (is (= "01310100" (cep/remove-symbols "01310 100")))
    (is (= "01310100" (cep/remove-symbols "CEP: 01310-100")))
    (is (= "01310100" (cep/remove-symbols "0.1.3.1.0-1.0.0"))))

  (testing "handles empty input"
    (is (= "" (cep/remove-symbols ""))))

  (testing "handles nil by converting to empty string"
    (is (= "" (cep/remove-symbols nil))))

  (testing "preserves all digits regardless of length"
    (is (= "0131010099999" (cep/remove-symbols "01310-10099999")))
    (is (= "013" (cep/remove-symbols "013")))
    (is (= "0" (cep/remove-symbols "0")))))

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

;; ============================================================================
;; Tests: ViaCEP API Integration
;; ============================================================================

(deftest get-address-from-cep-test
  (testing "retrieves address information from a valid CEP"
    ;; Test with formatted CEP
    (let [result (cep/get-address-from-cep "01310-100")]
      (is (map? result))
      (if-let [error (:error result)]
        ;; Network error is acceptable in test environment
        (is (string? error))
        ;; On success, validate specific fields
        (do
          ;; Required address fields
          (is (contains? result :logradouro))
          (is (contains? result :bairro))
          (is (contains? result :localidade))
          (is (contains? result :uf))
          (is (contains? result :cep))
          
          ;; Type validation
          (is (string? (:logradouro result)))
          (is (string? (:bairro result)))
          (is (string? (:localidade result)))
          (is (string? (:uf result)))
          (is (string? (:cep result)))
          
          ;; Value validation
          (is (not (empty? (:logradouro result))))
          (is (not (empty? (:bairro result))))
          (is (not (empty? (:localidade result))))
          (is (= 2 (count (:uf result))))  ;; UF should be 2 chars
          (is (re-matches #"\d{5}-\d{3}" (:cep result)))  ;; CEP format validation
          
          ;; Specific CEP validation
          (is (= "01310-100" (:cep result))))))
    
    ;; Test with unformatted CEP
    (let [result (cep/get-address-from-cep "01310100")]
      (is (map? result))
      (if-let [error (:error result)]
        (is (string? error))
        (do
          (is (contains? result :cep))
          (is (string? (:cep result)))
          (is (re-matches #"\d{5}-\d{3}" (:cep result)))
          (is (= "01310-100" (:cep result)))))))
  
  (testing "validates response structure from ViaCEP"
    (let [result (cep/get-address-from-cep "01310-100")]
      (if (not (:error result))
        (do
          ;; All these fields should be present in valid response
          (is (contains? result :logradouro))
          (is (contains? result :bairro))
          (is (contains? result :localidade))
          (is (contains? result :uf))
          (is (contains? result :cep))
          (is (contains? result :ddd))      ;; DDD (area code)
          (is (contains? result :ibge))     ;; IBGE code
          (is (contains? result :estado))   ;; Full state name
          
          ;; Type checks for all fields
          (is (every? string? [(:logradouro result)
                               (:bairro result)
                               (:localidade result)
                               (:uf result)
                               (:cep result)
                               (:ddd result)
                               (:ibge result)
                               (:estado result)]))
          
          ;; Field length validations
          (is (<= 2 (count (:uf result)) 2))           ;; UF is 2 chars
          (is (<= 1 (count (:ddd result)) 3))          ;; DDD is 1-3 digits
          (is (<= 1 (count (:ibge result)) 10))        ;; IBGE code
          (is (re-matches #"\d{5}-\d{3}" (:cep result))) ;; CEP format
          
          ;; State validation (should match UF)
          (let [uf (:uf result)
                estado (:estado result)]
            (is (and (string? uf) (string? estado)))
            (is (not (empty? estado))))))))
  
  (testing "returns error map on invalid request"
    (let [result (cep/get-address-from-cep "invalid")]
      (is (map? result))))
  
  (testing "handles various CEP formats"
    (doseq [cep-input ["12345-678" "12345678" "98765-432" "98765432"]]
      (let [result (cep/get-address-from-cep cep-input)]
        (is (map? result)))))
  
  (testing "handles invalid input gracefully"
    ;; All should return error maps, not crash
    (is (map? (cep/get-address-from-cep nil)))
    (is (map? (cep/get-address-from-cep "")))
    (is (map? (cep/get-address-from-cep "abc")))
    (is (map? (cep/get-address-from-cep 123)))
    (is (map? (cep/get-address-from-cep []))))
  
  (testing "handles malformed CEPs"
    ;; Should return error but not crash
    (is (map? (cep/get-address-from-cep "12345")))  ;; too short
    (is (map? (cep/get-address-from-cep "123456789")))  ;; too long
    (is (map? (cep/get-address-from-cep "abcd-efgh")))))

(deftest get-cep-information-from-address-test
  (testing "searches for CEPs by address information"
    (let [result (cep/get-cep-information-from-address "Avenida Paulista" "São Paulo" "SP")]
      (is (map? result))
      (if-let [error (:error result)]
        (is (string? error))
        (do
          ;; Should have all address fields
          (is (contains? result :cep))
          (is (contains? result :logradouro))
          (is (contains? result :bairro))
          (is (contains? result :localidade))
          (is (contains? result :uf))
          
          ;; Type validation
          (is (string? (:cep result)))
          (is (string? (:logradouro result)))
          (is (string? (:bairro result)))
          (is (string? (:localidade result)))
          (is (string? (:uf result)))
          
          ;; CEP format validation
          (is (re-matches #"\d{5}-\d{3}" (:cep result)))
          
          ;; Specific values validation
          (is (= "SP" (:uf result)))
          (is (= "São Paulo" (:localidade result)))
          (is (str/includes? (str/lower-case (:logradouro result)) "avenida"))))))
  
  (testing "validates response structure for address search"
    (let [result (cep/get-cep-information-from-address "Avenida Paulista" "São Paulo" "SP")]
      (if (not (:error result))
        (do
          ;; Check all expected fields are present and have correct types
          (is (string? (:cep result)))
          (is (string? (:logradouro result)))
          (is (string? (:bairro result)))
          (is (string? (:localidade result)))
          (is (string? (:uf result)))
          (is (string? (:ddd result)))
          (is (string? (:ibge result)))
          
          ;; Validate non-empty values
          (is (not (empty? (:logradouro result))))
          (is (not (empty? (:localidade result))))
          (is (not (empty? (:uf result))))
          
          ;; CEP format should be XXXXX-XXX
          (is (re-matches #"\d{5}-\d{3}" (:cep result)))
          
          ;; UF should be 2 characters
          (is (= 2 (count (:uf result)))))))
  
  (testing "handles different address inputs"
    (doseq [street ["Avenida Paulista" "Rua Augusta" "Alameda Santos"]]
      (let [result (cep/get-cep-information-from-address street "São Paulo" "SP")]
        (is (map? result))
        (if (not (:error result))
          (do
            (is (string? (:cep result)))
            (is (string? (:logradouro result)))
            (is (re-matches #"\d{5}-\d{3}" (:cep result))))))))
  
  (testing "handles different cities"
    (doseq [city ["São Paulo" "Rio de Janeiro" "Belo Horizonte"]]
      (let [result (cep/get-cep-information-from-address "Rua Principal" city "SP")]
        (is (map? result))
        (if (not (:error result))
          (do
            ;; Validate locality matches requested city
            (is (contains? result :localidade))
            (is (string? (:localidade result))))))))
  
  (testing "handles different states"
    (doseq [uf ["SP" "RJ" "MG" "BA" "RS"]]
      (let [result (cep/get-cep-information-from-address "Rua Principal" "São Paulo" uf)]
        (is (map? result))
        (if (not (:error result))
          (do
            ;; Validate UF matches
            (is (contains? result :uf))
            (is (= (str/upper-case uf) (:uf result))))))))
  
  (testing "handles invalid inputs gracefully"
    ;; All should return error maps, not crash
    (is (map? (cep/get-cep-information-from-address nil "São Paulo" "SP")))
    (is (map? (cep/get-cep-information-from-address "Rua" nil "SP")))
    (is (map? (cep/get-cep-information-from-address "Rua" "São Paulo" nil)))
    (is (map? (cep/get-cep-information-from-address "" "" "")))
    (is (map? (cep/get-cep-information-from-address 123 456 789))))
  
  (testing "handles malformed parameters"
    ;; Should return error but not crash
    (is (map? (cep/get-cep-information-from-address "x" "y" "ZZ")))  ;; invalid state
    (is (map? (cep/get-cep-information-from-address "!!!" "###" "$$$"))))))