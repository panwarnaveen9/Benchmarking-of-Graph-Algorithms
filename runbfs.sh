#!/bin/bash

#cp -r  /home/hduser/workspace/test/src /home/hduser/hadoop/mytest/src 

echo "\n---------"
sec="$(date +%s%N)"
echo "----------------\n"


../bin/hadoop dfs -rm -r /user

javac -classpath /home/hduser/Downloads/commons-configuration-1.10.jar:/home/hduser/Downloads/hadoop-common-2.2.0.jar:/home/hduser/Downloads/hadoop-core-0.19.0.jar:/home/hduser/Downloads/commons-lang-2.6.jar:/home/hduser/Downloads/commons-logging-1.1.3.jar:/home/hduser/Downloads/hadoop-mapreduce-client-common-2.2.0.jar:/home/hduser/Downloads/log4j-1.2.17.jar:/home/hduser/Downloads/hadoop-mapreduce-client-common-2.2.0.jar:/home/hduser/Downloads/guava-15.0.jar:/home/hduser/Downloads/hadoop-mapreduce-client-core-2.2.0.jar:/home/hduser/Downloads/commons-cli-1.2.jar  -d graph -sourcepath src  src/*.java

jar -cvf BFS.jar -C graph/ .

../bin/hadoop jar BFS.jar  GraphSearch   -m 2 -r 2

echo "\n---------"
sec1="$(date +%s%N)"
echo $sec
echo "\n---------"
echo $sec1
echo "\n---------"
sec1="$((sec1-sec))"
echo "$((sec1/1000000))s"

