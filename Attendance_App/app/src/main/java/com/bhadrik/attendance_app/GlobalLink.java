package com.bhadrik.attendance_app;

public interface GlobalLink {


    public static String URL_ADDRESS="http://itechnuts.com/";
    public static String BASE_LINK=URL_ADDRESS+"webroot/adminPanel/API/";


    public static String LOGIN_LINK=BASE_LINK+"login.php";
    public static String FACTORYCLOCKIN_LINK=BASE_LINK+"factory_clockin.php";
    public static String GETCLOCKINDETAILS_LINK=BASE_LINK+"siteattendance_details.php";
    public static String SITECLOCKIN_LINK=BASE_LINK+"site_clockin.php";
    public static String SITECLOCKOUT_LINK=BASE_LINK+"site_clockout.php";

}
