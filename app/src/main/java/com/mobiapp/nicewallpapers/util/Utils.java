package com.mobiapp.nicewallpapers.util;

import android.annotation.TargetApi;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.Toast;

import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.api.ApiRequest;
import com.mobiapp.nicewallpapers.api.XMLParsingDOM;
import com.mobiapp.nicewallpapers.api.core.ApiCallback;
import com.mobiapp.nicewallpapers.api.core.ApiError;
import com.mobiapp.nicewallpapers.model.CategoriesRequest;
import com.mobiapp.nicewallpapers.model.ChooseSources;
import com.mobiapp.nicewallpapers.model.PhotoGoup;
import com.mobiapp.nicewallpapers.model.PhotoSave;
import com.mobiapp.nicewallpapers.model.PhotoSize;
import com.mobiapp.nicewallpapers.model.ResponePhoto;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class Utils {

    public static void setIntentWappaper(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(uri, "image/*");
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.use_as)));
    }

    public static void setWallpapersFromUri(Context context, Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            if (null != bitmap) {
                setWallpaper(context, bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setWallpaperRandom(final Context context, final boolean isShowToast) {
        if (!ConnectionUtil.isConnected(context)) {
            return;
        }
        if (isShowToast) {
            Toast.makeText(context, context.getString(R.string.please_wait_change_wallpaper),
                    Toast.LENGTH_LONG).show();
        }
        final Handler handlerLocal = new Handler(Looper.getMainLooper());
        XMLParsingDOM xmlParsingDOM = new XMLParsingDOM();
        xmlParsingDOM.parsingDOM(new XMLParsingDOM.OnXMLParsingDOMListener() {
            @Override
            public void parseResult(final List<CategoriesRequest> datasResult) {
                Runnable runnableLocal = new Runnable() {
                    @Override
                    public void run() {
                        if (datasResult.size() != 0) {
                            final List<ChooseSources> dataSaves = ChooseSources.listAll(ChooseSources.class);
                            List<CategoriesRequest> datas = new ArrayList<>();
                            if (dataSaves.get(0).isChecked()) {
                                datas.add(new CategoriesRequest(context.getString(R.string.download), ""));
                            }
                            if (dataSaves.get(1).isChecked()) {
                                datas.add(new CategoriesRequest(context.getString(R.string.blur_history), ""));
                            }
                            for (CategoriesRequest categoriesRequest : datasResult) {
                                for (ChooseSources chooseSources : dataSaves) {
                                    if (categoriesRequest.getTitle().equals(chooseSources.getTitle())
                                            && chooseSources.isChecked()) {
                                        datas.add(categoriesRequest);
                                        break;
                                    }
                                }
                            }
                            int randomPosition = new Random().nextInt(datas.size());
                            if (datas.get(randomPosition).getTitle().equals(context.getString(R.string.download))
                                    || datas.get(randomPosition).getTitle().equals(context.getString(R.string.blur_history))) {
                                String path = Environment.getExternalStorageDirectory().getPath() +
                                        "/" + context.getString(R.string.app_name);
                                if (datas.get(randomPosition).getTitle().equals(context.getString(R.string.blur_history))) {
                                    path = Environment.getExternalStorageDirectory().getPath() +
                                            "/" + context.getString(R.string.app_name) + "/" + context.getString(R.string.blur);
                                }
                                File downloadDir = new File(path);
                                File files[] = downloadDir.listFiles();
                                if (files != null && files.length != 0) {
                                    File fileRandom = files[new Random().nextInt(files.length)];
                                    try {
                                        Bitmap bitmap = MediaStore.Images.Media
                                                .getBitmap(context.getContentResolver(), Uri.fromFile(fileRandom));
                                        if (null != bitmap) {
                                            setWallpaper(context, bitmap);
                                        }
                                    } catch (IOException e) {
                                        Toast.makeText(context, context.getString(R.string.error_load_wallpaper), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Error to get new Image, please try again...!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                            String randomStr = datas.get(randomPosition).getGroupId();
                            ApiRequest.getPhotosRandom(randomStr, new ApiCallback<ResponePhoto>() {
                                @Override
                                public void failure(RetrofitError retrofitError, ApiError apiError) {
                                    Toast.makeText(context, context.getString(R.string.error_load_wallpaper), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void success(ResponePhoto responePhoto, Response response) {
                                    if (null == responePhoto
                                            || null == responePhoto.getPhotos()
                                            || responePhoto.getPhotos().getPhoto().size() == 0) {
                                        return;
                                    }
                                    List<PhotoGoup> photos = responePhoto.getPhotos().getPhoto();
                                    int index = new Random().nextInt(photos.size());
                                    final PhotoGoup photo = photos.get(index);
                                    ApiRequest.getPhotoSize(String.valueOf(photo.getId()), new ApiCallback<PhotoSize>() {
                                                @Override
                                                public void failure(RetrofitError retrofitError, ApiError apiError) {
                                                    Toast.makeText(context, context.getString(R.string.error_load_wallpaper), Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void success(PhotoSize photoSize, Response response) {
                                                    if (null == photoSize
                                                            || null == photoSize.getSizes()
                                                            || null == photoSize.getSizes().getSize()
                                                            || photoSize.getSizes().getSize().size() == 0) {
                                                        return;
                                                    }
                                                    String urlImage = photoSize.getSizes().getSize()
                                                            .get(photoSize.getSizes().getSize().size() - 1).getSource();
                                                    new LoadBitmap(context, photo).execute(urlImage);
                                                }
                                            }
                                    );
                                }
                            });
                        }
                    }
                };
                handlerLocal.postDelayed(runnableLocal, 100);
            }
        });
    }

    private static class LoadBitmap extends AsyncTask<String, String, Bitmap> {

        public Context mContext;
        PhotoGoup mPhoto;

        LoadBitmap(Context context, PhotoGoup photo) {
            mContext = context;
            mPhoto = photo;
        }

        @Override
        protected Bitmap doInBackground(String... args) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(
                        args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null) {
                return;
            }
            setWallpaper(mContext, bitmap);
            SimpleDateFormat formatter = new SimpleDateFormat("dd LLLL yyyy", Locale.getDefault());
            String currentDateandTime = formatter.format(new Date());

            PhotoSave photoSave = new PhotoSave();
            photoSave.setId(Long.valueOf(mPhoto.getId()));
            photoSave.setSecret(mPhoto.getSecret());
            photoSave.setServer(mPhoto.getServer());
            photoSave.setFarm(mPhoto.getFarm());
            photoSave.setTitle(mPhoto.getTitle());
            photoSave.setIspublic(mPhoto.getIspublic());
            photoSave.setIsfriend(mPhoto.getIsfriend());
            photoSave.setIsfamily(mPhoto.getIsfamily());
            photoSave.setDateCreated(currentDateandTime);
            photoSave.save();
        }
    }

    public static void setWallpaper(Context context, Bitmap bitmap) {
        WallpaperManager wallpaperManager;
        try {
            wallpaperManager = WallpaperManager.getInstance(context);
            wallpaperManager.setBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blurRenderScript(Context context, Bitmap smallBitmap, int radius) {

        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    public static void rateApp(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + context.getPackageName())));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="
                            + context.getPackageName())));
        }
    }

    public static void shareApp(Context context) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id="
                        + context.getPackageName());
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public static void feedBack(Context context) {
        Intent intentEmail = new Intent(Intent.ACTION_SEND);
        intentEmail.setType("text/email");
        intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.feedback_email)});
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_email_title));
        intentEmail.putExtra(Intent.EXTRA_TEXT, "");
        context.startActivity(Intent.createChooser(intentEmail, "Sending email..."));
    }

    public static boolean appInstalledOrNot(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}
