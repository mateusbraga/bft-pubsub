#! /bin/sh

for i in 0 1 2 3
do
    ./smartrun.sh publishsubscribe.Servidor $i &
    sleep 1
done

