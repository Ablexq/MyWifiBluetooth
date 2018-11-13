package com.xq.mywifibluetooth.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xq.mywifibluetooth.R;

public class BluetoothActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_ACCESS_LOCATION = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private TextView matchTv, matchInfoTv, disMatchTv;
    private Button botton;
    private static final String TAG = "BluetoothActivity";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);

        initViews();
        initBlueTooth();

        botton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                search();
            }
        });
    }

    private void search() {
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        mBluetoothAdapter.startDiscovery();
        matchInfoTv.setText("正在搜索...");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initBlueTooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, intentFilter);
    }

    private void initViews() {
        matchTv = (TextView) this.findViewById(R.id.textView);  //已配对
        matchInfoTv = (TextView) this.findViewById(R.id.textView2); //状态信息
        disMatchTv = (TextView) this.findViewById(R.id.textView3); //未配对
        botton = (Button) this.findViewById(R.id.button);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        //接收
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device;
            switch (action) {
                // 搜索发现设备时，取得设备的信息；注意，这里有可能重复搜索同一设备
                case BluetoothDevice.ACTION_FOUND:
                    matchInfoTv.setText("找到新设备了");
                    break;

                //状态改变时
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (device.getBondState()) {
                        case BluetoothDevice.BOND_BONDING://正在配对
                            Log.d(TAG, "正在配对......");
                            matchInfoTv.setText("正在配对...");
                            break;
                        case BluetoothDevice.BOND_BONDED://配对结束
                            Log.d(TAG, "配对成功！！！");
                            matchTv.append("\n" + device.getName() + "  " + device.getAddress() + "\n");
                            break;
                        case BluetoothDevice.BOND_NONE://取消配对/未配对
                            Log.d(TAG, "取消配对~~~");
                            disMatchTv.append("\n" + device.getName() + "   " + device.getAddress() + "\n");
                        default:
                            break;
                    }
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    matchInfoTv.setText("搜索完成...");
                    break;
            }
        }
    };
}
