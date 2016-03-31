package project2.mobile.fsu.edu.kachet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostKachet extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private static final String MEDIA_FOLDER_NAME = "Kachet";
    private Uri currentMediaUri;

    EditText message_text;
    ImageView view_image;
    FloatingActionButton camera, submit, reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        message_text = (EditText) findViewById(R.id.message_body);
        view_image = (ImageView) findViewById(R.id.view_img);
        submit = (FloatingActionButton) findViewById(R.id.checkmark_button);
        camera = (FloatingActionButton) findViewById(R.id.take_picture);
        reset = (FloatingActionButton) findViewById(R.id.x_button);

        camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                captureImage();
                view_image.setSaveEnabled(true);
            }

        });

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(message_text.getText().toString().equals("") && !view_image.isSaveEnabled()){
                    Toast.makeText(getApplicationContext(), "ERROR: Post is Blank", Toast.LENGTH_SHORT).show();
                }else{

                    //send to server here
                    Toast.makeText(getApplicationContext(), "Kache Posted", Toast.LENGTH_SHORT).show();

                    message_text.getText().clear();
                    view_image.setImageResource(0);
                    view_image.setSaveEnabled(false);
                }
            }

        });

        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    message_text.getText().clear();
                    view_image.setImageResource(0);
                    view_image.setSaveEnabled(false);
            }

        });

    }

    /*
 * Uses an image capture intent to obtain an image from the device's camera
 */
    void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create a file to save the image
        currentMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        if (currentMediaUri == null) {
            Log.e(TAG, "CaptureImage: could not create file URI");
            return;
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentMediaUri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // React to captured image
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                setPic();

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

    private void setPic() {
        // Get the dimensions of the View
        int targetW = view_image.getWidth();
        int targetH = view_image.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentMediaUri.getPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;


        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentMediaUri.getPath(), bmOptions);
        view_image.setImageBitmap(bitmap);
    }
}