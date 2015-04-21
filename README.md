# hdl-clj

A Clojure library to play with Hardware Description Language (HDL) with a DSL.

This library has been designed in early stage of the [nand2tetris](http://nand2tetris.org) course.

## Usage

Define some gates :

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


Then you can call your gates :

    (And 1 0)
    => [false]


You can also export the gates as valid HDL files :

    (println (hdl/gate->hdl Or))
    /** Generated with hdl-clj **/
    CHIP Or{
    	IN a, b;
    	OUT out;
    	PARTS :
    	Not(a=a, out=not-a);
    	Not(a=b, out=not-b);
    	Not(a=not-a, out=w);
    	Not(a=w, out=out);
    }

Or you can see the truth table :

    (-> And hdl/gate->truth-table hdl/pretty-truth-table)
    ┌───┬───┬─────┐
    │ a │ b │ out │
    ├───┼───┼─────┤
    │ 1 ╎ 1 ╎ 1   │
    │ 1 ╎ 0 ╎ 0   │
    │ 0 ╎ 1 ╎ 0   │
    │ 0 ╎ 0 ╎ 0   │
    └───┴───┴─────┘

## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
