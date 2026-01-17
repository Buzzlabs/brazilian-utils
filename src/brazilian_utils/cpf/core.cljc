(ns brazilian-utils.cpf.core
  (:require [brazilian-utils.cpf.internal :as i]
            [brazilian-utils.cpf.format :as fmt]
            [brazilian-utils.cpf.validation :as validation]
            [brazilian-utils.helpers :as helpers]))


(defn remove-symbols
  "Removes all non-numeric characters from a CPF.

  This function normalizes CPF input by removing formatting characters like dots
  and hyphens, returning only the digits.

  Args:
    cpf - CPF string (formatted or unformatted); nil allowed

  Returns:
    Digits-only string; nil yields an empty string

  Examples:
    (remove-symbols \"123.456.789-09\") ;; => \"12345678909\"
    (remove-symbols \"12345678909\")    ;; => \"12345678909\"
    (remove-symbols nil)                  ;; => \"\"
    (remove-symbols \"\")              ;; => \"\""
  [cpf]
  (helpers/only-numbers cpf))

(defn format-cpf
  "Formats a CPF string with standard Brazilian punctuation (XXX.XXX.XXX-XX).
  
  This function normalizes and formats a CPF string by adding the standard Brazilian
  punctuation mask. It supports both formatted and unformatted inputs, partial inputs,
  and optional zero-padding behavior.

  Args:
    cpf - CPF string or number to format
    opts - Optional map for zero-padding behavior (not required)

  Returns:
    A CPF string formatted with the mask (XXX.XXX.XXX-XX)
    
  Examples:
    (format-cpf \"12345678909\") ;; => \"123.456.789-09\"
    (format-cpf \"123.456.789-09\") ;; => \"123.456.789-09\"
    (format-cpf \"1234567\") ;; => \"12.345.67\" (partial)"
  ([cpf] (fmt/format-cpf cpf))
  ([cpf opts] (fmt/format-cpf cpf opts)))

(defn generate
  "Generates a valid random numeric CPF.

  Creates a random 9-digit base, calculates the proper check digits using
  the Brazilian CPF algorithm, and returns a complete 11-digit valid CPF.
  The generated CPF is guaranteed to pass validation and will not be one
  of the reserved (all-repeated-digits) numbers.

  Args:
    state - Optional Brazilian state code keyword (e.g., :SP, :RJ) to use as the 9th digit.
            If not provided or invalid, a random digit is used.

  Returns:
    Valid 11-digit numeric CPF string (unformatted)

  Examples:
    (generate)       ;; => \"12345678909\"
    (generate :SP)   ;; => \"12345678901\" (with SP state code)"
  ([]   (generate nil))
  ([state]
   (let [state-code (i/get-state-code state)
         base-8-digits (i/generate-non-repeated-base state-code)
         base-9-digits (str base-8-digits state-code)
         first-check-digit (i/calc-check-digit* base-9-digits i/first-check-digit-weight)
         base-with-first-dv (str base-9-digits first-check-digit)
         second-check-digit (i/calc-check-digit* base-with-first-dv i/second-check-digit-weight)]
     (str base-with-first-dv second-check-digit))))

(defn is-valid?
  "Validates a CPF string.

  Checks if the provided CPF is valid by verifying:
  - It is a string
  - It has exactly 11 digits (after cleaning)
  - It is not a reserved number (all repeated digits)
  - The check digits are correct according to the Brazilian algorithm

  Accepts both formatted (XXX.XXX.XXX-XX) and unformatted (XXXXXXXXXXX) CPFs.

  Args:
    cpf - CPF string to validate (formatted or unformatted)

  Returns:
    true if valid; false otherwise

  Examples:
    (is-valid? \"123.456.789-09\") ;; => true/false
    (is-valid? \"12345678909\")    ;; => true/false
    (is-valid? \"00000000000\")    ;; => false (reserved)
    (is-valid? \"12345678900\")    ;; => false (invalid check digit)
    (is-valid? nil)                 ;; => false"
  [cpf]
  (if-not (string? cpf)
    false
    (and (validation/is-valid-format? cpf)
         (let [cleaned (remove-symbols cpf)]
           (boolean
            (and (helpers/only-numbers cleaned)
                 (not (helpers/repeated-digits? cleaned))
                 (i/valid-checksum* cleaned)))))))