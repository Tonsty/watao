package org.zju.cadcg.watao.gl100;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.zju.cadcg.watao.gl.Pottery;
import org.zju.cadcg.watao.type.WTMode;
import org.zju.cadcg.watao.utils.MySensor;

import android.content.Context;
import android.opengl.GLU;


public class GLRenderer100 extends org.zju.cadcg.watao.gl.GLRenderer{
	
	public GLRenderer100(Context context) {
//		super(context);
	}
	
	public void onDrawFrame(GL10 gl) {
		
		
		// Clear the screen surface
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT
				| GL10.GL_DEPTH_BUFFER_BIT);
		

		// Position model so we can see it
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
//		//draw background
//		background.draw(gl);
//		
//		gl.glTranslatef(0, -2f, -7f);
//		if (mode == WTMode.SHAPE) {
//			float[] sensorData = MySensor.getAngle();
//			if (sensorData != null) {
//				float dataForY = sensorData[0];
//				if (dataForY < 0) {
//					dataForY = 0;
//				}
//				float dataForX = sensorData[1];
//				if (dataForX < 0) {
//					dataForX = -dataForX;
//				}
//				float newAngle = (float) ((dataForX > dataForY ? dataForX
//						: dataForY) / 1.5);
//				if (direction == 1) {
//					if (newAngle > currentAngleRotateX) {
//						if (newAngle - currentAngleRotateX > 3) {
//							currentAngleRotateX += 0.2 * (newAngle - currentAngleRotateX) / 10;
//						}
//					} else {
//						direction = -1;
//					}
//				} else {
//					if (newAngle < currentAngleRotateX) {
//						if (currentAngleRotateX - newAngle > 3) {
//							currentAngleRotateX -= 0.2 * (currentAngleRotateX - newAngle) / 10;
//						}
//					} else {
//						direction = 1;
//					}
//				}
//				gl.glRotatef(currentAngleRotateX, 1, 0, 0);
//			}
//		}
//			   
//
//		gl.glPushMatrix();
////		gl.glRotatef( 10.0f, 1, 0, 0 );
//		if(mode == WTMode.INTERACT_VIEW){
//			gl.glRotatef(verticalAngle, 1, 0, 0);
//			gl.glRotatef(horizontalAngle, 0, 1, 0);
//		}else{
//			// Set rotation angle based on the time
//			long elapsed = System.currentTimeMillis() - startTime;
//			gl.glRotatef(elapsed /5, 0, 1, 0);
//		}
//		pottery.draw(gl);
//	    table.draw(gl);
//	    
//	    gl.glPopMatrix();
//
////	    shadow.draw(gl);
		
	    super.onDrawFrame(gl);
	}


	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// ...

		// Define the view frustum
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		float ratio = (float) width / height;
		GLU.gluPerspective(gl, 45.0f, ratio, 1, 100f);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);
//
//		if(pottery == null){
//			pottery = new Pottery100();
//		}
		
//		background = new Background100(BitmapFactory.decodeResource(context.getResources(), R.drawable.create_background));
//		table = new Table100(context, "table.obj", BitmapFactory.decodeResource(context.getResources(), R.drawable.table));
//		shadow = new Shadow100(context, "shadow.obj", BitmapFactory.decodeResource(context.getResources(), R.drawable.shadow), 1.2f, 0.6f);
//		this.shadow.verticalChange(pottery.getHeight());
//		this.shadow.HorizonalChange(pottery.getBases(), pottery.getHeight());

		// Define the lighting
		gl.glEnable(GL10.GL_LIGHTING);
		float lightSpecular0[] = new float[] { 0.2f, 0.2f, 0.2f, 1.0f };
		float lightAmbient0[] = new float[] { 0.4f, 0.4f, 0.4f, 1.0f };
		float lightDiffuse0[] = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };
		float[] lightPos0 = new float[] { -20, 5, 20, 1 };
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, lightSpecular0, 0 );
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient0, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse0, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos0, 0);

//		float lightSpecular1[] = new float[] { 1f, 1f, 1f, 1f };
//		float lightAmbient1[] = new float[] { 1f, 1f, 1f, 1f };
//		float lightDiffuse1[] = new float[] { 1f, 1f, 1f, 1f };
//		float[] lightPos1 = new float[] { 0, 5, 10, 1 };
//		gl.glEnable(GL10.GL_LIGHT1);
//		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPECULAR, lightSpecular1, 0 );
//		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, lightAmbient1, 0);
//		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, lightDiffuse1, 0);
//		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, lightPos1, 0);

		float matSpecular[] = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
		float matAmbient[] = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
		float matDiffuse[] = new float[] {1.0f, 1.0f, 1.0f, 1.0f};	
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, matSpecular, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);
		gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 64.0f );

		// Set up any OpenGL options we need
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//		isReady = true;
	}

	public void preDerocate(float y, float height) {
//		float yInPottery = computerYInPottery(y, height);
//		pottery.preDerocate(yInPottery, 1l, 0.5f, BitmapFactory.decodeResource(context.getResources(), R.drawable.t5));
	}

//	public void reshape( float lastX, float lastY, float X, float Y, float width, float height ){
//		if(isReady){
//			float deltaX = X - lastX;
//			float deltaY = Y - lastY;
//			float[] bases = pottery.getBases();
//			float heightPottery = pottery.getHeight();
//			if( Math.abs( deltaX ) > Math.abs( deltaY ) ){
//				float y = computerYInPottery(Y, height);
//				if( X > width * 0.5f && deltaX > 0 || X < width * 0.5f && deltaX < 0 ){
//					pottery.fatter(y);
//					shadow.HorizonalChange(bases, heightPottery);
//				}
//				if( X < width * 0.5f && deltaX > 0 || X > width * 0.5f && deltaX < 0 ){
//					pottery.thinner( y );
//					shadow.HorizonalChange(bases, heightPottery);
//				}
//
//			}else{
//				if( deltaY < 0 ) {
//					pottery.taller();
//					shadow.verticalChange(heightPottery);
//				}
//				if( deltaY > 0 ) {
//					pottery.shorter();
//					shadow.verticalChange(heightPottery);
//				}
//			}
//		}
//	}

	public void setPottery(Pottery pottery){
		if(pottery != null){
//			this.pottery = pottery;

		}
	}

}
