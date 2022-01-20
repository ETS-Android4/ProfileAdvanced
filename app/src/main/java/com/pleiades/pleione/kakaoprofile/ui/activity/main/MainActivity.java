package com.pleiades.pleione.kakaoprofile.ui.activity.main;

import static com.pleiades.pleione.kakaoprofile.RequestConfig.EXTRA_REFRESH;
import static com.pleiades.pleione.kakaoprofile.RequestConfig.REQUEST_FULL;
import static com.pleiades.pleione.kakaoprofile.RequestConfig.REQUEST_PERMISSIONS;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_LAST_VERSION_CODE;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_NOTICE;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_PERMISSION;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_ROUND_DEFAULT;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.TAG_NOTICE;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.TAG_PERMISSION;
import static com.pleiades.pleione.kakaoprofile.ui.instant.InstantConfig.FROM_ARCHIVE_FRAGMENT;
import static com.pleiades.pleione.kakaoprofile.ui.instant.InstantConfig.FROM_FULL;
import static com.pleiades.pleione.kakaoprofile.ui.instant.InstantConfig.FROM_PROFILES_FRAGMENT;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pleiades.pleione.kakaoprofile.R;
import com.pleiades.pleione.kakaoprofile.cache.CacheController;
import com.pleiades.pleione.kakaoprofile.prefs.PrefsController;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.archive.ArchiveFragment;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.profiles.ProfilesFragment;
import com.pleiades.pleione.kakaoprofile.ui.dialog.DialogRoundFragment;
import com.pleiades.pleione.kakaoprofile.ui.instant.InstantHolder;

public class MainActivity extends AppCompatActivity {
    public static String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static Context applicationContext;

    private boolean isPermissionAllowed = false;
    private CacheController cacheController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // application context;
        applicationContext = getApplicationContext();

        // NoActionBar hide default app bar
        // Use custom toolbar as app bar
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // label
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        // bottom navigation
        initializeBottomNavigation();

        // notification dialog
        noticeDialog();
    }


    private void initializeBottomNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_profiles, R.id.navigation_archive, R.id.navigation_more).build();

        // new fragment container view
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void initializePermissions() {
        // always SDK > M
        if (checkPermissions())
            isPermissionAllowed = true;
        else {
            DialogRoundFragment dialogRoundFragment = new DialogRoundFragment(DIALOG_ROUND_DEFAULT, DIALOG_PERMISSION, this, getString(R.string.dialog_android_io_message), true, false);
            dialogRoundFragment.show(getSupportFragmentManager(), TAG_PERMISSION);
        }
    }

    private boolean checkPermissions() {
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    private void cacheControl() {
        // cache
        if (isPermissionAllowed) {
            // initialize cache controller
            if (cacheController == null)
                cacheController = new CacheController(this);

            Handler handler = new Handler(getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // initialize cache
                    cacheController.initializeCache();

                    // refresh profile recyclerview
                    ProfilesFragment.refreshProfileRecyclerView(false);
                }
            });
        }
    }

    private void noticeDialog() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(this.getPackageName(), 0);
            int version = packageInfo.versionCode;
            int lastVersionCode = PrefsController.getInt(KEY_LAST_VERSION_CODE);

            // for new, update users
            if (lastVersionCode < version) {
                DialogRoundFragment dialogRoundFragment = new DialogRoundFragment(DIALOG_ROUND_DEFAULT, DIALOG_NOTICE, this, getString(R.string.dialog_notice_message), true, false);
                dialogRoundFragment.show(getSupportFragmentManager(), TAG_NOTICE);

                // now latest user
                PrefsController.setInt(KEY_LAST_VERSION_CODE, version);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onBackPressed() {
        switch (InstantHolder.getFrom(false)) {
            case FROM_PROFILES_FRAGMENT:
                // selection mode true
                if ((ProfilesFragment.profileAdapter != null) && ProfilesFragment.profileAdapter.getSelectionMode()) {
                    ProfilesFragment.profileAdapter.unsetSelectedAll();
                    ProfilesFragment.profileAdapter.setSelectionMode(false);
                    invalidateOptionsMenu();
                } else
                    super.onBackPressed();
                break;
            case FROM_ARCHIVE_FRAGMENT:
                // selection mode true
                if ((ArchiveFragment.archiveAdapter != null) && ArchiveFragment.archiveAdapter.getSelectionMode()) {
                    ArchiveFragment.archiveAdapter.unsetSelectedAll();
                    ArchiveFragment.archiveAdapter.setSelectionMode(false);
                    invalidateOptionsMenu();
                } else
                    super.onBackPressed();
                break;
            default:
                super.onBackPressed();
        }
    }

    // after onCreate, onStart or return to activity
    @Override
    protected void onResume() {
        if (InstantHolder.getFrom(true) != FROM_FULL) {
            // permission
            initializePermissions();

            // cache
            cacheControl();
        }

        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == REQUEST_FULL) && (resultCode == RESULT_OK)) {
            InstantHolder.setFrom(FROM_FULL);
            if (data.getBooleanExtra(EXTRA_REFRESH, false))
                ProfilesFragment.profileAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionAllowed = true;
                cacheControl();
            }
        }
    }

}
