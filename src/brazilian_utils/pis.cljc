(ns brazilian-utils.pis
  "Brazilian PIS (Programa de Integração Social) validation and utilities."
  (:require [brazilian-utils.pis.core :as core]))

(def is-valid?
  "Validates a PIS number (11 digits)."
  core/is-valid?)

(def clean
  "Removes formatting from a PIS string."
  core/clean)
