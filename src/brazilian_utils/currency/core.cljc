(ns brazilian-utils.currency.core
  (:require [brazilian-utils.currency.format :as fmt]))

(defn format-currency
  "Formats a number as BRL (uses comma decimal and dot thousand).

   Inputs:
   - value: number (int/float/decimal)
   - opts-or-precision: either integer precision or {:precision n}

  Output: string like \"1.234,56\"."
  ([value]
   (fmt/format-currency value))
  ([value opts-or-precision]
   (if (map? opts-or-precision)
     (fmt/format-currency value (:precision opts-or-precision 2))
     (fmt/format-currency value opts-or-precision))))

(defn parse
  "Parses a BRL-formatted string into a number (double).

  Accepts input with or without \"R$\" and separators. Nil/blank returns 0.0.
  Example: \"R$ 1.234,56\" => 1234.56"
  [value]
  (fmt/parse-currency value))
