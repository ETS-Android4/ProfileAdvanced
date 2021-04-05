package com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.more;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.pleiades.pleione.kakaoprofile.R;
import com.pleiades.pleione.kakaoprofile.ui.activity.HideActivity;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.MainActivity;
import com.pleiades.pleione.kakaoprofile.ui.dialog.DialogRoundFragment;

import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_REMOVE_ADS;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_DISCLAIMER;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_ROUND_DEFAULT;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_TEMPORARY;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_TIP;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.TAG_DISCLAIMER;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.TAG_TEMPORARY;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.TAG_TIP;

public class MoreRecyclerAdapter extends RecyclerView.Adapter<MoreRecyclerAdapter.MoreViewHolder> {
    private String[] moreList;
    private Context context;

    // constructor
    public MoreRecyclerAdapter(String[] moreList) {
        this.moreList = moreList;
    }

    // attach view with holder
    class MoreViewHolder extends RecyclerView.ViewHolder {
        TextView infoTitle;

        MoreViewHolder(View view) {
            super(view);
            infoTitle = view.findViewById(R.id.more_title);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (moreList[position].equals(context.getString(R.string.array_more_default_hide))) {
                        Intent intent = new Intent(context, HideActivity.class);
                        ((Activity) context).startActivity(intent);
                    } else if (moreList[position].equals(context.getString(R.string.array_more_default_temporary))) {
                        DialogRoundFragment dialogRoundFragment = new DialogRoundFragment(DIALOG_ROUND_DEFAULT, DIALOG_TEMPORARY, context, context.getString(R.string.dialog_clear_temporary_message), true, true);
                        dialogRoundFragment.show(((FragmentActivity) context).getSupportFragmentManager(), TAG_TEMPORARY);
                    } else if (moreList[position].equals(context.getString(R.string.array_more_application_share))) {
                        String url = context.getString(R.string.more_store_url_kakao_profile);
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url);
                        context.startActivity(sharingIntent);
//                        ((Activity) context).startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    } else if (moreList[position].equals(context.getString(R.string.array_more_info_tip))) {
                        DialogRoundFragment dialogRoundFragment = new DialogRoundFragment(DIALOG_ROUND_DEFAULT, DIALOG_TIP, context, context.getString(R.string.dialog_tip_message), true, false);
                        dialogRoundFragment.show(((FragmentActivity) context).getSupportFragmentManager(), TAG_TIP);
                    } else if (moreList[position].equals(context.getString(R.string.array_more_info_disclaimer))) {
                        DialogRoundFragment dialogRoundFragment = new DialogRoundFragment(DIALOG_ROUND_DEFAULT, DIALOG_DISCLAIMER, context, context.getString(R.string.dialog_disclaimer_message), true, false);
                        dialogRoundFragment.show(((FragmentActivity) context).getSupportFragmentManager(), TAG_DISCLAIMER);
                    } else if (moreList[position].equals(context.getString(R.string.array_more_application_remove_ads))) {
                        MainActivity.billingProcessor.purchase(((Activity) context), KEY_REMOVE_ADS);
                    } else if (moreList[position].equals(context.getString(R.string.array_more_application_update))) {
                        String url = context.getString(R.string.more_store_url_kakao_profile);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        context.startActivity(intent);
                    } else if (moreList[position].equals(context.getString(R.string.array_more_application_pleione))) {
                        String url = context.getString(R.string.more_store_url_pleione);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        ((Activity) context).startActivity(intent);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public MoreViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_component_more, viewGroup, false);
        return new MoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoreViewHolder holder, int position) {
        // to prevent duplicate contents
        holder.setIsRecyclable(false);

        holder.infoTitle.setText(moreList[position]);
    }

    @Override
    public int getItemCount() {
        return moreList.length;
    }

    @Override
    public long getItemId(int position) {
        return moreList[position].hashCode();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
    }
}