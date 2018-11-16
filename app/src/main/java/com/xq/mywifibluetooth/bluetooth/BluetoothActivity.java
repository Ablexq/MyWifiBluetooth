package com.xq.mywifibluetooth.bluetooth;

import android.annotation.SuppressLint;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xq.mywifibluetooth.R;
import com.xq.mywifibluetooth.util.ExcelUtil;
import com.xq.mywifibluetooth.util.NotificationUtil;
import com.xq.mywifibluetooth.util.TimeUtil;
import com.xq.mywifibluetooth.wifi.MyAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener {

    private BluetoothAdapter mBluetoothAdapter;
    private MyAdapter myAdapter;
    private ArrayList<String> msgLists = new ArrayList<>();
    private TextView mTvBt;

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
        Button btnOpen = (Button) this.findViewById(R.id.open);
        mTvBt = (TextView) this.findViewById(R.id.localbt);
        Button btnClose = (Button) this.findViewById(R.id.close);
        btnOpen.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }

    @SuppressLint("SwitchIntDef")
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

    @SuppressLint("SwitchIntDef")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initBlueTooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, intentFilter);

        //获取状态码
        int state = mBluetoothAdapter.getState();
        //判断蓝牙状态
        switch (state) {
            case BluetoothAdapter.STATE_CONNECTED:
                Toast.makeText(this, "判断状态为连接中", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_CONNECTING:
                Toast.makeText(this, "判断状态为连接", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_DISCONNECTED:
                Toast.makeText(this, "判断状态为断开", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_DISCONNECTING:
                Toast.makeText(this, "判断状态为断中", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_OFF:
                Toast.makeText(this, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
                setMac();
                break;
            case BluetoothAdapter.STATE_ON:
                Toast.makeText(this, "蓝牙已打开", Toast.LENGTH_SHORT).show();
                setMac();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("SwitchIntDef")
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            //获取蓝牙设备实例【如果无设备链接会返回null，如果在无实例的状态下调用了实例的方法，会报空指针异常】
            //主要与蓝牙设备有关系
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String time = TimeUtil.getTime();
            short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
            String name = null;
            if (device != null) {
                name = device.getName();
            }
            String [] sheets = {"设备名称", "连接的设备名称", "状态", "信号强度"};
            String [] codeList = {};
            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    Toast.makeText(context, "蓝牙设备:" + name + "已链接", Toast.LENGTH_SHORT).show();
                    System.out.println("==================已连接=======================");
                    msgLists.add(time + "连接的设备 名称：" + name + " 信号强度：" + rssi);
                    myAdapter.notifyDataSetChanged();
                    NotificationUtil.sendNotification(context.getApplicationContext(),
                            NotificationUtil.NOTIFIED_4,
                            "连接的蓝牙设备", "名称：" + name + "     信号强度：" + rssi);
                    codeList = new String[]{getBtAddressByReflection(),name, "已连接", String.valueOf(rssi)};
                    break;

                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Toast.makeText(context, "蓝牙设备:" + name + "断开链接", Toast.LENGTH_SHORT).show();
                    System.out.println("==================已断开连接=======================");
                    msgLists.add(time + "断开的设备 名称：" + name + " 信号强度：" + rssi);
                    myAdapter.notifyDataSetChanged();
                    NotificationUtil.sendNotification(context.getApplicationContext(),
                            NotificationUtil.NOTIFIED_4,
                            "断开的蓝牙设备", "名称：" + name + "     信号强度：" + rssi);
                    sheets = new String[]{"设备名称", "连接的设备名称", "状态", "信号强度"};
                    codeList = new String[]{getBtAddressByReflection(),name, "已断开", String.valueOf(rssi)};
                    break;

                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    System.out.println("=================ACTION_BOND_STATE_CHANGED======================");
                    if (device != null) {
                        String status = "";
                        switch (device.getBondState()) {
                            case BluetoothDevice.BOND_NONE:
                                status = "取消配对";
                                break;
                            case BluetoothDevice.BOND_BONDING:
                                status = "配对中";
                                break;
                            case BluetoothDevice.BOND_BONDED:
                                status = "配对成功";
                                break;
                        }
                        System.out.println("=================="+status+"=======================");
                        codeList = new String[]{status};
                    }
                    break;

                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    String statusMsg = "";
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            statusMsg = "蓝牙已关闭";
                            setMac();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            statusMsg = "蓝牙正在关闭";
                            break;
                        case BluetoothAdapter.STATE_ON:
                            statusMsg = "蓝牙已开启";
                            setMac();
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            statusMsg = "蓝牙正在开启";
                            break;
                    }
                    System.out.println("=================="+statusMsg+"=======================");
                    codeList = new String[]{statusMsg};
                    break;

                default:
                    break;
            }
            try {
                ExcelUtil.saveExcel(codeList, sheets, "蓝牙");
            } catch (Exception e) {
                Log.e("BAG", "蓝牙数据写入excel失败", e);
            }
        }
    };

    private void setMac() {
        String btAddressByReflection = "本机MAC： " + getBtAddressByReflection();
        mTvBt.setText(btAddressByReflection);
    }

    public static String getBtAddressByReflection() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Field field;
        try {
            field = BluetoothAdapter.class.getDeclaredField("mService");
            field.setAccessible(true);
            Object bluetoothManagerService = field.get(bluetoothAdapter);
            if (bluetoothManagerService == null) {
                return null;
            }
            Method method = bluetoothManagerService.getClass().getMethod("getAddress");
            if (method != null) {
                Object obj = method.invoke(bluetoothManagerService);
                if (obj != null) {
                    return obj.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
