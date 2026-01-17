(ns brazilian-utils.processo-juridico.format
  (:require [brazilian-utils.processo-juridico.internal :as i]
            [brazilian-utils.processo-juridico.validation :as validation]
            [brazilian-utils.helpers :as helpers]))

(def ^:private hyphen-indexes
  "Positions where hyphens should be inserted in processo format.
   Format: NNNNNNN-DD.AAAA.J.TT.OOOO (hyphen after position 6)"
  [6])

(def ^:private dot-indexes
  "Positions where dots should be inserted in processo format.
   Format: NNNNNNN-DD.AAAA.J.TT.OOOO (dots at positions 8, 12, 15)"
  [8 12 15])

(defn- get-separator
  "Determines which separator to add at the given position.
   
   Args:
     idx - Current position in the formatted string
     max-idx - Maximum index (length - 1)
     hyphen-set - Set of positions where hyphens should be added
     dot-set - Set of positions where dots should be added
   
   Returns:
     A separator character (\"-\" or \".\") or empty string"
  [idx max-idx hyphen-set dot-set]
  (cond
    (and (< idx max-idx) (hyphen-set idx)) "-"
    (and (< idx max-idx) (dot-set idx)) "."
    :else ""))

(defn format-processo
  "Applies the standard mask NNNNNNN-DD.AAAA.J.TT.OOOO.
   Input may be partial; extra digits are discarded. Returns a partially
   masked string according to available digits.
   
   Args:
     processo - String with processo number (formatted or unformatted)
   
   Returns:
     Formatted processo string with separators applied"
  [processo]
  (if (validation/is-formatted? processo)
    processo
    (let [digits (helpers/only-numbers processo)
          trimmed (subs digits 0 (min (count digits) i/processo-length))
          trimmed-length (count trimmed)
          max-idx (dec trimmed-length)
          format-digit (fn [result [idx digit]]
                         (let [digit-str (str result digit)
                               separator (get-separator idx max-idx (set hyphen-indexes) (set dot-indexes))]
                           (str digit-str separator)))]
      (reduce format-digit "" (map-indexed vector trimmed)))))
