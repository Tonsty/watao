package org.zju.cadcg.watao.gl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Context;

public class GLMeshObject {
	protected float[] normals;
	protected float[] texCoords;
	protected float[] vertices;
	public float[] getVertices() {
		return vertices;
	}



	protected short[] indices;
	
	public FloatBuffer vertexBuffer;
	public int vertexBufferOffset;
	public boolean isVertexBufferDirty = true;
	
	public FloatBuffer normalBuffer;
	public int normalBufferOffset;
	public boolean isNormalBufferDirty = true;
	
	public FloatBuffer texCoordBuffer;
	public int texCoordBufferOffset;
	public boolean isTexCoordBufferDirty = true;
	
	public ShortBuffer indiceBuffer;
	public int indiceBufferOffset;
	public boolean isIndiceBufferdDirty = true;

	public void updateVertexBuffer(){
		if (vertices != null){
			if (vertexBuffer == null) {
				vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
			}
			synchronized (vertexBuffer) {
				vertexBuffer.put(vertices);
				isVertexBufferDirty = true;
				vertexBuffer.position(0);
			}
		}			
	}

	public void updateNormalBuffer(){
		if (normalBuffer == null) {
			normalBuffer = ByteBuffer.allocateDirect(normals.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		}
		synchronized (normalBuffer) {
			normalBuffer.put(normals);
			isNormalBufferDirty = true;
			normalBuffer.position(0);
		}
	}
	
	private void updateTextureBuffer(){
		if (texCoordBuffer == null) {
			texCoordBuffer = ByteBuffer.allocateDirect(texCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		}
		synchronized (texCoordBuffer) {
			texCoordBuffer.put(texCoords);
			isTexCoordBufferDirty = true;
			texCoordBuffer.position(0);
		}
	}
	
	private void updateIndiceBuffer(){
		if (indiceBuffer == null) {
			indiceBuffer = ByteBuffer.allocateDirect(indices.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
		}
//		synchronized (indiceBuffer) {
			indiceBuffer.position(0);
			indiceBuffer.put(indices);
			indiceBuffer.position(0);
			isIndiceBufferdDirty = true;
//		}
	}
	public void updateBuffers(){
		updateVertexBuffer();
		updateNormalBuffer();
		updateTextureBuffer();
		updateIndiceBuffer();
	}

	
	public void LoadObj(Context context, String fileName){
		InputStream input;
		BufferedReader reader;
		try{
			ArrayList<String> verticeLines = new ArrayList<String>();
			ArrayList<String> textureLines = new ArrayList<String>();
			ArrayList<String> normalLines = new ArrayList<String>();
			ArrayList<String> verticeIndiceLines = new ArrayList<String>();
			ArrayList<String> textureIndiceLines = new ArrayList<String>();
			ArrayList<String> normalIndiceLines = new ArrayList<String>();
			input = context.getResources().getAssets().open(fileName);
			reader = new BufferedReader(new InputStreamReader(input));
			String line = null;
			while((line = reader.readLine()) != null){
				if(line.startsWith("//") || line.startsWith("#") || line.trim().equals(" ") || line.equals("")){
					continue;
				}
				String SPACE = " ";
				String SLASH = "/";
				StringTokenizer st = new StringTokenizer(line, SPACE);
				String lineType = st.nextToken();
				switch (lineType) {
				case "v":
					verticeLines.add(st.nextToken());
					verticeLines.add(st.nextToken());
					verticeLines.add(st.nextToken());
					break;
				case "vt":
					textureLines.add(st.nextToken());
					textureLines.add(st.nextToken());
					break;
				case "vn":
					normalLines.add(st.nextToken());
					normalLines.add(st.nextToken());
					normalLines.add(st.nextToken());
					break;
				case "f":
					String v1 = st.nextToken();
					String v2 = st.nextToken();
					String v3 = st.nextToken();

					StringTokenizer st1 = new StringTokenizer(v1, SLASH);
					StringTokenizer st2 = new StringTokenizer(v2, SLASH);
					StringTokenizer st3 = new StringTokenizer(v3, SLASH);

					verticeIndiceLines.add(st1.nextToken());
					verticeIndiceLines.add(st2.nextToken());
					verticeIndiceLines.add(st3.nextToken());

					textureIndiceLines.add(st1.nextToken());
					textureIndiceLines.add(st2.nextToken());
					textureIndiceLines.add(st3.nextToken());

					normalIndiceLines.add(st1.nextToken());
					normalIndiceLines.add(st2.nextToken());
					normalIndiceLines.add(st3.nextToken());
					break;
				default:
					System.out.println("format error");
					break;
				}
			}
			
			int indicesSize = verticeIndiceLines.size();
			indices = new short[indicesSize];
			int verticeSize = indicesSize * 3;
			vertices = new float[verticeSize];
			int textureSize = indicesSize * 2;
			texCoords = new float[textureSize];
			int normalSize = indicesSize * 3;
			normals = new float[normalSize];
			
			for(int i = 0; i < verticeIndiceLines.size(); i++){
				indices[i] = (short)i;
				
				int indice = Integer.valueOf(verticeIndiceLines.get(i)) - 1;
				vertices[i * 3] = Float.valueOf(verticeLines.get(indice*3));
				vertices[i * 3 + 1] = Float.valueOf(verticeLines.get(indice*3 + 1));
				vertices[i * 3 + 2] = Float.valueOf(verticeLines.get(indice*3 + 2));
				
				int textureIndice = Integer.valueOf(textureIndiceLines.get(i))-1;
				texCoords[i * 2] = Float.valueOf(textureLines.get(textureIndice*2));
				texCoords[i * 2 + 1] = Float.valueOf(textureLines.get(textureIndice*2 + 1));
				
				int normalIndice = Integer.valueOf(normalIndiceLines.get(i))-1;
				normals[i * 3] = Float.valueOf(normalLines.get(normalIndice*3));
				normals[i * 3 + 1] = Float.valueOf(normalLines.get(normalIndice*3 + 1));
				normals[i * 3 + 2] = Float.valueOf(normalLines.get(normalIndice*3 + 2));
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void estimateNormals(){
		for (int i = 0; i < normals.length; i++) {
			normals[i] = 0;
		}
		
		for (int i = 0; i < indices.length/6; i++) {
			short a = indices[6*i+0], b = indices[6*i+1], c = indices[6*i+2];
			short d = indices[6*i+3], e = indices[6*i+4], f = indices[6*i+5];
			float abVector[] = new float[3], acVector[] = new float[3], normal[] = new float[3];
			abVector[0] = vertices[3*b+0] - vertices[3*a+0];
			abVector[1] = vertices[3*b+1] - vertices[3*a+1];
			abVector[2] = vertices[3*b+2] - vertices[3*a+2];	
			acVector[0] = vertices[3*c+0] - vertices[3*a+0];
			acVector[1] = vertices[3*c+1] - vertices[3*a+1];
			acVector[2] = vertices[3*c+2] - vertices[3*a+2];
			
			normal[0] = abVector[1] * acVector[2] - abVector[2] * acVector[1];
			normal[1] = abVector[2] * acVector[0] - abVector[0] * acVector[2];
			normal[2] = abVector[0] * acVector[1] - abVector[1] * acVector[0];
			
			normals[a*3+0] += normal[0];
			normals[a*3+1] += normal[1];
			normals[a*3+2] += normal[2];
			normals[b*3+0] += normal[0];
			normals[b*3+1] += normal[1];
			normals[b*3+2] += normal[2];
			normals[c*3+0] += normal[0];
			normals[c*3+1] += normal[1];
			normals[c*3+2] += normal[2];
			
			normals[d*3+0] += normal[0];
			normals[d*3+1] += normal[1];
			normals[d*3+2] += normal[2];
			normals[e*3+0] += normal[0];
			normals[e*3+1] += normal[1];
			normals[e*3+2] += normal[2];
			normals[f*3+0] += normal[0];
			normals[f*3+1] += normal[1];
			normals[f*3+2] += normal[2];

		}
		
		for (int i = 0; i < normals.length/3; i++) {
			float normal_length = (float)Math.sqrt( normals[i*3+0] * normals[i*3+0] + normals[i*3+1] * normals[i*3+1] + normals[i*3+2] * normals[i*3+2] );
			normals[i*3+0] /= normal_length;
			normals[i*3+1] /= normal_length;
			normals[i*3+2] /= normal_length;
		}
	}

	public int getAttributeBufferLength() {
		return vertexBuffer.capacity() + normalBuffer.capacity() + texCoordBuffer.capacity();
	}

	public int getIndiceBufferLength(){
		return indiceBuffer.capacity();
	}
//	private void accumulateNormal(int index, float value){
//		if(index/3%51 == 0){
//			normals[index + 50] += value;
//		}else if(index/3%51 == 50){
//			normals[index - 50] += value;
//		}
//		normals[index] += value;
//	}
	
	
	
}

