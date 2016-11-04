package com.tier5.redeemar.RedeemarConsumerApp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by tier5 on 3/11/16.
 */
public class DownloadAndReadImage
{

    private static final String LOGTAG = "DownloadAndReadImage";
    String strURL, imgFilename, dir;
    int pos;
    Bitmap bitmap=null;


    // pass image url and Pos for example i:
    public DownloadAndReadImage(String url, String filename, int type)
    {
        this.strURL = url;
        this.imgFilename = filename;
        this.pos = type;

        if(type == 1)
            this.dir = Constants.logoDir;
        if(type == 2)
            this.dir = Constants.brandImgDir;
        else if(type == 3)
            this.dir = Constants.storeImgDir;
        else
            this.dir = Constants.offerDir;
    }


    public void getDownloadImage()
    {
        downloadBitmapImage();
        //return readBitmapImage();
    }

    public Bitmap getBitmapImage()
    {
        //downloadBitmapImage();
        return readBitmapImage();
    }

    void downloadBitmapImage()
    {
        InputStream input;
        try {
            URL url = new URL(strURL);
            input = url.openStream();
            byte[] buffer = new byte[1500];
            String imgFilePath = Environment.getExternalStorageDirectory() +"/"+ dir;

            File mediaStorageDirLogo = new File(Environment.getExternalStorageDirectory(), dir);

            if (!mediaStorageDirLogo.exists()) {
                if (!mediaStorageDirLogo.mkdirs()) {
                    Log.d("App", "failed to create directory");
                }
            }
            OutputStream output = new FileOutputStream(imgFilePath+"/"+imgFilename);
            try
            {
                int bytesRead = 0;
                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0)
                {
                    output.write(buffer, 0, bytesRead);
                }
            }
            finally
            {
                output.close();
                buffer=null;
            }
        }
        catch(Exception e)
        {
            //Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
            Log.d(LOGTAG, "Exception: "+e.toString());
        }
    }

    Bitmap readBitmapImage()
    {
        String imageInSD = "/sdcard/mac/"+strURL;
        BitmapFactory.Options bOptions = new BitmapFactory.Options();
        bOptions.inTempStorage = new byte[16*1024];
        bitmap = BitmapFactory.decodeFile(imageInSD,bOptions);
        return bitmap;
    }

}
