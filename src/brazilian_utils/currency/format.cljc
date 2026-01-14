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
   (let [fixed-str (format-with-precision value precision)
         ;; Replace dot with comma for Brazilian format
         with-comma (str/replace fixed-str "." ",")
         ;; Split to get integer and decimal parts
         parts (str/split with-comma #",")
         integer-part (first parts)
         decimal-part (if (> (count parts) 1)
                        (second parts)
                        (apply str (repeat precision "0")))
         ;; Add thousands separator (dot) to integer part
         formatted-integer (str/replace integer-part
                                       validation/thousands-separator-pattern
                                       "$1.")]
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
