(ns brazilian-utils.pis.core
  (:require [brazilian-utils.pis.internal :as i]
            [brazilian-utils.pis.validation :as validation]
            [brazilian-utils.helpers :as helpers]))

(defn remove-symbols
  "Removes all non-numeric characters from a PIS.

  Args:
    pis - PIS string (formatted or unformatted); nil allowed

  Returns:
    Digits-only string; nil becomes an empty string

  Examples:
    (remove-symbols \"120.56874.10-7\") ;; => \"12056874107\"
    (remove-symbols \"12056874107\")    ;; => \"12056874107\"
    (remove-symbols nil)                  ;; => \"\"
    (remove-symbols \"\")              ;; => \"\""
  [pis]
  (helpers/only-numbers pis))

(defn is-valid?
  "Validates a PIS (Programa de Integração Social) number.

  Checks:
  - Input is a string
  - Exactly 11 digits after removing formatting
  - Not all digits repeated
  - Check digit matches PIS algorithm

  Accepts formatted (XXX.XXXXX.XX-X) or unformatted (XXXXXXXXXXX).

  Args:
    pis - PIS string to validate (formatted or not)

  Returns:
    true when valid; false otherwise

  Examples:
    (is-valid? \"120.56874.10-7\") ;; => true/false
    (is-valid? \"12056874107\")    ;; => true/false
    (is-valid? \"00000000000\")    ;; => false
    (is-valid? \"12345678901\")    ;; => false
    (is-valid? nil)                 ;; => false"
  [pis]
  (if-not (string? pis)
    false
    (and (validation/is-valid-format? pis)
         (let [cleaned (remove-symbols pis)]
           (and (not (helpers/repeated-digits? cleaned))
                (i/valid-checksum* cleaned))))))
