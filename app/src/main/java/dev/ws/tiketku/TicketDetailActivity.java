package dev.ws.tiketku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class TicketDetailActivity extends AppCompatActivity {

    Button btn_buy_ticket;
    LinearLayout btn_back;
    TextView tv_title_ticket, tv_location_ticket,tv_photo_spot_ticket,tv_wifi_ticket,tv_festival_ticket,tv_short_desc;
    ImageView iv_bg_header;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        //getData from intent
        Bundle bundle = getIntent().getExtras();
        String jenis_tiket_baru = bundle.getString("jenis_tiket");

        btn_buy_ticket = findViewById(R.id.btn_buy_ticket);
        btn_back = findViewById(R.id.btn_back);
        iv_bg_header = findViewById(R.id.iv_bg_header);
        tv_title_ticket = findViewById(R.id.tv_title_ticket);
        tv_location_ticket = findViewById(R.id.tv_location_ticket);
        tv_photo_spot_ticket = findViewById(R.id.tv_photo_spot_ticket);
        tv_wifi_ticket = findViewById(R.id.tv_wifi_ticket);
        tv_festival_ticket = findViewById(R.id.tv_festival_ticket);
        tv_short_desc = findViewById(R.id.tv_short_desc);

        //getData from firebase
        updateView(jenis_tiket_baru);

        btn_buy_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(TicketDetailActivity.this,TicketCheckoutActivity.class);
                startActivity(intent);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void updateView(String jenis_tiket){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Wisata").child(jenis_tiket);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    tv_title_ticket.setText(Objects.requireNonNull(dataSnapshot.child("nama_wisata").getValue()).toString());
                    tv_location_ticket.setText(Objects.requireNonNull(dataSnapshot.child("lokasi").getValue()).toString());
                    tv_photo_spot_ticket.setText(Objects.requireNonNull(dataSnapshot.child("is_photo_spot").getValue()).toString());
                    tv_wifi_ticket.setText(Objects.requireNonNull(dataSnapshot.child("is_wifi").getValue()).toString());
                    tv_festival_ticket.setText(Objects.requireNonNull(dataSnapshot.child("is_festival").getValue()).toString());
                    tv_short_desc.setText(Objects.requireNonNull(dataSnapshot.child("short_desc").getValue()).toString());
                    Picasso.with(TicketDetailActivity.this).load(dataSnapshot.child("url_thumbnail").getValue().toString()).centerCrop().fit()
                            .into(iv_bg_header);

                }else {
                    Toast.makeText(getApplicationContext(),"Maaf data tidak ada",Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Maaf terjadi kesalahan",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
