(ns brazilian-utils.cep.validation
  "Validation schemas for CEP (Brazilian postal code).
   
   Uses malli to define and validate CEP-related data structures."
  (:require [malli.core :as m]
            [brazilian-utils.helpers :as helpers]))

(def CEPFormatted
  "Schema for CEP with optional hyphen (xxxxx-xxx or xxxxxxxx).
   Follows the regex ^\\d{5}-?\\d{3}$ pattern.
   
   Example:
   (m/validate CEPFormatted \"01310-100\") ;; true
   (m/validate CEPFormatted \"01310100\")  ;; true
   (m/validate CEPFormatted \"0131010\")   ;; false (7 digits)"
  [:re #"^\d{5}-?\d{3}$"])

(defn validate-cep
  "Validates whether a string is a valid CEP (formatted or not).
   
   Checks:
   - Format: 8 numeric digits with optional hyphen (xxxxx-xxx or xxxxxxxx)
   - Not all repeated digits (rejects 00000000, 11111111, etc.)
   
   Returns true if valid, false otherwise.
   
   Example:
   (validate-cep \"01310100\")  ;; true
   (validate-cep \"01310-100\") ;; true
   (validate-cep \"00000000\")  ;; false (repeated digits)
   (validate-cep \"0131010\")   ;; false (wrong length)"
  [value]
  (and (m/validate CEPFormatted value)
       (not (helpers/repeated-digits? value 8))))
