(ns brazilian-utils.inscricao-estadual.validators.ap
  (:require [brazilian-utils.helpers :as helpers]))

;; Amapá (AP): 9 digits
;; Format: 03XXXXXX-X (must start with 03, then 6 body digits + 1 check digit)
;; Special ranges: 3000001-3017000 (p=5, d=0), 3017001-3019022 (p=9, d=1)

(defn is-valid? [ie-str]
  (let [ie (helpers/only-numbers ie-str)]
    (if (and (= 9 (count ie))
             (= "03" (subs ie 0 2)))  ; Must start with 03
      (let [body (subs ie 0 8)
            body-int (helpers/parse-int body)
            check-digit (helpers/char->digit (get ie 8))
            
            ; Determine p and d based on range
            [p d] (cond
                    (<= 3000001 body-int 3017000) [5 0]
                    (<= 3017001 body-int 3019022) [9 1]
                    :else [0 0])
            
            ; Calculate: sum = p + weighted sum with weights 9,8,7,6,5,4,3,2
            weights [9 8 7 6 5 4 3 2]
            sum (+ p (helpers/weighted-sum body weights))
            remainder (mod sum 11)
            dig (- 11 remainder)
            
            ; Adjust digit: if 10 → 0, if 11 → d
            final-dig (cond
                        (= dig 10) 0
                        (= dig 11) d
                        :else dig)]
        (= check-digit final-dig))
      false)))