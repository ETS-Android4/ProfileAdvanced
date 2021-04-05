package com.pleiades.pleione.kakaoprofile.cache;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.documentfile.provider.DocumentFile;

import com.pleiades.pleione.kakaoprofile.R;
import com.pleiades.pleione.kakaoprofile.prefs.PrefsController;
import com.pleiades.pleione.kakaoprofile.ui.activity.HideActivity;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.archive.ArchiveFragment;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.archive.ArchiveProfile;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.profiles.Profile;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.fragment.profiles.ProfilesFragment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

import static com.pleiades.pleione.kakaoprofile.cache.CacheConfig.CACHE_CHANGED;
import static com.pleiades.pleione.kakaoprofile.cache.CacheConfig.CACHE_NOT_CHANGED;
import static com.pleiades.pleione.kakaoprofile.cache.CacheConfig.CACHE_NOT_FOUND;
import static com.pleiades.pleione.kakaoprofile.cache.CacheConfig.archiveCacheFlag;
import static com.pleiades.pleione.kakaoprofile.cache.CacheConfig.profileCacheFlag;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_HIDE_NAME_LIST;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_NOT_NEW_PROFILE_NAME_LIST;

public class CacheController {
    private Context context;

    // constructor
    public CacheController(Context context) {
        this.context = context;
    }

    // public
    public boolean saveProfileCache(Profile kakaoprofile) {
        // find save directory
        // Pictures, kakaoprofile
        String[] directories = {context.getString(R.string.directory_default_pictures), context.getString(R.string.directory_kakao_profile_save)};
        File storageFile = Environment.getExternalStorageDirectory();
        DocumentFile storageDocument = DocumentFile.fromFile(storageFile);
        for (String findDocument : directories) {
            if (storageDocument.findFile(findDocument) == null)
                storageDocument.createDirectory(findDocument);
            storageDocument = storageDocument.findFile(findDocument);
        }

        // create file
        String fileName = kakaoprofile.getName() + ".png"; // concat format
        if (storageDocument.findFile(fileName) == null)
            storageDocument.createFile("image/*", fileName);
        DocumentFile saveProfile = storageDocument.findFile(fileName);

        // write
        return writeFile(kakaoprofile.getPath(), saveProfile.getUri());
    }

    public boolean removeArchiveCache(Uri uri) {
        File deleteArchiveProfile = new File(uri.getPath());
        return deleteArchiveProfile.delete();
    }

    public boolean copyProfileCache(Profile kakaoprofile) {
        // archive directory
        String archiveDirectoryName = context.getString(R.string.cache_archive);
        DocumentFile archiveDirectory = findCacheDirectory(false);
        if (archiveDirectory.findFile(archiveDirectoryName) == null)
            archiveDirectory.createDirectory(archiveDirectoryName);
        archiveDirectory = archiveDirectory.findFile(archiveDirectoryName);

        // create file
        String fileName = kakaoprofile.getName();
        if (archiveDirectory.findFile(fileName) == null)
            archiveDirectory.createFile("image/*", fileName);
        DocumentFile saveProfile = archiveDirectory.findFile(fileName);

        // write
        return writeFile(kakaoprofile.getPath(), saveProfile.getUri());
    }

    public void initializeCache() {
        initializeCache(true, true);
    }

    public void initializeCache(boolean initializeProfiles, boolean initializeArchive) {
        if (initializeProfiles) {
            DocumentFile kakaoCacheDirectory = findCacheDirectory(true);
            profileCacheFlag = loadProfilesCache(kakaoCacheDirectory);
        }

        if (initializeArchive) {
            DocumentFile cacheDirectory = findCacheDirectory(false);
            archiveCacheFlag = loadArchiveCache(cacheDirectory);
        }
    }

    public boolean clearTemporaryDirectory() {
        DocumentFile kakaoprofileDirectory = findCacheDirectory(true).findFile(context.getString(R.string.cache_mini));

        // cache not found
        if (kakaoprofileDirectory == null)
            return false;

        for (DocumentFile file : kakaoprofileDirectory.listFiles())
            file.delete();
        return true;
    }

    // private

    private boolean writeFile(Uri origin, Uri destination) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(origin);
            OutputStream outputStream = context.getContentResolver().openOutputStream(destination);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

            int read = 0;
            while ((read = bufferedInputStream.read()) != -1)
                bufferedOutputStream.write(read);

            outputStream.flush();
            bufferedOutputStream.flush();

            inputStream.close();
            outputStream.close();
            bufferedInputStream.close();
            bufferedOutputStream.close();

            // scan media
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(destination);
            context.sendBroadcast(intent);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private DocumentFile findCacheDirectory(boolean isKakaoTalk) {
        String[] directories;

        // kakao talk or profile
        if (isKakaoTalk)
            directories = context.getResources().getStringArray(R.array.cache_path_kakao_talk);
        else
            directories = context.getResources().getStringArray(R.array.cache_path_kakao_profile);

        File storageDirectory = Environment.getExternalStorageDirectory();
        DocumentFile storageDocument = DocumentFile.fromFile(storageDirectory);

        for (String findDocument : directories) {
            if (storageDocument.findFile(findDocument) == null)
                storageDocument.createDirectory(findDocument);
            storageDocument = storageDocument.findFile(findDocument);
        }

        return storageDocument;
    }

