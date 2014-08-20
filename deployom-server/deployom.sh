#!/bin/sh

# Set Java Home if required
if [ -d "/usr/java/latest" ]; then
    export JAVA_HOME=/usr/java/latest
fi

# File Caching, should be disable for fast Development
export FILE_CACHE_ENABLED=true

# Web folder with HTML5 static
export STATIC_PATH=web

# Threads Pools (defaults, CORE_POOL=50 MAX_POOL=100)
# export CORE_POOL=50
# export MAX_POOL=100

# Checking Java
if $JAVA_HOME/bin/java -version 2>&1|grep '1.[678]'
then
    echo INFO: Java is 1.6 or greater
else
    echo ERROR: Java 1.6 or greater is required
    exit
fi

# Get pid
pid=`ps aux | grep '[d]eployom.server.Start'|awk '{ print $2 }'`

case "$1" in
    'start')

        # Checking Server is running
        if [ $pid ];
        then
            echo 'ERROR: Server is Running already'
            exit
        else
            echo 'INFO: Starting Server...'
        fi

        # Run
        $JAVA_HOME/bin/java -cp "lib/*" -Djava.security.policy=lib/deployom.policy -Djava.security.egd=file:///dev/urandom -Djava.util.logging.config.file=lib/logging.properties org.deployom.server.Start >server.log 2>&1 &

        # Sleep and Show
        sleep 5
        cat server.log
        ;;
    'stop')

        # Checking Server is running
        if [ $pid ];
        then
            echo "INFO: Stopping Server... pid $pid"
        else
            echo 'ERROR: Server is not Running'
            exit
        fi

        # Run
        $JAVA_HOME/bin/java -cp "lib/*" -Djava.util.logging.config.file=lib/logging.properties org.deployom.server.Stop >> server.log

        # Sleep and Show Status
        sleep 5
        ./deployom.sh status
        ;;
    'add')

        # Checking Server is running
        if [ $pid ];
        then
            echo "INFO: Server is Running... pid $pid"
        else
            echo 'ERROR: Server is not Running'
            exit
        fi

        # Run
        $JAVA_HOME/bin/java -cp "lib/*" -Djava.util.logging.config.file=lib/logging.properties org.deployom.server.Cmd "$@" >> server.log
        ;;
    'status')

        # Checking Server is running
        if [ $pid ];
        then
            echo "INFO: Server is Running, pid $pid"
        else
            echo 'INFO: Server is not Running'
            exit
        fi

        echo
        echo 'Opened Sockets'
        echo '=============================================='

        # Opened Sockets
        lsof -n|grep $pid|grep TCP
        ;;
*)
        echo "Usage: $0 {add|start|status|stop}"
        ;;
esac
