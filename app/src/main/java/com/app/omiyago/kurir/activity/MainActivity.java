package com.app.omiyago.kurir.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.omiyago.kurir.R;
import com.app.omiyago.kurir.adapter.ScannedItemAdapter;
import com.app.omiyago.kurir.model.ScannedItem;
import com.app.omiyago.kurir.ui.CustomDividerItemDecoration;
import com.app.omiyago.kurir.ui.CustomRecyclerView;
import com.app.omiyago.kurir.util.DBHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    CustomRecyclerView recView;
    List<ScannedItem> scannedItemList;
    Button btnScan, btnScan2;
    DBHelper db;
    ScannedItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Daftar Barang");

        recView = findViewById(R.id.todo_list_recycler_view);
        recView.setLayoutManager(new LinearLayoutManager(this));

        View emptyView = findViewById(R.id.todo_list_empty_view);
        recView.setEmptyView(emptyView);
        recView.setItemAnimator(new DefaultItemAnimator());
        recView.addItemDecoration(new CustomDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));

        scannedItemList = new ArrayList<>();
        db = new DBHelper(getApplicationContext());
        scannedItemList.addAll(db.getAllItems());

       // int len = db.getAllItems().size();
        //Toast.makeText(getApplicationContext(),"Banyak data: "+len, Toast.LENGTH_SHORT).show();

        mAdapter = new ScannedItemAdapter(this, scannedItemList, new ScannedItemAdapter.ScannedItemClickListener() {
            @Override
            public void onPickup(View v, int position) {
                Intent iii = new Intent(MainActivity.this, PickupFormActivity.class);

                ScannedItem item = scannedItemList.get(position);

                iii.putExtra("alamat", item.getAlamat());
                iii.putExtra("noref", item.getNoRef());
                iii.putExtra("item_id", item.getId());
                startActivity(iii);
            }

            @Override
            public void onDelete(View v, int position) {

            }
        });

        recView.setAdapter(mAdapter);

        btnScan = (Button) findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
                scanIntegrator.setOrientationLocked(false);
                scanIntegrator.initiateScan();
            }
        });

        btnScan2 = (Button) findViewById(R.id.btn_scan_2);
        btnScan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
                scanIntegrator.setOrientationLocked(false);
                scanIntegrator.initiateScan();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanResult != null){
           String rawResult = scanResult.getContents();
           String nama = "", noRef= "", alamat="";
           int id=0, jumlah=0;
           double total = 0.0;

           String splitted[] = rawResult.split("-");
           id = Integer.parseInt(splitted[0].split(":")[1]);
           nama = splitted[1].split(":")[1];
           noRef = splitted[2].split(":")[1];
           jumlah = Integer.parseInt(splitted[3].split(":")[1]);
           total = Double.parseDouble(splitted[4].split(":")[1]);
           alamat = splitted[5].split(":")[1];

           ScannedItem item = new ScannedItem(id, nama, noRef, jumlah, total, alamat,"","","","");

           if (db.isExist(item)){
               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
               alertDialogBuilder.setTitle("OmiyagoKurir");
               alertDialogBuilder
                       .setMessage("Barang sudah berada dalam daftar pick up")
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
               db.addItem(item);
               scannedItemList.add(item);
               mAdapter.notifyDataSetChanged();
           }


          // Toast.makeText(getApplicationContext(),"Result: "+db.isExist(item), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
