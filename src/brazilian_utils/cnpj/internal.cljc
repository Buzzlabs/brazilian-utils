(ns brazilian-utils.cnpj.internal
  (:require [clojure.string :as string]
            [brazilian-utils.helpers :as helpers]))

(def ^:const cnpj-length 14)
(def ^:const first-check-digit-weights [5 4 3 2 9 8 7 6 5 4 3 2])
(def ^:const second-check-digit-weights [6 5 4 3 2 9 8 7 6 5 4 3 2])

(defn clean-alfanumeric
  "Removes non-alphanumeric characters and uppercases the CNPJ (A-Z, 0-9)."
  [cnpj]
  (if (nil? cnpj)
    ""
    (-> (str cnpj)
        (string/replace #"[^0-9A-Za-z]" "")
        string/upper-case)))

(defn clean-numeric
  "Removes all non-digit characters and returns only digits."
  [cnpj]
  (if (nil? cnpj)
    ""
    (-> (str cnpj)
        (string/replace #"[^0-9]" ""))))

(defn char->cnpj-value
  "Maps a character to CNPJ value: 0-9 -> 0..9, A-Z -> 10..35. Throws for invalid chars."
  [c]
  (let [c (first (str c))
        code #?(:clj (int c)
                :cljs (.charCodeAt c 0))]
    (cond
      (<= 48 code 57) (helpers/char->digit c)
      (<= 65 code 90) (+ 10 (- code 65))
      :else (throw (ex-info "Invalid CNPJ character" {:char c})))))

(defn calc-check-digit*
  "Computes mod-11 check digit for a CNPJ base (numeric or alphanumeric) using given weights."
  [base weights]
  (helpers/check-digit base weights {:char->val char->cnpj-value
                                           :stringify? true}))

(defn format-numeric
  "Formats numeric CNPJ as XX.XXX.XXX/XXXX-XX. Supports partials and optional :pad."
  ([cnpj] (format-numeric cnpj {}))
  ([cnpj {:keys [pad]}]
   (let [cleaned (clean-numeric cnpj)
         digits  (subs cleaned 0 (min (count cleaned) cnpj-length))
         padding-len (max 0 (- cnpj-length (count digits)))
         padded  (if pad
                   (str (string/join (repeat padding-len "0")) digits)
                   digits)
         n (count padded)
         segments [{:from 0 :to 2 :sep ""}
                   {:from 2 :to 5 :sep "."}
                   {:from 5 :to 8 :sep "."}
                   {:from 8 :to 12 :sep "/"}
                   {:from 12 :to 14 :sep "-"}]]
     (string/join ""
       (map (fn [{:keys [from to sep]}]
              (when (< from n)
                (str sep (subs padded from (min to n)))))
            segments)))))

(defn format-alfanumeric
  "Formats input as XX.XXX.XXX/XXXX-XX. Works for numeric and alphanumeric, supports partials.
   If :pad true, left-pads with zeros to 14 characters."
  ([cnpj] (format-alfanumeric cnpj {}))
  ([cnpj {:keys [pad]}]
   (let [s (str cnpj)
         already (re-matches #"^[0-9A-Z]{2}\.[0-9A-Z]{3}\.[0-9A-Z]{3}[/][0-9A-Z]{4}-\d{2}$" s)]
     (if (and (not pad) already)
       s
       (let [cleaned (clean-alfanumeric s)
             truncated (subs cleaned 0 (min (count cleaned) cnpj-length))
             padding-len (max 0 (- cnpj-length (count truncated)))
             padded (if pad
                      (str (string/join (repeat padding-len "0")) truncated)
                      truncated)
             n (count padded)
             segments [{:from 0 :to 2 :sep ""}
                       {:from 2 :to 5 :sep "."}
                       {:from 5 :to 8 :sep "."}
                       {:from 8 :to 12 :sep "/"}
                       {:from 12 :to 14 :sep "-"}]]
         (string/join ""
           (map (fn [{:keys [from to sep]}]
                  (when (< from n)
                    (str sep (subs padded from (min to n)))))
                segments)))))))

(defn valid-checksum*
  "Validates both CNPJ check digits for 14-character cleaned input."
  [cnpj]
  (when (= (count cnpj) 14)
    (let [base12 (subs cnpj 0 12)
          dv1 (calc-check-digit* base12 first-check-digit-weights)
          dv2 (calc-check-digit* (str base12 dv1) second-check-digit-weights)]
      (and (= (subs cnpj 12 13) dv1)
           (= (subs cnpj 13 14) dv2)))))


(defn generate-numeric-base
  "Generates a valid 12-digit numeric base that won't be all repeated digits.
  
  Returns:
    String with 12 random digits"
  []
  (let [rand-base (fn [] (helpers/random-digits 12))]
    (loop [b (rand-base)]
      (if (re-matches #"^(\d)\1{11}$" b) ; avoid all repeated
        (recur (rand-base))
        b))))

(defn generate-alphanumeric-base
  "Generates a valid 12-character alphanumeric base using letters and digits.
  
  Returns:
    String with 12 random alphanumeric characters (A-Z, 0-9)"
  []
  (let [chars "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        rand-ch (fn [] (nth chars (rand-int (count chars))))]
    (apply str (repeatedly 12 rand-ch))))
