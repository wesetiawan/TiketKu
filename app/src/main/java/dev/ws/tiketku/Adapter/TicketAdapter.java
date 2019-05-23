package dev.ws.tiketku.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dev.ws.tiketku.Model.MyTicket;
import dev.ws.tiketku.MyTicketDetailActivity;
import dev.ws.tiketku.R;

/**
 * Created by Wawan on 5/22/2019
 */
public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.MyViewHolder> {

    Context context;
    ArrayList<MyTicket> myTicket;

    public TicketAdapter(Context c,ArrayList<MyTicket> p){
        context = c;
        myTicket = p;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_myticket
        ,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_lokasi.setText(myTicket.get(position).getLokasi());
        holder.tv_nama_wisata.setText(myTicket.get(position).getNama_wisata());
        holder.tv_jumlah_tiket.setText(myTicket.get(position).getJumlah_tiket());

        final String getIdTiket = myTicket.get(position).getId_tiket().toString();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyTicketDetailActivity.class);
                intent.putExtra("id_tiket",getIdTiket);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myTicket.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_nama_wisata,tv_lokasi, tv_jumlah_tiket;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_nama_wisata = itemView.findViewById(R.id.tv_nama_wisata);
            tv_jumlah_tiket = itemView.findViewById(R.id.tv_jumlah_tiket);
            tv_lokasi = itemView.findViewById(R.id.tv_lokasi);
        }
    }
}
