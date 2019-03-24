package com.app.omiyago.kurir.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.omiyago.kurir.R;
import com.app.omiyago.kurir.util.PrefUtil;
import com.app.omiyago.kurir.util.SessionManager;
import com.app.omiyago.kurir.util.URLConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etNama, etPassword;
    private Button btnLogin;
    private ProgressDialog pdialog;
    private SessionManager session;
    private GetTokenTask getTokenTask;
    private GetAuthTokenToken getAuthTokenTask;
    private LoginWithXAuth loginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login");

        etNama = findViewById(R.id.etNama);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        session = new SessionManager(getApplicationContext());

        pdialog = new ProgressDialog(LoginActivity.this);
        pdialog.setMessage("Harap tunggu");
        pdialog.setTitle("OmiyagoKurir");

        if (session.isLoggedIn()){
            Intent iii = new Intent(LoginActivity.this, MainActivity.class);
            iii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            iii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(iii);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTokenTask = new GetTokenTask();
                getTokenTask.execute();
            }
        });
    }

    class GetTokenTask extends AsyncTask<String, Void, String>{

        private String responseServer;

        public GetTokenTask(){
            responseServer = "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient httpClient = new OkHttpClient();

            Request httpRequest = new Request.Builder()
                    .url(URLConfig.GET_TOKEN_URL)
                    .build();

            Response httpResponse = null;

            try {
                httpResponse = httpClient.newCall(httpRequest).execute();
            }
            catch (IOException ioe){
                ioe.printStackTrace();
            }

            try {
                if (httpResponse != null){
                    responseServer = httpResponse.body().string();
                }
            }
            catch (IOException ioe){
                ioe.printStackTrace();
            }

            return responseServer;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            getAuthTokenTask = new GetAuthTokenToken(etNama.getText().toString(), etPassword.getText().toString(), s);
            getAuthTokenTask.execute();
        }
    }

    class LoginWithXAuth extends AsyncTask<String, Void, String>{
        private String email, password, bearer, xauth;
        private String responseServer;

        public LoginWithXAuth(String email, String password, String bearer, String xauth){
            this.email = email;
            this.password = password;
            this.bearer = bearer;
            this.xauth = xauth;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            responseServer = "";
        }


        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient httpClient = new OkHttpClient();

            RequestBody reqBody = new FormBody.Builder()
                    .add("data[Employee][email]",email)
                    .add("data[Employee][password]",password)
                    .build();

            Request httpRequest = new Request.Builder()
                    .url(URLConfig.API_LOGIN)
                    .addHeader("X-Consumer-Client",PrefUtil.X_CONSUMER_CLIENT)
                    .addHeader("X-Consumer-Passcode",PrefUtil.X_CONSUMER_PASSCODE)
                    .addHeader("Bearer", bearer)
                    .addHeader("X-Auth", xauth)
                    .post(reqBody)
                    .build();

            Response httpResponse = null;

            try {
                httpResponse = httpClient.newCall(httpRequest).execute();
            }
            catch (IOException ioe){
                ioe.printStackTrace();
            }

            try {
                if (httpResponse != null){
                    responseServer = httpResponse.body().string();
                }
            }
            catch (IOException ioe){
                ioe.printStackTrace();
            }

            return responseServer;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            pdialog.dismiss();
            try {
                JSONObject jsobj = new JSONObject(s);
                String loginPayload = jsobj.getString("payload");


                if (loginPayload.equalsIgnoreCase("null")){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                    alertDialogBuilder.setTitle("OmiyagoKurir");
                    alertDialogBuilder
                            .setMessage("Login gagal. Coba ulangi lagi.")
                            .setCancelable(true)
                            .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog dialog = alertDialogBuilder.create();
                    dialog.show();
                }
                else {
                    session.createLoginSession(loginPayload);
                    Intent iii = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(iii);
                    finish();
                }

            }
            catch (JSONException je){

            }
        }
    }

    class GetAuthTokenToken extends AsyncTask<String, Void, String>{

        private String responseServer;
        private String email, password;
        private String bearer;

        public GetAuthTokenToken(String email, String password, String bearer){
            this.bearer = bearer;
            this.email = email;
            this.password = password;
            responseServer = "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            OkHttpClient httpClient = new OkHttpClient();

            RequestBody reqBody = new FormBody.Builder()
                    .add("data[Employee][email]",email)
                    .add("data[Employee][password]",password)
                    .build();

            Request httpRequest = new Request.Builder()
                    .url(URLConfig.API_LOGIN)
                    .addHeader("X-Consumer-Client",PrefUtil.X_CONSUMER_CLIENT)
                    .addHeader("X-Consumer-Passcode",PrefUtil.X_CONSUMER_PASSCODE)
                    .addHeader("Bearer", bearer)
                    .post(reqBody)
                    .build();

            Response httpResponse = null;

            try {
                httpResponse = httpClient.newCall(httpRequest).execute();
            }
            catch (IOException ioe){
                ioe.printStackTrace();
            }

            try {
                if (httpResponse != null){
                    responseServer = httpResponse.body().string();
                }
            }
            catch (IOException ioe){
                ioe.printStackTrace();
            }

            return responseServer;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pdialog.dismiss();

            String resp = s.replace("Result:","");
            resp = resp.trim();

            try {
                JSONObject jsobj = new JSONObject(resp);
                loginTask = new LoginWithXAuth(email, password, bearer, jsobj.getString("payload"));
                loginTask.execute();
            }
            catch (JSONException je){

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (loginTask != null){
            loginTask.cancel(true);
        }

        if (getTokenTask != null){
            getTokenTask.cancel(true);
        }

        if (getAuthTokenTask != null){
            getAuthTokenTask.cancel(true);
        }
    }
}
