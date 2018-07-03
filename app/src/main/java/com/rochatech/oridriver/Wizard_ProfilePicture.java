package com.rochatech.oridriver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.rochatech.library.Common;
import com.rochatech.webService.*;

public class Wizard_ProfilePicture extends AppCompatActivity {

    connectToService _svcConnection;
    Common obj;

    ImageButton UserPicture;
    EditText UserNickName, UserLastName, UserGivenName;
    Button MaleGenderPressed, FemaleGenderPressed, ContinuePressed;

    private int GenderSelected = 0;
    private boolean IsProfilePicSet;
    private static final int PICK_IMAGE = 100;
    Uri ImageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard_profilepicture_activity);
        obj = new Common(Wizard_ProfilePicture.this);
        _svcConnection = new connectToService(Wizard_ProfilePicture.this, obj.GetSharedPreferencesValue(Wizard_ProfilePicture.this, "SessionToken"));
        InitAppControls();
        UserPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        MaleGenderPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaleGenderSelected();
            }
        });
        FemaleGenderPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FemaleGenderSelected();
            }
        });
        ContinuePressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartCreatingUserAct();
            }
        });

        //region Hide Keyboard
        UserNickName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Wizard_ProfilePicture.this);
            }
        });
        UserLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Wizard_ProfilePicture.this);
            }
        });
        UserGivenName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Wizard_ProfilePicture.this);
            }
        });
        //endregion
    }

    //region Gallery
    private void OpenGallery() {
        Intent oGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(oGallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            ImageURI = data.getData();
            UserPicture.setScaleType(ImageView.ScaleType.FIT_CENTER);
            UserPicture.setAdjustViewBounds(true);
            UserPicture.setBackgroundColor(Color.TRANSPARENT);
            UserPicture.setImageURI(ImageURI);
            String [] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
            Cursor cur = getContentResolver().query(ImageURI, orientationColumn, null, null, null);
            int orientation = -1;
            int NeedRotation = 0;
            if (cur != null && cur.moveToFirst()) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                NeedRotation = 1;
            }
            if (NeedRotation == 1) {
                UserPicture.buildDrawingCache(true);
                Bitmap Bmap = Bitmap.createBitmap(UserPicture.getDrawingCache(true));
                Matrix mat = new Matrix();
                mat.postRotate(orientation);
                Bitmap bMapRot = Bitmap.createBitmap(Bmap, 0, 0, Bmap.getWidth(), Bmap.getHeight(), mat, false);
                UserPicture.setImageBitmap(bMapRot);
            }
            IsProfilePicSet = true;
        }
    }
    //endregion

    //region Gender selection
    private void MaleGenderSelected() {
        GenderSelected = 1;
        MaleGenderPressed.setBackgroundColor(Color.rgb(0, 177, 106));
        MaleGenderPressed.setTextColor(Color.WHITE);
        FemaleGenderPressed.setBackgroundColor(Color.WHITE);
        FemaleGenderPressed.setTextColor(Color.rgb(0, 177, 106));
    }
    private void FemaleGenderSelected() {
        GenderSelected = 2;
        FemaleGenderPressed.setBackgroundColor(Color.rgb(0, 177, 106));
        FemaleGenderPressed.setTextColor(Color.WHITE);
        MaleGenderPressed.setBackgroundColor(Color.WHITE);
        MaleGenderPressed.setTextColor(Color.rgb(0, 177, 106));
    }
    //endregion

    private void StartCreatingUserAct() {
        //TODO no olvidar la imagen del usuario
//        obj.ShowLoadingScreen(Wizard_ProfilePicture.this, "Por favor espere", "Estamos guardando sus datos...");
        String nickname = UserNickName.getText().toString();
        String genderselected = Integer.toString(GenderSelected);
        String lastname = UserLastName.getText().toString();
        String givenname = UserGivenName.getText().toString();
        String Errors = AnyErrors(nickname, genderselected, lastname, givenname);
        if (Errors.trim().isEmpty()) {
            /*Shared preferences*/
            try {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("NickName", nickname);
                edit.putString("Gender", genderselected);
                edit.putString("LastName", lastname);
                edit.putString("GivenName", givenname);
                edit.apply();
                Intent intent = new Intent(Wizard_ProfilePicture.this, Wizard_CreateAccount.class);
                startActivity(intent);
            } catch (Exception e) {
                obj.CloseLoadingScreen();
//                Common.DialogStatusAlert(Wizard_ProfilePicture.this, getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Msg), getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Title), false, "Error");
            }
        } else {
            obj.CloseLoadingScreen();
//            Common.DialogStatusAlert(Wizard_ProfilePicture.this, Errors, getResources().getString(R.string.ORIGlobal_AnyErrorsTitle), false, "Error");
        }
    }

    private String AnyErrors(String nickname, String genderselected, String lastname, String givenname) {
        String result = "";
        result += (!IsProfilePicSet) ? getResources().getString(R.string.WizardProfilePicture_ProfilePictureEmpty) : "";
        result += (nickname.trim().isEmpty()) ? getResources().getString(R.string.WizardProfilePicture_NickNameEmpty) + "\n" : "";
        result += (lastname.trim().isEmpty()) ? getResources().getString(R.string.WizardProfilePicture_LastNameEmpty) + "\n" : "";
        result += (givenname.trim().isEmpty()) ? getResources().getString(R.string.WizardProfilePicture_GivenNameEmpty) + "\n" : "";
        result += (genderselected.trim().isEmpty() || Integer.parseInt(genderselected) < 1) ? getResources().getString(R.string.WizardProfilePicture_GenderEmpty) + "\n" : "";
        return result;
    }

    //region MISC
    private void InitAppControls() {
        UserPicture = findViewById(R.id.UserProfilePicture);
        UserNickName = findViewById(R.id.UserNickName);
        UserLastName = findViewById(R.id.UserLastName);
        UserGivenName = findViewById(R.id.UserGivenName);
        MaleGenderPressed = findViewById(R.id.MaleGender);
        FemaleGenderPressed = findViewById(R.id.FemaleGender);
        ContinuePressed = findViewById(R.id.ContinuePressed);
    }
    //endregion
}
