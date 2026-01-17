(ns brazilian-utils.date
  "Public API for the date module."
  (:require [brazilian-utils.date.core :as core]))

;; Holiday functions
(def get-holidays core/get-holidays)
(def is-holiday? core/is-holiday?)
(def get-holiday-name core/get-holiday-name)

;; Date validation and conversion
(def valid-date-format? core/valid-date-format?)
(def valid-brazilian-date-format? core/valid-brazilian-date-format?)
(def normalize-date core/normalize-date)
(def brazilian->iso-date core/brazilian->iso-date)
(def iso->brazilian-date core/iso->brazilian-date)
