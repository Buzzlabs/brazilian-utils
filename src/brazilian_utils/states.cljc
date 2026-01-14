(ns brazilian-utils.states
  (:require [brazilian-utils.states.core :as core]
            [brazilian-utils.states.validation :as validation]
            [brazilian-utils.states.internal :as internal]))

;; Validation
(def valid-uf? validation/valid-uf?)

;; Lookups
(def uf->state-name core/uf->state-name)
(def uf->ie-length core/uf->ie-length)
(def uf->area-codes core/uf->area-codes)
(def uf->code core/uf->code)

;; Aggregations
(def all-ufs internal/all-ufs)
(def all-state-names internal/all-state-names)

(def code->uf core/code->uf)
(def name->uf core/name->uf)
