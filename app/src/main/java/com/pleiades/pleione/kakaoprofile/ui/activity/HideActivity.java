package com.pleiades.pleione.kakaoprofile.ui.activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.pleiades.pleione.kakaoprofile.R;
import com.pleiades.pleione.kakaoprofile.prefs.PrefsController;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.profiles.Profile;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.profiles.ProfileRecyclerAdapter;
import com.pleiades.pleione.kakaoprofile.ui.instant.InstantHolder;

import java.util.ArrayList;
import java.util.Collections;

import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_HIDE_NAME_LIST;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_SPAN_COUNT;
import static com.pleiades.pleione.kakaoprofile.ui.instant.InstantConfig.FROM_HIDE;

public class HideActivity extends AppCompatActivity {
    public static ArrayList<Profile> hideList = new ArrayList<Profile>();
    private RecyclerView hideRecyclerView;
    private ProfileRecyclerAdapter hideAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide);

        Toolbar toolbar = findViewById(R.id.toolbar_hide);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        initializeHideRecyclerView();
        refreshHideRecyclerView();
    }

    private void initializeHideRecyclerView() {
        hideRecyclerView = findViewById(R.id.recycler_view_hide);
        hideRecyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) hideRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, PrefsController.getInt(KEY_SPAN_COUNT));
        hideRecyclerView.setLayoutManager(gridLayoutManager);
    }

    private void refreshHideRecyclerView() {
        refreshMessageVisibility();

        hideAdapter = new ProfileRecyclerAdapter((hideList));
        hideAdapter.setHasStableIds(true);
        hideRecyclerView.setAdapter(hideAdapter);
    }

    private void refreshMessageVisibility() {
        // message visibility
        TextView message = findViewById(R.id.hide_fragment_message);
        if (hideList.size() == 0)
            message.setVisibility(View.VISIBLE);
        else
            message.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        ActionBar actionBar = getSupportActionBar();

        if (hideAdapter.getSelectionMode())
            getMenuInflater().inflate(R.menu.menu_action_bar_selected_hide, menu);
        else
            getMenuInflater().inflate(R.menu.menu_action_bar_hide, menu);

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorKakaoBlackText), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (hideAdapter.getSelectionMode()) {
            hideAdapter.unsetSelectedAll();
            hideAdapter.setSelectionMode(false);
            invalidateOptionsMenu();
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
            onBackPressed();
        else if (id == R.id.action_show) {
            Handler handler = new Handler(getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // initialize selected list
                    ArrayList<Integer> selectedList = new ArrayList<>(hideAdapter.getSelected());
                    Collections.sort(selectedList);

                    if (selectedList.size() > 0) {
                        ArrayList<String> hideNameList = PrefsController.getListPrefs(KEY_HIDE_NAME_LIST);
                        if (hideNameList == null)
                            return;

                        // reverse selected
                        Collections.reverse(selectedList);

                        // reinstate
                        for (int i : selectedList) {
                            hideAdapter.setSelected(i, false);
                            hideNameList.remove(i);
                            hideList.remove(i);
                            hideAdapter.notifyItemRemoved(i);
                        }

                        PrefsController.setListPrefs(KEY_HIDE_NAME_LIST, hideNameList);
                        refreshMessageVisibility();
                    }

                    // cancel selection
                    hideAdapter.setSelectionMode(false);
                    invalidateOptionsMenu();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        InstantHolder.setFrom(FROM_HIDE);
        super.onResume();
    }
}
