(ns brazilian-utils.states
  (:require [brazilian-utils.states.core :as core]
            [brazilian-utils.states.schemas :as schemas]))

;; Validation
(def valid-uf? schemas/valid-uf?)

;; Lookups
(def uf->state-name core/uf->state-name)
(def uf->ie-length core/uf->ie-length)
(def uf->area-codes core/uf->area-codes)

;; Aggregations
(def all-ufs core/all-ufs)
(def all-state-names core/all-state-names)
