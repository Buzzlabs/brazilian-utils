(ns brazilian-utils.cnh.validation
  "Validation schema for CNH (Carteira Nacional de Habilitação)."
  (:require [malli.core :as m]
            [brazilian-utils.helpers :as helpers]
            [brazilian-utils.cnh.internal :as internal]))

(def CNH
  [:and
   [:string {:min 1}]
   [:fn {:error/message "CNH must have 11 digits after cleaning"}
    (fn [s]
      (let [cleaned (helpers/only-numbers s)]
        (= internal/cnh-length (count cleaned))))]
   [:fn {:error/message "CNH cannot have all same digits"}
    (fn [s]
      (let [cleaned (helpers/only-numbers s)]
        (not (helpers/repeated-digits? cleaned internal/cnh-length))))]
   [:fn {:error/message "Invalid CNH check digits"}
    (fn [s]
      (let [cleaned (helpers/only-numbers s)]
        (internal/valid-check-digits? cleaned)))]] )

(defn is-valid?
  "Returns true if CNH is valid."
  [cnh]
  (m/validate CNH cnh))
