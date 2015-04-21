(ns hdl-clj.gates
  (:require [hdl-clj.core :as hdl]))

;; this is the base gate. Every gate will be defined upon this one

(defn tru|fal-thy [pin]
  (if (integer? pin)
    (= 1 pin)
    (if pin 1 0)))

(def Nand (hdl/->Gate "Nand"
                      ['a 'b]
                      ['out]
                      nil
                      (fn [a b] [(let [a' (tru|fal-thy a)
                                       b' (tru|fal-thy b)]
                                   (tru|fal-thy (not (and a' b'))))])))

;; other "logical" gates

(hdl/defgate Not [a] => [out]
             (Nand [a a] => [out]))

(hdl/defgate And [a b] => [out]
             (Nand [a b] => [out-nand])
             (Not [out-nand] => [out]))


(hdl/defgate Or [a b] => [out]
             (Not [a] => [not-a])
             (Not [b] => [not-b])
             (And [not-a not-b] => [w])
             (Not [w] => [out]))