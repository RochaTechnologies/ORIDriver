/*
 * Created by Adrian Joshet Moreno Fabian on 5/3/18 8:20 PM .
 * Rocha Technologies de Mexico SA de CV
 * soporte@rochatech.com
 */

package com.rochatech.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.rochatech.oridriver.R;
import com.rochatech.oridriver.Wizard_Login;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mx.openpay.android.Openpay;

public class Common {

    private ProgressDialog loading;

    public Common () {}
    public Common(Context context) {
        loading = new ProgressDialog(context);
    }

    public Boolean isInternetConnectionActive(Context context) {
        Boolean result;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        result = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return result;
    }


    //region OpenPay
    /** Funcion de openpay que regresa el objeto para iniciar con la asignacion / creacion de tarjetas
     * @return El objeto para poder comenzar a crear las tarjetas
     */
    public Openpay getOpenPay() {
        String _opMerchantId = "ml6xm8ytukxrevjxic0b";
        String _publicKey = "pk_4a00567cf73a4532837597a8485f2093";
        return new Openpay(_opMerchantId, _publicKey, false);
    }
    //endregion


    //region Invalid TokenSession Logoff
    public static void LogoffByInvalidToken(final Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.template_dialogstatusalert,null);
        TextView dialogTitle = dialogView.findViewById(R.id.alertDialogTitle);
        TextView dialogMsg = dialogView.findViewById(R.id.alertDialogMsg);
        ImageView dialogIcon = dialogView.findViewById(R.id.alertDialogIcon);
        dialogTitle.setText(context.getResources().getString(R.string.ORI_Common_SessionExpired_Title));
        dialogMsg.setText(context.getResources().getString(R.string.ORI_Common_SessionExpired_Msg));
        dialogIcon.setImageResource(R.drawable.ic_error);
        dialog.setView(dialogView);
        dialog.setPositiveButton(context.getString(R.string.ORIOkString), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, Wizard_Login.class);
                context.startActivity(intent);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
    //endregion


