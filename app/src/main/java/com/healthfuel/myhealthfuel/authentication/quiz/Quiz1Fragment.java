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
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;


public class Quiz1Fragment extends Fragment {

    String goal;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    Map<String, Object> hashMap = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        View view = inflater.inflate(R.layout.fragment_quiz1, container, false);

            RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    if (checkedId == R.id.lose) {
                        goal="Lose Weight";
                    } else if (checkedId == R.id.gain) {
                        goal="Gain Weight";
                    } else if (checkedId == R.id.maintain) {
                        goal="Maintain Weight";
                    }

                    //adaugarea datelor in baza de date
                    hashMap.put("goal", goal);
                    databaseReference = FirebaseDatabase.getInstance().getReference("Registered users");
                    databaseReference.child(user.getUid()).updateChildren(hashMap);
                }
            });


        return view;
    }
}
