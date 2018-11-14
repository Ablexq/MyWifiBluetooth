

[android bluetooth——蓝牙的开启、搜索、配对与连接](https://blog.csdn.net/yehui928186846/article/details/52710112)


[Android 蓝牙搜索，配对，连接发送数据](https://www.jianshu.com/p/7c92cfc5ee6b)

[Android BroadcastReceiver + 蓝牙状态监听Demo](https://blog.csdn.net/qq_30574785/article/details/74990370)

[android获取bluetooth的信号强度(RSSI)](https://blog.csdn.net/tanghongchang123/article/details/51679024)

[手机蓝牙检测蓝牙设备信号强度（RSSI）](https://www.cnblogs.com/sovagxa/p/7620380.html)

[Android获取本机蓝牙地址](https://blog.csdn.net/chengjiamei/article/details/78833281)

[]()


# 蓝牙强度Rssi的取值范围

Rssi和接收功率有关，单位是dBm，一般为负值，反应的是信号的衰减程度，理想状态下（无衰减），Rssi = 0dBm，

实际情况是，即使蓝牙设备挨得非常近，Rssi也只有-50dBm的强度，在传输过程中，不可避免要损耗。

一般情况下，经典蓝牙强度 
```
-50 ~ 0dBm    信号强
-70 ~-50dBm   信号中
<-70dBm       信号弱
```

低功耗蓝牙分四级
```
-60 ~ 0      4
-70 ~ -60    3
-80 ~ -70    2
<-80         1
```










