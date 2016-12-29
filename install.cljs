(ns arch.install
  (:require [cljs.nodejs :as node]))

(println "Script initialized")
(node/require "util")
(def exec
  (.-exec (node/require "child_process")))

(defn call [arg]
  (exec
    arg
    (fn [err stdout stderr]
      (.log js/console stdout)
      (.log js/console stderr))))

;; TODO detect if gpt or mbr is needed
(println "1. Disk setup - for now only mbr")
(call (str "parted --script /dev/sda "
           "mklabel msdos "
           "mkpart primary ext4 1M 100% "
           "set 1 boot on"))
