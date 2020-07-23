package com.obiangetfils.meetus.sign;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.obiangetfils.meetus.R;

public class SignUpActivity extends AppCompatActivity {

    private ImageView imageBack;
    private TextView textSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);


        imageBack = (ImageView) findViewById(R.id.imageBack);
        textSignIn = (TextView) findViewById(R.id.textSignIn);

        textSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}
