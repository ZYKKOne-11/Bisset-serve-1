#!/bin/bash

#full service name
SERVICE_NAME="serve-task-1.0-SNAPSHOT"
JAR_NAME=$SERVICE_NAME\.jar
PID=$SERVICE_NAME\.pid
CONFIG_DIR=${BASE_PATH}"/conf/"
JAVA_MEM_OPTS=" -server -Xmx700m -Xms700m -Xmn200m -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -XX:PermSize=256m -XX:+DisableExplicitGC
-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods
-XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/serve-task-1.0.0-SNAPSHOT.dump -Djava.net.preferIPv4Stack=true"
DEBUG=""
LOG_FILE=serve-task.log
BOOT_SUCCESS_SIGN='spring boot start successful'
STOP_SIGN='DruidDataSource.*closed'
JAVA_OPTS="$JAVA_MEM_OPTS $DEBUG"
#service dir
cd `dirname $0`
SERVICE_DIR=$(dirname $(pwd))
cd ..

# service start
function startService(){
        PIDS=`ps -ef | grep java |grep  "$SERVICE_NAME" | awk '{print $2}'`
        if [ -n "$PIDS" ]; then
            echo " ERROR : The $SERVICE_NAME already started!"
            echo " PID : $PIDS"
            exit 1
        else
		    echo "Starting the $SERVICE_NAME ... "
            nohup java $JAVA_OPTS -jar $JAR_NAME --spring.config.location=${CONFIG_DIR} >$SERVICE_DIR/logs/$LOG_FILE 2>&1 &
            echo $! > $SERVICE_DIR/$PID
			# Check whether process started successfully
			COUNT=0
			while [ $COUNT -lt 1 ]; do
				COUNT=`ps -ef | grep java | grep "$SERVICE_NAME" | awk '{print $2}' | wc -l`
				if [ $COUNT -gt 0 ]; then
				    while [ ! -f "$SERVICE_DIR/logs/$LOG_FILE" ]; do
				        sleep 2
				    done

					tailf $SERVICE_DIR/logs/$LOG_FILE | while read oneLineLog
                    do
					    echo $oneLineLog
		                if [[ $oneLineLog =~ $BOOT_SUCCESS_SIGN ]]; then
					        echo 'start sign match'
					        pkill tailf
                        fi
                    done
					break
				fi
			done

			echo  "$SERVICE_NAME  start success  !"
			export MALLOC_ARENA_MAX=1
			PID=`cat $SERVICE_DIR/$PID`

			echo  "The process after start, PID: $PID "
        fi
}
# service stop
function stopService (){
        pwd
        PID_FILE=$SERVICE_DIR/$PID
	    PID_FILE=`cat $PID_FILE`
		CUR_PID=""
        if [  ! -f "$PID_FILE" ]; then
        	CUR_PID=`ps -ef |grep java | grep  "$SERVICE_NAME" | awk '{print $2}'`
            if [  ! -n "$CUR_PID" ]; then
                  echo  "$SERVICE_NAME process not exists ! "
                  exit 0
            fi
            else
        	CUR_PID=`cat $PID_FILE`
        fi

        echo  "The process before stop,PID:$CUR_PID  "
        kill $CUR_PID
        rm -rf $PID_FILE
        echo  "stop $SERVICE_NAME ...  "

        STOPPED="0"
        while [ $STOPPED -eq "0" ]; do
            P_ID=`ps -ef |grep java | grep  "$SERVICE_NAME" | awk '{print $2}'`
            if [ "$P_ID" == "" ]; then
                # ok
                STOPPED="1"
                echo "$SERVICE_NAME stop success !"
                break
            else
                # 妫?鏌ユ棩蹇楁槸鍚﹀叧闂畬
                tailf $SERVICE_DIR/logs/$LOG_FILE | while read oneLineLog
                do
                    echo $oneLineLog
                    if [[ $oneLineLog =~ $STOP_SIGN ]]; then
		                echo 'stop sign match'
		                pkill tailf
                    fi
                    P_ID=`ps -ef |grep java | grep  "$SERVICE_NAME" | awk '{print $2}'`
                    if [ "$P_ID" == "" ]; then
                        # ok
                        pkill tailf
                    fi
                done

                # warning
                echo  "$SERVICE_NAME process PID :$P_ID "
                echo  "still killing $SERVICE_NAME process, PID :$P_ID ....."
                sleep 3
            fi
        done
}

case "$1" in

    start)
	     (startService)
        ;;

    stop)
	    (stopService)
        ;;

    restart)
        (stopService)
        sleep 2
        (startService)
        ;;

    *)
	    ## help
	    echo "which do you want to?input the command."
        echo  " ./startup.sh start  $SERVICE_NAME start"
        echo  " ./startup.sh stop  $SERVICE_NAME stop"
        echo  " ./startup.sh restart  $SERVICE_NAME restart"
        ;;
esac
exit 0

