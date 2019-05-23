package dev.ws.tiketku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout btn_back;
    Button btn_selfie,btn_save_profile;
    EditText et_nama_lengkap,et_bio,et_username,et_password,et_email_address;

    DatabaseReference databaseReference;
    StorageReference storageReference;

    String USERNAME_KEY ="usernamekey";
    String username_key = "";
    String username_key_new = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getUsernameLocal();


        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_save_profile = findViewById(R.id.btn_save_profile);
        btn_save_profile.setOnClickListener(this);
        btn_selfie = findViewById(R.id.btn_selfie);
        btn_selfie.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_save_profile:
                break;
            case R.id.btn_selfie:
                break;
        }

    }
    private void getUsernameLocal(){
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY,MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }


}
