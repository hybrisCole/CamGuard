package com.example.albertocole.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            Camera.Size size = sizes.get(0);
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
            Log.i(getString(R.string.app_name), view.getHeight() + "");
            Log.i(getString(R.string.app_name), view.getWidth() + "");
            mCamera.setPreviewDisplay(view.getHolder());
            mCamera.setParameters(parameters);
            mCamera.startPreview();


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
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{
                    System.out.println(1);
                    Thread.sleep(1000);
                    System.out.println(2);
                    mCamera.takePicture(null, null, new Camera.PictureCallback() {
                        public void onPictureTaken(byte[] data, Camera camera) {
                            System.out.println(data.length);
                            //mCamera.stopPreview();
                            //mCamera.release();
                /* BitmapFactory.Options bfo = new BitmapFactory.Options();
                bfo.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(data), null, bfo);
                System.out.println(5);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
                byte[] b = baos.toByteArray();
                System.out.println(6);
                String encImage = Base64.encodeToString(b, Base64.DEFAULT);
                System.out.println(7); */
                        }
                    });
                }
                catch (Exception e) {
                    Log.d(getString(R.string.app_name), Log.getStackTraceString(e));
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
