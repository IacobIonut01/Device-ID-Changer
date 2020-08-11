package com.iacob.idchanger.app_parser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class ApplicationEditAdapter extends RecyclerView.Adapter<ApplicationEditAdapter.ViewHolder> {

    public ArrayList<ApplicationModel> applicationModels;
    private FragmentManager fm;

    public ApplicationEditAdapter(ArrayList<ApplicationModel> applicationModels, FragmentManager fm) {
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
        AppPreferences prefs = new AppPreferences(holder.itemView.getContext());
        ApplicationModel app = applicationModels.get(position);
        holder.appName.setText(app.getApp_name());
        holder.appImage.setImageDrawable(app.getIcon());
        holder.appRemove.setOnClickListener(v -> {
            prefs.
            applicationModels.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return applicationModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView appName;
        ImageView appImage;
        ImageButton appRemove;

        ViewHolder(View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.dialog_appName);
            appImage = itemView.findViewById(R.id.dialog_appIcon);
            appRemove = itemView.findViewById(R.id.dialog_appRemove);
        }

    }

}
