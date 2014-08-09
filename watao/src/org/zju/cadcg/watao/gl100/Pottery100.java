package org.zju.cadcg.watao.gl100;

import javax.microedition.khronos.opengles.GL10;

import org.zju.cadcg.watao.gl.Pottery;
import org.zju.cadcg.watao.utils.PotteryTextureManager;

import android.graphics.Bitmap;

public class Pottery100 extends Pottery{

	public Pottery100() {
		super();
	}
	
	public void draw(GL10 gl){
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordBuffer);
		gl.glEnable(GL10.GL_TEXTURE_2D);
//		PotteryTextureManager.loadTexture(gl);
		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indiceBuffer);
	}
	
}
