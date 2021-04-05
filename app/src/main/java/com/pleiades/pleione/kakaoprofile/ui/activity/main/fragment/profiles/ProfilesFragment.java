package com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.profiles;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.pleiades.pleione.kakaoprofile.R;
import com.pleiades.pleione.kakaoprofile.cache.CacheController;
import com.pleiades.pleione.kakaoprofile.prefs.PrefsController;
import com.pleiades.pleione.kakaoprofile.ui.activity.HideActivity;
import com.pleiades.pleione.kakaoprofile.ui.dialog.DialogRoundFragment;
import com.pleiades.pleione.kakaoprofile.ui.instant.InstantHolder;
import com.pleiades.pleione.kakaoprofile.ui.toast.ToastController;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static com.pleiades.pleione.kakaoprofile.cache.CacheConfig.CACHE_CHANGED;
import static com.pleiades.pleione.kakaoprofile.cache.CacheConfig.CACHE_NOT_FOUND;
import static com.pleiades.pleione.kakaoprofile.cache.CacheConfig.profileCacheFlag;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_HIDE_NAME_LIST;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_SPAN_COUNT;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_ROUND_LIST;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_SORT_PROFILES;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.TAG_SORT_PROFILES;
import static com.pleiades.pleione.kakaoprofile.ui.instant.InstantConfig.FROM_PROFILES_FRAGMENT;
import static com.pleiades.pleione.kakaoprofile.ui.toast.ToastConfig.DEFAULT_POSITION;
import static com.pleiades.pleione.kakaoprofile.ui.toast.ToastConfig.SHORT;

public class ProfilesFragment extends Fragment {
    private static Context context;
    public DialogRoundFragment dialogRoundFragment;

    // profile recycler view
    public static RecyclerView profileRecyclerView;
    public static ArrayList<Profile> profileList = new ArrayList<Profile>();
    public static ProfileRecyclerAdapter profileAdapter;

    // save scroll position
    private final String KEY_RECYCLER_STATE = "recyclerState";
    private static Bundle recyclerStateBundle;
    private Parcelable listState = null;

