(ns brazilian-utils.processo-juridico.core
  (:require [brazilian-utils.processo-juridico.internal :as i]
            [brazilian-utils.processo-juridico.validation :as validation]
            [brazilian-utils.processo-juridico.format :as fmt]
            [brazilian-utils.helpers :as helpers]))

(defn clean
  "Removes all non-digits from a Processo Jurídico string."
  [processo]
  (helpers/only-numbers processo))

(defn format-processo
  "Formats a Processo Jurídico using the standard mask NNNNNNN-DD.AAAA.J.TT.OOOO.
   Partial inputs stay partial; extra digits are discarded."
  [processo]
  (fmt/format-processo processo))

(defn is-valid?
  "Validates a court case number (20 digits, mod 97-10).
   Input: formatted or unformatted string. Returns boolean."
  [processo]
  (if-not (string? processo)
    false
    (let [cleaned (clean processo)]
      (and (= (count cleaned) i/processo-length)
           (validation/is-valid-format? processo)
           (i/verify-digit* cleaned)))))
