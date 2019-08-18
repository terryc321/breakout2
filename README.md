# breakout2.core

simple ball bounce game

## Overview

this time using CANVAS in clojurescript to

+ 1 changed from setInterval to requestAnimationFrame thingy.
animation is still a bit laggy to be honest.

?? probably reagent might not be ideal for this , may just be fine with plain clojurescript

+ 1 uses mouse
+1 hides cursor with canvas style

    [:canvas .... {:style {:cursor "none"} ... } ... ]

## Development

if just copying core.cljs file from another build , just change name space in core.cljs
file so it matches the file directory structure , bit klanky but its javas way of doing
things i guess.

from bhauman figwheel developer github 

    lein new figwheel-main breakout2.core -- --reagent
    
To get an interactive development environment run:

    lein fig:build

This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

	lein clean

To create a production build run:

	lein clean
	lein fig:min


## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
