package ir.alirezaabrishami.luxmeter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Calibrate extends AppCompatActivity {

    ImageView nightSwitch;
    Button calibrateButton;
    TextView sensorValueText;
    ImageView helpButton;

    Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    Thread Thread1;
    final String SERVER_IP = "192.168.4.1";
    final int SERVER_PORT = 80;
    boolean sendOk;
    boolean stopThread3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);
        init();
        nightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mode = getResources().getString(R.string.mode);
                if (mode.equals("dark"))
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_NO);
                else
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_YES);

            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(Calibrate.this)
                        .setMessage(R.string.help)
                        .setNegativeButton("OK", null)
                        .create()
                        .show();
            }
        });
        Thread1 = new Thread(new Thread1());
        Thread1.start();
        calibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOk = true;
            }
        });
    }

    protected void processMessage(String message) {
        calibrateButton.setVisibility(View.VISIBLE);

        if (message.charAt(0) == 'o') {
            Intent intent = new Intent(Calibrate.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            sensorValueText.setText(message);
        }
    }

    class Thread1 implements Runnable {
        public void run() {
            try {
                Thread.sleep(1000);
                Log.e("CalibrateThread1", "running");
                socket = new Socket(SERVER_IP, SERVER_PORT);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                new Thread(new Calibrate.Thread3()).start();
                new Thread(new Calibrate.Thread2()).start();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Thread2 implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Log.e("CalibrateThread2: ", "running");
                    while (true) {
                        Thread.sleep(10);
                        final String message;
                        try {
                            message = input.readLine();
                        } catch (Exception e) {
                            return;
                        }
                        if (message != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processMessage(message);
                                }
                            });
                        } else {
                            Thread1 = new Thread(new Calibrate.Thread1());
                            Thread1.start();
                            return;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Thread3 implements Runnable {
        @Override
        public void run() {
            output.write("hi");
            output.flush();
            Log.e("CalibrateThread3: ", "running");
            while (true) {
                if (sendOk) {
                    output.write('o');
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    output.write('\n');
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    output.flush();
                    Log.e("CalibrateThread3", "ok");
                    sendOk = false;
                }
                if (stopThread3)
                    return;
            }
        }
    }


    private void init() {
        //custom toolbar
        nightSwitch = findViewById(R.id.tool_bar_button);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        calibrateButton = findViewById(R.id.calibrate_button);
        sensorValueText = findViewById(R.id.calibrate_value);
        helpButton = findViewById(R.id.calibrate_help);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopThread3 = true;
        try {
            if (socket != null) {
                input = null;
                output = null;
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
