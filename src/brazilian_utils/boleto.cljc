(ns brazilian-utils.boleto
  "Brazilian boleto (payment slip) validation and utilities."
  (:require [brazilian-utils.boleto.core :as core]))

(def is-valid?
  "Validates a boleto digitable line (47 or 48 digits)."
  core/is-valid?)

(def boleto-bancario?
  "Checks if boleto is bancário type (47 digits)."
  core/boleto-bancario?)

(def boleto-arrecadacao?
  "Checks if boleto is arrecadação type (48 digits)."
  core/boleto-arrecadacao?)

(def parse-boleto
  "Parses a bank boleto and returns structured information."
  core/parse-boleto)

(def barcode->linha-digitavel
  "Converts a 44-digit barcode to 47-digit digitable line."
  core/barcode->linha-digitavel)

(def format-linha-digitavel
  "Formats boleto with dots and spaces."
  core/format-linha-digitavel)
