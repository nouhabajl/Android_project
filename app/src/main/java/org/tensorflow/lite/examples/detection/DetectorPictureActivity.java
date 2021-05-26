/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.detection;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/*import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;*/

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
public class DetectorPictureActivity extends AppCompatActivity {

    private Uri imageUri;
    private static final int PHOTO_REQUEST = 1001;
    private static final int REQUEST_WRITE_PERMISSION = 2002;


    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.TITLE, "New Picture");
            contentValues.put(MediaStore.Images.Media.DESCRIPTION, "From the camera");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);


            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, PHOTO_REQUEST);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "We don't found any camera app", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Rejected!", Toast.LENGTH_SHORT).show();
                } else {
                    takePicture();
                }
        }
    }

    private Bitmap rotateBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

    }


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {
                Bitmap bitmap = rotateBitmap(getImage());
                imageView.setImageBitmap(bitmap);
                new Handler().post(() -> {
                    TensorImage tensorImage = TensorImage.fromBitmap(bitmap);
                    ObjectDetector.ObjectDetectorOptions build = ObjectDetector.ObjectDetectorOptions.builder()
                            .setMaxResults(5)
                            .setScoreThreshold(0.4F)
                            .build();
                    try {
                        ObjectDetector detector = ObjectDetector.createFromFileAndOptions(
                                this,
                                "yolov4-tiny-416.tflite",
                                build
                        );
                        List<Detection> result = detector.detect(tensorImage);
                        List<DetectionResult> list = new ArrayList<>();
                        for (Detection detection : result) {
                            list.add(new DetectionResult(detection.getBoundingBox(), detection.getCategories().get(0).getScore() + ""));
                        }
                        Bitmap resultNitamp = drawResult(bitmap, list);
                        runOnUiThread(() ->
                                imageView.setImageBitmap(resultNitamp));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception ex) {
                Toast.makeText(this, "Errrorr ", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    static class DetectionResult {
        public RectF boundingBox;
        public String text;


        DetectionResult(RectF boundingBox, String text) {
            this.boundingBox = boundingBox;
            this.text = text;
        }

    }


    Bitmap drawResult(
            Bitmap bitmap,
            List<DetectionResult> list
    ) {
        Bitmap output = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(output);
        Paint pen = new Paint();
        pen.setTextAlign(Paint.Align.LEFT);
        for (DetectionResult detectionResult : list) {
            pen.setColor(Color.RED);
            pen.setStrokeWidth(8F);
            pen.setStyle(Paint.Style.STROKE);
            RectF boundingBox = detectionResult.boundingBox;
            canvas.drawRect(boundingBox, pen);
            Rect tag = new Rect(0, 0, 0, 0);
            pen.setStyle(Paint.Style.FILL_AND_STROKE);
            pen.setColor(Color.YELLOW);
            pen.setStrokeWidth(2F);
            pen.setTextSize(62);
            pen.getTextBounds(detectionResult.text, 0, detectionResult.text.length(), tag);
            float fontSize = pen.getTextSize() * boundingBox.width() / tag.width();
            if (fontSize < pen.getTextSize())
                pen.setTextSize(fontSize);

            float margin = (boundingBox.width() - tag.width()) / 2.0F;
            if (margin < 0F)
                margin = 0F;
            canvas.drawText(
                    detectionResult.text, boundingBox.left + margin,
                    boundingBox.top + tag.height() * 1F, pen
            );
        }

        return output;
    }


    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private Bitmap getImage() throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, bmOptions);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_detect);
        imageView = (ImageView) findViewById(R.id.imageView);
        findViewById(R.id.take_picture).setOnClickListener(v -> {
            ActivityCompat.requestPermissions(this, new
                    String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_WRITE_PERMISSION);
        });
    }

    ImageView imageView;
}
