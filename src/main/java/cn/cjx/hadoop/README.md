# 运行单机hadoop2.6.0容器
```
docker run -it -p 8088:8088 -p 50070:50070 -p 50075:50075 --net bigdata --ip 172.168.0.2 --name hadoop2.6.0 --hostname bigdata dockerhubbigdata/hadoop:2.6.0-alone /bin/bash

service ssh start

export JAVA_HOME=/opt/modules/jdk1.8.0_271
export PATH=$JAVA_HOME/bin:$PATH

/opt/modules/hadoop-2.6.0/sbin/start-dfs.sh
/opt/modules/hadoop-2.6.0/sbin/start-yarn.sh
~# jps
787 SecondaryNameNode
599 DataNode
970 ResourceManager
459 NameNode
1420 Jps
1085 NodeManager
```
# WebUI
```
http://bigdata:50070/explorer.html#/user/root/data
http://bigdata:8088/cluster
```