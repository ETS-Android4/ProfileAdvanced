package com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.archive;

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
import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.profiles.ProfileRecyclerAdapter;
import com.pleiades.pleione.kakaoprofile.ui.dialog.DialogRoundFragment;
import com.pleiades.pleione.kakaoprofile.ui.instant.InstantHolder;
import com.pleiades.pleione.kakaoprofile.ui.toast.ToastController;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static com.pleiades.pleione.kakaoprofile.cache.CacheConfig.CACHE_CHANGED;
import static com.pleiades.pleione.kakaoprofile.cache.CacheConfig.CACHE_NOT_FOUND;
import static com.pleiades.pleione.kakaoprofile.cache.CacheConfig.archiveCacheFlag;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_SPAN_COUNT;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_ROUND_LIST;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_SORT_ARCHIVE;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.TAG_SORT_ARCHIVE;
import static com.pleiades.pleione.kakaoprofile.ui.instant.InstantConfig.FROM_ARCHIVE_FRAGMENT;
import static com.pleiades.pleione.kakaoprofile.ui.toast.ToastConfig.DEFAULT_POSITION;
import static com.pleiades.pleione.kakaoprofile.ui.toast.ToastConfig.SHORT;

public class ArchiveFragment extends Fragment {
    private Context context;
    public DialogRoundFragment dialogRoundFragment;

    // profile recycler view
    public static RecyclerView archiveRecyclerView;
    public static ArrayList<ArchiveProfile> archiveList = new ArrayList<ArchiveProfile>();
    public static ProfileRecyclerAdapter archiveAdapter;

    // save scroll position
    private final String KEY_RECYCLER_STATE = "recyclerState";
    private static Bundle recyclerStateBundle;
    private Parcelable listState = null;

    // root view
    private static View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_archive, container, false);

        setHasOptionsMenu(true);

        initializeArchiveRecyclerView();
        refreshArchiveRecyclerView(true);

        return root;
    }

    private void initializeArchiveRecyclerView() {
        // initialize and optimize recycler view
        archiveRecyclerView = root.findViewById(R.id.recycler_view_archive);
        archiveRecyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) archiveRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        // manage recycler view layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, PrefsController.getInt(KEY_SPAN_COUNT));
        archiveRecyclerView.setLayoutManager(gridLayoutManager);
    }

    public static void refreshArchiveRecyclerView(boolean ignoreCache) {
        refreshMessageVisibility();

        // refresh recyclerview core
        if (ignoreCache || (archiveCacheFlag == CACHE_CHANGED)) {
            // initialize adapter
            archiveAdapter = new ProfileRecyclerAdapter((archiveList));
            archiveAdapter.setHasStableIds(true);
            archiveRecyclerView.setAdapter(archiveAdapter);
        }
    }

    public static void refreshMessageVisibility() {
        // message visibility
        TextView message = root.findViewById(R.id.archive_fragment_message);
        if ((archiveCacheFlag == CACHE_NOT_FOUND) || (archiveList.size() == 0))
            message.setVisibility(View.VISIBLE);
        else
            message.setVisibility(View.INVISIBLE);
    }

    public static void sortArchiveRecyclerView() {
        if (archiveRecyclerView != null) {
            Collections.sort(archiveList);
            archiveAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate the menu; this adds items to the action bar if it is present.
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (archiveAdapter.getSelectionMode()) {
            inflater.inflate(R.menu.menu_action_bar_selected_archive, menu);
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
            dialogRoundFragment = new DialogRoundFragment(DIALOG_ROUND_LIST, DIALOG_SORT_ARCHIVE, context, getString(R.string.action_sort), messages);
            dialogRoundFragment.setCancelable(true);
            dialogRoundFragment.show(getActivity().getSupportFragmentManager(), TAG_SORT_ARCHIVE);
        } else if (id == android.R.id.home)
            getActivity().onBackPressed();
        else {
            Handler handler = new Handler(context.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // initialize selected list
                    ArrayList<Integer> selectedList = new ArrayList<>(archiveAdapter.getSelected());
                    Collections.sort(selectedList);

                    if (selectedList.size() > 0) {

                        CacheController cacheController = new CacheController(context);
                        ToastController toastController = new ToastController(context);
                        boolean result = true;

                        switch (id) {
                            case R.id.action_remove:
                                // reverse selected list
                                Collections.reverse(selectedList);

                                for (int i : selectedList) {
                                    cacheController.removeArchiveCache(archiveList.get(i).getPath());
                                    archiveAdapter.setSelected(i, false);
                                    archiveList.remove(i);
                                    archiveAdapter.notifyItemRemoved(i);
                                }

                                refreshMessageVisibility();
                                break;
                            case R.id.action_save:
                                // save
                                for (Integer i : selectedList)
                                    if (!cacheController.saveProfileCache(archiveList.get(i)))
                                        result = false;

                                // toast
                                if (result)
                                    toastController.showCustomToast(DEFAULT_POSITION, SHORT, R.dimen.nav_bar_default, R.string.toast_saved);
                                else
                                    toastController.showCustomToast(DEFAULT_POSITION, SHORT, R.dimen.nav_bar_default, R.string.toast_saved_failed);

                                archiveAdapter.unsetSelectedAll();
                                break;
                            case R.id.action_share:
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                                intent.setType("image/*");

                                ArrayList<Uri> uris = new ArrayList<Uri>();
                                for (Integer i : selectedList) {
                                    File file = new File(archiveList.get(i).getPath().getPath());
                                    Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
                                    uris.add(uri);
                                }

                                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                                startActivity(intent);

                                archiveAdapter.unsetSelectedAll();
                                break;
                        }

                        // cancel selection
//                        archiveAdapter.unsetSelectedAll();
                    }

                    // cancel selection mode
                    archiveAdapter.setSelectionMode(false);
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
        listState = archiveRecyclerView.getLayoutManager().onSaveInstanceState();
        recyclerStateBundle.putParcelable(KEY_RECYCLER_STATE, listState);

        super.onPause();
    }

    @Override
    public void onResume() {
        // load recycler view position
        if (recyclerStateBundle != null) {
            listState = recyclerStateBundle.getParcelable(KEY_RECYCLER_STATE);
            archiveRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }

        InstantHolder.setFrom(FROM_ARCHIVE_FRAGMENT);
        super.onResume();
    }
}
