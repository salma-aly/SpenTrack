package com.spentrack.spentrack;

import android.content.pm.PackageManager;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.widget.Button;

        import java.io.File;
        import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
        import java.util.Date;
        import java.util.List;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.content.pm.ResolveInfo;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Environment;
        import android.provider.MediaStore;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

import static com.loopj.android.http.AsyncHttpClient.log;



public class MainActivity extends Activity {


    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int ACTION_TAKE_PHOTO_S = 2;
    private static final int ACTION_TAKE_VIDEO = 3;

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private ImageView mImageView;
    private Bitmap mImageBitmap;

    private static final String VIDEO_STORAGE_KEY = "viewvideo";
    private static final String VIDEOVIEW_VISIBILITY_STORAGE_KEY = "videoviewvisibility";
    private VideoView mVideoView;
    private Uri mVideoUri;

    private String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    public TextView testPicReturn;
    public TextView testPicReturnGoogleURL;
    public TextView linkShopWebsite;

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }


    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        mCurrentPhotoPath = imageF.getAbsolutePath();
        log.w("path",mCurrentPhotoPath);
        return imageF;
    }

    public void postpicture(){
        AsyncHttpClient client = new AsyncHttpClient();
        try {
            testPicReturn.setText("Retrieving data from receipt...");
            linkShopWebsite.setText("");
            testPicReturnGoogleURL.setText("");
            RequestParams params = new RequestParams();
            File myFile = new File(mCurrentPhotoPath);
            params.put("media",myFile);
            client.post("http://104.196.62.234:8080/spentrack",params, new AsyncHttpResponseHandler() {
                //client.get(urlString,params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String content = new String(responseBody);
                    log.w("Response Body as String",content);

                    String content_notwrapped_by_quotes = content.replaceAll("^\"|\"$", "");
                    content = content_notwrapped_by_quotes.replaceAll("\\\\\"", "\"");

                    JSONObject content_inJSON= null;
                    try {
                        content_inJSON = new JSONObject(content);
                        String extractedTotal =content_inJSON.getString( "Total");
                        String extractedDate =content_inJSON.getString( "Date");
                        String extractedShopName =content_inJSON.getString( "Shop Name");
                        String extractedAddress =content_inJSON.getString( "Address");
                        String extractedCategory=content_inJSON.getString( "Category");
                        String extractedPhoneNumber =content_inJSON.getString( "Telephone number");
                        String extractedWebsite =content_inJSON.getString( "Website");
                        String extractedURLtoGoogleMaps =content_inJSON.getString( "See place on google maps");
                        String extractedRating=content_inJSON.getString( "Rating");

                        //map categories names given from the server to a user friendly syntax to be displayed on the android app
                        String catgegory;
                        switch (extractedCategory) {
                            case "restaurant":  catgegory = "Restaurant";
                                break;
                            case "cafe":  catgegory = "Cafe";
                                break;
                            case "clothing_store":  catgegory = "Clothing Store";
                                break;
                            case "furniture_store":  catgegory = "Furniture Store";
                                break;
                            case "hair_care":  catgegory= "Hair Care";
                                break;
                            case "grocery_or_supermarket":  catgegory = "Grocery";
                                break;
                            case "electronics_store":  catgegory = "Electronics Store";
                                break;
                            case "museum":  catgegory = "Museum";
                                break;
                            case "pharmacy":  catgegory = "Pharmacy";
                                break;
                            case "store":  catgegory = "Store";
                                break;
                            default: catgegory = "Other";
                                break;
                        }

                        testPicReturn.setText("Data retrieved from receipt: \n\n" +
                                "Total: " + extractedTotal +"$\n" +
                                "Date: " + extractedDate + "\n" +
                                "Shop Name: " + extractedShopName+ "\n" +
                                "Address: " + extractedAddress+ "\n" +
                                "Category: " + catgegory+ "\n" +
                                "Telephone number: " + extractedPhoneNumber+ "\n" +
                                "Rating: " + extractedRating+ "\n");
                        linkShopWebsite.setText("See shop's website: \n" + extractedWebsite+ "\n");
                        testPicReturnGoogleURL.setText("See shop's location on google maps: \n" + extractedURLtoGoogleMaps);


//                         extractedTotal = "";
//                         extractedDate ="";
//                         extractedShopName ="";
//                        extractedAddress ="";
//                         extractedCategory=content_inJSON.getString( "Category");
//                         extractedPhoneNumber =content_inJSON.getString( "Telephone number");
//                         extractedWebsite =content_inJSON.getString( "Website");
//                         extractedURLtoGoogleMaps =content_inJSON.getString( "See place on google maps");
//                         extractedRating=content_inJSON.getString( "Rating");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.w("ERRRRRRRRRRRRRRRRRROR", error);
                    testPicReturn.setText("Failed to process receipt:" +error);

                }
            });
        }
        catch (Exception e) {

            System.out.println(e.getMessage());



        }
    }



    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        mImageView.setImageBitmap(bitmap);
        mVideoUri = null;
        mImageView.setVisibility(View.VISIBLE);
        mVideoView.setVisibility(View.INVISIBLE);
        postpicture();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                log.w("tag","file creation failed");
            }
            /* Continue only if the File was successfully created */
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.spentrack.spentrack.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                //postpicture();
            }
        }
    }

   /* private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch(actionCode) {
            case ACTION_TAKE_PHOTO_B:
                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        startActivityForResult(takePictureIntent, actionCode);
    }*/

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
    }

    private void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        mImageBitmap = (Bitmap) extras.get("data");
        mImageView.setImageBitmap(mImageBitmap);
        mVideoUri = null;
        mImageView.setVisibility(View.VISIBLE);
        mVideoView.setVisibility(View.INVISIBLE);
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }

    private void handleCameraVideo(Intent intent) {
        mVideoUri = intent.getData();
        mVideoView.setVideoURI(mVideoUri);
        mImageBitmap = null;
        mVideoView.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.INVISIBLE);
    }

    Button.OnClickListener mTakePicOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent();
                }
            };

    Button.OnClickListener mTakePicSOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent();
                }
            };

    Button.OnClickListener mTakeVidOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakeVideoIntent();
                }
            };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageView1);
        mVideoView = (VideoView) findViewById(R.id.videoView1);
        mImageBitmap = null;
        mVideoUri = null;

        testPicReturn = (TextView) findViewById(R.id.testPicReturn);
        testPicReturnGoogleURL = (TextView) findViewById(R.id.testPicReturnGoogleURL);
        linkShopWebsite = (TextView) findViewById(R.id.linkShopWebsite);


