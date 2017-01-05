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

(defn chroot [mount-point & cmds]
  "Take a list of commands and executes them under a chroot jail at mount-point"
  (map (fn [cmd] (call "arch-chroot" ["/mnt" "/bin/bash" "-c" cmd])) cmds))

(call "arch-chroot" ["/mnt" "sed" "-i" "/^#en_US.UTF-8 /s/^#//" "/etc/locale.gen"])
(call "arch-chroot" ["/mnt" "sed" "-i" "/^#en_GB.UTF-8 /s/^#//" "/etc/locale.gen"])

(chroot
  "/mnt"
  "pacman -S gvim net-tools --noconfirm"
  "locale-gen"
  "echo 'LANG=en_GB.UTF-8' > /etc/locale.conf"
  "ln -s /usr/share/zoneinfo/Europe/London /etc/localtime"
  "hwclock --systohc --utc"
  "mkinitcpio -p linux"
  "pacman -S grub os-prober --noconfirm"
  "grub-install --recheck /dev/sda"
  "grub-mkconfig -o /boot/grub/grub.cfg")

;; UNTESTED beyond here - needs loading against virtualbox
;; TODO script hosting these files (maybe tar.gz'd?) for virtualbox to wget locally

;; TODO set /etc/hostname to mikepjb-laptop etc
;; TODO set /etc/hosts change localhost to new name
;; TODO systemctl enable dhcpcd@<interface>.service

;; XXX at this point can we umount partitions, reboot and set a script to resume on restart?
;; TODO how much of the following can be done in chroot before the restart... all of it?

;; Reached line 62 of notes
