package com.houseofdoyens.ultrahdplayer20;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

public class GetVideos {

    public void getAllVideosData(Context context, ArrayList<VideoViewInfo> AllVideosData) {

        String[] projection = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.RESOLUTION,
                MediaStore.Video.Media.SIZE
    };

        Uri Video = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Video.Media.DATE_TAKEN;

        Cursor cur = context.getContentResolver().query(Video,
                projection,
                null,
                null,
                orderBy + " DESC"
        );

        Log.i("ListingVideo"," query count=" + cur.getCount());

        if (cur.moveToFirst()) {
            String id;
            String title;
            String filePath;
            String bucketName;
            String date;
            String duration;
            String resolution;
            String size;

            int tempId = cur.getColumnIndex(
                    MediaStore.Video.Media._ID);

            int titleTemp = cur.getColumnIndex(
                    MediaStore.Video.Media.TITLE);

            int filePathTemp = cur.getColumnIndex(
                    MediaStore.Video.Media.DATA);

            int bucketTemp = cur.getColumnIndex(
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME);

            int dateTemp = cur.getColumnIndex(
                    MediaStore.Video.Media.DATE_TAKEN);

            int durationTemp = cur.getColumnIndex(
                    MediaStore.Video.Media.DURATION);

            int resolutionTemp = cur.getColumnIndex(
                    MediaStore.Video.Media.RESOLUTION);

            int sizeTemp = cur.getColumnIndex(
                    MediaStore.Video.Media.SIZE);

            do {
                id = cur.getString(tempId);
                title = cur.getString(titleTemp);
                filePath = cur.getString(filePathTemp);
                bucketName = cur.getString(bucketTemp);
                date = cur.getString(dateTemp);
                duration = cur.getString(durationTemp);
                resolution = cur.getString(resolutionTemp);
                size = cur.getString(sizeTemp);

                VideoViewInfo vvi = new VideoViewInfo(id, title, filePath, bucketName, date, duration, resolution, size);
                AllVideosData.add(vvi);

            } while (cur.moveToNext());
        }
    }
}
