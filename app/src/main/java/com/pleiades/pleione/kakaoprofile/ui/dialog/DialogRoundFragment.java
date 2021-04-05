package com.pleiades.pleione.kakaoprofile.ui.dialog;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pleiades.pleione.kakaoprofile.R;
import com.pleiades.pleione.kakaoprofile.cache.CacheController;
import com.pleiades.pleione.kakaoprofile.prefs.PrefsController;
import com.pleiades.pleione.kakaoprofile.ui.activity.HideActivity;
import com.pleiades.pleione.kakaoprofile.ui.activity.full.FullActivity;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.MainActivity;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.archive.ArchiveFragment;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.profiles.Profile;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.profiles.ProfilesFragment;
import com.pleiades.pleione.kakaoprofile.ui.toast.ToastController;

import java.io.File;
import java.util.ArrayList;

import static com.pleiades.pleione.kakaoprofile.RequestConfig.REQUEST_PERMISSIONS;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_HIDE_NAME_LIST;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_NOT_NEW_PROFILE_NAME_LIST;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_SORT_ARCHIVE;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_SORT_PROFILES;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_ADD_LIST;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_HIDE_LIST;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_MANAGE_ARCHIVE;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_MANAGE_PROFILE;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_PERMISSION;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_REMOVE_LIST;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_ROUND_DEFAULT;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_ROUND_LIST;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_SAVE_LIST;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_SHARE_LIST;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_SORT_ARCHIVE;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_SORT_PROFILES;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_TEMPORARY;
import static com.pleiades.pleione.kakaoprofile.ui.toast.ToastConfig.DEFAULT_POSITION;
import static com.pleiades.pleione.kakaoprofile.ui.toast.ToastConfig.HIGH_POSITION;
import static com.pleiades.pleione.kakaoprofile.ui.toast.ToastConfig.SHORT;

public class DialogRoundFragment extends androidx.fragment.app.DialogFragment {
    private int dialogType, contentsType, profilePosition;
    private Context context;
    private String title, message;
    private boolean positive, negative, cancelable;

    private String[] messages;

    // default constructor
    public DialogRoundFragment(int dialogType, int contentsType, Context context, String message, boolean positive, boolean negative) {
        this.dialogType = dialogType;
        this.contentsType = contentsType;
        this.context = context;
        this.message = message;
        this.positive = positive;
        this.negative = negative;
    }

    // list constructor
    public DialogRoundFragment(int dialogType, int contentsType, Context context, String title, String[] messages) {
        this.dialogType = dialogType;
        this.contentsType = contentsType;
        this.context = context;
        this.title = title;
        this.messages = messages;
    }

    // list constructor profiles
    public DialogRoundFragment(int dialogType, int contentsType, int profilePosition, Context context, String title, String[] messages) {
        this.dialogType = dialogType;
        this.contentsType = contentsType;
        this.profilePosition = profilePosition;
        this.context = context;
        this.title = title;
        this.messages = messages;
    }

    @Override
    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = null;

        if (dialogType == DIALOG_ROUND_DEFAULT) {
            // layout
            dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_round_default, null);

            // message
            ((TextView) dialogView.findViewById(R.id.dialog_message_default)).setText(message);

            // positive listener
            dialogView.findViewById(R.id.dialog_confirm_default).setOnClickListener(new OnClickListener());

            // negative listener
            dialogView.findViewById(R.id.dialog_negative_default).setOnClickListener(new OnClickListener());

