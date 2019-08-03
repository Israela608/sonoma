package com.lassafever.sonoma;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainTabsActivity extends Fragment implements View.OnClickListener {
    public static final String TAG = "TABS";

    //ViewPager and Fragments loader variables for tabs layout
    private SectionPageAdapter mSectionpageAdapter;
    private ViewPager mViewPager;

    TabLayout tabLayout;
    Toolbar toolbar;

    FloatingActionButton addFab;
    FloatingActionButton shuffleFab;

    //Tab Icons
    private int[] tabIcons = {R.drawable.home, R.drawable.all_song, R.drawable.album, R.drawable.profile};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.activity_tabs_main, container, false);

        //VIEWPAGER AND OTHER UI SETUP for tablayout
        mSectionpageAdapter = new SectionPageAdapter(getFragmentManager());
        //Set up the ViewPager with the sections adapter
        mViewPager = view.findViewById(R.id.container);
        setUpViewPager(mViewPager);

        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //Set up the tab icons
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);

        final ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);

        /*tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //int tabIconColor = ContextCompat.getColor(getContext(), R.color.white);
                //tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //int tabIconColor = ContextCompat.getColor(getContext(), R.color.blue);
                //tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/



        toolbar = view.findViewById(R.id.toolBar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addFab = view.findViewById(R.id.addFab);
        shuffleFab = view.findViewById(R.id.shuffleFab);

        addFab.setOnClickListener(this);
        shuffleFab.setOnClickListener(this);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.i("PageSelected", Integer.toString(i));

                switch (i){
                    case 0:
                        toolbar.setTitle("Sonoma");
                        break;
                    case 1:
                        toolbar.setTitle("Songs");
                        break;
                    case 2:
                        toolbar.setTitle("Albums");
                        break;
                    case 3:
                        toolbar.setTitle("Artists");
                        break;
                }

                animateFab(i);


                ViewGroup vgTab = (ViewGroup) vg.getChildAt(i);
                vgTab.setScaleX(0.8f);
                vgTab.setScaleY(0.8f);

                vgTab.animate().scaleX(1.2f).scaleY(1.2f).start();

                for (int j=0; j<4; j++){
                    if (j == i){
                        continue;
                    }
                    ViewGroup vgTabOthers = (ViewGroup) vg.getChildAt(j);
                    vgTabOthers.setScaleX(0.85f);
                    vgTabOthers.setScaleY(0.85f);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        return view;
    }

    public void setUpViewPager(ViewPager viewPager){
        SectionPageAdapter adapter = new SectionPageAdapter(getChildFragmentManager());

        adapter.addFragment(new TabHomeFragment(), "Home");
        adapter.addFragment(new TabSongsFragment(), "Songs");
        adapter.addFragment(new TabAlbumFragment(), "Albums");
        adapter.addFragment(new TabArtistFragment(), "Artist");
        viewPager.setAdapter(adapter);
    }


    private void animateFab(int position){
        switch (position){
            case 0:
                addFab.show();
                shuffleFab.hide();
                break;
            case 1:
                addFab.hide();
                shuffleFab.show();
                break;
           default:
               addFab.hide();
               shuffleFab.hide();
               break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addFab:
                Toast.makeText(getActivity(), "Add Playlist", Toast.LENGTH_SHORT).show();
                Log.i("CLICKED", "AddFab");
                break;

            case R.id.shuffleFab:
                Log.i("CLICKED", "Shuffle");

                MainActivity.playerItems.clear();
                for (int i=0; i < MainActivity.songDataList.size(); i++){
                    final MediaDataSongs items = MainActivity.songDataList.get(i);
                    MainActivity.playerItems.add((new MediaDataPlayer(items.getTitle(), items.getArtist(), items.getAlbum(), items.getPath(), items.getDuration(), items.getAlbumId(), items.getComposer(), items.getAlbumArt())));
                }

                MainActivity.firstLaunch = false;

                PlayerFragment playerFragment = (PlayerFragment) getActivity().getSupportFragmentManager().findFragmentByTag("PLAYER");
                PlayerFragment.numOfSong = MainActivity.playerItems.size();//just a reminder
                PlayerFragment.shuffleMode = true;
                playerFragment.shuffleSongAndPlay();
                playerFragment.shuffleButton.setImageResource(R.drawable.shuffle);

        }
    }
}
