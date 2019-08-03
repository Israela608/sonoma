package com.lassafever.sonoma;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class TabAlbumFragment extends Fragment {
    private static  final String TAG  = "TabAlbumFragment";

    RecyclerView recyclerView;
    AdapterAlbum adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.tab_album_fragment, container, false);

        // Set up the RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));

        adapter = new AdapterAlbum(MainActivity.albumDataList, getActivity());
        recyclerView.setAdapter(adapter);

        int largePadding = 6;
        int smallPadding = 6;
        recyclerView.addItemDecoration(new GridItemDecoration(largePadding, smallPadding));

        return view;
    }
}
