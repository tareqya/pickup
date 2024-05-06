package com.example.pickup.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pickup.R;
import com.example.pickup.callbacks.AuthCallBack;
import com.example.pickup.callbacks.UserCallBack;
import com.example.pickup.models.User;
import com.example.pickup.utils.DatabaseController;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;

public class SignupActivity extends AppCompatActivity {
    private TextInputLayout signup_TF_firstName;
    private TextInputLayout signup_TF_lastName;
    private TextInputLayout signup_TF_email;
    private TextInputLayout signup_TF_password;
    private TextInputLayout signup_TF_confirmPassword;
    private Button signup_BTN_createAccount;
    private CircularProgressIndicator signup_PB_loading;

    private DatabaseController databaseController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        findViews();
        initVars();
    }

    private void findViews() {
        signup_TF_firstName = findViewById(R.id.signup_TF_firstName);
        signup_TF_lastName = findViewById(R.id.signup_TF_lastName);
        signup_TF_email = findViewById(R.id.signup_TF_email);
        signup_TF_password = findViewById(R.id.signup_TF_password);
        signup_TF_confirmPassword = findViewById(R.id.signup_TF_confirmPassword);
        signup_BTN_createAccount = findViewById(R.id.signup_BTN_createAccount);
        signup_PB_loading = findViewById(R.id.signup_PB_loading);
    }

    private void initVars() {
        databaseController = new DatabaseController();
        databaseController.setAuthCallBack(new AuthCallBack() {
            @Override
            public void onLoginComplete(Task<AuthResult> task) {

            }

            @Override
            public void onCreateAuthUserComplete(Task<AuthResult> task) {

                if(task.isSuccessful()){
                    String firstName = signup_TF_firstName.getEditText().getText().toString();
                    String lastName = signup_TF_lastName.getEditText().getText().toString();
                    String email = signup_TF_email.getEditText().getText().toString();
                    User user = new User()
                            .setEmail(email)
                            .setFirstName(firstName)
                            .setLastName(lastName)
                            .setScore(0);
                    user.setId(task.getResult().getUser().getUid());

                    databaseController.saveUser(user);
                }else{
                    String err = task.getException().getMessage().toString();
                    Toast.makeText(SignupActivity.this, err, Toast.LENGTH_SHORT).show();
                }
            }
        });

        databaseController.setUserCallBack(new UserCallBack() {
            @Override
            public void onSaveUserComplete(Task<Void> task) {
                signup_PB_loading.setVisibility(View.INVISIBLE);
                databaseController.signOut();

                if(task.isSuccessful()){
                   Toast.makeText(SignupActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    String err = task.getException().getMessage().toString();
                    Toast.makeText(SignupActivity.this, err, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFetchUserComplete(User user) {

            }
        });

        signup_BTN_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validInput()){
                    Toast.makeText(SignupActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                createNewUser();
            }
        });
    }

    private void createNewUser() {
        signup_PB_loading.setVisibility(View.VISIBLE);
        String email = signup_TF_email.getEditText().getText().toString();
        String password = signup_TF_password.getEditText().getText().toString();
        databaseController.createAuthUser(email, password);
    }

    private boolean validInput() {
        String firstName = signup_TF_firstName.getEditText().getText().toString();
        String lastName = signup_TF_lastName.getEditText().getText().toString();
        String email = signup_TF_email.getEditText().getText().toString();
        String password = signup_TF_password.getEditText().getText().toString();
        String confirmPassword = signup_TF_confirmPassword.getEditText().getText().toString();

        if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
            return false;

        if(!password.equals(confirmPassword)) {
            Toast.makeText(SignupActivity.this, "mismatch between the password and confirm password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}