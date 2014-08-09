package org.zju.cadcg.watao.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.zju.cadcg.watao.utils.PotteryTextureManager.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;
import android.util.SparseArray;

public class FileUtils {
	/* Checks if external storage is available to write */
	public static boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	/**
	 * used for collect pottery;
	 * @author albuscrow
	 *
	 */
	public static class PotterySaved{
		public String fileName;
		public float[] vertices;
		public Bitmap texture;
		public Bitmap image;
	}
	
	public static String savePottery(Context context, PotterySaved pottery){
		String filename = new SimpleDateFormat("yyyyMMddhhmmss",Locale.CHINA).format(new Date(System.currentTimeMillis())); 
		saveSerializable(context, filename, pottery.vertices);
		System.out.println(filename);
		saveImage(context, filename + "1", pottery.texture);
		saveImage(context, filename + "2", pottery.image);
		return filename;
	}
	
	public static void deletePottery(Context context, String fileName){
		deleteSerializable(context, fileName);
		deleteImage(context, fileName + "1");
		deleteImage(context, fileName + "2");
	}
	
	public static List<PotterySaved> getSavedPottery(Context context){
		File parent = context.getFilesDir();
		String[] lists = parent.list();
		Set<String> fileNames = new HashSet<String>();
		for (String string : lists) {
			if (string.length() == 18) {
				fileNames.add(string.substring(0, string.length() - 4));
			}
		}
		List<PotterySaved> result = new ArrayList<PotterySaved>(); 
		
		for (String fileName : fileNames) {
			System.out.println(fileName);
			Options op = new Options();
			op.inSampleSize = 3;
			op.inJustDecodeBounds = false;
			Bitmap image = null;
			try {
				image = BitmapFactory.decodeStream(context.openFileInput(fileName + "2.png"),null, op);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			PotterySaved pottery = new PotterySaved();
			pottery.image = image;
			pottery.fileName = fileName;
			result.add(pottery);
		}
		return result;
	}

	
	public static void saveSerializable(Context context, String fileName, Serializable object) {
		ObjectOutputStream os = null;
		try {
			os =new ObjectOutputStream(context.openFileOutput(fileName+".obj", Context.MODE_PRIVATE));
			os.writeObject(object);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void deleteSerializable(Context context, String fileName){
		File file = new File(context.getFilesDir(), fileName + ".obj");
		file.delete();
	}
	
	public static Object getSerializable(Context context, String fileName) {
		ObjectInputStream is = null;
		try {
			is =new ObjectInputStream(context.openFileInput(fileName+".obj"));
			return is.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	
	public static void saveImage(Context context, String fileName, Bitmap bitmap){
		FileOutputStream os = null;
		try {
			os = context.openFileOutput(fileName + ".png", Context.MODE_PRIVATE);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, os);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void deleteImage(Context context, String fileName){
		File file = new File(context.getFilesDir(), fileName + ".png");
		file.delete();
	}



	public static void saveUncomplete(Context context, 
			float[] radius, float[] vertices, String[] occupy,
			Pattern[] patterns){
		
		//save vertices
		saveSerializable(context, "uv", vertices);
		//save radius
		saveSerializable(context, "ur", radius);
		
		//save occupy
		if (occupy != null) {
			saveSerializable(context, "uo", occupy);
		}
		
		
		if (patterns != null) {
			//delete customer patterns
//			File parent = context.getFilesDir();
//			String[] lists = parent.list();
//			for (String string : lists) {
//				if (string.substring(0, 2).equals("up")) {
//					new File(parent, string).delete();
//				}
//			}
//			
			saveSerializable(context, "up", patterns);
		}
		
	}
	
}
