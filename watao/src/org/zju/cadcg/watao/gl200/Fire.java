package org.zju.cadcg.watao.gl200;

import java.io.InputStream;

import org.zju.cadcg.watao.gl.Background;
import org.zju.cadcg.watao.shader.BackgroundShader;
import org.zju.cadcg.watao.shader.WTShader;
import org.zju.cadcg.watao.utils.ShaderUtil;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Fire extends Background{
	
	public Fire() {
		super();
		isNormalBufferDirty = false;
	}

	private WTShader shader;
	private boolean needReload = false;
	
	public void createShader(Resources res) {
		shader = new BackgroundShader("vertex_background.glsl", "frag_background.glsl", res);
	}
	
	@Override
	public void draw() {
		shader.useProgram();
		ShaderUtil.setTexParameter(GLES20.GL_TEXTURE0);
		shader.setVertexAttributeOffset(vertexBufferOffset, normalBufferOffset, texCoordBufferOffset);
		if(textureName == 0){
			int[] temp = new int[1];
			GLES20.glGenTextures(1, temp, 0);
			textureName = temp[0];
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture, 0);
			System.gc();
		}else if(needReload){
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture, 0);
			needReload = false;
		}else{
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);
		}
	    shader.setTexture(0);
        shader.drawVBO(indiceBuffer.capacity(),indiceBufferOffset);
	}
	public void genPosition(float left, float right, float bottom,
			float top, float near, float far) {
		this.top = top * 3 / near;
		this.bottom = bottom * 3 / near;
		this.left = left * 3 / near;
		this.right = right * 3 / near;
		vertices = new float[]{
				this.left, this.bottom, -3,
				this.right, this.bottom, -3,
				this.right, this.top, -3,
				this.left, this.top, -3
		};	
		updateVertexBuffer();
		this.orignalVertices = vertices.clone();
	}
	
	
	Options op = new Options();
	public void changeTexture(Activity activity, int mainActivityBackground) {
		InputStream is = activity.getResources().openRawResource(mainActivityBackground);
		op.inBitmap = texture;
		op.inMutable = true;
		op.inSampleSize = 1;
		texture = BitmapFactory.decodeStream(is,null,op);
		needReload = true;
	}
	
	@Override
	public void setTexture(Activity activity, int mainActivityBackground) {
		InputStream is = activity.getResources().openRawResource(mainActivityBackground);
		Options op = new Options();
		op.inMutable = true;
		texture = BitmapFactory.decodeStream(is, null, op);
		textureName = 0;
	}
	
}
