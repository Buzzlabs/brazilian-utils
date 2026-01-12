(ns brazilian-utils.cities.core
  "Utilities to work with Brazilian cities by state (UF)."
  (:require [brazilian-utils.data :as data]
            [brazilian-utils.states.validation :as state-validation]))

(defn- valid-state?
  "Returns true if state is a valid UF keyword.
   
   Arguments:
   - state: Keyword representing the state code (UF)
   
   Returns true if valid, false otherwise."
  [state]
  (state-validation/valid-uf? state))

(defn cities-of
  "Returns all cities of a given state.
   
   Arguments:
   - state: Keyword representing the state code (UF)
   
   Returns a vector of strings with city names, or empty vector if state not found.
   
   Example:
   (cities-of :SP) ;; [\"São Paulo\" \"Campinas\" ...]"
  [state]
  (get data/cities-by-state state []))

(defn cities-of!
  "Returns all cities of a given state or throws on invalid state.
   
   Arguments:
   - state: Keyword representing the state code (UF)
   
   Returns a vector of strings with city names.
   Throws ex-info with {:state <value>} if state is invalid.
   
   Example:
   (cities-of! :SP) ;; [\"São Paulo\" \"Campinas\" ...]
   (cities-of! :XX) ;; throws ex-info"
  [state]
  (if (valid-state? state)
    (get data/cities-by-state state [])
    (throw (ex-info "Invalid state" {:state state}))))

(defn all-cities
  "Returns all cities with their respective states.
   
   Returns a vector of maps with :state and :city keys.
   
   Example:
   (all-cities) ;; [{:state :SP :city \"São Paulo\"} {:state :RJ :city \"Rio de Janeiro\"} ...]"
  []
  (reduce (fn [acc [uf cities]]
            (into acc (map (fn [c] {:state uf :city c}) cities)))
          []
          data/cities-by-state))

(defn all-city-names
  "Returns all city names without state context.
   
   Returns a vector of strings with all city names (flat list).
   
   Example:
   (all-city-names) ;; [\"São Paulo\" \"Rio de Janeiro\" \"Salvador\" ...]"
  []
  (reduce into [] (vals data/cities-by-state)))
