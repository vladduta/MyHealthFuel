package com.healthfuel.myhealthfuel.mainpage;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.lifecycle.LifecycleOwner;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;
import com.healthfuel.myhealthfuel.R;

public class RepsCountActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    PreviewView previewView;
    PoseDetectorOptions options =
            new PoseDetectorOptions.Builder()
                    .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                    .build();

    PoseDetector poseDetector = PoseDetection.getClient(options);

    Canvas canvas;

    //Paint mPaint = new Paint();
    Paint pointPaint = new Paint();
    Paint linePaint = new Paint();
    RepsCountDisplay display;
    Button startSetButton, stopSetButton, saveDataButton;

    Bitmap bitmap4Save;
    Chronometer chronometer;

    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    ArrayList<Bitmap> bitmap4DisplayArrayList = new ArrayList<>();

    ArrayList<Pose> poseArrayList = new ArrayList<>();

    ArrayList<Integer> restBetweenSetsArrayList = new ArrayList<>();
    ArrayList<Integer> repsArrayList = new ArrayList<>();


    boolean isRunning = false;
    int reps = 0;
    int sets = 0;
    int restBetweenSets = 0;
    String stage = "";
    boolean startBtn = false;
    boolean stopBtn = false;
    ProcessCameraProvider cameraProvider;
    @ExperimentalGetImage
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reps_count);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        previewView = findViewById(R.id.previewView);
        display = findViewById(R.id.displayOverlay);
        startSetButton = findViewById(R.id.startSetButton);
        stopSetButton = findViewById(R.id.stopSetButton);
        saveDataButton = findViewById(R.id.saveDataButton);
        chronometer = findViewById(R.id.chronometer);

        startSetButton.setVisibility(View.VISIBLE);
        stopSetButton.setVisibility(View.GONE);

        //mPaint.setColor(Color.GREEN);
        //mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //mPaint.setStrokeWidth(10);

        pointPaint.setColor(Color.GREEN);
        pointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        pointPaint.setStrokeWidth(10);

        linePaint.setColor(Color.MAGENTA);
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setStrokeWidth(10);


        cameraProviderFuture.addListener(() -> {
            try {
                 cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {

            }
        }, ContextCompat.getMainExecutor(this));



        startSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBtn=true;
                stopSetButton.setVisibility(View.VISIBLE);
                startSetButton.setVisibility(View.GONE);
                if(stopBtn) {
                    restBetweenSets = (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000);
                    chronometer.stop();
                    restBetweenSetsArrayList.add(restBetweenSets);
                    chronometer.setBase(SystemClock.elapsedRealtime()); // Resetez cronometrul la zero

                    // Leg din nou preview-ul camerei
                    bindPreview(cameraProvider);
                }
                stopBtn=false;
                saveDataButton.setVisibility(View.GONE);


            }
        });

        stopSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBtn=false;
                stopBtn=true;
                sets++;
                repsArrayList.add(reps);
                reps = 0;

                startSetButton.setVisibility(View.VISIBLE);
                stopSetButton.setVisibility(View.GONE);

                chronometer.setBase(SystemClock.elapsedRealtime()); // Resetez cronometrul la zero
                chronometer.start();
                saveDataButton.setVisibility(View.VISIBLE);

                // Opreste camera
                cameraProviderFuture.addListener(() -> {
                    try {
                         cameraProvider = cameraProviderFuture.get();
                        cameraProvider.unbindAll();
                    } catch (ExecutionException | InterruptedException e) {
                    }
                }, ContextCompat.getMainExecutor(RepsCountActivity.this));
            }
        });



        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calc media aritmetica a listelor
                int  meanReps = calculateMean(repsArrayList);
                int  meanRestBetweenSets = calculateMean(restBetweenSetsArrayList);

                Intent intent = new Intent();
                intent.putExtra("sets", sets);
                intent.putExtra("reps", meanReps);
                intent.putExtra("rest", meanRestBetweenSets);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    Runnable RunMlkit = new Runnable() {
        @Override
        public void run() {
            poseDetector.process(InputImage.fromBitmap(bitmapArrayList.get(0),0)).addOnSuccessListener(new OnSuccessListener<Pose>() {
                @Override
                public void onSuccess(Pose pose) {
                    poseArrayList.add(pose);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    };



    @ExperimentalGetImage
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ActivityCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                ByteBuffer byteBuffer = imageProxy.getImage().getPlanes()[0].getBuffer();// buffer-ul de date care contine informatiile brute ale imaginii.
                byteBuffer.rewind();
                Bitmap bitmap = Bitmap.createBitmap(imageProxy.getWidth(), imageProxy.getHeight(), Bitmap.Config.ARGB_8888); //reprezentarea imaginii sub forma unui grid de pixeli
                bitmap.copyPixelsFromBuffer(byteBuffer);

                Matrix matrix = new Matrix();
                matrix.postRotate(rotationDegrees);
                matrix.postScale(-1,1);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap,0,0,imageProxy.getWidth(), imageProxy.getHeight(),matrix,false);

                bitmapArrayList.add(rotatedBitmap);

                if (poseArrayList.size() >= 1) {
                    canvas = new Canvas(bitmapArrayList.get(0));
                    Pose pose = poseArrayList.get(0);
                    // Desenarea landmark-urilor
                    List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
                    for (PoseLandmark poseLandmark : landmarks) {
                        float landmarkX = poseLandmark.getPosition().x;
                        float landmarkY = poseLandmark.getPosition().y;
                        canvas.drawCircle(landmarkX, landmarkY, 5, pointPaint);
                    }

                    // Desenarea liniilor intre landmark-urile cheie
                    //drawConnection(canvas, pose, PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER);
                    drawConnection(canvas, pose, PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW);
                    //drawConnection(canvas, pose, PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_ELBOW);
                    drawConnection(canvas, pose, PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST);
                    //drawConnection(canvas, pose, PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_WRIST);
                    //drawConnection(canvas, pose, PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP);
                    //drawConnection(canvas, pose, PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP);
                    //drawConnection(canvas, pose, PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP);
                    //drawConnection(canvas, pose, PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE);
                    //drawConnection(canvas, pose, PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE);
                    //drawConnection(canvas, pose, PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE);
                    //drawConnection(canvas, pose, PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE);
                    double angle = calculateAngle(pose, PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST);




                    // Implementarea logicii de numarare a repetitarilor
                    if(startBtn) {
                    if (angle > 160) {
                        stage = "down";
                    }
                    if (angle < 50 && stage.equals("down")) {
                        stage = "up";
                        reps++;
                        display.setReps(reps);
                    }
                        display.setStage(stage);
                        display.setReps(reps);
                        display.setSets(sets);
                        display.setAngle(angle);

                        bitmap4DisplayArrayList.clear();
                        bitmap4DisplayArrayList.add(bitmapArrayList.get(0));
                        bitmap4Save = bitmapArrayList.get(bitmapArrayList.size()-1);
                        bitmapArrayList.clear();
                        bitmapArrayList.add(bitmap4Save);
                        poseArrayList.clear();
                        isRunning = false;
                    }

                }

                if (poseArrayList.size() == 0 && bitmapArrayList.size() >= 1 && !isRunning ) {
                    RunMlkit.run();
                    isRunning = true;
                }

                if (bitmap4DisplayArrayList.size() >= 1) {
                    display.getBitmap(bitmap4DisplayArrayList.get(0));
                }

                imageProxy.close();
            }
        });

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis, preview);
    }




    // Metoda pentru a desena o linie intre doua landmark-uri
    private void drawConnection(Canvas canvas, Pose pose, int startLandmark, int endLandmark) {
        PoseLandmark start = pose.getPoseLandmark(startLandmark);
        PoseLandmark end = pose.getPoseLandmark(endLandmark);
        if (start != null && end != null) {
            canvas.drawLine(start.getPosition().x, start.getPosition().y,
                    end.getPosition().x, end.getPosition().y, linePaint);
        }
    }

    // Metoda pentru a calcula unghiul intre trei puncte
    private double calculateAngle(Pose pose, int landmark1, int landmark2, int landmark3) {
        PoseLandmark point1 = pose.getPoseLandmark(landmark1);
        PoseLandmark point2 = pose.getPoseLandmark(landmark2);
        PoseLandmark point3 = pose.getPoseLandmark(landmark3);

        if (point1 != null && point2 != null && point3 != null) {
            // Calcularea diferentelor de coordonate pentru fiecare punct
            double dx1 = point1.getPosition().x - point2.getPosition().x;
            double dy1 = point1.getPosition().y - point2.getPosition().y;
            double dx2 = point3.getPosition().x - point2.getPosition().x;
            double dy2 = point3.getPosition().y - point2.getPosition().y;

            // Calcularea produsului scalar al vectorilor dintre puncte
            double dotProduct = (dx1 * dx2) + (dy1 * dy2);

            // Calculare magnitudine pt fiecare vector
            double magnitude1 = Math.sqrt((dx1 * dx1) + (dy1 * dy1));
            double magnitude2 = Math.sqrt((dx2 * dx2) + (dy2 * dy2));

            // Calcularea cosinusul unghiului intre vectori folosind produsul scalar si magnitudinea
            double cosineAngle = dotProduct / (magnitude1 * magnitude2);
            // Calcularea unghiului in radiani
            double angle = Math.acos(cosineAngle);

            // Converteste unghiul din radiani in grade
             angle = Math.abs(angle * 180.0 / Math.PI);

            return angle;
        } else {
            return 0;
        }
    }

    private int calculateMean(ArrayList<Integer> list) {
        if (list.isEmpty()) {
            return 0;
        }
        int sum = 0;

        for (int num : list) {
            sum += num;
        }

        int mean =  sum / list.size();
        return mean;
    }


}