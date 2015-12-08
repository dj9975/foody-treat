package xpertrixitsolution.com.foodytreat.network;


/**
 * @author Dhiraj
 * This class consists of all the web service urls
 */
public class Post_URL {

     public static final String DOMAIN = "http://www.foodytreat.com/index.php/";

    public static final String URL_LOGIN = DOMAIN+"AppController/checkUserAuth";

    public static final String URL_API_LOGIN = DOMAIN+"AppController/checkUserAuthfb";

    public static final String URL_SIGNUP = DOMAIN+"AppController/regUser";

    public static final String URL_GET_AREA_LIST = DOMAIN+"AppController/select_area";

    public static final String URL_GET_VENDORS_LIST =  DOMAIN+"AppController/select_vendors";

    public static final String URL_GET_CAKES_LIST =  DOMAIN+"AppController/sel_products";

    public static final String URL_GET_CAKES_DETAILS =  DOMAIN+"AppController/cake_detail";

    public static final String URL_GET_CAKES_AVAIL_WEIGHT =  DOMAIN+"AppController/cake_weights";

    public static final String URL_GET_PERSONAL_DETAILS =  DOMAIN+"AppController/customer_detail";

    public static final String URL_SEND_ORDER =  DOMAIN+"AppController/order_place";

    // web services add on 12/10/2015 by Vrushali
    public static final String URL_GET_ORDER_HISTORY =  DOMAIN+"AppController/order_history";
    public static final String URL_GET_ADD_ITEM_TO_WISHLIST =  DOMAIN+"AppController/add_wishlist";
    public static final String URL_GET_WISHLIST =  DOMAIN+"AppController/get_wishlist";
    public static final String URL_GET_FORGET_PASSWORD =  DOMAIN+"AppController/forget_password";

    public static final String URL_SEND_PERSONAL_DETAILS =  DOMAIN+"AppController/edit_user";

    public static final String URL_CHANGE_PASSWORD =  DOMAIN+"AppController/change_password";

    public static final String URL_RATE_CAKE =  DOMAIN+"AppController/add_rating";


    public static final String URL_GET_FILTERED_CAKES_LIST =  DOMAIN+"AppController/filter";

    public static final String URL_GET_CAKES_FILTER_FLAVOUR =  DOMAIN+"AppController/flavours";

    public static final String URL_GET_CAKES_FILTER_WEIGHT =  DOMAIN+"AppController/weights";

    public static final String URL_GET_CAKES_FILTER_OCCASSION =  DOMAIN+"AppController/occasions";

    public static final String URL_GET_CAKES_FILTER_CAKETYPE =  DOMAIN+"AppController/cake_types";
}
