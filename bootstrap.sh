#!/bin/bash -e

function connected() {
    if ping -q -c 1 -W 1 8.8.8.8 >/dev/null; then
        echo "IPv4 is up..."
        true
    else
        echo "IPv4 is down..."
        false
    fi
}

if connected; then
    echo "Arch Linux Bootstrap install script"
    pacman -Sy nodejs npm --noconfirm # Download size approx. 16mb
    npm install clojurescript-nodejs
    # run cljs script via cljs.js
fi
