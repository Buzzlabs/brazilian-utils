(ns brazilian-utils.states.core
  (:require [brazilian-utils.data :as data]
            [brazilian-utils.states.validation :as validation]))

(defn uf->state-name
  "Returns the full state name for a UF keyword.
   
   Arguments:
   - uf: Keyword representing the state abbreviation
   
   Returns a string with the state name or nil if not found.
   
   Example:
   (uf->state-name :SP) ;; \"São Paulo\"
   (uf->state-name :RJ) ;; \"Rio de Janeiro\"
   (uf->state-name :XX) ;; nil"
  [uf]
  (when (validation/valid-uf? uf)
    (get-in data/states-map [uf :name])))

(defn uf->ie-length
  "Returns the expected length of State Registration (IE) for a UF.
   
   Some states accept multiple lengths, in this case returns a vector.
   
   Arguments:
   - uf: Keyword representing the state abbreviation
   
   Returns an integer or vector of integers, or nil if not found.
   
   Example:
   (uf->ie-length :SP) ;; 12
   (uf->ie-length :BA) ;; [8 9]
   (uf->ie-length :XX) ;; nil"
  [uf]
  (when (validation/valid-uf? uf)
    (get-in data/states-map [uf :ie-length])))

(defn uf->area-codes
  "Returns the telephone area codes (DDDs) for a UF.
   
   Arguments:
   - uf: Keyword representing the state abbreviation
   
   Returns a vector of integers or nil if not found.
   
   Example:
   (uf->area-codes :SP) ;; [11 12 13 14 15 16 17 18 19]
   (uf->area-codes :RJ) ;; [21 22 24]
   (uf->area-codes :XX) ;; nil"
  [uf]
  (when (validation/valid-uf? uf)
    (get-in data/states-map [uf :area-codes])))

(defn uf->code
  "Returns the code for a UF.
   
   Arguments:
   - uf: Keyword representing the state abbreviation
   
   Returns aintegers or nil if not found."
  [uf]
  (when (validation/valid-uf? uf)
    (get-in data/states-map [uf :code])))