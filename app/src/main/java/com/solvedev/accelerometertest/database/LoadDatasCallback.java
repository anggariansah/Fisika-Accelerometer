package com.solvedev.accelerometertest.database;

import com.solvedev.accelerometertest.model.Data;

import java.util.ArrayList;

public interface LoadDatasCallback {

    void preExecute();
    void postExecute(ArrayList<Data> datas);

}
