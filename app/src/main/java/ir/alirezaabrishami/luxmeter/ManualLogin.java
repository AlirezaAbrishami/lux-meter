package ir.alirezaabrishami.luxmeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ManualLogin extends AppCompatActivity {

    EditText ssidEt;
    EditText passwdEt;
    Button connectBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_login);
        init();
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void init() {
        ssidEt = findViewById(R.id.manual_login_ssid);
        passwdEt = findViewById(R.id.manual_login_password);
        connectBtn = findViewById(R.id.manual_login_connect);
    }
}
