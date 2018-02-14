package com.hajj.trekkon.jamaah.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hajj.trekkon.jamaah.R;
import com.hajj.trekkon.jamaah.model.BTDeviceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trekkon on 2/13/2018.
 */

public class RV_bt extends RecyclerView.Adapter<RV_bt.RV_btViewHolder> {

    private List<BTDeviceModel> btDeviceModelList;
    private int rowLayout;
    private Context context;

    private static String nama, uuid, status;



    public static class RV_btViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {



        TextView tvNama, tvUUID, tvStatus;

        List<BTDeviceModel> btDeviceModelList = new ArrayList<BTDeviceModel>();
        Context context;


        public RV_btViewHolder(View v, final Context context, List<BTDeviceModel> btDeviceModelList) {
            super(v);
            this.btDeviceModelList = btDeviceModelList;
            this.context = context;
            v.setOnClickListener(this);

            tvUUID=(TextView)v.findViewById(R.id.tvUUID);
            tvNama=(TextView)v.findViewById(R.id.tvNama);
            tvStatus=(TextView)v.findViewById(R.id.tvStatus);

        }

        public void Sel(){

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            BTDeviceModel btDeviceModel = this.btDeviceModelList.get(position);


            nama= btDeviceModel.getEXTRANAME();
            uuid = btDeviceModel.getEXTRAUUID();
            status = String.valueOf(btDeviceModel.getBONDBONDED());

            //Intent intent = new Intent(context, EditPerusahaan.class);
            //intent.putExtra("nama",nama);
            //intent.putExtra("alamat",alamat);
            //intent.putExtra("telepon",telepon);
            //intent.putExtra("id",id);

           // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           // context.startActivity(intent);

        }








    }

    public RV_bt(List<BTDeviceModel> btDeviceModelList, int rowLayout, Context context) {
        this.btDeviceModelList = btDeviceModelList;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public RV_bt.RV_btViewHolder onCreateViewHolder(final ViewGroup parent,
                                                                    int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new RV_btViewHolder(view, context, btDeviceModelList);

    }




    @Override
    public void onBindViewHolder(final RV_btViewHolder holder, final int position) {

        holder.setIsRecyclable(false);


        holder.tvNama.setText(btDeviceModelList.get(position).getEXTRANAME());
        holder.tvUUID.setText(btDeviceModelList.get(position).getEXTRAUUID());
        holder.tvStatus.setText(btDeviceModelList.get(position).getBONDBONDED().toString());

    }

    @Override
    public int getItemCount() {
        return btDeviceModelList.size();
    }





}
