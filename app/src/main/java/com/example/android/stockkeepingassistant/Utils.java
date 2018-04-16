package com.example.android.stockkeepingassistant;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class Utils {
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        // Read dimensions of disk image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // read dimensions w/o allocating memory for its pixels
        BitmapFactory.decodeFile(path);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // Figure out scaling factor
        int inSampleSize = 1; // default; No sub-sampling done by decoder
        if (srcHeight > destHeight || srcWidth > destWidth) {
            float heightScale = srcHeight / destHeight;
            float widthScale = srcWidth / destWidth;

            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        // Read again but this time create bitmap
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }
}
