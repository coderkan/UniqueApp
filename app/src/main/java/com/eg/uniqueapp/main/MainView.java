package com.eg.uniqueapp.main;

import com.eg.uniqueapp.model.Model;

import java.util.ArrayList;

/**
 * Created by Erkan.Guzeler on 31.01.2017.
 */

public interface MainView {
    void onLoadModel(Model referenceModel);

    void onShowMessageDialog();

    void onLoadAuth();

    void onLoadUpdates(ArrayList<Model> list);

}
