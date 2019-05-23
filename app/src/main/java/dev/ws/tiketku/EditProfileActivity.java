package dev.ws.tiketku;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.annotation.SuppressLint;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout btn_back, btn_password;
    Button btn_selfie, btn_save_profile;
    EditText et_nama_lengkap, et_bio, et_username, et_password, et_email_address;
    TextView tv_btn_password;
    ImageView iv_photo_profile;

    DatabaseReference referenceUser, referenceUpdateUserData;
    StorageReference storageReference;

    Uri photo_location;
    Integer photo_max = 1;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    String username_key_new = "";
    String url_photo;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getUsernameLocal();

        et_nama_lengkap = findViewById(R.id.et_nama_lengkap);
        et_bio = findViewById(R.id.et_bio);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_email_address = findViewById(R.id.et_email_address);
        iv_photo_profile = findViewById(R.id.iv_photo_profile);

        btn_password = findViewById(R.id.btn_password);
        btn_password.setOnTouchListener(showpwd);
        btn_password.setVisibility(View.GONE);
        tv_btn_password = findViewById(R.id.tv_btn_password);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_save_profile = findViewById(R.id.btn_save_profile);
        btn_save_profile.setOnClickListener(this);
        btn_selfie = findViewById(R.id.btn_selfie);
        btn_selfie.setOnClickListener(this);

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


        getUserDataFromFirebase();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_save_profile:
                updateUserData();
                break;
            case R.id.btn_selfie:
                findPhoto();
                break;
        }

    }

    private void getUsernameLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }

    private void getUserDataFromFirebase() {
        referenceUser = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(username_key_new);

        referenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                et_nama_lengkap.setText(Objects.requireNonNull(dataSnapshot.child("nama_lengkap").getValue()).toString());
                et_username.setText(Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString());
                et_bio.setText(Objects.requireNonNull(dataSnapshot.child("bio").getValue()).toString());
                et_email_address.setText(Objects.requireNonNull(dataSnapshot.child("email_address").getValue()).toString());
                et_password.setText(Objects.requireNonNull(dataSnapshot.child("password").getValue()).toString());
                url_photo = Objects.requireNonNull(dataSnapshot.child("url_photo_profile").getValue()).toString();
                Picasso.with(EditProfileActivity.this)
                        .load(Objects.requireNonNull(dataSnapshot.child("url_photo_profile").getValue()).toString()).centerCrop().fit().into(iv_photo_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUserData() {
        btn_save_profile.setEnabled(false);
        btn_save_profile.setText("Loading ...");


        if (namaIsValid() && bioIsValid() && usernameIsValid() && passwordIsValid() && emailIsValid()) {
            referenceUpdateUserData = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(username_key_new);
            storageReference = FirebaseStorage.getInstance().getReference()
                    .child("Photousers").child(username_key_new);

            if (photo_location != null) {
                final StorageReference fileName =
                        storageReference.child(System.currentTimeMillis() + "." +
                                getFileExtension(photo_location));
                final UploadTask uploadTask = fileName.putFile(photo_location);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Ulangi ganti photo profile", Toast.LENGTH_SHORT);
                    }
                })
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!task.isSuccessful()) {
                                            throw Objects.requireNonNull(task.getException());
                                        }
                                        return fileName.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            referenceUpdateUserData.getRef().child("username").setValue(et_username.getText().toString());
                                            referenceUpdateUserData.getRef().child("password").setValue(et_password.getText().toString());
                                            referenceUpdateUserData.getRef().child("email_address").setValue(et_email_address.getText().toString());
                                            referenceUpdateUserData.getRef().child("nama_lengkap").setValue(et_nama_lengkap.getText().toString());
                                            referenceUpdateUserData.getRef().child("bio").setValue(et_bio.getText().toString());
                                            referenceUpdateUserData.getRef().child("url_photo_profile").setValue(Objects.requireNonNull(task.getResult()).toString());
                                            finish();
                                        } else {
                                            btn_save_profile.setEnabled(true);
                                            btn_save_profile.setText("SAVE PROFILE");
                                            uploadTask.cancel();
                                            Toast.makeText(getApplicationContext(), "Gagal mengambil url photo profile", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            }
                        });
            } else {
                referenceUpdateUserData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("username").setValue(et_username.getText().toString());
                        dataSnapshot.getRef().child("password").setValue(et_password.getText().toString());
                        dataSnapshot.getRef().child("email_address").setValue(et_email_address.getText().toString());
                        dataSnapshot.getRef().child("nama_lengkap").setValue(et_nama_lengkap.getText().toString());
                        dataSnapshot.getRef().child("bio").setValue(et_bio.getText().toString());
                        dataSnapshot.getRef().child("url_photo_profile").setValue(url_photo);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        btn_save_profile.setEnabled(true);
                        btn_save_profile.setText("SAVE PROFILE");
                    }
                });

            }
        }
    }


    private void saveUsernameToLocal() {

        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(username_key, et_username.getText().toString());
        editor.apply();
        editor.commit();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void findPhoto() {
        Intent pic = new Intent();
        pic.setType("image/*");
        pic.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pic, photo_max);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == photo_max && resultCode == RESULT_OK && data != null && data.getData() != null) {
            photo_location = data.getData();
            Picasso.with(this).load(photo_location).centerCrop().fit().into(iv_photo_profile);

        }
    }

    private Boolean usernameIsValid() {
        if (et_username.getText().toString().equals("")) {
            updateActionError("Username tidak boleh kosong");
            return false;
        } else {
            return true;
        }
    }

    private Boolean passwordIsValid() {
        if (et_password.getText().toString().equals("")) {
            updateActionError("Password tidak boleh kosong");
            return false;
        } else {
            return true;
        }
    }

    private Boolean emailIsValid() {
        if (et_email_address.getText().toString().equals("")) {
            updateActionError("Email Tidak Boleh Kosong");
            return false;
        } else {
            return true;
        }
    }

    private Boolean namaIsValid() {
        if (et_nama_lengkap.getText().toString().equals("")) {
            updateActionError("Nama Tidak Boleh Kosong");
            return false;
        } else {
            return true;
        }
    }

    private Boolean bioIsValid() {
        if (et_bio.getText().toString().equals("")) {
            updateActionError("Bio Tidak Boleh Kosong");
            return false;
        } else {
            return true;
        }
    }


    private void updateActionError(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        btn_save_profile.setEnabled(true);
        btn_save_profile.setText("SAVE PROFILE");
    }


    private View.OnTouchListener showpwd = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                tv_btn_password.setText("\uD83D\uDC35");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                tv_btn_password.setText("\uD83D\uDE48");
                et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            return true;
        }
    };

}
