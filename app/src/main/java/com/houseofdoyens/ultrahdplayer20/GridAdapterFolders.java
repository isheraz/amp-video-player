package com.houseofdoyens.ultrahdplayer20;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class GridAdapterFolders extends BaseAdapter{

    private String textViews[];
    private Context context;
    private LayoutInflater inflater;

    public GridAdapterFolders(Context context, String textViews[]) {

        this.context = context;
        this.textViews = textViews;
    }

    @Override
    public int getCount() {
        return textViews.length;
    }

    @Override
    public Object getItem(int position) {
        return textViews[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View gridView = convertView;

        if(convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.custom_layout_folder, null);
        }

        ImageView imageView = (ImageView) gridView.findViewById(R.id.imageView);
        final TextView textView = (TextView) gridView.findViewById(R.id.textView);

        gridView.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            String folderName = (String) textViews[position];

                                            Intent intent = new Intent(context, FolderVideosActivity.class);
                                            intent.putExtra("folderName", folderName);
                                            context.startActivity(intent);
                                        }
                                    });

        imageView.setImageResource(R.drawable.folder);
        textView.setText(textViews[position]);

        return gridView;
    }
}
