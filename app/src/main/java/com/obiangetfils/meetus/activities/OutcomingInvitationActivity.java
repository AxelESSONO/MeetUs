package com.obiangetfils.meetus.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.obiangetfils.meetus.R;
import com.obiangetfils.meetus.models.User;

public class OutcomingInvitationActivity extends AppCompatActivity {

    private ImageView imageMeetingType, imageStopInvitation;
    private String meetingType;
    private TextView textFirstChar, textUserName, textEmail;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outcoming_invitation);

        imageMeetingType = (ImageView) findViewById(R.id.imageMeetingType);
        imageStopInvitation = (ImageView) findViewById(R.id.imageStopInvitation);
        textFirstChar = (TextView) findViewById(R.id.textFirstChar);
        textUserName = (TextView) findViewById(R.id.textUserName);
        textEmail = (TextView) findViewById(R.id.textEmail);

        meetingType = getIntent().getStringExtra("type");
        user = (User) getIntent().getSerializableExtra("user");

        if (meetingType != null) {
            if (meetingType.equals("video")){
                imageMeetingType.setImageResource(R.drawable.ic_video);
            }
        }

        if (user != null) {
            textFirstChar.setText(user.firstName.substring(0,2));
            textUserName.setText(String.format("%s %s", user.firstName, user.lastName));
            textEmail.setText(user.email);
        }
        imageStopInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}