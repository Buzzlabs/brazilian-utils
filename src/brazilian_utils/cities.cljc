(ns brazilian-utils.cities
  (:require [brazilian-utils.cities.core :as core]))

;; Lookups
(def cities-of core/cities-of)
(def cities-of! core/cities-of!)

;; Aggregations
(def all-cities core/all-cities)
(def all-city-names core/all-city-names)

(def find-city-by-name core/find-city-by-name)
(def city-exists? core/city-exists?)
