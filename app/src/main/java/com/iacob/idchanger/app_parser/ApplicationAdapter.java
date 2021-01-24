package com.iacob.idchanger.app_parser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iacob.idchanger.R;
import com.iacob.idchanger.fragments.EditFragment;
import com.iacob.idchanger.utils.AppPreferences;

import java.util.ArrayList;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    public ArrayList<ApplicationModel> applicationModels;
    private final FragmentManager fm;

    public ApplicationAdapter(ArrayList<ApplicationModel> applicationModels, FragmentManager fm) {
        this.applicationModels = applicationModels;
        this.fm = fm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.app_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApplicationModel app = applicationModels.get(position);
        holder.appName.setText(app.getApp_name());
        holder.appPackage.setText(app.getPackage_name());
        holder.appImage.setImageDrawable(app.getIcon());
        holder.appID.setText(app.getID());
        AppPreferences prefs = new AppPreferences(holder.itemView.getContext());
        if (prefs.getModifiedIDs() != null)
            holder.appModified.setText(prefs.getModifiedIDs().contains("packagename: " + app.getPackage_name()) ? "Modified ID" : "Original ID");
        else
            holder.appModified.setText("Original ID");
        holder.appContainer.setOnClickListener(v -> {
            EditFragment editFragment = new EditFragment(app);
            editFragment.show(fm, "edit_fragment");
        });
    }

    @Override
    public int getItemCount() {
        return applicationModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView appName, appPackage, appModified, appID;
        ImageView appImage;
        RelativeLayout appContainer;

        ViewHolder(View itemView) {
            super(itemView);
            appContainer = itemView.findViewById(R.id.appContainer);
            appName = itemView.findViewById(R.id.appName);
            appPackage = itemView.findViewById(R.id.appPackage);
            appModified = itemView.findViewById(R.id.appIDModified);
            appImage = itemView.findViewById(R.id.appIcon);
            appID = itemView.findViewById(R.id.appID);
        }

    }

}
