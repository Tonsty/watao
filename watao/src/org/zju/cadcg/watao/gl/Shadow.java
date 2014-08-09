package org.zju.cadcg.watao.gl;


import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Shadow extends GLMeshObject{

	protected Bitmap texture;
	protected int textureName;
	protected float origenalHeight;
	protected float origenalRadius;
	protected float vRate;
	protected float hRate;
	protected float angleForSensor = 0.0f;

	public void setAngleRotateY(float angleForSensor) {
		this.angleForSensor = angleForSensor;
	}

	public Shadow(Context context, String fileName){
		vRate = 1;
		hRate = 1;
		LoadObj(context, fileName);
		updateBuffers();
	}

	public void HorizonalChange(float[] bases, float height) {
		float total = 0;
		for (float f : bases) {
			total += f;
		}
		hRate = total/bases.length/origenalRadius;
		hRate = hRate + 3 * (hRate - 1);
	}

	public void verticalChange(float height) {
		vRate = (height / origenalHeight - 0.5f)/2.5f*0.3f + 0.6f;
	}

	public void draw(GL10 gl) {
	}

	public void draw() {
	}

	public void setTexture(Context context, int id) {
		InputStream is = context.getResources().openRawResource(id);
		this.texture = BitmapFactory.decodeStream(is);
		textureName = 0;
	}

}
