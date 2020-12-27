package ir.alirezaabrishami.luxmeter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
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
    ConstraintLayout fifthLayout;
    ConstraintLayout scrollLayout;
    Button connectButton;
    TextView firstText;
    TextView secondText;
    TextView thirdText;
    TextView fourthText;
    TextView fifthText;
    TextView connectionText;
    TextView resetText;
    TextView titleText;
    TextView firstPosition;
    TextView secondPosition;
    TextView thirdPosition;
    TextView fourthPosition;
    TextView fifthPosition;
    Button recordButton;
    TextView averageTextView;

    double[] luxValues = new double[5];
    String averageStr;

    private PrintWriter output;
    private BufferedReader input;
    Thread Thread1 = null;
    final String SERVER_IP = "172.20.10.6";
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
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                positionPointer++;
                switch (positionPointer) {
                    case 2:
                        secondLayout.setVisibility(View.VISIBLE);

                        firstPosition.setBackground(getResources().getDrawable(R.drawable.text_view_background_off));
                        secondPosition.setBackground(getResources().getDrawable(R.drawable.text_view_background));
                        break;
                    case 3:
                        thirdLayout.setVisibility(View.VISIBLE);

                        secondPosition.setBackground(getResources().getDrawable(R.drawable.text_view_background_off));
                        thirdPosition.setBackground(getResources().getDrawable(R.drawable.text_view_background));
                        break;
                    case 4:
                        fourthLayout.setVisibility(View.VISIBLE);

                        thirdPosition.setBackground(getResources().getDrawable(R.drawable.text_view_background_off));
                        fourthPosition.setBackground(getResources().getDrawable(R.drawable.text_view_background));
                        break;
                    case 5:
                        fifthLayout.setVisibility(View.VISIBLE);

                        fourthPosition.setBackground(getResources().getDrawable(R.drawable.text_view_background_off));
                        fifthPosition.setBackground(getResources().getDrawable(R.drawable.text_view_background));
                        break;
                    default:
                        recordButton.setVisibility(View.GONE);
                        averageTextView.setVisibility(View.VISIBLE);
                        averageTextView.setText(averageStr + getAverageLux());

                }
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
        scrollLayout = findViewById(R.id.main_scrollview_layout);
        connectButton = findViewById(R.id.main_connect);
        firstLayout = findViewById(R.id.main_first_layout);
        secondLayout = findViewById(R.id.main_second_layout);
        thirdLayout = findViewById(R.id.main_third_layout);
        fourthLayout = findViewById(R.id.main_fourth_layout);
        fifthLayout = findViewById(R.id.main_fifth_layout);
        connectionText = findViewById(R.id.main_connection_status);
        firstText = findViewById(R.id.main_first_value);
        secondText = findViewById(R.id.main_second_value);
        thirdText = findViewById(R.id.main_third_value);
        fourthText = findViewById(R.id.main_fourth_value);
        fifthText = findViewById(R.id.main_fifth_value);
        firstPosition = findViewById(R.id.main_position_one);
        secondPosition = findViewById(R.id.main_position_two);
        thirdPosition = findViewById(R.id.main_position_three);
        fourthPosition = findViewById(R.id.main_position_four);
        fifthPosition = findViewById(R.id.main_position_five);
        resetText = findViewById(R.id.main_reset);
        titleText = findViewById(R.id.main_title);
        recordButton = findViewById(R.id.main_record);
        averageTextView = findViewById(R.id.main_average);

        averageStr = getResources().getString(R.string.average);
    }

    private int getAverageLux() {
        int average = 0;
        for (int i = 0; i < 5; i++) {
            average += luxValues[i];
        }
        average = average / 5;
        return average;
    }

    protected void processMessage(String message) {
        if (positionPointer == 1) {
            firstPosition.setBackground(getResources().getDrawable(R.drawable.text_view_background));
            secondPosition.setBackground(getResources().getDrawable(R.drawable.text_view_background_off));
            thirdPosition.setBackground(getResources().getDrawable(R.drawable.text_view_background_off));
            fourthPosition.setBackground(getResources().getDrawable(R.drawable.text_view_background_off));
            fifthPosition.setBackground(getResources().getDrawable(R.drawable.text_view_background_off));

            scrollLayout.setVisibility(View.VISIBLE);
            firstLayout.setVisibility(View.VISIBLE);
            secondLayout.setVisibility(View.INVISIBLE);
            thirdLayout.setVisibility(View.INVISIBLE);
            fourthLayout.setVisibility(View.INVISIBLE);
            fifthLayout.setVisibility(View.INVISIBLE);
            connectButton.setVisibility(View.GONE);
            connectionText.setVisibility(View.GONE);
        }
        try {
            if (positionPointer < 6)
                luxValues[positionPointer - 1] = Double.parseDouble(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            case 5:
                fifthText.setText(message);
                break;
        }
    }

    protected void reset() {
        positionPointer = 1;
        averageTextView.setVisibility(View.GONE);
        secondLayout.setVisibility(View.INVISIBLE);
        thirdLayout.setVisibility(View.INVISIBLE);
        fourthLayout.setVisibility(View.INVISIBLE);
        fifthLayout.setVisibility(View.INVISIBLE);
        recordButton.setVisibility(View.VISIBLE);
    }

    class Thread1 implements Runnable {
        public void run() {
            Socket socket;
            try {
                connectionText.setVisibility(View.VISIBLE);
                scrollLayout.setVisibility(View.GONE);
                firstLayout.setVisibility(View.GONE);
                secondLayout.setVisibility(View.GONE);
                thirdLayout.setVisibility(View.GONE);
                fourthLayout.setVisibility(View.GONE);
                fifthLayout.setVisibility(View.GONE);
                averageTextView.setVisibility(View.GONE);
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

    @Override
    protected void onNightModeChanged(int mode) {
        super.onNightModeChanged(mode);
        Log.e("night moed", "changed");
        scrollLayout.setVisibility(View.VISIBLE);
        firstLayout.setVisibility(View.VISIBLE);
        secondLayout.setVisibility(View.INVISIBLE);
        thirdLayout.setVisibility(View.INVISIBLE);
        fourthLayout.setVisibility(View.INVISIBLE);
        fifthLayout.setVisibility(View.INVISIBLE);
        connectButton.setVisibility(View.GONE);
        connectionText.setVisibility(View.GONE);
        processMessage("");
    }
}
