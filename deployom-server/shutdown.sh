#!/bin/sh

# Export Java
#export JAVA_HOME=/usr/java/latest

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
     echo 'INFO: Stopping Server...'
else
     echo 'ERROR: Server is not Running'
     exit
fi

# Run
$JAVA_HOME/bin/java -cp "lib/*" -Djava.util.logging.config.file=logging.properties org.deployom.grizzly.Stop
