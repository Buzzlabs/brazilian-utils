(ns brazilian-utils.states.internal
  (:require [brazilian-utils.data :as data]))

;; Returns all valid UF keywords in alphabetical order.
(defn all-ufs
	[]
	(vec (sort (keys data/states-map))))

;; Returns all state names in alphabetical order.
(defn all-state-names
	[]
	(->> data/states-map
			 vals
			 (map :name)
			 sort))
