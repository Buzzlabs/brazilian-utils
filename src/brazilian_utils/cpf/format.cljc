(ns brazilian-utils.cpf.format
  (:require [brazilian-utils.helpers :as helpers]
            [clojure.string :as str]))

(def ^:const cpf-length 11)

(def ^:private cpf-segments
  "CPF format segments with their positions and separators.
  Format: XXX.XXX.XXX-XX"
  [{:from 0 :to 3 :sep ""}
   {:from 3 :to 6 :sep "."}
   {:from 6 :to 9 :sep "."}
   {:from 9 :to 11 :sep "-"}])

(defn- pad-with-zeros
  "Pads a string with leading zeros to reach the target length.
  
  Args:
    s - String to pad
    target-length - Desired length
    
  Returns:
    Padded string"
  [s target-length]
  (let [current-length (count s)
        zeros-needed (max 0 (- target-length current-length))]
    (str (str/join (repeat zeros-needed \0)) s)))

(defn- format-segment
  "Formats a single CPF segment by extracting substring and adding separator.
  
  Args:
    digits - The full digit string
    segment - Map with :from, :to, :sep keys
    
  Returns:
    Formatted segment string or empty string if segment is beyond available digits"
  [digits {:keys [from to sep]}]
  (let [digits-count (count digits)
        segment-end (min to digits-count)]
    (if (< from digits-count)
      (str sep (subs digits from segment-end))
      "")))

(defn format-cpf
  "Formats numeric CPF as XXX.XXX.XXX-XX. Supports partials and optional :pad."
  ([cpf] (format-cpf cpf {}))
  ([cpf {:keys [pad]}]
   (let [digits-only (helpers/only-numbers cpf)
         max-length (min (count digits-only) cpf-length)
         truncated-digits (subs digits-only 0 max-length)
         final-digits (if pad
                        (pad-with-zeros truncated-digits cpf-length)
                        truncated-digits)
         formatted-parts (map #(format-segment final-digits %) cpf-segments)]
     (str/join "" formatted-parts))))
