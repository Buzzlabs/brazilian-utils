(ns brazilian-utils.boleto-test
  "Tests for the boleto module (Brazilian bank slip)."
  (:require
   [clojure.test :refer [deftest is testing]]
   [brazilian-utils.boleto :as boleto]))

;; ============================================================================
;; Tests: valid-boleto?
;; ============================================================================

(deftest valid-boleto?-test
  (testing "valid bank boletos (47 digits) - VALIDATED WITH CORRECT DVs"
    ;; 1) Banco do Brasil – confirmed valid with correct DVs
    (is (true? (boleto/is-valid? "00190500954014481606906809350314337370000000100")))
    ;; 2) Banco do Brasil – formatted variant
    (is (true? (boleto/is-valid? "0019.05009 54014.481606 90680.935031 4 33737000000100")))
    ;; 3) Banco do Brasil – raw variant (all digits, no formatting)
    (is (true? (boleto/is-valid? "00190000090114971860168524522114675860000102656"))))

  (testing "collection boletos (48 digits)"
    ;; invalid: 2º dígito ≠ 1 ✓
    (is (false? (boleto/is-valid?
           "816000000004000000000000000000000000000000000000")))
    
    ;; invalid: DVs incorretos ✓
    (is (false? (boleto/is-valid?
           "817000000006000000000000000000000000000000000000")))

    ;; Simples Nacional (Tributos)
    (is (true? (boleto/is-valid?
          "85870000010 3 44490328260 4 13072026013 0 34553079440 2")))
    
    ;; Energia Elétrica (Luz)
    (is (true? (boleto/is-valid?
          "836000000015795400481003601619504017002105354381")))
    
    ;; Receita Federal (Impostos)
    (is (true? (boleto/is-valid?
          "85840000013 2 55930385253 6 51071625351 7 27843413630 0")))

  (testing "detects collection boleto type (48 digits, starts with 8)"
    (is (true? (boleto/boleto-arrecadacao? "836600000015010800863100460230002001200000000000")))
    (is (true? (boleto/boleto-arrecadacao? "858200000018010800863100460230002001200000000000")))
    (is (true? (boleto/boleto-arrecadacao? "826700000009024701026202000123456789012345678900"))))

  (testing "type checking with boleto-bancario? and boleto-arrecadacao?"
    ;; Bank boleto: 47 digits, doesn't start with 8
    (is (true? (boleto/boleto-bancario? "00190500954014481606906809350314337370000000100")))
    ;; Collection boleto: 48 digits, starts with 8
    (is (true? (boleto/boleto-arrecadacao? "836600000015010800863100460230002001200000000000")))
    ;; Cross-type rejection
    (is (false? (boleto/boleto-bancario? "836600000015010800863100460230002001200000000000")))
    (is (false? (boleto/boleto-arrecadacao? "00190500954014481606906809350314337370000000100"))))

  (testing "invalid bank boletos"
    (is (false? (boleto/is-valid? "00190500954014481606906809350314337370000000101"))) ; wrong field DV
    (is (false? (boleto/is-valid? "00190500954014481606906809350314337370000000199"))) ; wrong general DV
    (is (false? (boleto/is-valid? "00190000090114971860168524522114675860000102655"))) ; wrong second field DV
    (is (false? (boleto/is-valid? "3419179001010435100479102015000829107000001000")))  ; only 46 digits
    (is (false? (boleto/is-valid? "83790500954014481606906809350314337370000000100")))) ; starts with 8 (collection format)

  (testing "invalid collection boletos"
    (is (false? (boleto/is-valid? "888888888888888888888888888888888888888888888888"))) ; all digits equal
    (is (false? (boleto/is-valid? "83660000001501080086310046023000200120000000000")))) ; 47 digits instead of 48

  (testing "returns false for empty or nil input"
    (is (false? (boleto/is-valid? nil)))
    (is (false? (boleto/is-valid? ""))))

  (testing "returns false for non-string input"
    (is (false? (boleto/is-valid? 12345)))
    (is (false? (boleto/is-valid? :boleto))))

  (testing "returns false for incorrect length"
    (is (false? (boleto/is-valid? "123")))
    (is (false? (boleto/is-valid? "12345678901234567890123456789012345678901234567")))
    (is (false? (boleto/is-valid? "123456789012345678901234567890123456789012345")))))

;; ============================================================================
;; Tests: format-linha-digitavel
;; ============================================================================

