package com.lassafever.sonoma;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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

public class AdapterAlbumSongList extends RecyclerView.Adapter<AdapterAlbumSongList.ViewHolder> {
    private ArrayList<MediaDataAlbumSongList> data;
    private Context context;


    public AdapterAlbumSongList(ArrayList<MediaDataAlbumSongList> data, Context context){
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final MediaDataAlbumSongList itemList = data.get(i);
        viewHolder.titleText.setText(itemList.getTitle());
        viewHolder.artistText.setText(itemList.getArtist());

        String duration = MainActivity.milliSecondsToTimer(itemList.getDuration());
        viewHolder.durationText.setText(duration);

        Bitmap bitmap = itemList.getAlbumArt();
        if (bitmap!=null) {
            viewHolder.albumArt.setImageBitmap(bitmap);
        }else{
            viewHolder.albumArt.setImageResource(R.drawable.album);
        }


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
        viewHolder.rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, itemList.getTitle() + " Playing...", Toast.LENGTH_SHORT).show();

                MainActivity.playerItems.clear();
                for (int i=0; i<data.size(); i++){
                    final MediaDataAlbumSongList items = data.get(i);
                    MainActivity.playerItems.add((new MediaDataPlayer(items.getTitle(), items.getArtist(), items.getAlbum(), items.getPath(), items.getDuration(), items.getAlbumId(), items.getComposer(), items.getAlbumArt())));
                }
                PlayerFragment.currentPosition = i;


                MainActivity.firstLaunch = false;


                android.support.v4.app.FragmentManager fragmentMg = ((MainActivity) context).getSupportFragmentManager();
                FragmentTransaction ft = fragmentMg.beginTransaction();
                ft.replace(R.id.player_container, new PlayerFragment(), "PLAYER");
                ft.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView titleText;
        public TextView artistText;
        public TextView durationText;
        public ImageView albumArt;
        public ImageView optionMenu;

        ConstraintLayout rowLayout;

        public ViewHolder(View itemView){
            super(itemView);

            titleText = itemView.findViewById(R.id.title);
            artistText = itemView.findViewById(R.id.artist);
            durationText = itemView.findViewById(R.id.duration);
            albumArt = itemView.findViewById(R.id.albumArt);
            optionMenu = itemView.findViewById(R.id.optionMenu);

            rowLayout = itemView.findViewById(R.id.rowLayout);
        }
    }
}
