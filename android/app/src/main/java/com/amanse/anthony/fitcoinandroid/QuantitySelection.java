package com.amanse.anthony.fitcoinandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class QuantitySelection extends AppCompatActivity {

    private static final String TAG = "FITNESS_QUANTITY";
    private static final String BACKEND_URL = "https://anthony-blockchain.us-south.containers.mybluemix.net";

    ImageView productImage;
    TextView productName;
    TextView quantity;
    TextView productPrice;
    Gson gson = new Gson();
    Button incrementQuantity;
    Button decrementQuantity;
    Button claimButton;
    int maxNumberInQuantity;
    Snackbar maxLimitNotification;
    String userId;
    boolean isEnrolled;
    int availableBalance;

    ShopItemModel product;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quantity_selection);
        AppCompatActivity context = this;

        // get the user id
        SharedPreferences sharedPreferences = this.getSharedPreferences("shared_preferences_fitcoin", Context.MODE_PRIVATE);

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

        // connect views
        productImage = findViewById(R.id.productImageInQuantity);
        productName = findViewById(R.id.productNameInQuantity);
        productPrice = findViewById(R.id.totalPriceInQuantity);
        quantity = findViewById(R.id.quantityTextInQuantity);
        incrementQuantity = findViewById(R.id.plusButton);
        decrementQuantity = findViewById(R.id.minusButton);
        claimButton = findViewById(R.id.claimButton);

        // get image from chosen product
        int image = getIntent().getIntExtra("PRODUCT_CHOSEN",R.drawable.ic_footprint);
        productImage.setImageResource(image);

        // get available balance
        availableBalance = getIntent().getIntExtra("AVAILABLE_BALANCE",0);

        // get the data model in string of the chosen product
        String stringOfShopItemModel = getIntent().getStringExtra("PRODUCT_JSON");
        Log.d("FITNESS_QUANTITY", getIntent().getStringExtra("PRODUCT_JSON"));

        // convert the string to a data model and set to views
        final ShopItemModel shopItemModel = gson.fromJson(stringOfShopItemModel, ShopItemModel.class);
        this.product = shopItemModel;

        productName.setText(shopItemModel.getProductName());
        productPrice.setText(String.valueOf(shopItemModel.getPrice()));

        // request queue
        queue = Volley.newRequestQueue(this);

        // make the max quantity and notification
        maxNumberInQuantity = 5;
        if (shopItemModel.getProductId().equals("kubecoin-shirt") || shopItemModel.getProductId().equals("kubecoin_shirt")) {
            maxNumberInQuantity = 1;
        }
        maxLimitNotification = Snackbar.make(findViewById(R.id.quantityLayout),maxNumberInQuantity + " items is the maximum quantity for this product",Snackbar.LENGTH_SHORT);

        // set on click listeners on button
        incrementQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int number = Integer.valueOf(quantity.getText().toString());
                if (number < maxNumberInQuantity) {
                    number++;
                    quantity.setText(String.valueOf(number));
                    productPrice.setText(String.valueOf(number * shopItemModel.getPrice()));
                } else {
                    if (!maxLimitNotification.isShown()) {
                        maxLimitNotification.show();
                    }
                }
            }
        });

        decrementQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int number = Integer.valueOf(quantity.getText().toString());
                if (number > 1) {
                    number--;
                    quantity.setText(String.valueOf(number));
                    productPrice.setText(String.valueOf(number * shopItemModel.getPrice()));
                }
            }
        });

        claimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                // disable user interaction
                decrementQuantity.setAlpha(0.5f);
                decrementQuantity.setEnabled(false);
                decrementQuantity.setClickable(false);
                incrementQuantity.setAlpha(0.5f);
                incrementQuantity.setEnabled(false);
                incrementQuantity.setClickable(false);
                claimButton.setAlpha(0.5f);
                claimButton.setEnabled(false);
                claimButton.setClickable(false);

                // send makePurchase request to blockchain here

                if (availableBalance >= Integer.valueOf(productPrice.getText().toString())) {
                    purchaseItem();
                } else {
                    AlertDialog notEnoughBalance = new AlertDialog.Builder(view.getContext()).create();
                    notEnoughBalance.setTitle("Purchase failed");
                    notEnoughBalance.setMessage("You don't have enough available fitcoins. You may cancel your pending contracts if you want to change them.\n\nYour available balance is: " + availableBalance);
                    notEnoughBalance.setButton(DialogInterface.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            ((Activity) view.getContext()).finish();
                        }
                    });
                    notEnoughBalance.show();
                }
            }
        });
    }

    public void purchaseItem() {
        try {
            JSONObject params = new JSONObject("{\"type\":\"invoke\",\"queue\":\"user_queue\",\"params\":{\"userId\":\"" + userId + "\", \"fcn\":\"makePurchase\", \"args\":[" + userId + "," + product.getSellerId() + "," + product.getProductId() + ",\"" + quantity.getText() + "\"]}}");
            Log.d(TAG, params.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BACKEND_URL + "/api/execute", params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            InitialResultFromRabbit initialResultFromRabbit = gson.fromJson(response.toString(), InitialResultFromRabbit.class);
                            if (initialResultFromRabbit.status.equals("success")) {
                                Log.d(TAG, response.toString());
                                getTransactionResult(initialResultFromRabbit.resultId,0);
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

    public void getTransactionResult(final String resultId, final int attemptNumber) {
        if (attemptNumber < 60) {
            final Activity activity = this;
            // GET THE TRANSACTION RESULT
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
                                        getTransactionResult(resultId,attemptNumber + 1);
                                    }
                                },500);
                            } else if (backendResult.status.equals("done")) {
                                // when blockchain is done processing the request, get the contract model and start activity
                                ResultOfMakePurchase resultOfMakePurchase = gson.fromJson(backendResult.result, ResultOfMakePurchase.class);
                                ContractModel contractModel = gson.fromJson(resultOfMakePurchase.result.results.payload, ContractModel.class);

                                // start activity of contract details
                                Intent intent = new Intent(getApplicationContext(), ContractDetails.class);
                                intent.putExtra("CONTRACT_JSON", new Gson().toJson(contractModel, ContractModel.class));

                                Pair<View, String> pair1 = Pair.create(findViewById(R.id.productImageInQuantity),"productImage");
                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pair1);

                                activity.startActivity(intent,options.toBundle());
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
        }
    }
}
