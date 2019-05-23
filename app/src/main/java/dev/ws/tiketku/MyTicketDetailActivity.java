package dev.ws.tiketku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import dev.ws.tiketku.Adapter.TicketAdapter;
import dev.ws.tiketku.Model.MyTicket;

public class MyTicketDetailActivity extends AppCompatActivity {

    TextView tv_nama_wisata,tv_lokasi,tv_date_wisata,tv_time_wisata,tv_ketentuan, tv_jumlah_tiket;
    String id_tiket_baru = "";

    ArrayList<MyTicket> list;

    String USERNAME_KEY ="usernamekey";
    String username_key = "";
    String username_key_new = "";

    DatabaseReference referenceMyTiketDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ticket_detail);
        Bundle bundle = getIntent().getExtras();
        id_tiket_baru = bundle.getString("id_tiket");
        getUsernameLocal();

        tv_nama_wisata = findViewById(R.id.tv_nama_wisata);
        tv_lokasi = findViewById(R.id.tv_lokasi);
        tv_time_wisata = findViewById(R.id.tv_time_wisata);
        tv_ketentuan = findViewById(R.id.tv_ketentuan);
        tv_date_wisata = findViewById(R.id.tv_date_wisata);
        tv_jumlah_tiket = findViewById(R.id.tv_jumlah_tiket);

        getTicketIdFromFirebase(id_tiket_baru);
    }
    private void getUsernameLocal(){
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY,MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }


    private void getTicketIdFromFirebase(String s){
        list = new ArrayList<MyTicket>();
        referenceMyTiketDetail = FirebaseDatabase.getInstance().getReference()
                .child("MyTikets")
                .child(username_key_new)
                .child(s);
        referenceMyTiketDetail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                        MyTicket p = dataSnapshot1.getValue(MyTicket.class);
                        list.add(p);
                    }

                }else {
                    Toast.makeText(getApplicationContext(),"Maaf data tidak di temukan",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Maaf terjadi kesalahan mohon kembali",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
