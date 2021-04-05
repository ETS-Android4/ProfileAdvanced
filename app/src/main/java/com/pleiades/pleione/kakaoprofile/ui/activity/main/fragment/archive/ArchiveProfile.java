package com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.archive;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.pleiades.pleione.kakaoprofile.prefs.PrefsController;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.profiles.Profile;

import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.DEFAULT_NEWEST_FIRST;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_SORT_ARCHIVE;

public class ArchiveProfile extends Profile {
    public ArchiveProfile(String name, Uri path, long lastModified) {
        super(name, path, lastModified);
        super.setNew(false);
    }

    @Override
    public int compareTo(@NonNull Profile profile) {
        int newest = PrefsController.getInt(KEY_SORT_ARCHIVE);

        if (newest == DEFAULT_NEWEST_FIRST)
            return -1 * Long.compare(getLastModified(), profile.getLastModified()); // newest first
        else
            return Long.compare(getLastModified(), profile.getLastModified()); // oldest first
    }
}
