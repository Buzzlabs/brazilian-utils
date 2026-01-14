(ns brazilian-utils.currency.validation
  (:require [malli.core :as m]
            [clojure.string :as str]))

;; Regex to add thousands separator: matches digit followed by groups of 3 digits
(def thousands-separator-pattern #"(\d)(?=(\d{3})+(?!\d))")

(def BRLString
  "Schema for Brazilian currency format string.
   Accepts formats like: 1.234,56 or R$ 1.234,56 or 1234,56"
  [:re #"^[R$\s]*[\d.,]+$"])

(defn is-valid-format?
  "Checks if string is a valid Brazilian currency format. Nil/blank are treated as zero."
  [value]
  (cond
    (nil? value) true
    (and (string? value) (str/blank? value)) true
    (string? value) (m/validate BRLString value)
    :else false))
