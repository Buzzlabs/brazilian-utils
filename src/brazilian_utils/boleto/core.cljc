(ns brazilian-utils.boleto.core
  "Core public API for boleto validation and parsing.
   
   This is the main entry point for boleto functionality. Provides validation,
   type checking, and data extraction for Brazilian payment slips (boletos)."
  (:require [brazilian-utils.boleto.validation :as validation]
            [brazilian-utils.boleto.parser :as parser]
            [brazilian-utils.boleto.format :as format]))

;; Validation
(def is-valid? validation/is-valid?)

;; Type checking
(def boleto-bancario? validation/boleto-bancario?)
(def boleto-arrecadacao? validation/boleto-arrecadacao?)

;; Parsing and conversion
(def barcode->linha-digitavel parser/barcode->linha-digitavel)
(def parse-boleto parser/parse-boleto)

;; Formatting
(def format-linha-digitavel format/format-linha-digitavel)