package com.hotmail.uuzzmaa.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends Activity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    public static final String Age = "ageKey";
    public static final String Nationality = "nationalityKey";
    public static final String Gender = "genderKey";
    public static final String Entered = "enteredKey";

    // label our logs "CameraApp3"
    private static String logtag = "CameraApp3";
    // tells us which camera to take a picture from
    private static int TAKE_PICTURE = 1;
    // empty variable to hold our image Uri once we store it
    private Uri imageUri;


    SharedPreferences sharedpreferences;
    private RadioGroup radioGroup;
    EditText editName , editAge;
    String name, nationality , gender ;
    int age;
    Button done;
    RadioButton radioButton;
    Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editName = (EditText)findViewById(R.id.editName);
        editAge = (EditText) findViewById(R.id.editAge);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.nationality, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nationality = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        Button button = (Button) findViewById(R.id.done);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(checkValid() == true){
                            int selectedId = radioGroup.getCheckedRadioButtonId();
                            radioButton = (RadioButton) findViewById(selectedId);
                            Log.d("TEST", "id : " + radioButton.getId());
                            gender = radioButton.getText().toString();
//                        Toast.makeText(MainActivity.this,radioButton.getText(),Toast.LENGTH_SHORT).show();


                            name = editName.getText().toString();
                            age = Integer.parseInt(editAge.getText().toString());

                            SharedPreferences.Editor editor = sharedpreferences.edit();

                            editor.putString(Name, name);
                            editor.putInt(Age, age);
                            editor.putString(Nationality, nationality);
                            editor.putString(Gender, gender);
                            if (!name.isEmpty()) {
                                editor.putBoolean(Entered, true);
                            } else {
                                editor.putBoolean(Entered, false);
                            }
                            editor.commit();
                            Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                           // go_to_Categories(); CHANGE CATEGORIES TO YOUR PAGE ACTIVITY NAME
                        }
                    }
                }
        );

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        //Camera Starts

        Button cameraButton = (Button) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(cameraListener);

        //Camera Ends


    }

    private View.OnClickListener cameraListener = new View.OnClickListener() {
        public void onClick(View v) {
            // open the camera and pass in the current view
            takePhoto(v);
        }
    };

    public void takePhoto(View v) {
        // tell the phone we want to use the camera
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // create a new temp file called pic.jpg in the "pictures" storage area of the phone
        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "pic.jpg");
        // take the return data and store it in the temp file "pic.jpg"
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        // store the temp photo uri so we can find it later
        imageUri = Uri.fromFile(photo);
        // start the camera
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // call the parent
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            // if the requestCode was equal to our camera code (1) then...
            case 1:
                // if the user took a photo and selected the photo to use
                if(resultCode == Activity.RESULT_OK) {
                    // get the image uri from earlier
                    Uri selectedImage = imageUri;
                    // notify any apps of any changes we make
                    getContentResolver().notifyChange(selectedImage, null);
                    // get the imageView we set in our view earlier
                    ImageView imageView = (ImageView)findViewById(R.id.cameraView);
                    // create a content resolver object which will allow us to access the image file at the uri above
                    ContentResolver cr = getContentResolver();
                    // create an empty bitmap object
                    Bitmap bitmap;
                    try {
                        // get the bitmap from the image uri using the content resolver api to get the image
                        bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
                        // set the bitmap to the image view
                        imageView.setImageBitmap(bitmap);
                        // notify the user
                        Toast.makeText(MainActivity.this, selectedImage.toString(), Toast.LENGTH_LONG).show();
                    } catch(Exception e) {
                        // notify the user
                        Toast.makeText(MainActivity.this, "failed to load", Toast.LENGTH_LONG).show();
                        Log.e(logtag, e.toString());
                    }
                }
        }
    }

    public boolean checkValid(){
        Log.d("TEST", "name : " + editName.getText());
        if(editName.getText().length() == 0){
            Toast.makeText(getApplicationContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
            return false;
        }else if(editAge.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter your age", Toast.LENGTH_SHORT).show();
            return false;
        }else if(spinner.getSelectedItem().toString() == getString(R.string.nationality_default)) {
            Toast.makeText(getApplicationContext(), "Please select your nationality", Toast.LENGTH_SHORT).show();
            return false;
        }else if(radioGroup.getCheckedRadioButtonId() == -1 ) {
            Toast.makeText(getApplicationContext(), "Please select your gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        //       Log.d("TEST", "check Valid/ name : " + editName.getText() + " age : " + editAge.getText() + " nationality : " + spinner.getSelectedItem().toString()
        //      + " gender :  " + radioButton.getText().toString());
        return true;
    }

   //CHANGE CATEGORIES TO YOUR PAGE ACTIVITY NAME
   // private void go_to_Categories() {
     //   Log.d("TEST", "name : " + name + " age : " + age + " nationality : " + nationality + " gender : " + gender);
    //    Intent intent = new Intent(this, Categories.class);
    //    startActivity(intent);
    //}



    //   @Override
    //   public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    //       getMenuInflater().inflate(R.menu.menu_second_page, menu);
    //      return true;
    //   }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(getApplicationContext(),
//                "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
//                Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
}
