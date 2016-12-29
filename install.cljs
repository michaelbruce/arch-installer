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
;; TODO complete swapfile enabling - how do you use genfstab to assign swapfile instead of /mnt/swapfile - is this even an issue? it seems so!
;; (call "fallocate" ["-l" "1024M" "/mnt/swapfile"])
;; (call "chmod" ["600" "/mnt/swapfile"])
(call "arch-chroot" ["/mnt" "pacman" "-S" "gvim" "net-tools" "--noconfirm"])
(call "arch-chroot" ["/mnt" "sed" "-i" "/^#en_US.UTF-8 /s/^#//" "/etc/locale.gen"])
(call "arch-chroot" ["/mnt" "sed" "-i" "/^#en_GB.UTF-8 /s/^#//" "/etc/locale.gen"])
(call "arch-chroot" ["/mnt" "locale-gen"])
(call "arch-chroot" ["/mnt" "/bin/bash" "-c" "echo 'LANG=en_GB.UTF-8' > /etc/locale.conf"])
(call "arch-chroot" ["/mnt" "/bin/bash" "-c" "ln -s /usr/share/zoneinfo/Europe/London /etc/localtime"])
(call "arch-chroot" ["/mnt" "/bin/bash" "-c" "hwclock --systohc --utc"])
(call "arch-chroot" ["/mnt" "/bin/bash" "-c" "mkinitcpio -p linux"])
(call "arch-chroot" ["/mnt" "/bin/bash" "-c" "pacman -S grub os-prober --noconfirm"])
(call "arch-chroot" ["/mnt" "/bin/bash" "-c" "grub-install --recheck /dev/sda"])
(call "arch-chroot" ["/mnt" "/bin/bash" "-c" "grub-mkconfig -o /boot/grub/grub.cfg"])



