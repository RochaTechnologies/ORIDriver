package com.rochatech.oridriver;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                obj.ShowLoadingScreen(context,"Guardando información");
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
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.animator.anim_slide_inright, R.animator.anim_slide_outleft);
                    obj.CloseLoadingScreen();
                    transaction.replace(R.id.dialogframecontainer,fragment).commit();
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
        Intent oGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(oGallery, PICK_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            ImageURI = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(ImageURI,projection,null,null,null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            imageSelected = BitmapFactory.decodeFile(filePath);
            Drawable drawable = new BitmapDrawable(imageSelected);
            btnProfilePicturePressed.setImageBitmap(null);
            btnProfilePicturePressed.setBackground(drawable);
            IsProfilePicSet = true;
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
        result += (!givenName.matches("^[ A-Za-z]+$")) ? "Favor de solo utilizar letras en tu nombre" + "\n" : "";
        result += (!lastName.matches("^[ A-Za-z]+$")) ? "Favor de solo utilizar letras en tu apellido" + "\n" : "";
        result += (!nickName.matches("^[ A-Za-z]+$")) ? "Favor de solo utilizar letras tu nombre público" + "\n" : "";
        return result;
    }
    //endregion
}
