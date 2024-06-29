package com.healthfuel.myhealthfuel.mainpage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.healthfuel.myhealthfuel.R;

import pl.droidsonroids.gif.GifImageView;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ExerciseFragmentClick extends Fragment {
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Map<String, Object> hashMap = new HashMap<>();
    ArrayList<String> workoutTitles = new ArrayList<>();
    String exerciseId, exerciseName, preselectedWorkout, exerciseType;
    boolean cardioExercise = false;
    Integer exerciseMET;
    String selectedWorkoutTitle;
    Spinner spinnerWorkouts;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_AUTOMATED_COUNTING = 123;
    TextView textViewExerciseTitle, textViewSets, textViewReps,textViewWeight, textViewWeight1, editTextWeight, textViewRest, textViewRest1, textViewDuration, textViewDuration1;

    EditText editTextSets,editTextRest,editTextReps, editTextDuration;
    String exerciseNameBundle;
    GifImageView gifImageView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_click, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        ImageButton backButton = view.findViewById(R.id.backButton);
        Button addButton = view.findViewById(R.id.addExerciseButton);
        Button automatedCountingButton = view.findViewById(R.id.automatedCountingButton);
         gifImageView = view.findViewById(R.id.gifImage);
        editTextSets = view.findViewById(R.id.editTextSets);
        editTextReps = view.findViewById(R.id.editTextReps);
        editTextRest = view.findViewById(R.id.editTextRest);
         editTextWeight = view.findViewById(R.id.editTextWeight);
         editTextDuration = view.findViewById(R.id.editTextDuration);
        spinnerWorkouts = view.findViewById(R.id.spinnerWorkouts);

         textViewSets = view.findViewById(R.id.textViewSets);
         textViewReps = view.findViewById(R.id.textViewReps);
         textViewWeight = view.findViewById(R.id.textViewWeight);
         textViewRest = view.findViewById(R.id.textViewRest);
         textViewDuration = view.findViewById(R.id.textViewDuration);
         textViewDuration1 = view.findViewById(R.id.textViewDuration1);
         textViewRest1 = view.findViewById(R.id.textViewRest1);
         textViewWeight1 = view.findViewById(R.id.textViewWeight1);
         textViewExerciseTitle = view.findViewById(R.id.textViewExerciseTitle);

         //ascund initial toate editText si textView-urile
        editTextSets.setVisibility(View.GONE);
        textViewSets.setVisibility(View.GONE);

        editTextReps.setVisibility(View.GONE);
        textViewReps.setVisibility(View.GONE);

        editTextWeight.setVisibility(View.GONE);
        textViewWeight.setVisibility(View.GONE);
        textViewWeight1.setVisibility(View.GONE);

        editTextRest.setVisibility(View.GONE);
        textViewRest.setVisibility(View.GONE);
        textViewRest1.setVisibility(View.GONE);

        editTextDuration.setVisibility(View.GONE);
        textViewDuration.setVisibility(View.GONE);
        textViewDuration1.setVisibility(View.GONE);
        automatedCountingButton.setVisibility(View.GONE);


        //extragere mealtype din SharedPreferences
        SharedPreferences preferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
         preselectedWorkout = preferences.getString("lastSelectedWorkoutType", preselectedWorkout);

        // Set up spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, workoutTitles);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerWorkouts.setAdapter(adapter);


        // Retrieve workout name from WorkoutFragment to preselect the spinner
        getParentFragmentManager().setFragmentResultListener("datafromWorkoutFragment", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                preselectedWorkout = result.getString("selectedWorkout");
                saveWorkoutType(preselectedWorkout);
            }
        });



        Bundle bundle = getArguments();
        if (bundle != null) {
            int gifImageId = bundle.getInt("gifImageId");
            cardioExercise = bundle.getBoolean("cardioExercise");
            if (bundle.getString("selectedWorkout2") != null) {
                preselectedWorkout = bundle.getString("selectedWorkout2");
            }

            gifImageView.setImageResource(gifImageId);




            DatabaseReference exercisesRef = FirebaseDatabase.getInstance().getReference("Exercises");
            exercisesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Integer gifId = snapshot.child("gif").getValue(Integer.class);
                        if (gifId != null && gifId == gifImageId) {
                            exerciseName = snapshot.child("name").getValue(String.class);
                            exerciseType = snapshot.child("type").getValue(String.class);
                            textViewExerciseTitle.setText(exerciseName);

                            exerciseId = snapshot.getKey();
                            exerciseMET = snapshot.child("met").getValue(Integer.class);

                            if (!exerciseType.equals("Cardio")) {
                                editTextSets.setVisibility(View.VISIBLE);
                                textViewSets.setVisibility(View.VISIBLE);

                                editTextReps.setVisibility(View.VISIBLE);
                                textViewReps.setVisibility(View.VISIBLE);

                                editTextWeight.setVisibility(View.VISIBLE);
                                textViewWeight.setVisibility(View.VISIBLE);
                                textViewWeight1.setVisibility(View.VISIBLE);

                                editTextRest.setVisibility(View.VISIBLE);
                                textViewRest.setVisibility(View.VISIBLE);
                                textViewRest1.setVisibility(View.VISIBLE);

                            } else {
                                editTextDuration.setVisibility(View.VISIBLE);
                                textViewDuration.setVisibility(View.VISIBLE);
                                textViewDuration1.setVisibility(View.VISIBLE);
                            }

                            // pt btn de nr repetari
                            if (exerciseName.equals("Cable One Arm Curl")) {
                                automatedCountingButton.setVisibility(View.VISIBLE);
                            }else {
                                automatedCountingButton.setVisibility(View.GONE);

                            }

                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to read exercises.", Toast.LENGTH_SHORT).show();
                }
            });


        }



            DatabaseReference personalWorkoutRef = FirebaseDatabase.getInstance().getReference("Personal workouts").child(user.getUid());
            personalWorkoutRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    workoutTitles.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String workoutName = snapshot.getKey();
                        workoutTitles.add(workoutName);
                    }
                    adapter.notifyDataSetChanged();

                       // loadWorkoutType();

                    if (workoutTitles.isEmpty()) {
                        addButton.setVisibility(View.GONE);

                        editTextSets.setVisibility(View.GONE);
                        textViewSets.setVisibility(View.GONE);

                        editTextReps.setVisibility(View.GONE);
                        textViewReps.setVisibility(View.GONE);

                        editTextWeight.setVisibility(View.GONE);
                        textViewWeight.setVisibility(View.GONE);
                        textViewWeight1.setVisibility(View.GONE);

                        editTextRest.setVisibility(View.GONE);
                        textViewRest.setVisibility(View.GONE);
                        textViewRest1.setVisibility(View.GONE);

                        editTextDuration.setVisibility(View.GONE);
                        textViewDuration.setVisibility(View.GONE);
                        textViewDuration1.setVisibility(View.GONE);
                    }

                    if (preselectedWorkout != null) {
                        int position = workoutTitles.indexOf(preselectedWorkout);
                        if (position != -1) {
                            spinnerWorkouts.setSelection(position);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to read workoutItems.", Toast.LENGTH_SHORT).show();
                }
            });





        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //salvare mealtype-ul in SharedPreferences
                SharedPreferences preferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("lastSelectedWorkoutType", preselectedWorkout);
                editor.apply();

                // Revenire la ecranul anterior
                requireActivity().onBackPressed();
                adapter.notifyDataSetChanged();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sets = editTextSets.getText().toString();
                String reps = editTextReps.getText().toString();
                String weight = editTextWeight.getText().toString();
                String durartion = editTextDuration.getText().toString();
                String rest = editTextRest.getText().toString();


                if (!cardioExercise) {
                    if (TextUtils.isEmpty(sets) || TextUtils.isEmpty(reps) || TextUtils.isEmpty(weight) || TextUtils.isEmpty(rest)) {
                        Toast.makeText(getContext(), "Complete all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                preselectedWorkout = spinnerWorkouts.getSelectedItem().toString();


                if (!cardioExercise) {
                    hashMap.put("sets", Integer.parseInt(sets));
                    hashMap.put("reps", Integer.parseInt(reps));
                    hashMap.put("weight", Integer.parseInt(weight));
                    hashMap.put("restBetweenSets(sec)", Integer.parseInt(rest));
                    hashMap.put("met", exerciseMET);
                    hashMap.put("name", exerciseName);
                } else {
                    hashMap.put("duration", Integer.parseInt(durartion));
                    hashMap.put("met", exerciseMET);
                    hashMap.put("name", exerciseName);
                }
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Personal workouts");
                databaseReference.child(user.getUid()).child(preselectedWorkout).child("Exercises").child(exerciseId).setValue(hashMap);


                int position = workoutTitles.indexOf(preselectedWorkout);
                if (position != -1) {
                    Bundle bundle = new Bundle();
                    bundle.putString("title", preselectedWorkout);
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView2);
                    navController.navigate(R.id.action_exerciseFragment2_to_workoutFragment, bundle);
                } else {
                    Toast.makeText(getContext(), "Error: Workout not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        automatedCountingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (exerciseName.equals("Dumbbell Biceps Curl")) {
                   // if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                      //  ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                   // } else {
                        Intent intent = new Intent(requireContext(), RepsCountActivity.class);
                        startActivityForResult(intent, REQUEST_AUTOMATED_COUNTING);

                   // }
               // }else{
                 //   Toast.makeText(getContext(), "The functionality is not yet implemented for this exercise", Toast.LENGTH_SHORT).show();

                //}
            }
        });

        return view;
    }

    private void saveWorkoutType(String workoutType) {

    }

    // obtinem datele numarate automat
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AUTOMATED_COUNTING && resultCode == Activity.RESULT_OK) {

             int sets = data.getIntExtra("sets",0);
             int reps = data.getIntExtra("reps",0);
             int rest = data.getIntExtra("rest",0);

            editTextSets.setText(String.valueOf(sets));
            editTextReps.setText(String.valueOf(reps));
            editTextRest.setText(String.valueOf(rest));
        }
    }


}
