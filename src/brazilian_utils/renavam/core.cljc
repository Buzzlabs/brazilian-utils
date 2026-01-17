(ns brazilian-utils.renavam.core
  "Core utilities for RENAVAM validation."
  (:require [brazilian-utils.helpers :as helpers]
            [brazilian-utils.renavam.validation :as validation]))

(defn is-valid?
  "Validates a RENAVAM number.

  Input: string with 9 or 11 digits (formatted or not); non-strings return false.
  Output: true when length and check digit rules pass; otherwise false."
  [renavam]
  (validation/is-valid? renavam))



(defn remove-symbols
  "Removes non-digit characters from RENAVAM.

  Input: string or nil (formatted or not).
  Output: digits-only string; nil yields an empty string."
  [renavam]
  (if (string? renavam)
    (helpers/only-numbers renavam)
    ""))
