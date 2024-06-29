package com.healthfuel.myhealthfuel.authentication.quiz;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.healthfuel.myhealthfuel.R;
import com.healthfuel.myhealthfuel.mainpage.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class Quiz4Fragment extends Fragment {

    EditText editTextAge, editTextWeight, editTextHeight ;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    Map<String, Object> hashMap = new HashMap<>();
    Button quizButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz4, container, false);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        editTextAge = view.findViewById(R.id.editTextAge);
        editTextHeight = view.findViewById(R.id.editTextHeight);
        editTextWeight = view.findViewById(R.id.editTextWeight);
        quizButton = getActivity().findViewById(R.id.quizButton);




        quizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String age = editTextAge.getText().toString();
                String height = editTextHeight.getText().toString();
                String weight = editTextWeight.getText().toString();

                hashMap.put("age", Integer.parseInt(age));
                hashMap.put("height", Integer.parseInt(height));
                hashMap.put("weight", Integer.parseInt(weight));
                databaseReference = FirebaseDatabase.getInstance().getReference("Registered users");
                databaseReference.child(user.getUid()).updateChildren(hashMap);

                if (TextUtils.isEmpty(age)) {
                    Toast.makeText(requireContext(), "Enter age", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(height)) {
                    Toast.makeText(requireContext(), "Enter height", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(weight)) {
                    Toast.makeText(requireContext(), "Enter weight", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    Intent intent = new Intent(requireActivity(), MainActivity.class);
                    startActivity(intent);
                }

            }
        });

        return view;
    }
}