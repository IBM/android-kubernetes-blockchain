package com.amanse.anthony.fitcoinandroid;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopFragment extends Fragment {

    ArrayList<ShopItemModel> shopDataModels;
    ArrayList<ContractModel> contractDataModels;
    RecyclerView recyclerView;
    ShopItemsAdapter adapter;
    FloatingActionButton contractButton;

    TextView fitcoinsBalance, pendingCharges;

    private static final String TAG = "FITNESS_SHOP_FRAG";
    private static final String BACKEND_URL = "https://anthony-blockchain.us-south.containers.mybluemix.net";
    public String userId;
    public boolean isEnrolled;

    public RequestQueue queue;

    Gson gson = new Gson();


    public ShopFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_shop, container, false);
//        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        actionBar.show();
//        actionBar.setTitle("Shop");

        SharedPreferences sharedPreferences = ((AppCompatActivity) getActivity()).getSharedPreferences("shared_preferences_fitcoin", Context.MODE_PRIVATE);

        // check if enrolled in blockchain network
        if (sharedPreferences.contains("BlockchainUserId")) {
            this.userId = sharedPreferences.getString("BlockchainUserId","Something went wrong...");
            if (this.userId.equals("Something went wrong...")) {
                this.isEnrolled = false;
            } else {
                this.isEnrolled = true;
            }
        } else {
            this.isEnrolled = false;
        }

        // request queue
        queue = Volley.newRequestQueue((AppCompatActivity) getActivity());

        recyclerView = rootView.findViewById(R.id.productList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        fitcoinsBalance = rootView.findViewById(R.id.fitcoinsBalance);
        pendingCharges = rootView.findViewById(R.id.pendingCharges);

        shopDataModels = new ArrayList<>();

        adapter = new ShopItemsAdapter(rootView.getContext(), shopDataModels);
        recyclerView.setAdapter(adapter);

        // initialize contract data model
        contractDataModels = new ArrayList<>();

        // contracts floating button
        contractButton = rootView.findViewById(R.id.contractButton);

        contractButton.setVisibility(View.INVISIBLE);
        contractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(rootView.getContext(), ContractList.class);

                Log.d(TAG, contractDataModels.size() + "");

                // put contract models to next activity
                intent.putExtra("CONTRACT_MODELS_JSON",new Gson().toJson(contractDataModels.toArray(new ContractModel[contractDataModels.size()]),ContractModel[].class));
                rootView.getContext().startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.isEnrolled) {
            getStateOfUser(this.userId);
            getAllUserContracts(this.userId);
            getProductsForSale(this.userId);
        }
    }

    public static ShopFragment newInstance() {
        return new ShopFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void getAllUserContracts(String userId) {
        getAllUserContracts(userId, 0);
    }

    public void getAllUserContracts(String userId, final int failedAttempts) {
        try {
            JSONObject params = new JSONObject("{\"type\":\"query\",\"queue\":\"user_queue\",\"params\":{\"userId\":\"" + userId + "\", \"fcn\":\"getAllUserContracts\", \"args\":[" + userId + "]}}");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BACKEND_URL + "/api/execute", params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            InitialResultFromRabbit initialResultFromRabbit = gson.fromJson(response.toString(), InitialResultFromRabbit.class);
                            if (initialResultFromRabbit.status.equals("success")) {
                                Log.d(TAG, "Response is: -- " + response.toString());
                                getResultFromResultId("getAllUserContracts",initialResultFromRabbit.resultId,0, failedAttempts);
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

    public void getProductsForSale(String userId) {
        getProductsForSale(userId, 0);
    }

    public void getProductsForSale(String userId, final int failedAttempts) {
        try {
            JSONObject params = new JSONObject("{\"type\":\"query\",\"queue\":\"user_queue\",\"params\":{\"userId\":\"" + userId + "\", \"fcn\":\"getProductsForSale\", \"args\":[]}}");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BACKEND_URL + "/api/execute", params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            InitialResultFromRabbit initialResultFromRabbit = gson.fromJson(response.toString(), InitialResultFromRabbit.class);
                            if (initialResultFromRabbit.status.equals("success")) {
                                getResultFromResultId("getProductsForSale",initialResultFromRabbit.resultId,0, failedAttempts);
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

                // GET STATE OF USER
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

                                        // insert ui views here
                                        Log.d(TAG, gson.toJson(getStateFinalResult));
                                        fitcoinsBalance.setText(String.valueOf(getStateFinalResult.fitcoinsBalance));

                                    } else {
                                        // if blockchain fails to process for some reason
                                        if (failedAttempts < 10) {
                                            getStateOfUser(userId,failedAttempts + 1);
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
            } else if (initialRequestType.equals("getProductsForSale")) {

                // GET PRODUCTS FOR SALE
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
                                        ShopItemModel[] shopItemModels = gson.fromJson(resultOfBackendResult.result, ShopItemModel[].class);

                                        // insert ui views here
                                        Log.d(TAG, gson.toJson(shopItemModels));
                                        shopDataModels.clear();
                                        shopDataModels.addAll(Arrays.asList(shopItemModels));
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        // if blockchain fails to process for some reason
                                        if (failedAttempts < 10) {
                                            getStateOfUser(userId,failedAttempts + 1);
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
            } else if (initialRequestType.equals("getAllUserContracts")) {

                // GET ALL USER CONTRACTS
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
                                    Log.d(TAG, "get all user contracts backend result" + backendResult.result);
                                    ResultOfBackendResult resultOfBackendResult = gson.fromJson(backendResult.result, ResultOfBackendResult.class);

                                    if (resultOfBackendResult.message.equals("success")) {
                                        // Once successful, update UI
                                        ContractModel[] contractModels = gson.fromJson(resultOfBackendResult.result, ContractModel[].class);

                                        // clear the contracts list
                                        contractDataModels.clear();

                                        // ContractModel for pending contracts
                                        ArrayList<ContractModel> pendingContracts = new ArrayList<>();
                                        int pendingCoins = 0;

                                        if (contractModels != null) {
                                            for (ContractModel contract : contractModels) {
                                                if (contract.state.equals("pending")) {
                                                    pendingContracts.add(contract);
                                                    pendingCoins += contract.cost;
                                                }
                                            }
                                            contractDataModels.addAll(Arrays.asList(contractModels));
                                            pendingCharges.setTag(contractModels);
                                        }

                                        // insert ui views here
                                        pendingCharges.setText(String.valueOf(0 - pendingCoins));

                                        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
                                        animation.setDuration(500);
                                        contractButton.setVisibility(View.VISIBLE);
                                        contractButton.setAnimation(animation);

                                    } else {
                                        // if blockchain fails to process for some reason
                                        if (failedAttempts < 10) {
                                            getStateOfUser(userId,failedAttempts + 1);
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
}
