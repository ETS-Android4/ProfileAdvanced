package com.pleiades.pleione.kakaoprofile.ui.activity.full;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pleiades.pleione.kakaoprofile.R;
import com.pleiades.pleione.kakaoprofile.prefs.PrefsController;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.profiles.ProfilesFragment;
import com.pleiades.pleione.kakaoprofile.ui.instant.InstantHolder;

import java.util.ArrayList;

import static com.pleiades.pleione.kakaoprofile.RequestConfig.EXTRA_REFRESH;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_NOT_NEW_PROFILE_NAME_LIST;
import static com.pleiades.pleione.kakaoprofile.ui.instant.InstantConfig.FROM_PROFILES_FRAGMENT;

public class FullActivity extends AppCompatActivity {
    private boolean refresh = false;
    public static FullPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);

        // NoActionBar hide default app bar
        // Use custom toolbar as app bar
        Toolbar toolbar = findViewById(R.id.toolbar_full);
        setSupportActionBar(toolbar);

        // label
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // arrow color
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorKakaoBlackText, null), PorterDuff.Mode.SRC_ATOP);

        // navigation color
        getWindow().setNavigationBarColor(Color.WHITE);

        // get intent extra
        int position = getIntent().getIntExtra("position", 0);
        setNotNew(position);

        // view pager
        final ViewPagerFixed viewPager = (ViewPagerFixed) findViewById(R.id.pager_full);
        pagerAdapter = new FullPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(new ViewPagerFixed.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setNotNew(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setNotNew(int position) {
        // new option only service on profiles fragment
        if (InstantHolder.getFrom(false) == FROM_PROFILES_FRAGMENT) {
            ProfilesFragment.profileList.get(position).setNew(false);

            ArrayList<String> notNewNameList = PrefsController.getListPrefs(KEY_NOT_NEW_PROFILE_NAME_LIST);
            if (notNewNameList == null)
                notNewNameList = new ArrayList<String>();

            notNewNameList.add(ProfilesFragment.profileList.get(position).getName());
            PrefsController.setListPrefs(KEY_NOT_NEW_PROFILE_NAME_LIST, notNewNameList);

            refresh = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_REFRESH, refresh);
        setResult(RESULT_OK, intent);
        finish();

        super.onBackPressed();
    }
}