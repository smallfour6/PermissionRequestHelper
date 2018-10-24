package com.smallfour6.permission_lib;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhaoxiaosi
 * @desc
 * @create 2018/10/23 下午2:06
 **/
public class PermissionsHelper {
    private static final String SUFFIX = "$$Permission";

    private static final Map<String, IPermissionProxy> pools = new HashMap<>();//缓冲池，减少内存消耗
    private static boolean isInject = false;


    public static void requestPermissions(Activity activity, int requestCode, String... permissions) {
        _requestPermission(activity, requestCode, permissions);
    }

    public static void requestPermission(Fragment fragment, int requestCode, String... permissions) {
        _requestPermission(fragment, requestCode, permissions);
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity, int requestCode, String permission) {
        return _shouldShowRequestPermissionRationale(activity, requestCode, permission);
    }

    public static boolean shouldShowRequestPermissionRationale(Fragment fragment, int requestCode, String permission) {
        return _shouldShowRequestPermissionRationale(fragment, requestCode, permission);
    }

    private static boolean _shouldShowRequestPermissionRationale(Object object, int requestCode, String permission) {
        //有相关的 @PermissionRationale 标记的方法 且需要提供解释时，走此逻辑
        //ActivityCompat.shouldShowRequestPermissionRationale(findActivity(object),deniedList.get(0)) 第一次调用时 为 false，即只有当用户拒绝权限之后再次调用，返回值为 false。如果用户点击永远不提示，则返回为 false
        if (findProxy(object).needExecuteRationale(requestCode) && ActivityCompat.shouldShowRequestPermissionRationale(findActivity(object), permission)) {
            findProxy(object).rationale(object, requestCode);
            return true;
        }
        return false;
    }

    public static void inject(Application application) {

        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                pools.remove(activity.getClass().getName());
            }
        });

        isInject = true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static void _requestPermission(Object object, int requestCode, String... permissions) {
        if (!isInject) {
            throw new RuntimeException("please inject first, on application onCreate()");
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//低版本安装时直接获取权限
            doExecuteSuccess(object, requestCode);
            return;
        }

        List<String> deniedList = findDeniedPermissions(findActivity(object), permissions);

        if (deniedList.size() > 0) {
            if (object instanceof Activity) {
                ((Activity) object).requestPermissions(deniedList.toArray(new String[deniedList.size()]), requestCode);
            } else if (object instanceof Fragment) {
                ((Fragment) object).requestPermissions(deniedList.toArray(new String[deniedList.size()]), requestCode);
            } else {
                throw new IllegalArgumentException(object.getClass().getSimpleName() + "is not support");
            }
        } else {
            doExecuteSuccess(object, requestCode);
        }

    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        _onRequestPermissionResult(activity, requestCode, permissions, grantResults);
    }

    public static void onRequestPermissionsResult(Fragment fragment, int requestCode, String[] permissions, int[] grantResults) {
        _onRequestPermissionResult(fragment, requestCode, permissions, grantResults);
    }

    private static void doExecuteSuccess(Object object, int requestCode) {
        findProxy(object).granted(object, requestCode);
    }

    private static void doExecuteFailure(Object object, int requestCode) {
        findProxy(object).denied(object, requestCode);
    }

    private static void doExecuteRationale(Object object, int requestCode) {

    }

    private static IPermissionProxy findProxy(Object object) {

        String className = object.getClass().getName();
        if (pools.get(className) != null) {
            return pools.get(className);
        }

        Class clazz = object.getClass();
        try {
            Class proxyClass = Class.forName(clazz.getName() + SUFFIX);
            pools.put(className, (IPermissionProxy) proxyClass.newInstance());
            return pools.get(className);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("something was wrong when compiler!");
    }

    private static void _onRequestPermissionResult(Object object, int requestCode, String[] permissions, int[] grantResults) {
        if (!isInject) {
            throw new RuntimeException("please inject first, on application onCreate()");
        }

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            doExecuteSuccess(object, requestCode);
        } else {
            doExecuteFailure(object, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static List<String> findDeniedPermissions(Activity activity, String... permissions) {
        if (activity == null) {
            throw new IllegalArgumentException("is not a activity or a fragment");
        }
        List<String> deniedList = new ArrayList<>();
        for (String permission : permissions) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                deniedList.add(permission);
            }
        }

        return deniedList;
    }

    private static Activity findActivity(Object o) {
        if (o instanceof Activity) {
            return (Activity) o;
        } else if (o instanceof Fragment) {
            return ((Fragment) o).getActivity();
        }

        return null;
    }
}
