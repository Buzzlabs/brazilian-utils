(ns brazilian-utils.currency
  "Brazilian currency (BRL) formatting and parsing utilities."
  (:require [brazilian-utils.currency.core :as core]))

(def format-currency
  "Formats a number as Brazilian currency string (BRL)."
  core/format-currency)

(def parse
  "Parses a Brazilian currency string into a number."
  core/parse)
