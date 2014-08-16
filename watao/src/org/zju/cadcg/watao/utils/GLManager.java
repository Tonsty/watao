package org.zju.cadcg.watao.utils;

import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.zju.cadcg.watao.R;
import org.zju.cadcg.watao.Watao;
import org.zju.cadcg.watao.activity.DecorateActivity;
import org.zju.cadcg.watao.gl.Shadow;
import org.zju.cadcg.watao.gl200.Background200;
import org.zju.cadcg.watao.gl200.Fire;
import org.zju.cadcg.watao.gl200.Pottery200;
import org.zju.cadcg.watao.gl200.Shadow200;
import org.zju.cadcg.watao.gl200.Table200;
import org.zju.cadcg.watao.shader.WTShader;
import org.zju.cadcg.watao.type.WTMode;
import org.zju.cadcg.watao.utils.PotteryTextureManager.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.preference.PreferenceManager;
import android.text.method.MovementMethod;

public class GLManager {

	public static WTMode mode;
	public static Background200 background;
	public static Fire fire;
	public static Shadow shadow;
	public static Pottery200 pottery;
	public static Table200 table;

	public static Context context;
	
	private final static float TAN_22_5 = 0.40402622583516f;

	private static float horizontalAngle;
	public static float rotateSpeed;
	private static float verticalAngle;
	private static int direction;
	private static float eyeOffset;
	private static Thread moveBackgroundthread;
	private static Thread rotateThread;
	public static int hasUncomplete;
	public static int decoratorTypeUn;
	
	public static void initForGL(){
		WTShader.initForAllShader();
		((Pottery200)pottery).createShader(eyeOffset,context.getResources());
		WTShader.addGLMeshObject(pottery);
		
		((Background200) background).createShader(context.getResources());
		((Fire)fire).createShader(context.getResources());
		WTShader.addGLMeshObject(background);
		WTShader.addGLMeshObject(fire);

		((Table200)table).createShader(eyeOffset);
		WTShader.addGLMeshObject(table);
	
		((Shadow200)shadow).createShader(eyeOffset, context.getResources());
		WTShader.addGLMeshObject(shadow);
	
		WTShader.initVBO();
		alreadyInitGL = true;
	}
	
	public static void changeFrustum(int width, int height) {
		float ratio = (float) width / height;
		float left = -ratio*TAN_22_5;
		float right = ratio*TAN_22_5;
		float bottom = -TAN_22_5;
		float top = TAN_22_5;
		float near = 1.0f;
		float far = 100.0f;
		WTShader.frustumM(left, right, bottom, top, near, far);
		background.genPosition(left, right, bottom, top, near, far);
		fire.genPosition(left, right, bottom, top, near, far);
	}

	public static void changeGesture(float deltaX, float deltaY) {
		horizontalAngle += deltaX / 6;
		horizontalAngle %= 360;
		verticalAngle += deltaY / 6;
		if (verticalAngle > 45) {
			verticalAngle = 45;
		} else if (verticalAngle < -10) {
			verticalAngle = -10;
		}
	}

	protected static float computerYInPottery(float y, float height) {
		return (0.5f * height - y) / height * 2 * (float) TAN_22_5 * 6f + 2.1f;
	}

