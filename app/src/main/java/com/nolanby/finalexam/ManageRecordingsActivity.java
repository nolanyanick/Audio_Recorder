package com.nolanby.finalexam;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;

public class ManageRecordingsActivity extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;
    private RadioGroup mRecordingsRadioGroup;
    private Button mPlayButton;
    private Button mStopPlayingButton;
    private Button mAddRecordingButton;
    private Button mDeleteRecordingButton;
    private Button mBackButton;
    private TextView mNoFilesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_recordings);

        // Player
        mMediaPlayer = new MediaPlayer();

        // RadioGroup to list out each file/recording
        mRecordingsRadioGroup = findViewById(R.id.recordingsRadioGroup);

        // TextView to display when there are no files/recordings
        mNoFilesTextView = findViewById(R.id.noRecordingsTextView);

        // Buttons
        mPlayButton = findViewById(R.id.playButton);
        mStopPlayingButton = findViewById(R.id.stopPlayingButton);
        mAddRecordingButton = findViewById(R.id.addRecordingButton);
        mDeleteRecordingButton = findViewById(R.id.deleteRecordingButton);
        mBackButton = findViewById(R.id.backButton);

        // Disable the Stop button
        mStopPlayingButton.setEnabled(false);

        // Gets the all the recordings and displays them in a RadioGroup
        getAllRecordings();
    }

    // onClick Listener for the Play button
    // Starts playback of a selected recording
    public void playButtonClick(View view) {

        // Get the appropriate file name base on the selected radio button
        RadioButton radioButton = findViewById(mRecordingsRadioGroup.getCheckedRadioButtonId());

        // If no file selected, ignore click and return
        if (radioButton == null) {
            Toast.makeText(this, "Please select a recording to play", Toast.LENGTH_SHORT).show();
            return;
        }

        // Enables/Disables appropriate buttons
        mPlayButton.setEnabled(false);
        mAddRecordingButton.setEnabled(false);
        mDeleteRecordingButton.setEnabled(false);
        mBackButton.setEnabled(false);
        mStopPlayingButton.setEnabled(true);

        // Create a new player
        mMediaPlayer = new MediaPlayer();

        try {
            // Initialize the new player
            mMediaPlayer.setDataSource(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                    "/Recordings/" + radioButton.getText());
            mMediaPlayer.prepare();

            // Method to properly update the view upon playback completion
            mMediaPlayer.setOnCompletionListener( new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer player) {
                    // Stops the player and releases resources
                    mMediaPlayer.stop();
                    mMediaPlayer.release();

                    // Enable/Disable appropriate buttons
                    mPlayButton.setEnabled(true);
                    mAddRecordingButton.setEnabled(true);
                    mDeleteRecordingButton.setEnabled(true);
                    mBackButton.setEnabled(true);
                    mStopPlayingButton.setEnabled(false);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Log.w("ERROR:", e.toString());
        }

        // Starts the player
        mMediaPlayer.start();

        // Confirmation message
        Toast.makeText(this, "Recording Playing", Toast.LENGTH_SHORT).show();
    }

    // onClick Listener for the Stop button
    // Stops playback of the selected recording
    public void stopPlayingButtonClick(View view) {
        // Stops the player and releases resources
        mMediaPlayer.stop();
        mMediaPlayer.release();

        // Enable/Disable appropriate buttons
        mPlayButton.setEnabled(true);
        mAddRecordingButton.setEnabled(true);
        mDeleteRecordingButton.setEnabled(true);
        mBackButton.setEnabled(true);
        mStopPlayingButton.setEnabled(false);

        // Confirmation message
        Toast.makeText(this, "Recording Stopped", Toast.LENGTH_SHORT).show();
    }

    // onClick Listener for the Add button
    // Redirects the user the RecordActivity
    public void addButtonClick(View view) {
        Intent intent;
        intent = new Intent(this, RecorderActivity.class);
        startActivity(intent);
    }

    // onClick Listener for the Delete button
    // Deletes a selected file/recording
    public void deleteButtonClick(View view) {
        // Gets the name of the selected file/recording from the view
        RadioButton radioButton = findViewById(mRecordingsRadioGroup.getCheckedRadioButtonId());

        // If no file selected, ignore click and return
        if (radioButton == null) {
            // Message to user
            Toast.makeText(this, "Please select a recording to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gets the file/recording to be deleted based on its path + name
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File fileToDelete = new File(root.getAbsolutePath() + "/Recordings/" + radioButton.getText());

        // Deletes the file/recording
        fileToDelete.delete();

        // Confirmation message
        Toast.makeText(this, "Recording Removed", Toast.LENGTH_SHORT).show();

        // Updates view
        getAllRecordings();
    }

    // onClick Listener for the Back button
    // Returns the user to MainActivity/main menu
    public void backButtonClick(View view) {
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Gets all files/recordings from external storage located in /Download/Recordings
    public void getAllRecordings() {

        // Removes all current views from the radio group
        mRecordingsRadioGroup.removeAllViews();

        // Gets the proper directory
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File recordingsDirectory = new File(root.getAbsolutePath() + "/Recordings");

        // Creates the directory if it doesn't exist
        if(!recordingsDirectory.exists()) {
            recordingsDirectory.mkdirs();
        }

        // Gets all the files from the /Download/Recordings directory, puts them into a list
        File listOfFiles[] = recordingsDirectory.listFiles();

        // If 'listOfFiles' returns null, display a TextView to the user
        if (listOfFiles == null) {
            // Show TextView
            mNoFilesTextView.setVisibility(View.VISIBLE);
        }
        else {

            // If no files/recordings are present, display a TextView to the user
            if (listOfFiles.length <= 0) {
                // Show TextView
                mNoFilesTextView.setVisibility(View.VISIBLE);
                return;
            }
            else {

                // Hide TextView
                mNoFilesTextView.setVisibility(View.GONE);
            }

            // If external storage is connected,
            // populate the radio group view with radio buttons depicting each file/recording
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {

                for (int i = 0; i < listOfFiles.length; i++) {

                    RadioButton radioButton = new RadioButton(this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    radioButton.setLayoutParams(lp);

                    radioButton.setText(listOfFiles[i].getName());

                    radioButton.setPadding(0,0,0,0);
                    mRecordingsRadioGroup.addView(radioButton, i);
                }
            }
        }
    }
}