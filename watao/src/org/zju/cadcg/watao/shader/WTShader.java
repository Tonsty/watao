package org.zju.cadcg.watao.shader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.zju.cadcg.watao.gl.GLMeshObject;
import org.zju.cadcg.watao.utils.ShaderUtil;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class WTShader {

	//matrix
	protected float[] modelMatrix = new float[16];
	protected float[] viewMatrix = new float[16];
	protected static float[] perspctiveMatrix = new float[16];
	
	
	//shader object and program
	protected String mVertexShader;
	protected String mFragmentShader;
	protected int program;	
	
	//vertex parameter handle
	protected int uMVPMatrixHandle;
	protected int aPositionHandle;
	protected int aNormalHandle = -1;
	protected int aTexCoordHandle;
	
	//frag parameter handle
	protected int uTextureHandle;
	
	public static void initForAllShader(){
		// Set up any OpenGL options we need
		//TODO
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
	}

	public WTShader(String vertexShader, String fragShader, Resources resources){
		//加载顶点着色器的脚本内容
		mVertexShader=ShaderUtil.loadFromAssetsFile(vertexShader, resources);
		if (mVertexShader == null) {
			System.err.println("create shade failed!");
		}
		//加载片元着色器的脚本内容
		mFragmentShader=ShaderUtil.loadFromAssetsFile(fragShader, resources);  
		if (mFragmentShader == null) {
			System.err.println("create shade failed!");
		}
		//基于顶点着色器与片元着色器创建程序
		program = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
		if (program == 0) {
			System.err.println("create shade failed!");
		}
		
		Matrix.setIdentityM(modelMatrix, 0);
	}
	
	protected void checkParameterHandle(int handle, String tag){
		if (handle < 0) {
			System.err.println("no such variable: " + tag);
		}
		return;
	}

	public void setTexture(int i) {
		GLES20.glUniform1i(uTextureHandle, i);
		return;
	}

	public static void frustumM(float left, float right, float bottom, float top,
			float near, float far) {
		WTShader.frustum(perspctiveMatrix, left, right, bottom, top, near, far);
		return;
	}
	
	public static void frustum(float[] m, float left, float right, float bottom, float top,
			float near, float far){
		if (left == right) {
			throw new IllegalArgumentException("left == right");
		}
		if (top == bottom) {
			throw new IllegalArgumentException("top == bottom");
		}
		if (near == far) {
			throw new IllegalArgumentException("near == far");
		}
		if (near <= 0.0f) {
			throw new IllegalArgumentException("near <= 0.0f");
		}
		if (far <= 0.0f) {
			throw new IllegalArgumentException("far <= 0.0f");
		}
		final float r_width  = 1.0f / (right - left);
		final float r_height = 1.0f / (top - bottom);
		final float r_depth  = 1.0f / (near - far);
		final float x = 2.0f * (near * r_width);
		final float y = 2.0f * (near * r_height);
		final float A = (right + left) * r_width;
		final float B = (top + bottom) * r_height;
		final float C = (far + near) * r_depth;
		final float D = 2.0f * (far * near * r_depth);
		m[0] = x;
		m[5] = y;
		m[8] = A;
		m[ 9] = B;
		m[10] = C;
		m[14] = D;
		m[11] = -1.0f;
		m[ 1] = 0.0f;
		m[ 2] = 0.0f;
		m[ 3] = 0.0f;
		m[ 4] = 0.0f;
		m[ 6] = 0.0f;
		m[ 7] = 0.0f;
		m[12] = 0.0f;
		m[13] = 0.0f;
		m[15] = 0.0f;	
	}

	protected static FloatBuffer attributeBuffer;
	protected static ShortBuffer indiceBuffer;
	protected static List<GLMeshObject> glMeshObjects = new ArrayList<GLMeshObject>();
	protected static int[] VBOId = new int[2];
	
	public static void addGLMeshObject(GLMeshObject meshObject){
		glMeshObjects.add(meshObject);
	}

	static public void initVBO(){
		int attributeBufferLength = 0;
		int indiceBufferLength = 0;
		for (GLMeshObject object : glMeshObjects) {
			attributeBufferLength += object.getAttributeBufferLength();
			indiceBufferLength += object.getIndiceBufferLength();
		}
		attributeBuffer = ByteBuffer.allocateDirect(attributeBufferLength * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		for (GLMeshObject object : glMeshObjects) {
			object.vertexBufferOffset = attributeBuffer.position() * 4;
			attributeBuffer.put(object.vertexBuffer);
			object.vertexBuffer.position(0);
			
			object.normalBufferOffset = attributeBuffer.position() * 4;
			attributeBuffer.put(object.normalBuffer);
			object.normalBuffer.position(0);
			
			object.texCoordBufferOffset = attributeBuffer.position() * 4;
			attributeBuffer.put(object.texCoordBuffer);
			object.texCoordBuffer.position(0);
		}
		attributeBuffer.position(0);
		indiceBuffer = ByteBuffer.allocateDirect(indiceBufferLength * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
		for (GLMeshObject object : glMeshObjects) {
			object.indiceBufferOffset = indiceBuffer.position() * 2;
			indiceBuffer.put(object.indiceBuffer);
			object.indiceBuffer.position(0);
		}
		indiceBuffer.position(0);
		GLES20.glGenBuffers(2, VBOId, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, VBOId[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, attributeBufferLength * 4, attributeBuffer, GLES20.GL_DYNAMIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, VBOId[1]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indiceBufferLength * 2, indiceBuffer, GLES20.GL_STATIC_DRAW);
	}

	public void setVertexAttributeOffset(int vertexBufferOffset, int normalBufferOffset, int texCoordBufferOffset) {
		GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBufferOffset);
		if(aNormalHandle >= 0){
			GLES20.glVertexAttribPointer(aNormalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBufferOffset);
		}
        GLES20.glVertexAttribPointer(aTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBufferOffset);
        return;
	}
	
	protected static void updateVBO(){
		for (GLMeshObject object : glMeshObjects) {
			if(object.isVertexBufferDirty){
				synchronized (object.vertexBuffer) {
					GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, object.vertexBufferOffset, object.vertexBuffer.capacity() * 4, object.vertexBuffer);
					object.isVertexBufferDirty = false;
				}
			}
			
			if(object.isNormalBufferDirty){
				synchronized (object.normalBuffer) {
					GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, object.normalBufferOffset, object.normalBuffer.capacity() * 4, object.normalBuffer);
					object.isNormalBufferDirty = false;
				}
			}
			
			if(object.isTexCoordBufferDirty){
				synchronized (object.texCoordBuffer) {
					GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, object.texCoordBufferOffset, object.texCoordBuffer.capacity() * 4, object.texCoordBuffer);
					object.isTexCoordBufferDirty = false;
				}
			}
		}
	}
	
	public void drawVBO(int indiceNum, int offset) {
		updateVBO();
		float[] multiplyMMResult = new float[16];
		Matrix.multiplyMM(multiplyMMResult, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(multiplyMMResult, 0, perspctiveMatrix, 0, multiplyMMResult, 0);
		GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, multiplyMMResult, 0);
		
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indiceNum, GLES20.GL_UNSIGNED_SHORT, offset);
		return;
	}

	public void useProgram() {
		GLES20.glUseProgram(program);
	}
	public void setLookAt(float d) {
		//Position the eye behind the origin.
		final float eyeY = 0.0f;

		// We are looking toward the distance
		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = -1.0f;

		// Set our up vector. This is where our head would be pointing were we holding the camera.
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;

		Matrix.setLookAtM(viewMatrix, 0, genOffsetX(d), eyeY, genOffsetZ(d), lookX, lookY, lookZ, upX, upY, upZ);
	}

	static public float genOffsetX(float input){
		return input * 0.5f;
	}
	static public float genOffsetZ(float input){
		return input * -1.1f;
	}
	
	

}
