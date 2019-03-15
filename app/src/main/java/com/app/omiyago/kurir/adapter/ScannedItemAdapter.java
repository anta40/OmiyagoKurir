package com.app.omiyago.kurir.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.omiyago.kurir.R;
import com.app.omiyago.kurir.model.ScannedItem;

import java.util.List;


/**
 * Created by Andre Tampubolon (andre.tampubolon@idstar.co.id) on 3/10/2019.
 */
public class ScannedItemAdapter extends RecyclerView.Adapter<ScannedItemAdapter.ScannedItemHolder> {

    private List<ScannedItem> itemList;
    private Context ctxt;
    private ScannedItemClickListener clickListener;

    public ScannedItemAdapter(Context ctxt, List<ScannedItem> itemList, ScannedItemClickListener listener){
        this.ctxt = ctxt;
        this.itemList = itemList;
        clickListener = listener;
    }

    @NonNull
    @Override
    public ScannedItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_row, parent, false);
        return new ScannedItemAdapter.ScannedItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScannedItemHolder holder, int position) {
        ScannedItem item = itemList.get(position);
        holder.bindItem(item);
    }

    @Override
    public int getItemCount() {
        if (itemList == null) return 0;
        else return itemList.size();
    }

    public interface ScannedItemClickListener {
        void onPickup(View v, int position);
        void onDelete(View v, int position);
    }

    class ScannedItemHolder extends RecyclerView.ViewHolder {

        private ScannedItem item;
        private TextView tvNama, tvAlamat, tvNoRef;
        private Button btnPickup, btnDelete;

        public ScannedItemHolder(@NonNull View itemView) {
            super(itemView);

            tvAlamat = itemView.findViewById(R.id.tv_alamat);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvNoRef = itemView.findViewById(R.id.tv_no_ref);

            btnPickup = itemView.findViewById(R.id.btn_pickup);
            btnDelete = itemView.findViewById(R.id.btn_delete);

            btnPickup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onPickup(view, getAdapterPosition());
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onDelete(view, getAdapterPosition());
                }
            });
        }

        public void bindItem(ScannedItem item){
            this.item = item;

            tvAlamat.setText(item.getAlamat());
            tvNama.setText(item.getNama());
            tvNoRef.setText(item.getNoRef());

            if (item.getStatusPickup().equalsIgnoreCase("OK")){
                btnPickup.setEnabled(false);
                btnDelete.setEnabled(true);
            }
        }
    }

}
