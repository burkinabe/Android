package com.accurascandemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accurascandemo.api.GenerateRequest;
import com.accurascandemo.api.HandleResponse;
import com.accurascandemo.api.RequestTask;
import com.accurascandemo.model.PanAadharDetail;
import com.accurascandemo.util.AlertDialogAbstract;
import com.accurascandemo.util.ParsedResponse;
import com.accurascandemo.util.Utils;
import com.docrecog.scan.CameraActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by richa on 28/4/17.
 */

public class ScanNowPassportImageActivity extends BaseActivity implements View.OnClickListener {

    private static final int RETURN_RESULT = 1;
    private static final int REQUEST_CAMERA_FOR_RESULT = 100;
    private static final int REQUEST_PAN_AADHAAR_RESULT = 103;
    public boolean mPermissionRationaleDialogShown = false;
    public Uri fileUri;
    private int type = 0;
    private TextView tvTitle, tvLetsGo;
    private ImageView ivScan;
    private File imageFile;
    private int card_type;
    private Dialog dialog;
    private ImageView ivBack;

    public static void copyInputStream(InputStream input, OutputStream output) throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_passport_image);

        initUi(); // initialize the UI
    }

    private void initUi() {

        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);

        tvLetsGo = (TextView) findViewById(R.id.tvLetsGo);
        tvLetsGo.setOnClickListener(this);

        findViewById(R.id.ivBack).setOnClickListener(this);
        findViewById(R.id.tvLetsGo).setOnClickListener(this);
        tvTitle = findViewById(R.id.tvTitle);
        tvLetsGo = findViewById(R.id.tvLetsGo);
        ivScan = findViewById(R.id.ivScan);

        type = getIntent().getIntExtra(TYPE, 0);

        switch (type) {
            case 1:
                tvTitle.setText(getString(R.string.text_place_doc));
                ivScan.setImageResource(R.mipmap.demoimage);
                tvLetsGo.setText(getString(R.string.text_start_scanning));
                break;
            case 2:
                tvTitle.setText(getString(R.string.text_barcode_doc));
                ivScan.setImageResource(R.mipmap.driving_license);
                tvLetsGo.setText(getString(R.string.text_start_scanning));
                break;
            case 3:
                tvTitle.setText(getString(R.string.text_barcode_doc));
                ivScan.setImageResource(R.drawable.bar_code);
                tvLetsGo.setText(getString(R.string.text_start_scanning));
                break;
            case 4:
                tvTitle.setText(getString(R.string.text_pan_doc));
                ivScan.setImageResource(R.mipmap.ic_pancard);
                tvLetsGo.setText(getString(R.string.text_take_pic));
                break;
            case 5:
                tvTitle.setText(getString(R.string.text_aadhaar_doc));
                ivScan.setImageResource(R.mipmap.ic_aadharcard);
                tvLetsGo.setText(getString(R.string.text_take_pic));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AccuraDemoApplication.getInstance().reportToGoogleAnalytics(getString(R.string.launch_screen));
    }

    //handle click of particular view
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ivBack:
                ScanNowPassportImageActivity.this.finish();
                break;

            case R.id.tvLetsGo:
                //Request Permission:
                openSpecificActivity();
                break;

            case R.id.tvExcel:
                dialog.dismiss();
                if (dbHelper.getScanData().size() > 0) {
                    Utils.exportDataToExcel(dbHelper, this);
                } else {
                    new AlertDialogAbstract(this, getString(R.string.text_no_data_export), getString(R.string.ok), "") {

                        @Override
                        public void positive_negativeButtonClick(int pos_neg_id) {
                        }
                    };
                }
                break;

            case R.id.tvPDF:
                dialog.dismiss();
                if (dbHelper.getScanData().size() > 0) {
                    Utils.exportDataToPDF(dbHelper, this);
                } else {
                    new AlertDialogAbstract(this, getString(R.string.text_no_data_export), getString(R.string.ok), "") {

                        @Override
                        public void positive_negativeButtonClick(int pos_neg_id) {
                        }
                    };
                }
                break;
        }
    }

    private void showChooseOptionDialog() {
        if (dialog == null) {
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_export_chooser);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = metrics.widthPixels - 50;
            dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.findViewById(R.id.tvExcel).setOnClickListener(this);
            dialog.findViewById(R.id.tvPDF).setOnClickListener(this);

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog1) {
                    dialog = null;
                }
            });
            dialog.show();
        }
    }

    private void openSpecificActivity() {
        switch (type) {
            case 1: //passport
                card_type = 1;
                requestPermission(1);
                break;
            case 2: // driving license
                card_type = 4;
                requestPermission(4);
                break;
            case 3: // barcode
                card_type = 5;
                requestPermission(5);
                break;
            case 4: //pan card
                card_type = 2;
                requestPermission(2);
                break;
            case 5: //aadhaarcard
                card_type = 3;
                requestPermission(3);
                break;
        }
    }

    private void requestPermission(int type) {

        if (Build.VERSION.SDK_INT >= 23) {
            if (type == 4 || type == 5) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    card_type = type;
                    requestBarcode();
                } else {
                    ActivityCompat.requestPermissions(ScanNowPassportImageActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_FOR_RESULT);
                }
            } else {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if (type == 1) {
                        requestCameraActivity();
                    } else {
                        requestCameraForPanAddhaar();
                    }
                } else {
                    ActivityCompat.requestPermissions(ScanNowPassportImageActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_FOR_RESULT);
                }
            }
        } else {
            if (type == 4 || type == 5) {
                card_type = type;
                requestBarcode();
            } else {
                if (type == 1) {
                    requestCameraActivity();
                } else {
                    requestCameraForPanAddhaar();
                }
            }
        }
    }

    public void showPermissionRequiredDialog() {
        mPermissionRationaleDialogShown = true;
        // Dialog to show why permission is required
        String msg = String.valueOf(Html.fromHtml("If you are not allowed com.com.docrecog.scan.camera.scan.camera permission then your app may not working properly. please allow to this permission </br> <b> Follow this step: Permission -> Enable com.com.docrecog.scan.camera.scan.camera permission </b>"));

        AlertDialog.Builder builder = new AlertDialog.Builder(ScanNowPassportImageActivity.this);
        builder.setTitle(Html.fromHtml("<font color='#6DB0E4'> <b> EBW - Permission Necessary </b> </font>"));
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//              ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.RECEIVE_SMS}, 1);
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 1);
                dialog.dismiss();
            }
        });
        builder.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_FOR_RESULT) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.v("EBW", "Permission: " + permissions[0] + "was " + grantResults[0]);
                //resume tasks needing this permission
                if (card_type == 1) {
                    requestCameraActivity();
                } else {
                    requestCameraForPanAddhaar();
                }
            } else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v("EBW", "Permission: " + permissions[0] + "was " + grantResults[0]);
                //resume tasks needing this permission
                requestPermission(card_type);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestCameraActivity() {
        startActivityForResult(new Intent(ScanNowPassportImageActivity.this, CameraActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION), RETURN_RESULT);
        overridePendingTransition(0, 0);
    }

    private void requestBarcode() {
        Intent intent = new Intent(ScanNowPassportImageActivity.this, BarcodeCameraActivity.class);
        intent.putExtra("card_type", card_type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, RETURN_RESULT);
        overridePendingTransition(0, 0);
    }

    private void requestCameraForPanAddhaar() {
        Intent intent = new Intent(ScanNowPassportImageActivity.this, CameraPanAddhaarActivity.class);
        intent.putExtra("card_type", card_type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, Utils.REQUEST_CAMERA);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RETURN_RESULT) {// && resultCode == RESULT_OK
                startActivity(new Intent(ScanNowPassportImageActivity.this, ScanResultActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                overridePendingTransition(0, 0);
            } else if (requestCode == Utils.REQUEST_CAMERA) {
                requestPanAadhar(data.getStringExtra("fileUrl"));
            } else if (requestCode == REQUEST_PAN_AADHAAR_RESULT) {
                requestCameraForPanAddhaar();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void takePicture() {
        String state = Environment.getExternalStorageState();
        imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpeg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    fileUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", imageFile);
                } else {
                    fileUri = Uri.parse(Utils.CONTENT_URI);
                }
            } else {
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    fileUri = Uri.fromFile(imageFile);
                } else {
                    fileUri = Uri.parse(Utils.CONTENT_URI);
                }
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            intent.putExtra("return-data", true);
            intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            startActivityForResult(intent, Utils.REQUEST_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(Utils.FILE_URI, fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
            fileUri = savedInstanceState.getParcelable(Utils.FILE_URI);
    }

    private void setImage() {

        if (imageFile != null) {
            try {
                ExifInterface exif;
                exif = new ExifInterface(imageFile.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int angle = 0;

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        angle = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        angle = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        angle = 270;
                        break;
                }

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
                Matrix mat = new Matrix();
                mat.postRotate(angle);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
                FileOutputStream out = new FileOutputStream(imageFile.getAbsolutePath());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);
                out.close();
                bitmap.recycle();

                requestPanAadhar("");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void requestPanAadhar(final String path) {
        if (path != null) {
            File imageFile = new File(path);
            showProgressDialog();
            new RequestTask(ScanNowPassportImageActivity.this, getString(R.string.api_key),
                    GenerateRequest.requestPanAadharDetail(imageFile, card_type), getString(R.string.api), false) {

                @Override
                protected void onPostExecute(String response) {
                    super.onPostExecute(response);
                    dismissProgressDialog();
                    ParsedResponse p = HandleResponse.responsePanAadharDetail(ScanNowPassportImageActivity.this, response);
                    if (!p.error) {
                        PanAadharDetail panAadharDetail = (PanAadharDetail) p.o;
                        Intent intent = new Intent(ScanNowPassportImageActivity.this, PanAadharResultActivity.class);
                        intent.putExtra("panAadharDetail", panAadharDetail);
                        intent.putExtra("card_type", card_type);
                        intent.putExtra("imageFile", path);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, REQUEST_PAN_AADHAAR_RESULT);
                        overridePendingTransition(0, 0);

                    } else {
                        new AlertDialogAbstract(ScanNowPassportImageActivity.this, (String) p.o, getString(R.string.ok), "") {
                            @Override
                            public void positive_negativeButtonClick(int pos_neg_id) {
//                            finish();
                            }
                        };
                    }
                }
            }.execute();
        }

    }
}
