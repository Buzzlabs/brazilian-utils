(ns brazilian-utils.inscricao-estadual.internal
  "Internal validation logic for Brazilian state registration (IE).
   Coordinates validation by state using modular validators."
  (:require [clojure.string :as str]
            [brazilian-utils.helpers :as helpers]
            [brazilian-utils.inscricao-estadual.validators.sp :as sp]
            [brazilian-utils.inscricao-estadual.validators.rj :as rj]
            [brazilian-utils.inscricao-estadual.validators.mg :as mg]
            [brazilian-utils.inscricao-estadual.validators.ba :as ba]
            [brazilian-utils.inscricao-estadual.validators.to :as to]
            [brazilian-utils.inscricao-estadual.validators.rn :as rn]
            [brazilian-utils.inscricao-estadual.validators.ap :as ap]
            [brazilian-utils.inscricao-estadual.validators.pr :as pr]
            [brazilian-utils.inscricao-estadual.validators.go :as go]
            [brazilian-utils.inscricao-estadual.validators.al :as al]
            [brazilian-utils.inscricao-estadual.validators.ac :as ac]
            [brazilian-utils.inscricao-estadual.validators.mt :as mt]
            [brazilian-utils.inscricao-estadual.validators.rs :as rs]
            [brazilian-utils.inscricao-estadual.validators.pe :as pe]
            [brazilian-utils.inscricao-estadual.validators.ro :as ro]
            [brazilian-utils.inscricao-estadual.validators.ma :as ma]
            [brazilian-utils.inscricao-estadual.validators.pb :as pb]
            [brazilian-utils.inscricao-estadual.validators.pa :as pa]
            [brazilian-utils.inscricao-estadual.validators.df :as df]
            [brazilian-utils.inscricao-estadual.validators.ms :as ms]
            [brazilian-utils.inscricao-estadual.validators.am :as am]
            [brazilian-utils.inscricao-estadual.validators.ce :as ce]
            [brazilian-utils.inscricao-estadual.validators.pi :as pi]
            [brazilian-utils.inscricao-estadual.validators.sc :as sc]
            [brazilian-utils.inscricao-estadual.validators.se :as se]
            [brazilian-utils.inscricao-estadual.validators.es :as es]
            [brazilian-utils.inscricao-estadual.validators.rr :as rr]))

;; ============================================================================
;; IE Length Validation
;; ============================================================================

(def ie-length-by-state
  "Map of state UF to expected IE length(s).
   Some states accept multiple lengths (represented as vectors)."
  {:AC 13, :AL 9,  :AP 9,  :AM 9,  :BA [8 9], :CE 9, :DF 13, :ES 9,
   :GO 9,  :MA 9,  :MG 13, :MS 9,  :MT 11, :PA 9,  :PB 9,  :PE 9,
   :PI 9,  :PR 10, :RJ 8,  :RN [9 10], :RO 14, :RR 9,  :RS 10, :SC 9,
   :SE 9,  :SP 12, :TO [9 11]})

(defn valid-ie-length?
  "Checks if IE has valid length for the given state.
   
   Arguments:
   - uf: State keyword (e.g., :SP, :RJ)
   - ie-str: IE string (will be cleaned to only numbers)
   
   Returns true if length matches expected for the state, false otherwise.
   
   Example:
   (valid-ie-length? :SP \"110042490114\") ;; true (12 digits)
   (valid-ie-length? :RJ \"62545372\") ;; true (8 digits)
   (valid-ie-length? :TO \"294467696\") ;; true (9 digits, TO accepts 9 or 11)"
  [uf ie-str]
  (let [ie (helpers/only-numbers ie-str)
        expected-len (get ie-length-by-state uf)]
    (if expected-len
      (if (vector? expected-len)
        (contains? (set expected-len) (count ie))
        (= expected-len (count ie)))
      false)))

(defn validate-ie-length
  "Validates IE length for a state and returns result with error message.
   
   Arguments:
   - uf: State keyword (e.g., :SP, :RJ)
   - ie-str: IE string (will be cleaned to only numbers)
   
   Returns a map with:
   - :valid? - boolean indicating if length is valid
   - :error - error message string (only present if invalid)
   
   Example:
   (validate-ie-length :SP \"110042490114\") 
   ;; => {:valid? true}
   
   (validate-ie-length :SP \"1234\")
   ;; => {:valid? false, :error \"Invalid length: expected 12 digits, got 4\"}"
  [uf ie-str]
  (let [ie (helpers/only-numbers ie-str)
        actual-len (count ie)
        expected-len (get ie-length-by-state uf)]
    (cond
      (not expected-len)
      {:valid? false, :error (str "Unknown state: " (name uf))}
      
      (vector? expected-len)
      (if (contains? (set expected-len) actual-len)
        {:valid? true}
        {:valid? false
         :error (str "Invalid length: expected " 
                     (str/join " or " expected-len)
                     " digits, got " actual-len)})
      
      :else
      (if (= expected-len actual-len)
        {:valid? true}
        {:valid? false
         :error (str "Invalid length: expected " expected-len 
                     " digits, got " actual-len)}))))

;; ============================================================================
;; Generic state validator dispatch
;; ============================================================================

(defn validate-ie-by-state
  "Validates IE for a specific state.
   Returns true if valid, false otherwise."
  [uf ie-str]
  (let [ie (helpers/only-numbers ie-str)]
    (if (or (empty? ie)
            (every? #(= \0 %) ie))
      false
      (case uf
        :SP (sp/is-valid? ie)
        :RJ (rj/is-valid? ie)
        :MG (mg/is-valid? ie)
        :BA (ba/is-valid? ie)
        :TO (to/is-valid? ie)
        :RN (rn/is-valid? ie)
        :AP (ap/is-valid? ie)
        :PR (pr/is-valid? ie)
        :GO (go/is-valid? ie)
        :AL (al/is-valid? ie)
        :AC (ac/is-valid? ie)
        :MT (mt/is-valid? ie)
        :RS (rs/is-valid? ie)
        :PE (pe/is-valid? ie)
        :RO (ro/is-valid? ie)
        :MA (ma/is-valid? ie)
        :PB (pb/is-valid? ie)
        :PA (pa/is-valid? ie)
        :DF (df/is-valid? ie)
        :MS (ms/is-valid? ie)
        :AM (am/is-valid? ie)
        :CE (ce/is-valid? ie)
        :PI (pi/is-valid? ie)
        :SC (sc/is-valid? ie)
        :SE (se/is-valid? ie)
        :ES (es/is-valid? ie)
        :RR (rr/is-valid? ie)
        false))))

(defn valid-length?
  "Checks if IE has valid length for the given state."
  [uf ie-str]
  (valid-ie-length? uf ie-str))