(ns brazilian-utils.data
  (:require [malli.core :as m]
            [brazilian-utils.states.schemas :as state-schemas]
            [brazilian-utils.cities.schemas :as city-schemas])
  #?(:clj (:require [clojure.edn :as edn]
                    [clojure.java.io :as io]))
  #?(:cljs (:require-macros [brazilian-utils.data :refer [load-edn-resource]])))

#?(:clj
  (defmacro load-edn-resource
    "Macro that reads an .edn file at compile time.

    In CLJ/CLJS, the macro runs during compilation on the JVM,
    allowing file system access via io/resource. The data is
    embedded directly into the compiled code (compile-time)."
     [resource-name]
     (try
       (-> resource-name
           io/resource
           slurp
           edn/read-string)
       (catch Exception e
         (println "Warning: Could not load" resource-name (.getMessage e))
         {}))))

;; ============================================================================
;; States Data
;; ============================================================================

(defn- normalize-states-map
  "Validates (and potentially normalizes) the states map loaded from EDN.
   Currently only validates against state-schemas/StatesDataByUf."
  [m]
  (if (state-schemas/validate-states-map m)
    m
    (throw (ex-info "states.edn is invalid"
                    {:schema state-schemas/StatesDataByUf
                     :explain (m/explain state-schemas/StatesDataByUf m)}))))

(def states-map
  "Complete map with information for all 27 Brazilian states.

   Structure:
   {:UF {:code \"number\" :name \"Full Name\" :area-codes [ddd...] :ie-length number-or-vector}
    ...}"
  (-> (load-edn-resource "states.edn")
      (normalize-states-map)))

;; ============================================================================
;; Cities Data
;; ============================================================================

(defn- normalize-cities-by-state
  "Validates (and potentially normalizes) the cities-by-state map
   loaded from EDN. Currently validates against city-schemas/CitiesByUf."
  [m]
  (if (city-schemas/validate-cities-map m)
    m
    (throw (ex-info "cities.edn is invalid"
                    {:schema city-schemas/CitiesByUf
                     :explain (m/explain city-schemas/CitiesByUf m)}))))

(def cities-by-state
  "Map of cities by state (UF).

   Structure:
   {:UF [\"City Name 1\" \"City Name 2\" ...]
    ...}"
  (-> (load-edn-resource "cities.edn")
      (normalize-cities-by-state)))