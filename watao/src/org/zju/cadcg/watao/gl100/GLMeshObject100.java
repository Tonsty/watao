package org.zju.cadcg.watao.gl100;

import javax.microedition.khronos.opengles.GL10;

import org.zju.cadcg.watao.gl.GLMeshObject;

public class GLMeshObject100 extends GLMeshObject{
	public void draw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordBuffer);
		gl.glEnable(GL10.GL_TEXTURE_2D);
	}	
}

