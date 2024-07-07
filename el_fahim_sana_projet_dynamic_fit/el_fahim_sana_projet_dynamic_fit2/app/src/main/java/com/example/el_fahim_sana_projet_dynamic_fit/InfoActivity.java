package com.example.el_fahim_sana_projet_dynamic_fit;

import static com.example.el_fahim_sana_projet_dynamic_fit.R.id.radioMale;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class InfoActivity extends AppCompatActivity {

    TextView email;
    EditText password, nom, telephone, majour;
    Button annuler, saveM, homeButton;
    RadioButton homme, femme;
    String s;
    DBHelper DB;
    ImageView image;
    boolean passwordVesibility = false;

    private static final int IMAGE_PICK_CAMERA_CODE = 1000;
    private static final int IMAGE_PICK_GALLERY_CODE = 1001;
    private Uri imageUri;
    private String[] storagePermissions;
    private String[] cameraPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        DB = new DBHelper(this);

        image = findViewById(R.id.image);
        email = findViewById(R.id.email2);
        password = findViewById(R.id.password2);
        nom = findViewById(R.id.nom);
        telephone = findViewById(R.id.telephone);
        saveM = findViewById(R.id.enregistrer);
        homeButton = findViewById(R.id.homeButton);
        homme = findViewById(radioMale);
        femme = findViewById(R.id.radioFemale);

        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionChooseImage();
            }
        });

        String g = DB.getGenre(Variables.email);
        if (TextUtils.isEmpty(g)) {
            homme.setChecked(false);
            femme.setChecked(false);
        } else {
            if (g.equals("homme")) {
                homme.setChecked(true);
                femme.setChecked(false);
            } else if (g.equals("femme")) {
                homme.setChecked(false);
                femme.setChecked(true);
            }
        }

        email.setText(Variables.email);
        password.setText(DB.getPassword(Variables.email));
        nom.setText(DB.getNom(Variables.email));
        telephone.setText(DB.getTelephone(Variables.email));

        byte[] bytesImage = DB.getImage(Variables.id);
        if (bytesImage != null) {
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length);
            image.setImageBitmap(bitmapImage);
        }

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent9 = new Intent(InfoActivity.this, MainActivity.class);
                startActivity(intent9);
                finish();
            }
        });

        saveM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(password.getText().toString())) {
                    Toast.makeText(InfoActivity.this, "Remplir le mot de passe", Toast.LENGTH_SHORT).show();
                    password.setError("Remplir le mot de passe");
                } else {
                    if (((password.getText().toString()).replaceAll("\\s+", "")).length() > 2) {
                        boolean success = DB.updateData(Variables.id, password.getText().toString(), nom.getText().toString(), s, telephone.getText().toString(), ImageToByte(image));
                        if (success) {
                            Toast.makeText(InfoActivity.this, "Modification réussie", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(InfoActivity.this, "Modification échouée", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(InfoActivity.this, "Mot de passe court", Toast.LENGTH_SHORT).show();
                        password.setError("Mot de passe court");
                    }
                }
            }
        });

        RadioGroup rg = findViewById(R.id.radio);
        homme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = "homme";
            }
        });

        femme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = "femme";
            }
        });

        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (!passwordVesibility) {
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.show_password, 0);
                            passwordVesibility = true;
                        } else {
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.hide_password, 0);
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVesibility = false;
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void optionChooseImage() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image From");
        builder.setItems(options, (dialogInterface, i) -> {
            if (i == 0) {
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickFromCamera();
                }
            } else if (i == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    choosePicture();
                }
            }
        });
        builder.create().show();
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image description");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void choosePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, IMAGE_PICK_CAMERA_CODE);
    }

    private byte[] ImageToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return stream.toByteArray();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CAMERA_CODE && resultCode == RESULT_OK) {
            try {
                InputStream stream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                image.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == IMAGE_PICK_GALLERY_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    InputStream stream = getContentResolver().openInputStream(resultUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    image.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Erreur de recadrage : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
