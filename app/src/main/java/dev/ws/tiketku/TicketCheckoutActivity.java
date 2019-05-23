package dev.ws.tiketku;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Random;


public class TicketCheckoutActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout btn_back, iv_alert_balance;
    Button btn_buy_ticket, btn_plus, btn_minus;
    TextView tv_total_ticket, tv_my_balance, tv_total_price, tv_ketentuan, tv_title_ticket, tv_location_ticket;
    Integer valueTotalTicket = 1;
    Integer mybalance = 500;
    Integer updateBalance = 0;
    Integer totalharga = 0;
    Integer hargatiket = 0;
    Integer nomor_traksaksi = new Random().nextInt();

    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    String username_key_new = "";
    String nama_wisata,lokasi,ketentuan,date_wisata,time_wisata;
    String jenis_tiket_baru = "";

    DatabaseReference reference, referenceUser, referenceMyTicket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_checkout);
        Bundle bundle = getIntent().getExtras();
        jenis_tiket_baru = bundle.getString("jenis_tiket");


        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_plus = findViewById(R.id.btn_plus);
        btn_plus.setOnClickListener(this);
        btn_minus = findViewById(R.id.btn_minus);
        btn_minus.setOnClickListener(this);
        btn_buy_ticket = findViewById(R.id.btn_buy_ticket);
        btn_buy_ticket.setOnClickListener(this);

        tv_total_ticket = findViewById(R.id.tv_total_ticket);
        tv_ketentuan = findViewById(R.id.tv_ketentuan);
        tv_title_ticket = findViewById(R.id.tv_title_ticket);
        tv_location_ticket = findViewById(R.id.tv_location_ticket);

        tv_my_balance = findViewById(R.id.tv_my_balance);
        tv_total_price = findViewById(R.id.tv_total_price);
        iv_alert_balance = findViewById(R.id.iv_alert_balance);

        getUsernameLocal();
        getUserBalance();
        getTicketDetailFromFirebase(jenis_tiket_baru);

        iv_alert_balance.setVisibility(View.GONE);

        btn_minus.setAlpha(0);
        btn_minus.setEnabled(false);

    }

    public void getUsernameLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }

    private void getTicketDetailFromFirebase(String s) {
        reference = FirebaseDatabase.getInstance().getReference()
                .child("Wisata")
                .child(s);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tv_title_ticket.setText(Objects.requireNonNull(dataSnapshot.child("nama_wisata").getValue()).toString());
                    tv_location_ticket.setText(Objects.requireNonNull(dataSnapshot.child("lokasi").getValue()).toString());
                    tv_ketentuan.setText(Objects.requireNonNull(dataSnapshot.child("ketentuan").getValue()).toString());
                    hargatiket = Integer.valueOf(Objects.requireNonNull(dataSnapshot.child("harga_tiket").getValue()).toString());
                    hitungTotalHarga();
                    lokasi = (Objects.requireNonNull(dataSnapshot.child("lokasi").getValue()).toString());
                    nama_wisata=(Objects.requireNonNull(dataSnapshot.child("nama_wisata").getValue()).toString());
                    ketentuan = (Objects.requireNonNull(dataSnapshot.child("ketentuan").getValue()).toString());
                    date_wisata = (Objects.requireNonNull(dataSnapshot.child("date_wisata").getValue()).toString());
                    time_wisata = (Objects.requireNonNull(dataSnapshot.child("time_wisata").getValue()).toString());
                } else {
                    Toast.makeText(getApplicationContext(), "MAAF DATA TIDAK ADA", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Terjadi Keasalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserBalance() {
        referenceUser = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(username_key_new);
        referenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mybalance = Integer.valueOf(Objects.requireNonNull(dataSnapshot.child("user_balance").getValue()).toString());
                    tv_my_balance.setText(String.format("$ %s", mybalance.toString()));
                } else {
                    Toast.makeText(getApplicationContext(), "MAAF DATA TIDAK ADA", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Terjadi Keasalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserBalance() {
        updateBalance = mybalance-totalharga;
        referenceUser = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(username_key_new);
        referenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().child("user_balance").setValue(updateBalance.toString());
                    Intent intent = new Intent(TicketCheckoutActivity.this, SuccessBuyTicketActivity.class);
                    startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Terjadi Keasalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveMyTicket() {
        btn_buy_ticket.setText("Loading ...");
        btn_buy_ticket.setEnabled(false);


        //menyimpan ke firebasedatabase
        referenceMyTicket = FirebaseDatabase.getInstance().getReference()
                .child("MyTickets")
                .child(username_key_new)
                .child(nama_wisata + nomor_traksaksi);

        referenceMyTicket.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().child("id_tiket").setValue(tv_title_ticket.getText().toString() + nomor_traksaksi);
                dataSnapshot.getRef().child("nama_wisata").setValue(tv_title_ticket.getText().toString());
                dataSnapshot.getRef().child("lokasi").setValue(tv_location_ticket.getText().toString());
                dataSnapshot.getRef().child("ketentuan").setValue(tv_ketentuan.getText().toString());
                dataSnapshot.getRef().child("jumlah_tiket").setValue(valueTotalTicket.toString());
                dataSnapshot.getRef().child("date_wisata").setValue(date_wisata);
                dataSnapshot.getRef().child("time_wisata").setValue(time_wisata);
                updateUserBalance();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                btn_buy_ticket.setText("Loading ...");
                btn_buy_ticket.setEnabled(false);
            }
        });
    }


    private void operasiPengurangan() {
        valueTotalTicket -= 1;
        tv_total_ticket.setText(valueTotalTicket.toString());
        hitungTotalHarga();
        if (valueTotalTicket < 2) {
            btn_minus.animate().alpha(0).setDuration(150).start();
            btn_minus.setEnabled(false);
        }
        if (totalharga < mybalance) {
            btn_buy_ticket.animate().translationY(0)
                    .alpha(1)
                    .setDuration(200)
                    .start();
            btn_buy_ticket.setEnabled(true);
            tv_my_balance.setTextColor(getResources().getColor(R.color.bluePrimary));
            iv_alert_balance.setVisibility(View.GONE);
        }
    }

    private void operasiPenambahan() {
        valueTotalTicket += 1;
        tv_total_ticket.setText(valueTotalTicket.toString());
        hitungTotalHarga();
        if (valueTotalTicket > 1) {
            btn_minus.animate().alpha(1).setDuration(150).start();
            btn_minus.setEnabled(true);
        }
        if (totalharga > mybalance) {
            btn_buy_ticket.animate().translationY(250)
                    .alpha(0)
                    .setDuration(200)
                    .start();
            btn_buy_ticket.setEnabled(false);
            tv_my_balance.setTextColor(getResources().getColor(R.color.colorAccent));
            iv_alert_balance.setVisibility(View.VISIBLE);
        }
    }

    private void hitungTotalHarga() {
        totalharga = hargatiket * valueTotalTicket;
        tv_total_price.setText(String.format("$ %s", totalharga.toString()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_plus:
                operasiPenambahan();
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_minus:
                operasiPengurangan();
                break;
            case R.id.btn_buy_ticket:
                saveMyTicket();
                break;

        }
    }
}
