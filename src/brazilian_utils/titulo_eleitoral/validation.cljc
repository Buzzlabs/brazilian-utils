(ns brazilian-utils.titulo-eleitoral.validation
  "Validation schemas for Título Eleitoral."
  (:require [brazilian-utils.helpers :as helpers]
            [brazilian-utils.titulo-eleitoral.internal :as internal]))

(defn- validation-errors*
  "Returns a vector of error messages for a Título Eleitoral value."
  [voter-id]
  (let [cleaned (helpers/only-numbers voter-id)
        len-ok (= 12 (count cleaned))]
    (cond-> []
      (not (string? voter-id)) (conj "Voter ID must be a string")
      (not len-ok) (conj "Voter ID must have 12 digits after cleaning")
      (and len-ok (internal/all-same-digit? cleaned)) (conj "Voter ID cannot have all same digits")
      (and len-ok (not (internal/valid-uf-code? (subs cleaned 10 12)))) (conj "Invalid UF code")
      (and len-ok (not (internal/valid-check-digits? cleaned))) (conj "Invalid check digits"))))

(defn is-valid-voter-id?
  "Validates a Brazilian Voter ID (Título Eleitoral)."
  [voter-id]
  (empty? (validation-errors* voter-id)))

(defn explain-voter-id
  "Validates a Voter ID and returns detailed error information."
  [voter-id]
  (validation-errors* voter-id))
