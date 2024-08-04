package com.aaijee.app.Util;

import com.aaijee.app.BuildConfig;
import com.aaijee.app.Model.AboutUsList;
import com.aaijee.app.Model.AreaList;

import java.util.ArrayList;

public class Constants {

    public static final String APIKEY = "AIzaSyDKREnWf3lzfsUzZp-WoQIecZOY9vsfmSo";
    public static boolean WALLET_FLAG = false;

    public static String INCREMENT = "incr";
    public static String DECREMENT = "desc";
    public static String CHECKED = "checked";
    public static String UNCHECKED = "unchecked";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static String QUANTITY = "1";
    public static final String ADDRESS = "address";
    public static final int CHOOSE_ADDRESS_CODE = 300;
    public static final int MAPACTIVITY = 505;
    public static String CITY = "";
    public static final String EMAIL = "email";
    public static String type = "type";
    public static String order = "order";
    public static String normal = "Normal";
    public static String MOBILE = "mobile";
    public static String VERSION = "";
    public static String CART_COUNT = "";
    public static String TOTAL_PRICE = "";
    public static boolean refresh_flag = true;
    public static String MY_PREFS_NAME = BuildConfig.APPLICATION_ID;

  /*  PackageManager manager = this.getPackageManager();
    PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);*/

    public static String Base_Url = "https://aaijee.com/app/";
    public static String url = Base_Url + "all_api.php?";

    //About us data
    public static AboutUsList aboutUs;

    //Area List
    public static ArrayList<AreaList> areaLists = new ArrayList<>();
    public static ArrayList<String> area_name = new ArrayList<>();

    public static ArrayList<String> variant_list = new ArrayList<>();

    //delete account
    public static String delete_account = url + "delete_user&user_id=";

    //Category Listing
    public static String category = url + "category";

    //Menu Listing Category Wise
    public static String menu_cat_wise = url + "menu_list&cat_id=";

    // Update version n fcm token POST
    public static String update_version_fcm = url + "register_fcm";

    // Login Api GET
    public static String login = url + "login";

    // Signup Api POST
    public static String sign_up = url + "signup";

    public static String login_otp = url + "login_otp";

    public static String signup_detail = url + "signup_detail";

    // View Profile GET
    public static String view_profile = url + "view_profile&user_id=";

    //Edit Profile POST
    public static String edit_profile = url + "update_profile";

    //View Address GET
    public static String view_address = url + "view_address";

    //Delete Address GET
    public static String delete_address = url + "order_address_delete";

    //Add Address GET
    public static String new_address = url + "add_address";

    //Edit Address GET
    public static String edit_address = url + "edit_address";

    //Contact us GET
    public static String contact_us = url + "contact";

    //About us GET
    public static String about_us = url + "app_info";

    //Notification Listing GET
    public static String notification_list = url + "notification_list";

    //Cart Count Api
    public static String cart_count = url + "count_cart";

    //Cart List
    public static String cart_list = url + "view_cart";

    //Delete Items in cart
    public static String delete_cart = url + "delete_cart";

    //Verify Mobile No.
    public static String verify_mobile_no = url + "verify_mobile_no";

    //Forgot Password
    public static String forgot_password = url + "forgot_password";

    //Add to cart
    public static String add_cart = url + "add_cart_demo";

    //Order history
    public static String order_list = url + "order_list";

    //Config api
    public static String config = url + "config";

    //Home List
    public static String home_list = url + "app_home_list";

    //Place Order
    public static String place_order = url + "place_order";

    //Coupon List
    public static String coupon_list = url + "coupon_list";

    //Get Discount
    public static String get_discount = url + "get_discount";

    //View Bill
    public static String view_bill = Base_Url + "order-receipt.php?order_id=";

    //Search
    public static String search_list = url + "search";

    //Menu Detail
    public static String menu_detail = url + "menu_detail";

    //Total Price
    public static String total_price = url + "cart_total_price";

    //Area List
    public static String area_list = url + "area_list";

    //Payment Method
    public static String payment_method = url + "payment_method";

    //Set Primary Addresss
    public static String primary_address = url + "change_primary_address";

    //Payment Update
    public static String payment_update = url + "payment_update";

    //Payment failed
    public static String payment_update_fail = url + "payment_update_fail";

    //customer price
    //public static String customer_price = url + "";

    public static String pdf = url + "pdf";

    public static int order_id = 0;

}