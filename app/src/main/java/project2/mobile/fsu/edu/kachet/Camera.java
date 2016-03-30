package project2.mobile.fsu.edu.kachet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private static final String MEDIA_FOLDER_NAME = "Kachet";
    private Uri currentMediaUri;

    ImageView view_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        view_image = (ImageView) findViewById(R.id.view_img);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // React to captured image
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                view_image.setImageBitmap(imageBitmap);
                view_image.setImageDrawable(getDrawable(R.drawable.splash));

                //Log.i(TAG, "onActivityResult: Image saved to: " + currentMediaUri.getPath());

                // TODO: Do something useful with saved image, located at currentMediaUri
            }
            else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "onActivityResult: User cancelled the image capture");
            }
            else {
                // Image capture failed, advise user
                // TODO: Figure out how best to advise user
            }
        }

        // React to captured video
        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "onActivityResult: Video saved to: " + data.getData());

                // TODO: Do something useful with saved video, located at currentMediaUri
            }
            else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "onActivityResult: User cancelled the video capture");
            }
            else {
                // Video capture failed, advise user
                // TODO: Figure out how best to advise user
            }
        }
    }


    // TODO: Add ability to capture video

    /*
     * Creates a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
     * Creates a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // Check that external storage is mounted
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.e(TAG, "getOutputMediaFile: External storage is not mounted");
            return null;
        }

        // Set the directory in which the file will be stored
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), MEDIA_FOLDER_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "getOutputMediaFile: failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }
        else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        }
        else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}