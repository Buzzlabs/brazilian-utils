(ns brazilian-utils.cnh
  "Public API for CNH (Carteira Nacional de Habilitação) utilities."
  (:require [brazilian-utils.cnh.core :as core]))

(def is-valid? core/is-valid?)

(def remove-symbols core/remove-symbols)
