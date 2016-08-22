package com.tier5.redeemar.RedeemarConsumerApp.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tier5.redeemar.RedeemarConsumerApp.DisplayFailureActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {

    private static final String LOGTAG = "Utils";

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public static void redirectToError(Context ctx) {
        Intent errIntent = new Intent(ctx, DisplayFailureActivity.class);
        ctx.startActivity(errIntent);
    }

    public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public static String extractLogToFileAndWeb(){
        //set a file
        Date datum = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String fullName = df.format(datum)+"appLog.log";
        File file = new File (Environment.getExternalStorageDirectory(), fullName);
        String logText = "";


        //clears a file
        if(file.exists()){
            file.delete();
        }


        //write log to file
        int pid = android.os.Process.myPid();
        try {
            String command = String.format("logcat -d -v threadtime *:*");
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String currentLine = null;

            while ((currentLine = reader.readLine()) != null) {
                if (currentLine != null && currentLine.contains(String.valueOf(pid))) {
                    result.append(currentLine);
                    result.append("\n\r");
                }
            }

            logText = result.toString();



            //Runtime.getRuntime().exec("logcat -d -v time -f "+file.getAbsolutePath());
        } catch (IOException e) {

            Log.d(LOGTAG, "Exception occured in exporting ");

        }


        //clear the log
        try {
            Runtime.getRuntime().exec("logcat -c");
        } catch (IOException e) {

            Log.d(LOGTAG, "Runtime Exception occured in exporting ");

        }

        return logText;
    }

    public static Bitmap getFacebookProfilePicture(String userID){

        try {
            String fbPicUrl = "https://graph.facebook.com/" + userID + "/picture?type=small";
            URL imageURL = new URL(fbPicUrl);
            Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

            return bitmap;
        } catch(Exception ex){
            Log.e(LOGTAG, "Error in getting profile pic: "+ex.toString());
        }

        return null;

    }


    public static String saveToInternalStorage(Context context, Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //fos.close();
        }
        return directory.getAbsolutePath();
    }



}