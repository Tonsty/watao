package org.zju.cadcg.watao.shader;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class CopyOfPotteryShader extends WTShader{
	
	//light parameter
	private static final float[] lightPosition = new float[]{-20f, 10.0f, 20f, 1.0f};
	private static final float[] ambientL = new float[]{0.3f, 0.3f, 0.3f, 1.0f};
	private static final float[] ambientLForQInghua = new float[]{0.6f, 0.6f, 0.6f, 1.0f};
	private static final float[] diffuseL = new float[]{0.6f, 0.6f, 0.6f, 1.0f};
	private static final float[] specularL = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
	private static final float[] specularLForQinghua = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
	
	private static final float[] lightPosition2 = new float[]{20.0f, 5.0f, -9.0f, 1.0f};
	private static final float[] ambientL2 = new float[]{0.3f, 0.3f, 0.3f, 1.0f};
	private static final float[] diffuseL2 = new float[]{0.75f, 0.9f, 0.75f, 1.0f};
	private static final float[] specularL2 = new float[]{0.0f, 0.0f, 0.0f, 1.0f};

	//material parameter
	private static final float[] ambientM = new float[]{0.6f, 0.6f, 0.6f, 1.0f};
	private static final float[] diffuseM = new float[]{0.8f, 0.8f, 0.6f, 1.0f};
	private static final float[] specularM = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
	private static final float shininess = 32.0f;

	//vertex parameter handle
	private int uMVMatrixHandle;
	
	//frag parameter handle
	private int uAmbientLHandle;
	private int uDiffuseLHandle;
	private int uSpecularLHandle;
	private int uLightPositionHandle;
	private int uAmbientL2Handle;
	private int uDiffuseL2Handle;
	private int uSpecularL2Handle;
	private int uLightPosition2Handle;
	
	private int uAmbientMHandle;
	private int uDiffuseMHandle;
	private int uSpecularMHandle;
	private int uShininessHandle;
	private int uLookAtHandle;
	private int uTextureCubeHandle;
	
	

	public CopyOfPotteryShader(String vertexShader, String fragShader, Resources resources, float offset, boolean isQinghua) {
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
		
		this.uAmbientLHandle = GLES20.glGetUniformLocation(program, "uAmbientL");
		checkParameterHandle(uAmbientLHandle, "uAmbientL");
		GLES20.glUniform4fv(uAmbientLHandle, 1, ambientL, 0);
		
		this.uDiffuseLHandle = GLES20.glGetUniformLocation(program, "uDiffuseL");
		checkParameterHandle(uDiffuseLHandle, "uDiffuseL");
		GLES20.glUniform4fv(uDiffuseLHandle, 1, diffuseL, 0);
		
		this.uSpecularLHandle = GLES20.glGetUniformLocation(program, "uSpecularL");
		this.uAmbientLHandle = GLES20.glGetUniformLocation(program, "uAmbientL");
		checkParameterHandle(uAmbientLHandle, "uAmbientL");
		if (isQinghua) {
			GLES20.glUniform4fv(uAmbientLHandle, 1, ambientLForQInghua, 0);
		}else{
			GLES20.glUniform4fv(uAmbientLHandle, 1, ambientL, 0);
		}
		
		this.uDiffuseLHandle = GLES20.glGetUniformLocation(program, "uDiffuseL");
		checkParameterHandle(uDiffuseLHandle, "uDiffuseL");
		GLES20.glUniform4fv(uDiffuseLHandle, 1, diffuseL, 0);
		
		this.uSpecularLHandle = GLES20.glGetUniformLocation(program, "uSpecularL");
		checkParameterHandle(uSpecularLHandle, "uSpecularL");
		if (isQinghua) {
			GLES20.glUniform4fv(uSpecularLHandle, 1, specularLForQinghua, 0);
		}else{
			GLES20.glUniform4fv(uSpecularLHandle, 1, specularL, 0);
		}
	
		this.uLightPositionHandle = GLES20.glGetUniformLocation(program, "uLightPosition");
		checkParameterHandle(uLightPositionHandle, "uLightPosition");
		GLES20.glUniform4fv(uLightPositionHandle, 1, lightPosition, 0);
		
		this.uAmbientL2Handle = GLES20.glGetUniformLocation(program, "uAmbientL2");
		checkParameterHandle(uAmbientL2Handle, "uAmbientL2");
		GLES20.glUniform4fv(uAmbientL2Handle, 1, ambientL2, 0);
		
		this.uDiffuseL2Handle = GLES20.glGetUniformLocation(program, "uDiffuseL2");
		checkParameterHandle(uDiffuseL2Handle, "uDiffuseL2");
		GLES20.glUniform4fv(uDiffuseL2Handle, 1, diffuseL2, 0);
		
		this.uSpecularL2Handle = GLES20.glGetUniformLocation(program, "uSpecularL2");
		checkParameterHandle(uSpecularL2Handle, "uSpecularL2");
		GLES20.glUniform4fv(uSpecularL2Handle, 1, specularL2, 0);
	
		this.uLightPosition2Handle = GLES20.glGetUniformLocation(program, "uLightPosition2");
		checkParameterHandle(uLightPosition2Handle, "uLightPosition2");
		GLES20.glUniform4fv(uLightPosition2Handle, 1, lightPosition2, 0);
		
		this.uAmbientMHandle = GLES20.glGetUniformLocation(program, "uAmbientM");
		checkParameterHandle(uAmbientMHandle, "uAmbientM");
		GLES20.glUniform4fv(uAmbientMHandle, 1, ambientM, 0);
		
		this.uDiffuseMHandle = GLES20.glGetUniformLocation(program, "uDiffuseM");
		checkParameterHandle(uDiffuseMHandle, "uDiffuseM");
		GLES20.glUniform4fv(uDiffuseMHandle, 1, diffuseM, 0);
		
		this.uSpecularMHandle = GLES20.glGetUniformLocation(program, "uSpecularM");
		checkParameterHandle(uSpecularMHandle, "uSpecularM");
		GLES20.glUniform4fv(uSpecularMHandle, 1, specularM, 0);
		
		this.uShininessHandle = GLES20.glGetUniformLocation(program, "uShininess");
		checkParameterHandle(uShininessHandle, "uShininess");
		GLES20.glUniform1f(uShininessHandle, shininess);
		
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
		float[] multiplyMMResult = new float[16];
		Matrix.multiplyMM(multiplyMMResult, 0, viewMatrix, 0, modelMatrix, 0);
		GLES20.glUniformMatrix4fv(uMVMatrixHandle, 1, false, multiplyMMResult, 0);

		Matrix.multiplyMM(multiplyMMResult, 0, perspctiveMatrix, 0, multiplyMMResult, 0);
		GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, multiplyMMResult, 0);

		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indiceNum, GLES20.GL_UNSIGNED_SHORT, offset);
		return;
	}

	public void reloadModelMatrix() {
		Matrix.setIdentityM(modelMatrix, 0);
	}



	public void setTextureCube(int i) {
		GLES20.glUniform1i(uTextureCubeHandle, i);
		return;
	}
	
}
