package com.mrs.xmpp.im.commlib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Created by mrs on 2017/4/7.
 */

public class PicUtils {

    public static void load(Context context, ImageView img, String path, int plachHolder) {

        Glide.with(context).load(path)
                .asBitmap()
                //.thumbnail(0.1f)
                .placeholder(plachHolder)
                .error(plachHolder)
                .into(img);
    }

    public static void load(Context context, ImageView img, int plachHolder, String path) {

        Glide.with(context)
                .load(path)
                //.thumbnail(0.1f)
                .asBitmap()
                .placeholder(plachHolder)
                .error(plachHolder)
                .into(img);
    }

    public static void loadWithResize(Context context, ImageView img, int plachHolder, String path, int resizeW, int resizeH) {

        Glide.with(context)
                .load(path)
                .asBitmap()
                .placeholder(plachHolder)
                .error(plachHolder)
                .centerCrop()
                .override(resizeW, resizeH)
                .into(img);
    }

    public static void loadCircle(Context context, ImageView img, int plachHolder, String path) {

        Glide.with(context)
                .load(path)
                .asBitmap()
                .placeholder(plachHolder)
                .error(plachHolder)
                .transform(new CircleTransform(context, 10))
                .centerCrop()
                .into(img);
    }

    public static void loadWithBitmap(Context context, String path, final GlideLoadCallback callback) {
        Glide.with(context).load(path).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (callback != null)
                    callback.onBitmapLoaded(resource);
            }
        });
    }
}
