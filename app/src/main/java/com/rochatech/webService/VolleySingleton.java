/*
 * Created by Adrian Joshet Moreno Fabian on 5/3/18 9:08 PM .
 * Rocha Technologies de Mexico SA de CV
 * soporte@rochatech.com
 */

package com.rochatech.webService;

import android.content.Context;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {

    public RequestQueue requestQueue;
    public Context appContext;
    /*WARNING FROM COMMIT = Do not place Android context classes in static fields (static reference to `VolleySingleton` which has field `appContext` pointing to `Context`); this is a memory leak (and also breaks Instant Run)*/
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
