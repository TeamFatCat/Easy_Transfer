package com.fatcat.easy_transfer.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.fatcat.easy_transfer.entity.VideoInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by FatCat on 2016/8/5.
 */
public class VideoUtils {

    public static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public static final String MEDIA_CHECKED = "true";
    public static final String MEDIA_NO_CHECKED = "false";

    public static HashMap<String, String> videoSelect = new HashMap<String, String>();

    public static List<VideoInfo> getVideoInfos(Context context) {

        List<VideoInfo> videoInfos = new ArrayList<VideoInfo>();

        VideoInfo videoInfo = null;

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));

            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));


            String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));

            String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));

            String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));

            String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));

            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

            long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));


            videoInfo = new VideoInfo(id, title, album, artist, displayName, mimeType, path, size, duration);

            videoInfos.add(videoInfo);

        }

        for (int i = 0; i < videoInfos.size(); i++) {
            VideoUtils.videoSelect.put(i + "", MusicUtils.MEDIA_NO_CHECKED);
        }

        cursor.close();

        return videoInfos;

    }

    public static String getVideoSize(long size) {

        if (size / (1024 * 1024) > 1024)
            return (decimalFormat.format(size / 1024.0 / 1024.0 / 1024.0)) + " GB";
        else if (size / 1024 > 1024)
            return (decimalFormat.format(size / 1024.0 / 1024.0)) + " MB";
        else if (size > 1024)
            return (decimalFormat.format(size / 1024.0)) + " KB";
        else
            return size + " B";

    }

}
