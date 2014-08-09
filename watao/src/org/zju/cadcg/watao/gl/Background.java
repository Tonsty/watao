package org.zju.cadcg.watao.gl;

import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;


import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Background extends GLMeshObject{
	protected int textureName = 0;
	protected float top;
	protected float bottom;
	protected float left;
	protected float right;
	public float[] orignalVertices;
	public Bitmap texture;
	public Background() {
		vertices = new float[12];
		orignalVertices = new float[12];
		normals = new float[]{
				0,0,1,
				0,0,1,
				0,0,1,
				0,0,1
		};
		texCoords = new float[]{
				0,1,
				1,1,
				1,0,
				0,0
		};
		indices = new short[]{
				0,1,3,
				1,2,3
		};
		updateBuffers();
	}
	public void draw(GL10 gl) {
		
	}
	public void draw() {
		
	}
	
	protected static final float Z = 90.0f;
	public void genPosition(float left, float right, float bottom,
			float top, float near, float far) {
		this.top = top * Z / near;
		this.bottom = bottom * Z / near;
		this.left = left * Z / near;
		this.right = right * Z / near;
		int i = 45;
		vertices = new float[]{
				this.left, this.bottom, -Z + i,
				this.right, this.bottom, -Z+ i,
				this.right, this.top, -Z+ i,
				this.left, this.top, -Z+ i
		};	
		updateVertexBuffer();
		this.orignalVertices = vertices.clone();
	}
	
	public void setVertices(float[] vertices) {
		this.vertices = vertices;
		updateBuffers();
	}
	
	public float[]  getVertices() {
		return this.vertices;
	}
	public void setTexture(Activity activity, int mainActivityBackground) {
		InputStream is = activity.getResources().openRawResource(mainActivityBackground);
		texture = BitmapFactory.decodeStream(is);
		textureName = 0;
	}

}
