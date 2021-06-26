# 数据准备
```
docker cp secondSort.txt 482324c29c5d:/opt/datas/
root@bigdata:/opt/datas# ll
total 38524
drwxr-xr-x 2 root root     4096 Jun 26 09:56 ./
drwxr-xr-x 1 root root     4096 Jun 21 15:12 ../
-rw-rw-r-- 1 1000 1000 39425518 Jun 17 14:59 2015082818
-rw-rw-r-- 1 1000 1000       67 Jun 24 13:35 secondSort.txt
-rw-r--r-- 1 root root      150 Jun 14 12:38 wordcount.txt

root@bigdata:/opt/modules/hadoop-2.6.0# bin/hdfs dfs -put /opt/datas/secondSort.txt /user/root/datas/
root@bigdata:/opt/modules/hadoop-2.6.0# bin/hdfs dfs -text /user/root/datas/secondSort.txt
a 12
b 34
c 90
b 23
b 13
b 20
c 56
b 94
b 5
c 33
a 9
a 50
a 20
c 40
```
