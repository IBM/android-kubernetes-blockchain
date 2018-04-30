package com.amanse.anthony.fitcoinandroid;

public class Objects {}

class GetStateFinalResult {
    String[] contractIds;
    int fitcoinsBalance;
    String id;
    String memberType;
    int stepsUsedForConversion;
    int totalSteps;
}

class InitialResultFromRabbit {
    String status;
    String resultId;
}

class BackendResult {
    String status;
    String result;
}

class ResultOfEnroll {
    String message;
    EnrollFinalResult result;
}

class EnrollFinalResult {
    String user;
    String txId;
    String error;
}

class ResultOfBackendResult {
    String message;
    String result;
    String error;
}

class ResultOfTransactionResult {
    int status;
    String message;
    String payload;
}

class TransactionResult {
    String txId;
    ResultOfTransactionResult results;
}

class ResultOfMakePurchase {
    String message;
    TransactionResult result;
    String error;
}