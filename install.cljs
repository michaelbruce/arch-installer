(ns arch.install
  (:require [cljs.nodejs :as node]))

(println "Script initialized")
(node/require "util")
(def exec
  (.-exec (node/require "child_process")))

(exec "ls"
      (fn [err stdout stderr]
        (.log js/console stdout)
        (.log js/console stderr)))
