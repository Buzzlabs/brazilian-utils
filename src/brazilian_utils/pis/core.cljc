(ns brazilian-utils.pis.core
  (:require [brazilian-utils.pis.internal :as i]
            [brazilian-utils.pis.validation :as validation]
            [brazilian-utils.helpers :as helpers]))

(defn clean
  "Removes all non-numeric characters from a PIS.

  This function normalizes PIS input by removing formatting characters like dots
  and hyphens, returning only the digits.

  Args:
    pis - The PIS string to clean (may include formatting)

  Returns:
    A string containing only digits (0-9)

  Examples:
    (clean \"120.56874.10-7\") ;; => \"12056874107\"
    (clean \"12056874107\")     ;; => \"12056874107\"
    (clean \"\")                ;; => \"\""
  [pis]
  (helpers/only-numbers pis))

(defn is-valid?
  "Validates a PIS (Programa de Integração Social) number.

  Checks if the provided PIS is valid by verifying:
  - It is a string
  - It has exactly 11 digits (after cleaning)
  - It is not a reserved number (all repeated digits)
  - The check digit is correct according to the Brazilian PIS algorithm

  Accepts both formatted (XXX.XXXXX.XX-X) and unformatted (XXXXXXXXXXX) PIS.

  Args:
    pis - The PIS string to validate (formatted or unformatted)

  Returns:
    true if valid, false otherwise

  Examples:
    (is-valid? \"120.56874.10-7\") ;; => true or false
    (is-valid? \"12056874107\")     ;; => true or false
    (is-valid? \"00000000000\")     ;; => false (reserved)
    (is-valid? \"12345678901\")     ;; => false (invalid check digit)
    (is-valid? nil)                 ;; => false"
  [pis]
  (if-not (string? pis)
    false
    (and (validation/is-valid-format? pis)
         (let [cleaned (clean pis)]
           (and (not (helpers/repeated-digits? cleaned))
                (i/valid-checksum* cleaned))))))
