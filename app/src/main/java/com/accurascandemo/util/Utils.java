package com.accurascandemo.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.accurascandemo.BuildConfig;
import com.accurascandemo.R;
import com.accurascandemo.model.ScanData;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by richa on 27/4/17.
 */

public class Utils {

    public static final String CONTENT_URI = "content://com.accurascan.demoapp";
    public static final int REQUEST_CAMERA = 101;
    public static final int PERMISSION_CAMERA = 102;
    public static final String FILE_URI = "fileUri";

    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void hideKeyboard(Activity context) {
        // Check if no view has focus
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String getReverseCase(Context context) throws UnsupportedEncodingException {
        String string = context.getString(R.string.base);
        return new String(Base64.decode(string, Base64.NO_WRAP), "UTF-8");
    }

    public static void Log_e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.e(tag, msg);
        }
    }

    public static void Log_i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void Log_d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void Log_v(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.v(tag, msg);
        }
    }

    public static void exportDataToExcel(DBHelper dbHelper, Context context) {
        ArrayList<ScanData> scanDataList;
        Uri fileUri;
        if (dbHelper != null) {
            scanDataList = dbHelper.getScanData();
            String excelFile = "scanneddata.xls";
            String state = Environment.getExternalStorageState();
            String dir;
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                dir = Environment.getExternalStorageDirectory() + File.separator + "AccuraScan";
            } else {
                dir = context.getFilesDir() + File.separator + "AccuraScan";
            }
            File mFolder = new File(dir);
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
            File file = new File(dir, excelFile);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                    } else {
                        fileUri = Uri.parse(CONTENT_URI);
                    }
                } else {
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        fileUri = Uri.fromFile(file);
                    } else {
                        fileUri = Uri.parse(CONTENT_URI);
                    }
                }

                HSSFWorkbook wb = new HSSFWorkbook();
                HSSFSheet sheet = wb.createSheet("ScanData");

                Map<String, Object[]> data = new TreeMap<>();
                data.put("1", new Object[]{context.getString(R.string.sr_no), context.getString(R.string.documentType), context.getString(R.string.lastName), context.getString(R.string.firstName), context.getString(R.string.documentNo), context.getString(R.string.country), context.getString(R.string.gender), context.getString(R.string.dateOfBirth), context.getString(R.string.dateOfExpiry), context.getString(R.string.address),
                        context.getString(R.string.glassesDecision), context.getString(R.string.glassesScore), context.getString(R.string.livenessScore), context.getString(R.string.LivenessResult), context.getString(R.string.text_photo)});
                int count = 1, j = 2;
                for (int i = scanDataList.size() - 1; i >= 0; i--) {
                    if (count <= 10) {
                        ScanData scanData = scanDataList.get(i);
                        data.put(j + "", new Object[]{String.valueOf(count), scanData.getDocumentType(), scanData.getLastName(), scanData.getFirstName(), scanData.getPassportNo(), scanData.getCountry(), scanData.getGender(), scanData.getDateOfBirth(), scanData.getDateOfExpiry(), scanData.getAddress(),
                                scanData.getGlassesDecision(), scanData.getGlassesScore(), scanData.getLivenessScore(), scanData.getLivenessResult(), scanData.getUserPicture()});
                        count++;
                        j++;
                    }
                }

                Set<String> keyset = data.keySet();
                int rownum = 0;
                for (String key : keyset) {
                    Row row = sheet.createRow(rownum++);
                    Object[] objArr = data.get(key);
                    int cellnum = 0;
                    for (Object obj : objArr) {
                        if (rownum != 1 && cellnum == 14) {
                            if (obj != null) {
                                int my_picture_id = wb.addPicture((byte[]) obj, Workbook.PICTURE_TYPE_JPEG);
                                HSSFPatriarch drawing = sheet.createDrawingPatriarch();
                                ClientAnchor my_anchor = new HSSFClientAnchor();
                                my_anchor.setCol1(cellnum);
                                my_anchor.setRow1(rownum - 1);
                                my_anchor.setCol2(cellnum + 1);
                                my_anchor.setRow2(rownum + 3);
                                drawing.createPicture(my_anchor, my_picture_id);
                                rownum = rownum + 3;
                            }
                        } else {
                            Cell cell = row.createCell(cellnum++);
                            cell.setCellValue((String) obj);
                        }
                    }
                }

                FileOutputStream fileOut = new FileOutputStream(file);
                wb.write(fileOut);
                fileOut.flush();
                fileOut.close();
                SendData(fileUri, context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void exportDataToPDF(DBHelper dbHelper, Context context) {
        Uri fileUri;
        ArrayList<ScanData> scanDataList;
        if (dbHelper != null) {
            scanDataList = dbHelper.getScanData();
            String state = Environment.getExternalStorageState();
            String dir, pdfFile = "scanneddata.pdf";
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                dir = Environment.getExternalStorageDirectory() + File.separator + "AccuraScan";
            } else {
                dir = context.getFilesDir() + File.separator + "AccuraScan";
            }
            File mFolder = new File(dir);
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
            File file = new File(dir, pdfFile);
            Document document = new Document();  // create the document
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                    } else {
                        fileUri = Uri.parse(CONTENT_URI);
                    }
                } else {
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        fileUri = Uri.fromFile(file);
                    } else {
                        fileUri = Uri.parse(CONTENT_URI);
                    }
                }

                PdfWriter.getInstance(document, new FileOutputStream(file));

                document.open();

                PdfPTable table = new PdfPTable(15);
                table.setWidthPercentage(100);
                Font font = new Font();
                font.setFamily("arial");
                font.setSize(14);
                font.setStyle("bold");
                PdfPCell cellHeader;
                cellHeader = new PdfPCell(new Phrase(context.getString(R.string.sr_no), font));
                table.addCell(cellHeader);
                cellHeader = new PdfPCell(new Phrase(context.getString(R.string.documentType), font));
                table.addCell(cellHeader);
                cellHeader = new PdfPCell(new Phrase(context.getString(R.string.lastName), font));
                table.addCell(cellHeader);
                cellHeader = new PdfPCell(new Phrase(context.getString(R.string.firstName), font));
                table.addCell(cellHeader);
                cellHeader = new PdfPCell(new Phrase(context.getString(R.string.documentNo), font));
                table.addCell(cellHeader);
                cellHeader = new PdfPCell(new Phrase(context.getString(R.string.country), font));
                table.addCell(cellHeader);
                cellHeader = new PdfPCell(new Phrase(context.getString(R.string.gender), font));
                table.addCell(cellHeader);
                cellHeader = new PdfPCell(new Phrase(context.getString(R.string.dateOfBirth), font));
                table.addCell(cellHeader);
                cellHeader = new PdfPCell(new Phrase(context.getString(R.string.dateOfExpiry), font));
                table.addCell(cellHeader);
                cellHeader = new PdfPCell(new Phrase(context.getString(R.string.address), font));
                table.addCell(cellHeader);
                cellHeader = new PdfPCell(new Phrase(context.getString(R.string.glassesDecision), font));
                table.addCell(cellHeader);
                cellHeader = new PdfPCell(new Phrase(context.getString(R.string.glassesScore), font));
                table.addCell(cellHeader);
                cellHeader = new PdfPCell(new Phrase(context.getString(R.string.livenessScore), font));
                table.addCell(cellHeader);
                cellHeader = new PdfPCell(new Phrase(context.getString(R.string.LivenessResult), font));
                table.addCell(cellHeader);
                cellHeader = new PdfPCell(new Phrase(context.getString(R.string.text_photo), font));
                table.addCell(cellHeader);

                Font fontNormal = new Font();
                font.setFamily("Times-Roman");
                font.setSize(12);
                font.setStyle("normal");
                PdfPCell cellContent;

                int count = 1;
                for (int i = scanDataList.size() - 1; i >= 0; i--) {
                    if (count <= 10) {
                        ScanData scanData = scanDataList.get(i);
                        cellContent = new PdfPCell(new Phrase(String.valueOf(count), fontNormal));
                        table.addCell(cellContent);
                        cellContent = new PdfPCell(new Phrase(scanData.getDocumentType(), fontNormal));
                        table.addCell(cellContent);
                        cellContent = new PdfPCell(new Phrase(scanData.getLastName(), fontNormal));
                        table.addCell(cellContent);
                        cellContent = new PdfPCell(new Phrase(scanData.getFirstName(), fontNormal));
                        table.addCell(cellContent);
                        cellContent = new PdfPCell(new Phrase(scanData.getPassportNo(), fontNormal));
                        table.addCell(cellContent);
                        cellContent = new PdfPCell(new Phrase(scanData.getCountry(), fontNormal));
                        table.addCell(cellContent);
                        cellContent = new PdfPCell(new Phrase(scanData.getGender(), fontNormal));
                        table.addCell(cellContent);
                        cellContent = new PdfPCell(new Phrase(scanData.getDateOfBirth(), fontNormal));
                        table.addCell(cellContent);
                        cellContent = new PdfPCell(new Phrase(scanData.getDateOfExpiry(), fontNormal));
                        table.addCell(cellContent);
                        cellContent = new PdfPCell(new Phrase(scanData.getAddress(), fontNormal));
                        table.addCell(cellContent);
                        cellContent = new PdfPCell(new Phrase(scanData.getGlassesDecision(), fontNormal));
                        table.addCell(cellContent);
                        cellContent = new PdfPCell(new Phrase(scanData.getGlassesScore(), fontNormal));
                        table.addCell(cellContent);
                        cellContent = new PdfPCell(new Phrase(scanData.getLivenessScore(), fontNormal));
                        table.addCell(cellContent);
                        cellContent = new PdfPCell(new Phrase(scanData.getLivenessResult(), fontNormal));
                        table.addCell(cellContent);
                        if (scanData.getUserPicture() != null) {
                            Image image = Image.getInstance(scanData.getUserPicture());
                            cellContent = new PdfPCell(image, true);
                            table.addCell(cellContent);
                        } else {
                            cellContent = new PdfPCell(new Phrase("", fontNormal));
                            table.addCell(cellContent);
                        }
                        count++;
                    }
                }

                document.add(table);
                document.addCreationDate();
                document.close();

                SendData(fileUri, context);
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }
        }
    }

    private static void SendData(Uri uri, Context context) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
//        emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Scan data");
//        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(emailIntent, "Choose App"));
    }

    public static String randomToken() {
        return UUID.randomUUID().toString();
    }

    public static byte[] base64FromImage(String path) {
        byte[] bytes = null;
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File(path));
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            bytes = output.toByteArray();
            return bytes;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();

        return byteArray;
    }

    public static File fileFromBitmap(Context context, Bitmap bitmap, String filename) {
        //create a file to write bitmap data
        File f = new File(context.getExternalCacheDir(), filename);
        try {
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }
}