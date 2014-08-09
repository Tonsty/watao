package org.zju.cadcg.watao.gl200;

public class MeshObject {
	
	protected float[] normals;
	protected float[] texCoords;
	protected float[] vertices;
	protected short[] indices;
//	public class BitmapInGL{
//		public Bitmap bitmap;
//		public int id;
//		public ShortBuffer indices;
//		public boolean isInvalid = true;
//		public BitmapInGL(Bitmap texture) {
//			this.bitmap = texture;
//		}
//		public void deleteTexture(GL10 gl){
//			int[] textures = new int[1];
//			textures[0] = id;
//			gl.glDeleteTextures(1, textures, 0);
//			gl.glDisable(GL10.GL_TEXTURE_2D);
//		}
//
//		public void invalidTexture() {
//			isInvalid = true;
//		}
//
//		public void loadTexture(GL10 gl) {
//			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
//			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
//			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
//			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
//			if(isInvalid){
//				isInvalid = false;
//				int[] textureName = new int[1];
//				gl.glGenTextures(1, textureName, 0);
//				id = textureName[0];
//				gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
//				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
//			}else{
//				gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
//			}
//		}
//	}
//	protected Long[] textureIds;
//	public Long[] getTextureIds() {
//		return textureIds;
//	}
//	protected HashMap<Long, BitmapInGL> textures;

//	public MeshObject() {
//		textures = new HashMap<Long, BitmapInGL>();
//	}

//	public FloatBuffer getNormalBuffer(){
//		FloatBuffer result = ByteBuffer.allocateDirect(normals.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//		result.put(normals);
//		result.position(0);
//		return result;
//	}
//	
//	public FloatBuffer getTexCoordBuffer(){
//		FloatBuffer result = ByteBuffer.allocateDirect(texCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//		result.put(texCoords);
//		result.position(0);
//		return result;
//	}
//	
//	public float[] getTexCoords() {
//		return texCoords;
//	}
//	
//
//	public FloatBuffer getVerticeBuffer(){
//		FloatBuffer result = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//		result.put(vertices);
//		result.position(0);
//		return result;
//	}


//	public void setTexCoords(float[] texCoords) {
//		this.texCoords = texCoords;
//	}
//	
//	public void setTextureIds(Long[] textureIds) {
//		this.textureIds = textureIds;
//	}
		
//	public void updateIndices(){
//		Map<Long, List<Short>> temp = new HashMap<Long, List<Short>>();
//		for(int i = 0; i < textureIds.length; ++i){
//			Long id = textureIds[i];
//			if(!temp.containsKey(id)){
//				temp.put(id, new ArrayList<Short>());
//			}
//			temp.get(id).add((short) (i*3 + 0));
//			temp.get(id).add((short) (i*3 + 1));
//			temp.get(id).add((short) (i*3 + 2));
//		}
//		for (Entry<Long, List<Short>> entry : temp.entrySet()) {
//			Long id = entry.getKey();
//			List<Short> value = entry.getValue();
//			short[] shortArray = new short[value.size()];
//			for (int i = 0; i < shortArray.length; ++i) {
//				shortArray[i] = value.get(i);
//			}
//			ShortBuffer sb = ByteBuffer.allocateDirect(shortArray.length*2).order(ByteOrder.nativeOrder()).asShortBuffer();
//			sb.put(shortArray);
//			sb.position(0);
//			textures.get(id).indices = sb;
//		}
//		return;
//	}
	
//	public void estimateNormals(){
//		for (int i = 0; i < normals.length; i++) {
//			normals[i] = 0;
//		}
//		for (int i = 0; i < indices.length/3; i++) {
//			short a = indices[3*i+0], b = indices[3*i+1], c = indices[3*i+2];
//			float abVector[] = new float[3], acVector[] = new float[3], normal[] = new float[3];
//			abVector[0] = vertices[3*b+0] - vertices[3*a+0];
//			abVector[1] = vertices[3*b+1] - vertices[3*a+1];
//			abVector[2] = vertices[3*b+2] - vertices[3*a+2];	
//			acVector[0] = vertices[3*c+0] - vertices[3*a+0];
//			acVector[1] = vertices[3*c+1] - vertices[3*a+1];
//			acVector[2] = vertices[3*c+2] - vertices[3*a+2];
//			
//			normal[0] = abVector[1] * acVector[2] - abVector[2] * acVector[1];
//			normal[1] = abVector[2] * acVector[0] - abVector[0] * acVector[2];
//			normal[2] = abVector[0] * acVector[1] - abVector[1] * acVector[0];
//			
//			normals[a*3+0] += normal[0];
//			normals[a*3+1] += normal[1];
//			normals[a*3+2] += normal[2];
//			normals[b*3+0] += normal[0];
//			normals[b*3+1] += normal[1];
//			normals[b*3+2] += normal[2];
//			normals[c*3+0] += normal[0];
//			normals[c*3+1] += normal[1];
//			normals[c*3+2] += normal[2];
//		}
//		for (int i = 0; i < normals.length/3; i++) {
//			float normal_length = (float)Math.sqrt( normals[i*3+0] * normals[i*3+0] + normals[i*3+1] * normals[i*3+1] + normals[i*3+2] * normals[i*3+2] );
//			normals[i*3+0] /= normal_length;
//			normals[i*3+1] /= normal_length;
//			normals[i*3+2] /= normal_length;
//		}
//	}
//	
//	public void invertFaces(){
//		for (int i = 0; i < indices.length/3; i++) {
//			short temp = indices[i * 3 + 2];
//			indices[ i * 3 + 2 ] = indices[ i * 3 + 1 ];
//			indices[ i * 3 + 1 ] = temp;
//		}
//	}
	

	
//	public void LoadObj(Context context, String fileName)
//	{
//		InputStream input;
//		BufferedReader reader;
//		try
//		{
//			ArrayList<String> verticeLines = new ArrayList<String>();
//			ArrayList<String> textureLines = new ArrayList<String>();
//			ArrayList<String> normalLines = new ArrayList<String>();
//			ArrayList<String> verticeIndiceLines = new ArrayList<String>();
//			ArrayList<String> textureIndiceLines = new ArrayList<String>();
//			ArrayList<String> normalIndiceLines = new ArrayList<String>();
//			input = context.getResources().getAssets().open(fileName);
//			reader = new BufferedReader(new InputStreamReader(input));
//			String line = null;
//			while((line = reader.readLine()) != null)
//			{
//				if(line.startsWith("//") || line.startsWith("#") || line.trim().equals(" ") || line.equals(""))
//				{
//					continue;
//				}
//				String SPACE = " ";
//				String SLASH = "/";
//				StringTokenizer st = new StringTokenizer(line, SPACE);
//				String lineType = st.nextToken();
//				
//				if(lineType.equals("v"))
//				{
//					verticeLines.add(st.nextToken());
//					verticeLines.add(st.nextToken());
//					verticeLines.add(st.nextToken());
//				}
//				else if(lineType.equals("vt"))
//				{
//					textureLines.add(st.nextToken());
//					textureLines.add(st.nextToken());
//				}
//				else if(lineType.equals("vn"))
//				{
//					normalLines.add(st.nextToken());
//					normalLines.add(st.nextToken());
//					normalLines.add(st.nextToken());
//				}
//				else if(lineType.equals("f"))
//				{
//					String v1 = st.nextToken();
//					String v2 = st.nextToken();
//					String v3 = st.nextToken();
//					
//					StringTokenizer st1 = new StringTokenizer(v1, SLASH);
//					StringTokenizer st2 = new StringTokenizer(v2, SLASH);
//					StringTokenizer st3 = new StringTokenizer(v3, SLASH);
//					
//					verticeIndiceLines.add(st1.nextToken());
//					verticeIndiceLines.add(st2.nextToken());
//					verticeIndiceLines.add(st3.nextToken());
//					
//					textureIndiceLines.add(st1.nextToken());
//					textureIndiceLines.add(st2.nextToken());
//					textureIndiceLines.add(st3.nextToken());
//					
//					normalIndiceLines.add(st1.nextToken());
//					normalIndiceLines.add(st2.nextToken());
//					normalIndiceLines.add(st3.nextToken());
//				}
//			}
//			
//			int indicesSize = verticeIndiceLines.size();
//			indices = new short[indicesSize];
//			int verticeSize = indicesSize * 3;
//			vertices = new float[verticeSize];
//			int textureSize = indicesSize * 2;
//			texCoords = new float[textureSize];
//			int normalSize = indicesSize * 3;
//			normals = new float[normalSize];
//			
//			for(int i = 0; i < verticeIndiceLines.size(); i++)
//			{
//				indices[i] = (short)i;
//				
//				int indice = Integer.valueOf(verticeIndiceLines.get(i)) - 1;
//				vertices[i * 3] = Float.valueOf(verticeLines.get(indice*3));
//				vertices[i * 3 + 1] = Float.valueOf(verticeLines.get(indice*3 + 1));
//				vertices[i * 3 + 2] = Float.valueOf(verticeLines.get(indice*3 + 2));
//				
//				int textureIndice = Integer.valueOf(textureIndiceLines.get(i))-1;
//				texCoords[i * 2] = Float.valueOf(textureLines.get(textureIndice*2));
//				texCoords[i * 2 + 1] = Float.valueOf(textureLines.get(textureIndice*2 + 1));
//				
//				int normalIndice = Integer.valueOf(normalIndiceLines.get(i))-1;
//				normals[i * 3] = Float.valueOf(normalLines.get(normalIndice*3));
//				normals[i * 3 + 1] = Float.valueOf(normalLines.get(normalIndice*3 + 1));
//				normals[i * 3 + 2] = Float.valueOf(normalLines.get(normalIndice*3 + 2));
//			}
//		}
//		catch (FileNotFoundException e)
//		{
//			e.printStackTrace();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}

}
