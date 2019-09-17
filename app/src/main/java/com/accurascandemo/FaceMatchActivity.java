package com.accurascandemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.Provider;
import java.text.NumberFormat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accurascandemo.util.AppGeneral;
import com.accurascandemo.util.SendResultToServer;
import com.accurascandemo.view.CustomTextView;
import com.docrecog.scan.CameraActivity;
import com.docrecog.scan.FileUtils;
import com.docrecog.scan.RecogEngine;
import com.docrecog.scan.Util;
import com.inet.facelock.callback.FaceCallback;
import com.inet.facelock.callback.FaceDetectionResult;
import com.inet.facelock.callback.FaceLockHelper;

public class FaceMatchActivity extends Activity implements FaceCallback {
    int ind;

    MyView image1;
    MyView image2;
    CustomTextView txtScore;
    boolean bImage2 = false;
    boolean bImage1 = false;

    float[] inputFeature;
    float[] matchFeature;

    final private int PICK_IMAGE = 1;
    final private int CAPTURE_IMAGE = 2;

    Bitmap face1, face2 = null;

    private boolean isEmailSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facematch);

        initEngine();
        findViewById(R.id.btnGallery1).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                isEmailSent = false;
                ind = 1;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, ""), PICK_IMAGE);
            }
        });
        findViewById(R.id.btnCamera1).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                isEmailSent = false;
                ind = 1;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                Uri uriForFile = FileProvider.getUriForFile(
                        FaceMatchActivity.this,
                        "com.accurascan.demoapp.provider",
                        f
                );
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
                startActivityForResult(intent, CAPTURE_IMAGE);
            }
        });
        findViewById(R.id.btnGallery2).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                isEmailSent = false;
                ind = 2;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, ""), PICK_IMAGE);
            }
        });
        findViewById(R.id.btnCamera2).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                isEmailSent = false;
                ind = 2;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                Uri uriForFile = FileProvider.getUriForFile(
                        FaceMatchActivity.this,
                        "com.accurascan.demoapp.provider",
                        f
                );
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
                startActivityForResult(intent, CAPTURE_IMAGE);
            }
        });

        findViewById(R.id.ivBack).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txtScore = (CustomTextView) findViewById(R.id.tvScore);
        txtScore.setText("Match Score : 0 %");

        image1 = new MyView(this);
        image2 = new MyView(this);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                if (data == null)
                    return;
                Bitmap bmp = rotateImage(FileUtils.getPath(this,data.getData()));
                Bitmap nBmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
               /* if (face1 == null) {
                    face1 = nBmp;
                } else {
                    face2 = nBmp;
                }*/
                if (ind == 1) {
                    face1 = nBmp;
                    image1.setImage(nBmp);
                    SetImageView1();
                } else if (ind == 2) {
                    face2 = nBmp;
                    image2.setImage(nBmp);
                    SetImageView2();
                }
                int w = nBmp.getWidth();
                int h = nBmp.getHeight();
                int s = (w * 32 + 31) / 32 * 4;
                ByteBuffer buff = ByteBuffer.allocate(s * h);
                nBmp.copyPixelsToBuffer(buff);
                if (ind == 1)
                    FaceLockHelper.DetectLeftFace(buff.array(), w, h);
                else {
                    if (image1.getFaceDetectionResult() != null) {
                        FaceLockHelper.DetectRightFace(buff.array(), w, h, image1.getFaceDetectionResult().getFeature());
                    } else {
                        FaceLockHelper.DetectRightFace(buff.array(), w, h, null);
                    }
                }
            } else if (requestCode == CAPTURE_IMAGE) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                File ttt = null;
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        ttt = temp;
                        break;
                    }
                }
                if (ttt == null)
                    return;
                Bitmap bmp = rotateImage(ttt.getAbsolutePath());
                ttt.delete();
                Bitmap nBmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
              /*  if (face1 == null) {
                    face1 = nBmp;
                } else {
                    face2 = nBmp;
                }*/

                if (ind == 1) {
                    face1 = nBmp;
                    image1.setImage(nBmp);
                    SetImageView1();
                } else if (ind == 2) {
                    face2 = nBmp;
                    image2.setImage(nBmp);
                    SetImageView2();
                }
                int w = nBmp.getWidth();
                int h = nBmp.getHeight();
                int s = (w * 32 + 31) / 32 * 4;
                ByteBuffer buff = ByteBuffer.allocate(s * h);
                nBmp.copyPixelsToBuffer(buff);
                if (ind == 1)
                    FaceLockHelper.DetectLeftFace(buff.array(), w, h);
                else {
                    if (image1.getFaceDetectionResult() != null) {
                        FaceLockHelper.DetectRightFace(buff.array(), w, h, image1.getFaceDetectionResult().getFeature());
                    } else {
                        FaceLockHelper.DetectRightFace(buff.array(), w, h, null);
                    }
                }
            }
        }
    }

    private void SetImageView1() {
        if (!bImage1) {
            FrameLayout layout = (FrameLayout) findViewById(R.id.ivCardLayout);
            ImageView ivCard = (ImageView) findViewById(R.id.ivCard);
            image1.getLayoutParams().height = ivCard.getHeight();
            image1.requestLayout();
            layout.removeAllViews();
            layout.addView(image1);
            bImage1 = true;
        }
    }

    private void SetImageView2() {
        if (!bImage2) {
            FrameLayout layout2 = (FrameLayout) findViewById(R.id.ivFaceLayout);
            ImageView ivFace = (ImageView) findViewById(R.id.ivFace);
            image2.getLayoutParams().height = ivFace.getHeight();
            image2.requestLayout();
            layout2.removeAllViews();
            layout2.addView(image2);
            bImage2 = true;
        }
    }

    private Uri getUri() {
        String state = Environment.getExternalStorageState();
        if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    public String getAbsolutePath(Uri uri) {
        if (Build.VERSION.SDK_INT >= 19) {
            String arr[] = uri.getLastPathSegment().split(":");
            String id;
            if (arr.length > 1)
                id = arr[1];
            else
                id = uri.getLastPathSegment();
            final String[] imageColumns = {MediaStore.Images.Media.DATA};
            final String imageOrderBy = null;
            Uri tempUri = getUri();
            Cursor imageCursor = getContentResolver().query(tempUri, imageColumns,
                    MediaStore.Images.Media._ID + "=" + id, null, imageOrderBy);
            if (imageCursor.moveToFirst()) {
                return imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            } else {
                return null;
            }
        } else {
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } else
                return null;
       }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private Uri getImageUri(String path) {
         return Uri.fromFile(new File(path));
    }

    private Bitmap decodeFileFromPath(String path) {
        Uri uri = getImageUri(path);
        InputStream in = null;
        try {
            in = getContentResolver().openInputStream(uri);

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            int inSampleSize = 2048;
            if (o.outHeight > inSampleSize || o.outWidth > inSampleSize) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(inSampleSize / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = getContentResolver().openInputStream(uri);
            int MAXCAP_SIZE = 512;
            Bitmap b = getResizedBitmap(BitmapFactory.decodeStream(in, null, o2), MAXCAP_SIZE);
            in.close();

            return b;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap rotateImage(final String path) {
        Bitmap b = decodeFileFromPath(path);

        try {
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);

                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                    break;
                default:
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                    //b.copyPixelsFromBuffer(ByteBuffer.)
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return b;
    }

    private class MyView extends View {
        Bitmap image = null;
        FaceDetectionResult detectionResult = null;

        @SuppressLint("SdCardPath")
        public MyView(Context context) {
            super(context);

            @SuppressWarnings("deprecation")
            LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

            this.setLayoutParams(param);
        }

        public void setImage(Bitmap image) {
            this.image = Bitmap.createBitmap(image);
        }

        public Bitmap getImage() {
            return image;
        }

        public void setFaceDetectionResult(FaceDetectionResult result) {
            this.detectionResult = result;
        }

        public FaceDetectionResult getFaceDetectionResult() {
            return this.detectionResult;
        }

        @SuppressLint("DrawAllocation")
        @Override
        protected void onDraw(Canvas canvas) {
            if (image != null) {
                Rect clipRect = canvas.getClipBounds();
                int w = clipRect.width();
                int h = clipRect.height();
                int imgW = image.getWidth();
                int imgH = image.getHeight();

                float scaleX = ((float) w) / imgW;
                float scaleY = ((float) h) / imgH;
                float scale = scaleX;
                if (scaleX > scaleY)
                    scale = scaleY;
                imgW = (int) (scale * imgW);
                imgH = (int) (scale * imgH);
                Rect dst = new Rect();
                dst.left = (w - imgW) / 2;
                dst.top = (h - imgH) / 2;
                dst.right = dst.left + imgW;
                dst.bottom = dst.top + imgH;


                canvas.drawBitmap(image, null, dst, null);

                if (detectionResult != null) {
                    Paint myPaint = new Paint();
                    myPaint.setColor(Color.GREEN);
                    myPaint.setStyle(Paint.Style.STROKE);
                    myPaint.setStrokeWidth(2);
                    int x1 = (int) (detectionResult.getFaceRect().left * scale + dst.left);
                    int y1 = (int) (detectionResult.getFaceRect().top * scale + dst.top);
                    int x2 = (int) (detectionResult.getFaceRect().right * scale + dst.left);
                    int y2 = (int) (detectionResult.getFaceRect().bottom * scale + dst.top);

                    canvas.drawRect(x1, y1, x2, y2, myPaint);
                }
            }
        }
    }

    private void initEngine() {

        writeFileToPrivateStorage(R.raw.model, "model.prototxt");
        File modelFile = getApplicationContext().getFileStreamPath("model.prototxt");
        String pathModel = modelFile.getPath();
        writeFileToPrivateStorage(R.raw.weight, "weight.dat");
        File weightFile = getApplicationContext().getFileStreamPath("weight.dat");
        String pathWeight = weightFile.getPath();

        int nRet = FaceLockHelper.InitEngine(this, 30, 800, 1.18f, pathModel, pathWeight, this.getAssets());
        Log.i("facematch", "InitEngine: " + nRet);
        if (nRet < 0) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            if (nRet == -1) {
                builder1.setMessage("No Key Found");
            } else if (nRet == -2) {
                builder1.setMessage("Invalid Key");
            } else if (nRet == -3) {
                builder1.setMessage("Invalid Platform");
            } else if (nRet == -4) {
                builder1.setMessage("Invalid License");
            }

            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    @Override
    public void onInitEngine(int ret) {
    }

    @Override
    public void onLeftDetect(FaceDetectionResult faceResult) {
        if (faceResult != null) {
            image1.setImage(BitmapHelper.createFromARGB(faceResult.getNewImg(), faceResult.getNewWidth(), faceResult.getNewHeight()));
            image1.setFaceDetectionResult(faceResult);
            inputFeature = faceResult.getFeature().clone();
            Bitmap nBmp = image2.getImage();
            if (nBmp != null) {
                int w = nBmp.getWidth();
                int h = nBmp.getHeight();
                int s = (w * 32 + 31) / 32 * 4;
                ByteBuffer buff = ByteBuffer.allocate(s * h);
                nBmp.copyPixelsToBuffer(buff);
                if (image1.getFaceDetectionResult() != null) {
                    FaceLockHelper.DetectRightFace(buff.array(), w, h, image1.getFaceDetectionResult().getFeature());
                } else {
                    FaceLockHelper.DetectRightFace(buff.array(), w, h, null);
                }
            }
        } else {
            image1.setFaceDetectionResult(null);
            inputFeature = null;
            Bitmap nBmp = image2.getImage();
            if (nBmp != null) {
                int w = nBmp.getWidth();
                int h = nBmp.getHeight();
                int s = (w * 32 + 31) / 32 * 4;
                ByteBuffer buff = ByteBuffer.allocate(s * h);
                nBmp.copyPixelsToBuffer(buff);
                if (image1.getFaceDetectionResult() != null) {
                    FaceLockHelper.DetectRightFace(buff.array(), w, h, image1.getFaceDetectionResult().getFeature());
                } else {
                    FaceLockHelper.DetectRightFace(buff.array(), w, h, null);
                }
            }
        }
        calcMatch();
    }

    @Override
    public void onRightDetect(FaceDetectionResult faceResult) {
        if (faceResult != null) {
            image2.setImage(BitmapHelper.createFromARGB(faceResult.getNewImg(), faceResult.getNewWidth(), faceResult.getNewHeight()));
            image2.setFaceDetectionResult(faceResult);
            matchFeature = faceResult.getFeature().clone();
        } else {
            image2.setFaceDetectionResult(null);
            matchFeature = null;
        }
        calcMatch();
    }

    @Override
    public void onExtractInit(int ret) {
    }

    public void calcMatch() {
        if (image1.getFaceDetectionResult() == null || image2.getFaceDetectionResult() == null) {
            txtScore.setText("Match Score : 0 %");
        } else {
            float score = FaceLockHelper.Similarity(inputFeature, matchFeature, matchFeature.length);
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(1);
            String ss = nf.format(score * 100);
            txtScore.setText("Match Score : " + ss + " %");

            if (!isEmailSent && face1 != null && face2 != null) {
                isEmailSent = true;
                sendResultToServer(AppGeneral.SCAN_RESULT.ACCURA_FM);
            }
        }
    }

    public void writeFileToPrivateStorage(int fromFile, String toFile) {

        InputStream is = getApplicationContext().getResources().openRawResource(fromFile);
        int bytes_read;
        byte[] buffer = new byte[4096];
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(toFile, Context.MODE_PRIVATE);

            while ((bytes_read = is.read(buffer)) != -1)
                fos.write(buffer, 0, bytes_read); // write

            fos.close();
            is.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendResultToServer(String subject) {

        subject = subject + " " + txtScore.getText().toString() + "";
        String body = txtScore.getText().toString();
        String liveness = "False";
        String facematch = "True";
        String type = "";

        SendResultToServer.getInstance().send(this,
                face1,
                face2,
                null,
                subject,
                body,
                type,
                liveness,
                facematch);
    }
}
