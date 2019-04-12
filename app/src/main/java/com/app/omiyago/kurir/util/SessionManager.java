package com.app.omiyago.kurir.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.app.omiyago.kurir.activity.LoginActivity;

/**
 * Created by Andre Tampubolon (andre.tampubolon@idstar.co.id) on 3/12/2019.
 */
public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    private static final String PREF_NAME = "omiyagokurir_pref";
    int PRIVATE_MODE = 0;

    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_BEARER = "bearer";
    public static final String KEY_XAUTH = "xauth";

    public SessionManager(Context context){
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String bearer, String xauth){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_BEARER, bearer);
        editor.putString(KEY_XAUTH, xauth);
        editor.commit();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    /*
    public HashMap<String, String> getLoginDetail(){
        HashMap<String, String> detail = new HashMap<String, String>();
        detail.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
        detail.put(KEY_LOCATION_ID, pref.getString(KEY_LOCATION_ID, null));
        return detail;
    }
    */

    public void checkLogin(){
        if (!isLoggedIn()){
            Intent iii = new Intent(context, LoginActivity.class);
            iii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            iii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(iii);
        }
    }

    public void logout(){
        editor.clear();
        editor.commit();

        Intent iii = new Intent(context, LoginActivity.class);
        iii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        iii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(iii);
    }

    public String getKey(String keyType){
        return pref.getString(keyType, "");
    }

    /*
    public boolean isDebugModeEnabled(){
        return pref.getBoolean(KEY_DEBUG_MODE, false);
    }

    public void setDebugMode(boolean b){
        editor.putBoolean(KEY_DEBUG_MODE, b);
        editor.commit();
    }
    */
}
