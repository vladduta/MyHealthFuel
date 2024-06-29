package com.healthfuel.myhealthfuel.mainpage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.healthfuel.myhealthfuel.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class WorkoutFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference personalWorkoutRef;
    TextView textViewTitle, estimatedWorkoutDuration, estimatedMealCalories;
    ImageButton backButton, addButton;
    Button finishWorkoutButton;
    RecyclerView recyclerView;
    String title, exerciseId, exerciseDescription;
    int sets, reps, weight, duration, rest, userWeight, met;
    ArrayList<String> exerciseDescriptions;
    ArrayList<Integer> images;
    ArrayList<String> titles;
    ArrayList<String> exerciseIds;
    Map<String, Object> hashMapWorkoutDuration = new HashMap<>();
    Map<String, Object> hashMapWorkoutCalories = new HashMap<>();
    Map<String, Object> hashMapTotalCalories = new HashMap<>();
    AddedExerciseAdapter adapter;
    int workoutDuration, workoutCalories;
    ItemTouchHelper itemTouchHelper;
    int currentDay;
    String formattedDate;
    double totalBurnedCalories = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout, container, false);

        //obtinerea zilei si a datei curente
        Calendar calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.set(Calendar.DAY_OF_YEAR, currentDay);
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        formattedDate = dateFormat.format(date);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        recyclerView = view.findViewById(R.id.workoutRecyclerView);
        textViewTitle = view.findViewById(R.id.textViewWorkoutTitle);
        backButton = view.findViewById(R.id.backButton);
        addButton = view.findViewById(R.id.addWorkout);
        finishWorkoutButton = view.findViewById(R.id.finishWorkoutButton);
        estimatedWorkoutDuration = view.findViewById(R.id.estimatedWorkoutDuration);
        estimatedMealCalories = view.findViewById(R.id.estimatedMealCalories);

         workoutDuration = 0;
         workoutCalories = 0;

        //citim valoarea greutatii utilizatorului din baza de date
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered users").child(user.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() ) {
                    userWeight = dataSnapshot.child("weight").getValue(Integer.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read user weight.", databaseError.toException());
            }
        });

        //preluarea titlului din WorkoutAdapter
        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString("title");
            textViewTitle.setText(title);

            // obtin numele, imaginea si sets+reps+weight+duration din baza de date
                if (title != null) {
                    DatabaseReference addedExercisesRef = FirebaseDatabase.getInstance().getReference("Personal workouts")
                            .child(user.getUid()).child(title).child("Exercises");

                    personalWorkoutRef = FirebaseDatabase.getInstance().getReference("Personal workouts").child(user.getUid());
                    addedExercisesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            exerciseDescriptions = new ArrayList<>();
                            images = new ArrayList<>();
                            titles = new ArrayList<>();
                            exerciseIds = new ArrayList<>();


                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                DataSnapshot setsSnapshot = snapshot.child("sets");
                                DataSnapshot repsSnapshot = snapshot.child("reps");
                                DataSnapshot weightSnapshot = snapshot.child("weight");
                                DataSnapshot durationSnapshot = snapshot.child("duration");
                                DataSnapshot restSnapshot = snapshot.child("restBetweenSets(sec)");
                                DataSnapshot metSnapshot = snapshot.child("met");

                                // Verific daca sunt prezente nodurile corespunzatoare exercitiilor (cardio sau nu)
                                if (setsSnapshot.exists() && repsSnapshot.exists() && weightSnapshot.exists() && restSnapshot.exists()) {
                                    sets = setsSnapshot.getValue(Integer.class);
                                    reps = repsSnapshot.getValue(Integer.class);
                                    weight = weightSnapshot.getValue(Integer.class);
                                    rest = restSnapshot.getValue(Integer.class);
                                    met = metSnapshot.getValue(Integer.class);

                                    exerciseDescription = sets + " sets, " + reps + " reps, " + weight + " kg, " + rest + " seconds";

                                    workoutCalories += (met * 3.5 * userWeight / 200) * rest / 60;
                                    workoutDuration += (reps * sets * 6 + rest * (sets - 1))/ 60;

                                } else if (durationSnapshot.exists()) {
                                    duration = durationSnapshot.getValue(Integer.class);
                                    met = metSnapshot.getValue(Integer.class);

                                    exerciseDescription =  duration + " minutes";
                                    workoutDuration += duration;
                                    workoutCalories += (met * 3.5 * userWeight / 200) * duration;
                                }

                                exerciseId = snapshot.getKey();
                                exerciseIds.add(exerciseId);
                                exerciseDescriptions.add(exerciseDescription);

                                // citim numele si imaginea exercitiului din tabela exercises
                                String exerciseId = snapshot.getKey();
                                DatabaseReference exerciseRef = FirebaseDatabase.getInstance().getReference("Exercises").child(exerciseId);
                                exerciseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String exerciseTitle = dataSnapshot.child("name").getValue(String.class);
                                            int exerciseImage = dataSnapshot.child("image").getValue(Integer.class);

                                            titles.add(exerciseTitle);
                                            images.add(exerciseImage);

                                            // Verific daca ambele listele au aceeasi dimensiune
                                            if (titles.size() == images.size()) {
                                                adapter = new AddedExerciseAdapter(getContext(), titles, exerciseDescriptions, images, exerciseIds, title);
                                                recyclerView.setAdapter(adapter);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getContext(), "Failed to read exercise details.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            // actualizez textView-ul si baza de date cu durata antrenamentului si nr de cal
                            updateWorkoutDuration();
                            updateWorkoutCalories();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Failed to read added exercises.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
        }

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            //stergerea unui exercitiu din workout
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String exerciseIdToRemove = exerciseIds.get(position);

                DatabaseReference exerciseRef = FirebaseDatabase.getInstance().getReference("Personal workouts")
                        .child(user.getUid()).child(title).child("Exercises").child(exerciseIdToRemove);


                exerciseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("sets").exists() && dataSnapshot.child("restBetweenSets(sec)").exists()) {
                            sets = dataSnapshot.child("sets").getValue(Integer.class);
                            reps = dataSnapshot.child("reps").getValue(Integer.class);
                            rest = dataSnapshot.child("restBetweenSets(sec)").getValue(Integer.class);
                            met = dataSnapshot.child("met").getValue(Integer.class);


                            workoutDuration -= (reps * sets * 6 + rest * (sets - 1))/ 60;
                            updateWorkoutDuration();

                            workoutCalories -= (met * 3.5 * userWeight / 200) * rest / 60;
                            updateWorkoutCalories();

                        } else if (dataSnapshot.child("duration").exists()) {
                            duration = dataSnapshot.child("duration").getValue(Integer.class);
                            met = dataSnapshot.child("met").getValue(Integer.class);


                            workoutDuration -= duration;
                            updateWorkoutDuration();

                            workoutCalories -= (met * 3.5 * userWeight / 200) * duration;
                            updateWorkoutCalories();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to read exercise details.", Toast.LENGTH_SHORT).show();
                    }
                });


                titles.remove(position);
                exerciseDescriptions.remove(position);
                images.remove(position);
                exerciseIds.remove(position);

                adapter.notifyItemRemoved(position); // Notific adaptorul despre eliminarea exercitiului din RecyclerView

                // Actualizare baza de date (dupa stergere)
                DatabaseReference exerciseRefToDelete = FirebaseDatabase.getInstance().getReference("Personal workouts")
                        .child(user.getUid()).child(title).child("Exercises").child(exerciseIdToRemove);

                exerciseRefToDelete.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Exercise deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to delete exercise from database.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Atasez un LinearLayoutManager la RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                NavOptions navOptions = new NavOptions.Builder().setEnterAnim(R.anim.slide_in_left).setExitAnim(R.anim.slide_out_right).setPopEnterAnim(R.anim.slide_in_right).setPopExitAnim(R.anim.slide_out_left).build();
                navController.navigate(R.id.action_workoutFragment_to_homeFragment,null, navOptions);
            }
        });

        //trimitere numele workout-ului (pt a preselecta spinner ul) si navigare catre ExerciseFragmentClick
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedWorkout = textViewTitle.getText().toString();
                Bundle result = new Bundle();
                result.putString("selectedWorkout", selectedWorkout);
                getParentFragmentManager().setFragmentResult("datafromWorkoutFragment", result);

                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_workoutFragment_to_exerciseFragment);
            }
        });


        finishWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWorkoutButton.setEnabled(false); // dezactivarea butonului imediat

                DatabaseReference diaryReference = FirebaseDatabase.getInstance().getReference("Exercise Diary").child(user.getUid()).child(formattedDate);

                // Adaug caloriile totale arse
                diaryReference.child("Total calories burned").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Integer totalBurnedCaloriesDatabase = dataSnapshot.getValue(Integer.class);
                        if (totalBurnedCaloriesDatabase != null) {
                            totalBurnedCalories += totalBurnedCaloriesDatabase + workoutCalories;
                        } else {
                            totalBurnedCalories += 0 + workoutCalories;
                        }

                        diaryReference.child("Total calories burned").setValue(totalBurnedCalories)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            addExercisesToDiary(diaryReference);

                                            Toast.makeText(getContext(), "Workout finished !!!", Toast.LENGTH_SHORT).show();
                                            NavController navController = Navigation.findNavController(v);
                                            navController.navigate(R.id.action_workoutFragment_to_homeFragment);
                                        } else {
                                            Toast.makeText(getContext(), "Failed to update total calories burned", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to update total calories burned", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });






        return view;
    }

    private void updateWorkoutDuration(){
        hashMapWorkoutDuration.put("workoutDuration(min)", workoutDuration);
        estimatedWorkoutDuration.setText((int) workoutDuration + " min");
        personalWorkoutRef.child(title).updateChildren(hashMapWorkoutDuration);

    } private void updateWorkoutCalories(){
        hashMapWorkoutCalories.put("workoutCalories", workoutCalories);
        estimatedMealCalories.setText((int) workoutCalories + " kcal");
        personalWorkoutRef.child(title).updateChildren(hashMapWorkoutCalories);
    }

    // functie pt adaugarea exercitiilor din nodul workout in nodul diary
    private void addExercisesToDiary(DatabaseReference diaryReference) {
        DatabaseReference addedExercisesRef = FirebaseDatabase.getInstance().getReference("Personal workouts")
                .child(user.getUid()).child(title).child("Exercises");

        addedExercisesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String exerciseId = snapshot.getKey();
                    Map<String, Object> exerciseData = new HashMap<>();
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        exerciseData.put(childSnapshot.getKey(), childSnapshot.getValue());
                    }
                    diaryReference.child("Exercises").child(exerciseId).setValue(exerciseData);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to add exercises to diary", Toast.LENGTH_SHORT).show();
            }
        });
    }
}





