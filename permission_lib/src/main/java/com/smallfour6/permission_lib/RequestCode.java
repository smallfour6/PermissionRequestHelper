package com.smallfour6.permission_lib;

/**
 * @author zhaoxiaosi
 * @desc
 * @create 2018/10/23 下午5:37
 **/
public class RequestCode {

    //CALENDAR
    public static final byte READ_CALENDAR = 0;
    public static final byte WRITE_CALENDAR = 1;

    //CAMERA
    public static final byte CAMERA = 2;

    //CONTACTS
    public static final byte READ_CONTACTS = 3;
    public static final byte WRITE_CONTACTS = 4;
    public static final byte GET_ACCOUNTS = 5;

    //LOCATION
    public static final byte ACCESS_FINE_LOCATION = 6;
    public static final byte ACCESS_COARSE_LOCATION = 7;

    //MICROPHONE
    public static final byte RECORD_AUDIO = 8;

    //PHONE
    public static final byte READ_PHONE_STATE = 9;
    public static final byte CALL_PHONE = 10;
    public static final byte READ_CALL_LOG = 11;
    public static final byte WRITE_CALL_LOG = 12;
    public static final byte ADD_VOICEMAIL = 13;
    public static final byte USE_SIP = 14;
    public static final byte PROCESS_OUTGOING_CALLS = 15;

    //SENSOR
    public static final byte BODY_SENSORS = 16;


    //SMS
    public static final byte SEND_SMS = 17;
    public static final byte RECEIVE_SMS = 18;
    public static final byte READ_SMS = 19;
    public static final byte RECEIVE_WAP_PUSH = 20;
    public static final byte RECEIVE_MMS = 21;

    //STORAGE
    public static final byte READ_EXTERNAL_STORAGE = 22;
    public static final byte WRITE_EXTERNAL_STORAGE = 23;
}
