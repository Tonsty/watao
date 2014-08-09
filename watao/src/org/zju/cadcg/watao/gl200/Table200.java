package org.zju.cadcg.watao.gl200;

import org.zju.cadcg.watao.gl.Table;
import org.zju.cadcg.watao.shader.TableFireShader;
import org.zju.cadcg.watao.shader.TableShader;
import org.zju.cadcg.watao.utils.ShaderUtil;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Table200 extends Table {

	private TableShader	shader;
	private TableFireShader	fireShader;
	
	public static final int FIRE = 1 ;
	public static final int COMMON = 0;
	private int currentShader = COMMON;
	
	public Table200(Context context, String fileName){
		super(context, fileName);
	}
	
	public void createShader(float offset) {
		shader = new TableShader("vertex_table.glsl", "frag_table.glsl", this.resources, offset);
		fireShader = new TableFireShader("vertex_table.glsl", "frag_table.glsl", this.resources, offset);
	}
	
	@Override
	public void draw() {
		
		if (currentShader == COMMON) {
			drawWithShader(shader);
		}else{
			drawWithShader(fireShader);
		}
		super.draw();
	}
	
	
	public void drawWithShader(TableShader shader) {
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
		shader.rotate(angleForRotate, 0f, 1f, 0f);
		shader.scale(0.7f, 0.4f, 0.7f);
        shader.drawVBO(indiceBuffer.capacity(),indiceBufferOffset);
	}

	public void setOffset(float d) {
		if (shader != null) {
			shader.setLookAt(d);
		}
	}
	
	public void switchShader(int shader){
		currentShader = shader;
	}
	
	
	public void setLum(float f){
		fireShader.setLum(f);
	}
	
}