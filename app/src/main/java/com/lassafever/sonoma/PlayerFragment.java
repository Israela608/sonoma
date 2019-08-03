package com.lassafever.sonoma;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class PlayerFragment extends Fragment implements View.OnClickListener {
    ImageView albumArt;
    ImageView playButtonBackground;
    ImageView playButton;
    ImageView optionButton;
    ImageView queueButton;
    ImageView favouriteButton;
    ImageView nextButton;
    ImageView previousButton;
    ImageView closeButton;
    ImageView repeatButton;
    ImageView shuffleButton;
    ImageView equalizerButton;
    SeekBar seekBar;
    TextView titleText;
    TextView artistText;
    TextView progressText;
    TextView durationText;

    public static ArrayList<MediaDataPlayer> playerData;
    public static ArrayList<MediaDataPlayerRandom> playerShuffleData;

    String title;
    String artist;
    String path;
    Bitmap art;

    public static int numOfSong = 0;
    public static int currentPosition = 0;
    boolean playMode = true;//Media is playing
    public static boolean shuffleMode = false;

    public static MediaPlayer mediaPlayer;

    int repeatMode = 0;//0 for dont repeat, 1 for repeat one, 2 for repeat all

    private final Handler handler = new Handler();


    private static final int STEP_VALUE = 10000;
    private static final int UPDATE_FREQUENCY = 1000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.player_fragment, container, false);

        albumArt = v.findViewById(R.id.albumArt);
        playButton = v.findViewById(R.id.playButton);
        playButtonBackground = v.findViewById(R.id.playButtonBackground);
        optionButton = v.findViewById(R.id.optionButton);
        queueButton = v.findViewById(R.id.queueButton);
        favouriteButton = v.findViewById(R.id.favouriteButton);
        nextButton = v.findViewById(R.id.nextButton);
        previousButton = v.findViewById(R.id.previousButtton);
        closeButton = v.findViewById(R.id.closeButton);
        repeatButton = v.findViewById(R.id.repeatButton);
        shuffleButton = v.findViewById(R.id.shuffleButtton);
        equalizerButton = v.findViewById(R.id.equaliserButton);
        seekBar = v.findViewById(R.id.seekBar);
        titleText = v.findViewById(R.id.titleText);
        artistText = v.findViewById(R.id.artistText);
        progressText = v.findViewById(R.id.progressText);
        durationText = v.findViewById(R.id.durationText);

        albumArt.setOnClickListener(this);
        playButton.setOnClickListener(this);
        playButtonBackground.setOnClickListener(this);
        optionButton.setOnClickListener(this);
        queueButton.setOnClickListener(this);
        favouriteButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        repeatButton.setOnClickListener(this);
        shuffleButton.setOnClickListener(this);
        equalizerButton.setOnClickListener(this);
        seekBar.setOnClickListener(this);
        titleText.setOnClickListener(this);
        artistText.setOnClickListener(this);
        progressText.setOnClickListener(this);
        durationText.setOnClickListener(this);

        playerData = new ArrayList<>();
        playerShuffleData = new ArrayList<>();


        playerData.clear();
        playerData = MainActivity.playerItems;
        numOfSong = playerData.size();

        //Prepare the shuffle
        //shuffleSong();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(onCompletion);
        mediaPlayer.setOnErrorListener(onError);


        if (MainActivity.firstLaunch) {
            final MediaDataSongs initials = MainActivity.songDataList.get(0);
            title = initials.getTitle();
            artist = initials.getArtist();
            path = initials.getPath();
            art = initials.getAlbumArt();
            titleText.setText(title);
            artistText.setText(artist);
            getAlbumArt();

            ((MainActivity) getActivity()).updateMini(title, artist, art);

        } else {
            final MediaDataPlayer itemList = playerData.get(currentPosition);
            title = itemList.getTitle();
            artist = itemList.getArtist();
            path = itemList.getPath();
            art = itemList.getAlbumArt();
            startSong();
            updateSeekBar();
            titleText.setText(title);
            artistText.setText(artist);
            getAlbumArt();

            ((MainActivity) getActivity()).updateMini(title, artist, art);
        }

        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playButtonBackground:
            case R.id.playButton: {

                if (MainActivity.firstLaunch) {
                    startSong();
                    updateSeekBar();
                    MainActivity.firstLaunch = false;

                } else if (playMode) {
                    pauseSong();
                    playMode = false;
                    playButton.setImageResource(R.drawable.play);
                    ((MainActivity) getActivity()).playMiniButton.setImageResource(R.drawable.play_filled);

                } else {
                    playSong();
                    playMode = true;
                    playButton.setImageResource(R.drawable.pause);
                    ((MainActivity) getActivity()).playMiniButton.setImageResource(R.drawable.pause_filled);
                }
                break;
            }
            case R.id.optionButton: {
                Toast.makeText(getActivity(), "Option", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.queueButton: {
                Toast.makeText(getActivity(), "Queue", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.favouriteButton: {
                Toast.makeText(getActivity(), "Favourite", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.nextButton: {
                nextSong();
                MainActivity.firstLaunch = false;
                break;
            }
            case R.id.previousButtton: {
                previousSong();
                MainActivity.firstLaunch = false;
                break;
            }
            case R.id.closeButton: {
                Toast.makeText(getActivity(), "Close", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            }
            case R.id.repeatButton: {

                if (repeatMode == 0) {
                    repeatMode = 2;
                    repeatButton.setImageResource(R.drawable.repeat);
                } else if (repeatMode == 2) {
                    repeatMode = 1;
                    repeatButton.setImageResource(R.drawable.repeat_one);
                } else {
                    repeatMode = 0;
                    repeatButton.setImageResource(R.drawable.repeat_off);
                }
                break;
            }
            case R.id.shuffleButtton: {

                if (!shuffleMode) {
                    shuffleMode = true;
                    shuffleSong();
                    shuffleButton.setImageResource(R.drawable.shuffle);
                } else {
                    shuffleMode = false;
                    shuffleButton.setImageResource(R.drawable.shuffle_off);
                }
                break;
            }
            case R.id.equaliserButton: {
                Toast.makeText(getActivity(), "Equalizer", Toast.LENGTH_SHORT).show();

                break;
            }
        }

    }


    public void startSong() {
        seekBar.setProgress(0);
        ((MainActivity) getActivity()).progressBar.setProgress(0);

        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();

            mediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }


        playMode = true;
        playButton.setImageResource(R.drawable.pause);
        ((MainActivity) getActivity()).playMiniButton.setImageResource(R.drawable.pause_filled);

        updatePosition();
    }

    public void playSong() {
        mediaPlayer.start();
    }

    public void pauseSong() {
        mediaPlayer.pause();
    }

    public void nextSong() {
        if (!shuffleMode) { // Shuffle mode is off
            if (currentPosition < numOfSong - 1) {//If the song playing is not the last song
                currentPosition++;//Move to the next song
                updatePlayNormal();
            } else {//If the song playing is the last song
                currentPosition = 0;//reset to the first song
                updatePlayNormal();
            }
        } else { // Shuffle mode is on
            if (currentPosition < numOfSong - 1) {//If the song playing is not the last song
                currentPosition++;//Move to the next song
                updatePlayRandom();
            } else {//If the song playing is the last song
                currentPosition = 0;//reset to the first song
                updatePlayRandom();
            }
        }

        titleText.setText(title);
        artistText.setText(artist);
        getAlbumArt();
        updateSeekBar();
        ((MainActivity) getActivity()).updateMini(title, artist, art);
    }


    public void previousSong() {
        if (!shuffleMode) { // Shuffle mode is off
            if (currentPosition > 0) {//If the song playing is not the first song
                currentPosition--;//Move to the previous song
                updatePlayNormal();
            } else {//If the song playing is the first song
                currentPosition = numOfSong - 1;//Move to the last song
                updatePlayNormal();
            }
        } else { // Shuffle mode is on
            if (currentPosition > 0) {//If the song playing is not the first song
                currentPosition--;//Move to the previous song
                updatePlayRandom();
            } else {//If the song playing is the first song
                currentPosition = numOfSong - 1;//Move to the last song
                updatePlayRandom();
            }
        }

        titleText.setText(title);
        artistText.setText(artist);
        getAlbumArt();
        updateSeekBar();
        ((MainActivity) getActivity()).updateMini(title, artist, art);
    }

    public void updatePlayNormal() {
        final MediaDataPlayer itemList = playerData.get(currentPosition);
        title = itemList.getTitle();
        artist = itemList.getArtist();
        path = itemList.getPath();
        art = itemList.getAlbumArt();
        Log.d("my_log", "position = " + currentPosition);
        startSong();
    }

    public void updatePlayRandom() {
        final MediaDataPlayerRandom itemList = playerShuffleData.get(currentPosition);
        title = itemList.getTitle();
        artist = itemList.getArtist();
        path = itemList.getPath();
        art = itemList.getAlbumArt();
        Log.d("my_log", "position = " + currentPosition);
        startSong();
    }


    public void shuffleSong(){
        currentPosition = 0;

        playerShuffleData.clear();
        for (int i=0; i<playerData.size(); i++){
            final MediaDataPlayer items = playerData.get(i);
            playerShuffleData.add((new MediaDataPlayerRandom(items.getTitle(), items.getArtist(), items.getAlbum(), items.getPath(), items.getDuration(), items.getAlbumId(), items.getComposer(), items.getAlbumArt())));
        }

        Collections.shuffle(playerShuffleData);
    }

    public void shuffleSongAndPlay(){
        shuffleSong();

        final MediaDataPlayerRandom itemList = playerShuffleData.get(currentPosition);
        title = itemList.getTitle();
        artist = itemList.getArtist();
        path = itemList.getPath();
        art = itemList.getAlbumArt();
        titleText.setText(title);
        artistText.setText(artist);
        getAlbumArt();
        startSong();
        updateSeekBar();
        ((MainActivity) getActivity()).updateMini(title, artist, art);
    }

    public void endOfTheSong() {
        if (repeatMode == 1) { // currently repeat one song
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        } else if (repeatMode == 2) { // currently repeat all songs
            nextSong();
        } else { // currently no repeat

            if (currentPosition != MainActivity.songDataList.size() - 1) {//If the present song is not the last song
                nextSong();
            } else {//If the last song
                if (!shuffleMode) {//And there is no shuffle
                    //Move to the first song and pause the MediaPlayer
                    nextSong();
                    pauseSong();
                    playMode = false;
                    playButton.setImageResource(R.drawable.play_big);
                    ((MainActivity) getActivity()).playMiniButton.setImageResource(R.drawable.play_filled);

                } else {//If there is shuffle
                    shuffleSongAndPlay();
                }
            }
        }
    }

    public void updateSeekBar() {
        //SEEK BAR FOR CONTROLLING THE AUDIO SEEK
        seekBar.setMax(mediaPlayer.getDuration());//set the maximum of seekBar to the duration of the audio
        ((MainActivity) getActivity()).progressBar.setMax(mediaPlayer.getDuration());

        String durationTimer = milliSecondsToTimer(mediaPlayer.getDuration());
        durationText.setText(durationTimer);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {//i represents the progress of the bar according to the audio progress. b represents the user's manipulation of the bar
                if (b) {//the if(b) will make the audio not to stutter or lag on every change of the Timer period
                    mediaPlayer.seekTo(i);

                    //Update the progressText while the SeekBar is being tampered with
                    String progressTimer = milliSecondsToTimer(mediaPlayer.getCurrentPosition());
                    progressText.setText(progressTimer);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //mediaPlayer.pause();//pauses the audio when you want to seek
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //mediaPlayer.start();//resume or play the audio when you stop seeking
            }
        });//END OF AUDIO SEEK BAR
    }


    private final Runnable updatePositionRunnable = new Runnable() {
        public void run() {
            updatePosition();
        }
    };


    private void updatePosition() {
        handler.removeCallbacks(updatePositionRunnable);

        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        ((MainActivity) getActivity()).progressBar.setProgress(mediaPlayer.getCurrentPosition());

        //Update the progressText
        String progressTimer = milliSecondsToTimer(mediaPlayer.getCurrentPosition());
        progressText.setText(progressTimer);

        handler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);
    }


    public String milliSecondsToTimer(long milliseconds) {
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


    public void getAlbumArt() {
        if (art != null) {
            albumArt.setImageBitmap(art);
        } else {
            albumArt.setImageResource(R.drawable.album_grey_big);
        }
    }


    private MediaPlayer.OnCompletionListener onCompletion = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            endOfTheSong();
        }
    };


    private MediaPlayer.OnErrorListener onError = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {

            return false;
        }
    };


    @Override
    public void onDestroy() {
        handler.removeCallbacks(updatePositionRunnable);

        super.onDestroy();

        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();

        mediaPlayer = null;
    }
}
