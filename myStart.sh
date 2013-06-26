#! /bin/sh


for i in 0 1 2 3
do
    java -cp 'lib/*:bin' pubsub.Servidor $i &
    PROCESS_PID=$!
    touch pid_files/$PROCESS_PID

    sleep 1
done

