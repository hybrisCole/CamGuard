package com.example.albertocole.myapplication;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.Camera;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

import org.json.*;

public class MainActivity extends AppCompatActivity {
    private Camera mCamera;
    private Pubnub nPubNub;
    private Callback sendPictureCallback;

    public void sendPictureData (byte[] data) {

        // 32kb is the max size allowed for PubNub
        List<byte[]> imageChunks = divideArray(data, 16 * 1000);

        for(int i = 0; i < imageChunks.size(); i++) {
            nPubNub.publish("camguard:sendPicture", Base64.encodeToString(imageChunks.get(i), Base64.DEFAULT) , sendPictureCallback);
        }
    }

    public static List<byte[]> divideArray(byte[] source, int chunksize) {

        List<byte[]> result = new ArrayList<byte[]>();
        int start = 0;
        while (start < source.length) {
            int end = Math.min(source.length, start + chunksize);
            result.add(Arrays.copyOfRange(source, start, end));
            start += chunksize;
        }

        return result;
    }

    private void setupPubNub () {
        Log.i(getString(R.string.app_name), "SETTING UP PUBNUB");
        nPubNub = new Pubnub("pub-c-53c4cd7d-bc10-46c6-842b-36d270ea44f7", "sub-c-0483cd26-04fc-11e6-bbd9-02ee2ddab7fe", "sec-c-NDM5ZGZkZTMtMDU2Mi00MGQyLWFmZGEtNzdmOGQyODM5ZmU1");
        sendPictureCallback = new Callback() {
            public void successCallback(String channel, Object response) {
                Log.i(getString(R.string.app_name), response.toString());
            }
            public void errorCallback(String channel, PubnubError error) {
                Log.e(getString(R.string.app_name), error.toString());
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupPubNub();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Context mContext = getApplicationContext();

        try {
            System.out.println("SETTING UP CAMERA");
            mCamera = Camera.open();
            Camera.Parameters parameters = mCamera.getParameters();
            List<Integer> formats = parameters.getSupportedPictureFormats();
            if (formats.contains(ImageFormat.JPEG)) {
                parameters.setPictureFormat(ImageFormat.JPEG);
                parameters.setJpegQuality(100);
            } else {
                parameters.setPictureFormat(PixelFormat.RGB_565);
            }
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            System.out.println(sizes);
            Camera.Size size = sizes.get(sizes.size() - 1);
            // Print the name from the list....
            for(Camera.Size sizee : sizes) {
                System.out.println(sizee.width);
                System.out.println(sizee.height);
            }
            parameters.setPictureSize(size.width, size.height);
            parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            SurfaceView view = new SurfaceView(mContext);
            android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(size.width, size.height);
            view.setLayoutParams(params);
            mCamera.setPreviewDisplay(view.getHolder());
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            System.out.println("CAMERA READY");
        } catch  (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                sendPictureData(data);
                //mCamera.stopPreview();
                //mCamera.release();
            }
        });
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{
                    /* System.out.println("TAKING");
                    Thread.sleep(1000);
                    System.out.println("PICTURE");

                    mCamera.takePicture(null, null, new Camera.PictureCallback() {
                        public void onPictureTaken(byte[] data, Camera camera) {
                            sendPictureData(data);
                            //mCamera.stopPreview();
                            //mCamera.release();
                        }
                    }); */
                }
                catch (Exception e) {
                    Log.e(getString(R.string.app_name), Log.getStackTraceString(e));
                }
                finally{
                    //also call the same runnable to call it at regular interval
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.postDelayed(runnable, 1000);

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
