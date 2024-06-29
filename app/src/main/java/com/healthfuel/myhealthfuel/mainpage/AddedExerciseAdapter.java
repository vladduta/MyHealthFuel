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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.healthfuel.myhealthfuel.R;

import java.util.ArrayList;


public class AddedExerciseAdapter extends RecyclerView.Adapter<AddedExerciseAdapter.MyViewHolder2> {

    Context context;
    ArrayList<String> titles, descriptions;
    ArrayList<Integer> images;
    ArrayList<String> exerciseIds; //contine toate tipurile de exercitii

    String workoutTitle;
    boolean cardioExercise = false;
    public AddedExerciseAdapter(Context context, ArrayList<String> titles, ArrayList<String> descriptions, ArrayList<Integer> images, ArrayList<String> exerciseIds, String workoutTitle) {
        this.context = context;
        this.titles = titles;
        this.descriptions = descriptions;
        this.images = images;
        this.exerciseIds = exerciseIds;
        this.workoutTitle = workoutTitle;


    }

    @NonNull
    @Override
    public MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_item_exercise, parent, false);
        return new MyViewHolder2(view);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder2 holder, int position) {
        holder.title.setText(titles.get(position));
        holder.description.setText(descriptions.get(position));
        holder.image.setImageResource(images.get(position));


    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class MyViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView description;
        ImageView image;

        public MyViewHolder2(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewTitle);
            description = itemView.findViewById(R.id.textViewDescription);
            image = itemView.findViewById(R.id.recImage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                //obtinerea ID-ului imaginii GIF pt elementul selectat din lista de exercitii
                String exerciseId = exerciseIds.get(position);
                DatabaseReference exerciseRef = FirebaseDatabase.getInstance().getReference("Exercises").child(exerciseId);
                exerciseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            String type = dataSnapshot.child("type").getValue(String.class);

                            if(type.equals("Cardio")){
                                cardioExercise = true;
                            }

                            int gifImageId = dataSnapshot.child("gif").getValue(Integer.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("gifImageId", gifImageId);
                            bundle.putString("selectedWorkout2",workoutTitle);
                            bundle.putBoolean("cardioExercise",cardioExercise);
                            NavController navController = Navigation.findNavController(v);
                            navController.navigate(R.id.action_workoutFragment_to_exerciseFragment2, bundle);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }
}
