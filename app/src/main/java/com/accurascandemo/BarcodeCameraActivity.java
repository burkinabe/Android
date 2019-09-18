package com.accurascandemo;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.accurascandemo.Adapter.BarCodeTypeListAdapter;
import com.accurascandemo.R;
import com.accurascandemo.model.BarcodeData;
import com.accurascandemo.model.BarcodeTypeSelection;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.accurascandemo.util.Utils.PERMISSION_CAMERA;

/*
 * This class used to scan USA Driving License and different type of barcodes
 *  Result will be display in popup.
 *
 * */

public class BarcodeCameraActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    TextView infotext, tv, tvSave; //infotext textview required for post work (background task)
    //    Button close, copy;
    Dialog dialog;
    boolean isflashOn = false, isOpenResult;
    Camera camera; //for torch light
    Dialog types_dialog;
    TextView selectType;
    ImageView flashbtn;
    List<BarcodeTypeSelection> CODE_NAMES = new ArrayList<>();
    BarCodeTypeListAdapter adapter;
    int mposition = 0;
    int card_type;
    LayoutInflater controlInflater = null;
    DisplayMetrics displayMetrics;
    private BarcodeData barcodeData;

    public static Camera getCamera(@NonNull CameraSource cameraSource) {
        Field[] declaredFields = CameraSource.class.getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        return camera;
                    }

                    return null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                break;
            }
        }

        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_camera);

        //get data from intent
        getDataFromIntent(getIntent());

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        initUI();
        setTypes_dialog();
        addScannedDataItem();
        //Checking for camera permission
        checkCameraPermission();

        initializeCamera(card_type == 4 ? 2 : 0); // initialize the camera
    }

    private void addScannedDataItem() {
        CODE_NAMES.add(new BarcodeTypeSelection("ALL_FORMATS", true, Barcode.ALL_FORMATS));
        CODE_NAMES.add(new BarcodeTypeSelection("QR CODE", false, Barcode.QR_CODE));
        CODE_NAMES.add(new BarcodeTypeSelection("PDF417", false, Barcode.PDF417));
        CODE_NAMES.add(new BarcodeTypeSelection("DATA_MATRIX", false, Barcode.DATA_MATRIX));
        CODE_NAMES.add(new BarcodeTypeSelection("CODABAR", false, Barcode.CODABAR));
        CODE_NAMES.add(new BarcodeTypeSelection("CODE_39", false, Barcode.CODE_39));
        CODE_NAMES.add(new BarcodeTypeSelection("CODE_93", false, Barcode.CODE_93));
        CODE_NAMES.add(new BarcodeTypeSelection("CODE_128", false, Barcode.CODE_128));
        CODE_NAMES.add(new BarcodeTypeSelection("AZTEC", false, Barcode.AZTEC));
        CODE_NAMES.add(new BarcodeTypeSelection("CALENDAR_EVENT", false, Barcode.CALENDAR_EVENT));
        CODE_NAMES.add(new BarcodeTypeSelection("EAN_8", false, Barcode.EAN_8));
        CODE_NAMES.add(new BarcodeTypeSelection("EAN_13", false, Barcode.EAN_13));
        CODE_NAMES.add(new BarcodeTypeSelection("EMAIL", false, Barcode.EMAIL));
        CODE_NAMES.add(new BarcodeTypeSelection("PHONE", false, Barcode.PHONE));
        CODE_NAMES.add(new BarcodeTypeSelection("CONTACT_INFO", false, Barcode.CONTACT_INFO));
        CODE_NAMES.add(new BarcodeTypeSelection("GEO", false, Barcode.GEO));
        CODE_NAMES.add(new BarcodeTypeSelection("SMS", false, Barcode.SMS));
        CODE_NAMES.add(new BarcodeTypeSelection("DRIVER_LICENSE", false, Barcode.DRIVER_LICENSE));
        CODE_NAMES.add(new BarcodeTypeSelection("URL", false, Barcode.URL));
        CODE_NAMES.add(new BarcodeTypeSelection("ISBN", false, Barcode.ISBN));
        CODE_NAMES.add(new BarcodeTypeSelection("WIFI", false, Barcode.WIFI));
        CODE_NAMES.add(new BarcodeTypeSelection("TEXT", false, Barcode.TEXT));
        CODE_NAMES.add(new BarcodeTypeSelection("UPC_A", false, Barcode.UPC_A));
        CODE_NAMES.add(new BarcodeTypeSelection("UPC_E", false, Barcode.UPC_E));
        CODE_NAMES.add(new BarcodeTypeSelection("ITF", false, Barcode.ITF));
        CODE_NAMES.add(new BarcodeTypeSelection("PRODUCT", false, Barcode.PRODUCT));
    }

    public void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        }
    }

    public void getDataFromIntent(Intent intent) {
        card_type = intent.getIntExtra("card_type", 0);
    }

    private void initUI() {
        // initialize the UI
        surfaceView = findViewById(R.id.surfaceV);
        infotext = findViewById(R.id.infotv);
        //For Overlay buttons
        controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.overlay_btns, null);
        ViewGroup.LayoutParams layoutParamsControl
                = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addContentView(viewControl, layoutParamsControl);

        selectType = findViewById(R.id.select_type);
        flashbtn = findViewById(R.id.torch_btn);

        if (card_type == 4) {
            selectType.setVisibility(View.GONE);
        } else if (card_type == 5) {
            selectType.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        selectType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                types_dialog.show();
            }
        });

        flashbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasFlash()) {
                    if (!isflashOn) {
                        flashbtn.setBackgroundResource(R.drawable.flash);
                        isflashOn = true;
                        //turn flash on
                        start_Flash();
                    } else {
                        flashbtn.setBackgroundResource(R.drawable.flash_off);
                        isflashOn = false;
                        //turn flash off
                        stopFlash();
                    }
                } else {
                    Toast.makeText(BarcodeCameraActivity.this, "Flash Not Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();

    }

    private void start_scan() {
        if (ActivityCompat.checkSelfPermission(BarcodeCameraActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            if (surfaceView != null) {
                cameraSource.start(surfaceView.getHolder());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    start_scan();
                } else {
                    checkCameraPermission();
                }
                break;
        }
    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private void start_Flash() {

        camera = null;
        camera = getCamera(cameraSource);
        Camera.Parameters p;
        if (camera != null) {
            p = camera.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(p);
        }
    }

    private void stopFlash() {
        camera = null;
        camera = getCamera(cameraSource);
        Camera.Parameters p;
        if (camera != null) {
            p = camera.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
        }
    }

    private void initializeCamera(int format) {

        barcodeDetector = new BarcodeDetector.Builder(BarcodeCameraActivity.this)
                .setBarcodeFormats(CODE_NAMES.get(format).formatsType).build();


        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(displayMetrics.heightPixels, displayMetrics.widthPixels)
                .build();

        if (surfaceView != null) {
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    Log.w("surfaceCallback", "surfaceCreated");
                    if (ActivityCompat.checkSelfPermission(BarcodeCameraActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(holder);
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });
        }


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                /*called when detect data*/

                final SparseArray<Barcode> qrcode = detections.getDetectedItems();

                if (qrcode.size() != 0) {

                    infotext.post(new Runnable() {
                        @Override
                        public void run() {
                            cameraSource.stop();
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            if (vibrator != null) {
                                vibrator.vibrate(100); //vibration can be reduced or increased
                            }
                            String output = qrcode.valueAt(0).rawValue;
                            if (!extractScanResult(output)) {
                                //Display the normal result of barcode
                                setResultDialog(output);
                            } else {
                                /* When DL with PDF416 */
                                openBarcodeResult();
                            }
                            Log.w("Barcode", qrcode.toString());
                        }
                    });
                }
            }
        });
    }

    private void playBeepSound() {
        final MediaPlayer mediaPlayer = MediaPlayer.create(BarcodeCameraActivity.this, R.raw.beep);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer1) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        });
    }

    private void setTypes_dialog() {
        types_dialog = new Dialog(BarcodeCameraActivity.this);
        types_dialog.setContentView(R.layout.types_dialog);
        types_dialog.setCanceledOnTouchOutside(false);
        types_dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    types_dialog.cancel();

                }
                return true;
            }
        });

        ListView listView = types_dialog.findViewById(R.id.typelv);

        adapter = new BarCodeTypeListAdapter(BarcodeCameraActivity.this, CODE_NAMES);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                for (int i = 0; i < CODE_NAMES.size(); i++) {
                    CODE_NAMES.get(i).isSelected = i == position;
                }

                adapter.notifyDataSetChanged();

                mposition = position;
                cameraSource.stop();
                initializeCamera(position);
                start_scan();

                if (isflashOn) {
                    start_Flash();
                }

                types_dialog.cancel();
            }

        });

    }

    private void setResultDialog(String output) {
        if (dialog == null) {
            playBeepSound();
            dialog = new Dialog(BarcodeCameraActivity.this);
            dialog.setContentView(R.layout.m_dialog);
            dialog.setCancelable(true);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog1) {
                    dialog = null;
                    start_scan();
                    if (isflashOn) {
                        start_Flash();
                    }
                }
            });
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog1) {
                    dialog = null;
                }
            });

            tv = dialog.findViewById(R.id.result_tv);
            tvSave = dialog.findViewById(R.id.tvSave);
            tv.setText(output);
            tvSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    start_scan();
                    if (isflashOn) {
                        start_Flash();
                    }
                }
            });
            if (!isFinishing()) {
                dialog.show();
            }
        }
    }

    private void openBarcodeResult() {
        if (!isOpenResult) {
            playBeepSound();
            isOpenResult = true;
            BarcodeResultActivity.startActivity(this, barcodeData);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            isOpenResult = false;
        }
    }

    /*Method to to use extract scan result and identify that scanned document is DL with PDF417 or not*/
    private boolean extractScanResult(String output) {
        barcodeData = new BarcodeData();
        String Customer_Family_Name = "DCS", Customer_Given_Name = "DCT", Name_Suffix = "DCU",
                Street_Address_1 = "DAG", City = "DAI", Jurisdction_Code = "DAJ", Postal_Code = "DAK",
                Customer_Id_Number = "DAQ", Expiration_Date = "DBA", Sex = "DBC",
                Customer_First_Name = "DAC", Customer_Middle_Name = "DAD", Street_Address_2 = "DAH",
                Street_Address_1_optional = "DAL", Street_Address_2_optional = "DAM", Date_Of_Birth = "DBB",
                NameSuff = "DAE", NamePref = "DAF", LicenseClassification = "DAR", LicenseRestriction = "DAS",
                LicenseEndorsement = "DAT", IssueDate = "DBD", OrganDonor = "DBH", HeightFT = "DAU",
                FullName = "DAA", HeightCM = "DAV", WeightLBS = "DAW", WeightKG = "DAX",
                EyeColor = "DAY", HairColor = "DAZ", IssueTime = "DBE", NumberDuplicate = "DBF",
                UniqueCustomerId = "DBJ", SocialSecurityNo = "DBM", Under18 = "DDH", Under19 = "DDI",
                Under21 = "DDJ", PermitClassification = "PAA", VeteranIndicator = "DDL",
                PermitIssue = "PAD", PermitExpire = "PAB", PermitRestriction = "PAE", PermitEndorsement = "PAF",
                CourtRestriction = "ZVA", InventoryControlNo = "DCK", RaceEthnicity = "DCL", StandardVehicleClass = "DCM", DocumentDiscriminator = "DCF",
                Customer_Last_Name = "DAB", ResidenceCity = "DAN", ResidenceJurisdictionCode = "DAO", ResidencePostalCode = "DAP", MedicalIndicatorCodes = "DBG",
                NonResidentIndicator = "DBI", SocialSecurityNumber = "DBK", DateOfBirth = "DBL", FullName1 = "DBN", LastName = "DBO", FirstName = "DBP", MiddelName = "DBQ",
                Suffix = "DBR", Prefix = "DBS", VirginiaSpecificClass = "DCA", VirginiaSpecificRestrictions = "DCB", VirginiaSpecificEndorsements = "DCD", PhysicalDescriptionWeight = "DCE", CountryTerritoryOfIssuance = "DCG",
                FederalCommercialVehicleCodes = "DCH", PlaceOfBirth = "DCI", AuditInformation = "DCJ", StandardEndorsementCode = "DCN", StandardRestrictionCode = "DCO", JuriSpeciVehiClassiDescri = "DCP",
                JurisdictionSpecific = "DCQ", JuriSpeciRestriCodeDescri = "DCR", ComplianceType = "DDA", CardRevisionDate = "DDB", HazMatEndorsementExpiryDate = "DDC", LimitedDurationDocumentIndicator = "DDD",
                FamilyNameTruncation = "DDE", FirstNamesTruncation = "DDF", MiddleNamesTruncation = "DDG", OrganDonorIndicator = "DDK", PermitIdentifier = "PAC";

        HashMap<String, String> dataHashMap = new HashMap<>();
        ArrayList<String> allElements = new ArrayList<>();

        allElements.add(PermitIdentifier);
        allElements.add(OrganDonorIndicator);
        allElements.add(MiddleNamesTruncation);
        allElements.add(FirstNamesTruncation);
        allElements.add(FamilyNameTruncation);
        allElements.add(LimitedDurationDocumentIndicator);
        allElements.add(HazMatEndorsementExpiryDate);
        allElements.add(CardRevisionDate);
        allElements.add(ComplianceType);
        allElements.add(JuriSpeciRestriCodeDescri);
        allElements.add(JurisdictionSpecific);
        allElements.add(JuriSpeciVehiClassiDescri);
        allElements.add(StandardRestrictionCode);
        allElements.add(StandardEndorsementCode);
        allElements.add(AuditInformation);
        allElements.add(PlaceOfBirth);
        allElements.add(FederalCommercialVehicleCodes);
        allElements.add(CountryTerritoryOfIssuance);
        allElements.add(PhysicalDescriptionWeight);
        allElements.add(VirginiaSpecificRestrictions);
        allElements.add(VirginiaSpecificClass);
        allElements.add(Prefix);
        allElements.add(Suffix);
        allElements.add(MiddelName);
        allElements.add(FirstName);
        allElements.add(LastName);
        allElements.add(FullName1);
        allElements.add(DateOfBirth);
        allElements.add(SocialSecurityNumber);
        allElements.add(NonResidentIndicator);
        allElements.add(MedicalIndicatorCodes);
        allElements.add(ResidencePostalCode);
        allElements.add(ResidenceJurisdictionCode);
        allElements.add(ResidenceCity);
        allElements.add(Customer_Last_Name);
        allElements.add(Street_Address_1);
        allElements.add(City);
        allElements.add(Jurisdction_Code);
        allElements.add(Postal_Code);
        allElements.add(Expiration_Date);
        allElements.add(Sex);
        allElements.add(Street_Address_2);
        allElements.add(Street_Address_1_optional);
        allElements.add(Street_Address_2_optional);
        allElements.add(Date_Of_Birth);
        allElements.add(Customer_Family_Name);
        allElements.add(Customer_First_Name);
        allElements.add(Customer_Given_Name);
        allElements.add(Customer_Id_Number);
        allElements.add(Customer_Middle_Name);
        allElements.add(Name_Suffix);
        allElements.add(NameSuff);
        allElements.add(NamePref);
        allElements.add(LicenseClassification);
        allElements.add(LicenseRestriction);
        allElements.add(LicenseEndorsement);
        allElements.add(IssueDate);
        allElements.add(OrganDonor);
        allElements.add(HeightFT);
        allElements.add(FullName);
        allElements.add(HeightCM);
        allElements.add(WeightLBS);
        allElements.add(WeightKG);
        allElements.add(EyeColor);
        allElements.add(HairColor);
        allElements.add(IssueTime);
        allElements.add(NumberDuplicate);
        allElements.add(UniqueCustomerId);
        allElements.add(SocialSecurityNo);
        allElements.add(Under18);
        allElements.add(Under19);
        allElements.add(Under21);
        allElements.add(PermitClassification);
        allElements.add(VeteranIndicator);
        allElements.add(PermitIssue);
        allElements.add(PermitExpire);
        allElements.add(PermitRestriction);
        allElements.add(PermitEndorsement);
        allElements.add(CourtRestriction);
        allElements.add(InventoryControlNo);
        allElements.add(RaceEthnicity);
        allElements.add(StandardVehicleClass);
        allElements.add(DocumentDiscriminator);

        if (!TextUtils.isEmpty(output)) {
            String lines[] = output.split("\\r?\\n");
            if (lines.length > 0) {
                for (String line : lines) {
                    String str = line;
                    if (str.contains("ANSI") && str.contains("DL")) {
                        str = str.substring(str.indexOf("DL"));
                        String str1[] = str.split("DL");
                        if (str1.length > 1) {
                            str = str1[str1.length - 1];
                        }
                    }
                    if (str.length() > 3) {
                        String key = str.substring(0, 3);
                        String value = str.substring(3);
                        if (allElements.contains(key)) {
                            if (!value.equalsIgnoreCase("None")) {
                                /*Add key value in hashmap*/
                                dataHashMap.put(allElements.get(allElements.indexOf(key)), value);
                            }
                        }
                    }
                    Log.e("RESULT ", "line -->" + line);
                }
            }
        }

        barcodeData.wholeDataString = output;

        /*check keys and value and set data in barcodeData model class*/
        if (dataHashMap.containsKey(Customer_Family_Name) && !TextUtils.isEmpty(Customer_Family_Name)) {
            Log.v("TAG", "users family name:" + dataHashMap.get(Customer_Family_Name));
            barcodeData.lastName1 = dataHashMap.get(Customer_Family_Name).trim();
        }
        if (dataHashMap.containsKey(Customer_Last_Name) && !TextUtils.isEmpty(Customer_Last_Name)) {
            barcodeData.lname = dataHashMap.get(Customer_Last_Name).trim();
        }

        if (dataHashMap.containsKey(LastName)) {
            barcodeData.lastName = dataHashMap.get(LastName).trim();
        }
        if (dataHashMap.containsKey(ResidenceCity)) {
            barcodeData.ResidenceCity = dataHashMap.get(ResidenceCity).trim();
        }
        if (dataHashMap.containsKey(ResidenceJurisdictionCode)) {
            barcodeData.ResidenceJurisdictionCode = dataHashMap.get(ResidenceJurisdictionCode).trim();
        }
        if (dataHashMap.containsKey(ResidencePostalCode)) {
            barcodeData.ResidencePostalCode = dataHashMap.get(ResidencePostalCode).trim();
        }
        if (dataHashMap.containsKey(MedicalIndicatorCodes)) {
            barcodeData.MedicalIndicatorCodes = dataHashMap.get(MedicalIndicatorCodes).trim();
        }
        if (dataHashMap.containsKey(NonResidentIndicator)) {
            barcodeData.NonResidentIndicator = dataHashMap.get(NonResidentIndicator).trim();
        }
        if (dataHashMap.containsKey(VirginiaSpecificClass)) {
            barcodeData.VirginiaSpecificClass = dataHashMap.get(VirginiaSpecificClass).trim();
        }
        if (dataHashMap.containsKey(VirginiaSpecificRestrictions)) {
            barcodeData.VirginiaSpecificRestrictions = dataHashMap.get(VirginiaSpecificRestrictions).trim();
        }
        if (dataHashMap.containsKey(PhysicalDescriptionWeight)) {
            barcodeData.PhysicalDescriptionWeight = dataHashMap.get(PhysicalDescriptionWeight).trim();
        }
        if (dataHashMap.containsKey(CountryTerritoryOfIssuance)) {
            barcodeData.CountryTerritoryOfIssuance = dataHashMap.get(CountryTerritoryOfIssuance).trim();
        }
        if (dataHashMap.containsKey(FederalCommercialVehicleCodes)) {
            barcodeData.FederalCommercialVehicleCodes = dataHashMap.get(FederalCommercialVehicleCodes).trim();
        }
        if (dataHashMap.containsKey(PlaceOfBirth)) {
            barcodeData.PlaceOfBirth = dataHashMap.get(PlaceOfBirth).trim();
        }
        if (dataHashMap.containsKey(StandardEndorsementCode)) {
            barcodeData.StandardEndorsementCode = dataHashMap.get(StandardEndorsementCode).trim();
        }
        if (dataHashMap.containsKey(StandardRestrictionCode)) {
            barcodeData.StandardRestrictionCode = dataHashMap.get(StandardRestrictionCode).trim();
        }
        if (dataHashMap.containsKey(JuriSpeciVehiClassiDescri)) {
            barcodeData.JuriSpeciVehiClassiDescri = dataHashMap.get(JuriSpeciVehiClassiDescri).trim();
        }
        if (dataHashMap.containsKey(JurisdictionSpecific)) {
            barcodeData.JurisdictionSpecific = dataHashMap.get(JurisdictionSpecific).trim();
        }
        if (dataHashMap.containsKey(JuriSpeciRestriCodeDescri)) {
            barcodeData.JuriSpeciRestriCodeDescri = dataHashMap.get(JuriSpeciRestriCodeDescri).trim();
        }
        if (dataHashMap.containsKey(ComplianceType)) {
            barcodeData.ComplianceType = dataHashMap.get(ComplianceType).trim();
        }
        if (dataHashMap.containsKey(CardRevisionDate)) {
            barcodeData.CardRevisionDate = dataHashMap.get(CardRevisionDate).trim();
        }
        if (dataHashMap.containsKey(HazMatEndorsementExpiryDate)) {
            barcodeData.HazMatEndorsementExpiryDate = dataHashMap.get(HazMatEndorsementExpiryDate).trim();
        }
        if (dataHashMap.containsKey(LimitedDurationDocumentIndicator)) {
            barcodeData.LimitedDurationDocumentIndicator = dataHashMap.get(LimitedDurationDocumentIndicator).trim();
        }
        if (dataHashMap.containsKey(FamilyNameTruncation)) {
            barcodeData.FamilyNameTruncation = dataHashMap.get(FamilyNameTruncation).trim();
        }
        if (dataHashMap.containsKey(FirstNamesTruncation)) {
            barcodeData.FirstNamesTruncation = dataHashMap.get(FirstNamesTruncation).trim();
        }
        if (dataHashMap.containsKey(MiddleNamesTruncation)) {
            barcodeData.MiddleNamesTruncation = dataHashMap.get(MiddleNamesTruncation).trim();
        }
        if (dataHashMap.containsKey(OrganDonorIndicator)) {
            barcodeData.OrganDonorIndicator = dataHashMap.get(OrganDonorIndicator).trim();
        }
        if (dataHashMap.containsKey(PermitIdentifier)) {
            barcodeData.PermitIdentifier = dataHashMap.get(PermitIdentifier).trim();
        }
        if (dataHashMap.containsKey(AuditInformation)) {
            barcodeData.AuditInformation = dataHashMap.get(AuditInformation).trim();
        }

        if (dataHashMap.containsKey(Customer_Given_Name)) {
            Log.v("TAG", "users Given name:" + dataHashMap.get(Customer_Given_Name));
            try {
                String CustomerName[] = dataHashMap.get(Customer_Given_Name).split(" ");
                if(CustomerName.length >= 1)
                barcodeData.firstName1 = CustomerName[0].trim();
//                barcodeData.mname = CustomerName[1].substring(0, 1).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(FirstName)) {
            barcodeData.firstName = dataHashMap.get(FirstName);
        }
        if (dataHashMap.containsKey(Name_Suffix)) {
            Log.v("TAG", "Surname name:" + dataHashMap.get(Name_Suffix));
            barcodeData.nameSuffix = dataHashMap.get(Name_Suffix);
        }
        if (dataHashMap.containsKey(Suffix)) {
            barcodeData.Suffix = dataHashMap.get(Suffix);
        }
        if (dataHashMap.containsKey(Street_Address_1)) {
            Log.v("TAG", "Address line 1 :" + dataHashMap.get(Street_Address_1));
            try {
                barcodeData.address1 = dataHashMap.get(Street_Address_1).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (dataHashMap.containsKey(Street_Address_2)) {
            Log.v("TAG", "Address line 2 :" + dataHashMap.get(Street_Address_2));
            try {
                barcodeData.address2 = dataHashMap.get(Street_Address_2).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(Street_Address_1_optional)) {
            barcodeData.ResidenceAddress1 = dataHashMap.get(Street_Address_1_optional).trim();
        }

        if (dataHashMap.containsKey(Street_Address_2_optional)) {
            barcodeData.ResidenceAddress2 = dataHashMap.get(Street_Address_2_optional).trim();
        }


        if (dataHashMap.containsKey(City)) {
            Log.v("TAG", "City:" + dataHashMap.get(City));
            barcodeData.city = dataHashMap.get(City).trim();
        }
        if (dataHashMap.containsKey(Postal_Code)) {
            Log.v("TAG", "Pin Code:" + dataHashMap.get(Postal_Code));
            barcodeData.zipcode = dataHashMap.get(Postal_Code);
        }
        if (dataHashMap.containsKey(Date_Of_Birth)) {
            Log.v("TAG", "Birth Date    :" + dataHashMap.get(Date_Of_Birth));
            if (dataHashMap.get(Date_Of_Birth).length() > 4) {
//                barcodeData.birthday = dataHashMap.get(Date_Of_Birth).substring(0, 2) + "/"
//                        + dataHashMap.get(Date_Of_Birth).substring(2, 4) + "/" + dataHashMap.get(Date_Of_Birth).substring(4);

                barcodeData.birthday = dataHashMap.get(Date_Of_Birth);
            }
        }
        if (dataHashMap.containsKey(DateOfBirth)) {
            Log.v("TAG", "Birth Date    :" + dataHashMap.get(DateOfBirth));
            if (dataHashMap.get(DateOfBirth).length() > 4) {
//                barcodeData.birthday1 = dataHashMap.get(DateOfBirth).substring(0, 2) + "/"
//                        + dataHashMap.get(DateOfBirth).substring(2, 4) + "/" + dataHashMap.get(DateOfBirth).substring(4);

                barcodeData.birthday = dataHashMap.get(DateOfBirth);
            }
        }
        if (dataHashMap.containsKey(Sex)) {
            Log.v("TAG", "Sex:" + (dataHashMap.get(Sex).trim().equals("1") ? "Male" : "Female"));
//            barcodeData.sex = dataHashMap.get(Sex).trim().equals("1") ? "Male" : "Female";
            barcodeData.sex = dataHashMap.get(Sex).trim();
        }
        if (dataHashMap.containsKey(FullName)) {
            String cName = dataHashMap.get(FullName);
            int startIndexOfComma;
            int endIndexOfComma;
            startIndexOfComma = cName.indexOf(",");
            endIndexOfComma = cName.lastIndexOf(",");
            if (startIndexOfComma != endIndexOfComma) {
                String CustomerName[] = dataHashMap.get(FullName).split(",");
                if(CustomerName.length >= 1)
                barcodeData.lname = CustomerName[0].replace(",", "").trim();
                if(CustomerName.length >= 2)
                barcodeData.fname = CustomerName[1].trim();
//                barcodeData.mname = CustomerName[2].substring(0, 1).trim();
            } else {
                String CustomerName[] = dataHashMap.get(FullName).split(" ");
                if(CustomerName.length >= 1)
                barcodeData.lname = CustomerName[0].replace(",", "").trim();
                if(CustomerName.length >= 2)
                barcodeData.fname = CustomerName[1].trim();
//                barcodeData.mname = CustomerName[2].substring(0, 1).trim();
            }
        }
        if (dataHashMap.containsKey(Customer_First_Name)) {
            barcodeData.fname = dataHashMap.get(Customer_First_Name).trim();
        }
        if (dataHashMap.containsKey(Customer_Middle_Name)) {
            barcodeData.mname = dataHashMap.get(Customer_Middle_Name).trim();
        }
        if (dataHashMap.containsKey(MiddelName)) {
            barcodeData.middleName = dataHashMap.get(MiddelName);
        }

        if (dataHashMap.containsKey(Customer_Id_Number)) {
            barcodeData.licence_number = dataHashMap.get(Customer_Id_Number).trim();
            Log.e("TAG", "Licence Number is :" + barcodeData.licence_number);
        }
        if (dataHashMap.containsKey(Expiration_Date) && dataHashMap.get(Expiration_Date).length() > 4) {
//            barcodeData.licence_expire_date = dataHashMap.get(Expiration_Date).trim();
//            barcodeData.licence_expire_date = dataHashMap.get(Expiration_Date).substring(0, 2) + "/"
//                    + dataHashMap.get(Expiration_Date).substring(2, 4) + "/" + dataHashMap.get(Expiration_Date).substring(4);
            barcodeData.licence_expire_date = dataHashMap.get(Expiration_Date);
        }

        if (dataHashMap.containsKey(Jurisdction_Code)) {
            try {
                barcodeData.jurisdiction = dataHashMap.get(Jurisdction_Code).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(NameSuff)) {
            try {
                barcodeData.nameSuffix = dataHashMap.get(NameSuff).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(NamePref)) {
            try {
                barcodeData.namePrefix = dataHashMap.get(NamePref).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(barcodeData.namePrefix)) {
            barcodeData.namePrefix = dataHashMap.get(Prefix);
        }

        if (dataHashMap.containsKey(LicenseClassification)) {
            try {
                barcodeData.licenseClassification = dataHashMap.get(LicenseClassification).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(LicenseRestriction)) {
            try {
                barcodeData.licenseRestriction = dataHashMap.get(LicenseRestriction).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(LicenseEndorsement)) {
            try {
                barcodeData.licenseEndorsement = dataHashMap.get(LicenseEndorsement).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(IssueDate) && dataHashMap.get(IssueDate).length() > 4) {
//            try {
//                barcodeData.issueDate = dataHashMap.get(IssueDate).substring(0, 2) + "/"
//                        + dataHashMap.get(IssueDate).substring(2, 4) + "/" + dataHashMap.get(IssueDate).substring(4);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            barcodeData.issueDate = dataHashMap.get(IssueDate);
        }

        if (dataHashMap.containsKey(OrganDonor)) {
            try {
                barcodeData.organDonor = dataHashMap.get(OrganDonor).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(HeightFT)) {
            try {
                barcodeData.heightinFT = dataHashMap.get(HeightFT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(WeightLBS)) {
            try {
                barcodeData.weightLBS = dataHashMap.get(WeightLBS).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(WeightKG)) {
            try {
                barcodeData.weightKG = dataHashMap.get(WeightKG).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(EyeColor)) {
            try {
                barcodeData.eyeColor = dataHashMap.get(EyeColor).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(HairColor)) {
            try {
                barcodeData.hairColor = dataHashMap.get(HairColor).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(IssueTime)) {
            try {
                barcodeData.issueTime = dataHashMap.get(IssueTime).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(PermitIssue)) {
            try {
                barcodeData.permitIssue = dataHashMap.get(PermitIssue).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(PermitExpire)) {
            try {
                barcodeData.permitExpire = dataHashMap.get(PermitExpire).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(PermitRestriction)) {
            try {
                barcodeData.permitRestriction = dataHashMap.get(PermitRestriction).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(PermitEndorsement)) {
            try {
                barcodeData.permitEndorsement = dataHashMap.get(PermitEndorsement).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(FullName) && !TextUtils.isEmpty(FullName)) {
            try {
                barcodeData.fullName = dataHashMap.get(FullName).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (dataHashMap.containsKey(FullName1)) {
            try {
                barcodeData.fullName1 = dataHashMap.get(FullName1).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(HeightCM)) {
            try {
                barcodeData.heightCM = dataHashMap.get(HeightCM).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(NumberDuplicate)) {
            try {
                barcodeData.numberDuplicate = dataHashMap.get(NumberDuplicate).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(UniqueCustomerId)) {
            try {
                barcodeData.uniqueCustomerId = dataHashMap.get(UniqueCustomerId).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(SocialSecurityNo)) {
            try {
                barcodeData.socialSecurityNo = dataHashMap.get(SocialSecurityNo).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (dataHashMap.containsKey(SocialSecurityNumber)) {
            try {
                barcodeData.socialSecurityNo = dataHashMap.get(SocialSecurityNumber).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(Under18) && dataHashMap.get(Under18).length() > 4) {
            try {
                barcodeData.under18 = dataHashMap.get(Under18).substring(0, 2) + "/"
                        + dataHashMap.get(Under18).substring(2, 4) + "/" + dataHashMap.get(Under18).substring(4);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(Under19) && dataHashMap.get(Under19).length() > 4) {
            try {
                barcodeData.under19 = dataHashMap.get(Under19).substring(0, 2) + "/"
                        + dataHashMap.get(Under19).substring(2, 4) + "/" + dataHashMap.get(Under19).substring(4);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(Under21) && dataHashMap.get(Under21).length() > 4) {
            try {
                barcodeData.under21 = dataHashMap.get(Under21).substring(0, 2) + "/"
                        + dataHashMap.get(Under21).substring(2, 4) + "/" + dataHashMap.get(Under21).substring(4);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(InventoryControlNo)) {
            try {
                barcodeData.inventoryNo = dataHashMap.get(InventoryControlNo).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(RaceEthnicity)) {
            try {
                barcodeData.raceEthnicity = dataHashMap.get(RaceEthnicity).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(StandardVehicleClass)) {
            try {
                barcodeData.standardVehicleClass = dataHashMap.get(StandardVehicleClass).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(DocumentDiscriminator)) {
            try {
                barcodeData.documentDiscriminator = dataHashMap.get(DocumentDiscriminator).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(PermitClassification)) {
            try {
                barcodeData.permitClassification = dataHashMap.get(PermitClassification).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(VeteranIndicator)) {
            try {
                barcodeData.veteranIndicator = dataHashMap.get(VeteranIndicator).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dataHashMap.containsKey(CourtRestriction)) {
            try {
                barcodeData.courtRestriction = dataHashMap.get(CourtRestriction).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return barcodeData.fname != null || barcodeData.mname != null || barcodeData.lname != null || barcodeData.address1 != null || barcodeData.city != null
                || barcodeData.state != null || barcodeData.zipcode != null || barcodeData.licence_expire_date != null;
    }
}
