package com.healthfuel.myhealthfuel.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.healthfuel.myhealthfuel.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.healthfuel.myhealthfuel.mainpage.MainActivity;


public class LogInActivity extends AppCompatActivity {
    EditText editTextLogInEmail, editTextLogInPassword;
    ImageButton backButton;
    Button logButton;
    ProgressBar progressBar;
    FirebaseAuth mAuth;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        editTextLogInEmail = findViewById(R.id.loginEmail);
        editTextLogInPassword = findViewById(R.id.loginPassword);
        backButton = findViewById(R.id.ImageButtonLogin);
        logButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar7);
        mAuth = FirebaseAuth.getInstance();


        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE); // afisarea progressbar-ului


                String email = editTextLogInEmail.getText().toString();
                String password = editTextLogInPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LogInActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LogInActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(LogInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                Toast.makeText(LogInActivity.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(LogInActivity.this, "Email and password is not matching", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(LogInActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
            }
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, BeginningActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

    }
}