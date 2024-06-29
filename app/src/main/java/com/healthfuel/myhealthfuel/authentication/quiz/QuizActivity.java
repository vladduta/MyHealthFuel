package com.healthfuel.myhealthfuel.authentication.quiz;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.healthfuel.myhealthfuel.R;
import com.healthfuel.myhealthfuel.mainpage.MainActivity;

public class QuizActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private ProgressBar progressBar2, progressBar3, progressBar4;
    private ImageButton imageButtonRegister;
    private Button quizButton;

    private int incr = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        progressBar2 = findViewById(R.id.progressBar2);
        progressBar3 = findViewById(R.id.progressBar3);
        progressBar4 = findViewById(R.id.progressBar4);
        quizButton = findViewById(R.id.quizButton);
        imageButtonRegister = findViewById(R.id.ImageButtonRegister);


        quizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incr++;
                switch (incr) {
                    case 2:
                        progressBar2.incrementProgressBy(1);
                        goToFragment(new Quiz2Fragment());
                        break;
                    case 3:
                        progressBar3.incrementProgressBy(1);
                        goToFragment(new Quiz3Fragment());
                        break;
                    case 4:
                        progressBar4.incrementProgressBy(1);
                        goToFragment(new Quiz4Fragment());
                        break;
                    case 5:
                        Intent intent = new Intent(QuizActivity.this, MainActivity.class);
                        startActivity(intent);
                        incr--;
                }
            }
        });

        imageButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (incr) {
                    case 1:
                        Intent intent = new Intent(QuizActivity.this, BeginQuizActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        break;
                    case 2:
                        progressBar2.incrementProgressBy(-1);
                        goBackToFragment(new Quiz1Fragment());
                        break;
                    case 3:
                        progressBar3.incrementProgressBy(-1);
                        goBackToFragment(new Quiz2Fragment());
                        break;
                    case 4:
                        progressBar4.incrementProgressBy(-1);
                        goBackToFragment(new Quiz3Fragment());
                        break;
                }
                incr--;
            }
        });
    }

    private void goToFragment(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainerView, fragment).commit();
    }
    private void goBackToFragment(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.fragmentContainerView, fragment).commit();
    }
}
