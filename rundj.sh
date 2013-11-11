#!/bin/bash

#cp -r  /home/hduser/workspace/test/src /home/hduser/hadoop/mytest/src 

sec="$(date +%s%N)"
echo "------------\n"

../bin/hadoop dfs -rm -r /user

javac -classpath /home/hduser/Downloads/commons-configuration-1.10.jar:/home/hduser/Downloads/hadoop-common-2.2.0.jar:/home/hduser/Downloads/hadoop-core-0.19.0.jar:/home/hduser/Downloads/commons-lang-2.6.jar:/home/hduser/Downloads/commons-logging-1.1.3.jar:/home/hduser/Downloads/hadoop-mapreduce-client-common-2.2.0.jar:/home/hduser/Downloads/log4j-1.2.17.jar:/home/hduser/Downloads/hadoop-mapreduce-client-common-2.2.0.jar:/home/hduser/Downloads/guava-15.0.jar:/home/hduser/Downloads/hadoop-mapreduce-client-core-2.2.0.jar:/home/hduser/Downloads/commons-cli-1.2.jar  -d djgraph -sourcepath src_dj  src_dj/*.java

jar -cvf DJ.jar -C djgraph/ .

../bin/hadoop jar DJ.jar  Dijkstra   -m 2 -r 2


sec1="$(date +%s%N)"
echo "------------\n"
sec1="$((sec1-sec))"
echo "$((sec1/1000000))s"
echo "------------\n"
