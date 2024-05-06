package com.example.pickup.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pickup.R;
import com.example.pickup.callbacks.AuthCallBack;
import com.example.pickup.main.MainActivity;
import com.example.pickup.utils.DatabaseController;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout login_TF_email;
    private TextInputLayout login_TF_password;
    private CircularProgressIndicator login_PB_loading;
    private Button login_BTN_signup;
    private Button login_BTN_login;

    private DatabaseController databaseController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViews();
        initVars();
    }

    private void findViews() {
        login_TF_email = findViewById(R.id.login_TF_email);
        login_TF_password = findViewById(R.id.login_TF_password);
        login_PB_loading = findViewById(R.id.login_PB_loading);
        login_BTN_signup = findViewById(R.id.login_BTN_signup);
        login_BTN_login = findViewById(R.id.login_BTN_login);
    }

    private void initVars() {
        databaseController = new DatabaseController();
        databaseController.setAuthCallBack(new AuthCallBack() {
            @Override
            public void onLoginComplete(Task<AuthResult> task) {
                login_PB_loading.setVisibility(View.INVISIBLE);
                if(task.isSuccessful()){
                    openHomeScreen();
                }else{
                    String err = task.getException().getMessage().toString();
                    Toast.makeText(LoginActivity.this, err, Toast.LENGTH_SHORT);
                }

            }

            @Override
            public void onCreateAuthUserComplete(Task<AuthResult> task) {

            }
        });

        login_BTN_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validInput()){
                    Toast.makeText(LoginActivity.this, "Email and password are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginUser();
            }
        });

        login_BTN_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void openHomeScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void loginUser() {
        login_PB_loading.setVisibility(View.VISIBLE);
        String email = login_TF_email.getEditText().getText().toString();
        String password = login_TF_password.getEditText().getText().toString();
        databaseController.loginUser(email, password);
    }

    private boolean validInput() {
        String email = login_TF_email.getEditText().getText().toString();
        String password = login_TF_password.getEditText().getText().toString();

        if (email.isEmpty())
            return false;
        if(password.isEmpty())
            return false;
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(databaseController.currentUser() != null){
            openHomeScreen();
        }
    }
}