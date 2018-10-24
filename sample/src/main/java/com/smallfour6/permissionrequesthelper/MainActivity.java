package com.smallfour6.permissionrequesthelper;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.smallfour6.permission_annotation.PermissionDenied;
import com.smallfour6.permission_annotation.PermissionGranted;
import com.smallfour6.permission_annotation.PermissionRationale;
import com.smallfour6.permission_lib.PermissionsHelper;

import static com.smallfour6.permission_lib.RequestCode.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_read_sd = findViewById(R.id.btn_read_sd);
        btn_read_sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionsHelper.shouldShowRequestPermissionRationale(MainActivity.this, READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    PermissionsHelper.requestPermissions(MainActivity.this, READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
        });


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fl_container, new SampleFragment()).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    @PermissionGranted(READ_EXTERNAL_STORAGE)
    public void test2() {
        Toast.makeText(this, "权限申请成功", Toast.LENGTH_SHORT).show();
    }


    @PermissionDenied(READ_EXTERNAL_STORAGE)
    public void test4() {
        Toast.makeText(this, "权限申请失败", Toast.LENGTH_SHORT).show();
    }

    @PermissionRationale(READ_EXTERNAL_STORAGE)
    public void oo() {
        Toast.makeText(this, "我需要存储卡的权限，否则将会存储失败！", Toast.LENGTH_LONG).show();
        PermissionsHelper.requestPermissions(MainActivity.this, 2, Manifest.permission.READ_EXTERNAL_STORAGE);
    }
}
