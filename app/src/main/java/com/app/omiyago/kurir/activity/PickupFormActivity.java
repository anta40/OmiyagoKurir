package com.app.omiyago.kurir.activity;

import android.content.DialogInterface;
import android.content.Intent;
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

import org.w3c.dom.Text;

public class PickupFormActivity extends AppCompatActivity {

    Button btnTtdPenerima, btnTtdKurir, btnProsesPickup;
    EditText edtPenerima, edtKurir;
    TextView tvNoref, tvAlamat;
    String alamat, noref;
    int item_id;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_form);

        getSupportActionBar().setTitle("Form Pick Up");

        db = new DBHelper(getApplicationContext());

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
}
