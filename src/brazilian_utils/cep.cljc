(ns brazilian-utils.cep
  "Public API for the CEP module."
  (:require [brazilian-utils.cep.core :as core]))

(def remove-symbols core/remove-symbols)
(def format-cep core/format-cep)

;; Validation
(def is-valid? core/is-valid?)

;; ViaCEP API Integration
(def get-address-from-cep core/get-address-from-cep)
(def get-cep-information-from-address core/get-cep-information-from-address)