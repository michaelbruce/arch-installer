(ns arch.install
  (:require [cljs.nodejs :as node]))

(println "Script initialized")
(node/require "util")
(def exec
  (.-exec (node/require "child_process")))

(defn call [function]
  (let [process (spawn function (clj->js (first args)))]
    (.on (.-stdout process) "data" (fn [data] (.log js/console (str data))))
    (.on (.-stderr process) "data" (fn [data] (.log js/console (str data))))
    (.on process "close"
         (fn [code] (.log js/console (str function "finished"))))
    (.on process "error"
         (fn [code] (.log js/console (str code))))))

;; TODO detect if gpt or mbr is needed
(println "1. Disk setup - for now only mbr")
(call (str "parted --script /dev/sda "
           "mklabel msdos "
           "mkpart primary ext4 1M 100% "
           "set 1 boot on"))
(call "mkfs.ext4" ["/dev/sda1"])
(call "mount" ["/dev/sda1" "/mnt"])
(call "pacstrap" ["/mnt" "base" "base-devel"])
;; TODO detect ram amount from cat /proc/meminfo to dynamically set swap file size
