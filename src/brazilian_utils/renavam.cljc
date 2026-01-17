(ns brazilian-utils.renavam
  "Public API for RENAVAM utilities."
  (:require [brazilian-utils.renavam.core :as core]))

(def is-valid? core/is-valid?)

(def remove-symbols core/remove-symbols)
