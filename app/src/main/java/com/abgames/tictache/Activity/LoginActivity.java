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
 * LoginActivity to sign in our app by firebase implementation
 * It implement also swipe and button to switch to the sign-up activity
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Function exexute at the start of the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Tools.setSystemBarColor(this, R.color.green_500);

        /*
            Manage swipe action
            Swipe left to go on sign up page
         */
        LinearLayout rl = findViewById(R.id.login);
        rl.setOnTouchListener(new OnSwipeTouchListener(LoginActivity.this) {
            public void onSwipeTop() {

            }
            public void onSwipeRight() {

            }
            public void onSwipeLeft() {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);

            }
            public void onSwipeBottom() {

            }

        });

        // Button to go on sign-up page
        findViewById(R.id.tvSignUp).setOnClickListener((v)-> {
            startActivity( new Intent(LoginActivity.this, SignupActivity.class));
        });

        // Listener to detect when the user want to login
        Button btn = findViewById(R.id.btnSignIn);
        btn.setOnClickListener(v -> {
            EditText email = findViewById(R.id.etUserName);
            EditText password = findViewById(R.id.etPassword);
            BddManager.getInstance().signIn(v, LoginActivity.this, MainActivity.class, email.getText().toString(), password.getText().toString());
        });
    }
}