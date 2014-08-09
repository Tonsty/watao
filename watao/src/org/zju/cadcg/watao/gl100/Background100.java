package org.zju.cadcg.watao.gl100;

import javax.microedition.khronos.opengles.GL10;

import org.zju.cadcg.watao.gl.Background;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

public class Background100 extends Background{


	
	public Background100(int bitmap, Resources resources) {
//		super(bitmap, resources);
	}

	public void draw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordBuffer);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
		if(textureName == 0){
			int[] temp = new int[1];
			gl.glGenTextures(1, temp, 0);
			textureName = temp[0];
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName);
//			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture, 0);
		}else{
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName);
		}
		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indiceBuffer);
	}
	
}
