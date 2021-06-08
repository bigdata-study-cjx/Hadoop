```
root@bigdata-pro00:/opt/modules/hadoop-2.6.0# bin/hdfs dfs -put /opt/datas/wordcount.txt /user/root/data/
root@bigdata-pro00:/opt/modules/hadoop-2.6.0# bin/hdfs dfs -text /user/root/data/wordcount.txt
hadoop hadoop
java java kotlin kotlin scala scala c python
hdfs yarn hdfs yarn hadoop
spark storm flink
hbase hive hbase hive mysql
kafka flume sqoop
root@bigdata-pro00:/opt/modules/hadoop-2.6.0# bin/hdfs dfs -text /user/root/mr/result/output/par*
c	1
flink	1
flume	1
hadoop	3
hbase	2
hdfs	2
...
```