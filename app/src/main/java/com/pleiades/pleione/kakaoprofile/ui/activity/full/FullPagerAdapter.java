package com.pleiades.pleione.kakaoprofile.ui.activity.full;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.archive.ArchiveFragment;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.profiles.ProfilesFragment;
import com.pleiades.pleione.kakaoprofile.ui.instant.InstantHolder;

import static com.pleiades.pleione.kakaoprofile.ui.instant.InstantConfig.FROM_ARCHIVE_FRAGMENT;
import static com.pleiades.pleione.kakaoprofile.ui.instant.InstantConfig.FROM_PROFILES_FRAGMENT;

public class FullPagerAdapter extends FragmentPagerAdapter {
    private long baseID = 0;

    FullPagerAdapter(FragmentManager manager) {
        super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new FullPageFragment(position);
    }

    @Override
    public int getCount() {
        int from = InstantHolder.getFrom(false);
        if(from == FROM_PROFILES_FRAGMENT)
            return ProfilesFragment.profileList.size();
        else if (from == FROM_ARCHIVE_FRAGMENT)
            return ArchiveFragment.archiveList.size();

        return 0;
    }

    // called with notifyDataSetChanged()
    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public long getItemId(int position) {
        // give an ID different from position when position has been changed
        return baseID + position;
    }

    // create a new ID for each position to force recreation of the fragment
    public void notifyChangeInPosition(int n) { // number of items which have been changed
        // shift the ID returned by getItemId outside the range of all previous fragments
        baseID = baseID + getCount() + n;
    }
}
