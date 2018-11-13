
参考：

[Android-WiFi开发之 WifiManager](https://www.jianshu.com/p/67aaf1fdb921)

[TomChenS/WifiConnection](https://github.com/TomChenS/WifiConnection)

[codingbooo/wifiUtils](https://github.com/codingbooo/wifiUtils)


```
// 获取 WifiManager 实例. 
public static WifiManager getWifiManager(Context context) {
    return context == null ? null : (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
}
```

```
// 开启/关闭 WIFI.
public static boolean setWifiEnabled(WifiManager manager, boolean enabled) {
    return manager != null && manager.setWifiEnabled(enabled);
}

```


```
// 获取 WIFI 的状态.
public static int getWifiState(WifiManager manager) {
    return manager == null ? WifiManager.WIFI_STATE_UNKNOWN : manager.getWifiState();
}

/**
 * 注意:
 * WiFi 的状态目前有五种, 分别是:
 *  WifiManager.WIFI_STATE_ENABLING: WiFi正要开启的状态, 是 Enabled 和 Disabled 的临界状态;
 *  WifiManager.WIFI_STATE_ENABLED: WiFi已经完全开启的状态;
 *  WifiManager.WIFI_STATE_DISABLING: WiFi正要关闭的状态, 是 Disabled 和 Enabled 的临界状态;
 *  WifiManager.WIFI_STATE_DISABLED: WiFi已经完全关闭的状态;
 *  WifiManager.WIFI_STATE_UNKNOWN: WiFi未知的状态, WiFi开启, 关闭过程中出现异常, 或是厂家未配备WiFi外挂模块会出现的情况;
*/

```






# ACTION:  WifiManager.WIFI_STATE_CHANGED_ACTION （ "android.net.wifi.WIFI_STATE_CHANGED"）

wifi状态变化触发，触发两次。

可接收两个信息：（键） 

（这个是监听wifi状态变化的，wifi状态变化并不包括wifi的连接状态，只是单纯的指示wifi的5种状态）


1. WifiManager.EXTRA_PREVIOUS_WIFI_STATE（"previous_wifi_state"） 

int型值  intent.getIntExtra("键",0);（第二个为得不到时的默认值）

2. WifiManager.EXTRA_WIFI_STATE（"wifi_state"） 

int型值   intent.getIntExtra("键",0);（第二个为得不到时的默认值）



# ACTION: WifiManager.NETWORK_STATE_CHANGED_ACTION（"android.net.wifi.STATE_CHANGE"）

wifi连接网络的状态广播，连接过程中接收多次，

在连接过程中可与获取NetworkInfo对象，通过ni.getState()可以获取wifi连接状态。

如果连接state处于connected状态，可以通过WifiManager.EXTRA_WIFI_INFO得到wifiInfo对象。


可接收两个信息：（键）

1. WifiManager.EXTRA_BSSID（"bssid"）         

String类型值      intent.getStringExtra("键")

2. WifiManager.EXTRA_NETWORK_INFO（"networkInfo"）   

T    getParcelableExtra(“键”)



# ACTION: WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION（ "android.net.wifi.supplicant.CONNECTION_CHANGE"） 判断是否WIFI打开了，变化触发一次

可接收一个信息：（键）（这个应该是广播wifi启动状态的，true表示连接到wifi设备，

一般在wifi state处于enable时得到，false则表示断开设备连接，wifi此时状态为disabling。

这只是指机器内的wifi连接状态变化，与网络无关）

1. WifiManager.EXTRA_SUPPLICANT_CONNECTED（"connected"）    

boolean类型返回值   intent.getBooleanExtra(“键”, true)第二个是默认值



# ACTION: WifiManager.SUPPLICANT_STATE_CHANGED_ACTION（"android.net.wifi.supplicant.STATE_CHANGE"）发送WIFI连接的过程信息，如果出错ERROR信息才会收到。连接WIFI时触发，触发多次。

可接收两个信息：（键）

1. WifiManager.EXTRA_NEW_STATE（"newState"）           

intent.getParcelableExtra("键")

2. WifiManager.EXTRA_SUPPLICANT_ERROR（"supplicantError"）        

int型值  getIntExtra(“键”, int)



# WIFI状态int值对应的状态：

```
/**
 * Wi-Fi is currently being disabled. The state will change to {@link #WIFI_STATE_DISABLED} if
 * it finishes successfully.
 *                                      正在关闭
 * @see #WIFI_STATE_CHANGED_ACTION
 * @see #getWifiState()
 */
public static final int WIFI_STATE_DISABLING = 0;
/**
 * Wi-Fi is disabled.                   不可用
 *
 * @see #WIFI_STATE_CHANGED_ACTION
 * @see #getWifiState()
 */
public static final int WIFI_STATE_DISABLED = 1;
/**
 * Wi-Fi is currently being enabled. The state will change to {@link #WIFI_STATE_ENABLED} if
 * it finishes successfully.
 *                                      正在开启
 * @see #WIFI_STATE_CHANGED_ACTION
 * @see #getWifiState()
 */
public static final int WIFI_STATE_ENABLING = 2;
/**
 * Wi-Fi is enabled.
 *                                      可用
 * @see #WIFI_STATE_CHANGED_ACTION
 * @see #getWifiState()
 */
public static final int WIFI_STATE_ENABLED = 3;
/**
 * Wi-Fi is in an unknown state. This state will occur when an error happens while enabling
 * or disabling.
 *                                      未知
 * @see #WIFI_STATE_CHANGED_ACTION
 * @see #getWifiState()
 */
public static final int WIFI_STATE_UNKNOWN = 4;

```