package com.docrecog.scan;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class RecogEngine 
{
	static
	{
		System.loadLibrary("accuraocr");
	}
	
	public static RecogResult g_recogResult = new RecogResult();
	public static int facepick = 1;//0:disable-facepick,1:able-facepick
	
	private static final String TAG = "PassportRecog";
	public 			byte[] 	pDic 	= null;
	public			int		pDicLen = 0;
	public 			byte[] 	pDic1 	= null;
	public			int		pDicLen1 = 0;
	public static 	String[] 	assetNames = {"mMQDF_f_Passport_bottom_Gray.dic","mMQDF_f_Passport_bottom.dic"};

	//This is SDK app calling JNI method
	public native int loadDictionary(Context activity, byte[] img_Dic, int len_Dic, byte[] img_Dic1, int len_Dic1,/*, byte[] licenseKey*/AssetManager assets);

	//return value: 0:fail,1:success,correct document, 2:success,incorrect document
	public native int doRecogYuv420p(byte[] yuvdata,int width,int height, int facepick, int rot,int[] intData,Bitmap faceBitmap, boolean unknownVal);
	public native int doRecogBitmap(Bitmap bitmap, int facepick, int[] intData, Bitmap faceBitmap, int[] faced, boolean unknownVal);
	public native int doFaceDetect(Bitmap bitmap, Bitmap faceBitmap, float[] fConf);

	public static float[] fConf = new float[1]; //face detection confidence
	public static int[] faced = new int[1]; //value for detected face or not

	public static int[] intData = new int [3000];
	
	public static int 		NOR_W	= 400;//1200;//1006;
	public static int 		NOR_H	= 400;//750;//1451;

	public Context con;
	public RecogEngine()
	{
	
	}
	public void initEngine(Context context)
	{
		con = context;
		getAssetFile(assetNames[0],assetNames[1]);
		//	String sLicenseKey = "HHEJBFKOLDOADNEAIJFPMPGGDNNAEIFKCNNGDEGJPKCOBMIICGIOIDHEJKIAHEFJIDMIGMFGAHEMBBBNKMFJOILNALFBGKNGIKPKEDLPILDJFCEAEGMFJMIONLLBMIOJJAOCENAJAKCMKJDJNF";
		int ret = loadDictionary(context, pDic,pDicLen,pDic1,pDicLen1/*,sLicenseKey.getBytes()*/, context.getAssets());
		Log.i("recogPassport","loadDictionary: " + ret);
		if(ret < 0)
		{
			AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
			if(ret == -1) {
				builder1.setMessage("No Key Found");
			} else if(ret == -2) {
				builder1.setMessage("Invalid Key");
			} else if(ret == -3) {
				builder1.setMessage("Invalid Platform");
			} else if (ret == -4) {
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

			/*builder1.setNegativeButton(
					"No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});*/

			AlertDialog alert11 = builder1.create();
			alert11.show();
		}
		//if(ret>0){
		//	AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
		//	builder1.setMessage("Your License is expiring in " + ret + " days. Renew on Time.");
		//	builder1.setCancelable(true);
		//	builder1.setPositiveButton(
		//			"OK",
		//			new DialogInterface.OnClickListener() {
		//				public void onClick(DialogInterface dialog, int id) {
		//					dialog.cancel();
		//				}
		//			});
		//	AlertDialog alert11 = builder1.create();
		//	alert11.show();
		//}
	}
	
	public int getAssetFile(String fileName,String fileName1)
	{
	
		int size = 0;
		try {
	    	InputStream is = this.con.getResources().getAssets().open(fileName);
	        size = is.available();
	        pDic = new byte[size];
	        pDicLen = size;
	        is.read(pDic);
	        is.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		try {
	    	InputStream is = this.con.getResources().getAssets().open(fileName1);
	        size = is.available();
	        pDic1 = new byte[size];
	        pDicLen1 = size;
	        is.read(pDic1);
	        is.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		return size;
	}
	//If fail, empty string.
	// both => 0
	// only face => 1
	// only mrz => 2
	public int doRunData(byte[] data, int width, int height, int facepick, int rot, RecogResult result)
	{
		result.faceBitmap = null;
		if(facepick == 1){
			result.faceBitmap = Bitmap.createBitmap(NOR_W, NOR_H, Config.ARGB_8888);
		}
		long startTime = System.currentTimeMillis();
		int ret = doRecogYuv420p(data, width, height, facepick,rot,intData,result.faceBitmap, true);
		long endTime = System.currentTimeMillis() - startTime;
	    if(ret > 0) //>0
	    {
			result.ret = ret;
	    	result.SetResult(intData);
	    }
	    //Log.i(Defines.APP_LOG_TITLE, "Recog failed - " + String.valueOf(ret) + "- "  + String.valueOf(drawResult[0]));
		return ret;
	}

	//If fail, empty string.
	public int doRunData(Bitmap bmCard, int facepick,int rot, RecogResult result)
	{
		Bitmap faceBmp = null;
		if(facepick == 1){
			faceBmp = Bitmap.createBitmap(NOR_W, NOR_H, Config.ARGB_8888);
		}
		long startTime = System.currentTimeMillis();
		//int ret = doRecogYuv420p(data, width, height, facepick,rot,intData,faceBitmap, true);
		int ret = doRecogBitmap(bmCard, facepick, intData, faceBmp, faced,true);
		long endTime = System.currentTimeMillis() - startTime;

		if(ret > 0)
		{
			if (result.recType == RecType.INIT) {
				if (faced[0] == 0) {
					result.faceBitmap = null; //face not detected
					result.recType = RecType.MRZ;
				} else {
					result.faceBitmap = faceBmp.copy(Bitmap.Config.ARGB_8888, false);
					result.recType = RecType.BOTH;
					result.bRecDone = true;
				}
			}else if (result.recType == RecType.FACE){
				if (faced[0] > 0)
				{
					result.faceBitmap = faceBmp.copy(Bitmap.Config.ARGB_8888, false);
					result.bFaceReplaced = true;
				}
				result.bRecDone = true;
			}

			result.ret = ret;
			result.SetResult(intData);
		}
		//Log.i(Defines.APP_LOG_TITLE, "Recog failed - " + String.valueOf(ret) + "- "  + String.valueOf(drawResult[0]));
		return ret;
	}

    public int doRunFaceDetect(Bitmap bmImg, RecogResult result)
    {
        result.faceBitmap = Bitmap.createBitmap(NOR_W, NOR_H, Config.ARGB_8888);
        long startTime = System.currentTimeMillis();
        int ret = doFaceDetect(bmImg, result.faceBitmap, fConf);
        long endTime = System.currentTimeMillis() - startTime;

        //ret > 0 => detect face ok
		if (ret <= 0) result.faceBitmap = null;

		if (ret > 0 && result.recType == RecType.MRZ)
            result.bRecDone = true;

        return ret;
    }

}
