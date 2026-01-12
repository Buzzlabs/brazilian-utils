(ns brazilian-utils.phone
  (:require [brazilian-utils.phone.core :as core]))

;; Validation
(def is-valid? core/is-valid?)
(def is-valid-mobile? core/is-valid-mobile?)
(def is-valid-landline? core/is-valid-landline?)

;; Error reporting functions
(def validation-errors core/validation-errors)
(def mobile-validation-errors core/mobile-validation-errors)
(def landline-validation-errors core/landline-validation-errors)
