(ns brazilian-utils.states.core
  (:require [brazilian-utils.data :as data]
            [brazilian-utils.states.validation :as validation]
            [clojure.string :as str]))

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
  "Returns the numeric IBGE code for a given UF.
   
   The IBGE code is a numeric identifier used by the Brazilian Institute of Geography and Statistics
   to uniquely identify each state.
   
   Arguments:
   - uf: Keyword representing the state abbreviation (e.g., :SP, :RJ)
   
   Returns:
    An integer representing the IBGE code, or nil if the UF is invalid.
    
   Examples:
    (uf->code :SP) ;; 35
    (uf->code :RJ) ;; 33
    (uf->code :XX) ;; nil"
  [uf]
  (when (validation/valid-uf? uf)
    (get-in data/states-map [uf :code])))

(defn code->uf
  "Returns the UF keyword for a given IBGE state code.
   
   Arguments:
   - code: String or integer representing the IBGE state code
   
   Returns a keyword with the UF or nil if not found.
   
   Example:
   (code->uf \"8\") ;; :SP
   (code->uf 8) ;; :SP
   (code->uf \"3\") ;; :CE (first match if multiple states have same code)"
  [code]
  (let [code-str (str code)]
    (->> data/states-map
         (filter (fn [[_ v]] (= (:code v) code-str)))
         first
         first)))

(defn name->uf
  "Returns the UF keyword for a given state name.
   
   Arguments:
   - name: String with the full or partial state name (case-insensitive)
   
   Returns a keyword with the UF or nil if not found.
   
   Example:
   (name->uf \"São Paulo\") ;; :SP
   (name->uf \"são paulo\") ;; :SP
   (name->uf \"Rio de Janeiro\") ;; :RJ"
  [name]
  (when (string? name)
    (let [name-lower (str/lower-case name)]
      (->> data/states-map
           (filter (fn [[_ v]] (= (str/lower-case (:name v)) name-lower)))
           first
           first))))
