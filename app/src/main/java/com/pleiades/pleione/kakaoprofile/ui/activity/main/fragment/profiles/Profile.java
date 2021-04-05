package com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.profiles;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pleiades.pleione.kakaoprofile.prefs.PrefsController;

import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.DEFAULT_NEWEST_FIRST;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_SORT_PROFILES;

public class Profile implements Comparable<Profile> {
    private String name;
    private Uri path;
    private long lastModified;
    private boolean isNew = true;

    public Profile(String name, Uri path, long lastModified) {
        this.name = name;
        this.path = path;
        this.lastModified = lastModified;
    }

    // getter
    public String getName() {
        return name;
    }

    public Uri getPath() {
        return path;
    }

    public long getLastModified() {
        return lastModified;
    }

    public boolean getNew() {
        return isNew;
    }

    // setter
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public int compareTo(@NonNull Profile profile) {
        int newest = PrefsController.getInt(KEY_SORT_PROFILES);

        if (newest == DEFAULT_NEWEST_FIRST)
            return -1 * Long.compare(this.lastModified, profile.lastModified); // newest first
        else
            return Long.compare(this.lastModified, profile.lastModified); // oldest first
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Profile profile = (Profile) obj;
        boolean result = true;

        if (!this.name.equals(profile.name))
            result = false;
        else if (!this.path.equals(profile.path))
            result = false;
        else if (this.lastModified != profile.lastModified)
            result = false;

        return result;
    }
}
