/*
 * Created by Adrian Joshet Moreno Fabian on 5/3/18 9:12 PM .
 * Rocha Technologies de Mexico SA de CV
 * soporte@rochatech.com
 */

package com.rochatech.webService;

import org.json.JSONArray;

public interface WSResponseListener {
    void onError(String message);

    void onResponseObject(JSONArray jsonResponse);
}
