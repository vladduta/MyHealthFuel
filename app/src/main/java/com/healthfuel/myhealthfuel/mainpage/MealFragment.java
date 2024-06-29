package com.healthfuel.myhealthfuel.mainpage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.healthfuel.myhealthfuel.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MealFragment extends Fragment {
    String formattedDate;
    int currentDay;
    FirebaseAuth auth;
    FirebaseUser user;
    TextView title, estimatedMealCalories,estimatedMealProteins, estimatedMealCarbs, estimatedMealFats;
    RecyclerView recyclerView;
    ArrayList<Foods> foods = new ArrayList<>();
    ImageButton backButton;
    ImageButton addButton;
    String mealType;
    FoodAdapter adapter;
    Integer totalKcal;
    Double totalProteins,totalCarbs,totalFats;
    String foodId;
    ArrayList<String> foodIds = new ArrayList<>(); // pt a stoca id-ul alimentelor


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal, container, false);

        //obtinerea zilei si a datei curente
        Calendar calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.set(Calendar.DAY_OF_YEAR, currentDay);
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        formattedDate = dateFormat.format(date);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        recyclerView = view.findViewById(R.id.foodsRecyclerView);
        backButton= view.findViewById(R.id.backButton);
        addButton = view.findViewById(R.id.addButton);
        title = view.findViewById(R.id.textViewMealTitle);
        estimatedMealCalories = view.findViewById(R.id.estimatedMealCalories);
        estimatedMealProteins = view.findViewById(R.id.estimatedMealProteins);
        estimatedMealCarbs = view.findViewById(R.id.estimatedMealCarbs);
        estimatedMealFats = view.findViewById(R.id.estimatedMealFats);


        //extragem tipul de masa primit din homeFragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            mealType = bundle.getString("mealType");
        }
        title.setText(mealType);


        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new FoodAdapter(requireContext(), foods,mealType);
        recyclerView.setAdapter(adapter);



        DatabaseReference foodsRef = FirebaseDatabase.getInstance().getReference("Meals Diary").child(user.getUid()).child(String.valueOf(formattedDate)).child(mealType).child("Foods");
        foodsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foods.clear(); // Curat lista existenta de alimente pentru a evita duplicarea datelor
                foodIds.clear();
                totalKcal = 0;
                totalCarbs = 0.0;
                totalFats = 0.0;
                totalProteins = 0.0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Foods food = snapshot.getValue(Foods.class);
                    foods.add(food);

                    String foodId = snapshot.getKey(); // Obtin foodId-ul (cheia) din fiecare copil din dataSnapshot
                    foodIds.add(foodId); //pt stergere aliment

                    totalKcal += food.getKcal(); //calc totalul caloriilor pt meal-ul respectiv
                    totalProteins += food.getProteins();
                    totalFats += food.getFat();
                    totalCarbs += food.getCarbohydrates();
                    estimatedMealCalories.setText(totalKcal+ " kcal");
                    estimatedMealProteins.setText(String.format("%.1f Proteins", totalProteins));
                    estimatedMealCarbs.setText(String.format("%.1f Carbs", totalCarbs));
                    estimatedMealFats.setText(String.format("%.1f Fats", totalFats));
                }
                //adaug in baza de date totalul de cal pt meal
                DatabaseReference mealRef = FirebaseDatabase.getInstance().getReference("Meals Diary").child(user.getUid()).child(String.valueOf(formattedDate)).child(mealType);
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("total_kcal", totalKcal);
                hashMap.put("total_proteins", totalProteins);
                hashMap.put("total_carbs", totalCarbs);
                hashMap.put("total_fats", totalFats);
                mealRef.updateChildren(hashMap);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        //backButton
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                NavOptions navOptions = new NavOptions.Builder().setEnterAnim(R.anim.slide_in_left).setExitAnim(R.anim.slide_out_right).setPopEnterAnim(R.anim.slide_in_left).setPopExitAnim(R.anim.slide_out_right).build();
                navController.navigate(R.id.action_mealFragment2_to_homeFragment,null,navOptions);
            }
        });


        // In MealFragment, in metoda onCreateView()

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT // Directia in care se permite gestul de swipe dreapta
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Pozitia elementului sters
                int position = viewHolder.getAdapterPosition();
                Foods deletedFood = foods.get(position);

                String deletedFoodId = foodIds.remove(position);

                // Actualizarea totalului caloriilor
                totalKcal -= deletedFood.getKcal();
                totalCarbs -= deletedFood.getCarbohydrates();
                totalProteins -= deletedFood.getProteins();
                totalFats -= deletedFood.getFat();
                estimatedMealCalories.setText( totalKcal + " kcal");
                estimatedMealProteins.setText( String.format("%.1f", totalProteins) + " Proteins");
                estimatedMealCarbs.setText( String.format("%.1f", totalCarbs) + " Carbs");
                estimatedMealFats.setText( String.format("%.1f", totalFats) + " Fats");

                // Stergerea elementului din lista
                foods.remove(position);

                // Actualizare adaptator
                adapter.notifyItemRemoved(position);

                // Stergerea elementului din baza de date
                DatabaseReference deletedFoodRef = FirebaseDatabase.getInstance().getReference("Meals Diary").child(user.getUid()).child(String.valueOf(formattedDate)).child(mealType).child("Foods").child(deletedFoodId);
                deletedFoodRef.removeValue();
            }


        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle result = new Bundle();
                result.putString("mealType", mealType);
                getParentFragmentManager().setFragmentResult("datafromMealFragment", result);

                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_mealFragment2_to_foodFragment);
            }
        });

        return view;
    }


}
