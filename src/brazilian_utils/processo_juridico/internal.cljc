(ns brazilian-utils.processo-juridico.internal)

(def ^:const processo-length 20)

(defn- mod-large-number
  "Calculates mod for very large numbers in both clj and cljs."
  [num-str divisor]
  #?(:clj (mod (bigint num-str) divisor)
     :cljs (js/Number (mod (js/BigInt num-str) (js/BigInt divisor)))))

(defn verify-digit*
  "Verifies check digits using MOD 97-10 algorithm.
   
   Rearranges to NNNNNNN + AAAAJTTOOOO + DD and verifies mod 97 = 1"
  [processo]
  (when (= (count processo) processo-length)
    (let [;; Rearrange: NNNNNNN (0-7) + AAAA J TT OOOO (9-20) + DD (7-9)
          rearranged (str (subs processo 0 7) 
                          (subs processo 9 20)
                          (subs processo 7 9))
          mod-result (mod-large-number rearranged 97)]
      (= mod-result 1))))
