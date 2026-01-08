(ns brazilian-utils.cep
  "API pública do módulo de CEP."
  (:require [brazilian-utils.cep.core :as core]))

(def is-valid? core/is-valid?)
(def format-cep core/format-cep)