    // root view
    private static View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profiles, container, false);

        setHasOptionsMenu(true);

        // initialize profile recyclerview
        initializeProfileRecyclerView();

        // assignment recyclerview
        refreshProfileRecyclerView(true);

        return root;
    }

    private void initializeProfileRecyclerView() {
        // initialize and optimize recycler view
        profileRecyclerView = root.findViewById(R.id.recycler_view_profiles);
        profileRecyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) profileRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        // manage recycler view layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, PrefsController.getInt(KEY_SPAN_COUNT));
        profileRecyclerView.setLayoutManager(gridLayoutManager);
    }

    public static void refreshProfileRecyclerView(boolean ignoreCache) {
        refreshMessageVisibility();

        // refresh recyclerview core
        if (ignoreCache || (profileCacheFlag == CACHE_CHANGED)) {
            // initialize adapter
            profileAdapter = new ProfileRecyclerAdapter((profileList));
            profileAdapter.setHasStableIds(true);
            profileRecyclerView.setAdapter(profileAdapter);
        }
    }

    public static void refreshMessageVisibility() {
        // message visibility
        TextView message = root.findViewById(R.id.profile_fragment_message);
        if ((profileCacheFlag == CACHE_NOT_FOUND) || (profileList.size() == 0))
            message.setVisibility(View.VISIBLE);
        else
            message.setVisibility(View.INVISIBLE);
    }

    public static void sortProfileRecyclerView() {
        if (profileRecyclerView != null) {
            Collections.sort(profileList);
            profileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ProfilesFragment.context = context;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate the menu; this adds items to the action bar if it is present.
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (profileAdapter.getSelectionMode()) {
            inflater.inflate(R.menu.menu_action_bar_selected_profiles, menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            inflater.inflate(R.menu.menu_action_bar_main, menu);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(ContextCompat.getColor(context, R.color.colorKakaoBlackText), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();

        // not selected
        if (id == R.id.action_sort) {
            String[] messages = getResources().getStringArray(R.array.dialog_sort);
            dialogRoundFragment = new DialogRoundFragment(DIALOG_ROUND_LIST, DIALOG_SORT_PROFILES, context, getString(R.string.action_sort), messages);
            dialogRoundFragment.setCancelable(true);
            dialogRoundFragment.show(getActivity().getSupportFragmentManager(), TAG_SORT_PROFILES);
        } else if (id == android.R.id.home)
            getActivity().onBackPressed();
        else {
            Handler handler = new Handler(context.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // initialize selected list
                    ArrayList<Integer> selectedList = new ArrayList<>(profileAdapter.getSelected());
                    Collections.sort(selectedList);

                    if (selectedList.size() > 0) {
                        CacheController cacheController = new CacheController(context);
                        ToastController toastController = new ToastController(context);
                        boolean result = true;

                        switch (id) {
                            case R.id.action_add:
                                // copy
                                for (Integer i : selectedList)
                                    if (!cacheController.copyProfileCache(profileList.get(i)))
                                        result = false;

                                // initialize cache
                                cacheController.initializeCache(false, true);

                                // toast
                                if (result)
                                    toastController.showCustomToast(DEFAULT_POSITION, SHORT, R.dimen.nav_bar_default, R.string.toast_added);
                                else
                                    toastController.showCustomToast(DEFAULT_POSITION, SHORT, R.dimen.nav_bar_default, R.string.toast_added_failed);
                                profileAdapter.unsetSelectedAll();
                                break;
                            case R.id.action_save:
                                // save
                                for (Integer i : selectedList)
                                    if (!cacheController.saveProfileCache(profileList.get(i)))
                                        result = false;

                                // toast
                                if (result)
                                    toastController.showCustomToast(DEFAULT_POSITION, SHORT, R.dimen.nav_bar_default, R.string.toast_saved);
                                else
                                    toastController.showCustomToast(DEFAULT_POSITION, SHORT, R.dimen.nav_bar_default, R.string.toast_saved_failed);
                                profileAdapter.unsetSelectedAll();
                                break;
                            case R.id.action_share:
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                                intent.setType("image/*");

                                ArrayList<Uri> uris = new ArrayList<Uri>();
                                for (Integer i : selectedList) {
                                    File file = new File(profileList.get(i).getPath().getPath());
                                    Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
                                    uris.add(uri);
                                }

                                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                                startActivity(intent);
                                profileAdapter.unsetSelectedAll();
                                break;
                            case R.id.action_hide:
                                ArrayList<String> hideNameList = PrefsController.getListPrefs(KEY_HIDE_NAME_LIST);
                                if (hideNameList == null)
                                    hideNameList = new ArrayList<String>();

                                for (Integer i : selectedList) {
                                    // add hide profile
                                    HideActivity.hideList.add(profileList.get(i));

                                    // add hide name list
                                    String profileName = profileList.get(i).getName();
                                    if (!hideNameList.contains(profileName))
                                        hideNameList.add(profileName);
                                }
                                PrefsController.setListPrefs(KEY_HIDE_NAME_LIST, hideNameList);

                                // remove from profile list
                                // reverse selected
                                Collections.reverse(selectedList);

                                for (int i : selectedList) {
                                    profileAdapter.setSelected(i, false);
                                    profileList.remove(i);
                                    profileAdapter.notifyItemRemoved(i);
                                }

                                // initialize and refresh
//                                cacheController.initializeCache();
//                                refreshProfileRecyclerView(false);
                                break;
                        }

                        // cancel selection
//                        profileAdapter.unsetSelectedAll();
                    }

                    // cancel selection mode
                    profileAdapter.setSelectionMode(false);
                    getActivity().invalidateOptionsMenu();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        // store recycler view position
        recyclerStateBundle = new Bundle();
        listState = profileRecyclerView.getLayoutManager().onSaveInstanceState();
        recyclerStateBundle.putParcelable(KEY_RECYCLER_STATE, listState);

        super.onPause();
    }

    @Override
    public void onResume() {
        // load recycler view position
        if (recyclerStateBundle != null) {
            listState = recyclerStateBundle.getParcelable(KEY_RECYCLER_STATE);
            profileRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
        InstantHolder.setFrom(FROM_PROFILES_FRAGMENT);
        super.onResume();
    }
}
