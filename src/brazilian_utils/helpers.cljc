(ns brazilian-utils.helpers
  "Shared helper functions used across brazilian-utils modules.
  
  Provides common utility functions for string manipulation and validation
  used by other modules in the library.
  
  Functions:
    - only-numbers: Extract digits from strings
    - repeated-digits?: Check if string has all same digits
    - char->digit: Convert digit character to numeric value"
  (:require [clojure.string :as str]))

(defn only-numbers
  "Removes all non-numeric characters from a string.
   
   Arguments:
   - value: Input to extract only numbers from
   
   Returns a string containing only digits (0-9).
   
   Example:
   (only-numbers \"123.456.789-00\") ;; \"12345678900\"
   (only-numbers \"01310-100\") ;; \"01310100\"
   (only-numbers nil) ;; \"\""
  [value]
  (-> (or value "")
      str
      (str/replace #"[^0-9]" "")))

(defn repeated-digits?
  "Checks if a string contains only repeated digits of a specified length.
   
   Arguments:
   - value: Input to check (will be normalized to remove non-digits)
   - length: Expected length of digits (optional, defaults to any length >= 1)
   
   Returns true if all digits are the same, false otherwise.
   
   Example:
   (repeated-digits? \"00000000\" 8)  ;; true  
   (repeated-digits? \"00000-000\" 8) ;; true
   (repeated-digits? \"11111111\")    ;; true (any length)
   (repeated-digits? \"01310100\")    ;; false
   (repeated-digits? \"000\" 8)       ;; false (wrong length)"
  ([text]
   (repeated-digits? text nil))
  ([text expected-length]
   (if (string? text)
     (let [clean-digits (only-numbers text)
           digit-count (count clean-digits)]
       (and (pos? digit-count)
            (or (not expected-length) (= expected-length digit-count))
            (empty? (rest (distinct clean-digits)))))
     false)))

(defn char->digit
  "Converts a character digit (0-9) to its numeric value.
   
   Arguments:
   - c: A character or single-character string
   
   Returns the numeric value (0-9) as an integer.
   
   Example:
   (char->digit \\\"5\\\") ;; => 5
   (char->digit \\\\5) ;; => 5"
  [c]
  (let [c (first (str c))]
    (- (int c) 48)))