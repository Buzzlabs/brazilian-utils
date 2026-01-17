(ns brazilian-utils.titulo-eleitoral
  "Public API for Título Eleitoral (Voter ID) utilities."
  (:require [brazilian-utils.titulo-eleitoral.core :as core]))

;; Validation
(def is-valid? core/is-valid?)
(def validation-errors core/validation-errors)
(def remove-symbols core/remove-symbols)

;; Utilities
(def get-uf-code core/get-uf-code)
(def generate core/generate)
