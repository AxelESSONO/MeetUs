package com.obiangetfils.meetus.sign;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.obiangetfils.meetus.R;

public class SignInActivity extends AppCompatActivity {

    private TextView textSignUp;

    String myToken = "eq9cVNQsQTyvipZ2Uu6dRr:APA91bGyWG3NCVrEXbpFWmgqlrjknhWhn2xvUMYvfEu-K9vSz9uRL9RIGxdTBW4dWBjLjMxrhVX2gHe3BeDVoBBzaQVGAlJ1dt2L22bj9R0-CBFoYGca4Lvcn_fJxh8XI0oS1n-OacAe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        textSignUp = (TextView) findViewById(R.id.textSignUp);
        textSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });

    }
}
