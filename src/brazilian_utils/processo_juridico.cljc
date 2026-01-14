(ns brazilian-utils.processo-juridico
  "Brazilian Processo Jurídico (court case) validation and utilities."
  (:require [brazilian-utils.processo-juridico.core :as core]))

(def is-valid?
  "Validates a Processo Jurídico number (20 digits)."
  core/is-valid?)

(def remove-symbols
  "Removes formatting from a Processo Jurídico string."
  core/remove-symbols)

(def format-processo
  "Formats a Processo Jurídico with standard Brazilian court case format."
  core/format-processo)
