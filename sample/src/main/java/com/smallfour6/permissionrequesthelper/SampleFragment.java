package com.smallfour6.permissionrequesthelper;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.smallfour6.permission_annotation.PermissionDenied;
import com.smallfour6.permission_annotation.PermissionGranted;
import com.smallfour6.permission_lib.PermissionsHelper;
import com.smallfour6.permission_lib.RequestCode;

/**
 * @author zhaoxiaosi
 * @desc
 * @create 2018/10/24 上午9:23
 **/
public class SampleFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sample, container, false);
        Button button = view.findViewById(R.id.btn_read_phone_state);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionsHelper.requestPermission(SampleFragment.this, RequestCode.READ_PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
            }
        });

        return view;
    }

    @PermissionGranted(RequestCode.READ_PHONE_STATE)
    public void success() {
        Toast.makeText(getContext(), "权限申请成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied(RequestCode.READ_PHONE_STATE)
    public void failure() {
        Toast.makeText(getContext(), "权限申请失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
