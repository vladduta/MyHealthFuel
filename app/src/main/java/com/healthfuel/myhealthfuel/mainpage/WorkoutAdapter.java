package com.healthfuel.myhealthfuel.mainpage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.healthfuel.myhealthfuel.R;

import java.util.ArrayList;


public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.MyViewHolder2> {

    Context context;
    ArrayList<String> titles;
    ArrayList<String> days;
    ArrayList<String> workoutDurations;
    ArrayList<String> workoutCalories;
    FirebaseAuth auth;
    FirebaseUser user;

    public WorkoutAdapter(Context context, ArrayList<String> titles, ArrayList<String> days, ArrayList<String> workoutDurations, ArrayList<String> workoutCalories) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        this.context = context;
        this.titles = titles;
        this.days = days;
        this.workoutDurations = workoutDurations;
        this.workoutCalories = workoutCalories;
    }


    @NonNull
    @Override
    public MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_item_workout, parent, false);
        return new MyViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder2 holder, int position) {
        holder.title.setText(titles.get(position));
        holder.day.setText(days.get(position));
        holder.workoutDuration.setText(workoutDurations.get(position));
        holder.workoutCalorie.setText(workoutCalories.get(position));
    }


    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class MyViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView day;
        TextView workoutDuration;
        TextView workoutCalorie;
        ImageButton deleteButton;

        public MyViewHolder2(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.workoutTitle);
            day = itemView.findViewById(R.id.workoutDay);
            workoutDuration = itemView.findViewById(R.id.estimatedWorkoutDuration);
            workoutCalorie = itemView.findViewById(R.id.estimatedWorkoutCalories);
            itemView.setOnClickListener(this);

            deleteButton = itemView.findViewById(R.id.deleteButton);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String workoutTitle = titles.get(position);
                        titles.remove(position);
                        days.remove(position);
                        workoutDurations.remove(position);
                        workoutCalories.remove(position);

                        notifyItemRemoved(position);


                        DatabaseReference exerciseRef = FirebaseDatabase.getInstance().getReference("Personal workouts")
                                .child(user.getUid()).child(String.valueOf(workoutTitle));

                        exerciseRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Exercise deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed to delete exercise from database.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }




        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String selectedWorkoutTitle = titles.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("title", selectedWorkoutTitle);


                NavController navController = Navigation.findNavController(v);
                NavOptions navOptions = new NavOptions.Builder().setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_right).setPopExitAnim(R.anim.slide_out_left).build();
                navController.navigate(R.id.action_homeFragment_to_workoutFragment, bundle,navOptions);

            }
        }
    }
}

