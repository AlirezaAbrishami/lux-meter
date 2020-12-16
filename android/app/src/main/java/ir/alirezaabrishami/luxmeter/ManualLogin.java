package ir.alirezaabrishami.luxmeter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ManualLogin extends AppCompatActivity {

    EditText ssidEt;
    EditText passwdEt;
    Button connectBtn;
    ImageView nightSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_login);
        init();
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ssidEt.getText() != null && !ssidEt.getText().toString().equals("") && passwdEt.getText() != null && !passwdEt.getText().toString().equals("")) {
                    connectToAccessPoint(ssidEt.getText().toString(), passwdEt.getText().toString());
                } else {
                    Toast.makeText(ManualLogin.this, R.string.fill_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        nightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int defaultNight = AppCompatDelegate.getDefaultNightMode();
                if (defaultNight < 2)
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_YES);
                else {
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_NO);
                }

            }
        });
    }

    private void connectToAccessPoint(String ssid, String password) {
        try {
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = String.format("\"%s\"", ssid);
            wifiConfig.preSharedKey = String.format("\"%s\"", password);

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            //remember id
            assert wifiManager != null;
            int netId = wifiManager.addNetwork(wifiConfig);
            wifiManager.setWifiEnabled(true);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
            String a = wifiManager.getConnectionInfo().toString();
            Log.e("WiFi", a);
            Intent intent = new Intent(ManualLogin.this, MainActivity.class);
            startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void init() {
        ssidEt = findViewById(R.id.manual_login_ssid);
        passwdEt = findViewById(R.id.manual_login_password);
        connectBtn = findViewById(R.id.manual_login_connect);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        nightSwitch = findViewById(R.id.tool_bar_button);
    }
}
