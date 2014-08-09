package org.zju.cadcg.watao.gl200;

import org.zju.cadcg.watao.gl.Background;
import org.zju.cadcg.watao.shader.BackgroundShader;
import org.zju.cadcg.watao.shader.WTShader;
import org.zju.cadcg.watao.utils.ShaderUtil;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Background200 extends Background{
	
	public Background200() {
		super();
		isNormalBufferDirty = false;
	}

	private BackgroundShader shader;
	
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
			texture.recycle();
			texture = null;
			System.gc();
		}else{
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);
		}
	    shader.setTexture(0);
        shader.drawVBO(indiceBuffer.capacity(),indiceBufferOffset);
	}

	public void setLum(float f) {
		shader.setLum(f);
	}
}
