package com.example.pickup.callbacks;

import com.example.pickup.models.User;
import com.google.android.gms.tasks.Task;

public interface UserCallBack {
    void onSaveUserComplete(Task<Void> task);
    void onFetchUserComplete(User user);

}
