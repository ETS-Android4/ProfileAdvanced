package com.pleiades.pleione.kakaoprofile.prefs;

public class PrefsConfig {
    // prefs key
    public static final String KEY_SPAN_COUNT = "spanCount";
    public static final String KEY_SORT_PROFILES = "sortProfiles";
    public static final String KEY_SORT_ARCHIVE = "sortArchive";
    public static final String KEY_LENGTH_LOWER_BOUND = "lengthLowerBound";
    public static final String KEY_PERMISSION_URI = "uriPermission";

    public static final String KEY_NOT_NEW_PROFILE_NAME_LIST = "notNewProfileList";
    public static final String KEY_HIDE_NAME_LIST = "hideList";
    public static final String KEY_REMOVE_ADS = "remove_ads"; // id must consist only of lowercase letters, numbers, underscores, and dots.

    public static final String KEY_LAST_VERSION_CODE = "lastVersionCode";

    // prefs value
    public static final int DEFAULT_SPAN_COUNT = 3;
    public static final int DEFAULT_NEWEST_FIRST = 0; // oldest first is 1
    public static final int DEFAULT_LENGTH_LOWER_BOUND = 30; // kilobyte
    public static final int DEFAULT_VERSION = 1;
}
