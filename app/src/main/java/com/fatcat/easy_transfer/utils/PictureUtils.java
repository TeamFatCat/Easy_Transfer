package com.fatcat.easy_transfer.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.fatcat.easy_transfer.entity.ImageInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by FatCat on 2016/8/4.
 */
public class PictureUtils {


    public static final String MEDIA_CHECKED = "true";
    public static final String MEDIA_NO_CHECKED = "false";

    public static HashMap<String, String> pictureSelect = new HashMap<String, String>();


    public static List<ImageInfo> getPictureInfos(Context context) {

        List<ImageInfo> imageInfos = new ArrayList<ImageInfo>();

        ImageInfo imageInfo = null;

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
                null, null);

        while (cursor.moveToNext()) {
            int id = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            String title = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
            String path = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            String displayName = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
            String mimeType = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
            long size = cursor
                    .getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));

            imageInfo = new ImageInfo(id, title, displayName, mimeType, path, size);

            imageInfos.add(imageInfo);


        }


        for (int i = 0; i < imageInfos.size(); i++) {
            PictureUtils.pictureSelect.put(i + "", PictureUtils.MEDIA_NO_CHECKED);
        }

        cursor.close();

        return imageInfos;

    }


}
