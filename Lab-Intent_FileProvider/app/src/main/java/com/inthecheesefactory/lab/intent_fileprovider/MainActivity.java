package com.inthecheesefactory.lab.intent_fileprovider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;



@RuntimePermissions
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String[] str_results = new String[81];
    public static String url = "http://52.79.99.66:5000/uploader";

    private static final int REQUEST_TAKE_PHOTO = 1;

    Button btnTakePhoto;
    Button btnUpdatePhoto;
    ImageView ivPreview;

    String mCurrentPhotoPath;

    Bitmap targetPicture;
    String bytePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initInstances();
    }

    private void initInstances() {
        btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
        btnUpdatePhoto = (Button) findViewById(R.id.btnUploadPhoto);
        ivPreview = (ImageView) findViewById(R.id.ivPreview);

        btnTakePhoto.setOnClickListener(this);
        btnUpdatePhoto.setOnClickListener(this);
    }

    /////////////////////
    // OnClickListener //
    /////////////////////

    @Override
    public void onClick(View view) {
        if (view == btnTakePhoto) {
            MainActivityPermissionsDispatcher.startCameraWithCheck(this);
        }

        if (view == btnUpdatePhoto) {
            upload();
        }
    }

    ////////////
    // Camera //
    ////////////

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void startCamera() {
        try {
            dispatchTakePictureIntent();
        } catch (IOException e) {
        }
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("Access to External Storage is required")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.proceed();
                    }
                })
                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // Show the thumbnail on ImageView
            Uri imageUri = Uri.parse(mCurrentPhotoPath);
            File file = new File(imageUri.getPath());
            try {
                InputStream ims = new FileInputStream(file);
                targetPicture = BitmapFactory.decodeStream(ims);
                ivPreview.setImageBitmap(targetPicture);
            } catch (FileNotFoundException e) {
                return;
            }

            // ScanFile so it will be appeared on Gallery
            MediaScannerConnection.scanFile(MainActivity.this,
                    new String[]{imageUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file://" + image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /////////////
    // Upload //
    ///////////
    /*
    void upload() {
        try {

            ANRequest request = AndroidNetworking.upload(url)
                    .addMultipartFile("image", createImageFile())
                    .addMultipartParameter("filename", "test.jpg")
                    .setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            // do anything with progress
                        }
                    })
                    .build();


            ANResponse<JSONObject> response = request.executeForJSONObject();

            if (response.isSuccess()) {
                JSONObject jsonObject = response.getResult();
                Log.d("tag", "response : " + jsonObject.toString());
                Response okHttpResponse = response.getOkHttpResponse();
                Log.d("tag", "headers : " + okHttpResponse.headers().toString());
            } else {
                ANError error = response.getError();
                // Handle Error
                Log.e("tag", error.toString());
            }
        } catch (IOException e){
            Log.e("tag", e.toString());
        }

        AndroidNetworking.upload(url)
                .addMultipartFile("image", image)
                .addMultipartParameter("filename","test.jpg")
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse (JSONObject response){
                        // do anything with response
                        Log.e("tag", "response" + response);

                    }
                    @Override
                    public void onError (ANError error){
                        // handle error
                        Log.e("tag", error.toString());
                    }
                });

    }
    */

    void upload() {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        targetPicture.compress(Bitmap.CompressFormat.JPEG, 50, bao);
        byte[] ba = bao.toByteArray();
        bytePicture = Base64.encodeToString(ba, Base64.NO_WRAP);

        // Upload image to server
        new uploadToServer().execute();
    }


    public class uploadToServer extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("image", bytePicture));
            nameValuePairs.add(new BasicNameValuePair("ImageName", System.currentTimeMillis() + ".jpg"));
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                String st = EntityUtils.toString(response.getEntity());
                Log.v("log_tag", "In the try Loop" + st);
                JSONObject reader = new JSONObject(st);

                JSONArray results = reader.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    str_results[i] = results.getString(i);
                }

                Intent intent =  new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("answer", str_results);
                startActivity(intent);

            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }
            return "Success";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}


