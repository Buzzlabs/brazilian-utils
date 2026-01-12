(ns brazilian-utils.email.validation
  (:require [clojure.string :as str]
            [malli.core :as m]
            [malli.error :as me]))

(def ^:const max-recipient-length 64)
(def ^:const max-domain-length 253)
(def ^:const max-email-length (+ max-recipient-length 1 max-domain-length))

(def ^:private valid-email-regex
  #"^([!#$%&'*+\-/=?^_`{|}~]{0,1}([a-zA-Z0-9][!#$%&'*+\-/=?^_`{|}~.]{0,1})+)@(([a-zA-Z0-9][-.]{0,1})+)([.]{1}[a-zA-Z0-9]+)$")

(def Email
  "Schema for email validation with comprehensive checks"
  [:and
   :string
   [:fn
    {:error/message "Email cannot be blank"}
    (fn [s] (not (str/blank? s)))]
   [:fn
    {:error/message (str "Email exceeds maximum length of " max-email-length " characters")}
    (fn [s] (<= (count s) max-email-length))]
   [:fn
    {:error/message "Invalid email format"}
    (fn [s] (boolean (re-matches valid-email-regex s)))]
   [:fn
    {:error/message (str "Recipient part exceeds maximum length of " max-recipient-length " characters")}
    (fn [s]
      (if-let [at-pos (str/index-of s "@")]
        (<= (count (subs s 0 at-pos)) max-recipient-length)
        true))]
   [:fn
    {:error/message (str "Domain part exceeds maximum length of " max-domain-length " characters")}
    (fn [s]
      (if-let [at-pos (str/index-of s "@")]
        (<= (count (subs s (inc at-pos))) max-domain-length)
        true))]])

(defn validate-email
  "Validates whether a given value is a well-formed email address."
  [email]
  (m/validate Email email))

(defn explain-email
  "Provides detailed explanation of why a given value is or isn't a well-formed email address."
  [email]
  (me/humanize (m/explain Email email)))