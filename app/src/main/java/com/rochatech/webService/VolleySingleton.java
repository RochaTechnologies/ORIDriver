/*
 * Created by Adrian Joshet Moreno Fabian on 5/3/18 9:08 PM .
 * Rocha Technologies de Mexico SA de CV
 * soporte@rochatech.com
 */

package com.rochatech.webService;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

@SuppressWarnings({"WeakerAccess","unused","FieldCanBeLocal"})
public class VolleySingleton {

    public RequestQueue requestQueue;
    public Context appContext;
    public static VolleySingleton instanceVolley;

    public VolleySingleton(Context mContext) {
        this.appContext = mContext;
        this.requestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getVolleySingleton(Context context){
        if (instanceVolley == null){
            return new VolleySingleton(context);
        }
        return instanceVolley;
    }

    public RequestQueue getRequestQueue(){
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(appContext.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }
}
