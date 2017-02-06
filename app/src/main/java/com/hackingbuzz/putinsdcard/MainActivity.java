package com.hackingbuzz.putinsdcard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Bitmap bitmap;
    ImageView imageView1,imageView2,imageView3;

    public static int IMAGE_GETTING = 2;
    public static int IMAGE_PUTTING = 1;


    public void getLocationOfPhoto() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  // content:// (url) - External_Content_Uri ..location location of images in sd card (MediaStore)
        startActivityForResult(intent, IMAGE_GETTING);

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == IMAGE_PUTTING) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                storeImageInSdCard(bitmap);

            }

          

            else if(requestCode == IMAGE_GETTING) {

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // i guess we just take 1 permiision at a time when we acces camera like ..or another time when we click get libaray ....on their own time they will store at grant[0] location u are just required to check request code

                    getLocationOfPhoto();
                }

            }
        }
    }







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);

        Button cameraButton = (Button) findViewById(R.id.cameraButton);
        Button getImage = (Button) findViewById(R.id.getImage);

        cameraButton.setOnClickListener(this);
        getImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.cameraButton) {  // open camera

            // we are taking picture through camera n sending it through intent we dont need to use putExtra for sending the images coz we taking constant to click the picture and see constatnt value is going in intenet and we are sening itnetn
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // we captured the image and sending it directly
            startActivityForResult(intent, IMAGE_PUTTING);

        } else if(v.getId() == R.id.getImage) {  // open library

            if(Build.VERSION.SDK_INT < 23) {

                getLocationOfPhoto();

            } else
                // for alove L version we need to get permission at the door to get throught that location ...
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {  // checkSelfPermission is a method avail in 23 api ..without if condition of (SDK_INT < 23 ) you cant implement it..

                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},IMAGE_GETTING );

                } else {

                    getLocationOfPhoto();
                }



        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  //
        super.onActivityResult(requestCode, resultCode, data);

        // IMAGE_PUTTING code is for putting image in sd card by clicking through camera  and GETTING_IMAGE is for getting image from sd card

        if(requestCode == IMAGE_PUTTING && resultCode == RESULT_OK && data != null) {

            bitmap = (Bitmap) data.getExtras().get("data");   // geting intent data with getExtra and getting value of key (data) using get method

            // now got the image time to store it in sd card

            // get path to sd card n store image in it

            if(Build.VERSION.SDK_INT < 23) {

                storeImageInSdCard(bitmap);

            } else
                // for alove L version we need to get permission at the door to get throught that location ...
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {  // checkSelfPermission is a method avail in 23 api ..without if condition of (SDK_INT < 23 ) you cant implement it..

                    requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},IMAGE_PUTTING );

                } else {

                    storeImageInSdCard(bitmap);
                }



        }  else

            if(requestCode == IMAGE_GETTING && resultCode == RESULT_OK && data != null){
                Uri uri =  data.getData();  // getting location (ofcourse it a uri) ..got that in uri variable so that we can get image from there
                // lets get the image from there //  lets go to media store to get the image

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);  // we need contentResolver to get the content...without him we cant get the content...

                    if(imageView1.getDrawable() == null) {
                   imageView1.setImageBitmap(bitmap);
                    return; }     // Bang!  return matlab iske aage vala code na run ho...... agar image null hai to image laga dega n code se bahar dusri image nai lagaega ..n jab dusri baar run hoga to phala image view null nai hoga to ofcurse dusre pe jayega n image lagega..

                    if(imageView2.getDrawable() == null) {
                        imageView2.setImageBitmap(bitmap);
                        return;
                    }

                    if(imageView3.getDrawable() == null) {
                        imageView3.setImageBitmap(bitmap);
                        return;
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

// code to put image in sd card

    public void storeImageInSdCard(Bitmap bitmap) {

        File sdFile = Environment.getExternalStorageDirectory();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(sdFile); // read ulta ..writing output stream to file sd
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
