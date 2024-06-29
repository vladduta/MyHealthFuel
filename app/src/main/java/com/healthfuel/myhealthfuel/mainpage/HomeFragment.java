package com.healthfuel.myhealthfuel.mainpage;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements SensorEventListener {
    TextView textViewCaloriesGoal, textViewCaloriesRemaining,textViewProteinsRemaining, textViewCarboRemaining, textViewFatsRemaining, textViewFoodConsumed, textViewExerciseDone;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    Map<String, Object> hashMapCalories = new HashMap<>();
    Map<String, Object> hashMapWorkout = new HashMap<>();
    ArrayList<String> workoutTitles = new ArrayList<>();
    ArrayList<String> workoutDays = new ArrayList<>();
    ArrayList<String> workoutCalories = new ArrayList<>();
    ArrayList<String> workoutDurations = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    WorkoutAdapter adapter;
    RecyclerView recyclerView;
    ImageButton addWorkoutButton;
    String selectedDay;
    int currentDay;
    String formattedDate;
    SensorManager sensorManager = null;
    Sensor stepSensor;
    int totalSteps;
    int currentSteps;
    ProgressBar progressBarCalories, progressBarProteins, progressBarCarbs, progressBarFats;
    TextView steps, goalSteps, textViewKcal;
    int previewsTotalSteps = 0;
    private int lastDay = -1;
    private static final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 1;
    CardView breakfastCard, lunchCard, dinnerCard;
    TextView estimatedBreakfastCalories, estimatedBreakfastProteins, estimatedBreakfastCarbo, estimatedBreakfastFat;
    TextView estimatedLunchCalories, estimatedLunchtProteins, estimatedLunchFat, estimatedLunchCarbo;
    TextView estimatedDinnerCalories, estimatedDinnerProteins, estimatedDinnerCarbo, estimatedDinnerFat;
    int totalCaloriesConsumed,totalBurnedCalories, totalProteinsConsumed, totalCarboConsumed, totalFatsConsumed = 0 ;
    double totalCaloriesGoal, totalProteinsGoal, totalCarboGoal, totalFatsGoal;
    //Toolbar toolbar;

    @Override
    public void onResume() {
        super.onResume();
        totalCaloriesConsumed = 0;
        totalBurnedCalories = 0;
        totalProteinsConsumed = 0;
        totalCarboConsumed = 0;
        totalFatsConsumed = 0;
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        previewsTotalSteps = prefs.getInt("previewsTotalSteps", 0);
        lastDay = prefs.getInt("lastDay", -1);

        if (stepSensor == null) {
            Toast.makeText(getContext(), "No sensor detected on your device", Toast.LENGTH_SHORT).show();
        } else {
            sensorManager.registerListener(this, stepSensor, sensorManager.SENSOR_DELAY_FASTEST);
        }

        steps.setText(String.valueOf(currentSteps));
    }


    @Override
    public void onPause() {
        super.onPause();
        // Salvare previewsTotalSteps in SharedPreferences
        SharedPreferences.Editor editor = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();
        editor.putInt("previewsTotalSteps", previewsTotalSteps);
        editor.putInt("lastDay", lastDay);
        editor.apply();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        SharedPreferences.Editor editor = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();
        editor.putInt("lastDay", lastDay);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            totalSteps = (int) event.values[0]; //returneaza numarul de pasi din accelerometru (totalul de la utlima resetare a dispozitivului)
            currentSteps = totalSteps - previewsTotalSteps;

            if (currentDay != lastDay) {
                // Cream un nou nod pentru noul minut si setam numarul de pasi la 0
                DatabaseReference stepRef = FirebaseDatabase.getInstance().getReference("StepCount").child(user.getUid()).child(String.valueOf(formattedDate));
                stepRef.child("steps").setValue(0); // Resetam numarul total de pasi la zero pentru noul minut
                previewsTotalSteps = totalSteps;
                lastDay = currentDay;

            }
            // Actualizam numarul total de pasi pentru minutul curent in baza de date

            steps.setText(String.valueOf(currentSteps));

            DatabaseReference stepRef = FirebaseDatabase.getInstance().getReference("StepCount").child(user.getUid()).child(formattedDate);
            stepRef.child("steps").setValue(currentSteps);
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
       // totalCaloriesConsumed = 0;
        //obtinerea zilei si a datei curente
        Calendar calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.set(Calendar.DAY_OF_YEAR, currentDay);
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        formattedDate = dateFormat.format(date);


        //toolbar = view.findViewById(R.id.)
        addWorkoutButton = view.findViewById(R.id.addButton);
        recyclerView = view.findViewById(R.id.workoutRecyclerView);
        textViewCaloriesGoal = view.findViewById(R.id.textViewGoalValue);
        textViewFoodConsumed = view.findViewById(R.id.textViewFoodConsumedValue);
        textViewExerciseDone = view.findViewById(R.id.textViewExerciseValue);

        textViewCaloriesRemaining = view.findViewById(R.id.textViewCaloriesRemaining);
        textViewProteinsRemaining = view.findViewById(R.id.textViewProteinsRemain);
        textViewCarboRemaining = view.findViewById(R.id.textViewCarbsRemain);
        textViewFatsRemaining = view.findViewById(R.id.textViewFatsRemain);
       // progressBarSteps = view.findViewById(R.id.progressBarSteps);
        progressBarCalories = view.findViewById(R.id.progressBarCalories);
        progressBarProteins = view.findViewById(R.id.progressBarProteins);
        progressBarCarbs = view.findViewById(R.id.progressBarCarbs);
        progressBarFats = view.findViewById(R.id.progressBarFats);
        steps = view.findViewById(R.id.textViewSteps);
        goalSteps = view.findViewById(R.id.textViewStepsValue);
        textViewKcal = view.findViewById(R.id.textViewKcal);
        breakfastCard = view.findViewById(R.id.breakfastCard);
        lunchCard = view.findViewById(R.id.lunchCard);
        dinnerCard = view.findViewById(R.id.dinnerCard);
        estimatedBreakfastCalories = view.findViewById(R.id.estimatedBreakfastCalories);
        estimatedBreakfastProteins = view.findViewById(R.id.estimatedBreakfastProteins);
        estimatedBreakfastCarbo = view.findViewById(R.id.estimatedBreakfastCarbo);
        estimatedBreakfastFat = view.findViewById(R.id.estimatedBreakfastFat);

        estimatedLunchCalories = view.findViewById(R.id.estimatedLunchCalories);
        estimatedLunchtProteins = view.findViewById(R.id.estimatedLunchProteins);
        estimatedLunchCarbo = view.findViewById(R.id.estimatedLunchCarbo);
        estimatedLunchFat = view.findViewById(R.id.estimatedLunchFat);

        estimatedDinnerCalories = view.findViewById(R.id.estimatedDinnerCalories);
        estimatedDinnerProteins = view.findViewById(R.id.estimatedDinnerProteins);
        estimatedDinnerCarbo = view.findViewById(R.id.estimatedDinnerCarbo);
        estimatedDinnerFat = view.findViewById(R.id.estimatedDinnerFat);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);


        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        adapter = new WorkoutAdapter(getContext(), workoutTitles, workoutDays, workoutDurations, workoutCalories);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);




        databaseReference = FirebaseDatabase.getInstance().getReference("Registered users").child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer age = dataSnapshot.child("age").getValue(Integer.class);
                Integer height = dataSnapshot.child("height").getValue(Integer.class);
                Integer weight = dataSnapshot.child("weight").getValue(Integer.class);
                String gender = dataSnapshot.child("gender").getValue(String.class);
                String activityLevel = dataSnapshot.child("activity level").getValue(String.class);
                String goal = dataSnapshot.child("goal").getValue(String.class);
                Integer stepsGoal = dataSnapshot.child("steps goal").getValue(Integer.class);

                double bmr = 0;
                double activity = 0;
                double protein = 0;

                switch (activityLevel) {
                    case "Sedentary":
                        activity = 1.2;
                        stepsGoal = 2000;
                        break;
                    case "Low Active":
                        activity = 1.375;
                        stepsGoal = 5000;

                        break;
                    case "Active":
                        activity = 1.55;
                        stepsGoal = 7000;

                        break;
                    case "Very Active":
                        activity = 1.725;
                        stepsGoal = 9000;

                        break;
                }
                goalSteps.setText(" / " + stepsGoal);

                if (gender.equals("Male")) {
                    bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
                } else if (gender.equals("Female")) {
                    bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
                }

                totalCaloriesGoal = bmr * activity; //TDEE

                if (goal.equals("Lose Weight")) {
                    totalCaloriesGoal *= 0.8;
                } else if (goal.equals("Gain Weight")) {
                    totalCaloriesGoal *= 1.2;
                }

                if(activityLevel.equals("Sedentary")){
                    protein = 0.8; // g / kg
                }else{
                    protein = 1.2;

                }

                totalProteinsGoal = protein * weight;
                totalFatsGoal = 0.25 * totalCaloriesGoal / 9;
                totalCarboGoal = (totalCaloriesGoal - 4 * totalProteinsGoal - 9 * totalFatsGoal) / 4;

                textViewCaloriesGoal.setText((int) totalCaloriesGoal + "  kCal");
                progressBarCalories.setMax((int) totalCaloriesGoal);
                progressBarFats.setMax((int) totalFatsGoal);
                progressBarCarbs.setMax((int) totalCarboGoal);

                updateCaloriesProgress(totalCaloriesConsumed,totalBurnedCalories, totalCaloriesGoal, totalProteinsConsumed,totalProteinsGoal, totalCarboConsumed,totalCarboGoal,totalFatsConsumed,totalFatsGoal);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        DatabaseReference personalWorkoutRef = FirebaseDatabase.getInstance().getReference("Personal workouts").child(user.getUid());
        personalWorkoutRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workoutTitles.clear();
                workoutDays.clear();
                workoutCalories.clear();
                workoutDurations.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String workoutName = snapshot.getKey(); // cheia elementului din baza de date (numele antrenamentului)
                    String dayName = snapshot.child("day").getValue(String.class);
                    String workoutDuration = "0 min";
                    String workoutCalorie = "0 kcal";
                    if (snapshot.child("workoutDuration(min)").exists()) {
                        workoutDuration = snapshot.child("workoutDuration(min)").getValue(Integer.class) + " min";
                    }
                    if (snapshot.child("workoutCalories").exists()) {
                        workoutCalorie = snapshot.child("workoutCalories").getValue(Integer.class) + " kcal";
                    }
                    workoutTitles.add(workoutName);
                    workoutDays.add(dayName);
                    workoutDurations.add(workoutDuration);
                    workoutCalories.add(workoutCalorie);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to read workoutItems.", Toast.LENGTH_SHORT).show();
            }
        });
        DatabaseReference breakfastRef = FirebaseDatabase.getInstance().getReference("Meals Diary").child(user.getUid()).child(String.valueOf(formattedDate)).child("Breakfast");
        breakfastRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer totalBreakfastCalories = dataSnapshot.child("total_kcal").getValue(Integer.class);
                    Double totalBreakfastCarbs = dataSnapshot.child("total_carbs").getValue(Double.class);
                    Double totalBreakfastProteins = dataSnapshot.child("total_proteins").getValue(Double.class);
                    Double totalBreakfastFats = dataSnapshot.child("total_fats").getValue(Double.class);
                    if (totalBreakfastCalories != null) {
                        estimatedBreakfastCalories.setText(totalBreakfastCalories + " kcal");

                        totalCaloriesConsumed += totalBreakfastCalories;
                        totalProteinsConsumed += totalBreakfastProteins;
                        totalFatsConsumed += totalBreakfastFats;
                        totalCarboConsumed += totalBreakfastCarbs;
                        updateCaloriesProgress(totalCaloriesConsumed,totalBurnedCalories, totalCaloriesGoal, totalProteinsConsumed,totalProteinsGoal, totalCarboConsumed,totalCarboGoal,totalFatsConsumed,totalFatsGoal);
                    }
                    if (totalBreakfastCarbs != null) {
                        estimatedBreakfastCarbo.setText(totalBreakfastCarbs.intValue() + " Carbs");
                    }
                    if (totalBreakfastProteins != null) {
                        estimatedBreakfastProteins.setText(totalBreakfastProteins.intValue() + " Proteins");
                    }
                    if (totalBreakfastFats != null) {
                        estimatedBreakfastFat.setText(totalBreakfastFats.intValue() + " Fats");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference LunchRef = FirebaseDatabase.getInstance().getReference("Meals Diary").child(user.getUid()).child(String.valueOf(formattedDate)).child("Lunch");
        LunchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer totalLunchCalories = dataSnapshot.child("total_kcal").getValue(Integer.class);
                    Double totalLunchCarbs = dataSnapshot.child("total_carbs").getValue(Double.class);
                    Double totalLunchProteins = dataSnapshot.child("total_proteins").getValue(Double.class);
                    Double totalLunchFats = dataSnapshot.child("total_fats").getValue(Double.class);
                    if (totalLunchCalories != null) {
                        estimatedLunchCalories.setText(totalLunchCalories + " kcal");

                        totalCaloriesConsumed += totalLunchCalories;
                        totalProteinsConsumed += totalLunchProteins;
                        totalFatsConsumed += totalLunchFats;
                        totalCarboConsumed += totalLunchCarbs;
                        updateCaloriesProgress(totalCaloriesConsumed,totalBurnedCalories, totalCaloriesGoal, totalProteinsConsumed,totalProteinsGoal, totalCarboConsumed,totalCarboGoal,totalFatsConsumed,totalFatsGoal);

                    }
                    if (totalLunchCarbs != null) {
                        estimatedLunchCarbo.setText(totalLunchCarbs.intValue() + " Carbs");
                    }
                    if (totalLunchProteins != null) {
                        estimatedLunchtProteins.setText(totalLunchProteins.intValue() + " Proteins");
                    }
                    if (totalLunchFats != null) {
                        estimatedLunchFat.setText(totalLunchFats.intValue() + " Fats");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        DatabaseReference dinnerRef = FirebaseDatabase.getInstance().getReference("Meals Diary").child(user.getUid()).child(String.valueOf(formattedDate)).child("Dinner");
        dinnerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer totalDinnerCalories = dataSnapshot.child("total_kcal").getValue(Integer.class);
                    Double totalDinnerCarbs = dataSnapshot.child("total_carbs").getValue(Double.class);
                    Double totalDinnerProteins = dataSnapshot.child("total_proteins").getValue(Double.class);
                    Double totalDinnerFats = dataSnapshot.child("total_fats").getValue(Double.class);
                    if (totalDinnerCalories != null) {
                        estimatedDinnerCalories.setText(totalDinnerCalories + " kcal");

                        totalCaloriesConsumed += totalDinnerCalories;
                        totalProteinsConsumed += totalDinnerProteins;
                        totalFatsConsumed += totalDinnerFats;
                        totalCarboConsumed += totalDinnerCarbs;
                        updateCaloriesProgress(totalCaloriesConsumed,totalBurnedCalories, totalCaloriesGoal, totalProteinsConsumed,totalProteinsGoal, totalCarboConsumed,totalCarboGoal,totalFatsConsumed,totalFatsGoal);

                    }
                    if (estimatedDinnerCarbo != null) {
                        estimatedDinnerCarbo.setText(totalDinnerCarbs.intValue() + " Carbs");
                    }
                    if (estimatedDinnerProteins != null) {
                        estimatedDinnerProteins.setText(totalDinnerProteins.intValue() + " Proteins");
                    }
                    if (estimatedDinnerFat != null) {
                        estimatedDinnerFat.setText(totalDinnerFats.intValue() + " Fats");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Exercise Diary").child(user.getUid()).child(formattedDate);
        databaseReference.child("Total calories burned").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    totalBurnedCalories = dataSnapshot.getValue(Integer.class);
                    textViewExerciseDone.setText(totalBurnedCalories + "  kCal");
                    updateCaloriesProgress(totalCaloriesConsumed,totalBurnedCalories, totalCaloriesGoal, totalProteinsConsumed,totalProteinsGoal, totalCarboConsumed,totalCarboGoal,totalFatsConsumed,totalFatsGoal);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        addWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.popup_workout, null);
                TextInputEditText editText1 = view1.findViewById(R.id.workoutEditText);

                RadioButton radioButtonSun = view1.findViewById(R.id.Sun);
                RadioButton radioButtonMon = view1.findViewById(R.id.Mon);
                RadioButton radioButtonTue = view1.findViewById(R.id.Tue);
                RadioButton radioButtonWed = view1.findViewById(R.id.Wed);
                RadioButton radioButtonThu = view1.findViewById(R.id.Thu);
                RadioButton radioButtonFri = view1.findViewById(R.id.Fri);
                RadioButton radioButtonSat = view1.findViewById(R.id.Sat);


                AlertDialog alertDialog = new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Add Workout")
                        .setView(view1)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String workoutName = editText1.getText().toString().trim();
                                if (TextUtils.isEmpty(workoutName)) {
                                    Toast.makeText(getContext(), "Enter a name for your workout", Toast.LENGTH_SHORT).show();
                                    return;
                                }


                                if (radioButtonMon.isChecked()) {
                                    selectedDay = "Monday";
                                } else if (radioButtonTue.isChecked()) {
                                    selectedDay = "Tuesday";
                                } else if (radioButtonWed.isChecked()) {
                                    selectedDay = "Wednesday";
                                } else if (radioButtonThu.isChecked()) {
                                    selectedDay = "Thursday";
                                } else if (radioButtonFri.isChecked()) {
                                    selectedDay = "Friday";
                                } else if (radioButtonSat.isChecked()) {
                                    selectedDay = "Saturday";
                                } else if (radioButtonSun.isChecked()) {
                                    selectedDay = "Sunday";
                                } else {
                                    Toast.makeText(getContext(), "Enter a day for your workout", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                hashMapWorkout.put("day", selectedDay);
                                personalWorkoutRef.child(workoutName).setValue(hashMapWorkout);
                                workoutDays.add(selectedDay);
                                adapter.notifyDataSetChanged();

                                dialog.dismiss();

                            }
                        })

                        .create();
                alertDialog.show();
            }
        });


        //meals Onclick
        breakfastCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("mealType", "Breakfast");
                NavController navController = Navigation.findNavController(v);
                NavOptions navOptions = new NavOptions.Builder().setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_right).setPopExitAnim(R.anim.slide_out_left).build();
                navController.navigate(R.id.action_homeFragment_to_mealFragment2, bundle,navOptions);
            }
        });

        lunchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("mealType", "Lunch");
                NavController navController = Navigation.findNavController(v);
                NavOptions navOptions = new NavOptions.Builder().setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_right).setPopExitAnim(R.anim.slide_out_left).build();
                navController.navigate(R.id.action_homeFragment_to_mealFragment2, bundle,navOptions);
            }
        });

        dinnerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("mealType", "Dinner");
                NavController navController = Navigation.findNavController(v);
                NavOptions navOptions = new NavOptions.Builder().setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_right).setPopExitAnim(R.anim.slide_out_left).build();
                navController.navigate(R.id.action_homeFragment_to_mealFragment2, bundle,navOptions);
            }
        });



        return view;
    }

    private void updateCaloriesProgress(int totalCaloriesConsumed, double totalBurnedCalories, double totalCaloriesGoal, int totalProteinsConsumed, double totalProteinsGoal, int totalCarboConsumed, double totalCarboGoal, int totalFatsConsumed, double totalFatsGoal) {
        int remainingCalories = (int) (totalCaloriesGoal - totalCaloriesConsumed + totalBurnedCalories);
        int remainingProteins = (int) (totalProteinsGoal - totalProteinsConsumed);
        int remainingCarbo = (int) (totalCarboGoal - totalCarboConsumed);
        int remainingFats = (int) (totalFatsGoal - totalFatsConsumed);

        textViewCaloriesRemaining.setText(String.valueOf(Math.abs(remainingCalories)));
        textViewProteinsRemaining.setText(Math.abs(remainingProteins) + "g remain");
        textViewCarboRemaining.setText(Math.abs(remainingCarbo) + "g remain");
        textViewFatsRemaining.setText(Math.abs(remainingFats) + "g remain");


        textViewFoodConsumed.setText(totalCaloriesConsumed + "  kCal");

        progressBarCalories.setProgress(totalCaloriesConsumed);
        progressBarProteins.setProgress(totalProteinsConsumed);
        progressBarCarbs.setProgress(totalCarboConsumed);
        progressBarFats.setProgress(totalFatsConsumed);

        if (remainingCalories < 0) {
            progressBarCalories.setProgressTintList(ColorStateList.valueOf(Color.RED));
            textViewKcal.setText("kcal Over");

        }else {
            progressBarCalories.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.royalblue)));
            progressBarCalories.setProgressDrawable(getResources().getDrawable(R.drawable.circle));
            textViewKcal.setText("kcal Remaining");
        }


        if (remainingProteins < 0) {
            progressBarProteins.setProgressTintList(ColorStateList.valueOf(Color.RED));
            textViewProteinsRemaining.setText(Math.abs(remainingProteins) + "g over");

        }else {
            progressBarProteins.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.salmon)));
            textViewProteinsRemaining.setText(Math.abs(remainingProteins) + "g remain");
        }

        if (remainingCarbo < 0) {
            progressBarCarbs.setProgressTintList(ColorStateList.valueOf(Color.RED));
            textViewCarboRemaining.setText(Math.abs(remainingCarbo) + "g over");

        }else {
            progressBarCarbs.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
            textViewCarboRemaining.setText(Math.abs(remainingCarbo) + "g remain");
        }

        if (remainingFats < 0) {
            progressBarFats.setProgressTintList(ColorStateList.valueOf(Color.RED));
            textViewFatsRemaining.setText(Math.abs(remainingFats) + "g over");

        }else {
            progressBarFats.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
            textViewFatsRemaining.setText(Math.abs(remainingFats) + "g remain");
        }



    }

}
