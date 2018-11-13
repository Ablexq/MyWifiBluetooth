package com.xq.mywifibluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.xq.mywifibluetooth.wifi.WifiReceiver;
import com.xq.mywifibluetooth.wifi.WifiService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity {

    private LocalBroadcastManager mLocalBroadcastManager;
    private ArrayList<String> msgLists = new ArrayList<>();
    private MyAdapter myAdapter;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) this.findViewById(R.id.rv_msg);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        myAdapter = new MyAdapter(this, msgLists);
        recyclerView.setAdapter(myAdapter);

        startService(new Intent(this, WifiService.class));

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiReceiver.ACTION_WIFI_INFO);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String time = getTime();
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

    private String getTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return format.format(date);
    }
}