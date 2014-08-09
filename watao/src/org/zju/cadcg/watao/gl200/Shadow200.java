package org.zju.cadcg.watao.gl200;

import org.zju.cadcg.watao.gl.Shadow;
import org.zju.cadcg.watao.shader.ShadowShader;
import org.zju.cadcg.watao.utils.ShaderUtil;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Shadow200 extends Shadow {

	public Shadow200(Context context, String fileName) {
		super(context, fileName);
	}
	
	@Override
	public void draw() {
		shader.useProgram();
		ShaderUtil.setTexParameter(GLES20.GL_TEXTURE0);
		shader.setVertexAttributeOffset(vertexBufferOffset, normalBufferOffset, texCoordBufferOffset);
		if (textureName == 0) {
			int[] temp = new int[1];
			GLES20.glGenTextures(1, temp, 0);
			textureName = temp[0];
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture, 0);
			texture.recycle();
			System.gc();
			shader.setTexture(0);
		}else{
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);
			shader.setTexture(0);
		}
		shader.reloadModelMatrix();
		shader.translate(0f, -1.9f, -6.4f);
		shader.rotate(angleForSensor, 1.0f, 0.0f, 0.0f);
		shader.rotate(135, 0, 1, 0);
		shader.scale(1.2f, 1.0f, 0.95f);
		shader.translate(0f, 0f, -0.5f);
        shader.drawVBO(indiceBuffer.capacity(),indiceBufferOffset);
	}
	
	private ShadowShader shader;
	public void createShader(float offset, Resources res) {
		shader = new ShadowShader("vertex_shadow.glsl", "frag_shadow.glsl", res, offset);
	}

	public void setOffset(float d) {
		if (shader != null) {
			shader.setLookAt(d);
		}
	}
}
