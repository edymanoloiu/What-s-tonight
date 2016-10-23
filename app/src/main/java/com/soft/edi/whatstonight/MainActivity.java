package com.soft.edi.whatstonight;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private boolean logout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            System.exit(0);
        } else if (getIntent().getBooleanExtra("LOGOUT", false)) {
            logout = true;
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);
        info = (TextView) findViewById(R.id.info);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "user_friends"));
        if (loginButton.getText().toString().toLowerCase().endsWith("out") && !logout) {
            Intent intent = new Intent(this, EventsActivity.class);
            intent.putExtra("list",getIntent().getSerializableExtra("list"));
            intent.putExtra("userName", Profile.getCurrentProfile().getFirstName() + " " + Profile.getCurrentProfile().getLastName());
            startActivity(intent);
        } else
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Intent intent = new Intent(getBaseContext(), EventsActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onCancel() {
                    info.setText("Login attempt canceled.");
                }

                @Override
                public void onError(FacebookException e) {
                    info.setText("Login attempt failed.");
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
