(ns brazilian-utils.cpf.format
  (:require [brazilian-utils.helpers :as helpers]
            [clojure.string :as str]))

(def ^:const cpf-length 11)

(defn format-cpf
  "Formats numeric CPF as XXX.XXX.XXX-XX. Supports partials and optional :pad."
  ([cpf] (format-cpf cpf {}))
  ([cpf {:keys [pad]}]
   (let [cleaned (helpers/only-numbers cpf)
         digits  (subs cleaned 0 (min (count cleaned) cpf-length))
         padding-len (max 0 (- cpf-length (count digits)))
         padded  (if pad
                   (str (str/join (repeat padding-len \0)) digits)
                   digits)
         n (count padded)
         segments [{:from 0 :to 3 :sep ""}
                   {:from 3 :to 6 :sep "."}
                   {:from 6 :to 9 :sep "."}
                   {:from 9 :to 11 :sep "-"}]]
     (str/join ""
       (map (fn [{:keys [from to sep]}]
              (when (< from n)
                (str sep (subs padded from (min to n)))))
            segments)))))
