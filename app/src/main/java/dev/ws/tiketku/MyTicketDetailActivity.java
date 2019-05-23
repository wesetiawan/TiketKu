package dev.ws.tiketku;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import dev.ws.tiketku.Model.MyTicket;

public class MyTicketDetailActivity extends AppCompatActivity {

    TextView tv_nama_wisata,tv_lokasi,tv_date_wisata,tv_time_wisata,tv_ketentuan, tv_jumlah_tiket;
    ImageView iv_barcode;

    ArrayList<MyTicket> list;

    String USERNAME_KEY ="usernamekey";
    String username_key = "";
    String username_key_new = "";
    String id_tiket = "";
    String fileName;

    Bitmap generatedBitmap;

    DatabaseReference referenceMyTiketDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ticket_detail);
        Bundle bundle = getIntent().getExtras();
        id_tiket = bundle.getString("id_tiket");
        getUsernameLocal();

        tv_nama_wisata = findViewById(R.id.tv_nama_wisata);
        tv_lokasi = findViewById(R.id.tv_lokasi);
        tv_time_wisata = findViewById(R.id.tv_time_wisata);
        tv_ketentuan = findViewById(R.id.tv_ketentuan);
        tv_date_wisata = findViewById(R.id.tv_date_wisata);
        tv_jumlah_tiket = findViewById(R.id.tv_jumlah_tiket);
        iv_barcode = findViewById(R.id.iv_barcode);


        getTicketIdFromFirebase(id_tiket);
    }
    private void getUsernameLocal(){
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY,MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }


    private void getTicketIdFromFirebase(String id_tiket){
        referenceMyTiketDetail = FirebaseDatabase.getInstance().getReference()
                .child("MyTickets")
                .child(username_key_new)
                .child(id_tiket);
        referenceMyTiketDetail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    tv_nama_wisata.setText(Objects.requireNonNull(dataSnapshot.child("nama_wisata").getValue()).toString());
                    tv_lokasi.setText(Objects.requireNonNull(dataSnapshot.child("lokasi").getValue()).toString());
                    tv_jumlah_tiket.setText(Objects.requireNonNull(dataSnapshot.child("jumlah_tiket").getValue()).toString());
                    tv_date_wisata.setText(Objects.requireNonNull(dataSnapshot.child("date_wisata").getValue()).toString());
                    tv_time_wisata.setText(Objects.requireNonNull(dataSnapshot.child("time_wisata").getValue()).toString());
                    tv_ketentuan.setText(Objects.requireNonNull(dataSnapshot.child("ketentuan").getValue()).toString());
                    qrCodeGenerator(Objects.requireNonNull(dataSnapshot.child("id_tiket").getValue()).toString());
                }else {
                    Toast.makeText(getApplicationContext(),"Maaf data tiket tidak di temukan",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Maaf terjadi kesalahan mohon kembali",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void qrCodeGenerator(String code) {
        fileName = code;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        String finalData = Uri.encode(code);
        BitMatrix bm = null;
        try {
            bm = multiFormatWriter.encode(finalData, BarcodeFormat.CODE_128, 1080, 1);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        int bmWidth = bm.getWidth();
        Bitmap imageBitmap = Bitmap.createBitmap(bmWidth, 640, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < bmWidth; i++) {
            // Paint columns of width 1
            int[] column = new int[640];
            Arrays.fill(column, bm.get(i, 0) ? Color.BLACK : Color.WHITE);
            imageBitmap.setPixels(column, 0, 1, i, 0, 1, 640);
        }



        iv_barcode.setImageBitmap(generatedBitmap);
    }
}
