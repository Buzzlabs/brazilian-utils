(ns brazilian-utils.processo-juridico.core
  (:require [brazilian-utils.processo-juridico.internal :as i]
            [brazilian-utils.processo-juridico.validation :as validation]
            [brazilian-utils.processo-juridico.format :as fmt]
            [brazilian-utils.helpers :as helpers]))

(defn remove-symbols
  "Removes all non-digits from a court case number.

  Args:
    processo - Court case number string (formatted or not); nil allowed

  Returns:
    Digits-only string; nil yields an empty string

  Examples:
    (remove-symbols \"0002080-25.2012.515.0049\") ;; => \"00020802520125150049\"
    (remove-symbols nil)                           ;; => \"\""
  [processo]
  (helpers/only-numbers processo))

(defn format-processo
  "Formats a court case number with mask NNNNNNN-DD.AAAA.J.TT.OOOO.

  Args:
    processo - Court case number string (formatted or not)

  Returns:
    Masked string; partial input stays partial; extra digits are discarded

  Examples:
    (format-processo \"00020802520125150049\") ;; => \"0002080-25.2012.515.0049\"
    (format-processo \"0002080\")              ;; => \"0002080\"
    (format-processo \"\")                      ;; => \"\""
  [processo]
  (fmt/format-processo processo))

(defn is-valid?
  "Validates a court case number (20 digits, mod 97-10).

  Checks:
  - Input is a string
  - After removing symbols, length is 20 digits
  - Formatting is acceptable
  - Check digits (mod 97-10) are correct

  Args:
    processo - Court case number string (formatted or not)

  Returns:
    true when valid; false otherwise

  Examples:
    (is-valid? \"0002080-25.2012.515.0049\") ;; => true/false
    (is-valid? \"00020802520125150049\")     ;; => true/false
    (is-valid? nil)                            ;; => false"
  [processo]
  (if-not (string? processo)
    false
    (let [cleaned (remove-symbols processo)]
      (and (= (count cleaned) i/processo-length)
           (validation/is-valid-format? processo)
           (i/verify-digit* cleaned)))))
