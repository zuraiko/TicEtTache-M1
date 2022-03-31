package com.abgames.tictache.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.abgames.tictache.Firebase.BddManager;
import com.abgames.tictache.R;
import com.abgames.tictache.Tools.OnSwipeTouchListener;
import com.abgames.tictache.Tools.Tools;


/**
 * SignupActivity to sign up our app by firebase implementation
 * It implement also swipe and button to switch to the sign-in activity
 */
public class SignupActivity extends AppCompatActivity {

    /**
     * Manage swipe action
     * Swipe right to go on sign up page
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Tools.setSystemBarColor(this, R.color.green_500);

        LinearLayout rl = findViewById(R.id.linearContent);
        rl.setOnTouchListener(new OnSwipeTouchListener(SignupActivity.this) {
            public void onSwipeTop() {

            }
            public void onSwipeRight() {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
            }
            public void onSwipeLeft() {

            }
            public void onSwipeBottom() {

            }

        });


        Button btn = findViewById(R.id.btnSubmit);
        // Listener to detect when the user want to sign-up
        btn.setOnClickListener(v -> {
            EditText email = findViewById(R.id.etMail);
            EditText password = findViewById(R.id.etPassword);
            EditText username = findViewById(R.id.etUserName);
            BddManager.getInstance().signUp(v, SignupActivity.this, MainActivity.class, email.getText().toString(), password.getText().toString(),username.getText().toString());
        });
    }
}