package org.zju.cadcg.watao.gl;

import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import org.zju.cadcg.watao.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Table extends GLMeshObject {
	
	protected Bitmap texture;
	protected int textureName = 0;
	protected float angleForRotate = 0.0f;
	protected float angleForSensor = 0.0f;
	protected Resources resources;

	public Table(Context context, String fileName){
		this.resources = context.getResources();
		LoadObj(context, fileName);
		updateBuffers();
	}

	public void draw(GL10 gl) {
		
	}

	public void draw() {
		
	}

	public void setAngleForRotate(float rotateAngle) {
		this.angleForRotate = rotateAngle;
	}

	public void setAngleRotateY(float potteryCurrentAngleRotateX) {
		this.angleForSensor = potteryCurrentAngleRotateX;
	}

	public void setTexture(Context context, int id) {
		InputStream is = context.getResources().openRawResource(id);
		this.texture = BitmapFactory.decodeStream(is);
		this.textureName = 0;
	}
}
