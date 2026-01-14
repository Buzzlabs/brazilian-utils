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
     (char->digit \" 5 \") ;; => 5
     (char->digit \\5) ;; => 5"
  [c]
  (let [c (first (str c))
        code #?(:clj (int c)
                :cljs (.charCodeAt c 0))]
    (- code 48)))

(defn weighted-sum
  "Computes the weighted sum of digit characters.

    digits    - string/seq of characters to convert
    weights   - either a descending start weight number (e.g., 10) or a seq of weights
    char->val - optional fn to convert each char to numeric value (defaults to char->digit)

    Returns the integer sum or nil if weight count does not match digit count."
  ([digits weights]
   (weighted-sum digits weights char->digit))
  ([digits weights char->val]
   (let [ds (seq (or digits ""))
         ws (if (number? weights)
              (map #(- weights %) (range (count ds)))
              weights)]
     (when (= (count ds) (count ws))
       (reduce + (map * (map char->val ds) ws))))))

(defn mod11-check-digit
  "Computes a mod-11 check digit using weighted sum.

   digits     - string/seq of characters to convert
   weights    - descending start weight number or a seq of weights
   options    - {:char->val fn, :threshold n, :stringify? bool}
                threshold default is 2 (remainder < threshold -> 0)
                stringify? when true returns the digit as string

   Returns the digit (int or string) or nil if weight count does not match."  
  ([digits weights]
   (mod11-check-digit digits weights {}))
  ([digits weights {:keys [char->val threshold stringify?]
                    :or {char->val char->digit
                         threshold 2
                         stringify? false}}]
   (when-let [sum (weighted-sum digits weights char->val)]
     (let [remainder (mod sum 11)
           digit (if (< remainder threshold) 0 (- 11 remainder))]
       (if stringify? (str digit) digit)))))

(defn slice-safe
  "Safe substring that handles bounds correctly.
   
   Returns substring from start to end, handling cases where indices
   exceed string length. Returns nil if start is beyond string length.
   
   Arguments:
   - s: String to slice
   - start: Start index (inclusive)
   - end: End index (exclusive)
   
   Returns substring or nil if start exceeds string length.
   
   Example:
   (slice-safe \"hello\" 0 3) ;; \"hel\"
   (slice-safe \"hello\" 2 10) ;; \"llo\" (handles overflow)
   (slice-safe \"hello\" 10 15) ;; nil (start beyond length)"
  [s start end]
  (when (< start (count s))
    (subs s start (min end (count s)))))