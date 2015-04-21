(ns hdl-clj.core
  (:require [clojure.math.combinatorics :as combo]
            [table.core :as table])
  (:import (clojure.lang IFn)))

(defrecord Gate [name ins outs parts fn]
  IFn
  (invoke [this i1] ((:fn this) i1))
  (invoke [this i1 i2] ((:fn this) i1 i2))
  (applyTo [this ins] (apply (:fn this) ins)))

(defn- exec-part [part]
  (flatten (take 2 part)))

(defn- output [part]
  (last part))

(defn- let-output [part]
  [(output part) (exec-part part)])

(defn- flatten-1 [xs]
  (reduce into [] xs))

(defmacro defgate
  "Creates a Gate. Eg: (hdl/defgate Not [a] => [out] (Nand [a a] => [out]))"
  [gate ins _ outs & parts]
  `(def ~gate (->Gate ~(str gate)
                      ~(mapv str ins)
                      ~(mapv str outs)
                      (quote ~parts)
                      (fn ~ins
                        (let ~(flatten-1 (map let-output parts))
                          ~(output (last parts)))))))

(defn- pin->hdl [ins]
  (clojure.string/join ", " ins))

(defn- pin-mapping->hdl [gate-in call-in]
  (str gate-in "=" call-in))

(defn- part->hdl [[symb-gate ins _ outs]]
  (str
    symb-gate "("
    (clojure.string/join ", " (map pin-mapping->hdl (:ins (eval symb-gate)) ins))
    ", "
    (clojure.string/join ", " (map pin-mapping->hdl (:outs (eval symb-gate)) outs))
    ");"))

(defn gate->hdl
  "Outputs a valid HDL string defining this gate"
  [gate]
  (str "/** Generated with hdl-clj **/" "\n"
       "CHIP " (:name gate) "{" "\n"
       "\t" "IN " (pin->hdl (:ins gate)) ";" "\n"
       "\t" "OUT " (pin->hdl (:outs gate)) ";" "\n"
       "\t" "PARTS : " "\n"
       "\t" (clojure.string/join "\n\t" (map part->hdl (:parts gate))) "\n"
       "}"))

(defn- in-comb->truth-table-line [gate in-comb]
  (concat in-comb (apply gate in-comb)))

(defn gate->truth-table [{:keys [ins outs] :as gate}]
  (->> (combo/selections [1 0] (count ins))
       (map (partial in-comb->truth-table-line gate))
       (cons (concat ins outs))))

(defn pretty-truth-table [truth-table]
  (table/table truth-table :style :unicode))