            // negative visibility
            if (!negative)
                dialogView.findViewById(R.id.dialog_negative_default).setVisibility(View.INVISIBLE);
        } else if (dialogType == DIALOG_ROUND_LIST) {
            // layout
            dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_round_list, null);

            // title
            ((TextView) dialogView.findViewById(R.id.dialog_title_list)).setText(title);

            // list recycler view
            RecyclerView recyclerView = dialogView.findViewById(R.id.dialog_recycler_view_list);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(linearLayoutManager);
            DialogRecyclerAdapter adapter = new DialogRecyclerAdapter(messages, contentsType);
            adapter.setOnItemClickListener(new OnItemClickListener());

            // set adapter
            recyclerView.setAdapter(adapter);
        }

        // set dialog view
        builder.setView(dialogView);

        // create dialog
        Dialog dialog = builder.create();

        // set transparent background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // set cancelable
        dialog.setCanceledOnTouchOutside(cancelable);

        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        int dialogWidth = getResources().getDimensionPixelSize(R.dimen.dialog_round_width);
        int dialogHeight = ActionBar.LayoutParams.WRAP_CONTENT;

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }

    private void dismissDialog() {
        this.dismiss();
    }

    // click listener
    class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            Handler handler = new Handler(context.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    int id = v.getId();

                    // default dialog confirm
                    if (id == R.id.dialog_confirm_default) {
                        // permission confirm
                        if (contentsType == DIALOG_PERMISSION)
                            ((Activity) context).requestPermissions(MainActivity.permissions, REQUEST_PERMISSIONS);
                        else if (contentsType == DIALOG_TEMPORARY) {
                            CacheController cacheController = new CacheController(context);
                            ToastController toastController = new ToastController(context);

                            // initialize not new, hide
                            PrefsController.setListPrefs(KEY_NOT_NEW_PROFILE_NAME_LIST, new ArrayList<String>());
                            PrefsController.setListPrefs(KEY_HIDE_NAME_LIST, null);

                            // initialize cache
//                    cacheController.initializeCache(true, false);

                            // to fix clear bug
                            ProfilesFragment.profileList = new ArrayList<Profile>();

                            // toast
                            boolean result = cacheController.clearTemporaryDirectory();
                            if (result)
                                toastController.showCustomToast(DEFAULT_POSITION, SHORT, R.dimen.nav_bar_default, R.string.toast_cleared);
                            else
                                toastController.showCustomToast(DEFAULT_POSITION, SHORT, R.dimen.nav_bar_default, R.string.toast_cleared_failed);
                        }

                        dismissDialog();
                    } else if (id == R.id.dialog_negative_default) {
                        dismissDialog();
                    }
                }
            });
        }
    }

    // recycler view item click listener
    class OnItemClickListener implements DialogRecyclerAdapter.OnItemClickListener {
        @Override
        public void onItemClick(View v, final int position) {
            Handler handler = new Handler(context.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    CacheController cacheController;
                    ToastController toastController;

                    switch (contentsType) {
                        case DIALOG_SORT_PROFILES:
                            // set prefs
                            PrefsController.setInt(KEY_SORT_PROFILES, position);

                            // sort and notify
                            ProfilesFragment.sortProfileRecyclerView();
                            break;
                        case DIALOG_SORT_ARCHIVE:
                            // set prefs
                            PrefsController.setInt(KEY_SORT_ARCHIVE, position);

                            // sort and notify
                            ArchiveFragment.sortArchiveRecyclerView();
                            break;
                        case DIALOG_MANAGE_PROFILE:
                            cacheController = new CacheController(context);
                            toastController = new ToastController(context);

                            if (position == DIALOG_ADD_LIST) { // add to archive
                                // copy
                                boolean result = cacheController.copyProfileCache(ProfilesFragment.profileList.get(profilePosition));

                                // toast
                                if (result) {
                                    // initialize cache
                                    cacheController.initializeCache(false, true);

                                    toastController.showCustomToast(HIGH_POSITION, SHORT, R.dimen.app_bar_default, R.string.toast_added);
                                } else
                                    toastController.showCustomToast(HIGH_POSITION, SHORT, R.dimen.app_bar_default, R.string.toast_added_failed);
                            } else if (position == DIALOG_SAVE_LIST) { // save to storage
                                // save
                                boolean result = cacheController.saveProfileCache(ProfilesFragment.profileList.get(profilePosition));

                                // toast
                                if (result)
                                    toastController.showCustomToast(HIGH_POSITION, SHORT, R.dimen.app_bar_default, R.string.toast_saved);
                                else
                                    toastController.showCustomToast(HIGH_POSITION, SHORT, R.dimen.app_bar_default, R.string.toast_saved_failed);
                            } else if (position == DIALOG_SHARE_LIST) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_SEND);
                                intent.setType("image/*");

                                File file = new File(ProfilesFragment.profileList.get(profilePosition).getPath().getPath());
                                Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
                                intent.putExtra(Intent.EXTRA_STREAM, uri);

                                startActivity(intent);
                                break;
                            } else if (position == DIALOG_HIDE_LIST) {
                                // add hide profile
                                HideActivity.hideList.add(ProfilesFragment.profileList.get(profilePosition));

                                // initialize hide name list
                                ArrayList<String> hideNameList = PrefsController.getListPrefs(KEY_HIDE_NAME_LIST);
                                if (hideNameList == null)
                                    hideNameList = new ArrayList<String>();

                                // add hide name
                                String profileName = ProfilesFragment.profileList.get(profilePosition).getName();
                                if (!hideNameList.contains(profileName))
                                    hideNameList.add(profileName);
                                PrefsController.setListPrefs(KEY_HIDE_NAME_LIST, hideNameList);

                                // remove from profile list
//                                cacheController.initializeCache();
                                ProfilesFragment.profileList.remove(profilePosition);
                                ProfilesFragment.profileAdapter.notifyItemRemoved(profilePosition);
                                ProfilesFragment.refreshMessageVisibility();
//                                ProfilesFragment.refreshProfileRecyclerView(false);
                                FullActivity.pagerAdapter.notifyDataSetChanged();
                            }
                            break;
                        case DIALOG_MANAGE_ARCHIVE:
                            cacheController = new CacheController(context);
                            toastController = new ToastController(context);
                            if (position == DIALOG_REMOVE_LIST) { // remove from archive
                                boolean result = cacheController.removeArchiveCache(ArchiveFragment.archiveList.get(profilePosition).getPath());
                                if (result) {
                                    ArchiveFragment.archiveList.remove(profilePosition);
                                    ArchiveFragment.archiveAdapter.notifyItemRemoved(profilePosition);
                                    ArchiveFragment.refreshMessageVisibility();

//                                    cacheController.initializeCache(false, true);
//                                    ArchiveFragment.refreshArchiveRecyclerView(false);
                                    FullActivity.pagerAdapter.notifyDataSetChanged();
                                }

                            } else if (position == DIALOG_SAVE_LIST) { // save to storage
                                // save
                                boolean result = cacheController.saveProfileCache(ArchiveFragment.archiveList.get(profilePosition));

                                // toast
                                if (result)
                                    toastController.showCustomToast(HIGH_POSITION, SHORT, R.dimen.app_bar_default, R.string.toast_saved);
                                else
                                    toastController.showCustomToast(HIGH_POSITION, SHORT, R.dimen.app_bar_default, R.string.toast_saved_failed);
                            } else if (position == DIALOG_SHARE_LIST) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_SEND);
                                intent.setType("image/*");

                                File file = new File(ArchiveFragment.archiveList.get(profilePosition).getPath().getPath());
                                Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
                                intent.putExtra(Intent.EXTRA_STREAM, uri);

                                startActivity(intent);
                                break;
                            }
                            break;
                    }
                    // dismiss
                    dismissDialog();
                }
            });
        }
    }
}
