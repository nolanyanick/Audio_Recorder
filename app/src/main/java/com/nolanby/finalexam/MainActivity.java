package com.nolanby.finalexam;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mNewRecordButton;
    private Button mManageRecordingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Buttons
        mNewRecordButton = findViewById(R.id.newRecordButton);
        mManageRecordingsButton = findViewById(R.id.manageRecordingsButton);
    }

    // Redirects the user to the appropriate activity based on which button was clicked
    public void menuButtonClick(View view) {
        // Get clicked button from view
        Button selectedButton = findViewById(view.getId());
        Intent intent;

        // Redirect to RecorderActivity
        if(selectedButton.getId() == mNewRecordButton.getId()) {
            intent = new Intent(this, RecorderActivity.class);
            startActivity(intent);
        }
        // Redirect to ManageRecordingsActivity
        else if(selectedButton.getId() == mManageRecordingsButton.getId()) {
            intent = new Intent(this, ManageRecordingsActivity.class);
            startActivity(intent);
        }
    }
}