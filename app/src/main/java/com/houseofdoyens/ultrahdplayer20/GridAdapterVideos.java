package com.houseofdoyens.ultrahdplayer20;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GridAdapterVideos extends BaseAdapter {

    private String path[];
    private String name[];
    private String duration[];
    private String resolution[];
    private Context context;
    private LayoutInflater inflater;

    public GridAdapterVideos(Context context, String path[], String name[], String duration[], String resolution[]) {

        this.path = path;
        this.context = context;
        this.name = name;
        this.duration = duration;
        this.resolution = resolution;
    }

    @Override
    public int getCount() {
        return name.length;
    }

    @Override
    public Object getItem(int position) {
        return name[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View gridView = convertView;

        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.custom_layout_video, null);
        }

        ImageView imageView = (ImageView) gridView.findViewById(R.id.folderImage);
        final TextView textView = (TextView) gridView.findViewById(R.id.textView);
        final TextView vidDuration = (TextView) gridView.findViewById(R.id.duration);

        int dur = Integer.parseInt(duration[position]);
        String durationInMinutes = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(dur),
                TimeUnit.MILLISECONDS.toSeconds(dur) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dur))
        );

        Log.d("resolu", "" + resolution[position]);

        Glide.with(context)
                .load(path[position])
                .centerCrop()
                .placeholder(Color.BLUE)
                .crossFade()
                .into(imageView);
        textView.setText(name[position]);
        vidDuration.setText(durationInMinutes);

        gridView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String videoName = name[position];
                String videoPath = path[position];
//                String res = resolution[position];

                ArrayList<String> AllVideoName = new ArrayList<>();
                ArrayList<String> AllVideoPath = new ArrayList<>();

                for (int i = 0; i < name.length; i++) {
                    AllVideoName.add(name[i]);
                    AllVideoPath.add(path[i]);
                }

//                Log.i("VideoRes",res); // Okay getting Resolution of one Video
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("videoName", videoName);
                intent.putExtra("videoPath", videoPath);
                intent.putExtra("defaultFlag", true);
                intent.putStringArrayListExtra("videoNameArray", AllVideoName);
                intent.putStringArrayListExtra("videoPathArray", AllVideoPath);
                context.startActivity(intent);
            }
        });
        return gridView;
    }
}
