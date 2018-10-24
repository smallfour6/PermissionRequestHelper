package com.smallfour6.permissionrequesthelper;

import android.app.Application;

import com.smallfour6.permission_lib.PermissionsHelper;

/**
 * @author zhaoxiaosi
 * @desc
 * @create 2018/10/23 下午3:33
 **/
public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PermissionsHelper.inject(this);
    }
}
