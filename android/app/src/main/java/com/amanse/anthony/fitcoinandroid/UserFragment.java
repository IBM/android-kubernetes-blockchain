package com.amanse.anthony.fitcoinandroid;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "FITNESS_API_USER_FRAG";
    private static final String BACKEND_URL = "https://anthony-blockchain.us-south.containers.mybluemix.net";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;
    private static final int FITCOINS_STEPS_CONVERSION = 100;

    private static OnDataPointListener stepListener;
    private static OnDataPointListener distanceListener;

    public TextView userSteps;
    public TextView distanceFromSteps;
    public TextView userId;
    public TextView coinsBalance;
    public SwipeRefreshLayout swipeRefreshLayout;

    private long userStartingDate;
    private float distanceInMeters;

    public RequestQueue queue;

    public Integer totalStepsConvertedToFitcoin;

    public boolean sendingInProgress;

    public String userIdFromStorage;

    public Runnable refreshRunnable;
    public Handler handler = new Handler();

    Gson gson = new Gson();

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);

//        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        actionBar.show();
//        actionBar.setTitle("Footsteps");

        // attach labels
        userSteps = rootView.findViewById(R.id.numberOfSteps);
        distanceFromSteps = rootView.findViewById(R.id.distance);
        userId = rootView.findViewById(R.id.userIdText);
        coinsBalance = rootView.findViewById(R.id.numberOfCoins);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        // request queue
        queue = Volley.newRequestQueue((AppCompatActivity) getActivity());

        // initialize shared preferences - persistent data
        SharedPreferences sharedPreferences = ((AppCompatActivity) getActivity()).getSharedPreferences("shared_preferences_fitcoin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // check if enrolled in blockchain network
        if (sharedPreferences.contains("BlockchainUserId")) {
            userIdFromStorage = sharedPreferences.getString("BlockchainUserId","Something went wrong...");
            userId.setText(userIdFromStorage);
            if (!userIdFromStorage.equals("Something went wrong...")) {
                getStateOfUser(userIdFromStorage);
            }
        } else {
            userId.setText(R.string.notEnrolled);
        }

        // Get the date now
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        // if DateStarted is existing, use it
        if (sharedPreferences.contains("DateStarted")) {
            userStartingDate = sharedPreferences.getLong("DateStarted", cal.getTimeInMillis());
        } else { // else use time and date now and persist it for later use
            userStartingDate = cal.getTimeInMillis();
            editor.putLong("DateStarted",userStartingDate);
            editor.apply();
        }

        // build the fitness options
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .build();

        // check if app has permissions
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount((AppCompatActivity) getActivity()), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount((AppCompatActivity) getActivity()),
                    fitnessOptions);
        } else {
            accessGoogleFit();
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                Log.d(TAG, "accessing...");
                accessGoogleFit();
            }
        }
    }

    private void accessGoogleFit() {
        // Subscribe to recordings
        Fitness.getRecordingClient((AppCompatActivity) getActivity(), GoogleSignIn.getLastSignedInAccount((AppCompatActivity) getActivity()))
                .subscribe(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully subscribed");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "There was a problem subscribing...", e);
                    }
                });

        Fitness.getRecordingClient((AppCompatActivity) getActivity(), GoogleSignIn.getLastSignedInAccount((AppCompatActivity) getActivity()))
                .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully subscribed");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "There was a problem subscribing...", e);
                    }
                });
        Fitness.getRecordingClient((AppCompatActivity) getActivity(), GoogleSignIn.getLastSignedInAccount((AppCompatActivity) getActivity()))
                .subscribe(DataType.TYPE_DISTANCE_DELTA)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully subscribed");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "There was a problem subscribing...", e);
                    }
                });
        // end of subscriptions

        // prepare to get history
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        long startTime = userStartingDate;

        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        Log.d(TAG,"Range Start: " + dateFormat.format(startTime));
        Log.d(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(365, TimeUnit.DAYS)
                .build();

        // get history
        Fitness.getHistoryClient((AppCompatActivity) getActivity(), GoogleSignIn.getLastSignedInAccount((AppCompatActivity) getActivity()))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        Log.d(TAG, "successfully got history");
                        getDataSetsFromBucket(dataReadResponse);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "failed to get history", e);
                    }
                });
    }

    public void getDataSetsFromBucket(DataReadResponse dataReadResult) {

        // number of buckets would always be 1 (bucket size was set to 365 days in readRequest)
        if (dataReadResult.getBuckets().size() > 0) {
            Log.d(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    parseDataSet(dataSet);
                }
            }
        }
    }


    // Logic for reading the data sets.
    private void parseDataSet(DataSet dataSet) {
        Log.d(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getTimeInstance();

        int totalStepsFromDataPoints = 0;
        float distanceTraveledFromDataPoints = 0;

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.d(TAG, "Data point:");
            Log.d(TAG, "\tType: " + dp.getDataType().getName());
            Log.d(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.d(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));

            for (Field field : dp.getDataType().getFields()) {
                Log.d(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));

                // increment the steps or distance
                if (field.getName().equals("steps")) {
                    totalStepsFromDataPoints += dp.getValue(field).asInt();
                } else if (field.getName().equals("distance")) {
                    distanceTraveledFromDataPoints += dp.getValue(field).asFloat();
                }
            }
        }

        // update the proper labels
        if (dataSet.getDataType().getName().equals("com.google.step_count.delta")) {
            userSteps.setText(String.valueOf(totalStepsFromDataPoints));

            if (totalStepsConvertedToFitcoin != null && !sendingInProgress) {
                sendingInProgress = true;

                if (totalStepsFromDataPoints - totalStepsConvertedToFitcoin > FITCOINS_STEPS_CONVERSION) {
                    sendStepsToFitchain(userIdFromStorage,totalStepsFromDataPoints);
                }
            }
        } else if (dataSet.getDataType().getName().equals("com.google.distance.delta")) {
            distanceFromSteps.setText(String.format("%.2f", distanceTraveledFromDataPoints/1000.00));
            distanceInMeters = distanceTraveledFromDataPoints;
        }
    }

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Resumed app");

        // initialize the step listener
        stepListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Log.d(TAG, "Field: " + field.getName());
                    Log.d(TAG, "Value: " + dataPoint.getValue(field));

                    if (field.getName().equals("steps")) {
                        int currentSteps = Integer.valueOf(userSteps.getText().toString());
                        currentSteps = currentSteps + dataPoint.getValue(field).asInt();
                        userSteps.setText(Integer.toString(currentSteps));

                        // if nothing in sending in totalStepsConvertedToFitcoin
                        if (totalStepsConvertedToFitcoin != null && !sendingInProgress) {
                            if (currentSteps - totalStepsConvertedToFitcoin > FITCOINS_STEPS_CONVERSION) {
                                sendingInProgress = true;

                                // send steps to blockchain
                                sendStepsToFitchain(userIdFromStorage,currentSteps);

                                // insert send steps to mongo

                                // insert logic for leaderboards
                            }
                        }
                    }
                }
            }
        };

        // register the step listener
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount((AppCompatActivity) getActivity()))) {
            Log.d(TAG, "Not signed in...");
        } else {
            Fitness.getSensorsClient((AppCompatActivity) getActivity(), GoogleSignIn.getLastSignedInAccount((AppCompatActivity) getActivity()))
                    .add(
                            new SensorRequest.Builder()
                                    .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                                    .setSamplingRate(10, TimeUnit.SECONDS)
                                    .build(), stepListener
                    )
                    .addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Step Listener Registered.");
                                    } else {
                                        Log.e(TAG, "Step Listener not registered", task.getException());
                                    }
                                }
                            }
                    );
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // remove callbacks from refreshing
        handler.removeCallbacks(refreshRunnable);

        // set refreshing back to false if it was refreshing
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        // unregister the listener
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount((AppCompatActivity) getActivity()))) {
            Log.d(TAG, "Not signed in...");
        } else {
            Fitness.getSensorsClient((AppCompatActivity) getActivity(), GoogleSignIn.getLastSignedInAccount((AppCompatActivity) getActivity()))
                    .remove(stepListener)
                    .addOnCompleteListener(
                            new OnCompleteListener<Boolean>() {
                                @Override
                                public void onComplete(@NonNull Task<Boolean> task) {
                                    if (task.isSuccessful() && task.getResult()) {
                                        Log.d(TAG, "Step Listener for steps was removed.");
                                    } else {
                                        Log.e(TAG, "Step Listener for steps was not removed.", task.getException());
                                    }
                                }
                            }
                    );
        }
    }

    public  void getStateOfUser(String userId) {
        getStateOfUser(userId, 0);
    }

    public void getStateOfUser(String userId, final int failedAttempts) {
        try {
            JSONObject params = new JSONObject("{\"type\":\"query\",\"queue\":\"user_queue\",\"params\":{\"userId\":\"" + userId + "\", \"fcn\":\"getState\", \"args\":[" + userId + "]}}");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BACKEND_URL + "/api/execute", params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            InitialResultFromRabbit initialResultFromRabbit = gson.fromJson(response.toString(), InitialResultFromRabbit.class);
                            if (initialResultFromRabbit.status.equals("success")) {
                                getResultFromResultId("getStateOfUser",initialResultFromRabbit.resultId,0, failedAttempts);
                            } else {
                                Log.d(TAG, "Response is: " + response.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "That didn't work!");
                }
            });
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getResultFromResultId(final String initialRequestType, final String resultId, final int attemptNumber) {
        // initial failed attempt is 0
        getResultFromResultId(initialRequestType,resultId,attemptNumber,0);
    }

    public void getResultFromResultId(final String initialRequestType, final String resultId, final int attemptNumber, final int failedAttempts) {
        Log.d(TAG, "Attempt number: " + attemptNumber);
        // Limit to 60 attempts
        if (attemptNumber < 60) {
            if (initialRequestType.equals("getStateOfUser")) {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, BACKEND_URL + "/api/results/" + resultId, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                BackendResult backendResult = gson.fromJson(response.toString(), BackendResult.class);

                                // Check status of queued request
                                if (backendResult.status.equals("pending")) {
                                    // if it is still pending, check again
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            getResultFromResultId(initialRequestType,resultId,attemptNumber + 1);
                                        }
                                    },500);
                                } else if (backendResult.status.equals("done")) {
                                    // when blockchain is done processing the request, read the message & result
                                    Log.d(TAG, backendResult.result);
                                    ResultOfBackendResult resultOfBackendResult = gson.fromJson(backendResult.result, ResultOfBackendResult.class);

                                    if (resultOfBackendResult.message.equals("success")) {
                                        // Once successful, update UI
                                        GetStateFinalResult getStateFinalResult = gson.fromJson(resultOfBackendResult.result, GetStateFinalResult.class);
                                        totalStepsConvertedToFitcoin = getStateFinalResult.stepsUsedForConversion;
                                        coinsBalance.setText(String.valueOf(getStateFinalResult.fitcoinsBalance));

                                        // if it was pulled down to refresh
                                        if (swipeRefreshLayout.isRefreshing()) {
                                            swipeRefreshLayout.setRefreshing(false);
                                        }
                                    } else {
                                        // if blockchain fails to process for some reason
                                        if (failedAttempts < 10) {
                                            getStateOfUser(userIdFromStorage,failedAttempts + 1);
                                        } else {
                                            Log.d(TAG,"10 failed attempts reached -- getStateOfUser");
                                            Log.d(TAG, resultOfBackendResult.error);
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Response is: " + response.toString());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "That didn't work!");
                            }
                        });
                this.queue.add(jsonObjectRequest);
            }
        } else {
            Log.d(TAG, "No results after 180 times...");
        }
    }

    public void sendStepsToFitchain(final String userId, final int numberOfStepsToSend) {
        try {
            JSONObject params = new JSONObject("{\"type\":\"invoke\",\"queue\":\"user_queue\",\"params\":{\"userId\":\"" + userId + "\",\"fcn\":\"generateFitcoins\",\"args\":[" + userId + ",\"" + String.valueOf(numberOfStepsToSend)+ "\"]}}");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BACKEND_URL + "/api/execute", params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            InitialResultFromRabbit initialResultFromRabbit = gson.fromJson(response.toString(),InitialResultFromRabbit.class);
                            Log.d(TAG,"Response is: --- " + response.toString());
                            if (initialResultFromRabbit.status.equals("success")) {
                                // toggle sendingInProgress to false after 3 seconds
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendingInProgress = false;
                                        getStateOfUser(userIdFromStorage);
                                    }
                                },3000);

                                int stepsUsedForConversion = numberOfStepsToSend - (numberOfStepsToSend % 100);
                                totalStepsConvertedToFitcoin = stepsUsedForConversion;

                                // update mongodb for leaderboard
                                sendStepsToMongo(userId,numberOfStepsToSend);
                            } else {
                                Log.d(TAG, "Response is: " + response.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "That didn't work!");
                }
            });
            this.queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendStepsToMongo(String userId, int numberOfStepsToSend) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BACKEND_URL + "/registerees/update/" + userId + "/steps/" + String.valueOf(numberOfStepsToSend),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        Log.d(TAG, "Steps updated in mongo");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "That didn't work!");
            }
        });
        this.queue.add(stringRequest);
    }

    // this handles the pull down to refresh
    @Override
    public void onRefresh() {
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                getStateOfUser(userIdFromStorage);

                // refresh google fit history
                if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount((AppCompatActivity) getActivity()))) {
                    Log.d(TAG, "Not signed in...");
                } else {
                    accessGoogleFit();
                }
            }
        };

        handler.postDelayed(refreshRunnable,5000);
    }
}
