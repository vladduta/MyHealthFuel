package com.healthfuel.myhealthfuel.mainpage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.healthfuel.myhealthfuel.R;
import com.healthfuel.myhealthfuel.authentication.BeginningActivity;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    TextView userName;
    Button logOutbutton;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    ImageButton backButton;
    Button buttonEmail, buttonAge, buttonGender, buttonHeight, buttonWeight, buttonActivityLevel, buttonGoal;
    String gender, activityLevel, goal;
    Integer age,height,weight;
    Map<String, Object> hashMap = new HashMap<>();
    Map<String, Object> hashMapAge = new HashMap<>();
    Map<String, Object> hashMapActivityLevel = new HashMap<>();
    Map<String, Object> hashMapGoal = new HashMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        logOutbutton = view.findViewById(R.id.buttonLogOut);
        userName = view.findViewById(R.id.userAccountTextView);
        backButton = view.findViewById(R.id.backButton);
        buttonEmail = view.findViewById(R.id.buttonEmail);
        buttonAge = view.findViewById(R.id.buttonAge);
        buttonGender = view.findViewById(R.id.buttonGender);
        buttonHeight = view.findViewById(R.id.buttonHeight);
        buttonWeight = view.findViewById(R.id.buttonWeight);
        buttonActivityLevel = view.findViewById(R.id.buttonActivityLevel);
        buttonGoal = view.findViewById(R.id.buttonGoal);
        buttonGender = view.findViewById(R.id.buttonGender);

        user = auth.getCurrentUser();





        if (user == null) {
            Intent intent = new Intent(getActivity(), BeginningActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        logOutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getActivity(), BeginningActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });



        databaseReference = FirebaseDatabase.getInstance().getReference("Registered users").child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                age = dataSnapshot.child("age").getValue(Integer.class);
                height = dataSnapshot.child("height").getValue(Integer.class);
                weight = dataSnapshot.child("weight").getValue(Integer.class);
                gender = dataSnapshot.child("gender").getValue().toString();
                activityLevel = dataSnapshot.child("activity level").getValue().toString();
                goal = dataSnapshot.child("goal").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();

                // extrag partea dinaintea simbolului @
                String[] parts = email.split("@");
                String namePart = parts[0];
                userName.setText(namePart);

                buttonEmail.setText(email);
                buttonAge.setText(String.valueOf(age));
                buttonHeight.setText(height + " cm");
                buttonWeight.setText(weight + " kg");
                buttonGender.setText(gender);
                buttonActivityLevel.setText(activityLevel);
                buttonGoal.setText(goal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        buttonGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.popup_gender, null);

                RadioButton radioButtonMale = view1.findViewById(R.id.Male);
                RadioButton radioButtonFemale = view1.findViewById(R.id.Female);

                AlertDialog alertDialog = new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Your Gender : ")
                        .setView(view1)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (radioButtonMale.isChecked()) {
                                    gender = "Male";
                                } else if (radioButtonFemale.isChecked()) {
                                    gender = "Female";
                                } else {
                                    Toast.makeText(getContext(), "Enter a gender", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                hashMap.put("gender", gender);
                                databaseReference = FirebaseDatabase.getInstance().getReference("Registered users");
                                databaseReference.child(user.getUid()).updateChildren(hashMap);

                                dialog.dismiss();

                            }
                        })

                        .create();
                alertDialog.show();
            }
        });




        buttonAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.popup_edit, null);
                TextInputEditText editText1 = view1.findViewById(R.id.editText);

                AlertDialog alertDialog = new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Your Age : ")
                        .setView(view1)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String text = editText1.getText().toString().trim();
                                age = Integer.valueOf(text);
                                if (TextUtils.isEmpty(text)) {
                                    Toast.makeText(getContext(), "Enter data", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                hashMapAge.put("age", age);
                                databaseReference = FirebaseDatabase.getInstance().getReference("Registered users");
                                databaseReference.child(user.getUid()).updateChildren(hashMapAge);

                                dialog.dismiss();

                            }
                        })

                        .create();
                alertDialog.show();
            }
        });

        buttonHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.popup_edit, null);
                TextInputEditText editText1 = view1.findViewById(R.id.editText);

                AlertDialog alertDialog = new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Your Height : ")
                        .setView(view1)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String text = editText1.getText().toString().trim();
                                height = Integer.valueOf(text);
                                if (TextUtils.isEmpty(text)) {
                                    Toast.makeText(getContext(), "Enter data", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                hashMapAge.put("height", height);
                                databaseReference = FirebaseDatabase.getInstance().getReference("Registered users");
                                databaseReference.child(user.getUid()).updateChildren(hashMapAge);

                                dialog.dismiss();

                            }
                        })

                        .create();
                alertDialog.show();
            }
        });


        buttonWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.popup_edit, null);
                TextInputEditText editText1 = view1.findViewById(R.id.editText);

                AlertDialog alertDialog = new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Your Weight : ")
                        .setView(view1)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String text = editText1.getText().toString().trim();
                                weight = Integer.valueOf(text);
                                if (TextUtils.isEmpty(text)) {
                                    Toast.makeText(getContext(), "Enter data", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                hashMapAge.put("weight", weight);
                                databaseReference = FirebaseDatabase.getInstance().getReference("Registered users");
                                databaseReference.child(user.getUid()).updateChildren(hashMapAge);

                                dialog.dismiss();

                            }
                        })

                        .create();
                alertDialog.show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                NavOptions navOptions = new NavOptions.Builder().setEnterAnim(R.anim.slide_in_left).setExitAnim(R.anim.slide_out_right).setPopEnterAnim(R.anim.slide_in_left).setPopExitAnim(R.anim.slide_out_right).build();
                navController.navigate(R.id.action_profileFragment_to_homeFragment,null,navOptions);
            }
        });


        buttonActivityLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.popup_activity_level, null);

                RadioButton radioButtonSedentary = view1.findViewById(R.id.Sedentary);
                RadioButton radioButtonLowActive = view1.findViewById(R.id.LowActive);
                RadioButton radioButtonActive = view1.findViewById(R.id.Active);
                RadioButton radioButtonVeryActive = view1.findViewById(R.id.VeryActive);

                AlertDialog alertDialog = new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Your Activity Level : ")
                        .setView(view1)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (radioButtonSedentary.isChecked()) {
                                    activityLevel = "Sedentary";
                                } else if (radioButtonLowActive.isChecked()) {
                                    activityLevel = "Low Active";
                                }else if (radioButtonActive.isChecked()) {
                                    activityLevel = "Active";
                                }else if (radioButtonVeryActive.isChecked()) {
                                    activityLevel = "Very Active";
                                } else {
                                    Toast.makeText(getContext(), "Enter your activity level", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                hashMapActivityLevel.put("activity level", activityLevel);
                                databaseReference = FirebaseDatabase.getInstance().getReference("Registered users");
                                databaseReference.child(user.getUid()).updateChildren(hashMapActivityLevel);

                                dialog.dismiss();

                            }
                        })

                        .create();
                alertDialog.show();
            }
        });


        buttonGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.popup_goal, null);

                RadioButton radioButtonLoseWeight = view1.findViewById(R.id.LoseWeight);
                RadioButton radioButtonMaintainWeight = view1.findViewById(R.id.MaintainWeight);
                RadioButton radioButtonGainWeight = view1.findViewById(R.id.GainWeight);

                AlertDialog alertDialog = new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Your Activity Level : ")
                        .setView(view1)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (radioButtonLoseWeight.isChecked()) {
                                    goal = "Lose Weight";
                                } else if (radioButtonMaintainWeight.isChecked()) {
                                    goal = "Maintain Weight";
                                }else if (radioButtonGainWeight.isChecked()) {
                                    goal = "Gain Weight";
                                } else {
                                    Toast.makeText(getContext(), "Enter your goal", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                hashMapGoal.put("goal", goal);
                                databaseReference = FirebaseDatabase.getInstance().getReference("Registered users");
                                databaseReference.child(user.getUid()).updateChildren(hashMapGoal);

                                dialog.dismiss();

                            }
                        })

                        .create();
                alertDialog.show();
            }
        });


        return view;
    }
}
