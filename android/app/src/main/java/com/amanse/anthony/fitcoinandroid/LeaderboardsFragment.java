package com.amanse.anthony.fitcoinandroid;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardsFragment extends Fragment {

    RequestQueue queue;
    Gson gson;
    String TAG = "FITNESS_LEADERBOARDS";
    String BACKEND_URL = "https://anthony-blockchain.us-south.containers.mybluemix.net";

    ArrayList<UserInfoModel> userInfoModels;

    RecyclerView recyclerView;
    TextView userStats, userPosition, status;
    Toast loadingToast;

    int numberOfUsersInStanding = 10;
    int totalNumberOfUsers = 0;

    LeaderboardAdapater adapter;

    public LeaderboardsFragment() {
        // Required empty public constructor
    }

    public static LeaderboardsFragment newInstance() {
        return new LeaderboardsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_leaderboards, container,false);

        final ImageView userImage = rootView.findViewById(R.id.userImage);
        final TextView userName = rootView.findViewById(R.id.userName);
        userStats = rootView.findViewById(R.id.userStats);
        userPosition = rootView.findViewById(R.id.userPosition);
        status = rootView.findViewById(R.id.status);

        userName.setText("-");
        userStats.setText("-");
        userPosition.setText("-");
        status.setText("-");

        userInfoModels = new ArrayList<>();
        loadingToast = Toast.makeText(rootView.getContext(),"Loading...",Toast.LENGTH_SHORT);


        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());

        recyclerView = rootView.findViewById(R.id.leaderboardsList);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if (totalNumberOfUsers != 0 && numberOfUsersInStanding < totalNumberOfUsers) {
                        loadingToast.show();
                        numberOfUsersInStanding += 10;
                        getLeaderboardTop(numberOfUsersInStanding);
                    }
                }
            }
        });

        adapter = new LeaderboardAdapater(rootView.getContext(), userInfoModels);
        recyclerView.setAdapter(adapter);

        gson = new Gson();
        queue = Volley.newRequestQueue(rootView.getContext());

        // initialize shared preferences - persistent data
        SharedPreferences sharedPreferences = ((AppCompatActivity) getActivity()).getSharedPreferences("shared_preferences_fitcoin", Context.MODE_PRIVATE);

        // get user info from shared prefrences
        if (sharedPreferences.contains("UserInfo")) {
            String userInfoJsonString = sharedPreferences.getString("UserInfo","error");
            if (!userInfoJsonString.equals("error")) {
                UserInfoModel userInfoModel = gson.fromJson(userInfoJsonString,UserInfoModel.class);
                userImage.setImageBitmap(userInfoModel.getBitmap());
                userName.setText(userInfoModel.getName());
            }
        }

        // check for the userId
        if (sharedPreferences.contains("BlockchainUserId")) {
            String userId = sharedPreferences.getString("BlockchainUserId","error");
            if (!userId.equals("error")) {
                getUserFromMongo(userId);
            }
        }

        getLeaderboardTop(numberOfUsersInStanding);

        return rootView;
    }

    public void getUserFromMongo(String userId) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, BACKEND_URL + "/registerees/info/" + userId , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        UserInfoModel userInfoModel = gson.fromJson(response.toString(), UserInfoModel.class);

                        userStats.setText(String.format("%s steps", String.valueOf(userInfoModel.getSteps())));
                        getUserPosition(String.valueOf(userInfoModel.getSteps()));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "That didn't work!");
            }
        });
        queue.add(jsonObjectRequest);
    }

    public void getUserPosition(String steps) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, BACKEND_URL + "/leaderboard/position/steps/" + steps , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            userPosition.setText(String.valueOf(response.getInt("userPosition")));
                            getStatus(response.getInt("userPosition"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "That didn't work!");
            }
        });
        queue.add(jsonObjectRequest);
    }

    public void getLeaderboardTop(int number) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, BACKEND_URL + "/leaderboard/top/" + String.valueOf(number) , null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        UserInfoModel[] dataModels = gson.fromJson(response.toString(), UserInfoModel[].class);
                        userInfoModels.clear();
                        userInfoModels.addAll(Arrays.asList(dataModels));
                        adapter.notifyDataSetChanged();
                        loadingToast.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "That didn't work!");
            }
        });
        queue.add(jsonArrayRequest);
    }

    public void getStatus(final int position) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, BACKEND_URL + "/registerees/totalUsers" , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int totalUsers = response.getInt("count");
                            status.setText(String.format("You are position %d of %d", position, totalUsers));
                            totalNumberOfUsers = totalUsers;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "That didn't work!");
            }
        });
        queue.add(jsonObjectRequest);
    }

}
