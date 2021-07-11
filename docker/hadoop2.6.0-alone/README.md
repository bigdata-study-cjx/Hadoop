# 添加hadoop2.6.0
## 下载hadoop2.6.0
https://archive.apache.org/dist/hadoop/core/hadoop-2.6.0/hadoop-2.6.0.tar.gz
## 安装测试hadoop
```
8088:yarn wenui端口
19888:yarn 任务列表
50070:hadoop webui端口
50075:hadoop 下载文件端口
➜ ~ docker run -it -p 8088:8088 -p 19888:19888 -p 50070:50070 -p 50075:50075 --net bigdata --ip 172.168.0.2 --name hadoop2.6.0 --hostname bigdata-pro00 dockerhubbigdata/bigdata:jdk8 /bin/bash
root@bigdata-pro00:~# 
docker cp hadoop-2.6.0.tar.gz 37dc36e9d1b5:/opt/modules/
root@bigdata-pro00:~# cd /opt/modules/
root@bigdata-pro00:/opt/modules# tar -zxf hadoop-2.6.0.tar.gz -C ./
root@bigdata-pro00:/opt/modules# rm hadoop-2.6.0.tar.gz
```
### 配置hadoop单机版
```
root@bigdata-pro00:/opt/modules# cd hadoop-2.6.0/
root@bigdata-pro00:/opt/modules/hadoop-2.6.0# rm -rf share/doc/
root@bigdata-pro00:/opt/modules/hadoop-2.6.0# rm -rf etc/hadoop/*.cmd
root@bigdata-pro00:/opt/modules/hadoop-2.6.0# cd etc/hadoop/
root@bigdata-pro00:/opt/modules/hadoop-2.6.0/etc/hadoop# mkdir -p /opt/modules/hadoop-2.6.0/tmp
```
#### 导入配置文件
```
docker cp copy-files/ 37dc36e9d1b5:/opt/modules/hadoop-2.6.0/etc/hadoop/
root@bigdata-pro00:/opt/modules/hadoop-2.6.0/etc/hadoop# cp copy-files/* ./
root@bigdata-pro00:/opt/modules/hadoop-2.6.0/etc/hadoop# rm -rf copy-files/
```
### 运行测试
```
service ssh start

root@bigdata-pro00:/opt/modules/hadoop-2.6.0/etc/hadoop# export JAVA_HOME=/opt/modules/jdk1.8.0_271
root@bigdata-pro00:/opt/modules/hadoop-2.6.0/etc/hadoop# export PATH=$JAVA_HOME/bin:$PATH

格式化namenode
root@bigdata-pro00:/opt/modules/hadoop-2.6.0/etc/hadoop# /opt/modules/hadoop-2.6.0/bin/hdfs namenode -format
启动hdfs: 
root@bigdata-pro00:/opt/modules/hadoop-2.6.0/etc/hadoop# /opt/modules/hadoop-2.6.0/sbin/start-dfs.sh
root@bigdata-pro00:/opt/modules/hadoop-2.6.0/etc/hadoop# jps
418 DataNode
279 NameNode
759 Jps
601 SecondaryNameNode
启动yarn:
root@bigdata-pro00:/opt/modules/hadoop-2.6.0/etc/hadoop# /opt/modules/hadoop-2.6.0/sbin/start-yarn.sh
root@bigdata-pro00:/opt/modules/hadoop-2.6.0/etc/hadoop# jps
418 DataNode
279 NameNode
823 ResourceManager
601 SecondaryNameNode
937 NodeManager
1257 Jps

root@bigdata-pro00:/opt/modules/hadoop-2.6.0# /opt/modules/hadoop-2.6.0/bin/hdfs dfs -mkdir -p /user/root/data/
root@bigdata-pro00:/opt/modules/hadoop-2.6.0# /opt/modules/hadoop-2.6.0/bin/hdfs dfs -put /opt/modules/hadoop-2.6.0/etc/hadoop/core-site.xml /user/root/data
root@bigdata-pro00:/opt/modules/hadoop-2.6.0# /opt/modules/hadoop-2.6.0/bin/hdfs dfs -text /user/root/data/core-site.xml

WebUI查看：
```
### 提交镜像
```
docker commit hadoop2.6.0 dockerhubbigdata/hadoop:2.6.0-alone
docker push dockerhubbigdata/hadoop:2.6.0-alone
```
### 宿主机局域网测试
```
docker run -it -p 8088:8088 -p 50070:50070 -p 50075:50075 --net bigdata --ip 172.168.0.2 --name hadoop2.6.0 --hostname bigdata-pro00 dockerhubbigdata/hadoop:2.6.0-alone /bin/bash

service ssh start

/opt/modules/hadoop-2.6.0/etc/hadoop# export JAVA_HOME=/opt/modules/jdk1.8.0_271
/opt/modules/hadoop-2.6.0/etc/hadoop# export PATH=$JAVA_HOME/bin:$PATH

/opt/modules/hadoop-2.6.0/etc/hadoop# /opt/modules/hadoop-2.6.0/sbin/start-dfs.sh
/opt/modules/hadoop-2.6.0/etc/hadoop# /opt/modules/hadoop-2.6.0/sbin/start-yarn.sh
~# jps
787 SecondaryNameNode
599 DataNode
970 ResourceManager
459 NameNode
1420 Jps
1085 NodeManager
```
#### WebUI测试
```
http://bigdata-pro00:50070/explorer.html#/user/root/data
下载时注意把容器id换成宿主机ip
http://bigdata-pro00:8088/cluster
```