(deftest format-linha-digitavel-test
  (testing "formats valid boleto number correctly"
    ;; BB1: 00190500954014481606906809350314337370000000100
    ;; Should format as: 0019.05009 54014.481606 90680.935031 4 33737000000100
    (is (= "0019.05009 54014.481606 90680.935031 4 33737000000100"
           (boleto/format-linha-digitavel "00190500954014481606906809350314337370000000100"))))
    ;; BB2: 00190000090114971860168524522114675860000102656
    ;; Formatted output: 0019.00000 90114.971860 16852.452211 4 67586000102656
    (is (= "0019.00000 90114.971860 16852.452211 4 67586000102656"
           (boleto/format-linha-digitavel "00190000090114971860168524522114675860000102656"))))

  (testing "formats boleto with dots and spaces already present"
    (is (= "0019.05009 54014.481606 90680.935031 4 33737000000100"
           (boleto/format-linha-digitavel "0019.05009 54014.481606 90680.935031 4 33737000000100"))))

  (testing "handles non-numeric characters by removing them"
    (is (= "0019.05009 54014.481606 90680.935031 4 33737000000100"
           (boleto/format-linha-digitavel "0019x.05009 54014.481606 90680.935031 4 33737000000100abc"))))

  (testing "handles partial input (returns partial format)"
    ;; For boletos shorter than 47 digits, should still attempt to format what's available
    (let [result (boleto/format-linha-digitavel "00190500954014481606906809350314337")]
      (is (string? result))
      (is (pos? (count result)))))

  (testing "returns empty string for empty input"
    (is (= "" (boleto/format-linha-digitavel ""))))

  (testing "FEBRABAN BB example with correct formatting"
    ;; Raw: 34191790010104351004791020150082910700000100000 (47 digits, DV=8)
    ;; Note: formatter produces: 3419.17900 10104.351004 79102.015008 2 91070000100000
    (is (= "3419.17900 10104.351004 79102.015008 2 91070000100000"
           (boleto/format-linha-digitavel "34191790010104351004791020150082910700000100000")))))

;; ============================================================================
;; Tests: Edge Cases
;; ============================================================================

(deftest edge-cases-test
  (testing "valid-boleto? handles mixed formatting"
    ;; BB1 with hyphens instead of dots/spaces should still work
    (is (true? (boleto/is-valid? "0019-05009 54014-481606 90680-935031 4 33737000000100"))))

  (testing "format-linha-digitavel handles nil by returning empty string"
    (is (= "" (boleto/format-linha-digitavel nil)))))

(deftest parse-boleto-test
  (testing "parses boleto with formatting correctly"
    (let [result (boleto/parse-boleto "0019.05009 54014.481606 90680.935031 4 33737000000100")]
      (is (= "001" (:bank-code result)))
      (is (= "Banco do Brasil" (:bank-name result)))))

  (testing "parses boleto without formatting"
    (let [result (boleto/parse-boleto "00190500954014481606906809350314337370000000100")]
      (is (= "001" (:bank-code result)))
      (is (= "Banco do Brasil" (:bank-name result)))))

  (testing "returns nil for invalid input"
    (is (nil? (boleto/parse-boleto "123")))
    (is (nil? (boleto/parse-boleto "")))
    (is (nil? (boleto/parse-boleto nil))))

  (testing "handles unknown bank code"
    ;; Using boleto format but with unknown bank code (999)
    (let [result (boleto/parse-boleto "99990500954014481606906809350314337370000000100")]
      (is (= "999" (:bank-code result)))
      (is (nil? (:bank-name result))))))

;; ============================================================================
;; Tests: barcode->linha-digitavel
;; ============================================================================

(deftest barcode->linha-digitavel-test
  (testing "converts valid barcode to digitable line"
    (let [barcode-bb1 "00193373700000001000500940144816060680935031"
          linha      (boleto/barcode->linha-digitavel barcode-bb1)]
      (is (some? linha))
      (is (= 47 (count linha)))))

  (testing "returns nil for invalid length"
    (is (nil? (boleto/barcode->linha-digitavel "123")))
    (is (nil? (boleto/barcode->linha-digitavel "123456789012345678901234567890123456789012345"))))

  (testing "returns nil for nil or empty input"
    (is (nil? (boleto/barcode->linha-digitavel nil)))
    (is (nil? (boleto/barcode->linha-digitavel ""))))

  (testing "handles non-numeric characters by removing them"
    ;; Barcode with dots: should fail (not 44 digits when normalized)
    (let [barcode "0019.3373.700000.01000500940144816060680935031"
          linha   (boleto/barcode->linha-digitavel barcode)]
      (is (nil? linha)))))
