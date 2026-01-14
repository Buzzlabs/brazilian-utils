(ns brazilian-utils.boleto.format
  "Formatting functions for boleto digitable lines.
   
   Provides visual formatting for bank boletos (47 digits) by inserting
   dots and spaces at standard positions."
  (:require [brazilian-utils.helpers :as helpers]))

(defn format-linha-digitavel
  "Formats a 47-digit bank boleto by inserting dots and spaces at standard positions.

  Field layout (47 digits):
  - Field 1: 9 digits + DV → 10 (formatted as XXXXX.XXXXX)
  - Field 2: 10 digits + DV → 11 (formatted as XXXXX.XXXXXX)
  - Field 3: 10 digits + DV → 11 (formatted as XXXXX.XXXXXX)
  - Field 4: General DV → 1 (single digit)
  - Field 5: Due date + value → 14 (unformatted)

  Args:
    boleto - String containing boleto digits (may include existing formatting)

  Returns:
    Formatted string with dots and spaces, or empty string for blank input

  Examples:
    (format-linha-digitavel \"23790000000000000000000000000000000000000000000\")
    ;; => \"23790.00000 00000.000000 00000.000000 0 00000000000000\""
  [boleto]
  (let [normalized (helpers/only-numbers boleto)
        digits (cond
                 (= 47 (count normalized)) (str (subs normalized 0 38)
                                              (subs normalized 39))
                 :else normalized)
        digits (if (> (count digits) 46) (subs digits 0 46) digits)
        [p1a p1b p2a p2b p3a p3b dv geral]
        (map #(or (helpers/slice-safe digits (first %) (second %)) "")
             [[0 4] [4 9] [9 14] [14 20] [20 25] [25 31] [31 32] [32 46]])]
    (if (empty? digits)
      ""
      (str p1a
           (when (seq p1b) (str "." p1b))
           (when (seq p2a) (str " " p2a))
           (when (seq p2b) (str "." p2b))
           (when (seq p3a) (str " " p3a))
           (when (seq p3b) (str "." p3b))
           (when (seq dv)  (str " " dv))
           (when (seq geral) (str " " geral))))))
