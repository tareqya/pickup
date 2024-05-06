package com.example.pickup.callbacks;


import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public interface AuthCallBack {
    void onLoginComplete(Task<AuthResult> task);
    void onCreateAuthUserComplete(Task<AuthResult> task);

}
