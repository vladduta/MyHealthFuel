package com.healthfuel.myhealthfuel.mainpage;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.healthfuel.myhealthfuel.R;

import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.MyViewHolder> {

    Context context;
    ArrayList<Foods> foods;
    String mealtype;


    public FoodAdapter(Context context, ArrayList<Foods> foods) {
        this.context = context;
        this.foods = foods;
    }

    public FoodAdapter(Context context, ArrayList<Foods> foods, String mealtype) {
        this.context = context;
        this.foods = foods;
        this.mealtype = mealtype;
    }

    @NonNull
    @Override
    public FoodAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_item_food, parent, false);
        return new FoodAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdapter.MyViewHolder holder, int position) {
        holder.textViewName.setText(foods.get(position).getProduct_name());
        holder.textViewKcalories.setText(foods.get(position).getKcal()+" kcal");
        holder.textViewQuantity.setText(foods.get(position).getQuantity()+" g");
        holder.textViewMacros.setText("Carbs: " + String.format("%.1f g", foods.get(position).getCarbohydrates()) + "    Fat: " + String.format("%.1f g", foods.get(position).getFat()) + "    Protein: " + String.format("%.1f g", foods.get(position).getProteins()));
        Picasso.get().load(foods.get(position).getImage_url()).into(holder.imageView); //  incarc imaginea de la URL si o afisez in imageView folosind Picasso
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //un fel de onCreate

        ImageView imageView;

        TextView textViewMacros, textViewKcalories, textViewName, textViewQuantity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.recImage);
            textViewName = itemView.findViewById(R.id.textViewTitle);
            textViewKcalories = itemView.findViewById(R.id.textViewKcalories);
            textViewMacros = itemView.findViewById(R.id.textViewMacros);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String image_url = foods.get(position).getImage_url();
                String id = foods.get(position).getId();
                Double carbohydrates = foods.get(position).getCarbohydrates();
                Integer kcal = foods.get(position).getKcal();
                String foodName = foods.get(position).getProduct_name();
                Double fat = foods.get(position).getFat();
                Double proteins = foods.get(position).getProteins();
                Double fiber = foods.get(position).getFiber();
                Double sugar = foods.get(position).getSugar();
                Double salt = foods.get(position).getSalt();
                Integer quantity = foods.get(position).getQuantity();

                if (kcal == null) {
                    kcal = 0;
                }
                if (fat == null) {
                    fat = 0.0;
                }
                if (carbohydrates == null) {
                    carbohydrates = 0.0;
                }if (proteins == null) {
                    proteins = 0.0;
                }
                if (sugar == null) {
                    sugar = 0.0;
                }if (salt == null) {
                    salt = 0.0;
                }
                if (fiber == null) {
                    fiber = 0.0;
                }
                    Bundle bundle = new Bundle();
                    bundle.putString("image_url", image_url);
                    bundle.putString("id", id);
                    bundle.putDouble("carbohydrates", carbohydrates);
                    bundle.putInt("kcal", kcal);
                    bundle.putString("foodName", foodName);
                    bundle.putDouble("fat", fat);
                    bundle.putDouble("proteins", proteins);
                    bundle.putDouble("fiber", fiber);
                    bundle.putDouble("salt", salt);
                    bundle.putDouble("sugar", sugar);
                    bundle.putString("mealType", mealtype);
                    bundle.putInt("quantity", quantity);


                NavController navController = Navigation.findNavController(v);
                // Verific daca ne aflam in FoodFragment
                if(navController.getCurrentDestination().getId() == R.id.foodFragment) {

                    navController.navigate(R.id.action_foodFragment_to_foodFragmentClick, bundle);
                }else{
                    navController.navigate(R.id.action_mealFragment2_to_foodFragmentClick, bundle);

                }
            }
        }
    }
}


