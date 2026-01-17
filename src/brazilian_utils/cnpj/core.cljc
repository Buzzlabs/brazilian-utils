(ns brazilian-utils.cnpj.core
  (:require [brazilian-utils.cnpj.internal :as i]
            [brazilian-utils.cnpj.format :as fmt]
            [brazilian-utils.cnpj.validation :as validation]
            [brazilian-utils.helpers :as helpers]))

(defn remove-symbols
  "Normalizes a CNPJ by stripping non-alphanumerics and uppercasing.

   Args:
     cnpj - String (formatted or not); nil allowed

   Returns:
     Up to 14 uppercase A-Z/0-9 characters; nil becomes empty string

  Examples:
  (remove-symbols \"12.345.678/0001-95\") ;; => \"12345678000195\"
  (remove-symbols \"ab1c2d3e4f5g19\")    ;; => \"AB1C2D3E4F5G19\"
  (remove-symbols \"\")                  ;; => \"\""
  [cnpj]
  (i/clean-alfanumeric (str cnpj)))

(defn format-cnpj
  "Formats a CNPJ with standard Brazilian mask (XX.XXX.XXX/XXXX-XX).
  
  This function normalizes and formats a CNPJ string by adding the standard Brazilian
  punctuation mask. It supports numeric, alphanumeric, partial inputs, and optional
  zero-padding behavior.

  Arguments:
    cnpj - String or number to format (formatted or unformatted)
    opts - Optional map for zero-padding behavior (not required)

  Returns:
    A CNPJ string formatted with the mask (XX.XXX.XXX/XXXX-XX)
    
  Examples:
    (format-cnpj \"12345678000195\") ;; => \"12.345.678/0001-95\"
    (format-cnpj \"12.345.678/0001-95\") ;; => \"12.345.678/0001-95\"
    (format-cnpj \"1234567\") ;; => \"12.345.67\" (partial)"
  ([cnpj] (fmt/format-cnpj cnpj))
  ([cnpj opts] (fmt/format-cnpj cnpj opts)))

(defn generate
  "Generates a valid numeric CNPJ (14 digits, unformatted).
  
  This function creates a random valid CNPJ that passes all validation checks.
  It ensures the generated CNPJ is not composed entirely of repeated digits and
  has valid check digits according to the Brazilian CNPJ algorithm.

  Arguments:
    None

  Returns:
    A valid 14-digit numeric CNPJ string (unformatted, digits only)
    
  Examples:
    (generate) ;; => \"12345678000195\"
    (generate) ;; => \"98765432000123\""
  []
  (let [base (i/generate-numeric-base)
        dv1 (i/calc-check-digit* base i/first-check-digit-weights)
        dv2 (i/calc-check-digit* (str base dv1) i/second-check-digit-weights)]
    (str base dv1 dv2)))

(defn generate-alfanumeric
  "Generates a valid alphanumeric CNPJ (14 chars, unformatted).
  
  This function creates a random valid alphanumeric CNPJ where the first 12 characters
  can be letters (A-Z) or digits (0-9), and the last 2 characters are numeric check digits.
  The generated CNPJ passes all validation checks according to IN RFB nº 2.229/2024.

  Arguments:
    None

  Returns:
    A valid 14-character alphanumeric CNPJ string (unformatted)
    Last 2 characters are always numeric (check digits)
    
  Examples:
    (generate-alfanumeric) ;; => \"AB1234567000195\"
    (generate-alfanumeric) ;; => \"XY9876543000123\""
  []
  (let [base (i/generate-alphanumeric-base)
        dv1 (i/calc-check-digit* base i/first-check-digit-weights)
        dv2 (i/calc-check-digit* (str base dv1) i/second-check-digit-weights)]
    (str base dv1 dv2)))

(defn is-valid?
  "Validates numeric or alphanumeric CNPJ (IN RFB nº 2.229/2024).

  This function checks if a CNPJ is valid by verifying:
  - It is a string
  - It has exactly 14 characters (after cleaning)
  - It is not all repeated digits
  - The check digits are correct according to the Brazilian algorithm
  - If it contains letters, it follows the alphanumeric format rules

  Accepts both formatted (XX.XXX.XXX/XXXX-XX) and unformatted (XXXXXXXXXXXXXX) CNPJs.

  Arguments:
    cnpj - CNPJ string to validate (formatted or unformatted)

  Returns:
    true if valid; false otherwise
    
  Examples:
    (is-valid? \"12.345.678/0001-95\") ;; => true/false
    (is-valid? \"12345678000195\")    ;; => true/false
    (is-valid? \"00000000000000\")    ;; => false (repeated)
    (is-valid? \"AB.123.456/0001-95\") ;; => true/false (alphanumeric)
    (is-valid? nil)                    ;; => false"
  [cnpj]
  (if-not (string? cnpj)
    false
    (let [cleaned (i/clean-alfanumeric cnpj)
          contains-letter? (boolean (re-find #"[A-Z]" cleaned))
          length-ok? (= (count cleaned) i/cnpj-length)
          shape-ok? (and length-ok?
                         (if contains-letter?
                           (validation/is-alfanumeric? cleaned)
                           (validation/is-numeric? cleaned)))]
      (boolean
        (and shape-ok?
             (not (helpers/repeated-digits? cleaned i/cnpj-length))
             (i/valid-checksum* cleaned))))))
