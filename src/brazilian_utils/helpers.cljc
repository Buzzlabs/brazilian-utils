(ns brazilian-utils.helpers
  "Shared helper functions used across brazilian-utils modules."
  (:require [clojure.string :as str]))

(defn only-numbers
  "Removes all non-numeric characters from a string.
   
   Arguments:
   - text: String to extract only numbers from
   
   Returns a string containing only digits (0-9).
   
   Example:
   (only-numbers \"123.456.789-00\") ;; \"12345678900\"
   (only-numbers \"01310-100\") ;; \"01310100\"
   (only-numbers nil) ;; \"\""
  [text]
  (if (string? text)
    (str/replace text #"[^0-9]" "")
    ""))
