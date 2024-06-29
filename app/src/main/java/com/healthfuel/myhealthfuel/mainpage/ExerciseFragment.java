package com.healthfuel.myhealthfuel.mainpage;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.healthfuel.myhealthfuel.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExerciseFragment extends Fragment {

    SearchView searchBar;
    RecyclerView recyclerView;
    ExerciseAdapter adapter;
    ArrayList<String> selectedCategory = new ArrayList<String>();
    ArrayList<Exercises> exerciseItems = new ArrayList<>();
    private String currentSearchText="";
    Button absButton, armsButton, backButton, chestButton, cardioButton, legsButton, shouldersButton, allButton;
    ImageButton backButton1;
    Dialog exerciseDialog; //fereastra de pop up

    private int grey, white, black, royaleBlue;
    private boolean dataAdded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        absButton = view.findViewById(R.id.AbsButton);
        armsButton = view.findViewById(R.id.ArmsButton);
        backButton = view.findViewById(R.id.BackButton);
        backButton1 = view.findViewById(R.id.backButton);
        chestButton = view.findViewById(R.id.ChestButton);
        legsButton = view.findViewById(R.id.LegsButton);
        shouldersButton = view.findViewById(R.id.ShouldersButton);
        allButton = view.findViewById(R.id.AllButton);
        cardioButton = view.findViewById(R.id.CardioButton);


        exerciseDialog = new Dialog(getContext());

        white= ContextCompat.getColor(getContext(), R.color.white);
        black= ContextCompat.getColor(getContext(), R.color.black);
        royaleBlue= ContextCompat.getColor(getContext(), R.color.royalblue);
        grey= ContextCompat.getColor(getContext(), R.color.grey);

        recyclerView = view.findViewById(R.id.exerciseRecyclerView);
        searchBar = view.findViewById(R.id.searchBar);

        if (!dataAdded) {
            exerciseItems.add(new Exercises("Sit-ups", "Abs", R.drawable.sit_ups_image, R.drawable.sit_ups, 5));
            exerciseItems.add(new Exercises("Leg Raises", "Abs", R.drawable.leg_raise_image, R.drawable.leg_raise, 5));
            exerciseItems.add(new Exercises("Cable One Arm Curl", "Arms", R.drawable.cable_one_arm_curl_image, R.drawable.cable_one_arm_curl, 5));
            exerciseItems.add(new Exercises("Dumbbell Biceps Curl", "Arms", R.drawable.dumbell_biceps_curl_image, R.drawable.dumbell_biceps_curl, 5));
            exerciseItems.add(new Exercises("Hammer Curl", "Arms", R.drawable.hammer_curl_image, R.drawable.hammer_curl, 5));
            exerciseItems.add(new Exercises("Lat Pulldown", "Back", R.drawable.lat_pulldown_image, R.drawable.lat_pulldown, 5));
            exerciseItems.add(new Exercises("Reverse Grip Lat Pulldown", "Back", R.drawable.reverse_lat_pulldown_image, R.drawable.reverse_lat_pulldown, 5));
            exerciseItems.add(new Exercises("Seated Row", "Back", R.drawable.seated_row_image, R.drawable.seated_row, 5));
            exerciseItems.add(new Exercises("Barbell Bench Press", "Chest", R.drawable.barbell_bench_press_image, R.drawable.barbell_bench_press, 5));
            exerciseItems.add(new Exercises("Incline Barbell Bench Press", "Chest", R.drawable.incline_bench_press_image, R.drawable.incline_bench_press, 5));
            exerciseItems.add(new Exercises("Dumbbell Bench Press", "Chest", R.drawable.dumbell_bench_press_image, R.drawable.dumbell_bench_press, 5));
            exerciseItems.add(new Exercises("Leg Extension", "Legs", R.drawable.leg_extension_image, R.drawable.leg_extension, 5));
            exerciseItems.add(new Exercises("Leg Press", "Legs", R.drawable.leg_press_image, R.drawable.leg_press, 5));
            exerciseItems.add(new Exercises("Lateral Raise", "Shoulders", R.drawable.lateral_raise_image, R.drawable.lateral_raise, 5));
            exerciseItems.add(new Exercises("Front Raise", "Shoulders", R.drawable.front_raise_image, R.drawable.front_raise, 5));
            exerciseItems.add(new Exercises("Treadmill Running", "Cardio", R.drawable.treadmill_image, R.drawable.treadmill, 5));
            exerciseItems.add(new Exercises("Elliptical", "Cardio", R.drawable.elliptical_image, R.drawable.elliptical, 5));
            exerciseItems.add(new Exercises("Burpee", "Cardio", R.drawable.burpee_image, R.drawable.burpee, 5));
            exerciseItems.add(new Exercises("Battle Ropes", "Cardio", R.drawable.battle_rope_image, R.drawable.battle_rope, 5));
            exerciseItems.add(new Exercises("Dumbbell Fly", "Chest", R.drawable.dumbbell_fly_image, R.drawable.dumbbell_fly, 5));
            exerciseItems.add(new Exercises("Lever Chest Press", "Chest", R.drawable.lever_chest_press_image, R.drawable.lever_chest_press, 5));
            exerciseItems.add(new Exercises("Push Up", "Chest", R.drawable.push_ups_image, R.drawable.push_ups, 5));
            exerciseItems.add(new Exercises("Cable Fly", "Chest", R.drawable.cable_fly_image, R.drawable.cable_fly, 5));
            exerciseItems.add(new Exercises("Chest Dips", "Chest", R.drawable.chest_dips_image, R.drawable.chest_dips, 5));
            exerciseItems.add(new Exercises("Dumbbell Bent-over", "Back", R.drawable.dumbbel_bent_over_image, R.drawable.dumbell_bench_press, 5));
            exerciseItems.add(new Exercises("Pull Up", "Back", R.drawable.pull_up_image, R.drawable.pull_up, 5));
            exerciseItems.add(new Exercises("T-Bar Row", "Back", R.drawable.tbar_row_image, R.drawable.tbar_row, 5));
            exerciseItems.add(new Exercises("Barbell Bent-over Row", "Back", R.drawable.barbell_bent_over_image, R.drawable.barbell_bent_over, 5));
            exerciseItems.add(new Exercises("Barbell Underhand Bent-over Row", "Back", R.drawable.barbell_underhand_bent_over_image, R.drawable.barbell_underhand_bent_over, 5));
            exerciseItems.add(new Exercises("Cable High Row", "Back", R.drawable.cable_high_row_image, R.drawable.cable_high_row, 5));
            exerciseItems.add(new Exercises("Barbell Biceps Curl", "Arms", R.drawable.barbell_biceps_curl_image, R.drawable.barbell_biceps_curl, 5));
            exerciseItems.add(new Exercises("Concentration Curl", "Arms", R.drawable.concentration_curl_image, R.drawable.concentration_curl, 5));
            exerciseItems.add(new Exercises("Cable Triceps Pushdown", "Arms", R.drawable.cable_triceps_pushdown_image, R.drawable.cable_triceps_pushdown, 5));
            exerciseItems.add(new Exercises("Dumbbell Triceps Extension", "Arms", R.drawable.dumbbell_triceps_extension_image, R.drawable.dumbbell_triceps_extension, 5));
            exerciseItems.add(new Exercises("Dumbbell Kickback", "Arms", R.drawable.dumbbell_kickback_image, R.drawable.dumbbell_kickback, 5));
            exerciseItems.add(new Exercises("Lever Triceps Dip", "Arms", R.drawable.lever_triceps_dip_image, R.drawable.lever_triceps_dip, 5));
            exerciseItems.add(new Exercises("Squat", "Legs", R.drawable.squat_image, R.drawable.squat, 5));
            exerciseItems.add(new Exercises("Barbell Squat", "Legs", R.drawable.barbell_squat_image, R.drawable.barbell_squat, 5));
            exerciseItems.add(new Exercises("Dumbbell Lunge", "Legs", R.drawable.dumbbell_lunge_image, R.drawable.dumbbell_lunge, 5));
            exerciseItems.add(new Exercises("Barbell Lunge", "Legs", R.drawable.barbell_lunge_image, R.drawable.barbell_lunge, 5));
            exerciseItems.add(new Exercises("Lying Leg Curl", "Legs", R.drawable.lying_leg_curl_image, R.drawable.lying_leg_curl, 5));
            exerciseItems.add(new Exercises("Hip Adduction", "Legs", R.drawable.hip_adduction_image, R.drawable.hip_adduction, 5));
            exerciseItems.add(new Exercises("Military Press", "Shoulders", R.drawable.military_press_image, R.drawable.military_press, 5));
            exerciseItems.add(new Exercises("Overhead Press", "Shoulders", R.drawable.overhead_press_image, R.drawable.overhead_press, 5));
            exerciseItems.add(new Exercises("Reverse Fly", "Shoulders", R.drawable.reverse_fly_image, R.drawable.reverse_fly, 5));
            exerciseItems.add(new Exercises("Arnold Press", "Shoulders", R.drawable.arnold_press_image, R.drawable.arnold_press, 5));
            exerciseItems.add(new Exercises("Dumbbell Side Bend", "Abs", R.drawable.dumbbell_side_bend_image, R.drawable.dumbbell_side_bend, 5));
            exerciseItems.add(new Exercises("Cable Crunch", "Abs", R.drawable.cable_crunch_image, R.drawable.cable_crunch, 5));
            exerciseItems.add(new Exercises("Side Plank", "Abs", R.drawable.side_plank_image, R.drawable.side_plank, 5));
            exerciseItems.add(new Exercises("Band Twist", "Abs", R.drawable.band_twist_image, R.drawable.band_twist, 5));
            exerciseItems.add(new Exercises("Twisting Crunch", "Abs", R.drawable.twisting_crunch_image, R.drawable.twisting_crunch, 5));
            exerciseItems.add(new Exercises("Lever Crunch", "Abs", R.drawable.lever_crunch_image, R.drawable.lever_crunch, 5));
            exerciseItems.add(new Exercises("Running", "Cardio", R.drawable.running_image, R.drawable.running, 7));
            exerciseItems.add(new Exercises("Skiing", "Cardio", R.drawable.skiing_image, R.drawable.skiing, 7));
            exerciseItems.add(new Exercises("Badminton", "Cardio", R.drawable.badminton_image, R.drawable.badminton, 6));
            exerciseItems.add(new Exercises("Basketball", "Cardio", R.drawable.basketball_image, R.drawable.basketball, 6));
            exerciseItems.add(new Exercises("Football", "Cardio", R.drawable.football_image, R.drawable.football, 7));
            exerciseItems.add(new Exercises("Tennis", "Cardio", R.drawable.tennis_image, R.drawable.tennis, 6));
            exerciseItems.add(new Exercises("Volleyball", "Cardio", R.drawable.volleyball_image, R.drawable.volleyball, 5));
            exerciseItems.add(new Exercises("Baseball", "Cardio", R.drawable.baseball_image, R.drawable.baseball, 5));
            exerciseItems.add(new Exercises("Martial Arts", "Cardio", R.drawable.martial_arts_image, R.drawable.martial_arts, 5));
            exerciseItems.add(new Exercises("Boxing", "Cardio", R.drawable.boxing_image, R.drawable.boxing, 6));
            exerciseItems.add(new Exercises("Fencing", "Cardio", R.drawable.fencing_image, R.drawable.fencing, 6));
            exerciseItems.add(new Exercises("Bicycling", "Cardio", R.drawable.bicycling_image, R.drawable.bicycling, 7));
            exerciseItems.add(new Exercises("Rowing", "Cardio", R.drawable.rowing_image, R.drawable.rowing, 5));
            exerciseItems.add(new Exercises("Windsurfing", "Cardio", R.drawable.windsurfing_image, R.drawable.windsurfing, 3));
            exerciseItems.add(new Exercises("Swimming", "Cardio", R.drawable.swimming_image, R.drawable.swimming, 7));
            dataAdded = true;
        }
// pt incarcarea datelor exercitiilor in baza de date
        //DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Exercises");
       // for (Exercises exercise : exerciseItems) {
        //  databaseRef.push().setValue(exercise);
      //  }


        adapter = new ExerciseAdapter(getContext(), exerciseItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });


        selectAll();

        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAll();
            }
        });

        absButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCategory("Abs", absButton);
            }
        });

        armsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCategory("Arms", armsButton);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCategory("Back", backButton);
            }
        });

        chestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCategory("Chest", chestButton);
            }
        });

        cardioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCategory("Cardio", cardioButton);
            }
        });

        legsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCategory("Legs", legsButton);
            }
        });

        shouldersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCategory("Shoulders", shouldersButton);
            }
        });


        //backButton
        backButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                NavOptions navOptions = new NavOptions.Builder().setEnterAnim(R.anim.slide_in_left).setExitAnim(R.anim.slide_out_right).setPopEnterAnim(R.anim.slide_in_left).setPopExitAnim(R.anim.slide_out_right).build();

                navController.navigate(R.id.action_exerciseFragment_to_homeFragment,null,navOptions);
            }
        });

        return view;
    }



    private void selectAll() {
        selectedCategory.clear();
        selectedCategory.add("All");
        searchBar.setQuery("", false);
        searchBar.clearFocus();
        filterList("");
        unSelectAllFilterBtn();
        lookSelected(allButton);
    }

    private void changeCategory(String category, Button button) {
        boolean isSelected = button.getCurrentTextColor() == white;

        if (isSelected) {
            lookUnSelected(button);
            selectedCategory.remove(category);
        } else {
            filterType(category);
            lookSelected(button);
            lookUnSelected(allButton);
            selectedCategory.remove("All");
        }

        filterList(currentSearchText);

        if (selectedCategory.isEmpty()) {
            selectAll();
        }
    }

    private void filterList(String text) {
        currentSearchText = text;
        ArrayList<Exercises> filteredList = new ArrayList<>();
        for (Exercises ex : exerciseItems) {
            if (ex.getName().toLowerCase().contains(text.toLowerCase())) {
                if (selectedCategory.contains("All")) {
                    filteredList.add(ex);
                } else {
                    for (String filter : selectedCategory) {
                        if (ex.getType().toLowerCase().contains(filter.toLowerCase())) {
                            filteredList.add(ex);
                        }
                    }
                }
            }
        }

        if (filteredList.isEmpty()) {
            adapter.clearFilteredList();
        } else {
            adapter.setFilteredList(filteredList);
        }
    }

    private void filterType(String status) {
        selectedCategory.add(status);
        ArrayList<Exercises> filteredList = new ArrayList<>();

        if (status.equals("All")) {
            adapter.setFilteredList(exerciseItems);
            return;
        }

        for (Exercises ex : exerciseItems) {
            for (String filter : selectedCategory) {
                if (ex.getType().toLowerCase().contains(filter.toLowerCase())) {
                    if (currentSearchText.equals("") || ex.getName().toLowerCase().contains(currentSearchText.toLowerCase())) {
                        filteredList.add(ex);
                    }
                }
            }
        }
        adapter.setFilteredList(filteredList);
    }

    private void lookSelected(Button btn) {
        btn.setTextColor(white);
        btn.setBackgroundColor(royaleBlue);
    }

    private void lookUnSelected(Button btn) {
        btn.setTextColor(black);
        btn.setBackgroundColor(grey);
    }

    private void unSelectAllFilterBtn() {
        lookUnSelected(absButton);
        lookUnSelected(armsButton);
        lookUnSelected(backButton);
        lookUnSelected(chestButton);
        lookUnSelected(cardioButton);
        lookUnSelected(legsButton);
        lookUnSelected(shouldersButton);
    }

}
