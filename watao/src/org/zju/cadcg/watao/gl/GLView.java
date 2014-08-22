
package org.zju.cadcg.watao.gl;

import org.zju.cadcg.watao.R;
import org.zju.cadcg.watao.type.WTDecorator;
import org.zju.cadcg.watao.type.WTMode;
import org.zju.cadcg.watao.utils.GLManager;
import org.zju.cadcg.watao.utils.PotteryTextureManager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.zju.cadcg.watao.gl100.GLRenderer100;
import org.zju.cadcg.watao.gl200.GLRenderer200;

public class GLView extends GLSurfaceView {

	
	/**
	 * the touch point position
	 */
	private float lastX = 0, lastY = 0;

	/**
	 * the view type
	 * @see WTMode
	 */
	private WTMode mode = WTMode.VIEW;

	/**
	 * the renderer used for render the pottery
	 */
	protected GLRenderer renderer;
	
	public GLRenderer200 getMyRenderer(){
		return (GLRenderer200) renderer;
	}

	/**
	 * used for switch gles version;
	 * TODO delete in release version;
	 */
	boolean isUsedGL1 = false;

	private float deltaX;

	private float deltaY;

	private Boolean lock = false;

	public boolean flag = false;
	

	/**
	 * this construction is used for construct from xml;
	 * @param context
	 * @param attrs
	 */
	public GLView(Context context, AttributeSet attrs){
		super(context,attrs);
		this.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GLView);
		boolean isTransparent;

		try{
			isTransparent = typedArray.getBoolean(R.styleable.GLView_is_transparent, false);
		}finally{
			typedArray.recycle();
		}
		
		if (isTransparent) {
			this.setZOrderOnTop(true);
			this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		}
		final ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configInfo = activityManager.getDeviceConfigurationInfo();
        
        //TODO modify in release version
        int reqGlEsVersion = configInfo.reqGlEsVersion;
		if(!isUsedGL1 && reqGlEsVersion >= 0x20000) {
            this.setEGLContextClientVersion(2);
            renderer = new GLRenderer();
        }else{
        	this.setEGLContextClientVersion(1);
        	renderer = new GLRenderer100(context);
        }
		setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR);
		setRenderer(renderer);
	}
	
	public boolean onTouchEvent(final MotionEvent event) {
		final float width = getWidth();
		final float height = getHeight();
		int action = event.getAction();
		if(GLView.this.mode == WTMode.SHAPE){
			if(action == MotionEvent.ACTION_DOWN){
//				lastX = event.getX();
//				lastY = event.getY();
			}else if(action == MotionEvent.ACTION_MOVE){
				synchronized (lock) {
					lock = true;
				}
				
				float X = event.getX();
				float Y = event.getY();
				if (lastX == 0) {
					lastX = X;
					lastY = Y;
				}
				
				GLManager.reshape(lastX, lastY, X, Y, width, height);
				deltaY = Y - lastY;
				lastX = X;
				lastY = Y;
				synchronized (lock) {
					lock = false;
				}
			}else if(action == MotionEvent.ACTION_UP){
				lastX = lastY = 0;
				new Thread(new Runnable() {
					private float deltaX;
					private float deltaY;
					private float lastX;
					private float lastY;
					{
						this.lastX = GLView.this.lastX;
						this.lastY = GLView.this.lastY;
						this.deltaX = GLView.this.deltaX;
						this.deltaY = GLView.this.deltaY;
					}
					public void run() {
						while(!lock && (Math.abs(deltaX) > 1 || Math.abs(deltaY) > 1)){
							float currentX = this.lastX + deltaX;
							float currentY = this.lastY + deltaY;
//							GLManager.reshape(this.lastX, this.lastY, currentX, currentY, width, height);
							this.lastX = currentX;
							this.lastY = currentY;
							deltaX /= 2;
							deltaY /= 2;
						}
						GLManager.direct = 0;
					}
				}).start();
			}
		}else if(GLView.this.mode == WTMode.DEROCATE){
			if(action == MotionEvent.ACTION_DOWN){
				PotteryTextureManager.preDerocate(event.getY(), height);
			}else if(action == MotionEvent.ACTION_MOVE){
				PotteryTextureManager.tempDerocate(event.getY(), height);
			}else if(action == MotionEvent.ACTION_UP){
				PotteryTextureManager.finalDerocate(event.getY(), height);
			}
		}else if(GLView.this.mode == WTMode.INTERACT_VIEW){
			if(action == MotionEvent.ACTION_DOWN){
				lastX = event.getX();
				lastY = event.getY();
			}else if(action == MotionEvent.ACTION_MOVE){
				float deltaX = event.getX() - lastX;
				float deltaY = event.getY() - lastY;
				GLManager.changeGesture(deltaX,deltaY);
				lastX = event.getX();
				lastY = event.getY();
			}
		}
		return true;
	}

	public void setMode(WTMode mode) {
		this.mode = mode;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		PotteryTextureManager.setIsInvalidGL();
	}
}


