package com.pleiades.pleione.kakaoprofile.ui.activity.full;

import android.content.Context;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.chrisbanes.photoview.PhotoView;
import com.pleiades.pleione.kakaoprofile.R;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.archive.ArchiveFragment;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.profiles.ProfilesFragment;
import com.pleiades.pleione.kakaoprofile.ui.dialog.DialogRoundFragment;
import com.pleiades.pleione.kakaoprofile.ui.instant.InstantHolder;

import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_MANAGE_ARCHIVE;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_MANAGE_PROFILE;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.DIALOG_ROUND_LIST;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.TAG_MANAGE_ARCHIVE;
import static com.pleiades.pleione.kakaoprofile.ui.dialog.DialogConfig.TAG_MANAGE_PROFILE;
import static com.pleiades.pleione.kakaoprofile.ui.instant.InstantConfig.FROM_ARCHIVE_FRAGMENT;
import static com.pleiades.pleione.kakaoprofile.ui.instant.InstantConfig.FROM_PROFILES_FRAGMENT;

public class FullPageFragment extends Fragment {
    private Context context;
    private int position;

    public static DialogRoundFragment dialogRoundFragment;

    public FullPageFragment(int position) {
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_full_page, container, false);

        // attach full image
        PhotoView photoView = (PhotoView) v.findViewById(R.id.full_profile_image);

        // set full image
        int from = InstantHolder.getFrom(false);
        if(from == FROM_PROFILES_FRAGMENT) {
            photoView.setImageURI(ProfilesFragment.profileList.get(position).getPath());
            photoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // haptic feedback
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                    // dialog
                    String[] messages = getResources().getStringArray(R.array.dialog_manage_profile);

                    dialogRoundFragment = new DialogRoundFragment(DIALOG_ROUND_LIST, DIALOG_MANAGE_PROFILE, position, context, getString(R.string.dialog_manage_profile), messages);
                    dialogRoundFragment.setCancelable(true);
                    try {
                        dialogRoundFragment.show(getActivity().getSupportFragmentManager(), TAG_MANAGE_PROFILE);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
            });
        }
        else if (from == FROM_ARCHIVE_FRAGMENT) {
            photoView.setImageURI(ArchiveFragment.archiveList.get(position).getPath());
            photoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // haptic feedback
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                    // dialog
                    String[] messages = getResources().getStringArray(R.array.dialog_manage_profile_archive);

                    dialogRoundFragment = new DialogRoundFragment(DIALOG_ROUND_LIST, DIALOG_MANAGE_ARCHIVE, position, context, getString(R.string.dialog_manage_profile), messages);
                    dialogRoundFragment.setCancelable(true);
                    try {
                        dialogRoundFragment.show(getActivity().getSupportFragmentManager(), TAG_MANAGE_ARCHIVE);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
            });
        }
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
