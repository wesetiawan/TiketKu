package dev.ws.tiketku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    TextView tvNewAccount, tv_btn_password;
    EditText et_username, et_password;
    Button btn_signIn;
    Animation btt, shake;
    LinearLayout btn_password;
    DatabaseReference reference;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        et_password = findViewById(R.id.et_password);
        et_username = findViewById(R.id.et_username);
        btn_signIn = findViewById(R.id.btn_signIn);
        tvNewAccount = findViewById(R.id.tv_new_account);
        btn_password = findViewById(R.id.btn_password);
        btn_password.setOnTouchListener(showpwd);
        btn_password.setVisibility(View.GONE);
        tv_btn_password = findViewById(R.id.tv_btn_password);
        btt = AnimationUtils.loadAnimation(this, R.anim.btt);
        shake = AnimationUtils.loadAnimation(this, R.anim.shake);

        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_password.getText().toString().equals("")){
                    btn_password.setVisibility(View.GONE);
                }else {
                    btn_password.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_signIn.setEnabled(false);
                btn_signIn.setText("Loading ...");


                if (usernameIsValid() && passwordIsValid()) {
                    final String password = et_password.getText().toString();
                    final String username = et_username.getText().toString();

                    reference = FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child(username);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                //ambil data password dari firebase
                                String pwd = dataSnapshot.child("password").getValue().toString();
                                //validasi password dengan password yg di firebase
                                if (password.equals(pwd)) {
                                    //simpan username ke lokal
                                    saveUsernameToLocal();

                                    Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    loginErrorAction(et_password, "Password kamu salah");
                                }

                            } else {
                                loginErrorAction(et_password, "Kamu belum terdaftar");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Database Error", Toast.LENGTH_SHORT).show();
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

    private View.OnTouchListener showpwd = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                tv_btn_password.setText("\uD83D\uDC35");
                et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                tv_btn_password.setText("\uD83D\uDE48");
                et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            return true;
        }
    };

    private Boolean usernameIsValid() {
        if (et_username.getText().toString().equals("")) {
            loginErrorAction(et_username, "Username tidak boleh kosong");
            return false;
        } else {
            return true;
        }
    }

    private Boolean passwordIsValid() {
        if (et_password.getText().toString().equals("")) {
            loginErrorAction(et_password, "Password tidak boleh kosong");
            return false;
        } else {
            return true;
        }
    }

    private void saveUsernameToLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(username_key, et_username.getText().toString());
        editor.apply();
        editor.commit();
    }

    private void loginErrorAction(View v, String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        v.startAnimation(shake);
        btn_signIn.setEnabled(true);
        btn_signIn.setText("SIGN IN");
    }
}
