package dev.ws.tiketku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class RegisterOneActivity extends AppCompatActivity {

    LinearLayout btnBack;
    Button btnContinue;
    EditText et_username, et_password, et_email_address;
    DatabaseReference reference;
    Animation shake;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_one);
        btnBack = findViewById(R.id.btn_back);
        btnContinue = findViewById(R.id.btn_continue);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_email_address = findViewById(R.id.et_email_address);
        shake = AnimationUtils.loadAnimation(this,R.anim.shake);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameIsValid() && emailIsValid() && passwordIsValid()){
                    savedataToLocal();
                    savedataToDatabase();

                }
            }
        });
    }

    public void savedataToLocal(){
        //menyimpan username ke lokal
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(username_key, et_username.getText().toString());
        editor.apply();
    }

    public void savedataToDatabase(){
        btnContinue.setText("Loading ...");
        btnContinue.setEnabled(false);


        //menyimpan ke firebasedatabase
        reference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(et_username.getText().toString());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Toast.makeText(getApplicationContext(),"Username yang kamu gunakan sudah terdaftar",Toast.LENGTH_SHORT).show();
                    errorAnimation(et_username,"Username yang kamu gunakan sudah terdaftar");
                }else {
                    dataSnapshot.getRef().child("username").setValue(et_username.getText().toString());
                    dataSnapshot.getRef().child("password").setValue(et_password.getText().toString());
                    dataSnapshot.getRef().child("email_address").setValue(et_email_address.getText().toString());
                    dataSnapshot.getRef().child("user_balance").setValue("500");
                    Intent intent = new Intent(RegisterOneActivity.this,RegisterTwoActivity.class);
                    startActivity(intent);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                errorAnimation(btnContinue,"Database Error");
            }
        });
    }



    private Boolean emailIsValid(){
        if (et_email_address.getText().toString().equals("")){
            errorAnimation(et_email_address,"Email tidak boleh kosong");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(et_email_address.getText().toString()).matches()){
            errorAnimation(et_email_address,"Masukan alamat email dengan benar");
            return false;
        }else {
           return true;
        }
    }
    private Boolean usernameIsValid(){
        if (et_username.getText().toString().equals("")){
            errorAnimation(et_username,"Username idak boleh kosong");
            return false;
        }else {
            return true;
        }
    }
    private Boolean passwordIsValid(){
        if (et_password.getText().toString().equals("")){
            errorAnimation(et_password,"Password tidak boleh kosong");
            return false;
        }else {
            return true;
        }
    }


    private void errorAnimation(View v, String s){
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
        v.startAnimation(shake);
        btnContinue.setEnabled(true);
        btnContinue.setText("CONTINUE");
    }

}
