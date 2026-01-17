(ns brazilian-utils.inscricao-estadual.validators.mg
  "Minas Gerais (MG) - 13 dígitos, dois dígitos verificadores.
   Regra conforme implementação do brazilian-utils/javascript."
  (:require [brazilian-utils.helpers :as helpers]))

(defn- calc-first-digit [ie]
  (let [body (str (subs ie 0 3) "0" (subs ie 3))
    ; monta concat de produtos com pesos alternando 2 e 1
    concat-str (apply str
              (map-indexed (fn [idx ch]
                     (let [d (helpers/char->digit ch)
                         w (if (= 0 (mod (+ idx 3) 2)) 2 1)
                         prod (* d w)]
                       (str prod)))
                     (seq body)))
    sum (helpers/sum-digits concat-str)
    last-digit (mod sum 10)]
  (if (= last-digit 0) 0 (- 10 last-digit))))

(defn- calc-second-digit [base12]
  (let [digits (helpers/digits->ints base12)]
    (loop [ds digits w 3 sum 0]
      (if (empty? ds)
        (let [rest (mod sum 11)
              dig (- 11 rest)]
          (if (>= dig 10) 0 dig))
        (let [sum' (+ sum (* (first ds) w))
              w' (dec w)
              w'' (if (= w' 1) 11 w')]
          (recur (rest ds) w'' sum'))))))

(defn is-valid?
  [ie]
  (when (= 13 (count ie))
    (let [pos1 11
          pos2 12
          first (calc-first-digit (subs ie 0 11))
          second (calc-second-digit (str (subs ie 0 11) first))
          d1 (helpers/char->digit (get ie pos1))
          d2 (helpers/char->digit (get ie pos2))]
      (and (= first d1) (= second d2)))))