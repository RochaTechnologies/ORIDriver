package com.rochatech.oridriver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.rochatech.webService.*;
import com.rochatech.library.Common;
import org.json.JSONArray;
import java.io.ByteArrayOutputStream;

public class Settings_PersonalInfo extends AppCompatActivity {

    //region Global
    ImageView _profilepic;
    EditText _publicname;
    TextView _givenname, _mobile;
    Button _saveInformation;
    connectToService _svcConnection;
    Common obj;
    //endregion

    //region Profile Picture
    private static final int PICK_IMAGE = 100;
    Uri ImageURI;
    private boolean IsProfilePicSet = false;
    Bitmap imageSelected;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_personalinfo_activity);
        obj = new Common(Settings_PersonalInfo.this);
        _svcConnection = new connectToService(Settings_PersonalInfo.this, obj.GetSharedPreferencesValue(Settings_PersonalInfo.this, "SessionToken"));
        obj.ShowLoadingScreen(Settings_PersonalInfo.this,"Estamos cargando tu información");
        InitAppControls();
        InitPersonalInfo();
        _profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        _saveInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.ShowLoadingScreen(Settings_PersonalInfo.this,"Actualizando información");
                String nickName = _publicname.getText().toString();
                String mobile = _mobile.getText().toString();
                int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Settings_PersonalInfo.this,"UID"));
                String Errors = CheckForErrors(nickName, mobile);
                if (Errors.trim().isEmpty()){
                    UpdateNickNameNMobile(UID, nickName, mobile);
                } else {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Settings_PersonalInfo.this,Errors,getResources().getString(R.string.ORIGlobal_AnyErrorsTitle),"Error");
                }
            }
        });

        //region Hide keyboard
        _publicname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Settings_PersonalInfo.this);
            }
        });
        _mobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Settings_PersonalInfo.this);
            }
        });
        //endregion
    }

    //region WebService Call
    private void UpdateNickNameNMobile(int UID, final String nickName, final String mobile) {
        _svcConnection.UpdateNickNameNCell(UID, nickName, mobile, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        obj.CloseLoadingScreen();
                        Common.LogoffByInvalidToken(Settings_PersonalInfo.this);
                        break;
                    case "NO_CONNECTION":
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(Settings_PersonalInfo.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                if (SaveSharedPreferences(nickName, mobile)) {
                    if (IsProfilePicSet) {
                        UpdateProfilePicture(imageSelected);
                    } else {
                        obj.CloseLoadingScreen();
                        LinearLayoutCompat MainLinear = findViewById(R.id.MainLinear);
                        Snackbar snackbar = Snackbar.make(MainLinear,"¡Información actualizada!",Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                } else {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Settings_PersonalInfo.this,getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Msg),getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Title),"Error");
                }
            }
        });
    }
    private void UpdateProfilePicture(Bitmap imageSelected) {
        int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Settings_PersonalInfo.this,"UID"));
        int origWidth = imageSelected.getWidth();
        int origHeight = imageSelected.getHeight();
        final int destWidth = 100;
        int destHeight = origHeight/( origWidth / destWidth ) ;
        Bitmap finalPic = Bitmap.createScaledBitmap(imageSelected, destWidth, destHeight, false);
        if(origWidth > destWidth){
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            finalPic.compress(Bitmap.CompressFormat.JPEG,70 , outStream);
        }
        _svcConnection.UpdateProfilePicture(UID, imageSelected, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        obj.CloseLoadingScreen();
                        Common.LogoffByInvalidToken(Settings_PersonalInfo.this);
                        break;
                    case "NO_CONNECTION":
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(Settings_PersonalInfo.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                obj.CloseLoadingScreen();
                LinearLayoutCompat MainLinear = findViewById(R.id.MainLinear);
                Snackbar snackbar = Snackbar.make(MainLinear,"¡Información actualizada!",Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
    }
    //endregion

    //region Set / Save Gallery picture selected on ImageButton
    private void OpenGallery() {
        Intent oGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(oGallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            ImageURI = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = Settings_PersonalInfo.this.getContentResolver().query(ImageURI,projection,null,null,null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            imageSelected = BitmapFactory.decodeFile(filePath);
            Drawable drawable = new BitmapDrawable(imageSelected);
            _profilepic.setImageBitmap(null);
            _profilepic.setBackground(drawable);
            IsProfilePicSet = true;
        }
    }
    //endregion

    //region Init
    private void InitAppControls() {
        _profilepic = findViewById(R.id.NewProfilePic);
        _givenname = findViewById(R.id.NewGivenName);
        _publicname = findViewById(R.id.NewNickName);
        _mobile = findViewById(R.id.NewMobile);
        _saveInformation = findViewById(R.id.SaveProfileInfo);
    }
    private void InitPersonalInfo() {
        String fullName = obj.GetSharedPreferencesValue(Settings_PersonalInfo.this,"LastName") + ", " + obj.GetSharedPreferencesValue(Settings_PersonalInfo.this,"GivenName");
        _givenname.setText(fullName);
        _publicname.setText(obj.GetSharedPreferencesValue(Settings_PersonalInfo.this,"NickName"));
        _mobile.setText(obj.GetSharedPreferencesValue(Settings_PersonalInfo.this,"Mobile"));
        Drawable drawable = new BitmapDrawable(_svcConnection.GetProfilePictureFromUID(Integer.parseInt(obj.GetSharedPreferencesValue(Settings_PersonalInfo.this,"UID"))));
        _profilepic.setImageBitmap(null);
        _profilepic.setBackground(drawable);
        obj.CloseLoadingScreen();
    }
    //endregion

    //region MISC
    private String CheckForErrors(String nickName, String mobile) {
        String result = "";
        result += (nickName.trim().isEmpty()) ? getResources().getString(R.string.SettingsPersonalInfo_NewNickNameEmpty) : "";
        result += (!nickName.matches("^[ A-Za-zéáíóúñÑüÁÉÍÓÚÜ]+$")) ? "Favor de solo utilizar letras en tu nombre público" + "\n" : "";
        result += (mobile.trim().isEmpty()) ? getResources().getString(R.string.SettingsPersonalInfo_NewMobileEmpty) + "\n" : "";
        return result;
    }
    private Boolean SaveSharedPreferences(String nickname, String mobile) {
        try {
            SharedPreferences preferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("NickName", nickname);
            edit.putString("Mobile", mobile);
            edit.apply();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    //endregion
}
