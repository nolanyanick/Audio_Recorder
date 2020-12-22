package com.nolanby.finalexam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class RecorderActivity extends AppCompatActivity {

    private final int REQUEST_WRITE_CODE = 0;
    private MediaRecorder mRecorder;
    private EditText mRecordingNameEditText;
    private String mRandomName;
    private Random mRandom;
    private Button mStartButton;
    private Button mStopButton;
    private Button mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        // Recorder
        mRecorder = new MediaRecorder();

        // User input for name
        mRecordingNameEditText = findViewById(R.id.recordingNameEditText);

        // Name generator variables
        mRandom = new Random();
        mRandomName = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        // Buttons
        mStartButton = findViewById(R.id.startButton);
        mStopButton = findViewById(R.id.stopButton);
        mBackButton = findViewById(R.id.backButton);

        // Disable Stop button
        mStopButton.setEnabled(false);

        // Get permissions
        hasWritePermissions();
        hasReadPermissions();
        hasRecordPermissions();
    }

    // Checks for Write permissions on external storage
    public boolean hasWritePermissions() {
        String writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, writePermission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, writePermission)) {

            }
            else {
                ActivityCompat.requestPermissions(
                        this, new String[] { writePermission }, REQUEST_WRITE_CODE);
            }
            return false;
        }
        return  true;
    }

    // Checks for Record permissions
    public boolean hasRecordPermissions() {
        String recordPermission = Manifest.permission.RECORD_AUDIO;
        if (ContextCompat.checkSelfPermission(this, recordPermission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, recordPermission)) {

            }
            else {
                ActivityCompat.requestPermissions(
                        this, new String[] { recordPermission }, REQUEST_WRITE_CODE);
            }
            return false;
        }
        return  true;
    }

    // Check for Read permissions on external storage
    public boolean hasReadPermissions() {
        String readPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, readPermission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, readPermission)) {

            }
            else {
                ActivityCompat.requestPermissions(
                        this, new String[] { readPermission }, REQUEST_WRITE_CODE);
            }
            return false;
        }
        return  true;
    }

    // onClick Listener for the Start button
    // Starts the recording
    public void startButtonClick(View view) {

        // Checks for proper permissions
        if(hasRecordPermissions() && hasReadPermissions()) {

            // Gets the proper directory
            File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File recordingsDirectory = new File(root.getAbsolutePath() + "/Recordings");

            // Creates the directory if it doesn't exist
            if(!recordingsDirectory.exists()) {
                recordingsDirectory.mkdirs();
            }

            // Initialize recorder
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // using MIC
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // .3gp files
            mRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB); // encoder
            mRecorder.setOutputFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Recordings" +
                    "/" + getRecordingName() + ".3gp"); // path to write to

            // Start recorder
            try {
                mRecorder.prepare();
                mRecorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                Log.w("ERROR:       ", e);
            } catch (IOException e) {
                e.printStackTrace();
                Log.w("ERROR:       ", e);
            }

            // Enable/Disable appropriate buttons
            mStartButton.setEnabled(false);
            mStopButton.setEnabled(true);
            mBackButton.setEnabled(false);

            // Confirmation message
            Toast.makeText(this, "Recording Started", Toast.LENGTH_SHORT).show();
        }
    }

    // onClick Listener for the Stop button
    // Stops the recording
    public void stopButtonClick(View view) {

        // Stops recorder and releases resources
        mRecorder.stop();
        mRecorder.release();

        // Enable/Disable appropriate buttons
        mStopButton.setEnabled(false);
        mStartButton.setEnabled(true);
        mBackButton.setEnabled(true);

        // Confirmation message
        Toast.makeText(this, "Recording Saved as " + mRecordingNameEditText.getText(), Toast.LENGTH_LONG).show();

        // Reset EditText used for names
        mRecordingNameEditText.setText("");
    }

    // onClick Listener for the Back button
    // Redirects the user to MainActivity/main menu
    public void backButtonClick(View view) {
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Gets the name for the recording from the user
    public String getRecordingName() {
        // Gets user input text from the view
        String userInput = mRecordingNameEditText.getText().toString();

        // If user input is blank, return random name, otherwise return the user input
        if (!userInput.equals("")) {
            return userInput;
        }
        else {

            userInput = getRandomName(10);
            mRecordingNameEditText.setText(userInput);
            return userInput;
        }
    }

    // Creates a random name if the user didn't supply one
    public String getRandomName(int nameLength) {
        // StringBuilder to store random name
        StringBuilder stringBuilder = new StringBuilder( nameLength );

        // Loop that generates random name
        // Name will consists of letters A-Z
        // Name will be as long as 'nameLength'
        int i = 0 ;
        while(i < nameLength ) {

            stringBuilder.append(mRandomName.charAt(mRandom.nextInt(mRandomName.length())));
            i++ ;
        }

        return stringBuilder.toString();
    }
}