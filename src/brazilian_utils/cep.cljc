(ns brazilian-utils.cep
  "Public API for the CEP module."
  (:require [brazilian-utils.cep.core :as core]))

(def clean core/clean)
(def format-cep core/format-cep)

;; Validation
(def is-valid? core/is-valid?)