//        String testJSON = "{\"Website\": \"https://www.montrealkoreanfood.com/\", \"Shop Name\": \"\\u00c9picerie Cor\\u00e9enne et Japonaise (\\ud55c\\uad6d\\uc2dd\\ud488)\", \"Total\": 7.98, \"Category\": \"grocery_or_supermarket\", \"Telephone number\": \"(514) 779-7456\", \"Date\": \"2017/11/01\", \"See place on google maps\": \"https://maps.google.com/?cid=5463033794973654626\", \"Address\": \"1829 Rue Sainte-Catherine O, Montr\\u00e9al, QC H3H 1M6, Canada\", \"Rating\": \"4.4\"}";
//        System.out.print("TEST_JSON" + testJSON);
//        log.w("TEST_JSON", testJSON);

//        try {
//            JSONObject obj = new JSONObject(testJSON);
//            String shopname = obj.getString("Shop Name");
//            String linkOnGoogleMaps = obj.getString("See place on google maps");


//            testPicReturnGoogleURL.setText(linkOnGoogleMaps);

//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        Button picBtn = (Button) findViewById(R.id.btnIntend);
        setBtnListenerOrDisable(
                picBtn,
                mTakePicOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );

        Button picSBtn = (Button) findViewById(R.id.btnIntendS);
        setBtnListenerOrDisable(
                picSBtn,
                mTakePicSOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );

        Button vidBtn = (Button) findViewById(R.id.btnIntendV);
        setBtnListenerOrDisable(
                vidBtn,
                mTakeVidOnClickListener,
                MediaStore.ACTION_VIDEO_CAPTURE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            } // ACTION_TAKE_PHOTO_B

            case ACTION_TAKE_PHOTO_S: {
                if (resultCode == RESULT_OK) {
                    handleSmallCameraPhoto(data);
                }
                break;
            } // ACTION_TAKE_PHOTO_S

            case ACTION_TAKE_VIDEO: {
                if (resultCode == RESULT_OK) {
                    handleCameraVideo(data);
                }
                break;
            } // ACTION_TAKE_VIDEO
        } // switch
    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
        outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY, (mVideoUri != null) );
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);
        mImageView.setImageBitmap(mImageBitmap);
        mImageView.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );
        mVideoView.setVideoURI(mVideoUri);
        mVideoView.setVisibility(
                savedInstanceState.getBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );
    }

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
     *
     * @param context The application's environment.
     * @param action The Intent action to check for availability.
     *
     * @return True if an Intent with the specified action can be sent and
     *         responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void setBtnListenerOrDisable(
            Button btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(
                    getText(R.string.cannot).toString() + " " + btn.getText());
            btn.setClickable(false);
        }
    }

}