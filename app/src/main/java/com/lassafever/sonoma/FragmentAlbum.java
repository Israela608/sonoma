package com.lassafever.sonoma;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class FragmentAlbum extends Fragment {

    RecyclerView recyclerView;
    AdapterAlbumSongList adapter;

    ArrayList<MediaDataSongs> data = MainActivity.songDataList;

    public static ArrayList<MediaDataAlbumSongList> albumSongDataList;

    //Variables for getting the music components
    public static String albumName;

    Toolbar toolbar;
    FloatingActionButton shuffleButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_album, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Toast.makeText(getContext(), "ALBUM: " + albumName, Toast.LENGTH_SHORT).show();

        albumSongDataList = new ArrayList<>();

        getAlbumSongs();

        adapter = new AdapterAlbumSongList(albumSongDataList, getActivity());
        recyclerView.setAdapter(adapter);

        toolbar = view.findViewById(R.id.toolBar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(albumName);


        shuffleButton = view.findViewById(R.id.shuffleButton);
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("CLICKED", "Shuffle");

                MainActivity.playerItems.clear();
                for (int i=0; i < albumSongDataList.size(); i++){
                    final MediaDataAlbumSongList items = albumSongDataList.get(i);
                    MainActivity.playerItems.add((new MediaDataPlayer(items.getTitle(), items.getArtist(), items.getAlbum(), items.getPath(), items.getDuration(), items.getAlbumId(), items.getComposer(), items.getAlbumArt())));
                }

                MainActivity.firstLaunch = false;

                PlayerFragment playerFragment = (PlayerFragment) getActivity().getSupportFragmentManager().findFragmentByTag("PLAYER");
                PlayerFragment.numOfSong = MainActivity.playerItems.size();//just a reminder
                PlayerFragment.shuffleMode = true;
                playerFragment.shuffleSongAndPlay();
                playerFragment.shuffleButton.setImageResource(R.drawable.shuffle);
            }
        });


        ((MainActivity) getActivity()).setOnBackClickListener(new MainActivity.OnBackClickListener() {
            @Override
            public boolean onBackClick() {
                /*if () {
                    return false;
                }*/
                android.support.v4.app.FragmentManager fragmentMg = ((MainActivity) getActivity()).getSupportFragmentManager();
                fragmentMg.beginTransaction().show(fragmentMg.findFragmentByTag("TABS")).commit();
                fragmentMg.beginTransaction().hide(fragmentMg.findFragmentByTag("ALBUMLIST")).commit();

                return true;//means back press wont work
            }
        });

        return view;
    }

    public void getAlbumSongs(){
        for (int i=0; i<MainActivity.songDataList.size(); i++){
            final MediaDataSongs itemList = data.get(i);

            if (itemList.getAlbum().equals(albumName)){
                albumSongDataList.add(new MediaDataAlbumSongList(itemList.getTitle(), itemList.getArtist(), itemList.getAlbum(), itemList.getPath()
                        , itemList.getDuration(), itemList.getAlbumId(), itemList.getComposer(), itemList.getAlbumArt()));
            }
        }
    }

}
