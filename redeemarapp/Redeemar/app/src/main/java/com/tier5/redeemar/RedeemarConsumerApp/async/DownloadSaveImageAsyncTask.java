package com.tier5.redeemar.RedeemarConsumerApp.async;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import com.tier5.redeemar.RedeemarConsumerApp.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadSaveImageAsyncTask extends AsyncTask<String, Integer, String> {
    Bitmap map = null;
    ByteArrayOutputStream bs;
    final Context context;
    ProgressDialog progressDialog;
    private String dirPath = "/redeemar/logos";

    public static final String URL3 =
            "http://www.w3schools.com/html/pic_mountain.jpg";
    public static final String URL1 =
            "http://www.w3schools.com/html/pic_mountain.jpg";
    public static final String URL2 =
            "http://www.w3schools.com/html/pic_mountain.jpg";

    private URL myUrl = null;
    private File sdRoot;
    private String dir;
    private String fileName,fileName2,fileName3,filepath;
    FileOutputStream out;
    int count=0;

    Button recyclerbtn,download;
    boolean issdcard=false;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private int REQUEST_ID_MULTIPLE_PERMISSIONS = 23;

    int noOfURLs;
    public DownloadSaveImageAsyncTask(Activity context) {
        Log.d("Download", "Inside download");
        this.context = context;
    }

    @Override
    protected String doInBackground(String... urls) {
        noOfURLs = urls.length;

        for (String url : urls) {
            map = downloadImage(url);

            if (map != null) {

                if(issdcard){
                    count++;
                    fileName = dirPath + "/" + count + ".jpg";
                    File mkDir = new File(sdRoot, dir);
                    mkDir.mkdirs();
                    File file = new File(sdRoot, dir + fileName);
                    filepath=file.getAbsolutePath();
                    if (file.exists()) file.delete();
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        options.inDither = false;
                        //options.inPurgeable = true;
                        //options.inInputShareable = true;
                        options.inTempStorage = new byte[32 * 1024];
                        options.inPreferredConfig = Bitmap.Config.RGB_565;

                        out = new FileOutputStream(file);
                        map.compress(Bitmap.CompressFormat.PNG, 0, out);

                        out.flush();
                        out.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(issdcard==false){
                    //Movie movie = new Movie(map);
                    //movieList.add(movie);
                }

            }
        }
        return null;
    }

    private Bitmap downloadImage(String urlString) {

        int count = 0;
        Bitmap bitmap = null;

        URL myUrl;
        InputStream inputStream = null;
        BufferedOutputStream outputStream = null;

        try {
            myUrl = new URL(urlString);
            URLConnection connection = myUrl.openConnection();
            int lenghtOfFile = connection.getContentLength();

            inputStream = new BufferedInputStream(myUrl.openStream());
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();

            outputStream = new BufferedOutputStream(dataStream);

            byte data[] = new byte[512];
            long total = 0;

            while ((count = inputStream.read(data)) != -1) {
                total += count;
                publishProgress((int)((total*100)/lenghtOfFile));

                // writing data to byte array stream
                outputStream.write(data, 0, count);
            }
            outputStream.flush();

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            byte[] bytes = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,bmOptions);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.close(inputStream);
            Utils.close(outputStream);
        }
        return bitmap;
    }

    protected void onProgressUpdate(Integer... progress) {

    }
    @Override
    protected void onPostExecute(String result1) {
        //progressDialog.dismiss();
        if(issdcard==false) {
            //mAdapter.notifyDataSetChanged();
        }else if(issdcard){
            count=0;
            //Start_dialog start_dialog = new Start_dialog(MainActivity.this, "Download complete.Check the files at "+filepath);
            //start_dialog.dialogbox();
        }
    }

}