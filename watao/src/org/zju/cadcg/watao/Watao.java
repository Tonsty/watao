package org.zju.cadcg.watao;




import org.zju.cadcg.watao.activity.DecorateActivity;
import org.zju.cadcg.watao.utils.MySensor;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;


public class Watao extends Application {
	public static int screenWidthPixel;
	public static int screenHeightPixel;
	private static MediaPlayer mp;
	public static float density;
	private static Context THIS;	
	
	public static void pauseBGM(){
		if(mp != null){
			mp.release();
			mp = null;
		}
	}

	public static void startBGM(int bgmId) {
		mp = MediaPlayer.create(THIS, bgmId);
		mp.setLooping(true);
		mp.seekTo(0);
		mp.start();
	}
	
	@Override
	public void onCreate() {
		THIS = this;
		super.onCreate();

		MySensor.init(getApplicationContext());


		//initialize screen width and height
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();  
		windowManager.getDefaultDisplay().getMetrics(dm);  
		Watao.screenWidthPixel = dm.widthPixels;
		Watao.screenHeightPixel = dm.heightPixels;
		Watao.density = dm.density;

		Editor editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
		editor.putBoolean(DecorateActivity.KEY_IS_SHOW_PWD_TIP, true);
		editor.commit();
	}
}
