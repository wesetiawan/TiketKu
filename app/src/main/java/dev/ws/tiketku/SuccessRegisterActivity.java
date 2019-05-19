package dev.ws.tiketku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SuccessRegisterActivity extends AppCompatActivity {

    Button btnExplore;
    Animation app_splash,btt,ttb;
    ImageView iv_success;
    TextView tv_satu, tv_dua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_register);

        btnExplore = findViewById(R.id.btn_explore);
        iv_success = findViewById(R.id.iv_success);
        tv_satu = findViewById(R.id.tv_satu);
        tv_dua = findViewById(R.id.tv_dua);
        app_splash = AnimationUtils.loadAnimation(this,R.anim.app_splash);
        btt = AnimationUtils.loadAnimation(this,R.anim.btt);
        ttb = AnimationUtils.loadAnimation(this,R.anim.ttb);

        btnExplore.setAnimation(btt);
        iv_success.setAnimation(app_splash);
        tv_satu.setAnimation(ttb);
        tv_dua.setAnimation(ttb);

        btnExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuccessRegisterActivity.this,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
