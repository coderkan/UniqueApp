package com.eg.uniqueapp.auth;

import com.eg.uniqueapp.model.Model;

import java.util.ArrayList;

/**
 * Created by Erkan.Guzeler on 31.01.2017.
 */

public interface SettingsView {
    void onOpenAuth();
    void onShowMessage(String msg);
    void onLoadModel(Model model);
    void onLoadFirebase(Model lmodel, Model refModel);
    void onLoadUpdates(ArrayList<Model> list);
}
