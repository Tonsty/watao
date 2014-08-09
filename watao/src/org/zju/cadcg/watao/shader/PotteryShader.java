package org.zju.cadcg.watao.shader;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class PotteryShader extends WTShader{
	
	
	//vertex parameter handle
	int uMVMatrixHandle;
	
	//frag parameter handle
	int uAmbientLHandle;
	int uDiffuseLHandle;
	int uSpecularLHandle;
	int uLightPositionHandle;
	int uAmbientL2Handle;
	int uDiffuseL2Handle;
	int uSpecularL2Handle;
	int uLightPosition2Handle;
	
	int uAmbientMHandle;
	int uDiffuseMHandle;
	int uSpecularMHandle;
	int uShininessHandle;
	int uLookAtHandle;
	int uTextureCubeHandle;
	
	

	public PotteryShader(String vertexShader, String fragShader, Resources resources, float offset) {
		super(vertexShader, fragShader, resources);
		
		useProgram();
		//get the location of variable in vertex shader
		this.aPositionHandle = GLES20.glGetAttribLocation(program, "aPosition");
		checkParameterHandle(aPositionHandle, "aPosition");
		this.aTexCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord");
		checkParameterHandle(aTexCoordHandle, "aTexCoord");
		this.aNormalHandle = GLES20.glGetAttribLocation(program, "aNormal");
		checkParameterHandle(aNormalHandle, "aNormal");
		GLES20.glEnableVertexAttribArray(aPositionHandle);
		GLES20.glEnableVertexAttribArray(aNormalHandle);
		GLES20.glEnableVertexAttribArray(aTexCoordHandle);
		
        this.uMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        checkParameterHandle(uMVPMatrixHandle, "uMVPMatrix");
        this.uMVMatrixHandle = GLES20.glGetUniformLocation(program, "uMVMatrix");
        checkParameterHandle(uMVMatrixHandle, "uMVMatrix");
        
        //get the location of variable in frag shader
		this.uTextureHandle = GLES20.glGetUniformLocation(program, "uTexture");
		checkParameterHandle(uTextureHandle, "uTexture");
		
		this.uTextureCubeHandle = GLES20.glGetUniformLocation(program, "uCube");
		checkParameterHandle(uTextureCubeHandle, "uCube");
		
		this.uLookAtHandle = GLES20.glGetUniformLocation(program, "uLookAt");
		checkParameterHandle(uLookAtHandle, "uLookAt");
		
		//Position the eye behind the origin.
		final float eyeX = genOffsetX(offset) ;
		final float eyeY = 0.0f;
		final float eyeZ = genOffsetZ(offset);

		// We are looking toward the distance
		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = -1.0f;

		// Set our up vector. This is where our head would be pointing were we holding the camera.
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;

		Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
	}
	
	public void setLookAt(float eyeX, float eyeY, float eyeZ, float lookX,
			float lookY, float lookZ, float upX, float upY, float upZ) {
		Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
		GLES20.glUniform4fv(uLookAtHandle, 1, new float[]{eyeX, eyeY, eyeZ, 1.0f}, 0);
		return;
	}

	public void translate(float x, float y, float z) {
		Matrix.translateM(modelMatrix, 0, x, y, z);
		return;
	}

	public void rotate(float angle, float x, float y, float z) {
		Matrix.rotateM(modelMatrix, 0, angle, x, y, z);
		return;
	}
	
	public void scale(float x, float y, float z) {
		Matrix.scaleM(modelMatrix, 0, x, y, z);
		return;
	}

	@Override
	public void drawVBO(int indiceNum, int offset) {
		updateVBO();
		updateM();
		float[] multiplyMMResult = new float[16];
		Matrix.multiplyMM(multiplyMMResult, 0, viewMatrix, 0, modelMatrix, 0);
		GLES20.glUniformMatrix4fv(uMVMatrixHandle, 1, false, multiplyMMResult, 0);

		Matrix.multiplyMM(multiplyMMResult, 0, perspctiveMatrix, 0, multiplyMMResult, 0);
		GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, multiplyMMResult, 0);

		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indiceNum, GLES20.GL_UNSIGNED_SHORT, offset);
		return;
	}

	protected void updateM() {
	
	}

	public void reloadModelMatrix() {
		Matrix.setIdentityM(modelMatrix, 0);
	}



	public void setTextureCube(int i) {
		GLES20.glUniform1i(uTextureCubeHandle, i);
		return;
	}
	
	protected boolean needReloadM = false;



	public void setLum(float radio) {
	}
	
}
