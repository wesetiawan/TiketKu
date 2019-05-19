package dev.ws.tiketku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    TextView tvNewAccount;
    EditText et_username, et_password;
    Button btn_signIn;
    Animation btt;
    Drawable et_error_action, et_normal_action;

    DatabaseReference reference;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        et_password = findViewById(R.id.et_password);
        et_username = findViewById(R.id.et_username);
        btn_signIn = findViewById(R.id.btn_signIn);
        tvNewAccount = findViewById(R.id.tv_new_account);
        btt = AnimationUtils.loadAnimation(this,R.anim.btt);
        et_error_action = getDrawable(R.drawable.bg_input_error);
        et_normal_action = getDrawable(R.drawable.bg_input_edit);


        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_password.setBackground(et_normal_action);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String username = et_username.getText().toString();
                final String password = et_password.getText().toString();

                if (username.equals("")){
                    Toast.makeText(getApplicationContext(),"Harap isi username terlebih dahulu",Toast.LENGTH_SHORT).show();
                }else {
                    reference = FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child(username);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                btn_signIn.setEnabled(false);
                                btn_signIn.setText("Loading ...");
                                //ambil data password dari firebase
                                String pwd = dataSnapshot.child("password").getValue().toString();
                                if (password.equals(pwd)){
                                    SharedPreferences sharedPreferences =getSharedPreferences(USERNAME_KEY,MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(username_key,et_username.getText().toString());
                                    editor.apply();

                                    Intent intent = new Intent(SignInActivity.this,HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                }else if (password.equals("")){
                                    Toast.makeText(getApplicationContext(),"Harap isi password terlebih dahulu",Toast.LENGTH_SHORT).show();
                                    passwordError();
                                }else {
                                    Toast.makeText(getApplicationContext(),"Password kamu salah",Toast.LENGTH_SHORT).show();
                                    passwordError();
                                }

                            }else {
                                Toast.makeText(getApplicationContext(),"Kamu belum terdaftar",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(),"Database Error", Toast.LENGTH_SHORT).show();
                            btn_signIn.setEnabled(true);
                            btn_signIn.setText("SIGN IN");
                        }
                    });
                }
            }
        });

        tvNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, RegisterOneActivity.class);
                startActivity(intent);
            }
        });
    }
    private void passwordError(){
        et_password.setBackground(et_error_action);
        YoYo.with(Techniques.Shake)
                .duration(500)
                .playOn(et_password);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
