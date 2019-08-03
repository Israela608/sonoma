package com.lassafever.sonoma;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class TabSongsFragment extends Fragment {
    private static final String TAG = "TabSongsFragment";

    RecyclerView recyclerView;
    AdapterSongs adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_songs_fragment, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //dataList = new ArrayList<>();

        //requestRead();
        adapter = new AdapterSongs(MainActivity.songDataList, getActivity());
        recyclerView.setAdapter(adapter);

        return v;
    }


    /*void getSong(){
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//Get the Audio
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";//Some audio may be explicitly marked as not being music
        String sortOrder = "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC";//Order by ascending whether lower or uppercase
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        //Uri and Cursor to get the Album Arts
        Uri albumUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor albumCursor;

        int count = cursor.getCount();//Number of songs
        audioPath = new String[count];//'audioPath' Array of 'count' number of elements
        titleList = new String[count];
        albumArtList = new String[count];
        int i = 0;

        if (cursor.moveToFirst()){
            //int durationId = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do {
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String composer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER));

                audioPath[i] = data;
                titleList[i] = title;

                //Now the Album Arts
                String albumSelection = MediaStore.Audio.Albums._ID + "=" + albumId;//Select the art that equals the album ID for the particular music
                albumCursor = contentResolver.query(albumUri, null, albumSelection, null, null);

                String albumArt = null;

                if(albumCursor != null && albumCursor.moveToFirst()){
                    albumArt = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                    albumArtList[i] = albumArt;

                    albumCursor.close();
                }

                i++;

                dataList.add(new MediaData(title, artist, album, data, duration,  albumId, composer, albumArt));
            }while (cursor.moveToNext());
        }

        cursor.close();
    }


    public void requestRead() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            getSong();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSong();
            } else {
                // Permission Denied
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }*/
}