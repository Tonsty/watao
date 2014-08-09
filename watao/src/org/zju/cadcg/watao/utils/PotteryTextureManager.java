package org.zju.cadcg.watao.utils;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zju.cadcg.watao.activity.DecorateActivity;
import org.zju.cadcg.watao.type.WTDecorator;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class PotteryTextureManager {
	private static final int VERTICAL_PRICISION = 50;
	
	private static Bitmap texture;
	private static Bitmap textureTempBackup;
	private static Bitmap origenalTexture;
	
	private static int textureWidth_PIX;
	private static int textureHeight_PIX;
	
	private static Boolean isTextureInvalid = false;
	private static int[] GLTextureNames = new int[1];
	
	private static String[] occupied = new String[VERTICAL_PRICISION * 10];
	public static String[] getOccupied() {
		return occupied;
	}
	public static void setOccupied(String[] occupied) {
		PotteryTextureManager.occupied = occupied;
	}

	private static Map<String, Pattern> patterns = new HashMap<String, Pattern>();
	public static Map<String, Pattern> getPatterns() {
		return patterns;
	}
	public static void setPatterns(Pattern[] patternsList) {
		for (Pattern pattern : patternsList) {
			patterns.put(pattern.idAfter + pattern.top, pattern);
		}
	}

	public static boolean isEraseMode = false;
	
	public static boolean isBefore;
	
	public static final void changeBaseTexture(Resources resource, int id){
//		if (texture != null) {
//			texture.recycle();
//			texture = null;
//		}
		if (textureTempBackup != null) {
			textureTempBackup.recycle();
			textureTempBackup = null;
		}
		if(origenalTexture != null){
			origenalTexture.recycle();
			origenalTexture = null;
		}
		System.gc();
		
		Options opts = new Options();
		opts.inMutable = true;
		texture = BitmapFactory.decodeStream(resource.openRawResource(id), null, opts);
		origenalTexture = texture.copy(Bitmap.Config.ARGB_8888, true); 
		textureWidth_PIX = texture.getWidth();
		textureHeight_PIX = texture.getHeight()/2;
		reloadPattern();
		isTextureInvalid = true;
	}
	
	
	public static final void setBaseTexture(Resources resource, int id){
//		if (texture != null) {
//			texture.recycle();
//			texture = null;
//		}
		if (textureTempBackup != null) {
			textureTempBackup.recycle();
			textureTempBackup = null;
		}
		if(origenalTexture != null){
			origenalTexture.recycle();
			origenalTexture = null;
		}
		System.gc();
		Options opts = new Options();
		opts.inMutable = true;
		texture = BitmapFactory.decodeStream(resource.openRawResource(id), null, opts);
		origenalTexture = texture.copy(Bitmap.Config.ARGB_8888, true); 
		textureWidth_PIX = texture.getWidth();
		textureHeight_PIX = texture.getHeight()/2;
		for(int i = 0; i < VERTICAL_PRICISION * 10; ++i){
			occupied[i] = null;
		}
		patterns.clear();
		isTextureInvalid = true;
	}	
	
	public static final void setBaseTexture(Bitmap bitmap){
		if (texture != null) {
			texture.recycle();
			texture = null;
		}
		if (textureTempBackup != null) {
			textureTempBackup.recycle();
			textureTempBackup = null;
		}
		if(origenalTexture != null){
			origenalTexture.recycle();
			origenalTexture = null;
		}
		System.gc();
		texture = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		bitmap.recycle();
		bitmap = null;
		System.gc();
		origenalTexture = texture.copy(Bitmap.Config.ARGB_8888, true); 
		textureWidth_PIX = texture.getWidth();
		textureHeight_PIX = texture.getHeight()/2;
		for(int i = 0; i < VERTICAL_PRICISION * 10; ++i){
			occupied[i] = null;
		}
		patterns.clear();
		isTextureInvalid = true;
	}
	
	public static final Bitmap getTexture(){
		return texture;
	}
	
	
	public static void reloadPattern(){
		texture = origenalTexture.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(texture);
		for (Pattern pattern : patterns.values()) {
			occupy(pattern.buttom, pattern.top, pattern);
			Bitmap tempTexture = null;
			if (isBefore) {
				tempTexture = getPatternTexture(pattern.idBefore);
			}else{
				tempTexture = getPatternTexture(pattern.idAfter);
			}
			canvas.drawBitmap(Bitmap.createScaledBitmap(tempTexture, textureWidth_PIX, pattern.heightf, true), 0, pattern.topf, null);
		}
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
	}
	
	
	public static final void addTexture(int bottom, int top, Bitmap decorator){
		synchronized (isTextureInvalid) {
			Set<String> checkOccupied = checkOccupied(bottom,top);
			int middleNewPattern = (bottom + top) / 2;	
			if (checkOccupied.size() == 1) {
				Object key = checkOccupied.toArray()[0];
				Pattern p = patterns.get(key);
				int middleOrignal = (p.top + p.buttom)/2;
				int dis = Math.abs(middleNewPattern - middleOrignal);
				int halfOfTwoPattern = ((p.top - p.buttom)+(top - bottom))/2;
				int overLapWidth = halfOfTwoPattern - dis;
				if(dis / (float)halfOfTwoPattern > 0.25f && top + overLapWidth < occupied.length && bottom - overLapWidth >= 0){
					if (middleNewPattern > middleOrignal) {
						overLapWidth = p.top - bottom;
						bottom += overLapWidth;
						top += overLapWidth;
					}else{
						overLapWidth = top - p.buttom;
						bottom -= overLapWidth;
						top -= overLapWidth;
					}
					checkOccupied = checkOccupied(bottom, top);
				}
			}
			
			float topf = (float)top/(float)VERTICAL_PRICISION / 10;
			float bottomf = (float)bottom / (float)VERTICAL_PRICISION / 10;
			Canvas canvas = new Canvas(texture);
			int dstHeight = (int) ((topf - bottomf)*textureHeight_PIX);
			float drawtopf = textureHeight_PIX*(2 - topf * 0.97f);
			if(0 == checkOccupied.size()){
				if (!isEraseMode) {
					if(decorator != null){
						Pattern pattern = new Pattern(top, bottom, drawtopf, dstHeight, currentDecorater.idBefore, currentDecorater.idAfter);
						patterns.put(pattern.idAfter + pattern.top, pattern);
						occupy(bottom, top, pattern);
						canvas.drawBitmap(Bitmap.createScaledBitmap(decorator, textureWidth_PIX, dstHeight, true), 0, drawtopf, null);
						DecorateActivity.isModify = true;
					}else{
						Paint paint = new Paint();
						paint.setColor(Color.WHITE);
						paint.setStyle(Paint.Style.FILL);
						canvas.drawRect(0, drawtopf, textureWidth_PIX, drawtopf + dstHeight, paint);
					}
				}
			}else{
				if(decorator != null){
					Pattern pattern = new Pattern(top, bottom, drawtopf, dstHeight, currentDecorater.idBefore, currentDecorater.idAfter);
					if (!isEraseMode) {
						patterns.put(pattern.idAfter + pattern.top, pattern);
					}
					deletePattern(checkOccupied);
					reloadPattern();
					DecorateActivity.isModify = true;
				}else{
					Paint paint = new Paint();
					paint.setColor(Color.RED);
					paint.setStyle(Paint.Style.FILL);
					if (isEraseMode) {
						for (String id : checkOccupied) {
							Pattern p = patterns.get(id);
							canvas.drawRect(0, p.topf, textureWidth_PIX, p.topf + p.heightf, paint);
						}
					}else{
						canvas.drawRect(0, drawtopf, textureWidth_PIX, drawtopf + dstHeight, paint);
					}
					
				}
			}
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
			isTextureInvalid = true;
		}
	}
	
	private static void deletePattern(Set<String> checkOccupied) {
		for(String id:checkOccupied){
			Pattern pattern = patterns.get(id);
			patterns.remove(id);
			for(int i = pattern.buttom; i < pattern.top; ++i){
				occupied[i] = null;
			}
		}
	}

	private static Set<String> checkOccupied(int bottom, int top) {
		Set<String> result = new HashSet<>();
		for(int i = bottom; i < top; ++i){
			if(occupied[i] != null){
				result.add(occupied[i]);
			}
		}
		return result;
	}
	
	private static void occupy(int bottom, int top, Pattern pattern){
		for(int i = bottom; i < top; ++i){
			occupied[i] = pattern.idAfter + pattern.top;
		}
	};

	public static final void loadTexture(){
		if (GLTextureNames[0] == 0) {
			synchronized (GLTextureNames) {
				if (GLTextureNames[0] == 0) {
					GLES20.glGenTextures(1, GLTextureNames, 0);
				}
			}
		}
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLTextureNames[0]);
		
		if (isTextureInvalid) {
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture, 0);
		}

	}

	public static void preDerocate_(int bottom, int top ) {
		textureTempBackup = texture.copy(Bitmap.Config.ARGB_8888, true);
		addTexture(bottom, top, null);
	}
	
	public static void tempDerocate_(int bottom, int top){
		if (textureTempBackup == null) {
			return;
		}
		texture = textureTempBackup.copy(Bitmap.Config.ARGB_8888, true);
		addTexture(bottom, top, null);
	}
	
	public static void finalDerocate_(int bottom, int top){
		if (textureTempBackup == null) {
			return;
		}
		texture = textureTempBackup;
		textureTempBackup = null;
		String currentId = null;
		if (isBefore) {
			currentId = currentDecorater.idBefore;
		}else{
			currentId = currentDecorater.idAfter;
		}
		addTexture(bottom, top, getPatternTexture(currentId));
	}
	
	static Map<String,SoftReference<Bitmap>> texturePool = new HashMap<String, SoftReference<Bitmap>>();
	public static Bitmap getPatternTexture(String id) {
		Bitmap texture = null;
		SoftReference<Bitmap> softReference = texturePool.get(id);
		if (softReference == null || (texture = softReference.get()) == null) {
			texture = getBitmapFromPatternId(id);
			texturePool.put(id, new SoftReference<Bitmap>(texture));
		}
		return texture;
	}
	
	private static Bitmap getBitmapFromPatternId(String id) {
		try{
			int i = Integer.parseInt(id);
			return BitmapFactory.decodeStream(GLManager.context.getResources().openRawResource(i));
		}catch (Exception e){
			try {
				return BitmapFactory.decodeStream(GLManager.context.openFileInput(id));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return null;
			}
		}
	}

	public static void setIsInvalid() {
		isTextureInvalid = true;
	}
	
	public static void setIsInvalidGL() {
		GLTextureNames[0] = 0;
	}
	
	public static WTDecorator currentDecorater;
	
	public static class Pattern implements Serializable{

		private static final long serialVersionUID = -7459629728473683679L;
		
		public int top;
		public int buttom;
		public float topf;
		public int heightf;
		public String idAfter;
		public String idBefore;
		
		public Pattern(int top,int buttom,float topf, int heightf, String idBefore, String idAfter) {
			this.top = top;
			this.buttom = buttom;
			this.topf = topf;
			this.heightf = heightf;
			this.idAfter = idAfter;
			this.idBefore = idBefore;
		}
		
		public static List<Integer> usedId;
		
	}


	public static void preDerocate(float y, float height) {
		float yInPottery = computerYInPottery(y, height);
		if (yInPottery< -0.15 || yInPottery- GLManager.pottery.getHeight() >= 0.3) {
			return;
		}
		
		int[] border = computerBorder(yInPottery, currentDecorater.getWidth(), GLManager.pottery.getHeight());
		preDerocate_(border[0], border[1]);
	}
	
	public static void tempDerocate(float y, float height) {
		float yInPottery = computerYInPottery(y, height);
		if (yInPottery< -0.15 || yInPottery- GLManager.pottery.getHeight() >= 0.3) {
			return;
		}

		int[] border = computerBorder(yInPottery, currentDecorater.getWidth(), GLManager.pottery.getHeight());
		tempDerocate_(border[0], border[1]);
	}
	
	
	public static void finalDerocate(float y, float height) {
		float yInPottery = computerYInPottery(y, height);
		if (yInPottery< -0.15 || yInPottery- GLManager.pottery.getHeight() >= 0.3) {
			PotteryTextureManager.reloadPattern();
			return;
		}
		
		int[] border = computerBorder(yInPottery, currentDecorater.getWidth(), GLManager.pottery.getHeight());
		finalDerocate_(border[0], border[1]);
	}
	
	static private int[] computerBorder(float yInPottery, float width, float height) {
		int[] border = new int[2];
		if(yInPottery > height){
			yInPottery = height;
		}else if(yInPottery < 0){
			yInPottery = 0;
		}
		float bottom = yInPottery - width/2;
		float top = yInPottery + width/2;
		if (top > height) {
			bottom -= (top - height);
			top = height;
		}
		if(bottom < 0){
			top += -bottom;
			bottom = 0;
		}
		border[0] = (int) (bottom / height * VERTICAL_PRICISION * 10);
		border[1] = (int) (top / height* VERTICAL_PRICISION * 10);
		return border;
	}
	
	private final static float TAN_22_5 = 0.40402622583516f;
	protected static float computerYInPottery(float y, float height) {
		return (0.5f * height - y) / height * 2 * (float) TAN_22_5 * 6f + 2.1f;
	}
	
}