    private int loadProfilesCache(DocumentFile kakaoCacheDirectory) {
        // initialize new profile list
        ArrayList<Profile> profileList = new ArrayList<Profile>();

        // find kakao mini profile directory (contains profile data)
        DocumentFile kakaoprofileDirectory = kakaoCacheDirectory.findFile(context.getString(R.string.cache_mini));

        // cache not found
        if (kakaoprofileDirectory == null) {
            ProfilesFragment.profileList = profileList;
            return CACHE_NOT_FOUND;
        }

        // sub directories
        DocumentFile[] kakaoSubDirectories = kakaoprofileDirectory.listFiles();

        // load profile caches
        for (DocumentFile kakaoSubDirectory : kakaoSubDirectories) {
            if (kakaoSubDirectory.isDirectory()) {
                DocumentFile[] kakaoprofiles = kakaoSubDirectory.listFiles();
                for (DocumentFile kakaoprofile : kakaoprofiles) {
                    Uri pathUri = kakaoprofile.getUri();

                    // add new profile
                    if(calcImageVolume(pathUri) != 0){
                        Profile newProfile = new Profile(kakaoprofile.getName(), pathUri, kakaoprofile.lastModified());
                        profileList.add(newProfile);
                    }

                }
            }
        }

        // sort profile list
        Collections.sort(profileList);

        // check hide
        ArrayList<String> hideNameList = PrefsController.getListPrefs(KEY_HIDE_NAME_LIST);
        ArrayList<Profile> hideList = new ArrayList<Profile>();
        if (hideNameList != null) {
            for (String hideProfileName : hideNameList) {
                for (int i = 0; i < profileList.size(); i++) {
                    if (profileList.get(i).getName().equals(hideProfileName)) {
                        Profile hideProfile = profileList.get(i);
                        hideProfile.setNew(false);
                        hideList.add(hideProfile);
                        profileList.remove(i);
                        break;
                    }
                }
            }
        }
        HideActivity.hideList = hideList;


        // check new
        ArrayList<String> notNewNameList = PrefsController.getListPrefs(KEY_NOT_NEW_PROFILE_NAME_LIST);
        Integer newFlag = null;

        if (notNewNameList == null) { // first caching
            notNewNameList = new ArrayList<String>();
            PrefsController.setListPrefs(KEY_NOT_NEW_PROFILE_NAME_LIST, notNewNameList);
        } else
            newFlag = 1; // not null

        for (int i = 0; i < profileList.size(); i++) {
            if (newFlag == null) { // first caching
                profileList.get(i).setNew(false);
                notNewNameList.add(profileList.get(i).getName());
            } else {
                Profile profile = profileList.get(i);
                for (String notNewName : notNewNameList) {
                    if (notNewName.equals(profile.getName())) {
                        profileList.get(i).setNew(false);
                    }
                }
            }
        }
        PrefsController.setListPrefs(KEY_NOT_NEW_PROFILE_NAME_LIST, notNewNameList); // if first caching, not new list is added.

        // existing profile list is null or not contain all of new profile list
        if ((ProfilesFragment.profileList == null) || (!ProfilesFragment.profileList.equals(profileList))) {
            ProfilesFragment.profileList = profileList;
            return CACHE_CHANGED;
        } else {
            return CACHE_NOT_CHANGED;
        }
    }

    private int loadArchiveCache(DocumentFile cacheDirectory) {
        // initialize new archive list
        ArrayList<ArchiveProfile> archiveList = new ArrayList<ArchiveProfile>();

        // find archive directory (contains profile data)
        DocumentFile archiveDirectory = cacheDirectory.findFile(context.getString(R.string.cache_archive));

        // cache not found
        if (archiveDirectory == null) {
            ArchiveFragment.archiveList = archiveList;
            return CACHE_NOT_FOUND;
        }

        // sub archive profiles
        DocumentFile[] archiveProfiles = archiveDirectory.listFiles();

        // load archive caches
        for (DocumentFile archiveProfile : archiveProfiles) {
            Uri pathUri = archiveProfile.getUri();

            // add new profile
            if(calcImageVolume(pathUri) != 0){
                ArchiveProfile newProfile = new ArchiveProfile(archiveProfile.getName(), pathUri, archiveProfile.lastModified());
                archiveList.add(newProfile);
            }
        }

        // sort profile list
        Collections.sort(archiveList);

        // existing profile list is null or not contain all of new profile list
        if ((ArchiveFragment.archiveList == null) || (!ArchiveFragment.archiveList.equals(archiveList))) {
            ArchiveFragment.archiveList = archiveList;
            return CACHE_CHANGED;
        } else {
            return CACHE_NOT_CHANGED;
        }
    }

    private long calcImageVolume(Uri pathUri) {
        String pathString = pathUri.getPath();

        long size = 0;
        if (pathString != null) {
            size = new File(pathString).length();
            size = size / 1024; // get kilobyte
        }

        return size;
    }

}
