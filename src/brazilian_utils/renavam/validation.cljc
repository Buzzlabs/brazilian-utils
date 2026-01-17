(ns brazilian-utils.renavam.validation
  "Validation schema for RENAVAM."
  (:require [brazilian-utils.helpers :as helpers]
            [brazilian-utils.renavam.internal :as internal]))

(defn- validation-errors*
  "Returns a vector of error messages for a RENAVAM value."
  [renavam]
  (let [cleaned (helpers/only-numbers renavam)
        normalized (internal/normalize-length cleaned)
        len-ok (some? normalized)]
    (cond-> []
      (not (string? renavam)) (conj "RENAVAM must be a string")
      (not len-ok) (conj "RENAVAM must have 9 or 11 digits after cleaning")
      (and len-ok (helpers/repeated-digits? normalized internal/renavam-length)) (conj "RENAVAM cannot have all same digits")
      (and len-ok (not (internal/valid-check-digit? normalized))) (conj "Invalid RENAVAM check digit"))))

(defn is-valid?
  "Returns true if RENAVAM is valid."
  [renavam]
  (empty? (validation-errors* renavam)))
