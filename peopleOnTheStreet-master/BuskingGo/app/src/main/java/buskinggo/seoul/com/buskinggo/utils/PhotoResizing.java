package buskinggo.seoul.com.buskinggo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public class PhotoResizing {

    // 이미지 리사이징
    public Bitmap loadPictureWithResize(File file , int size) {
        Bitmap resizeBitmap;

        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeFile(file.getAbsolutePath(), options); // 1번

        int width = options.outWidth;
        int height = options.outHeight;
        int samplesize = 1;

        while (true) {//2번
            if (width / 2 < size || height / 2 < size)
                break;
            width /= 2;
            height /= 2;
            samplesize *= 2;
        }

        options.inSampleSize = samplesize;
        resizeBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        return resizeBitmap;

    }
}
