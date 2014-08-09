package org.zju.cadcg.watao.gl;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.zju.cadcg.watao.Watao;
import org.zju.cadcg.watao.shader.WTShader;
import org.zju.cadcg.watao.utils.GLManager;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

public class GLRenderer implements GLSurfaceView.Renderer{
	
	protected static final String TAG = "GLRenderer";
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLManager.initForGL();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		GLManager.changeFrustum(width, height);
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		GLManager.draw(gl);
	}
}
