package com.quick.trainingandroid.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quick.trainingandroid.DetailPekerja;
import com.quick.trainingandroid.MainActivity;
import com.quick.trainingandroid.R;

import java.util.ArrayList;

public class RVApekerja extends RecyclerView.Adapter<RVApekerja.ViewHolder>{
    private Context mContext;
    private ArrayList<String> id, nama, kelamin, alamat, hobi, transportasi;

    public RVApekerja(Context context, ArrayList<String> list_id, ArrayList<String> list_nama, ArrayList<String> list_kelamin, ArrayList<String> list_alamat, ArrayList<String> list_hobi, ArrayList<String> list_trans) {
        this.id = list_id;
        this.nama = list_nama;
        this.kelamin = list_kelamin;
        this.alamat = list_alamat;
        this.hobi = list_hobi;
        this.transportasi = list_trans;
        this.mContext = context;
    }

    @Override
    public RVApekerja.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_pekerja, parent, false);
        return new RVApekerja.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RVApekerja.ViewHolder holder, final int position) {
        holder.tv_nama.setText(""+nama.get(position));
        holder.tv_alamat.setText(""+alamat.get(position));

        holder.ll_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, DetailPekerja.class);

                Bundle c = new Bundle();
                c.putString("id",id.get(position));
                c.putString("nama",nama.get(position));
                c.putString("kelamin",kelamin.get(position));
                c.putString("alamat",alamat.get(position));
                c.putString("hobi",hobi.get(position));
                c.putString("trans",transportasi.get(position));
                i.putExtras(c);

                Activity now = (Activity)mContext;
                now.startActivityForResult(i,777);
            }
        });
    }

    @Override
    public int getItemCount() {
        return id.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_nama, tv_alamat;
        LinearLayout ll_card;


        public ViewHolder(final View itemView) {
            super(itemView);

            tv_nama = (TextView) itemView.findViewById(R.id.tv_nama);
            tv_alamat = (TextView) itemView.findViewById(R.id.tv_alamat);
            ll_card = (LinearLayout) itemView.findViewById(R.id.ll_card);

        }
    }
}
