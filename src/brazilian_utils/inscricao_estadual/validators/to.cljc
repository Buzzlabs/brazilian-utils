(ns brazilian-utils.inscricao-estadual.validators.to
  (:require [brazilian-utils.helpers :as helpers]))

;; Tocantins (TO): 9 or 11 digits
;; Old format (11 digits): positions 2-3 must be 01/02/03/99, body = ie[0:2] + ie[4:11]
;; New format (9 or 11 digits): weights start at 9, if rest < 2 then dig = 0, else dig = 11 - rest

(defn- old-format?
  "Check if IE is in old format: 11 digits with positions 2-3 being 01/02/03/99"
  [ie]
  (and (= 11 (count ie))
       (contains? #{"01" "02" "03" "99"} (subs ie 2 4))))

(defn- calc-old-format
  "Old format: body = ie[0:2] + ie[4:10], uses CE algorithm"
  [ie]
  (let [body (str (subs ie 0 2) (subs ie 4 10))
        check-digit (helpers/char->digit (get ie 10))
        weights [9 8 7 6 5 4 3 2]
        sum (helpers/weighted-sum body weights)
        remainder (mod sum 11)
        dig (- 11 remainder)
        final-dig (if (>= dig 10) 0 dig)]
    (= check-digit final-dig)))

(defn- calc-new-format
  "New format: weights start at 9 and decrement, if rest < 2 then dig = 0"
  [ie]
  (let [body (subs ie 0 (dec (count ie)))
        check-digit (helpers/char->digit (get ie (dec (count ie))))
        weights (range 9 (- 9 (count body)) -1)
        sum (helpers/weighted-sum body weights)
        remainder (mod sum 11)
        dig (if (< remainder 2) 0 (- 11 remainder))]
    (= check-digit dig)))

(defn is-valid? [ie-str]
  (let [ie (helpers/only-numbers ie-str)]
    (cond
      (= 11 (count ie))
      (if (old-format? ie)
        (calc-old-format ie)
        (calc-new-format ie))
      
      (= 9 (count ie))
      (calc-new-format ie)
      
      :else false)))
