package com.example.muzirisheritage_livestreamingmodule;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Instant;

public class MainActivity extends AppCompatActivity {
    private TextView timerValue;
    private long startTime = 0L;
    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button startButton;
        final Button stopButton;
        final Button resetButton;

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
         myRef = database.getReference("Timeofshowstarted");




             timerValue = (TextView) findViewById (R.id.timerValue);

        resetButton = (Button) findViewById (R.id.resetButton);
        resetButton.setClickable(false);
        resetButton.setTextColor(Color.parseColor("#a6a6a6"));

        startButton = (Button)  findViewById (R.id.startButton);

        stopButton = (Button) findViewById (R.id.stopButton);
        stopButton.setClickable(false);
        stopButton.setTextColor(Color.parseColor("#a6a6a6"));

        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                resetButton.setClickable(false);
                resetButton.setTextColor(Color.parseColor("#8e8e8e"));
                timerValue.setText(String.format("%02d", 00) + ":"
                        + String.format("%02d", 00) + ":"
                        + String.format("%03d", 000));
                startTime = SystemClock.uptimeMillis();
                timeSwapBuff = 0;
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View view) {

                long now = Instant.now().toEpochMilli();
                myRef.setValue(now);

                startButton.setClickable(false);
                startButton.setTextColor(Color.parseColor("#a6a6a6"));
                resetButton.setClickable(false);
                resetButton.setTextColor(Color.parseColor("#a6a6a6"));
                stopButton.setClickable(true);
                stopButton.setTextColor(Color.parseColor("#000000"));
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startButton.setClickable(true);
                startButton.setTextColor(Color.parseColor("#000000"));
                stopButton.setClickable(false);
                stopButton.setTextColor(Color.parseColor("#a6a6a6"));
                resetButton.setClickable(true);
                resetButton.setTextColor(Color.parseColor("#000000"));
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);
            }
        });


    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            int seconds = (int) (updatedTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (updatedTime % 1000);

            String string = "";
            string += "" + String.format("%02d", minutes);
            string += ":" + String.format("%02d", seconds);
            string += ":" + String.format("%03d", milliseconds);

            timerValue.setText(string);
            customHandler.postDelayed(this, 0);
        }
    };
}
