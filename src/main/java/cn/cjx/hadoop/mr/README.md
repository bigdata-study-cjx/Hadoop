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
# 打包在YARN上面测试
```
docker cp Hadoop.jar b1c00f9b6edc:/opt/jars/
root@bigdata-pro00:/opt/modules/hadoop-2.6.0# sbin/start-yarn.sh
后面两个文件输入路径和结果输出路径，都是HDFS的路径。
root@bigdata-pro00:/opt/modules/hadoop-2.6.0# bin/yarn jar /opt/jars/Hadoop.jar /user/root/data/wordcount.txt /user/root/mr/result/output2
root@bigdata-pro00:/opt/modules/hadoop-2.6.0# bin/hdfs dfs -text /user/root/mr/result/output2/par*
c	1
flink	1
flume	1
hadoop	3
hbase	2
...

```