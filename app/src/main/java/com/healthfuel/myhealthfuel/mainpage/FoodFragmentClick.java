package com.healthfuel.myhealthfuel.mainpage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.healthfuel.myhealthfuel.R;

import org.checkerframework.common.value.qual.StringVal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FoodFragmentClick extends Fragment {
    String formattedDate;
    int currentDay;
    FirebaseAuth auth;
    FirebaseUser user;
    String foodName, id, image_url;
    Double carbohydrates, fat, proteins, fiber, sugar, salt;
    Integer kcal;
    Integer quantity = 100;
    ImageButton backButton;
    Button addFood;
    PieChart pieChart;
    Spinner spinnerMeal;
    ArrayList<String> mealTitles = new ArrayList<>(Arrays.asList("Breakfast", "Lunch", "Dinner"));
    String mealType;
    int position;
    String foodId;
    int serving=100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_click, container, false);

        //obtinerea zilei si a datei curente
        Calendar calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.set(Calendar.DAY_OF_YEAR, currentDay);
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        formattedDate = dateFormat.format(date);


        getParentFragmentManager().setFragmentResultListener("datafromMealFragment", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                // Actualizare valoare mealType cu valoarea primita
                mealType = result.getString("mealType");
                if (mealType != null) {
                    // Setare selectie in spinner conform valorii mealType
                    position = mealTitles.indexOf(mealType);
                    spinnerMeal.setSelection(position);
                }
            }
        });




        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        backButton = view.findViewById(R.id.backButton);
        pieChart = view.findViewById(R.id.pieChart);
        spinnerMeal = view.findViewById(R.id.spinnerMeal);
        addFood = view.findViewById(R.id.addFoodButton);

        TextView textViewCalories = view.findViewById(R.id.textViewCalories);
        TextView textViewCarbo = view.findViewById(R.id.textViewCarbo);
        TextView textViewProteins = view.findViewById(R.id.textViewProteins);
        TextView textViewFat = view.findViewById(R.id.textViewFat);
        TextView textViewFiber = view.findViewById(R.id.textViewFiber);
        TextView textViewSugar = view.findViewById(R.id.textViewSugar);
        TextView textViewSalt = view.findViewById(R.id.textViewSalt);
        TextView title = view.findViewById(R.id.textViewFoodTitle);
        TextView textViewNutri = view.findViewById(R.id.textViewNutri);
        EditText editTextServingSize = view.findViewById(R.id.editTextServingSize);

        //Extragere mealtype din SharedPreferences
        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        mealType = sharedPref.getString("mealType", mealType);

        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getString("id");
            image_url = bundle.getString("image_url");
            carbohydrates = bundle.getDouble("carbohydrates");
            kcal = bundle.getInt("kcal");
            foodName = bundle.getString("foodName");
            fat = bundle.getDouble("fat");
            proteins = bundle.getDouble("proteins");
            fiber = bundle.getDouble("fiber");
            sugar = bundle.getDouble("sugar");
            salt = bundle.getDouble("salt");
            quantity = bundle.getInt("quantity");
            if(bundle.getString("mealType") != null) {
                mealType = bundle.getString("mealType");
            }
        }


        editTextServingSize.setText(quantity.toString());
        textViewNutri.setText("Nutritional information for " + quantity + "g");

        // Configurare spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mealTitles);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerMeal.setAdapter(adapter);

        if(mealType!= null) {
            spinnerMeal.setSelection(mealTitles.indexOf(mealType));
        }

        // Ascultator pentru spinner
        spinnerMeal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mealType = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //modific caloriile in functie de gramajul produsului
        editTextServingSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Inainte de modificare
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // In timpul modificarii (nu este necesar pentru aceasta implementare)
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Dupa ce s-a incheiat modificarea

                // Verific daca campul EditText nu este gol
                if (!s.toString().isEmpty()) {
                    // Obtin valoarea gramajului introdus de utilizator
                    serving = Integer.parseInt(s.toString());

                    //modific caloriile si nutrientii in functie de gramajul produsului
                    kcal = bundle.getInt("kcal") * serving / quantity;
                    carbohydrates = bundle.getDouble("carbohydrates") * serving / quantity;
                    fat = bundle.getDouble("fat") * serving / quantity;
                    proteins = bundle.getDouble("proteins") * serving / quantity;
                    fiber = bundle.getDouble("fiber") * serving / quantity;
                    sugar = bundle.getDouble("sugar") * serving / quantity;
                    salt = bundle.getDouble("salt") * serving / quantity;
                    //quantity = serving;

                    // Actualizare text pentru numarul de calorii afisat in centrul pie chartului
                    pieChart.setCenterText(kcal + "\ncal");
                    pieChart.invalidate(); // Force redraw of the chart
                    textViewCalories.setText(String.valueOf(kcal));

                    textViewCarbo.setText(String.valueOf(carbohydrates));
                    textViewProteins.setText(String.valueOf(proteins));
                    textViewFat.setText(String.valueOf(fat));
                    textViewFiber.setText(String.valueOf(fiber));
                    textViewSugar.setText(String.valueOf(sugar));
                    textViewSalt.setText(String.valueOf(salt));
                    textViewNutri.setText("Nutritional information for " + serving + "g");

                }

            }
        });


        textViewCalories.setText(String.valueOf(kcal));
        textViewCarbo.setText(String.valueOf(carbohydrates));
        textViewProteins.setText(String.valueOf(proteins));
        textViewFat.setText(String.valueOf(fat));
        textViewFiber.setText(String.valueOf(fiber));
        textViewSugar.setText(String.valueOf(sugar));
        textViewSalt.setText(String.valueOf(salt));
        title.setText(foodName);

        if (carbohydrates != null && kcal != null && fat != null && proteins != null) {
            ArrayList<PieEntry> data = new ArrayList<>();

            // Calculam totalul caloric pentru carbohidrati, proteine si grasimi
            double carbCalories = carbohydrates * 4;
            double proteinCalories = proteins * 4;
            double fatCalories = fat * 9;

            // Calculam suma totala a caloriilor
            double totalCalories = carbCalories + proteinCalories + fatCalories;

            // Calculam procentajul din totalul caloric pentru carbohidrati, proteine si grasimi
            double carbPercentage = (carbCalories / totalCalories) * 100;
            double proteinPercentage = (proteinCalories / totalCalories) * 100;
            double fatPercentage = (fatCalories / totalCalories) * 100;

            // Adaugam datele si procentele corespunzatoare in diagrama
            data.add(new PieEntry((float) carbPercentage, "Carbs: " + String.format("%.1f", carbPercentage) + "%"));
            data.add(new PieEntry((float) proteinPercentage, "Proteins: " + String.format("%.1f", proteinPercentage) + "%"));
            data.add(new PieEntry((float) fatPercentage, "Fat: " + String.format("%.1f", fatPercentage) + "%"));

            PieDataSet pieDataSet = new PieDataSet(data, "");
            pieDataSet.setColors(getResources().getColor(R.color.green), getResources().getColor(R.color.salmon), getResources().getColor(R.color.yellow));
            pieDataSet.setDrawValues(false); // Eliminam etichetele de la felii

            PieData pieData = new PieData(pieDataSet);
            pieChart.setData(pieData);
            pieChart.setHoleRadius(80);
            pieChart.setCenterText(kcal + "\ncal");
            pieChart.setCenterTextSize(20);
            pieChart.setDrawEntryLabels(false);
            pieChart.setTouchEnabled(false);
            pieChart.getDescription().setEnabled(false);


            Legend legend = pieChart.getLegend();
            // Setam alinierea legendei la dreapta
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
            legend.setOrientation(Legend.LegendOrientation.VERTICAL);
            legend.setDrawInside(false);
            legend.setTextSize(15);
            legend.setXEntrySpace(7f);
            legend.setYEntrySpace(0f);
            legend.setYOffset(0f);

        }


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //salvare mealtype-ul in SharedPreferences
                SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("mealType", mealType);
                editor.apply();

                // Revenire la ecranul anterior
                requireActivity().onBackPressed();
                adapter.notifyDataSetChanged();

            }
        });


        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference foodsRef = FirebaseDatabase.getInstance().getReference("Meals Diary").child(user.getUid()).child(String.valueOf(formattedDate)).child(mealType).child("Foods");
                Foods food = new Foods(foodName, image_url, id, carbohydrates, kcal, fat, proteins, fiber, sugar, salt, serving);
                foodId = encryptedValue(id, 10); //generez un id unic pt fiecare aliment (barcode-ul criptat)
                foodsRef.child(foodId).setValue(food);

                Bundle bundle = new Bundle();
                bundle.putString("mealType", mealType);

                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView2);
                navController.navigate(R.id.action_foodFragmentClick_to_mealFragment2, bundle);
            }
        });
        return view;
    }
    //criptez id-ul alimentelor
    private String encryptedValue(String value, int key) {
        String myId = "";

        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            ch += key;
            myId += ch;
        }
        return myId;
    }


}