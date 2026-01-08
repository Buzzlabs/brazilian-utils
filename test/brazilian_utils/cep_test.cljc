(ns brazilian-utils.cep-test
  "Testes para o módulo de CEP (Código de Endereçamento Postal)."
  (:require
   [clojure.test :refer [deftest is testing]]
   [brazilian-utils.cep :as cep]))

;; ============================================================================
;; Testes: is-valid?
;; ============================================================================

(deftest is-valid?-test
  (testing "returns true for valid CEPs without formatting"
    (is (true? (cep/is-valid? "01310100")))
    (is (true? (cep/is-valid? "12345678")))
    (is (true? (cep/is-valid? "00000000"))))

  (testing "returns true for valid CEPs with formatting"
    (is (true? (cep/is-valid? "01310-100")))
    (is (true? (cep/is-valid? "12345-678")))
    (is (true? (cep/is-valid? "00000-000"))))

  (testing "returns false for invalid CEPs"
    (is (false? (cep/is-valid? "0131010")))    ;; 7 dígitos
    (is (false? (cep/is-valid? "013101000")))  ;; 9 dígitos
    (is (false? (cep/is-valid? "01310-10")))   ;; formatado incompleto
    (is (false? (cep/is-valid? "abcdefgh")))   ;; não numérico
    (is (false? (cep/is-valid? "")))           ;; vazio
    (is (false? (cep/is-valid? nil)))          ;; nil
    (is (true? (cep/is-valid? "01310 100"))))  ;; espaço é aceito (extraído como 01310100)

  (testing "handles mixed characters"
    (is (true? (cep/is-valid? "CEP: 01310-100"))))

  (testing "handles different types"
    (is (false? (cep/is-valid? 123)))
    (is (false? (cep/is-valid? [])))
    (is (false? (cep/is-valid? {})))))

;; ============================================================================
;; Testes: format-cep
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


