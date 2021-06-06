package org.tensorflow.lite.examples.detection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.detection.customview.OverlayView;
import org.tensorflow.lite.examples.detection.env.ImageUtils;
import org.tensorflow.lite.examples.detection.env.Logger;
import org.tensorflow.lite.examples.detection.env.Utils;
import org.tensorflow.lite.examples.detection.tflite.Classifier;
import org.tensorflow.lite.examples.detection.tflite.YoloV4Classifier;
import org.tensorflow.lite.examples.detection.tracking.MultiBoxTracker;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
//import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity1 extends AppCompatActivity implements View.OnClickListener{

    public  String name, number;
    public  int ocrRead;
    public Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    @BindView(R.id.register) Button mRegisterMeterButton;
    @BindView(R.id.customerName) EditText mCustomerName;
    @BindView(R.id.customerNumber) EditText mCustomerNumber;
    @BindView(R.id.ocr) TextView capturedText;
    @BindView(R.id.detect) Button Detect;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.take_picture)Button pictureBtn;
    @BindView(R.id.logout) ImageView logout;
    @BindView(R.id.history) Button historyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        ButterKnife.bind(this);
        mRegisterMeterButton.setOnClickListener(this);
        Detect.setOnClickListener(this);
        pictureBtn.setOnClickListener(this);
        logout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity1.this, AddReadings.class);
        if(v== mRegisterMeterButton){

            number=mCustomerNumber.getText().toString();
            name=mCustomerName.getText().toString();
            intent.putExtra("Cus",name);
            intent.putExtra("num",number);
            startActivity(intent);
        }

        if(v== Detect){
            detectTextFromImage();
        }
        if(v== historyBtn){

        }
        if(v == pictureBtn){
            dispatchTakePictureIntent();
        }

        if(v == logout){
            logout();
        }

    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity1.this, login_form.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void dispatchTakePictureIntent() {
        /*explicit intent to open phone camera*/
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!= null){
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);//key,value
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");//getting the image from the intent
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void detectTextFromImage(){
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector detector =  FirebaseVision.getInstance().getVisionTextDetector();

        detector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText){
        List<FirebaseVisionText.Block> blockList = firebaseVisionText.getBlocks();
        if(blockList.size() == 0){
            Toast.makeText(MainActivity1.this, "No numbers found", Toast.LENGTH_SHORT).show();
        }
        else {
            for(FirebaseVisionText.Block block : firebaseVisionText.getBlocks()){
                String text = block.getText();
                capturedText.setText(text);
            }
        }
    }
}

