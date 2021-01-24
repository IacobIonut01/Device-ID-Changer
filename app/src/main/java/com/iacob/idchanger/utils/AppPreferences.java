package com.iacob.idchanger.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class AppPreferences {

    private final SharedPreferences prefs;

    public AppPreferences(Context context) {
        prefs = context.getSharedPreferences("app_utils", MODE_PRIVATE);
    }

    public void rememberRootUser(boolean isRoot) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("rootUser", isRoot);
        editor.apply();
    }

    public boolean isRootUser() {
        return prefs.getBoolean("rootUser", false);
    }

    public void addModifiedID(String packagename) {
        SharedPreferences.Editor editor = prefs.edit();
        ArrayList<String> list = new ArrayList<>();
        if (getModifiedIDs() != null) {
            list = getModifiedIDs();
        }
        if (list.contains(packagename)) {
            list.removeIf(s -> s.contains(packagename));
            list.add(String.format("packagename: %s", packagename));
        } else {
            list.add(String.format("packagename: %s", packagename));
        }
        try {
            editor.putString("modifiedIDs", ObjectSerializer.serialize(list));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    public ArrayList<String> getModifiedIDs() {
        ArrayList<String> list = null;
        String result = prefs.getString("modifiedIDs", "");
        if (result != null && !result.equals("")) try {
            list = (ArrayList<String>) ObjectSerializer.deserialize(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.d("ListCheck", list.toString());
        return list;
    }

    public void clearModifiedIDsFromStorage() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("modifiedIDs", "");
        editor.apply();
    }
}
