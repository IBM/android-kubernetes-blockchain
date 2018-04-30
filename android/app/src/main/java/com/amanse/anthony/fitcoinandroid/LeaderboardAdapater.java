package com.amanse.anthony.fitcoinandroid;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

public class LeaderboardAdapater extends RecyclerView.Adapter<LeaderboardAdapater.LeaderboardViewHolder> {

    private Context context;
    private ArrayList<UserInfoModel> userInfoModels;
    SparseIntArray stepsAndPlace = new SparseIntArray();

    public LeaderboardAdapater(Context context, ArrayList<UserInfoModel> userInfoModels) {
        this.context = context;
        this.userInfoModels = userInfoModels;
    }

    @Override
    public LeaderboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.leaderboard_item, null);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LeaderboardViewHolder holder, int position) {
        UserInfoModel userInfoModel = userInfoModels.get(position);
        int userSteps = userInfoModel.getSteps();

        // set views
        holder.userImage.setImageBitmap(userInfoModel.getBitmap());

        holder.userName.setText(userInfoModel.getName());
        holder.userStats.setText(String.format("%d steps", userSteps));

        if(stepsAndPlace.get(userSteps) != 0) {
            holder.userPosition.setText(String.valueOf(stepsAndPlace.get(userSteps)));
        } else {
            holder.userPosition.setText(String.valueOf(position+1));
            stepsAndPlace.put(userSteps,position+1);
        }

        setAnimation(holder);
    }

    public void setAnimation(LeaderboardViewHolder holder) {
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return userInfoModels.size();
    }

    public class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userStats, userPosition;
        ImageView userImage;

        public LeaderboardViewHolder(View view) {
            super(view);

            userName = view.findViewById(R.id.userName);
            userStats = view.findViewById(R.id.userStats);
            userPosition = view.findViewById(R.id.userPosition);

            userImage = view.findViewById(R.id.userImage);
        }
    }
}
