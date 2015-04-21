(ns hdl-clj.gates
  (:require [hdl-clj.core :as hdl]))

(def Nand (hdl/->Gate "Nand"
                  ['a 'b]
                  ['out]
                  nil
                  (fn [a b] [(not (and a b))])))


(hdl/defgate Not [a] => [out]
         (Nand [a a] => [out]))

(hdl/defgate And [a b] => [out]
         (Nand [a b] => [out-nand])
         (Not [out-nand] => [out]))


(hdl/defgate Or [a b] => [out]
         (Not [a] => [not-a])
         (Not [b] => [not-b])
         (Not [not-a not-b] => [w])
         (Not [w] => [out]))