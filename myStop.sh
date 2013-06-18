#! /bin/sh

ps aux | grep 'java -cp' | grep -v grep | awk '{ print $2 }' | xargs kill -9
