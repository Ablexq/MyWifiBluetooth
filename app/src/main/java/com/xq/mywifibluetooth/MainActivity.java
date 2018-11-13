package com.xq.mywifibluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xq.mywifibluetooth.bluetooth.BluetoothActivity;
import com.xq.mywifibluetooth.wifi.WifiActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.findViewById(R.id.wifi).setOnClickListener(this);
        this.findViewById(R.id.bluetooth).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wifi:
                startActivity(new Intent(this, WifiActivity.class));
                break;

            case R.id.bluetooth:
                startActivity(new Intent(this, BluetoothActivity.class));
                break;
        }
    }
}
