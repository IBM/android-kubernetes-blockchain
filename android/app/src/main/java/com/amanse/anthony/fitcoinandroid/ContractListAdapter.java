package com.amanse.anthony.fitcoinandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ContractListAdapter extends RecyclerView.Adapter<ContractListAdapter.ContractListViewHolder> implements View.OnClickListener {

    private Context context;
    private ArrayList<ContractModel> contractModels;
    private static String TAG = "FITNESS_CONTRACT_LIST";
    private static String BACKEND_URL = "https://anthony-blockchain.us-south.containers.mybluemix.net";

    RequestQueue queue;

    public ContractListAdapter(Context context, ArrayList<ContractModel> contractModels) {
        this.context = context;
        this.contractModels = contractModels;
        this.queue = Volley.newRequestQueue(context);
    }

    @Override
    public ContractListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.contract_item, null);
        return new ContractListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContractListViewHolder holder, int position) {
        ContractModel contractModel = contractModels.get(position);

        String contractDetails = String.valueOf(contractModel.quantity) + " of " + contractModel.productName;

        holder.cancelContractButton.setVisibility(View.GONE);

        holder.contractIdFromList.setText(contractModel.getContractId());
        holder.contractStateFromList.setText(contractModel.getState());
        holder.contractDetails.setText(contractDetails);

        holder.contractCard.setTag(contractModel);
        holder.contractCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final ContractModel contractModel = (ContractModel) view.getTag();

        boolean isPending = contractModel.getState().equals("pending");

        final Button cancelButton = view.findViewById(R.id.cancelContractButton);
        final TextView contractState = view.findViewById(R.id.contractStateFromList);

        if (cancelButton.getVisibility() == View.GONE && isPending) {
            cancelButton.setVisibility(View.VISIBLE);
        } else {
            cancelButton.setVisibility(View.GONE);
        }



        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FITNESS_CONTRACT_ITEM",contractModel.getContractId());

                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Decline this contract?");
                alertDialog.setMessage("Do you want to cancel contract " + contractModel.getContractId() + ".\nThe items are: " + contractModel.getQuantity() + " of " + contractModel.getProductName());
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Yes, remove this contract",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                declineContract(contractModel);

                                // build request sent dialog
                                AlertDialog requestSentDialog = new AlertDialog.Builder(context).create();
                                requestSentDialog.setTitle("Request Sent!");
                                requestSentDialog.setMessage("Your request to cancel the contract has been sent to the blockchain network. The contract's state should update at a later time.");
                                requestSentDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                                // set cancel button visibility to gone
                                cancelButton.setVisibility(View.GONE);

                                // update contract state
                                contractState.setText("declined");

                                // dismiss dialog
                                dialog.dismiss();

                                // show request sent dialog
                                requestSentDialog.show();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Nevermind",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button negativeButton =  alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        Button dismissButton = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);

                        negativeButton.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));
                        negativeButton.setTextColor(Color.RED);
                        dismissButton.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));
                        dismissButton.setTextColor(Color.BLUE);
                    }
                });
                alertDialog.show();

                // This finishes the activity
//                ((AppCompatActivity) view.getContext()).finish();
            }
        });

//        Intent intent = new Intent(view.getContext(),ContractDetails.class);
//        intent.putExtra("CONTRACT_JSON", new Gson().toJson(contractModel, ContractModel.class));
//
//        view.getContext().startActivity(intent);
    }

    public void declineContract(ContractModel contractModel) {
        try {
            JSONObject params = new JSONObject("{\"type\":\"invoke\",\"queue\":\"user_queue\",\"params\":{\"userId\":\"" + contractModel.getUserId() + "\", \"fcn\":\"transactPurchase\", \"args\":[" + contractModel.getUserId() + "," + contractModel.getContractId() + ",\"declined\"]}}");
            Log.d(TAG, params.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BACKEND_URL + "/api/execute", params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            InitialResultFromRabbit initialResultFromRabbit = new Gson().fromJson(response.toString(), InitialResultFromRabbit.class);
                            if (initialResultFromRabbit.status.equals("success")) {
                                Log.d(TAG, response.toString());
                                // add dialog here
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

    public class ContractListViewHolder extends RecyclerView.ViewHolder {
        TextView contractIdFromList, contractStateFromList, contractDetails;
        CardView contractCard;
        Button cancelContractButton;

        public ContractListViewHolder(View view) {
            super(view);

            contractIdFromList = view.findViewById(R.id.contractIdFromList);
            contractStateFromList = view.findViewById(R.id.contractStateFromList);
            contractDetails = view.findViewById(R.id.contractDetails);
            contractCard = view.findViewById(R.id.contractCard);
            cancelContractButton = view.findViewById(R.id.cancelContractButton);
        }
    }

    @Override
    public int getItemCount() {
        return contractModels.size();
    }
}
