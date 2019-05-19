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
    Drawable et_error_action, et_normal_action;
    Animation shake;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    String username,email_address,password;
    Boolean username_sts,email_sts,password_sts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_one);
        btnBack = findViewById(R.id.btn_back);
        btnContinue = findViewById(R.id.btn_continue);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_email_address = findViewById(R.id.et_email_address);
        et_error_action = getDrawable(R.drawable.bg_input_error);
        et_normal_action = getDrawable(R.drawable.bg_input_edit);
        shake = AnimationUtils.loadAnimation(this,R.anim.shake);

        username_sts = true;
        email_sts = true;
        password_sts = true;

        et_email_address.addTextChangedListener(textWatcher);
        et_username.addTextChangedListener(textWatcher);
        et_password.addTextChangedListener(textWatcher);



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = et_username.getText().toString();
                email_address = et_email_address.getText().toString();
                password = et_password.getText().toString();

                if (usernameIsValid() && emailIsValid() && passwordIsValid()){

                    savedataToDatabase();

                }
            }
        });
    }

    public void savedataToDatabase(){
        btnContinue.setText("Loading ...");
        btnContinue.setEnabled(false);

        //menyimpan username ke lokal
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(username_key, username);
        editor.apply();

        //menyimpan ke firebasedatabase
        reference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(username);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Toast.makeText(getApplicationContext(),"Username yang kamu gunakan sudah terdaftar",Toast.LENGTH_SHORT).show();
                    username_sts = false;
                    errorAnimation(et_username);
                    btnContinue.setText("CONTINUE");
                    btnContinue.setEnabled(true);
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
                Toast.makeText(getApplicationContext(),"Database Error",Toast.LENGTH_SHORT).show();
                btnContinue.setText("CONTINUE");
                btnContinue.setEnabled(true);
            }
        });
    }

    private Boolean emailIsValid(){
        if (email_address.equals("")){
            Toast.makeText(getApplicationContext(),"Email tidak boleh kosong",Toast.LENGTH_SHORT).show();
            errorAnimation(et_email_address);
            email_sts = false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email_address).matches()){
            Toast.makeText(getApplicationContext(),"Masukan alamat email dengan benar",Toast.LENGTH_SHORT).show();
            errorAnimation(et_email_address);
            email_sts = false;
        }else {
            email_sts = true;
        }
        return email_sts;
    }
    private Boolean usernameIsValid(){
        if (username.equals("")){
            Toast.makeText(getApplicationContext(),"Username idak boleh kosong",Toast.LENGTH_SHORT).show();
            errorAnimation(et_username);
            username_sts = false;
        }else {
            username_sts = true;
        }
        return username_sts;
    }
    private Boolean passwordIsValid(){
        if (password.equals("")){
            Toast.makeText(getApplicationContext(),"Password tidak boleh kosong",Toast.LENGTH_SHORT).show();
            errorAnimation(et_password);
            password_sts = false;
        }else {
            password_sts = true;
        }
        return password_sts;
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            et_username.addTextChangedListener(textWatcher);
            et_email_address.addTextChangedListener(textWatcher);
            et_password.addTextChangedListener(textWatcher);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (et_username.isFocused()){
                et_email_address.removeTextChangedListener(textWatcher);
                et_password.removeTextChangedListener(textWatcher);
                et_username.setBackground(et_normal_action);
            }
            if (et_email_address.isFocused()){
                et_username.removeTextChangedListener(textWatcher);
                et_password.removeTextChangedListener(textWatcher);
                et_email_address.setBackground(et_normal_action);
            }
            if (et_password.isFocused()){
                et_username.removeTextChangedListener(textWatcher);
                et_email_address.removeTextChangedListener(textWatcher);
                et_password.setBackground(et_normal_action);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void errorAnimation(View v){
        v.setBackground(et_error_action);
        v.startAnimation(shake);
    }

}
