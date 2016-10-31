package com.tier5.redeemar.RedeemarConsumerApp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tier5.redeemar.RedeemarConsumerApp.DisplayFailureActivity;
import com.tier5.redeemar.RedeemarConsumerApp.async.DownloadBitmapTask;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.ImageDownloadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Address;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class Utils {

    private static final String LOGTAG = "Utils";
    private final int TYPE_LOGO = 1;
    private final int TYPE_OFFER = 2;
    private static final String text10="OOOOOOOOOO";

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
            Log.d(LOGTAG, "Exception occured in exporting");
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

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }


    public static String getFormattedAddress(Address address){

        String fullAddr = "";

        if(address.getStreet() != null && !address.getStreet().equals(""))
            fullAddr = address.getStreet();

        if(address.getLocation() != null && !address.getLocation().equals(""))
            fullAddr = fullAddr + ", " + address.getLocation();

        if(address.getCity() != null && !address.getCity().equals("") && !address.getCity().equalsIgnoreCase(address.getLocation()))
            fullAddr = fullAddr + ", " + address.getCity();

        if(address.getState() != null && !address.getState().equals(""))
            fullAddr = fullAddr + ", " + address.getState();

        if(address.getZip() != null && !address.getZip().equals(""))
            fullAddr = fullAddr + ", " + address.getZip();

        return fullAddr;
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String calculateDiscount(Double retailVal, Double payVal, int type) {
        Double discount = 0.0;
        String disc = "";
        DecimalFormat precision = new DecimalFormat("#.00");

        // Type = 1 stands for absolute value
        // Type = 2 stands for % value

        try {


            if(type == 1) {
                discount = retailVal - payVal;
                disc = String.valueOf(discount);
                disc = precision.format(discount);
            }
            else {
                discount = ((retailVal - payVal) / retailVal)*100;
                disc = String.valueOf(Math.round(discount));
            }




        } catch (Exception ex) {
            Log.d(LOGTAG, "Exception occurred in discount calculation "+ex.toString());
        }


        return disc;

    }

    public static String formatPrice(Double price) {

        String sPrice = "";
        DecimalFormat precision = new DecimalFormat("#.##");
        sPrice = precision.format(price);
        return sPrice;
    }



    public static File getFile(String dirName, String fileName) {
        File mediaStorageDirLogo = new File(Environment.getExternalStorageDirectory(), dirName);
        String path = mediaStorageDirLogo.getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file;
    }


    public static String saveToInternalStorage(Bitmap bitmapImage, String filename){

        File mediaStorageDirLogo = new File(Environment.getExternalStorageDirectory(), Constants.logoDir);

        if (!mediaStorageDirLogo.exists()) {
            if (!mediaStorageDirLogo.mkdirs()) {
                Log.d("App", "failed to create directory");
            }
        }

        /*ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);*/

        // Create imageDir
        File mypath = new File(mediaStorageDirLogo, filename);

        Log.d(LOGTAG, "Saving image: "+mediaStorageDirLogo.getAbsolutePath());

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            //bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //fos.close();
        }
        return mediaStorageDirLogo.getAbsolutePath();
    }


    public static boolean setImageView(String imageFilePath, ImageView holder) {
        Bitmap myBitmap = BitmapFactory.decodeFile(imageFilePath);
        holder.setImageBitmap(myBitmap);

        return true;
    }


    public static boolean showLogoImageFromStorage(String fileName, ImageView imView) {

        if(Utils.getFile(Constants.logoDir, fileName).exists()) {
            String imgFilePath = Environment.getExternalStorageDirectory() +"/"+ Constants.logoDir;
            Log.d(LOGTAG, "Logo Path: " + imgFilePath);
            File mediaStorageDirLogo = new File(imgFilePath, fileName);

            Utils.setImageView(mediaStorageDirLogo.getAbsolutePath(), imView);
            return true;
        }
        return false;

    }



    public static boolean findDuplicate(ArrayList<String> listItems, String item) {
        //Log.d(LOGTAG, "Duplicate Items: "+listItems.size());
        //Log.d(LOGTAG, "An Item: "+item);

        for(int i=0; i < listItems.size(); i++) {
            if(listItems.get(i).equals(item))
                return true;
        }

        return false;
    }


    public static boolean checkLocationPermissions(Activity activity, Context ctx) {


        int REQUEST_ID_MULTIPLE_PERMISSIONS = 23;

        String[] permissions = new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };

        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(ctx,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    public static void noLocationFound(Context ctx, String msg){

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static void close(InputStream stream) {
        if(stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(OutputStream stream) {
        if(stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Paint adjustTextSize(Paint paint, int numCharacters, int widthPixels, int heightPixels) {
        float width = paint.measureText(text10)*numCharacters/text10.length();
        float newSize = (int)((widthPixels/width)*paint.getTextSize());
        paint.setTextSize(newSize);

        // remeasure with font size near our desired result
        width = paint.measureText(text10)*numCharacters/text10.length();
        newSize = (int)((widthPixels/width)*paint.getTextSize());
        paint.setTextSize(newSize);

        // Check height constraints
        Paint.FontMetricsInt metrics = paint.getFontMetricsInt();
        float textHeight = metrics.descent-metrics.ascent;
        if (textHeight > heightPixels) {
            newSize = (int)(newSize * (heightPixels/textHeight));
            paint.setTextSize(newSize);
        }

        return paint;
    }

    public static boolean urlExists(String url){

        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con =  (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("HEAD");
            System.out.println(con.getResponseCode());
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private Bitmap decodeFile(File f){
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        try {

            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            if (o.outHeight > 250 || o.outWidth > 500) {
                scale = (int)Math.pow(2, (int) Math.ceil(Math.log(500 /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();

        } catch(Exception ex) {
            Log.d(LOGTAG, "");
        }

        return b;
    }

}