	public static void init(Context context) {
		FramRateUtil.init();
		GLManager.context = context;
		
		pottery = new Pottery200();
		table = new Table200(context, "table.obj");
		shadow = new Shadow200(context, "shadow.obj");
		background = new Background200();
		fire = new Fire();
		
		SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		hasUncomplete = defaultSharedPreferences.getInt("hasUncomplete", 0);
//			restorePottery(context, defaultSharedPreferences);
//			PotteryTextureManager.setBaseTexture(context.getResources(), R.drawable.clay);
//			((Pottery200)pottery).switchShader(Pottery200.CLAY);
		if (rotateThread == null) {
			rotateThread = new Thread() {
				@Override
				public void run() {
					long startTime = System.currentTimeMillis();
					while(!this.isInterrupted()){
						long temp = System.currentTimeMillis();
						horizontalAngle += (temp-startTime) * rotateSpeed;
						startTime = temp;
						horizontalAngle %= 360;
					}
				}
			};	
			rotateThread.start();
		}
		if (moveBackgroundthread == null) {

			moveBackgroundthread = new Thread() {
				@Override
				public void run() {
					MySensor.openAccelerometer();
					while(!this.isInterrupted()){
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						float[] data = MySensor.getAccelerometerData();
						if (data != null) {
							float current[] = background.getVertices().clone();
							float alpha = 0.2f;
							float vertices[] = new float[12];
							vertices[0] = (background.orignalVertices[0] - data[0] - current[0]) * alpha + current[0] ;
							vertices[1] = (background.orignalVertices[1] - data[1] - current[1]) * alpha + current[1];
							vertices[2] = background.orignalVertices[2];
							vertices[3] = (background.orignalVertices[3] - data[0] - current[3]) * alpha + current[3];
							vertices[4] = (background.orignalVertices[4] - data[1] - current[4]) * alpha + current[4];
							vertices[5] = background.orignalVertices[5];
							vertices[6] = (background.orignalVertices[6] - data[0] - current[6]) * alpha + current[6];
							vertices[7] = (background.orignalVertices[7] - data[1] - current[7]) * alpha + current[7];
							vertices[8] = background.orignalVertices[8];
							vertices[9] = (background.orignalVertices[9] - data[0] - current[9]) * alpha + current[9];
							vertices[10] = (background.orignalVertices[10] - data[1] - current[10]) * alpha + current[10];
							vertices[11] = background.orignalVertices[11];							
							background.setVertices(vertices);							
						}
					}
				}
			};

			moveBackgroundthread.start();

		}
		alreadyInit = true;
	}

	public static void restorePottery(Context context,
			SharedPreferences defaultSharedPreferences) {
		float[] radius = (float[]) FileUtils.getSerializable(context, "ur");
//		float[] vertices = (float[]) FileUtils.getSerializable(context, "uv");
		Pattern[] ps = null;
		String[] occupy = null;
		if (hasUncomplete == 2) {
			ps = (Pattern[]) FileUtils.getSerializable(context, "up");
			occupy = (String[]) FileUtils.getSerializable(context, "uo");
		}
//		System.out.println(radius);
//		System.out.println(vertices);
//		System.out.println(hasUncomplete);
//		System.out.println(ps);
//		System.out.println(occupy);
		if (radius != null /*&& vertices != null*/ && (hasUncomplete == 1 ||(ps != null && occupy != null))) {
//			pottery.setRadiuses(radius);
//			pottery.setVertices(vertices);
			pottery.setVarUsedForEllipseToRegular(defaultSharedPreferences.getFloat("var", 0));
//			pottery.setCurrentHeight(float1);;

			float endHeight = defaultSharedPreferences.getFloat("height", -1.0f);
			pottery.startReset(pottery.getCurrentHeight(), endHeight, pottery.getRadiuses().clone(), radius, false);
			if (hasUncomplete == 1) {
				PotteryTextureManager.setBaseTexture(context.getResources(), R.drawable.clay);
				((Pottery200)pottery).switchShader(Pottery200.CLAY);
			}else if(hasUncomplete == 2){
				decoratorTypeUn = defaultSharedPreferences.getInt("potteryType", -1);
				if (decoratorTypeUn == DecorateActivity.QINGHUA) {
					PotteryTextureManager.setBaseTexture(context.getResources(), R.drawable.clay);
					((Pottery200)pottery).switchShader(Pottery200.DRY_CLAY);
				}else{
					PotteryTextureManager.setBaseTexture(context.getResources(), R.drawable.procelain);
					((Pottery200)pottery).switchShader(Pottery200.CI);
				}
				PotteryTextureManager.setOccupied(occupy);
				PotteryTextureManager.setPatterns(ps);

				PotteryTextureManager.reloadPattern();

			}

			PreferenceManager.getDefaultSharedPreferences(context)
			.edit()
			.putInt("hasUncomplete", 0).commit();
			hasUncomplete = 0;
		}else{
			PotteryTextureManager.
			setBaseTexture(context.getResources(), R.drawable.clay);
			((Pottery200)pottery).switchShader(Pottery200.CLAY);
		}
	}
	
	
		
	public static void setEyeOffset(float eyeOffset) {
		if (alreadyInit) {
			((Table200)table).setOffset(eyeOffset);
			((Pottery200)pottery).setOffset(eyeOffset);
			((Shadow200)shadow).setOffset(eyeOffset);
			GLManager.eyeOffset = eyeOffset;
		}
	}


