(ns hdl-clj.core
  (:import (clojure.lang IFn)))

(defrecord Gate [name ins outs parts fn]
  IFn
  (invoke [this i1] ((:fn this) i1))
  (invoke [this i1 i2] ((:fn this) i1 i2)))

(defn- exec-part [part]
  (flatten (take 2 part)))

(defn- output [part]
  (last part))

(defn- let-output [part]
  [(output part) (exec-part part)])

(defn- flatten-1 [xs]
  (reduce (fn [acc x] (into acc x)) [] xs))

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