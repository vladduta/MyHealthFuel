package com.healthfuel.myhealthfuel.authentication;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.healthfuel.myhealthfuel.R;
import com.healthfuel.myhealthfuel.authentication.quiz.BeginQuizActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {
    EditText editTextsignupEmail, editTextsignupPassword;
    ImageButton backButton;
    Button regButton;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    Map<String, Object> hashMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        editTextsignupEmail = findViewById(R.id.signupEmail);
        editTextsignupPassword = findViewById(R.id.signupPassword);
        backButton = findViewById(R.id.ImageButtonRegister);
        regButton = findViewById(R.id.signupButton);
        progressBar = findViewById(R.id.progressBarSignUp);
        mAuth = FirebaseAuth.getInstance();



        regButton.setOnClickListener(new View.OnClickListener() {  //butonul de inregistrare
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE); // afisarea progressbar-ului

                String email = editTextsignupEmail.getText().toString();
                String password = editTextsignupPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE); // ascundere progressbar
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Account created.", Toast.LENGTH_SHORT).show();
                            //introducerea datele de autentificare in baza de date
                            user = mAuth.getCurrentUser();
                            hashMap.put("email", email);
                            hashMap.put("password", password);
                            databaseReference = FirebaseDatabase.getInstance().getReference("Registered users");
                            databaseReference.child(user.getUid()).setValue(hashMap);
                            Intent intent = new Intent(getApplicationContext(), BeginQuizActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();

                        } else {

                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(SignUpActivity.this, "Password is too weak", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(SignUpActivity.this, "Email is invalid", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(SignUpActivity.this, "Email is already used", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(SignUpActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });
            }
        });

        backButton.setOnClickListener(v ->

        {
            Intent intent = new Intent(this, BeginningActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

    }


}