	public static boolean reshape(float lastX, float lastY, float currentX,
			float currentY, float width, float height) {
		if (pottery != null) {
			float deltaX = currentX - lastX;
			float deltaY = currentY - lastY;
			if (Math.abs(deltaX) > Math.abs(deltaY)) {
				float y = computerYInPottery(currentY, height);
				if (currentX > width * 0.5f && deltaX > 0
						|| currentX < width * 0.5f && deltaX < 0) {
					pottery.fatter(y);
				}
				if (currentX < width * 0.5f && deltaX > 0
						|| currentX > width * 0.5f && deltaX < 0) {
					pottery.thinner(y);
				}
			} else {
				if (deltaY < -3) {
					pottery.taller();
				}else if (deltaY > 3) {
					pottery.shorter();
				}
			}
		}
		return true;
	}

	public static void saveOnDesc() {
	}

	public static boolean isDrawed = false;
	public static void draw(GL10 gl) {
		if (!alreadyInitGL || !alreadyInit) {
			return;
		}
		FramRateUtil.count();
		if (needImage) {
			outImage = SavePixels(gl);
			needImage = false;
		}
		if (mode != WTMode.INTERACT_VIEW) {
			sensorRotate();
		}
		pottery.setAngleForRotate(horizontalAngle);
		pottery.setAngleRotateY(verticalAngle);
		table.setAngleForRotate(horizontalAngle);
		table.setAngleRotateY(verticalAngle);
		shadow.setAngleRotateY(verticalAngle);
		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		pottery.draw();
		table.draw();
		
		background.draw();
		
		GLES20.glEnable(GLES20.GL_BLEND);
		if(mode == WTMode.FIRE){
			GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			fire.draw();
		}else{
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			shadow.draw();
		}
		GLES20.glDisable(GLES20.GL_BLEND);

		isDrawed = true;
	}

	private static boolean sensorRotate() {
		float[] sensorData = MySensor.getAngle();
		if (sensorData == null) {
			return false;
		}

		float dataForY = sensorData[0];
		if (dataForY < 0) {
			dataForY = 0;
		}
		float dataForX = sensorData[1];
		if (dataForX < 0) {
			dataForX = -dataForX;
		}
		float newAngle = (float) ((dataForX > dataForY ? dataForX
				: dataForY));
		float temp = 30 - newAngle / 3;
		if (direction == 1) {
			if (temp > verticalAngle) {
				if (temp - verticalAngle > 3) {
					verticalAngle += 0.08 * (temp - verticalAngle);
				}
			} else {
				direction = -1;
			}
		} else {
			if (temp < verticalAngle) {
				if (verticalAngle - temp > 3) {
					verticalAngle -= 0.08 * (verticalAngle - temp);
				}
			} else {
				direction = 1;
			}
		}

		return true;
	}
		public static Bitmap SavePixels(GL10 gl) {  
		int x = 0, y = 0, w = Watao.screenWidthPixel, h = Watao.screenHeightPixel;
	    int b[]=new int[w*h];
	    int bt[]=new int[w*h];
	    IntBuffer ib=IntBuffer.wrap(b);
	    ib.position(0);
	    gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);

	    /*  remember, that OpenGL bitmap is incompatible with 
	        Android bitmap and so, some correction need.
	     */   
	    for(int i=0; i<h; i++)
	    {         
	        for(int j=0; j<w; j++)
	        {
	            int pix=b[i*w+j];
	            int pb=(pix>>16)&0xff;
	            int pr=(pix<<16)&0x00ff0000;
	            int pix1=(pix&0xff00ff00) | pr | pb;
	            bt[(h-i-1)*w+j]=pix1;
	        }
	    }              
	    Bitmap sb=Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
	    return sb;
	}
		
	private static boolean needImage = false;
	private static Bitmap outImage;
	private static float[] potteryBackup;
	public static boolean alreadyInit = false;
	public static boolean alreadyInitGL = false;
	public static Bitmap tempImage;


	public static Bitmap getImage(int width, int height){
		needImage = true;
		while(outImage == null){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Bitmap temp = Bitmap.createScaledBitmap(outImage, width, height, false);
		outImage.recycle();
		outImage = null;
		
		int wid = temp.getWidth();
		int hei = temp.getHeight();
		return	Bitmap.createBitmap(temp, 0, (int) (hei * 0.25f), wid, (int) (hei * 0.75f));
	}

	public static void pushPottery() {
		potteryBackup = GLManager.pottery.getVertices().clone();
	}
	
	public static void popPottery(){
		if (potteryBackup != null) {
			pottery.setVertices(potteryBackup);
			potteryBackup = null;
		}
	}

	public static float getPrice() {
		return 89.9f;
	}

}
