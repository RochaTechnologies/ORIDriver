package com.rochatech.oridriver;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import static android.app.Activity.RESULT_OK;
import com.rochatech.library.Common;
import com.rochatech.webService.*;

import java.io.IOException;

public class Fragment_Wizard_ProfilePicture extends Fragment {

    Context context;
    Button btnCreateAccountPressed;
    ImageButton btnProfilePicturePressed;
    RadioGroup rdbContainer;
    RadioButton rdbMaleSelected, rdbFemaleSelected;
    EditText txtUserGivenName, txtUserLastName, txtUserNickName;
    View view;
    Bitmap imageSelected;
    connectToService _svcConnection;
    Common obj;
    private boolean IsProfilePicSet;
    private static final int PICK_IMAGE = 100;
    private static final int REQUEST_READ_STORAGE = 200;
    Uri ImageURI;

    public Fragment_Wizard_ProfilePicture() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wizard_profilepicture, container, false);
        obj = new Common(context);
        _svcConnection = new connectToService(context, obj.GetSharedPreferencesValue(context, "SessionToken"));
        InitFragControls();
        btnProfilePicturePressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        btnCreateAccountPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.ShowLoadingScreen(context,getResources().getString(R.string.ORISavingInfo));
                String GivenName = txtUserGivenName.getText().toString();
                String LastName = txtUserLastName.getText().toString();
                String NickName = txtUserNickName.getText().toString();
                String GenderSelected = "";
                int radioSelected = rdbContainer.getCheckedRadioButtonId();
                switch (radioSelected) {
                    case R.id.rdbMaleSelected:
                        GenderSelected = "H";
                        break;
                    case R.id.rdbFemaleSelected:
                        GenderSelected = "M";
                        break;
                }
                String Errors = AnyErrors(GivenName, LastName, NickName, IsProfilePicSet);
                if (Errors.trim().isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("ori_givenname",GivenName);
                    bundle.putString("ori_lastname",LastName);
                    bundle.putString("ori_nickname",NickName);
                    bundle.putString("ori_genderselected",GenderSelected);
                    Fragment_Wizard_CreateAccount fragment = new Fragment_Wizard_CreateAccount();
                    fragment.SetProfilePicture(imageSelected);
                    fragment.setArguments(bundle);
                    if (getFragmentManager() != null) {
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.animator.anim_slide_inright, R.animator.anim_slide_outleft);
                        obj.CloseLoadingScreen();
                        transaction.replace(R.id.dialogframecontainer,fragment).commit();
                    }
                } else {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(context,Errors,getResources().getString(R.string.ORIGlobal_AnyErrorsTitle),"Error");
                }
            }
        });

        //region Hide keyboard
        txtUserGivenName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, context);
            }
        });
        txtUserLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, context);
            }
        });
        txtUserNickName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, context);
            }
        });
        //endregion

        return view;
    }

    //region Gallery
    private void OpenGallery() {
        Boolean hasPermissions = (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED);
        if (!hasPermissions) {
            if (getActivity() != null) {
                ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_READ_STORAGE);
            }
        } else {
            Intent oGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(oGallery, PICK_IMAGE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            ImageURI = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};
            if (getActivity() != null) {
                Cursor cursor = getActivity().getContentResolver().query(ImageURI,projection,null,null,null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    Integer currentOrientation = GetOrientation(filePath);
                    if (currentOrientation != null) {
                        if (currentOrientation > 0) {
                            Bitmap tmp = BitmapFactory.decodeFile(filePath);
                            imageSelected = RotateBitmap(tmp,currentOrientation);
                        } else {
                            imageSelected = BitmapFactory.decodeFile(filePath);
                        }
                        Drawable drawable = new BitmapDrawable(getResources(),imageSelected);
                        btnProfilePicturePressed.setImageBitmap(null);
                        btnProfilePicturePressed.setBackground(drawable);
                        IsProfilePicSet = true;
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)  {
            case REQUEST_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)  {
                    Intent oGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(oGallery, PICK_IMAGE);
                }
            }
        }
    }

    private Integer GetOrientation(String filepath) {
        Integer orientation = null;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exif != null) {
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        }
        return orientation;
    }
    private Bitmap RotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
    //endregion

    //region MISC
    private void InitFragControls() {
        btnCreateAccountPressed = view.findViewById(R.id.btnCreateAccountPressed);
        btnProfilePicturePressed = view.findViewById(R.id.btnProfilePicturePressed);
        rdbContainer = view.findViewById(R.id.rdbContainer);
        rdbMaleSelected = view.findViewById(R.id.rdbMaleSelected);
        rdbFemaleSelected = view.findViewById(R.id.rdbFemaleSelected);
        txtUserGivenName = view.findViewById(R.id.txtUserGivenName);
        txtUserLastName = view.findViewById(R.id.txtUserLastName);
        txtUserNickName = view.findViewById(R.id.txtUserNickName);
    }
    private String AnyErrors(String givenName, String lastName, String nickName, boolean isProfilePicSet) {
        String result = "";
        result += (!isProfilePicSet) ? getResources().getString(R.string.WizardProfilePicture_ProfilePictureEmpty) + "\n" : "";
        result += (!givenName.matches("^[ A-Za-zéáíóúñÑüÁÉÍÓÚÜ]+$")) ? "Favor de solo utilizar letras en tu nombre" + "\n" : "";
        result += (!lastName.matches("^[ A-Za-zéáíóúñÑüÁÉÍÓÚÜ]+$")) ? "Favor de solo utilizar letras en tu apellido" + "\n" : "";
        result += (!nickName.matches("^[ A-Za-zéáíóúñÑüÁÉÍÓÚÜ]+$")) ? "Favor de solo utilizar letras tu nombre público" + "\n" : "";
        return result;
    }
    //endregion
}
