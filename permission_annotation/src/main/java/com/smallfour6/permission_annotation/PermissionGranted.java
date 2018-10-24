package com.smallfour6.permission_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author zhaoxiaosi
 * @desc
 * @create 2018/10/22 下午3:37
 **/
@Target(ElementType.METHOD)
public @interface PermissionGranted {
    int value();
}
