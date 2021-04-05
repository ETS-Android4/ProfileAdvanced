package com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.profiles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.pleiades.pleione.kakaoprofile.DeviceController;
import com.pleiades.pleione.kakaoprofile.R;
import com.pleiades.pleione.kakaoprofile.prefs.PrefsController;
import com.pleiades.pleione.kakaoprofile.ui.activity.full.FullActivity;
import com.pleiades.pleione.kakaoprofile.ui.instant.InstantHolder;

import java.util.ArrayList;
import java.util.HashSet;

import static com.pleiades.pleione.kakaoprofile.RequestConfig.EXTRA_POSITION;
import static com.pleiades.pleione.kakaoprofile.RequestConfig.REQUEST_FULL;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_SPAN_COUNT;
import static com.pleiades.pleione.kakaoprofile.ui.instant.InstantConfig.FROM_ARCHIVE_FRAGMENT;
import static com.pleiades.pleione.kakaoprofile.ui.instant.InstantConfig.FROM_HIDE;

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.ProfileViewHolder> {
    private ArrayList<Profile> profileList;
    private Context context;

    private HashSet<Integer> selected;
    private boolean selectionMode;

    // constructor
    public ProfileRecyclerAdapter(ArrayList profileList) {
        this.profileList = profileList;

        selected = new HashSet<>();
        selectionMode = false;
    }

    // attach view with holder
    class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        ImageView profileNew;
        ImageView profileSelected;

        ProfileViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            profileNew = itemView.findViewById(R.id.profile_new);
            profileSelected = itemView.findViewById(R.id.profile_selected);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectionMode)
                        toggleSelected(getAdapterPosition());
                    else if (InstantHolder.getFrom(false) != FROM_HIDE) {
                        int position = getAdapterPosition();
                        Intent intent = new Intent(context, FullActivity.class);
                        intent.putExtra(EXTRA_POSITION, position);
                        ((Activity) context).startActivityForResult(intent, REQUEST_FULL);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // haptic feedback
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                    // change selection mode
                    selectionMode = true;

                    // set item selected
                    setSelected(getAdapterPosition(), true);

                    // refresh action bar menu
                    ((FragmentActivity) context).invalidateOptionsMenu();

                    return true;
                }
            });
        }
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_component_profile, viewGroup, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        // to prevent duplicate contents
        // holder.setIsRecyclable(false);

        // set profile image
        Glide.with(context)
                .load(profileList.get(position).getPath())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(DeviceController.getWidthMax() / PrefsController.getInt(KEY_SPAN_COUNT))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.profileImage);

        // selection visibility
        if (selectionMode) {
            if (selected.contains(position))
                holder.profileSelected.setVisibility(View.VISIBLE);
            else
                holder.profileSelected.setVisibility(View.INVISIBLE);
        } else
            holder.profileSelected.setVisibility(View.INVISIBLE);

        // new visibility
        int from = InstantHolder.getFrom(false);
        switch (from) {
            case FROM_ARCHIVE_FRAGMENT:
            case FROM_HIDE:
                holder.profileNew.setVisibility(View.INVISIBLE);
                break;
            default:
                if (profileList.get(position).getNew())
                    holder.profileNew.setVisibility(View.VISIBLE);
                else
                    holder.profileNew.setVisibility(View.INVISIBLE);
        }
    }

    // selected
    public void setSelected(int position, boolean selected) {
        if (selected)
            this.selected.add(position);
        else
            this.selected.remove(position);
        notifyItemChanged(position);
    }

    public void setSelectedRange(int start, int end, boolean selected) {
        for (int i = start; i <= end; i++) {
            if (selected)
                this.selected.add(i);
            else
                this.selected.remove(i);
        }
        notifyItemRangeChanged(start, end - start + 1);
    }

    public void toggleSelected(int position) {
        if (selected.contains(position))
            selected.remove(position);
        else
            selected.add(position);
        notifyItemChanged(position);
    }

    public void setSelectedAll() {
        for (int i = 0; i < getItemCount(); i++)
            selected.add(i);
        notifyDataSetChanged();
    }

    public void unsetSelectedAll() {
        selected.clear();
        notifyDataSetChanged();
    }

    public HashSet<Integer> getSelected() {
        return selected;
    }

    // selection mode
    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }

    public boolean getSelectionMode() {
        return selectionMode;
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    @Override
    public long getItemId(int position) {
        return profileList.get(position).getName().hashCode();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
    }
}