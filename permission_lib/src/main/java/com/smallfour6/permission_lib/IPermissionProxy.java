package com.smallfour6.permission_lib;

/**
 * @author zhaoxiaosi
 * @desc request 相当于 唯一标识符
 * @create 2018/10/22 下午9:06
 **/
public interface IPermissionProxy<T> {

    void granted(T source, int requestCode);

    void denied(T source, int requestCode);

    void rationale(T source, int requestCode);

    boolean needExecuteRationale(int requestCode);
}
