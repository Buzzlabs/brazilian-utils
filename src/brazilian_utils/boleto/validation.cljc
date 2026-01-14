(ns brazilian-utils.boleto.validation
  "Validation logic for Brazilian boleto (payment slip) digitable lines.
   
   Supports two types of boletos:
   - Bancário (bank boleto): 47 digits, for general payments
   - Arrecadação (collection boleto): 48 digits, for taxes and utility bills"
  (:require [brazilian-utils.boleto.internal :as i]
            [brazilian-utils.boleto.schemas :as schemas]))

;; ============================================================================
;; Structure Validation - Arrecadação
;; ============================================================================

(defn- valid-arrecadacao?
  "Validates arrecadação (collection) boleto structure.
   
   Checks:
   - Exactly 48 digits
   - Starts with digit 8
   - Not all zeros (prevents fake test boletos)
   - All 4 blocks have valid check digits"
  [digits]
  (and (= (count digits) i/arrecadacao-length)
       (= (first digits) \8)
       ;; Prevents fake test boletos (e.g., all zeros)
       (not (every? #(= % \0) (subs digits 1)))
       ;; Validates each of the 4 blocks individually
       (let [dv-fn (i/resolve-arrecadacao-dv digits)]
         (every? #(i/valid-arrecadacao-block? % dv-fn) 
                 (i/arrecadacao-blocks digits)))))

;; ============================================================================
;; Structure Validation - Bancário
;; ============================================================================

(defn- valid-bancario-fields?
  "Validates all three fields of a bancário boleto against their check digits."
  [digits]
  (every? #(i/valid-field? % digits) i/bancario-fields))

(defn- valid-bancario-dv-geral?
  "Validates the general check digit (DV) of a bancário boleto.
   
   The general DV is at position 4 of the barcode and validates
   the entire barcode using modulo 11 algorithm."
  [digits]
  (let [barcode (i/linha->barcode digits)
        general-dv-position 4
        informed-dv (i/parse-digit (nth barcode general-dv-position))
        barcode-without-dv (str (subs barcode 0 general-dv-position)
                                (subs barcode (inc general-dv-position)))
        expected-dv (i/dv-mod11-bank barcode-without-dv)]
    (= informed-dv expected-dv)))

(defn- valid-bancario?
  "Validates bancário (bank) boleto structure.
   
   Checks:
   - Exactly 47 digits
   - Does not start with 8 (to distinguish from arrecadação)
   - All three field check digits are valid
   - General check digit is valid"
  [digits]
  (and (= (count digits) i/bancario-length)
       (not= (first digits) \8)
       (valid-bancario-fields? digits)
       (valid-bancario-dv-geral? digits)))

;; ============================================================================
;; Public API
;; ============================================================================

(defn boleto-bancario?
  "Checks if boleto is of BANCÁRIO type (bank boleto).
   
   Bank boletos have 47 digits and do not start with 8.
   Note: This only checks the type, not validity.
   
   Args:
     digits - String of digits (should be at least 47 digits)
   
   Returns:
     true if bancário type, false otherwise"
  [digits]
  (and (>= (count digits) i/bancario-length)
       (not= (first digits) \8)))

(defn boleto-arrecadacao?
  "Checks if boleto is of ARRECADAÇÃO type (collection boleto).
   
   Collection boletos have 48 digits and start with 8.
   Used for taxes, utility bills, and government payments.
   Note: This only checks the type, not validity.
   
   Args:
     digits - String of digits
   
   Returns:
     true if arrecadação type, false otherwise"
  [digits]
  (and (= (count digits) i/arrecadacao-length)
       (= (first digits) \8)))

(defn is-valid?
  "Validates a complete boleto digitable line.
   
   Accepts both formatted and unformatted inputs with 46, 47, or 48 digits.
   Automatically detects type (bancário or arrecadação) and applies
   appropriate validation rules.
   
   Args:
     digitable-line - String containing boleto digits (may include formatting)
   
   Returns:
     true if valid, false otherwise
   
   Examples:
     (is-valid? \"23790.00000 00000.000000 00000.000000 0 00000000000000\") ;; => true/false
     (is-valid? \"848600000000123456789012345678901234567890123456\") ;; => true/false"
  [digitable-line]
  (if-let [{:keys [digits kind]} (schemas/parse-digitable-line digitable-line)]
    (case kind
      :bancario    (valid-bancario? digits)
      :arrecadacao (valid-arrecadacao? digits)
      false)
    false))