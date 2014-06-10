# Minesweeper, ClojureScript style

This is the code from a talk I did a few times recently. It is a functional,
although not entirely complete version of Minesweeper in ClojureScript, using
[Om](https://github.com/swannodette/om) (a [React.JS](facebook.github.io/react/)
wrapper). Basically, it is as complete as I had time to make it in the talk - I
have not touched the code afterwards, except for one small detail: I added
source maps. (If you saw my talk at NDC, you know why).

## Run it

* Start a repl, either with `lein repl`, or if you're in Emacs: `cider-jack-in` (`C-c M-j`)
* Compile ClojureScript: `lein cljsbuild auto`
* Start the web server from within the REPL: `(run!)`
* Hit [localhost:8080](http://localhost:8080) to play
* In the REPL, run (cljs!) to enter a REPL connected to the browser

That's about it.

## flatMap(Oslo) 2014

The [flatMap(Oslo) 2014](http://flatmap.no) version of this talk is the one I am
personally the most happy with. [Video](http://2014.flatmap.no/speakers/johansen.html).

## NDC 2014

I did a slightly longer version at [NDC 2014](http://ndcoslo.no). I stumbled
more than I'd like when I forgot to make the `app-history` an atom as I
developed the undo feature. As I had not added source maps to my setup, it felt
unelegant to say the least. It was all solved in the end, by a hero in the
audience. [Video](http://vimeo.com/97516219).
