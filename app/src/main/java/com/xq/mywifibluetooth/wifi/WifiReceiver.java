package com.xq.mywifibluetooth.wifi;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;

import com.xq.mywifibluetooth.util.NotificationUtil;

/**
 * 监听Wfif状态广播接收者
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
@SuppressWarnings("deprecation")
public class WifiReceiver extends BroadcastReceiver {

    public static final String ACTION_WIFI_INFO = "com.xq.wifi.info";
    public static final String BSSID = "bssid";
    public static final String SSID = "ssid";
    public static final String RSSI = "rssi";
    public static final String IP_ADDRESS = "ip_address";

    @Override
    public void onReceive(Context context, Intent intent) {
        wifiStateChanged(context, intent);
        wifiIsConnected(context, intent);
        isConnected(context, intent);
    }

    /**
     * 监听网络是否连接
     */
    private void isConnected(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifi.isConnected()) {
                NotificationUtil.sendNotification(context.getApplicationContext(),
                        NotificationUtil.NOTIFIED_3,
                        "网络是否连接", "wifi已连接");
            } else if (gprs.isConnected()) {
                NotificationUtil.sendNotification(context.getApplicationContext(),
                        NotificationUtil.NOTIFIED_3,
                        "网络是否连接", "GPRS已连接");
            } else {
                NotificationUtil.sendNotification(context.getApplicationContext(),
                        NotificationUtil.NOTIFIED_3,
                        "网络是否连接", "未连接WiFi或GPRS");
            }
            setMessage(context);
        }
    }

    /**
     * 监听Wifi网络否连接
     */
    private void wifiIsConnected(Context context, Intent intent) {
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                NetworkInfo.State state = networkInfo.getState();
                boolean isConnected = (state == NetworkInfo.State.CONNECTED);
                if (isConnected) {
                    NotificationUtil.sendNotification(context.getApplicationContext(),
                            NotificationUtil.NOTIFIED_2,
                            "Wifi网络是否连接", "是");
                } else {
                    NotificationUtil.sendNotification(context.getApplicationContext(),
                            NotificationUtil.NOTIFIED_2,
                            "Wifi网络是否连接", "否");
                }
            }
        }
    }

    private void setMessage(Context context) {
        @SuppressLint("WifiManagerPotentialLeak")
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        String ssid = (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
        int rssi = (mWifiInfo == null) ? 0 : mWifiInfo.getRssi();
        String bssid = (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
        int ip = (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
        setBroadcast(context, ssid, "" + rssi, bssid, "" + ip);
    }

    /**
     * 监听Wifi功能是否开启
     */
    private void wifiStateChanged(Context context, Intent intent) {
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 这个监听wifi的打开与关闭，与wifi的连接无关
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    NotificationUtil.sendNotification(context.getApplicationContext(),
                            NotificationUtil.NOTIFIED_1,
                            "Wifi功能是否开启", "否");
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    NotificationUtil.sendNotification(context.getApplicationContext(),
                            NotificationUtil.NOTIFIED_1,
                            "Wifi功能是否开启", "是");
                    break;
            }
        }
    }

    private void setBroadcast(Context context, String getSSID, String getRssi, String getBSSID, String getIpAddress) {
        LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent();
        intent.putExtra(BSSID, getBSSID);
        intent.putExtra(SSID, getSSID);
        intent.putExtra(IP_ADDRESS, getIpAddress);
        intent.putExtra(RSSI, getRssi);
        intent.setAction(WifiReceiver.ACTION_WIFI_INFO);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

}