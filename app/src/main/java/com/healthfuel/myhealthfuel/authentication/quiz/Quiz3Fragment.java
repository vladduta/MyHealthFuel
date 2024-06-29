package com.healthfuel.myhealthfuel.authentication.quiz;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.healthfuel.myhealthfuel.R;

import java.util.HashMap;
import java.util.Map;

public class Quiz3Fragment extends Fragment {
    String activityLevel;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    Map<String, Object> hashMap = new HashMap<>();
    int stepsGoal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz3, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        RadioGroup radioGroup = view.findViewById(R.id.radioGroupActivityLevel);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radiobuttonSedentaryActive){
                    activityLevel = "Sedentary";
                    stepsGoal = 2000;
                }
                else if(checkedId == R.id.radiobuttonLowActive){
                    activityLevel = "Low Active";
                    stepsGoal = 5000;

                }
                else if(checkedId == R.id.radiobuttonActive){
                    activityLevel = "Active";
                    stepsGoal = 7000;

                }
                else if(checkedId == R.id.radiobuttonVeryActive){
                    activityLevel = "Very Active";
                    stepsGoal = 9000;

                }

                //adaugarea datelor in baza de date
                hashMap.put("activity level", activityLevel);
                hashMap.put("steps goal", stepsGoal);
                databaseReference = FirebaseDatabase.getInstance().getReference("Registered users");
                databaseReference.child(user.getUid()).updateChildren(hashMap);
            }
        });

        return view;
    }
}
