package com.example.pickup.main;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pickup.R;
import com.example.pickup.callbacks.UserCallBack;
import com.example.pickup.models.User;
import com.example.pickup.utils.DatabaseController;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity {
    private CircleImageView profileUpdate_image;
    private FloatingActionButton profileUpdate_FBTN_uploadImage;
    private TextInputLayout profileUpdate_TF_firstName;
    private TextInputLayout profileUpdate_TF_lastName;
    private Button profileUpdate_BTN_update;
    private Uri selectedImageUri;
    private User user;
    private DatabaseController databaseController;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        Intent intent = getIntent();
        this.user = (User) intent.getSerializableExtra("USER");
        findViews();
        initVars();
    }

    private void findViews() {
        profileUpdate_image = findViewById(R.id.profileUpdate_image);
        profileUpdate_FBTN_uploadImage = findViewById(R.id.profileUpdate_FBTN_uploadImage);
        profileUpdate_TF_firstName = findViewById(R.id.profileUpdate_TF_firstName);
        profileUpdate_TF_lastName = findViewById(R.id.profileUpdate_TF_lastName);
        profileUpdate_BTN_update = findViewById(R.id.profileUpdate_BTN_update);
    }
    
    private void initVars() {

        displayUI();

        if(!checkPermissions()){
            requestPermissions();
        }

        databaseController = new DatabaseController();
        databaseController.setUserCallBack(new UserCallBack() {
            @Override
            public void onSaveUserComplete(Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(UpdateProfileActivity.this, "User update success", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    String err = task.getException().getMessage().toString();
                    Toast.makeText(UpdateProfileActivity.this, err, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFetchUserComplete(User user) {

            }
        });

        profileUpdate_BTN_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserData();
            }
        });

        profileUpdate_FBTN_uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageSourceDialog();
            }
        });
    }

    private void updateUserData() {
        this.user.setFirstName(profileUpdate_TF_firstName.getEditText().getText().toString());
        this.user.setLastName(profileUpdate_TF_lastName.getEditText().getText().toString());
        if(selectedImageUri != null){
            String imagePath = "Profile/"+this.user.getId()+"."+getFileExtension(this, selectedImageUri);
            boolean result = databaseController.uploadImage(selectedImageUri, imagePath);
            if(result){
                this.user.setImagePath(imagePath);
            }
        }

        databaseController.saveUser(user);
    }

    private void displayUI() {
        if(this.user.getImageUrl() != null){
            Glide.with(this).load(this.user.getImageUrl()).into(profileUpdate_image);
        }
        profileUpdate_TF_firstName.getEditText().setText(this.user.getFirstName());
        profileUpdate_TF_lastName.getEditText().setText(this.user.getLastName());
    }

    private void showImageSourceDialog() {
        CharSequence[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case 0:
                        openCamera();
                        break;
                    case 1:
                        openGallery();
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraResults.launch(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery_results.launch(intent);
    }

    private final ActivityResultLauncher<Intent> gallery_results = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            try {
                                Intent intent = result.getData();
                                selectedImageUri = intent.getData();
                                final InputStream imageStream = UpdateProfileActivity.this.getContentResolver().openInputStream(selectedImageUri);
                                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                profileUpdate_image.setImageBitmap(selectedImage);
                            }
                            catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(UpdateProfileActivity.this, "Gallery canceled", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });

    private final ActivityResultLauncher<Intent> cameraResults = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            Intent intent = result.getData();
                            Bitmap bitmap = (Bitmap)  intent.getExtras().get("data");
                            profileUpdate_image.setImageBitmap(bitmap);
                            selectedImageUri = getImageUri(UpdateProfileActivity.this, bitmap);
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(UpdateProfileActivity.this, "Camera canceled", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });

    public  boolean checkPermissions() {
        return (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                100
        );
    }

    public static String getFileExtension(Activity activity, Uri uri){
        // .png .jpeg
        ContentResolver contentResolver = activity.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public static Uri getImageUri(Activity activity, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}