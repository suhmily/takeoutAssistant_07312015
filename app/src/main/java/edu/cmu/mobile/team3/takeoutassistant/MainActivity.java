package edu.cmu.mobile.team3.takeoutassistant;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.googlecode.tesseract.android.TessBaseAPI;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private List<Restaurant> menuList = null;
    //private ListView listView = null;
    private SimpleAdapter simpleAdapter = null;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int SELECT_PHOTO_ACTIVITY_REQUEST_CODE = 200;
    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/TakeoutAssistant-master/";

    // You should have the trained data file in assets folder
    // You can get them at:
    // http://code.google.com/p/tesseract-ocr/downloads/list
    public static final String lang = "eng";

    private static final String TAG = "TakeOutAssistant.java";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }

        }

        // lang.traineddata file with the app (in assets folder)
        // You can get them at:
        // http://code.google.com/p/tesseract-ocr/downloads/list
        // This area needs work and optimization
        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.take_photo) {
            //Toast.makeText(this, "Take photo", Toast.LENGTH_SHORT).show();
            openCamera();
            return true;
        } else if (id == R.id.choose_photo) {
            //Toast.makeText(this, "Choose photo", Toast.LENGTH_SHORT).show();
            openImagePicker();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateView() {
        ListView listView = (ListView) super.findViewById(R.id.listView);
        settingAdapter();
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(this);
    }

    private void settingAdapter() {
        initList();
        String from[] = new String[]{"name"};
        int to[] = new int[]{R.id.restaurant_name};
        simpleAdapter = new SimpleAdapter(this, list, R.layout.list_item_menu,
                from, to);
    }

    private void initList() {
        menuList = Database.read();

        list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < menuList.size(); ++i) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            Restaurant menu = menuList.get(i);
            map.put("name", menu.getName());
            map.put("address", menu.getAddress());
            map.put("phone", menu.getPhone());
            list.add(map);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id) {
        Toast.makeText(this, Integer.toString(position), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, MenuActivity.class);
        intent.putExtra("menu", menuList.get(position));
        startActivity(intent);
        MainActivity.this.finish();
    }

    private void openCamera() {
        // create Intent to take a picture and return control to the calling applicatio
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputMediaFileUri(MEDIA_TYPE_IMAGE)); // set the image file name
        startActivityForResult(imageIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void openImagePicker() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Camera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Dir", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path = "";
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                ContentResolver resolver = getApplicationContext().getContentResolver();
                Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
                if (cursor != null) {
                    cursor.moveToFirst();
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    Toast.makeText(this, "Image saved to:\n" + path, Toast.LENGTH_LONG).show();
                    cursor.close();
                }
            } else if (requestCode == SELECT_PHOTO_ACTIVITY_REQUEST_CODE) {
                final Uri imageUri = data.getData();
                ContentResolver resolver = getApplicationContext().getContentResolver();
                String[] projects = {MediaStore.MediaColumns.DATA};
                Cursor cursor = resolver.query(imageUri, projects, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    path = cursor.getString(cursor.getColumnIndex(projects[0]));
                    Toast.makeText(this, "Image picked from:\n" + path, Toast.LENGTH_LONG).show();
                    cursor.close();
                }
            }


        }


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        try {
            ExifInterface exif = new ExifInterface(path);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v(TAG, "Orient: " + exifOrientation);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }

            // Convert to ARGB_8888, required by tess
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            Bitmap grayScale = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(grayScale);
            Paint paint = new Paint();
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
            paint.setColorFilter(f);
            c.drawBitmap(bitmap, 0, 0, paint);
            bitmap = grayScale;

        } catch (IOException e) {
            Log.e(TAG, "Couldn't correct orientation: " + e.toString());
        }

        // _image.setImageBitmap( bitmap );

        Log.v(TAG, "Before baseApi");

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);

        baseApi.init(DATA_PATH, lang);
        baseApi.setImage(bitmap);

        String recognizedText = baseApi.getUTF8Text();

        baseApi.end();

        // You now have the text in recognizedText var, you can do anything with it.
        // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
        // so that garbage doesn't make it to the display.

        Log.v(TAG, "OCRED TEXT: " + recognizedText);

        if ( lang.equalsIgnoreCase("eng") ) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }

        recognizedText = recognizedText.trim();
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE ||
                requestCode == SELECT_PHOTO_ACTIVITY_REQUEST_CODE) {
            OCR ocr = new OCR(recognizedText);
            //PaperMenu pm = ocr.getRestaurant();
            Restaurant pm = new Restaurant();
            pm.setName("Little Asia");
            pm.setPhone("4121111111");
            pm.setAddress("417 Cragi St, Pittsburgh, PA 15213");
            //     if (pm != null && pm.getPhoneNumber() != null && !menuList.contains(pm)) {
                menuList.add(pm);
                Database.write(menuList);
                updateView();
            //     }
        }



    }

}
