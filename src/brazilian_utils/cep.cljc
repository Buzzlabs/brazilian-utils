(ns brazilian-utils.cep
  "Public API for the CEP module."
  (:require [brazilian-utils.cep.core :as core]))

(def is-valid? core/is-valid?)
(def format-cep core/format-cep)
