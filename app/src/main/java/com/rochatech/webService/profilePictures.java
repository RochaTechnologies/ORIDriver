/*
 *
 *  * Created by Adrian Joshet on 4/23/18 6:47 PM
 *  * Rocha Technologies de Mexico SA de CV
 *  * soporte@rochatech.com
 *
 */

package com.rochatech.webService;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class profilePictures extends AsyncTask<String, Void, Bitmap> {

    @Override
    protected Bitmap doInBackground(String... imageurls) {
        URL url;
        HttpURLConnection httpURLConnection;
        final String basicAuth = "Basic " + Base64.encodeToString("RochaTech:Rocha2018".getBytes(), Base64.NO_WRAP);
        try {
            url = new URL(imageurls[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty ("Authorization", basicAuth);
            httpURLConnection.connect();
            InputStream in = httpURLConnection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(in);
            return myBitmap;
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
