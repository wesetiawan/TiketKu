package dev.ws.tiketku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.florent37.shapeofview.shapes.CircleView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    LinearLayout btn_ticket_pisa;
    ImageView iv_photo_profile;
    TextView tv_nama_lengkap,tv_bio,tv_user_balance;

    DatabaseReference reference;
    String USERNAME_KEY ="usernamekey";
    String username_key = "";
    String username_key_new = "";

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

        reference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(username_key_new);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv_nama_lengkap.setText(dataSnapshot.child("nama_lengkap").getValue().toString());
                tv_bio.setText(dataSnapshot.child("bio").getValue().toString());
                tv_user_balance.setText("$ "+ dataSnapshot.child("user_balance").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
    public void getUsernameLocal(){
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY,MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }
}
