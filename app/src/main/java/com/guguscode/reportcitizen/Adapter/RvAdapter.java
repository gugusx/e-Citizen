package com.guguscode.reportcitizen.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guguscode.reportcitizen.Dashboard.DetailActivity;
import com.guguscode.reportcitizen.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<DataModel> dataModelArrayList;
    Context ctx;

    public RvAdapter(Context ctx, ArrayList<DataModel> dataModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.dataModelArrayList = dataModelArrayList;
        this.ctx = ctx;
    }

    @Override
    public RvAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.rv_one, parent, false);
        MyViewHolder holder = new MyViewHolder(view,ctx,dataModelArrayList);

        return holder;
    }

    @Override
    public void onBindViewHolder(RvAdapter.MyViewHolder holder, final int position) {

        Picasso.get().load(dataModelArrayList.get(position).getGbrURL()).fit().centerInside().into(holder.iv);
        holder.np.setText(dataModelArrayList.get(position).getNp());
        holder.jk.setText(dataModelArrayList.get(position).getJk());
        holder.ket.setText(dataModelArrayList.get(position).getKet());
        holder.wkt.setText(dataModelArrayList.get(position).getWkt());
        holder.tpt.setText(dataModelArrayList.get(position).getNt());
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView np, jk, tpt, ket, wkt;
        ImageView iv, iv2;
        ArrayList<DataModel> dataModelArrayList;
        Context ctx;

        public MyViewHolder(View itemView, Context ctx, ArrayList<DataModel> dataModelArrayList) {
            super(itemView);

            this.dataModelArrayList = dataModelArrayList;
            this.ctx = ctx;
            itemView.setOnClickListener(this);
            np = itemView.findViewById(R.id.NP);
            jk = itemView.findViewById(R.id.JK);
            ket = itemView.findViewById(R.id.KET);
            iv = itemView.findViewById(R.id.iv);
            wkt = itemView.findViewById(R.id.WKT);
            tpt = itemView.findViewById(R.id.TPT);
        }

       @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            DataModel dataModel = this.dataModelArrayList.get(position);
            Intent intent = new Intent(ctx, DetailActivity.class);
            intent.putExtra("gambar", dataModel.getGbrURL());
            intent.putExtra("kategori", dataModel.getJk());
            intent.putExtra("keterangan", dataModel.getKet());
            intent.putExtra("waktu_lapor", dataModel.getWkt());
            intent.putExtra("nama_tempat", dataModel.getNt());
            this.ctx.startActivity(intent);

        }
    }
}
