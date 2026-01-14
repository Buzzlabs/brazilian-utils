(ns brazilian-utils.cep
  "Public API for the CEP module."
  (:require [brazilian-utils.cep.core :as core]))

(def remove-symbols core/remove-symbols)
(def format-cep core/format-cep)

;; Validation
(def is-valid? core/is-valid?)