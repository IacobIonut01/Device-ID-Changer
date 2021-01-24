package com.iacob.idchanger.fragments;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.iacob.idchanger.MainActivity;
import com.iacob.idchanger.R;
import com.iacob.idchanger.app_parser.ApplicationModel;
import com.iacob.idchanger.utils.AppPreferences;
import com.iacob.idchanger.utils.IDInputFilter;
import com.iacob.idchanger.utils.IDManager;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class EditFragment extends RoundedSheetFragment {

    private final ApplicationModel applicationInfo;

    public EditFragment(ApplicationModel applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_sheet, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        AppPreferences prefs = new AppPreferences(requireContext());
        TextView appname = view.findViewById(R.id.s_appname);
        appname.setText(applicationInfo.getApp_name());
        TextView apppackage = view.findViewById(R.id.s_packagename);
        apppackage.setText(applicationInfo.getPackage_name());
        ImageView appicon = view.findViewById(R.id.s_appIcon);
        appicon.setImageDrawable(applicationInfo.getIcon());
        ExtendedEditText currID = view.findViewById(R.id.currentID);
        ExtendedEditText defID = view.findViewById(R.id.defaultID);
        currID.setText(applicationInfo.getID());
        defID.setText(applicationInfo.getDefID());
        if (applicationInfo.package_name.equals("android")) {
            currID.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new IDInputFilter("[A-F0-9]*"), new InputFilter.LengthFilter(64)});
            defID.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new IDInputFilter("[A-F0-9]*"), new InputFilter.LengthFilter(64)});
        } else {
            currID.setFilters(new InputFilter[]{new IDInputFilter("[a-f0-9]*"), new InputFilter.LengthFilter(16)});
            defID.setFilters(new InputFilter[]{new IDInputFilter("[a-f0-9]*"), new InputFilter.LengthFilter(16)});
        }
        ImageButton randomizer = view.findViewById(R.id.randomizeIDs);
        randomizer.setOnClickListener(v -> {
            currID.setText(IDManager.randomize(applicationInfo.package_name.equals("android")));
            defID.setText(IDManager.randomize(applicationInfo.package_name.equals("android")));
        });
        MaterialButton apply_changes = view.findViewById(R.id.apply_changes);
        apply_changes.setOnClickListener(v -> {
            applicationInfo.setID(currID.getText().toString());
            applicationInfo.setDefID(defID.getText().toString());
            prefs.addModifiedID(applicationInfo.package_name);
            applicationInfo.modified = true;
            MainActivity.updateApp(applicationInfo);
            dismiss();
        });
        super.onViewCreated(view, savedInstanceState);
    }
}
