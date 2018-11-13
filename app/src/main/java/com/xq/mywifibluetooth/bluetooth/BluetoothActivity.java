package com.xq.mywifibluetooth.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xq.mywifibluetooth.R;
import com.xq.mywifibluetooth.util.NotificationUtil;
import com.xq.mywifibluetooth.util.TimeUtil;
import com.xq.mywifibluetooth.wifi.MyAdapter;

import java.util.ArrayList;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener {

    private BluetoothAdapter mBluetoothAdapter;
    private Button btnOpen;
    private Button btnClose;
    private MyAdapter myAdapter;
    private ArrayList<String> msgLists = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);

        initViews();
        initRv();
        initBlueTooth();
    }

    private void initRv() {
        RecyclerView recyclerView = (RecyclerView) this.findViewById(R.id.rv_blue);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        myAdapter = new MyAdapter(this, msgLists);
        recyclerView.setAdapter(myAdapter);
    }

    private void initViews() {
        btnOpen = (Button) this.findViewById(R.id.open);
        btnClose = (Button) this.findViewById(R.id.close);
        btnOpen.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open:
                if (!mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.enable();
                    Toast.makeText(getApplicationContext(), "蓝牙打开成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "蓝牙已经打开", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.close:
                if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.disable();
                    Toast.makeText(getApplicationContext(), "蓝牙关闭成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "蓝牙已经关闭", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initBlueTooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device;
            String time = TimeUtil.getTime();
            switch (action) {
                //连接时
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                        String name = device.getName();
                        msgLists.add(time + "连接的设备 名称：" + name + " 信号强度：" + rssi);
                        myAdapter.notifyDataSetChanged();
                        NotificationUtil.sendNotification(context.getApplicationContext(),
                                NotificationUtil.NOTIFIED_4,
                                "连接的蓝牙设备", "名称：" + name + " 信号强度：" + rssi);
                    }
                    break;

                //断开连接时
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                        String name = device.getName();
                        msgLists.add(time + "断开的设备 名称：" + name + " 信号强度：" + rssi);
                        myAdapter.notifyDataSetChanged();
                        NotificationUtil.sendNotification(context.getApplicationContext(),
                                NotificationUtil.NOTIFIED_4,
                                "断开的蓝牙设备", "名称：" + name + " 信号强度：" + rssi);
                    }
                    break;
            }
        }
    };
}
