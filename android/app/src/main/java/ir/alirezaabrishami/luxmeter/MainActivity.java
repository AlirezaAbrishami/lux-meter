package ir.alirezaabrishami.luxmeter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

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

public class MainActivity extends AppCompatActivity {

    ImageView nightSwitch;
    ConstraintLayout firstLayout;
    ConstraintLayout secondLayout;
    ConstraintLayout thirdLayout;
    ConstraintLayout fourthLayout;
    Button connectButton;
    TextView firstText;
    TextView secondText;
    TextView thirdText;
    TextView fourthText;
    TextView connectionText;
    TextView resetText;
    TextView titleText;

    private PrintWriter output;
    private BufferedReader input;
    Thread Thread1 = null;
    final String SERVER_IP = "192.168.4.1";
    final int SERVER_PORT = 80;

    byte positionPointer = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectionText.setText(R.string.connecting);
                Thread1 = new Thread(new Thread1());
                Thread1.start();
            }
        });

        resetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });

    }

    private void init() {
        //custom toolbar
        nightSwitch = findViewById(R.id.tool_bar_button);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        connectButton = findViewById(R.id.main_connect);
        firstLayout = findViewById(R.id.main_first_layout);
        secondLayout = findViewById(R.id.main_second_layout);
        thirdLayout = findViewById(R.id.main_third_layout);
        fourthLayout = findViewById(R.id.main_fourth_layout);
        connectionText = findViewById(R.id.main_connection_status);
        firstText = findViewById(R.id.main_first_value);
        secondText = findViewById(R.id.main_second_value);
        thirdText = findViewById(R.id.main_third_value);
        fourthText = findViewById(R.id.main_fourth_value);
        resetText = findViewById(R.id.main_reset);
        titleText = findViewById(R.id.main_title);
    }

    protected void processMessage(String message) {
        if (positionPointer == 1) {
            firstLayout.setVisibility(View.VISIBLE);
            titleText.setVisibility(View.VISIBLE);
            resetText.setVisibility(View.VISIBLE);
            connectButton.setVisibility(View.GONE);
            connectionText.setVisibility(View.GONE);
        }
        if (message.equals("$")) {
            positionPointer++;
            switch (positionPointer) {
                case 2:
                    secondLayout.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    thirdLayout.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    fourthLayout.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            switch (positionPointer) {
                case 1:
                    firstText.setText(message);
                    break;
                case 2:
                    secondText.setText(message);
                    break;
                case 3:
                    thirdText.setText(message);
                    break;
                case 4:
                    fourthText.setText(message);
                    break;
            }
        }
    }

    protected void reset() {
        positionPointer = 1;
        secondLayout.setVisibility(View.GONE);
        thirdLayout.setVisibility(View.GONE);
        fourthLayout.setVisibility(View.GONE);
    }

    class Thread1 implements Runnable {
        public void run() {
            Socket socket;
            try {
                connectionText.setVisibility(View.VISIBLE);
                firstLayout.setVisibility(View.GONE);
                secondLayout.setVisibility(View.GONE);
                thirdLayout.setVisibility(View.GONE);
                fourthLayout.setVisibility(View.GONE);
                resetText.setVisibility(View.GONE);
                titleText.setVisibility(View.GONE);
                socket = new Socket(SERVER_IP, SERVER_PORT);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectionText.setText("Connected\n");
                    }
                });
                new Thread(new Thread3()).start();
                new Thread(new Thread2()).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Thread2 implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Log.e("Thread2: ", "running");
                    while (true) {
                        Thread.sleep(10);
                        final String message = input.readLine();
                        if (message != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processMessage(message);
                                }
                            });
                        } else {
                            Thread1 = new Thread(new Thread1());
                            Thread1.start();
                            return;
                        }
                    }
                } catch (IOException | InterruptedException e) {
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
            Log.e("Thread3: ", "running");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectionText.append("client: " + "hi" + "\n");
                }
            });
        }
    }
}
