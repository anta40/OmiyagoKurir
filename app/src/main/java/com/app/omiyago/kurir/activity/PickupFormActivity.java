package com.app.omiyago.kurir.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.omiyago.kurir.R;
import com.app.omiyago.kurir.util.Constants;
import com.app.omiyago.kurir.util.DBHelper;
import com.app.omiyago.kurir.util.PrefUtil;
import com.app.omiyago.kurir.util.SessionManager;
import com.app.omiyago.kurir.util.URLConfig;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PickupFormActivity extends AppCompatActivity {

    Button btnTtdPenerima, btnTtdKurir, btnProsesPickup;
    EditText edtPenerima, edtKurir;
    TextView tvNoref, tvAlamat;
    String alamat, noref;
    int item_id;
    DBHelper db;
    SessionManager session;
    UpdateItemStatusTask updateItemStatusTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_form);

        getSupportActionBar().setTitle("Form Pick Up");

        db = new DBHelper(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        alamat = getIntent().getStringExtra("alamat");
        noref = getIntent().getStringExtra("noref");
        item_id = getIntent().getIntExtra("item_id", -1);

        btnTtdPenerima = (Button) findViewById(R.id.btn_ttd_penerima);
        btnTtdKurir = (Button) findViewById(R.id.btn_ttd_kurir);
        btnProsesPickup = (Button) findViewById(R.id.btn_proses_pickup);

        edtKurir = (EditText) findViewById(R.id.edt_nama_kurir);
        edtPenerima = (EditText) findViewById(R.id.edt_nama_penerima);

        tvNoref = (TextView) findViewById(R.id.tv_pickup_noref);
        tvAlamat = (TextView) findViewById(R.id.tv_pickup_alamat);

        tvNoref.setText(noref);
        tvAlamat.setText(alamat);

        btnTtdKurir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIntent = new Intent(PickupFormActivity.this, SignatureActivity.class);
                signIntent.putExtra("tipe_ttd", "kurir");
                signIntent.putExtra("item_id", item_id);
                startActivity(signIntent);
            }
        });

        btnTtdPenerima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIntent = new Intent(PickupFormActivity.this, SignatureActivity.class);
                signIntent.putExtra("tipe_ttd", "penerima");
                signIntent.putExtra("item_id", item_id);
                startActivity(signIntent);
            }
        });

        btnProsesPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                boolean val1 = !edtKurir.getText().toString().isEmpty();
                boolean val2 = !edtPenerima.getText().toString().isEmpty();
                boolean val3 = db.isSigned(item_id, "kurir");
                boolean val4 = db.isSigned(item_id, "penerima");

               //Toast.makeText(getApplicationContext(), Boolean.toString(val3), Toast.LENGTH_SHORT).show();


                if (val1 && val2 && val3 && val4){
                    /*
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PickupFormActivity.this);
                    alertDialogBuilder.setTitle("OmiyagoKurir");
                    alertDialogBuilder
                            .setMessage("Barang siap untuk di-pick up")
                            .setCancelable(true)
                            .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog dialog = alertDialogBuilder.create();
                    dialog.show();
                    */

                    updateItemStatusTask = new UpdateItemStatusTask(item_id);
                    updateItemStatusTask.execute();

                }
                else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PickupFormActivity.this);
                    alertDialogBuilder.setTitle("OmiyagoKurir");
                    alertDialogBuilder
                            .setMessage("Semua nama dan tanda tangan harus diisi dahulu")
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

        });
    }

    class UpdateItemStatusTask extends AsyncTask<String, Void, String> {
        private String responseServer;
        private int itemId;

        public UpdateItemStatusTask(int itemId){
            this.itemId = itemId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            responseServer = "";
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient httpClient = new OkHttpClient();

            String updateUrl = "http://api.developlagi.net/order_states/update/" + itemId;

            RequestBody reqBody = new FormBody.Builder()
                    .add("status_for_order_id", "5")
                    .build();

            Request httpRequest = new Request.Builder()
                    .url(updateUrl)
                    .addHeader("X-Consumer-Client", PrefUtil.X_CONSUMER_CLIENT)
                    .addHeader("X-Consumer-Passcode",PrefUtil.X_CONSUMER_PASSCODE)
                    .addHeader("Bearer", session.getKey(SessionManager.KEY_BEARER))
                    .addHeader("X-Auth", session.getKey(SessionManager.KEY_XAUTH))
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
            Toast.makeText(getApplicationContext(), "Response: "+s, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (updateItemStatusTask != null){
            updateItemStatusTask.cancel(true);
        }
    }
}
