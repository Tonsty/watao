package org.zju.cadcg.watao.gl200;


import java.io.IOException;
import java.io.InputStream;

import org.zju.cadcg.watao.R;
import org.zju.cadcg.watao.gl.Pottery;
import org.zju.cadcg.watao.shader.PotteryShader;
import org.zju.cadcg.watao.shader.PotteryShaderCi;
import org.zju.cadcg.watao.shader.PotteryShaderClay;
import org.zju.cadcg.watao.shader.PotteryShaderDryClay;
import org.zju.cadcg.watao.shader.PotteryShaderFire;
import org.zju.cadcg.watao.utils.ShaderUtil;
import org.zju.cadcg.watao.utils.PotteryTextureManager;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class Pottery200 extends Pottery{
	public static final int CLAY = 0;
	public static final int DRY_CLAY = 1;
	public static final int CI = 2;
	public static final int FIRE = 3;
	
	private int shaderType;
	private PotteryShader clayShader;
	private PotteryShader ciShader;
	private PotteryShader dryClayShader;
	private PotteryShader fireShader;

	public Pottery200() {
		super();
	}
	
	public void createShader(float offset, Resources resources){
		int[] cubeMapResourceIds = new int[] {
				R.drawable.light, R.drawable.light, R.drawable.light,
                    R.drawable.light, R.drawable.light, R.drawable.light
                   };
    	GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		generateCubeMap(cubeMapResourceIds, resources); 
		
		ciShader = new PotteryShaderCi("vertex_pottery.glsl", "frag_pottery_ci.glsl", resources, offset);
		ciShader.useProgram();
		ciShader.setTextureCube(1);
		
		clayShader = new PotteryShaderClay("vertex_pottery.glsl", "frag_pottery.glsl", resources, offset);
		clayShader.useProgram();
		clayShader.setTextureCube(1);
		
		dryClayShader = new PotteryShaderDryClay("vertex_pottery.glsl", "frag_pottery_dry_clay.glsl", resources, offset);
		dryClayShader.useProgram();
		dryClayShader.setTextureCube(1);
		
		fireShader = new PotteryShaderFire("vertex_pottery.glsl", "frag_pottery_dry_clay.glsl", resources, offset);
		fireShader.useProgram();
		fireShader.setTextureCube(1);
	}
	
	@Override
	public void draw() {
		switch (shaderType) {
		case CLAY:
			drawWithShader(clayShader);
			break;

		case DRY_CLAY:
			drawWithShader(dryClayShader);
			break;

		case CI:
			drawWithShader(ciShader);
			break;

		case FIRE:
			drawWithShader(fireShader);
			break;

		default:
			break;
		}
	}

	private void drawWithShader(PotteryShader shader) {
		shader.useProgram();
		ShaderUtil.setTexParameter(GLES20.GL_TEXTURE0);
		shader.setVertexAttributeOffset(vertexBufferOffset, normalBufferOffset, texCoordBufferOffset);
		
		shader.reloadModelMatrix();
		shader.translate(0f, -1.9f, -6.4f);
		shader.rotate(angleForSensor, 1.0f, 0.0f, 0.0f);
		shader.rotate(angleForRotate, 0f, 1f, 0f);
        PotteryTextureManager.loadTexture();
        shader.drawVBO(indiceBuffer.capacity(), indiceBufferOffset);
	}
	
	public void setOffset(float ratio) {
		if (dryClayShader != null) {
			clayShader.setLookAt(ratio);
			ciShader.setLookAt(ratio);
			dryClayShader.setLookAt(ratio);
		}
	}
	
	//加载立方图纹理
    public int generateCubeMap(int[] resourceIds, Resources resources) {
        int[] ids = new int[1];
        GLES20.glGenTextures(1, ids, 0);
        int cubeMapTextureId = ids[0];
        
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, cubeMapTextureId);     
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_CUBE_MAP,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_CUBE_MAP,GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);

        for (int face = 0; face < 6; face++) 
        {
            InputStream is = resources.openRawResource(resourceIds[face]);
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(is);
            } finally {
                try {
                    is.close();
                } catch(IOException e) {
                    Log.e("CubeMap", "Could not decode texture for face " + Integer.toString(face));
                }
            }
            GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + face, 0,bitmap, 0);
            bitmap.recycle();
        }
        return cubeMapTextureId;
    }

	public void switchShader(int i) {
		shaderType = i;
	}
	
	public void setLum(float radio){
		fireShader.setLum(radio);
	}
}

