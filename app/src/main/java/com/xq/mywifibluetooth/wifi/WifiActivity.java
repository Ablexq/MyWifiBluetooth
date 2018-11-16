package com.xq.mywifibluetooth.wifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xq.mywifibluetooth.R;
import com.xq.mywifibluetooth.util.TimeUtil;

import java.io.File;
import java.util.ArrayList;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

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
                    try {
                        saveExcel(bssid, ssid, ip, rssi);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            msgLists.add(stringBuilder.toString());
            myAdapter.notifyDataSetChanged();
        }

    };

    public static String getExcelDir() {
        // SD卡指定文件夹
        String sdcardPath = Environment.getExternalStorageDirectory().toString();
        File dir = new File(sdcardPath + File.separator + "Excel"+ File.separator + "Person");
        if (dir.exists()) {
            return dir.toString();
        } else {
            dir.mkdirs();
            Log.e("BAG", "保存路径不存在,");
            return dir.toString();
        }
    }

    private void saveExcel(String bssid, String ssid, String ip, String rssi) throws Exception{
        String excelPath = getExcelDir()+ File.separator+"wify与蓝牙记录表.xls";
        File file = new File(excelPath);
        WritableSheet ws = null;
        WritableWorkbook wwb = null;
        if (!file.exists()) {
            wwb = Workbook.createWorkbook(file);
            ws = wwb.createSheet("wifi信息", 0);
            // 在指定单元格插入数据
            Label lbl1 = new Label(0, 0, "bssid");
            Label lbl2 = new Label(1, 0, "ssid");
            Label lbl3 = new Label(2, 0, "ip");
            Label lbl4 = new Label(3, 0, "rssi");
            ws.addCell(lbl1);
            ws.addCell(lbl2);
            ws.addCell(lbl3);
            ws.addCell(lbl4);
        }else {
            Workbook oldWwb = Workbook.getWorkbook(file);
            wwb = Workbook.createWorkbook(file, oldWwb);
            ws = wwb.getSheet(0);
        }
        addExcelData(bssid, ssid, ip, rssi, ws);
        wwb.write();
        wwb.close();
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
    }

    public void addExcelData(String bssid, String ssid, String ip, String rssi, WritableSheet ws) throws Exception {
        // 当前行数
        int row = ws.getRows();
        Label lab1 = new Label(0, row, bssid);
        Label lab2 = new Label(1, row, ssid);
        Label lab3 = new Label(2, row, ip);
        Label lab4 = new Label(3, row, rssi);
        ws.addCell(lab1);
        ws.addCell(lab2);
        ws.addCell(lab3);
        ws.addCell(lab4);
    }

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