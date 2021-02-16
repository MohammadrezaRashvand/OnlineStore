package com.example.onlinestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

        EditText mFullName, mEmail, mPassword, mPhone;
        Button mRegisterButton;
        TextView mLoginButton;
        FirebaseAuth fAuth;
        ProgressBar progressBar;
        ImageView mimageView;
        FirebaseFirestore fStore;
        String userId;

        public final static String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName = findViewById(R.id.FullName);
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.Password);
        mPhone = findViewById(R.id.Phone);
        mRegisterButton = findViewById(R.id.Signin);
        mLoginButton = findViewById(R.id.Login);
        progressBar = findViewById(R.id.progressBar);
        mimageView = findViewById(R.id.imageView);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext() , MainActivity.class));
            finish();
        }


        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String fullName = mFullName.getText().toString();
                String phone = mPhone.getText().toString();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required... ");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required... ");
                    return;
                }

                if(password.length() < 6) {
                    mPassword.setError("Password must be >= 6 Characters ");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(Register.this, "User Created. ", Toast.LENGTH_SHORT).show();
                            userId = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("Users").document(userId);
                            Map<String,Object> user = new HashMap<>();
                            user.put("fName", fullName);
                            user.put("email",email);
                            user.put("phone", phone);
                            documentReference.set(user).addOnSuccessListener((OnSuccessListener) (aVoid) -> {
                                Log.d(TAG,"onSuccess: user Profile is Created for" + userId);
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext() , MainActivity.class));
                        } else {
                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });


    }
}