package com.app.omiyago.kurir.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.omiyago.kurir.R;

import org.w3c.dom.Text;

public class PickupFormActivity extends AppCompatActivity {

    Button btnTtdPenerima, btnTtdKurir, btnProsesPickup;
    EditText edtPenerima, edtKurir;
    TextView tvNoref, tvAlamat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_form);

        getSupportActionBar().setTitle("Form Pick Up");

        String alamat = getIntent().getStringExtra("alamat");
        String noref = getIntent().getStringExtra("noref");

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

            }
        });

        btnTtdPenerima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnProsesPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtKurir.getText().toString().isEmpty() || edtPenerima.getText().toString().isEmpty()){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PickupFormActivity.this);
                    alertDialogBuilder.setTitle("OmiyagoKurir");
                    alertDialogBuilder
                            .setMessage("Nama dan tanda tangan tidak boleh ada yang kosong")
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
