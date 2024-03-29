package org.zju.cadcg.watao.utils;

import android.util.Log;

public class FramRateUtil {
	private static long startTime;
	private static long fpsStartTime;
	private static int numFrames;

	public static void init(){
		startTime = System.currentTimeMillis();
		fpsStartTime = startTime;
		numFrames = 0;	
	}
	
	public static void count() {
		numFrames++;
		long fpsElapsed = System.currentTimeMillis() - fpsStartTime;
		if (fpsElapsed > 5 * 1000) { // every 5 seconds
			float fps = (numFrames * 1000.0F) / fpsElapsed;
			Log.d("FramUtil", "Frames per second: " + fps + " (" + numFrames
					+ " frames in " + fpsElapsed + " ms)");
			fpsStartTime = System.currentTimeMillis();
			numFrames = 0;
		}
	}
}
