package com.lassafever.sonoma;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Song Data Array
    public static ArrayList<MediaDataSongs> songDataList;

    //Album Data Array
    public static ArrayList<MediaDataAlbum> albumDataList;

    //Artist Data Array
    public static ArrayList<MediaDataArtist> artistDataList;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    //BottomSheet implementation
    public BottomSheetBehavior bottomSheetBehavior;

    ConstraintLayout miniPlayer;
    FrameLayout playerFragment;

    TextView songMiniText;
    TextView artistMiniText;
    ImageView albumMiniArt;
    ImageView previousMiniButton;
    ImageView nextMiniButton;
    ImageView playMiniButton;
    ProgressBar progressBar;


    //This is for the player
    public static ArrayList<MediaDataPlayer> playerItems;

    public static boolean firstLaunch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//always portrait


        songDataList = new ArrayList<>();
        albumDataList = new ArrayList<>();
        artistDataList = new ArrayList<>();
        playerItems = new ArrayList<>();

        //Get the song data
        requestRead();


        playerItems.clear();
        for (int i = 0; i < songDataList.size(); i++) {
            final MediaDataSongs items = songDataList.get(i);
            playerItems.add((new MediaDataPlayer(items.getTitle(), items.getArtist(), items.getAlbum(), items.getPath(), items.getDuration(), items.getAlbumId(), items.getComposer(), items.getAlbumArt())));
        }


        //Mini Player
        miniPlayer = findViewById(R.id.miniPlayer);
        playerFragment = findViewById(R.id.player_container);
        songMiniText = findViewById(R.id.songMiniText);
        songMiniText.setSelected(true);
        artistMiniText = findViewById(R.id.artistMiniText);
        albumMiniArt = findViewById(R.id.miniAlbumArt);
        playMiniButton = findViewById(R.id.miniPlayButton);
        previousMiniButton = findViewById(R.id.miniPreviousButton);
        nextMiniButton = findViewById(R.id.miniNextButton);
        progressBar = findViewById(R.id.progressBar);

        miniPlayer.setOnClickListener(this);
        playMiniButton.setOnClickListener(this);
        previousMiniButton.setOnClickListener(this);
        nextMiniButton.setOnClickListener(this);


        //BottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheet));
        bottomSheetBehavior.setPeekHeight(120);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                Log.i("STATE", Integer.toString(i));
                switch (i) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        //constraintLayout.setAlpha(1f);
                        //textView.setText("Collapsed");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        //textView.setText("Dragging...");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        //constraintLayout.setAlpha(0);
                        //textView.setText("Expanded");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        //textView.setText("Hidden");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        // textView.setText("Settling");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                miniPlayer.setAlpha(1 - v);
                playerFragment.setAlpha(v);
                Log.i("SLIDING NUMBER", Double.toString(v));
            }
        });


        if (savedInstanceState == null) {
            //For the fragment_container
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            FragmentAlbum fragmentAlbum = new FragmentAlbum();
            fragmentTransaction.add(R.id.fragment_container, fragmentAlbum, "ALBUMLIST");

            FragmentArtist fragmentArtist = new FragmentArtist();
            fragmentTransaction.add(R.id.fragment_container, fragmentArtist, "ARTISTLIST");

            MainTabsActivity mainTabsActivity = new MainTabsActivity();
            fragmentTransaction.add(R.id.fragment_container, mainTabsActivity, "TABS");

            fragmentTransaction.commit();


            //for the player fragment
            FragmentManager playerFm = getSupportFragmentManager();
            FragmentTransaction playerFt = playerFm.beginTransaction();

            PlayerFragment playerFragment = new PlayerFragment();
            playerFt.add(R.id.player_container, playerFragment, "PLAYER");
            playerFt.commit();
        }
    }


    void getSong() {
        ContentResolver contentResolver = this.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//Get the Audio
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";//Some audio may be explicitly marked as not being music
        String sortOrder = "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC";//Order by ascending whether lower or uppercase
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        //Uri and Cursor to get the Album Arts
        Uri albumUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor albumCursor;


        //Album Array
        ArrayList<String> albumsAbc = new ArrayList<>();

        //Artist Array
        ArrayList<String> artistAbc = new ArrayList<>();


        if (cursor.moveToFirst()) {
            //int durationId = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do {
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String composer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER));


                //Some artist edit
                if (artist.matches("<unknown>")){
                    artist = "Unknown artist";
                }


                //Now the Album Arts
                String albumSelection = MediaStore.Audio.Albums._ID + "=" + albumId;//Select the art that equals the album ID for the particular music
                albumCursor = contentResolver.query(albumUri, null, albumSelection, null, null);

                String albumArt = null;

                if (albumCursor != null && albumCursor.moveToFirst()) {
                    albumArt = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));

                    albumCursor.close();
                }

                //Convert the albumArt into a bitmap
                Bitmap albumArtBitmap = null;

                Bitmap bitmap;

                try {
                    bitmap = BitmapFactory.decodeFile(albumArt);/////BUG//// java.lang.OutOfMemoryError: Failed to allocate a 6553612 byte allocation with 5010104 free bytes and 4MB until OOM
                }catch (Exception e){
                    e.printStackTrace();

                    bitmap = decodeFileMachine(albumArt);
                }

                if (bitmap != null) {
                    albumArtBitmap = bitmap;
                }

                songDataList.add(new MediaDataSongs(title, artist, album, data, duration, albumId, composer, albumArtBitmap));


                // Add albums
                albumsAbc.add(album);

                //Add artists
                artistAbc.add(artist);


            } while (cursor.moveToNext());
        }


        //FOR ALBUM

        //Clear duplicates
        Set<String> setAlb = new HashSet<>(albumsAbc);
        albumsAbc.clear();
        albumsAbc.addAll(setAlb);

        //Arrange in alphabetical order
        Collections.sort(albumsAbc, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });


        for (String alb : albumsAbc) {
            for (int i = 0; i < songDataList.size(); i++) {
                final MediaDataSongs items = songDataList.get(i);
                if (alb.equals(items.getAlbum())) {
                    albumDataList.add(new MediaDataAlbum(items.getAlbum(), items.getArtist(), items.getAlbumId(), items.getAlbumArt()));
                    break;
                }
            }
        }


        //FOR ARTIST

        //Calculate the number of songs of each artist
        Map<String, Integer> map = new HashMap<>();

        for (String name : artistAbc) {
            if (map.containsKey(name)) {
                map.put(name, map.get(name) + 1);
            } else {
                map.put(name, 1);
            }
        }

        //Clear duplicates
        Set<String> setArts = new HashSet<>(artistAbc);
        artistAbc.clear();
        artistAbc.addAll(setArts);

        //Re-Arrange in alphabetical order
        Collections.sort(artistAbc, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });


        for (String artist : artistAbc) {
            for (int i = 0; i < songDataList.size(); i++) {
                final MediaDataSongs items = songDataList.get(i);
                if (artist.equals(items.getArtist())) {
                    artistDataList.add(new MediaDataArtist(items.getArtist(), items.getAlbumArt(), map.get(items.getArtist())));
                    break;
                }
            }
        }

        cursor.close();
    }


    //Decode Bitmap
    public static Bitmap decodeFileMachine(String f) {
        Bitmap b = null;
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        try {
            BitmapFactory.decodeFile(f, o);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // In Samsung Galaxy S3, typically max memory is 64mb
        // Camera max resolution is 3264 x 2448, times 4 to get Bitmap memory of 30.5mb for one bitmap
        // If we use scale of 2, resolution will be halved, 1632 x 1224 and x 4 to get Bitmap memory of 7.62mb
        // We try use 25% memory which equals to 16mb maximum for one bitmap
        long maxMemory = Runtime.getRuntime().maxMemory();
        int maxMemoryForImage = (int) (maxMemory / 100 * 25);

        // Refer to
        // http://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
        // A full screen GridView filled with images on a device with
        // 800x480 resolution would use around 1.5MB (800*480*4 bytes)
        // When bitmap option's inSampleSize doubled, pixel height and
        // weight both reduce in half
        int scale = 1;
        while ((o.outWidth / scale) * (o.outHeight / scale) * 4 > maxMemoryForImage)
            scale *= 2;

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;

        try {
            b = BitmapFactory.decodeFile(f, o2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }


    public void requestRead() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            getSong();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSong();
            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    //Update the Mini Player
    public void updateMini(String title, String artist, Bitmap albumArt) {

        songMiniText.setText(title);
        artistMiniText.setText(artist);

        if (albumArt != null) {
            albumMiniArt.setImageBitmap(albumArt);
        } else {
            albumMiniArt.setImageResource(R.drawable.album);
        }
    }


    @Override
    public void onClick(View v) {

        PlayerFragment playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("PLAYER");

        switch (v.getId()) {
            case R.id.miniPlayer: {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            }
            case R.id.miniPreviousButton: {
                playerFragment.previousSong();
                firstLaunch = false;
                break;
            }
            case R.id.miniNextButton: {
                playerFragment.nextSong();
                firstLaunch = false;
                break;
            }
            case R.id.miniPlayButton: {

                if (firstLaunch) {
                    playerFragment.startSong();
                    playerFragment.updateSeekBar();
                    firstLaunch = false;

                } else if (playerFragment.playMode) {
                    playerFragment.pauseSong();
                    playerFragment.playMode = false;
                    playMiniButton.setImageResource(R.drawable.play_filled);
                    playerFragment.playButton.setImageResource(R.drawable.play);

                } else {
                    playerFragment.playSong();
                    playerFragment.playMode = true;
                    playMiniButton.setImageResource(R.drawable.pause_filled);
                    playerFragment.playButton.setImageResource(R.drawable.pause);
                }
                break;
            }
        }
    }


    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("position", PlayerFragment.mediaPlayer.getCurrentPosition());
        outState.putBoolean("isPlaying", PlayerFragment.mediaPlayer.isPlaying());

        if (PlayerFragment.mediaPlayer.isPlaying()) {
            PlayerFragment.mediaPlayer.pause();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        int pos = savedInstanceState.getInt("position");
        PlayerFragment.mediaPlayer.seekTo(pos);
        if (savedInstanceState.getBoolean("isPlaying")){
            PlayerFragment.mediaPlayer.start();
        }
        //
    }*/

    //Convert milliseconds time to displayable format
    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }


    public interface OnBackClickListener {
        boolean onBackClick();
    }

    public OnBackClickListener onBackClickListener;

    public void setOnBackClickListener(OnBackClickListener onBackClickListener) {
        this.onBackClickListener = onBackClickListener;
    }

    @Override
    public void onBackPressed() {
        if (onBackClickListener != null && onBackClickListener.onBackClick()) {
            return;
        }
        super.onBackPressed();
    }
}