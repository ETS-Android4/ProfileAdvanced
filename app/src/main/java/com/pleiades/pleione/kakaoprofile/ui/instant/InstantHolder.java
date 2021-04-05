package com.pleiades.pleione.kakaoprofile.ui.instant;

import static com.pleiades.pleione.kakaoprofile.ui.instant.InstantConfig.FROM_DEFAULT;

public class InstantHolder {
    // from
    private static int from = FROM_DEFAULT;

    public static int getFrom(boolean initialize) {
        int fromBackup = from;

        if(initialize) // initialize instant
            from = FROM_DEFAULT;

        return fromBackup;
    }

    public static void setFrom(int from) {
        InstantHolder.from = from;
    }
}
