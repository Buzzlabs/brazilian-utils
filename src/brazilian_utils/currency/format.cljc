(ns brazilian-utils.currency.format
  "Formatting functions for Brazilian currency (BRL)."
  (:require [clojure.string :as str]
            [brazilian-utils.currency.validation :as validation])
  #?(:clj (:import [java.lang Double])))

(defn- format-with-precision
  "Formats a number to fixed decimal places.
   Uses platform-specific formatting: DecimalFormat (CLJ) or toFixed (CLJS)."
  [value precision]
  #?(:clj
     (let [formatter (java.text.DecimalFormat. (str "0." (apply str (repeat precision "0"))))]
       (.format formatter (double value)))
     :cljs
     (.toFixed value precision)))

(defn- convert-to-brazilian-decimal
  "Converts dot decimal separator to comma (Brazilian format).
  
  Args:
    s - String with dot as decimal separator
    
  Returns:
    String with comma as decimal separator
    
  Example:
    (convert-to-brazilian-decimal \"1234.56\") ;; => \"1234,56\""
  [s]
  (str/replace s "." ","))

(defn- split-integer-and-decimal
  "Splits a number string into integer and decimal parts.
  
  Args:
    s - String with comma as decimal separator
    precision - Number of decimal places expected
    
  Returns:
    Vector [integer-part decimal-part]
    
  Example:
    (split-integer-and-decimal \"1234,56\" 2) ;; => [\"1234\" \"56\"]
    (split-integer-and-decimal \"1234\" 2)    ;; => [\"1234\" \"00\"]"
  [s precision]
  (let [parts (str/split s #",")
        integer-part (first parts)
        decimal-part (if (> (count parts) 1)
                       (second parts)
                       (apply str (repeat precision "0")))]
    [integer-part decimal-part]))

(defn- add-thousands-separators
  "Adds dot separators for thousands in the integer part.
  
  Args:
    integer-str - String with the integer part
    
  Returns:
    String with dots as thousands separators
    
  Example:
    (add-thousands-separators \"1234567\") ;; => \"1.234.567\""
  [integer-str]
  (str/replace integer-str
               validation/thousands-separator-pattern
               "$1."))

(defn format-currency
  "Formats a number as Brazilian currency string (BRL).
   
   Converts number to fixed decimal places, uses comma as decimal separator
   and dot as thousands separator (Brazilian format).

  Args:
    value - The number to be formatted
    precision - Number of decimal places (default: 2)

  Returns:
    The formatted currency string

  Examples:
    (format-currency 1234.56)     ;; => \"1.234,56\"
    (format-currency 1234.56 3)   ;; => \"1.234,560\"
    (format-currency 0.5)         ;; => \"0,50\"
    (format-currency 1000000.5)   ;; => \"1.000.000,50\""
  ([value]
   (format-currency value 2))
  ([value precision]
   (let [number-str (format-with-precision value precision)
         with-brazilian-decimal (convert-to-brazilian-decimal number-str)
         [integer-part decimal-part] (split-integer-and-decimal with-brazilian-decimal precision)
         formatted-integer (add-thousands-separators integer-part)]
     (str formatted-integer "," decimal-part))))

(defn parse-currency
  "Parses a string representing Brazilian currency format into a number.
   
   Removes all non-digit characters and converts to decimal number,
   assuming the last 2 digits are decimals.

  Args:
    value - The string value to be parsed (e.g., \"R$ 1.234,56\" or \"1234,56\")

  Returns:
    The parsed number value (e.g., 1234.56), or nil for invalid input

  Examples:
    (parse-currency \"R$ 1.234,56\")  ;; => 1234.56
    (parse-currency \"1234,56\")       ;; => 1234.56
    (parse-currency \"R$ 0,50\")       ;; => 0.5
    (parse-currency \"\")              ;; => 0.0
    (parse-currency \"1000\")          ;; => 10.0"
  [value]
  (when (validation/is-valid-format? value)
    (let [digits (str/replace (or value "") #"\D" "")]
      (if (str/blank? digits)
        0.0
        (/ #?(:clj (Double/parseDouble digits)
              :cljs (js/parseFloat digits))
           100.0)))))
