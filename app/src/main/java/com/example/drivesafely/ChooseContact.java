/**
 * Author : Reece Gavin
 * ID Number : 17197589
 * Date last modified: 17/02/2021
 */

package com.example.drivesafely;

//Import all necessary dependencies
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class ChooseContact extends Fragment implements EasyPermissions.PermissionCallbacks {

    //Declare private variables
    private static final int RESULT_PICK_CONTACT = 1;
    private static final String TAG = "Settings";

    //Declare public variables
    public static String CONTACT_PREF = "sharedPrefs";
    public static String phoneNumber;
    public static String TEXT = "text";
    public static String hello;
    private static boolean contactSelected = true;
    private TextView numberTextview, messageTextview;
    private Button selectButton;

    /* Adds permission check when the user clicks to choose a contact. If the permission has already
    * been accepted, when this method is called, a new intent is started to allow the user to select
    * theirr desired contact*/
    @AfterPermissionGranted(123)
    private void requestPermissions() {
        String[] perms = {Manifest.permission.READ_CONTACTS};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(in, RESULT_PICK_CONTACT);
        } else {
            EasyPermissions.requestPermissions(this, "Permissions need to be " +
                            "accepted in order to use the application",
                    123, perms);
        }
    }

    //Method used to determine what is done if a permission is denied
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

        // If the permission READ_CONTACTS is denied, the below dialog box is shown
        if (EasyPermissions.somePermissionDenied(this, permissions)) {

            new AlertDialog.Builder(getActivity())
                    .setTitle("Permissions Needed")
                    .setMessage("Permissions need to be accepted in order to use the application")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //The user is returned to the dashboard screen on clicking OK
                            getActivity().finish();
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            };
        }

    //Unused required method
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    //Method used if some permission is permanently denied.
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
         /* If some permission is permanently denied,
           the user is brought to their phone settings, where they can manually
           accept the apps permissions*/
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            getActivity().finish();
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    //Unused required method
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Method used to inflate the layout of this fragment file
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_contact, container, false);
        }
    }

    /* Method used to set different button and textview values and call
    required methods to enable user to choose an emergency contact*/
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //Buttons, TextViews and CardViews are found in the layout using findViewById
        numberTextview = view.findViewById(R.id.phone);
        selectButton = view.findViewById(R.id.select);
        messageTextview = view.findViewById(R.id.contact_message);

        try {
            //Load previously saved contact
            loadContact();

            //Set message and button text
            if (phoneNumber.startsWith("No")) {
                contactSelected = false;
                messageTextview.setText("Please Select an Emergency Contact ");
                selectButton.setText("Select Emergency Contact");

            } else {
                selectButton.setText("Deselect  Contact");
                messageTextview.setText("Your chosen emergency contact number is ");
            }
        }
        // Catch exception if there is any error loading previously saved contact
        catch (Exception e) {
            e.printStackTrace();
        }

        // Button listener to allow user to pick their contact
        selectButton.setOnClickListener(v -> {
            if (contactSelected) {
                contactSelected = false;
                TEXT = "";
                phoneNumber = null;
                numberTextview.setText("No Phone number selected");
                selectButton.setText("Select Emergency Contact");
                messageTextview.setText("Please Select an Emergency Contact ");
                saveContact();
            } else {
                requestPermissions();
            }
        });
    }

    //Method used to check if a contact was picked or not
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Toast.makeText(getActivity(), "Failed To pick contact", Toast.LENGTH_SHORT).show();
        }
    }

    //Method used to assign the phone number chosen to the phoneNumber variable and save it.
    private void contactPicked(Intent data) {
        Cursor cursor = null;

        try {
            String phoneNo = null;
            Uri uri = data.getData();
            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phoneNo = cursor.getString(phoneIndex);

            // Define strings to be excluded
            String toRemove = "-";
            String space = " ";
            String plus = "\\+";

            //If statement to remove hyphens, plus symbols and spaces from the displayed phone number
            if (phoneNo.contains(toRemove) || phoneNo.contains(space) || phoneNo.contains(plus)) {
                phoneNo = phoneNo.replaceAll(toRemove, "");
                phoneNo = phoneNo.replaceAll(plus, "");
                phoneNo = phoneNo.replaceAll(space, "");

            }

            //Display chosen contact number
            numberTextview.setText(phoneNo);
            messageTextview.setText("Your chosen emergency contact number is ");
            phoneNumber = phoneNo;
            contactSelected = true;
            Log.i(TAG, "Phone number is " + phoneNumber);

            //Set text of button and save contact number to its assigned sharedPreference
            selectButton.setText("Deselect Contact");
            saveContact();
        }

        // Catch any exceptions that may occur when choosing the contact
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Method to save chosen contact number into its assigned sharePreference
    private void saveContact() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(CONTACT_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CONTACT_PREF, phoneNumber);
        editor.apply();
        Log.i(TAG, "(SavedData) Phone Number is " + CONTACT_PREF);
    }

    //Method to load chosen contact number from its assigned sharePreference
    public void loadContact() {
        SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences(CONTACT_PREF, MODE_PRIVATE);
        phoneNumber= sharedPreferences2.getString(CONTACT_PREF,"No phone number chosen");
        numberTextview.setText(phoneNumber);
        Log.i(TAG, "(LoadData) Phone is " + phoneNumber);

    }
}

