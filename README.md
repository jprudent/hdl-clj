# hdl-clj

A Clojure library to play with Hardware Description Language (HDL) with a DSL.

This library has been designed in early stage of the [nand2tetris](http://nand2tetris.org) course.

## Usage

Define some gates :

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


Then you can call your gates :

    (And true false)
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


## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
