/*
 *
 *  * Created by Adrian Joshet on 4/23/18 6:47 PM
 *  * Rocha Technologies de Mexico SA de CV
 *  * soporte@rochatech.com
 *
 */

package com.rochatech.webService;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import com.rochatech.oridriver.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
@SuppressWarnings({"WeakerAccess","unused","FieldCanBeLocal"})
public class profilePictures extends AsyncTask<String, Void, Bitmap> {

    Context context;
    public void SetContext(Context context){
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... imageurls) {
        URL url;
        HttpURLConnection httpURLConnection;
        final String basicAuth = "Basic " + Base64.encodeToString("RochaTech:Rocha2018".getBytes(), Base64.NO_WRAP);
        try {
            url = new URL(imageurls[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            //Dev
            SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.ORIGlobal_SharedPreferences),Context.MODE_PRIVATE);
            String env = pref.getString("reqenv_environment","");
            if (!env.trim().isEmpty()) {
                if (env.contains("Dev")) {
                    httpURLConnection.setRequestProperty ("Authorization", basicAuth);
                }
            }
            httpURLConnection.setRequestProperty ("Authorization", basicAuth);
            httpURLConnection.connect();
            InputStream in = httpURLConnection.getInputStream();
            return BitmapFactory.decodeStream(in);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
