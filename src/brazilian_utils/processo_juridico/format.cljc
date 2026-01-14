(ns brazilian-utils.processo-juridico.format
  (:require [brazilian-utils.processo-juridico.internal :as i]
            [brazilian-utils.processo-juridico.validation :as validation]
            [brazilian-utils.helpers :as helpers]))

(def ^:private hyphen-indexes [6])
(def ^:private dot-indexes [8 12 15])

(defn format-processo
  "Applies the standard mask NNNNNNN-DD.AAAA.J.TT.OOOO.
   Input may be partial; extra digits are discarded. Returns a partially
   masked string according to available digits."
  [processo]
  (if (validation/is-formatted? processo)
    processo
    (let [digits (helpers/only-numbers processo)
          trimmed (subs digits 0 (min (count digits) i/processo-length))
          len (count trimmed)
          hyphen-set (set hyphen-indexes)
          dot-set (set dot-indexes)]
      (reduce (fn [result [idx digit]]
                (let [with-digit (str result digit)]
                  (cond
                    (and (< idx (dec len)) (hyphen-set idx)) (str with-digit "-")
                    (and (< idx (dec len)) (dot-set idx)) (str with-digit ".")
                    :else with-digit)))
              ""
              (map-indexed vector trimmed)))))
