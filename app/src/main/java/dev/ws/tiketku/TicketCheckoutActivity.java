package dev.ws.tiketku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TicketCheckoutActivity extends AppCompatActivity {

    LinearLayout btn_back, iv_alert_balance;
    Button btn_buy_ticket,btn_plus,btn_minus;
    TextView tv_total_ticket, tv_my_balace, tv_total_price;
    Integer valueTotalTicket = 1;
    Integer mybalance = 329;
    Integer totalharga = 0;
    Integer hargatiket = 40;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_checkout);

        btn_back = findViewById(R.id.btn_back);
        btn_plus = findViewById(R.id.btn_plus);
        btn_minus = findViewById(R.id.btn_minus);
        tv_total_ticket = findViewById(R.id.tv_total_ticket);
        btn_back = findViewById(R.id.btn_back);
        btn_buy_ticket = findViewById(R.id.btn_buy_ticket);
        tv_my_balace = findViewById(R.id.tv_my_balance);
        tv_total_price = findViewById(R.id.tv_total_price);
        iv_alert_balance = findViewById(R.id.iv_alert_balance);


        tv_total_ticket.setText(valueTotalTicket.toString());
        totalharga = hargatiket*valueTotalTicket;
        tv_total_price.setText("$ "+ totalharga.toString());
        tv_my_balace.setText("$ "+mybalance.toString());
        iv_alert_balance.setVisibility(View.GONE);

        btn_minus.setAlpha(0);
        btn_minus.setEnabled(false);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valueTotalTicket -=1;
                tv_total_ticket.setText(valueTotalTicket.toString());
                totalharga = hargatiket*valueTotalTicket;
                tv_total_price.setText("$ "+ totalharga.toString());
                if (valueTotalTicket < 2){
                    btn_minus.animate().alpha(0).setDuration(150).start();
                    btn_minus.setEnabled(false);
                }
                if (totalharga < mybalance){
                    btn_buy_ticket.animate().translationY(0)
                            .alpha(1)
                            .setDuration(200)
                            .start();
                    btn_buy_ticket.setEnabled(true);
                    tv_my_balace.setTextColor(getResources().getColor(R.color.bluePrimary));
                    iv_alert_balance.setVisibility(View.GONE);
                }
            }
        });

        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valueTotalTicket +=1;
                tv_total_ticket.setText(valueTotalTicket.toString());
                totalharga = hargatiket*valueTotalTicket;
                tv_total_price.setText("$ "+ totalharga.toString());
                if (valueTotalTicket > 1){
                    btn_minus.animate().alpha(1).setDuration(150).start();
                    btn_minus.setEnabled(true);
                }
                if (totalharga > mybalance){
                    btn_buy_ticket.animate().translationY(250)
                            .alpha(0)
                            .setDuration(200)
                            .start();
                    btn_buy_ticket.setEnabled(false);
                    tv_my_balace.setTextColor(getResources().getColor(R.color.colorAccent));
                    iv_alert_balance.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_buy_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(TicketCheckoutActivity.this,SuccessBuyTicketActivity.class);
                startActivity(intent);
            }
        });



    }
}
