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
    private LoginTask loginTask;
    private ProgressDialog pdialog;
    private SessionManager session;

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
                loginTask = new LoginTask(etNama.getText().toString(), etPassword.getText().toString());
                loginTask.execute();
            }
        });
    }

    class LoginTask extends AsyncTask<String, Void, String> {

        private String responseServer;
        private String email, password;

        public LoginTask(String email, String password){
            this.email = email;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog.show();
            responseServer = "";
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient httpClient = new OkHttpClient();

            RequestBody reqBody = new FormBody.Builder()
                    .add("email",email)
                    .add("password",password)
                    .build();

            Request httpRequest = new Request.Builder()
                    .url(URLConfig.API_LOGIN)
                    .addHeader("Accept","application/json")
                    .addHeader("Content-Type","application/json")
                    .addHeader("Authorization", PrefUtil.AUTH_BEARER)
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

            if (isOK(s)) {
                try {
                    JSONObject jsObj = new JSONObject(s);
                    String token = jsObj.get("token").toString();

                    session.createLoginSession(token);
                    Intent iii = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(iii);
                    finish();
                }
                catch (JSONException je) {
                    je.printStackTrace();
                }
            }
            else {
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

        }
    }

    private static boolean isOK(String input) {
        return input.contains("\"success\":\"success\"");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (loginTask != null){
            loginTask.cancel(true);
        }
    }
}
