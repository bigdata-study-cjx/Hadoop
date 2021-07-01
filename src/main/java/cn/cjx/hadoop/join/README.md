# 数据准备
```
root@bigdata:/opt/datas# cat custom.txt 
1,jone,13133441234
2,ben,15511112222
3,henry,13533334444
4,tony,13988886666

root@bigdata:/opt/datas# cat order.txt 
100,1,45.50,product-1
200,1,23,product-2
300,1,50,product-3
400,1,99,product-4
102,2,19.9,product-5
103,2,33,product-6
104,3,44,product-7
105,4,1009,product-8
106,5,22,product-9

root@bigdata:/opt/modules/hadoop-2.6.0# bin/hdfs dfs -mkdir /user/root/datas/join/
root@bigdata:/opt/modules/hadoop-2.6.0# bin/hdfs dfs -put /opt/datas/custom.txt /user/root/datas/join/
root@bigdata:/opt/modules/hadoop-2.6.0# bin/hdfs dfs -put /opt/datas/order.txt /user/root/datas/join/
```