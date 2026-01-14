(ns brazilian-utils.boleto.schemas
  "Malli schemas and input normalization for boleto processing.
   
   Provides schemas for validating and normalizing digitable line inputs."
  (:require [brazilian-utils.boleto.internal :as i]
            [brazilian-utils.helpers :as helpers]
            [malli.core :as m]))

(def DigitableLineInput
  "Schema for digitable line input (formatted or unformatted).
   
   Accepts strings with 46, 47, or 48 digits after stripping non-numeric characters.
   - 46 digits: Bank boleto without one digit (will be padded to 47)
   - 47 digits: Bank boleto (bancário)
   - 48 digits: Collection boleto (arrecadação)"
  [:and
   :string
   [:fn
    {:error/message "Digitable line must contain 46, 47, or 48 digits"}
    (fn [s]
      (let [digits (helpers/only-numbers s)]
        (#{46 47 48} (count digits))))]])

(defn parse-digitable-line
  "Normalizes and classifies a digitable line input.
   
   Strips non-numeric characters, applies padding if needed (46->47 digits),
   and determines the boleto type based on digit count and first digit.
   
   Args:
     value - String containing digitable line (may include formatting)
   
   Returns:
     Map with :digits (normalized string) and :kind (:bancario or :arrecadacao),
     or nil if input is invalid
   
   Examples:
     (parse-digitable-line \"23790.00000 00000.000000 00000.000000 0 00000000000000\")
     ;; => {:digits \"23790000000000000000000000000000000000000000000\" :kind :bancario}
     
     (parse-digitable-line \"848600000000123456789012345678901234567890123456\")
     ;; => {:digits \"848600000000123456789012345678901234567890123456\" :kind :arrecadacao}"
  [value]
  (when (and value (string? value) (m/validate DigitableLineInput value))
    (let [digits (helpers/only-numbers value)
          digits (i/coerce-bancario-digits digits)]
      {:digits digits
       :kind   (case (count digits)
                 47 :bancario
                 48 :arrecadacao)})))