    //region Create Dialogs, Msg, etc
    /** Funcion que muestra alertas de mensaje
     * @param context Es el contexto de la actividad
     * @param msg El mensaje que se va a mostrar
     * @param title Titulo de la alerta
     * @param iconName Nombre del icono que va en el dialog
     */
    public static void DialogStatusAlert(Context context, String msg, String title, String iconName) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            View dialogView = inflater.inflate(R.layout.template_dialogstatusalert,null);
            TextView dialogTitle = dialogView.findViewById(R.id.alertDialogTitle);
            TextView dialogMsg = dialogView.findViewById(R.id.alertDialogMsg);
            ImageView dialogIcon = dialogView.findViewById(R.id.alertDialogIcon);
            dialogTitle.setText(title);
            dialogMsg.setText(msg);
            switch (iconName) {
                case "Error":
                    dialogIcon.setImageResource(R.drawable.ic_error);
                    break;
                case "Success":
                    dialogIcon.setImageResource(R.drawable.ic_success);
                    break;
            }
            dialog.setView(dialogView);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    /** Crea una pantallla estilo dialog que funciona como pantalla de espera
     * @param context Contexto de la actividad
     * @param msg Mensaje del dialog, puede ser vacio
     */
    public void ShowLoadingScreen(Context context, String msg) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            View loadingView = inflater.inflate(R.layout.template_dialogloading,null);
            TextView loadingMsg = loadingView.findViewById(R.id.dialogLoadingMsg);
            loadingMsg.setText(msg);
            loading.setMessage(msg);
            loading.setCancelable(false);
            loading.show();
            loading.setContentView(loadingView);
        }
    }


    /**
     * Cierra la pantalla de carga
     */
    public void CloseLoadingScreen() {
        loading.dismiss();
    }


    /** Regresa el dialog creado por ShowLoadingScreen para poder cerrarlo o detenerlo
     * @return El progressDialog creado por ShowLoadingScreen
     */
    public ProgressDialog GetProgressDialogLoadinScreen() {
        return loading;
    }
    //endregion


    //region Validate Formats

    /** Funcion revisa si el correo cumple con el siguiente formato correo@dominio.com
     * @param base El correo a validar
     * @return Verdadero si el correo cumple con el formato
     */
    public static Boolean IsEmail(String base) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(base);
        return mat.matches();
    }
    //endregion


    //region SharedPreferences

    /** Funcion que elimina todos los parametros guardados en las preferencias del usuario
     * @param context Contexto de la actividad
     */
    public static void DeleteAllSharedPreferences(Context context) {
        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.clear();
        edit.apply();
    }

    /** Funcion que busca el valor del keyvalue en las preferencias
     * @param context Contexto de la actividad
     * @param keyvalue Keyvalue del valor en las preferencias
     * @return Devuelve el valor guardado de las preferencias
     */
    public String GetSharedPreferencesValue(Context context, String keyvalue) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
        return preferences.getString(keyvalue, null);
    }
    //endregion


    //region Dates

    /** Muestra la fecha actual
     * @return Fecha actual
     */
    public static Date getNow(){
        Date date = new Date();
        return  formatNowToAppNow(date);
    }

    /** Muestra la fecha actual formateada
     * @param appNow Fecha actual
     * @return Fecha actual formateada
     */
    private static Date formatNowToAppNow(Date appNow){
        try{
            String format = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat dateFormatter = new SimpleDateFormat(format, Locale.US);
            return dateFormatter.parse(dateFormatter.format(appNow));
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getAppStringFullDateFromFullDate(Date appNow){
        try{
            String format = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat dateFormatter = new SimpleDateFormat(format, Locale.US);
            return dateFormatter.format(appNow);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Date getAppFullDateFromAppStringFullDate(String appNow){
        if(appNow.isEmpty()){
            return null;
        }
        try{
            String format = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat dateFormatter = new SimpleDateFormat(format, Locale.US);
            return dateFormatter.parse(appNow);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Funcion que formatea fechas en distintos formatos
     * @param comp El tipo de formato a aplicar
     * @param FromDate La fecha
     * @return La fecha formateada segun el formato
     */
    public static String getDateComp(String comp, Date FromDate) {
        SimpleDateFormat dateFormatter = null;
        switch (comp) {
            case "Time":
                dateFormatter = new SimpleDateFormat("hh:mm a", Locale.US);
                break;
            case "Day":
                dateFormatter = new SimpleDateFormat("dd", Locale.US);
                break;
            case "Month":
                dateFormatter = new SimpleDateFormat("MMM", Locale.US);
                break;
            case "MonthNum":
                dateFormatter = new SimpleDateFormat("MM", Locale.US);
                break;
            case "MonthFull":
                dateFormatter = new SimpleDateFormat("MMMM", Locale.US);
                break;
            case "Year":
                dateFormatter = new SimpleDateFormat("yyyy", Locale.US);
                break;
            case "YearLastDigits":
                dateFormatter = new SimpleDateFormat("yy", Locale.US);
                break;
        }
        if (dateFormatter != null) {
            return dateFormatter.format(FromDate);
        } else {
            return null;
        }
    }
    //endregion


    //region Log Errors
    public String LogDeviceError (Exception error) {
        StackTraceElement[] rawStackTrace = error.getStackTrace();
        StringBuilder fullStackTrace = new StringBuilder();
        for (int i = 0; i < rawStackTrace.length; i++) {
            fullStackTrace.append(rawStackTrace[i]);
        }
        return fullStackTrace.toString();
    }

    //endregion


    /** Funcion que oculta el teclado al dar un toque en cualquier lugar de la pantalla
     * @param view La vista en la que esta la actividad
     * @param context El contexto de la actividad
     */
    public static void HideKeyboard(View view, Context context) {
        InputMethodManager inputMM = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMM != null) {
            inputMM.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /** Funcion que encripta cadenas de caracteres
     * @param base String para encriptar
     * @return String ya encriptada
     */
    public static String SHA256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte n: hash) {
                String hex = Integer.toHexString(0xff & n);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

//            for (int i = 0; i < hash.length; i++) {
//                String hex = Integer.toHexString(0xff & hash[i]);
//                if(hex.length() == 1) hexString.append('0');
//                hexString.append(hex);
//            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
