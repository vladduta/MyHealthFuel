package com.healthfuel.myhealthfuel.mainpage;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.healthfuel.myhealthfuel.R;

import java.util.ArrayList;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.MyViewHolder> {

    Context context; //pt inflate the layout
    ArrayList<Exercises> exercises;//contine toate tipurile de exercitii
    boolean cardioExercise = false;

    public void setFilteredList(ArrayList<Exercises> filteredList){
        this.exercises=filteredList;
        notifyDataSetChanged();
    }
    public void clearFilteredList() {
        this.exercises.clear();
        notifyDataSetChanged(); // notifica adaptorul ca datele au fost modificate
    }

    public ExerciseAdapter(Context context, ArrayList<Exercises> exercises) {
        this.context = context;
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ExerciseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_item_exercise, parent, false);
        return new ExerciseAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseAdapter.MyViewHolder holder, int position) {
        //schimba datele bazat pe pozitia recyclerview-ul nostru pt fiecare item in parte
        holder.textViewName.setText(exercises.get(position).getName());
        holder.textViewType.setText(exercises.get(position).getType());
        holder.imageView.setImageResource(exercises.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        //nr de items care vor fi afisate
        return exercises.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //un fel de onCreate
        ImageView imageView;
        TextView textViewName, textViewType;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.recImage);
            textViewName = itemView.findViewById(R.id.textViewTitle);
            textViewType = itemView.findViewById(R.id.textViewDescription);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();


            if (position != RecyclerView.NO_POSITION) {
                int gifImageId = exercises.get(position).getGif(); // obtinerea ID-ului imaginii GIF pentru elementul selectat
                String exerciseName = exercises.get(position).getName();
                String exerciseType = exercises.get(position).getType();

                if (exerciseType.equals("Cardio")) {
                    cardioExercise = true;
                } else {
                    cardioExercise = false;
                }

                Bundle bundle = new Bundle();
                bundle.putInt("gifImageId", gifImageId);
                bundle.putBoolean("cardioExercise", cardioExercise);
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_exerciseFragment_to_exerciseFragment2, bundle);

            }
        }



    }
}


