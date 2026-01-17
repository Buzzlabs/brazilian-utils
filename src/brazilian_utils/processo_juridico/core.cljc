(ns brazilian-utils.processo-juridico.core
  (:require [brazilian-utils.processo-juridico.internal :as i]
            [brazilian-utils.processo-juridico.validation :as validation]
            [brazilian-utils.processo-juridico.format :as fmt]
            [brazilian-utils.helpers :as helpers]))

(defn remove-symbols
  "Removes all non-numeric characters from a court case number.

  This function normalizes court case number input by stripping formatting characters,
  returning only the digits.

  Arguments:
    processo - Court case number string (formatted or unformatted); nil allowed

  Returns:
    Digits-only string; nil yields an empty string

  Examples:
    (remove-symbols \"0002080-25.2012.515.0049\") ;; => \"00020802520125150049\"
    (remove-symbols \"00020802520125150049\")     ;; => \"00020802520125150049\"
    (remove-symbols nil)                           ;; => \"\"
    (remove-symbols \"\")                        ;; => \"\""
  [processo]
  (helpers/only-numbers processo))

(defn format-processo
  "Formats a court case number with the standard judicial mask (NNNNNNN-DD.AAAA.J.TT.OOOO).

  This function takes a 20-digit court case number and formats it according to the
  Brazilian judicial system standard. It supports partial input (preserving the number
  of digits) and discards extra digits beyond 20.

  Arguments:
    processo - Court case number string (formatted or unformatted)

  Returns:
    Formatted string with the mask; partial input stays partial; extra digits are discarded

  Examples:
    (format-processo \"00020802520125150049\") ;; => \"0002080-25.2012.515.0049\"
    (format-processo \"0002080\")              ;; => \"0002080\"
    (format-processo \"\")                      ;; => \"\"
    (format-processo \"0002080-25.2012.515.0049\") ;; => \"0002080-25.2012.515.0049\""
  [processo]
  (fmt/format-processo processo))

(defn is-valid?
  "Validates a Brazilian court case number (processo judicial).

  This function checks if a court case number is valid by verifying:
  - It is a string
  - Contains exactly 20 digits (after removing formatting)
  - The formatting follows the standard judicial mask (NNNNNNN-DD.AAAA.J.TT.OOOO)
  - The check digits (mod 97-10 algorithm) are correct

  Accepts both formatted (NNNNNNN-DD.AAAA.J.TT.OOOO) and unformatted (20 digits) court case numbers.

  Arguments:
    processo - Court case number string (formatted or unformatted)

  Returns:
    true if valid; false otherwise

  Examples:
    (is-valid? \"0002080-25.2012.515.0049\") ;; true/false depending on check digits
    (is-valid? \"00020802520125150049\")     ;; true/false depending on check digits
    (is-valid? \"0002080\")                  ;; false (incomplete)
    (is-valid? nil)                            ;; false"
  [processo]
  (if-not (string? processo)
    false
    (let [cleaned (remove-symbols processo)]
      (and (= (count cleaned) i/processo-length)
           (validation/is-valid-format? processo)
           (i/verify-digit* cleaned)))))
