package com.xq.mywifibluetooth.wifi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xq.mywifibluetooth.R;
import com.xq.mywifibluetooth.util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WifiActivity extends Activity implements View.OnClickListener {

    private LocalBroadcastManager mLocalBroadcastManager;
    private ArrayList<String> msgLists = new ArrayList<>();
    private MyAdapter myAdapter;
    private Button bt_close;
    private Button bt_open;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        initBtn();
        initRv();

        // 取得WifiManager对象
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();

        startService(new Intent(this, WifiService.class));
        register();
    }

    private void initBtn() {
        bt_close = (Button) findViewById(R.id.bt_close);
        bt_open = (Button) findViewById(R.id.bt_open);
        bt_close.setOnClickListener(this);
        bt_open.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void register() {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiReceiver.ACTION_WIFI_INFO);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, filter);
    }

    private void initRv() {
        RecyclerView recyclerView = (RecyclerView) this.findViewById(R.id.rv_msg);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        myAdapter = new MyAdapter(this, msgLists);
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String time = TimeUtil.getTime();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(time)
                    .append("\n")
                    .append("收到wifi信息为：\n");
            if (!TextUtils.isEmpty(action) && action.equals(WifiReceiver.ACTION_WIFI_INFO)) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    String bssid = extras.getString(WifiReceiver.BSSID);
                    if (!TextUtils.isEmpty(bssid)) {
                        stringBuilder.append("bssid : ").append(bssid).append("\n");
                    }
                    String ssid = extras.getString(WifiReceiver.SSID);
                    if (!TextUtils.isEmpty(ssid)) {
                        stringBuilder.append("ssid : ").append(ssid).append("\n");
                    }
                    String ip = extras.getString(WifiReceiver.IP_ADDRESS);
                    if (!TextUtils.isEmpty(ip)) {
                        stringBuilder.append("ip : ").append(ip).append("\n");
                    }
                    String rssi = extras.getString(WifiReceiver.RSSI);
                    if (!TextUtils.isEmpty(rssi)) {
                        stringBuilder.append("rssi : ").append(rssi).append("\n");
                    }
                }
            }
            msgLists.add(stringBuilder.toString());
            myAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_open:
                if (!mWifiManager.isWifiEnabled()) {
                    mWifiManager.setWifiEnabled(true);
                    Toast.makeText(getApplicationContext(), "wifi打开成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "wifi已经打开", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.bt_close:
                if (mWifiManager.isWifiEnabled()) {
                    mWifiManager.setWifiEnabled(false);
                    Toast.makeText(getApplicationContext(), "WiFi关闭成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "wifi已经关闭", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}