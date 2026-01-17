(ns brazilian-utils.inscricao-estadual.validators.go
  (:require [brazilian-utils.helpers :as helpers]))

;; Goiás (GO): 9 digits
;; Format: (10|11|12)XXXXXX-X (must start with 10, 11, or 12)
;; Special rule: if dig=11 and body in range 10103105-10119997, then dig=1; else if dig>=10, dig=0

(defn is-valid? [ie-str]
  (let [ie (helpers/only-numbers ie-str)]
    (if (= 9 (count ie))
      (let [prefix (subs ie 0 2)]
        (if (contains? #{"10" "11" "12"} prefix)  ; Must start with 10, 11, or 12
          (let [body (subs ie 0 8)
                body-int (helpers/parse-int body)
                check-digit (helpers/char->digit (get ie 8))
                weights [9 8 7 6 5 4 3 2]
                sum (helpers/weighted-sum body weights)
                remainder (mod sum 11)
                dig (- 11 remainder)
                
                ; Special GO rule
                final-dig (if (>= dig 10)
                            (if (and (= dig 11) (<= 10103105 body-int 10119997))
                              1
                              0)
                            dig)]
            (= check-digit final-dig))
          false))
      false)))
