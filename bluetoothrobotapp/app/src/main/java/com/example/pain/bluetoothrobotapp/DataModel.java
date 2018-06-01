package com.example.pain.bluetoothrobotapp;

class DataModel {
    private static final DataModel ourInstance = new DataModel();

    float currentlum;//luminosité actuelle

    static DataModel getInstance() {
        return ourInstance;
    }

    private DataModel() {
    }
}
