#!/bin/sh

# Get pid
pid=`ps aux | grep '[d]eployom'|awk '{ print $2 }'`

# Checking Server is running
if [ $pid ];
then
     echo INFO: Server is Running, pid $pid
else
     echo ERROR: Server is not Running
     exit
fi

echo 'Opened Sockets'
echo '=============================================='

# Opened Sockets
lsof -n|grep $pid|grep TCP
