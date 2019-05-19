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

public class GetStartedActivity extends AppCompatActivity {

    Button btn_Signin,btn_newAccount;
    Animation ttb, btt;
    ImageView iv_logo;
    TextView tv_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        btn_Signin = findViewById(R.id.btn_signIn);
        btn_newAccount = findViewById(R.id.btn_newAccount);
        iv_logo = findViewById(R.id.iv_logo);
        tv_logo = findViewById(R.id.tv_logo);
        ttb = AnimationUtils.loadAnimation(this,R.anim.ttb);
        btt = AnimationUtils.loadAnimation(this,R.anim.btt);

        iv_logo.setAnimation(ttb);
        tv_logo.setAnimation(ttb);
        btn_newAccount.setAnimation(btt);
        btn_Signin.setAnimation(btt);

        btn_Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetStartedActivity.this,SignInActivity.class);
                startActivity(intent);

            }
        });
        btn_newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetStartedActivity.this, RegisterOneActivity.class);
                startActivity(intent);
            }
        });
    }
}
