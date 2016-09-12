package com.example.liumin.arcamera.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	private static final  String TAG = "FileUtil";
	private static final File parentPath = Environment.getExternalStorageDirectory();
	private static   String storagePath = "";
	private static final String DST_FOLDER_NAME = "PlayCamera";

	/**��ʼ������·��
	 * @return
	 */
	private static String initPath(){
		if(storagePath.equals("")){
			storagePath = parentPath.getAbsolutePath()+"/" + DST_FOLDER_NAME;
			File f = new File(storagePath);
			if(!f.exists()){
				f.mkdir();
			}
		}
		return storagePath;
	}

	/**����Bitmap��sdcard
	 * @param b
	 */
	public static void saveBitmap(Bitmap b){

		String path = initPath();
		long dataTake = System.currentTimeMillis();
		String jpegName = path + "/" + dataTake +".jpg";
		Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.i(TAG, "saveBitmap�ɹ�");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "saveBitmap:ʧ��");
			e.printStackTrace();
		}

	}

	//遍历某个目录
	public static List<String>  getFile(File file) {
		Log.e("file parent", file.getAbsolutePath());
		List list = new ArrayList<String>();
		File[] subFiles = file.listFiles();

		if (subFiles != null) {
			for (File f : subFiles) {
				boolean flag = true;
				if (f.isFile()) {
					Log.e("file movie", f.getAbsolutePath());
					String name = f.getName();
					if (name.trim().toLowerCase().endsWith(".png") || name.trim().toLowerCase().endsWith(".jpg") || name.trim().toLowerCase().endsWith(".jpeg")
							|| name.trim().toLowerCase().endsWith(".bmp")) {

						list.add(f.getAbsolutePath());
					}
				}
			}
		}
		return list;
	}

}
