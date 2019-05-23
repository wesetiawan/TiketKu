package dev.ws.tiketku;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class RegisterTwoActivity extends AppCompatActivity {

    LinearLayout btnBack;
    Button btnContinue, btnSelfie;
    EditText et_nama_lengkap, et_bio;
    ImageView iv_profile_pic;
    Animation btt, shake;

    Uri photo_location;
    Integer photo_max = 1;
    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    String username_key_new = "";

    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_two);
        getUsernameLocal();

        btnBack = findViewById(R.id.btn_back);
        btnContinue = findViewById(R.id.btn_continue);
        btnSelfie = findViewById(R.id.btn_selfie);
        et_nama_lengkap = findViewById(R.id.et_nama_lengkap);
        et_bio = findViewById(R.id.et_bio);
        iv_profile_pic = findViewById(R.id.iv_profile_pic);
        btt = AnimationUtils.loadAnimation(this, R.anim.btt);
        shake = AnimationUtils.loadAnimation(this, R.anim.shake);


        btnSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPhoto();

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnContinue.setEnabled(false);
                btnContinue.setText("Loading ...");
                //Save ke firebase
                databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(username_key_new);
                storageReference = FirebaseStorage.getInstance().getReference()
                        .child("Photousers").child(username_key_new);

                //validasi foto apakah file ada
                if (photo_location != null) {
                    final StorageReference fileName =
                            storageReference.child(System.currentTimeMillis() + "." +
                                    getFileExtension(photo_location));
                    final UploadTask uploadTask = fileName.putFile(photo_location);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    btnContinue.setEnabled(true);
                                    btnContinue.setText("CONTINUE");
                                    Toast.makeText(getApplicationContext(), "Maaf sedang terjadi error", Toast.LENGTH_SHORT);
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                            if (!task.isSuccessful()){
                                                throw Objects.requireNonNull(task.getException());
                                            }
                                            return fileName.getDownloadUrl();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()){
                                                Uri downloadUri = task.getResult();
                                                assert downloadUri != null;
                                                String urlPhoto = downloadUri.toString();
                                                databaseReference.getRef().child("url_photo_profile").setValue(urlPhoto);
                                                databaseReference.getRef().child("nama_lengkap").setValue(et_nama_lengkap.getText().toString());
                                                databaseReference.getRef().child("bio").setValue(et_bio.getText().toString());
                                            }else {
                                                uploadTask.cancel();
                                                btnContinue.setEnabled(true);
                                                btnContinue.setText("CONTINUE");
                                                Toast.makeText(getApplicationContext(), "Maaf sedang terjadi error", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    Intent intent = new Intent(RegisterTwoActivity.this, SuccessRegisterActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                }


            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void findPhoto() {
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
            Picasso.with(this).load(photo_location).centerCrop().fit().into(iv_profile_pic);
        }
    }

    public void getUsernameLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }

}
