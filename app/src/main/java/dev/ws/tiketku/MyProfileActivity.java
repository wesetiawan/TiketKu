package dev.ws.tiketku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import dev.ws.tiketku.Adapter.TicketAdapter;
import dev.ws.tiketku.Model.MyTicket;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {

    View btn_back;
    Button btn_edit_profile,btn_signOut;
    ImageView iv_photo_profile;
    TextView tv_nama_lengkap,tv_bio;

    RecyclerView rv_myticket_container;
    ArrayList<MyTicket> list;
    TicketAdapter ticketAdapter;

    DatabaseReference referenceUser, referenceMyTicket;
    String USERNAME_KEY ="usernamekey";
    String username_key = "";
    String username_key_new = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        getUsernameLocal();


        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_signOut = findViewById(R.id.btn_signOut);
        btn_signOut.setOnClickListener(this);
        btn_edit_profile = findViewById(R.id.btn_edit_profile);
        btn_edit_profile.setOnClickListener(this);
        iv_photo_profile = findViewById(R.id.iv_photo_profile);
        tv_nama_lengkap = findViewById(R.id.tv_nama_lengkap);
        tv_bio = findViewById(R.id.tv_bio);
        rv_myticket_container = findViewById(R.id.rv_myticket_container);

        getUserDataFromFirebase();
        getMyTicket();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_signOut:
                SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(username_key, null);
                editor.apply();

                Intent signOut = new Intent(MyProfileActivity.this,SignInActivity.class);
                signOut.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signOut);
                finish();
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_edit_profile:
                Intent editProfile = new Intent(MyProfileActivity.this,EditProfileActivity.class);
                startActivity(editProfile);
                break;

        }
    }

    private void getUsernameLocal(){
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY,MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }

    private void getUserDataFromFirebase(){
        referenceUser = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(username_key_new);
        referenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv_nama_lengkap.setText(Objects.requireNonNull(dataSnapshot.child("nama_lengkap").getValue()).toString());
                tv_bio.setText(Objects.requireNonNull(dataSnapshot.child("bio").getValue()).toString());
                Picasso.with(MyProfileActivity.this)
                        .load(Objects.requireNonNull(dataSnapshot.child("url_photo_profile")
                                .getValue()).toString()).centerCrop().fit().into(iv_photo_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getMyTicket() {
        rv_myticket_container.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<MyTicket>();

        //mengambil my tiket dari username
        referenceMyTicket = FirebaseDatabase.getInstance().getReference()
                .child("MyTickets")
                .child(username_key_new);
        referenceMyTicket.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    MyTicket p = dataSnapshot1.getValue(MyTicket.class);
                    list.add(p);
                }
                ticketAdapter = new TicketAdapter(MyProfileActivity.this,list);
                rv_myticket_container.setAdapter(ticketAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
