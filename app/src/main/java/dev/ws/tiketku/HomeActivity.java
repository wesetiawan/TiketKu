package dev.ws.tiketku;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    LinearLayout btn_ticket_pisa;
    ImageView iv_photo_profile;
    TextView tv_nama_lengkap,tv_bio,tv_user_balance;

    DatabaseReference reference;
    String USERNAME_KEY ="usernamekey";
    String username_key = "";
    String username_key_new = "";
    String nama_lengkap,bio,user_balance,url_photo_profile,mata_uang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getUsernameLocal();

        btn_ticket_pisa = findViewById(R.id.btn_ticket_pisa);
        iv_photo_profile = findViewById(R.id.iv_photo_profile);
        tv_bio = findViewById(R.id.tv_bio);
        tv_nama_lengkap = findViewById(R.id.tv_nama_lengkap);
        tv_user_balance = findViewById(R.id.tv_user_balance);
        mata_uang = "$ ";


        getDataFromFirebase();


        btn_ticket_pisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,TicketDetailActivity.class);
                startActivity(intent);
            }
        });
        iv_photo_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,MyProfileActivity.class);
                startActivity(intent);
            }
        });

    }
    private void getUsernameLocal(){
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY,MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }

    private void getDataFromFirebase(){
        reference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(username_key_new);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nama_lengkap = Objects.requireNonNull(dataSnapshot.child("nama_lengkap").getValue()).toString();
                bio = Objects.requireNonNull(dataSnapshot.child("bio").getValue()).toString();
                user_balance = Objects.requireNonNull(dataSnapshot.child("user_balance").getValue()).toString();
                url_photo_profile = Objects.requireNonNull(dataSnapshot.child("url_photo_profile").getValue()).toString();
                updateViewFromFireBase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateViewFromFireBase(){
        tv_nama_lengkap.setText(nama_lengkap);
        tv_bio.setText(bio);
        tv_user_balance.setText(mata_uang+user_balance);
        Picasso.with(HomeActivity.this).load(url_photo_profile).centerCrop().fit().into(iv_photo_profile);
    }
}
