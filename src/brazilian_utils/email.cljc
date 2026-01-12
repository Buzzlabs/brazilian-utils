(ns brazilian-utils.email
  (:require [brazilian-utils.email.core :as core]))

;; Validation
(def is-valid? core/is-valid?)

;; Error reporting functions
(def validation-errors core/validation-errors)
