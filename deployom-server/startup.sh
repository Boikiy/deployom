#!/bin/sh

# Export Java
#export JAVA_HOME=/usr/java/latest

# File Caching, should be disable for fast Development
export FILE_CACHE_ENABLED=true
export STATIC_PATH=web

# Checking Java
if $JAVA_HOME/bin/java -version 2>&1|grep '1.[678]'
then
     echo INFO: Java is 1.6 or greater
else
     echo ERROR: Java 1.6 or greater is required
     exit
fi

# Checking Server is running
if ps aux | grep '[d]eployom'
then
     echo ERROR: Server is Running already
     exit
else
     echo INFO: Starting Server...
fi

# Run
$JAVA_HOME/bin/java -cp "lib/*" -Djava.security.policy=lib/deployom.policy -Djava.util.logging.config.file=lib/logging.properties org.deployom.grizzly.Start >/dev/null 2>&1 &

# Sleep and Show
sleep 5
cat server.log
