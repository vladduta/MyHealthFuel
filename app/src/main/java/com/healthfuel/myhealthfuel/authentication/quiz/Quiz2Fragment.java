package com.healthfuel.myhealthfuel.authentication.quiz;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.healthfuel.myhealthfuel.R;

import java.util.HashMap;
import java.util.Map;

public class Quiz2Fragment extends Fragment {
    String gender;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    Map<String, Object> hashMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz2, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        RadioGroup radioGroup = view.findViewById(R.id.radioGroupGender);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioFemale){
                    gender = "Female";
                }
                else if(checkedId == R.id.radioMale){
                    gender = "Male";
                }

                //adaugarea datelor in baza de date
                hashMap.put("gender", gender);
                databaseReference = FirebaseDatabase.getInstance().getReference("Registered users");
                databaseReference.child(user.getUid()).updateChildren(hashMap);
            }
        });

        return view;
    }
}
