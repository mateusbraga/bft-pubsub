#! /bin/sh

cd pid_files

for f in *
do

    if [ -d "/proc/$f" ]
    then
        echo "Kill $f"
        kill -9 $f
        rm $f
    else
        rm $f
    fi
done
