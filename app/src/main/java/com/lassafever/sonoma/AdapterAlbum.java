package com.lassafever.sonoma;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

//import static android.support.constraint.Constraints.TAG;

public class AdapterAlbum extends RecyclerView.Adapter<AdapterAlbum.ViewHolder> {
    private ArrayList<MediaDataAlbum> data;
    private Context context;


    public AdapterAlbum(ArrayList<MediaDataAlbum> data, Context context){
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterAlbum.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.album_card, viewGroup, false);
        return new AdapterAlbum.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final AdapterAlbum.ViewHolder viewHolder, int i) {
        final MediaDataAlbum itemList = data.get(i);
        viewHolder.albumText.setText(itemList.getAlbum());
        viewHolder.artistText.setText(itemList.getArtist());

        Bitmap bitmap = itemList.getAlbumArt();
        if (bitmap!=null) {
            viewHolder.albumArt.setImageBitmap(bitmap);
        }else{
            viewHolder.albumArt.setImageResource(R.drawable.album_grey);
            viewHolder.albumArt.setPadding(60,40,60,40);
        }


        //When PlayButton is clicked
        viewHolder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, itemList.getAlbum() + " Playing...", Toast.LENGTH_SHORT).show();

                ArrayList<MediaDataSongs> newData = MainActivity.songDataList;

                MainActivity.playerItems.clear();
                for (int i=0; i<MainActivity.songDataList.size(); i++){
                    final MediaDataSongs items = newData.get(i);

                    if (items.getAlbum().equals(itemList.getAlbum())){
                        MainActivity.playerItems.add((new MediaDataPlayer(items.getTitle(), items.getArtist(), items.getAlbum(), items.getPath(), items.getDuration(), items.getAlbumId(), items.getComposer(), items.getAlbumArt())));
                    }
                }

                PlayerFragment.currentPosition = 0;

                MainActivity.firstLaunch = false;

                android.support.v4.app.FragmentManager fragmentMg = ((MainActivity) context).getSupportFragmentManager();
                FragmentTransaction ft = fragmentMg.beginTransaction();
                ft.replace(R.id.player_container, new PlayerFragment(), "PLAYER");
                ft.commit();
            }
        });


        //When the option menu is clicked
        viewHolder.optionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Menu" , "Clicked");

                //Display Option menu
                PopupMenu popupMenu = new PopupMenu(context, viewHolder.optionMenu);
                popupMenu.inflate(R.menu.option_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.menu_item_save:
                                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.menu_item_delete:
                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                break;

                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


        //When the Entire row layout is clicked
        viewHolder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ((MainActivity)context).requestFragmentRead(itemList.getAlbum());

                /*Bundle bundle = new Bundle();
                bundle.putString("albumName", itemList.getAlbum());
                FragmentAlbum fragmentAlbum = new FragmentAlbum();
                fragmentAlbum.setArguments(bundle);*/

                //FragmentAlbum.albumName = itemList.getAlbum();

                FragmentAlbum.albumName = itemList.getAlbum();


                android.support.v4.app.FragmentManager fragmentMg = ((MainActivity) context).getSupportFragmentManager();
                FragmentTransaction ft = fragmentMg.beginTransaction();
                ft.remove(fragmentMg.findFragmentByTag("ALBUMLIST"));
                //ft.attach(fragmentMg.findFragmentByTag("ALBUMLIST"));
                ft.add(R.id.fragment_container, new FragmentAlbum(), "ALBUMLIST");
                ft.hide(fragmentMg.findFragmentByTag("TABS"));
                ft.hide(fragmentMg.findFragmentByTag("ARTISTLIST"));
                ft.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView albumText;
        public TextView artistText;
        public ImageView albumArt;
        public ImageView playButton;
        public ImageView optionMenu;

        MaterialCardView cardLayout;

        public ViewHolder(View itemView){
            super(itemView);

            albumText = itemView.findViewById(R.id.albumName);
            artistText = itemView.findViewById(R.id.artistName);
            albumArt = itemView.findViewById(R.id.albumArt);
            optionMenu = itemView.findViewById(R.id.optionMenu);
            playButton = itemView.findViewById(R.id.playButtonBackground);

            cardLayout = itemView.findViewById(R.id.cardLayout);
        }
    }
}
