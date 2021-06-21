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
# 验证设置reduce数量
```
job.setNumReduceTasks(2);

http://bigdata:50070/explorer.html#/user/root/mr/result/output 目录下有两个part*
root@bigdata:/opt/modules/hadoop-2.6.0# bin/hdfs dfs -text /user/root/mr/result/output/part-r-00000
c	1
flume	1
hadoop	3
hbase	2
hdfs	2
kotlin	2
spark	1
storm	1
root@bigdata:/opt/modules/hadoop-2.6.0# bin/hdfs dfs -text /user/root/mr/result/output/part-r-00001
flink	1
hive	2
java	2
kafka	1
mysql	1
python	1
scala	2
sqoop	1
yarn	2
```
# 调优参数(具体数值根据测试得到)
```
map任务输出时进行压缩
mapreduce.map.output.compress

环形缓冲区大小
mapreduce.task.io.sort.mb

达到环形缓冲区大小的什么比例开始写入到磁盘
mapreduce.map.sort.spill.percent

小文件数达到多少开始一次合并
mapreduce.task.io.sort.factor

map，reduce任务执行时，能使用的虚拟计算核心数量
mapreduce.map.cpu.vcores
mapreduce.reduce.cpu.vcores

map，reduce任务执行时，能使用的内存资源
mapreduce.map.memory.mb
mapreduce.reduce.memory.mb
```

# MR案例
## 数据
```
➜  datas pwd
/opt/datas
➜  datas ll
总用量 41M
-rw-rw-r-- 1 cjx cjx  38M 6月  17 22:59 2015082818
-rw-r--r-- 1 cjx cjx 448K 6月  17 22:59 movies.txt
-rw-r--r-- 1 cjx cjx 2.4M 6月  17 22:59 ratings.txt
➜  datas head -n 1 2015082818 
121508281810000000	http://www.yhd.com/?union_ref=7&cp=0			3	PR4E9HWE38DMN4Z6HUG667SCJNZXMHSPJRER					VFA5QRQ1N4UJNS9P6MH6HPA76SXZ737P	10977119545124.65.159.122		unionKey:10977119545		2015-08-28 18:10:00	50116447	http://image.yihaodianimg.com/virtual-web_static/virtual_yhd_iframe_index_widthscreen.html?randid=2015828	6	1000					Mozilla/5.0 (Windows NT 6.1; rv:40.0) Gecko/20100101 Firefox/40.0	Win32					lunbo_tab_3		北京市	2			北京市						1		1	1		1											1440*900				1440756285639
```
## 上传到HDFS
```
➜  datas docker cp ./2015082818 482324c29c5d:/opt/datas/
root@bigdata:/opt/modules/hadoop-2.6.0# bin/hdfs dfs -mkdir -p /user/root/datas/webpv
root@bigdata:/opt/modules/hadoop-2.6.0# bin/hdfs dfs -put /opt/datas/2015082818 /user/root/datas/webpv/
查看：http://bigdata:50070/explorer.html#/user/root/datas/webpv
```
## 打包，用Yarn运行
```
➜  Hadoop_jar git:(main) ✗ pwd
/git-projects/bigdata-study-cjx/Hadoop/out/artifacts/Hadoop_jar
➜  Hadoop_jar git:(main) ✗ docker cp Hadoop.jar 482324c29c5d:/opt/jars/
bin/yarn jar /opt/jars/Hadoop.jar /user/root/datas/webpv /user/root/mr/result/output
hadoop-2.6.0# bin/hdfs dfs -text /user/root/mr/result/output/part*
```

# 问题
## 问题一
Docker内 yarn执行任务，卡在running job
### 原因
Docker内分配的内存、CPU资源不足
### 解决方案
在yarn-site.xml中加:
```
<property>
    <name>yarn.nodemanager.resource.memory-mb</name>
    <value>20480</value>
</property>
<property>
   <name>yarn.scheduler.minimum-allocation-mb</name>
   <value>2048</value>
</property>
<property>
    <name>yarn.nodemanager.vmem-pmem-ratio</name>
    <value>2.1</value>
</property>
```
重启YARN：/opt/modules/hadoop-2.6.0/sbin/stop-yarn.sh  /opt/modules/hadoop-2.6.0/sbin/start-yarn.sh