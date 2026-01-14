(ns brazilian-utils.boleto.parser
  "Parsing and data extraction functions for boleto digitable lines.
   
   Provides functions to convert between barcode and digitable line formats,
   and to extract structured information from boletos."
  (:require [brazilian-utils.boleto.internal :as i]
            [brazilian-utils.boleto.schemas :as schemas]
            [brazilian-utils.helpers :as helpers]))

(def ^:private bank-code->name
  "Map of bank codes to bank names."
  {"001" "Banco do Brasil"
   "033" "Banco Santander"
   "104" "Caixa Econômica Federal"
   "237" "Banco Bradesco"
   "341" "Banco Itaú"
   "356" "Banco Real"
   "389" "Banco Mercantil do Brasil"
   "399" "HSBC Bank Brasil"
   "422" "Banco Safra"
   "453" "Banco Rural"
   "633" "Banco Rendimento"
   "652" "Itaú Unibanco Holding"
   "745" "Banco Citibank"})

;; ============================================================================
;; Public API
;; ============================================================================

(defn barcode->linha-digitavel
  "Converts a 44-digit barcode into a 47-digit digitable line.
   
   Only works for bank boletos (bancário type). Inserts check digits
   for each of the three fields using modulo 10 algorithm.

  Args:
    barcode - String containing 44-digit barcode (separators allowed)

  Returns:
    47-digit string with field check digits inserted, or nil if invalid
    
  Examples:
    (barcode->linha-digitavel \"23799123400000012345678901234567890123456789\")
    ;; => \"23799123456789012345678901234567890123456789012\""
  [barcode]
  (when (and barcode (string? barcode))
    (let [digits (helpers/only-numbers barcode)]
      (when (= (count digits) 44)
        (let [bank-currency (subs digits 0 4)
              dv-geral      (subs digits 4 5)
              venc-valor    (subs digits 5 19)
              campo-livre-1 (subs digits 19 24)
              campo-livre-2 (subs digits 24 34)
              campo-livre-3 (subs digits 34 44)

              field1 (str bank-currency campo-livre-1)
              field2 campo-livre-2
              field3 campo-livre-3

              dv1 (i/dv-mod10 field1)
              dv2 (i/dv-mod10 field2)
              dv3 (i/dv-mod10 field3)]

          (str field1 dv1 field2 dv2 field3 dv3 dv-geral venc-valor))))))

(defn parse-boleto
  "Parses a 47-digit bank boleto and returns structured information.
   
   Extracts bank code, currency, due date, value, and converts to barcode format.
   Only works for bank boletos (bancário type).

  Args:
    digitable-line - String containing digitable line (may include formatting)

  Returns:
    Map with keys:
    - :bank-code - 3-digit bank code
    - :bank-name - Bank name (if known)
    - :currency - Currency code (1 digit)
    - :due-date-factor - Days since 1997-10-07
    - :due-date - ISO date string (YYYY-MM-DD) or nil
    - :value - Value in cents
    - :barcode - 44-digit barcode
    
    Returns nil if input is invalid or not a bank boleto
    
  Examples:
    (parse-boleto \"23790.00000 00000.000000 00000.000000 0 12345678901234\")
    ;; => {:bank-code \"237\" :bank-name \"Banco Bradesco\" ...}"
  [digitable-line]
  (when-let [{:keys [digits kind]} (schemas/parse-digitable-line digitable-line)]
    (when (= kind :bancario)
      (let [{:keys [bank-code currency]} (i/extract-bank-info digits)
            due-date-factor (i/extract-due-date-factor digits)
            value           (i/extract-value digits)
            barcode         (i/linha->barcode digits)
            due-date        (i/calculate-due-date due-date-factor)]
        {:bank-code       bank-code
         :bank-name       (get bank-code->name bank-code)
         :currency        currency
         :due-date-factor due-date-factor
         :due-date        due-date
         :value           value
         :barcode         barcode}))))
