package org.zju.cadcg.watao.gl;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.zju.cadcg.watao.utils.PotteryTextureManager;

import android.graphics.Bitmap;

public class Pottery extends GLMeshObject{
	
	public final static int VERTICAL_PRICISION = 50;
	public final static int HORIZONAL_PRICISION = 50;
	
	private final float minHeight = 0.45f;
	private final float maxHeight = 3.0f;
	private final float midHeight = 1.725f;
	private final float initializeHeight = 1.0f;
	private final float initialRadius = 0.6f;
	private final float thickness = 0.05f;
	
	private float currentHeight = 1.0f;
	
	public float getCurrentHeight() {
		return currentHeight;
	}
	public void setCurrentHeight(float currentHeight) {
		this.currentHeight = currentHeight;
	}

	private float[] radiuses = new float[VERTICAL_PRICISION];
	public void setRadiuses(float[] radiuses) {
		this.radiuses = radiuses;
	}

	private float radiusesMax = 1.0625f;
	private float[] radiusesMin = new float[VERTICAL_PRICISION];
	
	protected float angleForSensor = 0.0f;
	protected float angleForRotate = 0.0f;
	public float varUsedForEllipseToRegular;
	
	public float getHeight() {
		return currentHeight;
	}
	public void setVertices(float[] vertices) {
		this.vertices = vertices;
		fastEstimateNormals();
		updateVertexBuffer();
		updateNormalBuffer();
	}
	
	public Pottery(){
		initializeData();
		int pointNum = 2 * VERTICAL_PRICISION * (HORIZONAL_PRICISION + 1);
		vertices = new float[(pointNum+1) * 3];
		genVerticesRandom();
		
		
		normals = new float[(pointNum+1) * 3];
		genNormalsXZ();
		fastEstimateNormals();
		
		texCoords = new float[(pointNum+1)*2];
		genTexCoords();
		
		indices = new short[(2 * VERTICAL_PRICISION - 1) * HORIZONAL_PRICISION * 6 + HORIZONAL_PRICISION * 3];
		genIndices();
		
		updateBuffers();
	}	
	
	private void initializeData(){
		for(int i = 0; i < VERTICAL_PRICISION; i++){
			radiuses[i] = initialRadius * (1.03f - i / (float) VERTICAL_PRICISION / 16.0f);
		}
		
		int j = VERTICAL_PRICISION/8;
		for(int i = 0; i < j; ++i){
			radiusesMin[i] = 0.3125f - 0.125f * ((float)i/j);
		}
		for(int i = j; i < VERTICAL_PRICISION; ++i){
			radiusesMin[i] = 0.1875f;
		}
		
		//change the shape randomly
		Random random = new Random(System.currentTimeMillis());
		float delta = 0.1f;
		changeBasesFatter(random.nextFloat() * currentHeight, 0, delta);
		changeBasesFatter(random.nextFloat() * currentHeight, 0, delta);
		changeBasesThinner(random.nextFloat() * currentHeight, 0, delta);
		changeBasesThinner(random.nextFloat() * currentHeight, 0, delta);
		changeBasesThinner(random.nextFloat() * currentHeight, 0, delta);
		changeBasesThinner(random.nextFloat() * currentHeight, 0, delta);
	}
	
	public void genVerticesRandom(){
		varUsedForEllipseToRegular = 0;
		for( int i = 0; i < 2*VERTICAL_PRICISION; i++ ){
			for( int j = 0; j < HORIZONAL_PRICISION + 1; j++ ){
				int offset = (i*(HORIZONAL_PRICISION + 1) + j)*3;
				vertices[offset] = computerVerticeX(i, j);
				vertices[offset + 1] = computerVerticeY(i);
				vertices[offset + 2] = computerVerticeZ(i, j);
			}
		}
		
		int length = vertices.length;
		vertices[length - 1] = 0;
		vertices[length - 2] = 0;
		vertices[length - 3] = 0;
	}
	
	public float getVarUsedForEllipseToRegular() {
		return varUsedForEllipseToRegular;
	}
	public void setVarUsedForEllipseToRegular(float varUsedForEllipseToRegular) {
		this.varUsedForEllipseToRegular = varUsedForEllipseToRegular;
	}
	private void genVerticesFromBases(){
		for( int i = 0; i < 2*VERTICAL_PRICISION; i++ ){
			for( int j = 0; j < HORIZONAL_PRICISION + 1; j++ ){
				int offset = (i*(HORIZONAL_PRICISION + 1) + j)*3;
				vertices[offset] = computerVerticeX(i, j);
				vertices[offset + 1] = computerVerticeY(i);
				vertices[offset + 2] = computerVerticeZ(i, j);
			}
		}
		if(varUsedForEllipseToRegular < 0.9){
			varUsedForEllipseToRegular += 0.005f;
		}
	}
	
	private void genNormalsXZ(){
		for( int i = 0; i < 2*VERTICAL_PRICISION; i++ ){
			for( int j = 0; j < HORIZONAL_PRICISION + 1; j++ ){
				int offset = (i*(HORIZONAL_PRICISION + 1) + j)*3;
				normals[offset] = computerNormalX(i, j);
				normals[offset + 2] = computerNormalZ(i, j);
			}
		}
		int length = normals.length;
		normals[length - 1] = 0;
		normals[length - 2] = 1;
		normals[length - 3] = 0;
	}
	
	
	public void genTexCoords(){
		float verticalStep = 0.5f/VERTICAL_PRICISION;
		float horizonalStep = 1f/HORIZONAL_PRICISION;
		for(int i = 0; i < 2*VERTICAL_PRICISION; ++i){
			float f = 1f - i*verticalStep;
			for(int j = 0; j < HORIZONAL_PRICISION + 1; ++j){
				int offset = (i*(HORIZONAL_PRICISION + 1) + j)*2;
				texCoords[offset] = j*horizonalStep;
				texCoords[offset + 1] = f;
			}
		}
		
		int length = texCoords.length;
		texCoords[length - 2] = 0.5f;
		texCoords[length - 1] = 0.999f;
	}
	
	public void genIndices(){
		for(int i = 0; i < 2 * VERTICAL_PRICISION - 1; ++i){
			for(int j = 0; j < HORIZONAL_PRICISION; ++j){
				int offset = (i*HORIZONAL_PRICISION + j) * 6;
				int baseIndex = (i*(HORIZONAL_PRICISION + 1) + j);
				indices[offset] = (short) baseIndex;
				indices[offset + 1] = (short) (baseIndex + HORIZONAL_PRICISION + 1);
				indices[offset + 2] = (short) (baseIndex + 1);
				indices[offset + 3] = (short) (baseIndex + 1);
				indices[offset + 4] = (short) (baseIndex + HORIZONAL_PRICISION + 1);
				indices[offset + 5] = (short) (baseIndex + HORIZONAL_PRICISION + 1 + 1);
			}
		}
		
		int offset = (2 * VERTICAL_PRICISION - 1) * HORIZONAL_PRICISION * 6;
		for(int j = 0; j < HORIZONAL_PRICISION; ++j){
			indices[offset + j * 3] = (short) j;
			indices[offset + j * 3 + 2] = (short) (j + 1);
			indices[offset + j * 3 + 1] = (short) (vertices.length / 3 - 1);
		}
	}

	private float computeRadius(int i) {
		float radius;
		if(i > VERTICAL_PRICISION - 1){
			i = 2*VERTICAL_PRICISION - 1 - i;
			if(i == VERTICAL_PRICISION - 1){
				radius = radiuses[i] - thickness * 4 / 5;
			}else{
				radius = radiuses[i] - thickness;
			}
		}else{
			if(i == VERTICAL_PRICISION - 1){
				radius = radiuses[i] - thickness * 1 / 5;
			}else{
				radius = radiuses[i];
			}
			
		}
		return radius;
	}

	float computerVerticeX(int i, int j){
		float radius = computeRadius(i);
		j %= HORIZONAL_PRICISION;
		if(i > VERTICAL_PRICISION - 1){
			i = 2*VERTICAL_PRICISION - 1 - i;
		}
		radius = radius *(0.95f + 0.05f * varUsedForEllipseToRegular + 0.15f*(1.0f - varUsedForEllipseToRegular) / VERTICAL_PRICISION * i);
		return (float) (Math.cos(j*2.0f*Math.PI/HORIZONAL_PRICISION)*radius);
	}
	
    float computerVerticeY(int i){
		if(i > VERTICAL_PRICISION - 1){
			i = 2*VERTICAL_PRICISION - 1 - i;
		}
		return (float) i*currentHeight/(VERTICAL_PRICISION - 1);
	}
    
	float computerVerticeZ(int i, int j){
		float radius = computeRadius(i);
		j %= HORIZONAL_PRICISION;
		if(i > VERTICAL_PRICISION - 1){
			i = 2*VERTICAL_PRICISION - 1 - i;
		}
		radius = radius * (1.1f - 0.15f*(1.0f - varUsedForEllipseToRegular) / VERTICAL_PRICISION * i - 0.1f * varUsedForEllipseToRegular);
		return (float) Math.sin(j*2*Math.PI/HORIZONAL_PRICISION)*radius;
	}
	
	float computerNormalX(int i, int j){
		float result = (float) Math.cos(j*2*Math.PI / HORIZONAL_PRICISION);
		if (i > VERTICAL_PRICISION) {
			result = -result;
		}else if(i == VERTICAL_PRICISION - 1 || i == VERTICAL_PRICISION){
			result = 0.0f;
		}
		return result;
	}
	
	float computerNormalY(int i){
		if(i == VERTICAL_PRICISION - 1 || i == VERTICAL_PRICISION){
			return 1.0f;
		}else{
			return 0.0f;
		}
	}
	
	float computerNormalZ(int i, int j){
		float result = (float) Math.sin(j*2*Math.PI / HORIZONAL_PRICISION);
		if (i > VERTICAL_PRICISION) {
			result = -result;
		}else if(i == VERTICAL_PRICISION - 1 || i == VERTICAL_PRICISION){
			result = 0.0f;
		}
		return result;
	}
	
	float vSpeed = 0.018f;
	public float getVSpeed() {
		return vSpeed;
	}

	public void setVSpeed(float vSpeed) {
		this.vSpeed = vSpeed;
	}

	public void taller(){
		if (currentHeight > midHeight) {
			currentHeight += computerVerticalDelta();
		}else{
			currentHeight += vSpeed;
		}
		
		genVerticesFromBases();
		fastEstimateNormals();
		updateVertexBuffer();
		updateNormalBuffer();
	}
	
	public void shorter(){
		if (currentHeight < midHeight) {
			currentHeight -= computerVerticalDelta();
		}else{
			currentHeight -= vSpeed;
		}
		
		genVerticesFromBases();
		fastEstimateNormals();
		updateVertexBuffer();
		updateNormalBuffer();
	}
	
	private float computerVerticalDelta() {
		float delta = Math.abs(currentHeight - midHeight);
		return vSpeed * (1.0f - 2 * delta / (maxHeight - minHeight));
	}

	
	private static final float CONSTANTS_FOR_GAUSSIAN =  (float) (1.0f / Math.sqrt( 2 * Math.PI ));
	public float gaussian( float delta, float mean, float x ){
		return   (float) (CONSTANTS_FOR_GAUSSIAN / delta * Math.exp(-(x - mean)*(x - mean) / 2.0f / delta / delta ));
	}
	
	float liandai = 0.28f;
	public float getLiandai() {
		return liandai;
	}

	public void setLiandai(float liandai) {
		this.liandai = liandai;
	}
	
	public	float hspeed = 0.02f;
	public float getHspeed() {
		return hspeed;
	}

	public void setHspeed(float hspeed) {
		this.hspeed = hspeed;
	}
	
	public void thinner( float y ){
		changeBasesThinner(y, 0.0f);
			genVerticesFromBases();
			fastEstimateNormals();
			updateVertexBuffer();
			updateNormalBuffer();
	}
	
	public void fatter (float y){
		changeBasesFatter(y, 0.0f);
		genVerticesFromBases();
		fastEstimateNormals();
		updateVertexBuffer();
		updateNormalBuffer();
	}
	private void changeBasesFatter(float y, float mean) {
		changeBasesFatter(y, mean, liandai);
	}

	private void changeBasesFatter(float y, float mean, float delta) {
		for( int i = 0; i < VERTICAL_PRICISION; i++ ){
			radiuses[i] += (float) Math.atan((radiusesMax - radiuses[i]))
					* 2.0f / (float) Math.PI* hspeed
					* gaussian(delta, mean, (float)i / VERTICAL_PRICISION * currentHeight - y );
		}
	}

	private void changeBasesThinner(float y, float mean) {
		changeBasesThinner(y, mean, liandai);
	}
	
	private void changeBasesThinner(float y, float mean,float delta) {
		for( int i = 0; i < VERTICAL_PRICISION; i++ ){
			float temp = (float) Math.atan((radiuses[i] - radiusesMin[i])) * 2.0f / (float) Math.PI;
			radiuses[i] = radiuses[i] - temp * hspeed * gaussian(delta, mean, (float)i / VERTICAL_PRICISION * currentHeight - y );
		}
	}
	
	
	public float[] getRadiuses() {
		return radiuses;
	}

		private class vec3{
		public float x = 0.0f;
		public float y = 0.0f;
		public float z = 0.0f;
		
		public vec3(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public vec3(){
		};
		
		public vec3(vec3 begin, vec3 end){
			this.x = end.x - begin.x;
			this.y = end.y - begin.y;
			this.z = end.z - begin.z;
		}
		
		public vec3(vec3 currentPoint) {
			this.x = currentPoint.x;
			this.y = currentPoint.y;
			this.z = currentPoint.z;
		}

		public vec3 crossProduct(vec3 v2){
			vec3 result = new vec3();
			result.x = this.y * v2.z - this.z * v2.y;
			result.y = this.x * v2.z - this.z * v2.x;
			result.z = this.x * v2.y - this.y * v2.x;
			return result;
		}
		
		public void normalize(){
			float length = (float) Math.sqrt(x*x + y*y + z*z);
			this.x /= length;
			this.y /= length;
			this.z /= length;
		}
		
	}
	
	public void fastEstimateNormals() {
		for( int i = 0; i < 2*VERTICAL_PRICISION; i++ ){
			vec3 normal = new vec3();
			if(i == VERTICAL_PRICISION - 1 || i == VERTICAL_PRICISION){
				normal.y = 1.0f;
			}else{
				//get current point;
				int baseIndex = i * (HORIZONAL_PRICISION + 1) * 3;
				vec3 currentPoint = new vec3(vertices[baseIndex], vertices[baseIndex + 1], vertices[baseIndex + 2]);

				//get the current point's up point;
				vec3 upPoint = null;
				if(i == VERTICAL_PRICISION - 1 || i == VERTICAL_PRICISION){
					upPoint = new vec3(currentPoint);
				}else{
					int upBaseIndex = (i + 1) * (HORIZONAL_PRICISION + 1) * 3;	
					upPoint = new vec3(vertices[upBaseIndex], vertices[upBaseIndex + 1], vertices[upBaseIndex + 2]);
				}

				//get the current point's down point;
				vec3 downPoint = null;
				if(i == 0 || i == 2 * VERTICAL_PRICISION - 1){
					downPoint = new vec3(currentPoint);
				}else{
					int downBaseIndex = (i - 1) * (HORIZONAL_PRICISION + 1) * 3;
					downPoint = new vec3(vertices[downBaseIndex], vertices[downBaseIndex + 1], vertices[downBaseIndex + 2]);
				}

				//get the current point's left point
				int leftBaseIndex = (i * (HORIZONAL_PRICISION + 1) + HORIZONAL_PRICISION - 1) * 3;
				vec3 leftPoint = new vec3(vertices[leftBaseIndex], vertices[leftBaseIndex + 1], vertices[leftBaseIndex + 2]);

				//get the current point's right point
				int rightBaseIndex = (i * (HORIZONAL_PRICISION + 1) + 1) * 3;
				vec3 rightPoint = new vec3(vertices[rightBaseIndex], vertices[rightBaseIndex + 1], vertices[rightBaseIndex + 2]);

				//computer the four vector: up,down,left,right
				vec3 upVector = new vec3(currentPoint, upPoint);
				vec3 downVector = new vec3(currentPoint, downPoint);
				vec3 leftVector = new vec3(currentPoint, leftPoint);
				vec3 rightVector = new vec3(currentPoint, rightPoint);

				//computer the four sub-normal
				vec3 normal1 = rightVector.crossProduct(upVector);
				vec3 normal2 = upVector.crossProduct(leftVector);
				vec3 normal3 = leftVector.crossProduct(downVector);
				vec3 normal4 = downVector.crossProduct(rightVector);

				//add the four sub-normal
				normal.x = normal1.x + normal2.x + normal3.x + normal4.x;
				normal.y = normal1.y + normal2.y + normal3.y + normal4.y;
				normal.z = normal1.z + normal2.z + normal3.z + normal4.z;

				//normalize the final normal
				normal.normalize();
			}
			
			//the above code computer the first pointer in this floor,because the pottery's 
			//shape is very regular, so we assume that the normal of the points in the same 
			//storey have the same normal.y; 
			for(int j = 0; j < HORIZONAL_PRICISION + 1; ++j){
				int offset = (i*(HORIZONAL_PRICISION + 1) + j)*3;
				float x = normals[offset];
				float z = normals[offset + 2];
				if(z != 0.0f){
					float xdz = x/z;
					normals[offset + 1] = normal.y;
					float newZ = (float) Math.sqrt((1-normal.y * normal.y)/(1 + xdz * xdz));
					if(normals[offset + 2] < 0){
						normals[offset + 2] = -newZ;
					}else{
						normals[offset + 2] = newZ;
					}
					normals[offset] = xdz * normals[offset + 2];
				}else{
					normals[offset + 1] = normal.y;
					normals[offset + 2] = 0;
					float newX = (float) Math.sqrt(1-normal.y * normal.y);
					if(normals[offset] < 0){
						normals[offset] = -newX;
					}else{
						normals[offset] = newX;
					}
				}
			}
		}	
	}
	@Override
	public void estimateNormals() {
		for( int i = 0; i < 2*VERTICAL_PRICISION; i++ ){
			for(int j = 0; j < HORIZONAL_PRICISION; ++j){
				vec3 p1 = null, p2 = null, p3 = null, p4 = null;
				if (i == 0 || i == VERTICAL_PRICISION) {
					int offsetP1 = ((i + 1) * (HORIZONAL_PRICISION + 1) + (j - 1 + HORIZONAL_PRICISION) % HORIZONAL_PRICISION) * 3;
					p1 = new vec3(vertices[offsetP1], vertices[offsetP1 + 1], vertices[offsetP1 + 2]);
					
					int offsetP2 = ((i + 1) * (HORIZONAL_PRICISION + 1) + (j + 1) % HORIZONAL_PRICISION) * 3;
					p2 = new vec3(vertices[offsetP2], vertices[offsetP2 + 1], vertices[offsetP2 + 2]);
					
					int offsetP3 = (i * (HORIZONAL_PRICISION + 1) + (j - 1 + HORIZONAL_PRICISION) % HORIZONAL_PRICISION) * 3;
					p3 = new vec3(vertices[offsetP3], vertices[offsetP3 + 1], vertices[offsetP3 + 2]);
					
					int offsetP4 = (i * (HORIZONAL_PRICISION + 1) + (j + 1) % HORIZONAL_PRICISION) * 3;
					p4 = new vec3(vertices[offsetP4], vertices[offsetP4 + 1], vertices[offsetP4 + 2]);
				}else if(i == VERTICAL_PRICISION - 1 || i == 2 * VERTICAL_PRICISION - 1){
					int offsetP1 = (i * (HORIZONAL_PRICISION + 1) + (j - 1 + HORIZONAL_PRICISION) % HORIZONAL_PRICISION) * 3;
					p1 = new vec3(vertices[offsetP1], vertices[offsetP1 + 1], vertices[offsetP1 + 2]);
					
					int offsetP2 = (i * (HORIZONAL_PRICISION + 1) + (j + 1) % HORIZONAL_PRICISION) * 3;
					p2 = new vec3(vertices[offsetP2], vertices[offsetP2 + 1], vertices[offsetP2 + 2]);
					
					int offsetP3 = ((i - 1) * (HORIZONAL_PRICISION + 1) + (j - 1 + HORIZONAL_PRICISION) % HORIZONAL_PRICISION) * 3;
					p3 = new vec3(vertices[offsetP3], vertices[offsetP3 + 1], vertices[offsetP3 + 2]);
					
					int offsetP4 = ((i - 1) * (HORIZONAL_PRICISION + 1) + (j + 1) % HORIZONAL_PRICISION) * 3;
					p4 = new vec3(vertices[offsetP4], vertices[offsetP4 + 1], vertices[offsetP4 + 2]);
				}else{
					int offsetP1 = ((i + 1) * (HORIZONAL_PRICISION + 1) + (j - 1 + HORIZONAL_PRICISION) % HORIZONAL_PRICISION) * 3;
					p1 = new vec3(vertices[offsetP1], vertices[offsetP1 + 1], vertices[offsetP1 + 2]);
					
					int offsetP2 = ((i + 1) * (HORIZONAL_PRICISION + 1) + (j + 1) % HORIZONAL_PRICISION) * 3;
					p2 = new vec3(vertices[offsetP2], vertices[offsetP2 + 1], vertices[offsetP2 + 2]);
					
					int offsetP3 = ((i - 1) * (HORIZONAL_PRICISION + 1) + (j - 1 + HORIZONAL_PRICISION) % HORIZONAL_PRICISION) * 3;
					p3 = new vec3(vertices[offsetP3], vertices[offsetP3 + 1], vertices[offsetP3 + 2]);
					
					int offsetP4 = ((i - 1) * (HORIZONAL_PRICISION + 1) + (j + 1) % HORIZONAL_PRICISION) * 3;
					p4 = new vec3(vertices[offsetP4], vertices[offsetP4 + 1], vertices[offsetP4 + 2]);
				}
				
				vec3 v1 = new vec3(p1, p4);
				vec3 v2 = new vec3(p3, p2);
				
				vec3 normal = v2.crossProduct(v1);
				normal.normalize();
				
				int offset = (i*(HORIZONAL_PRICISION + 1) + j)*3;
				normals[offset] = normal.x;
				normals[offset + 1] = normal.y;
				normals[offset + 2] = normal.z;
			}
			int offset = (i*(HORIZONAL_PRICISION + 1) + HORIZONAL_PRICISION)*3;
			int offsetBegin = i*(HORIZONAL_PRICISION + 1)*3; 
			normals[offset] = normals[offsetBegin];
			normals[offset + 1] = normals[offsetBegin + 1];
			normals[offset + 2] = normals[offsetBegin + 2];
		}	
	}

	public void setAngleRotateY(float potteryCurrentAngleRotateX) {
		this.angleForSensor = potteryCurrentAngleRotateX;
	}

	public void setAngleForRotate(float rotateAngle) {
		this.angleForRotate = rotateAngle;
	}

	public void reset() {
		final float heightBegin = this.currentHeight;
		final float heightEnd = this.initializeHeight;
		final float[] radiusesBegin = radiuses.clone();
		
		for(int i = 0; i < VERTICAL_PRICISION; i++){
			radiuses[i] = initialRadius * (1.03f - i / (float) VERTICAL_PRICISION / 16.0f);
		}
		//change the shape randomly
		Random random = new Random(System.currentTimeMillis());
		float delta = 0.1f;
		changeBasesFatter(random.nextFloat() * currentHeight, 0, delta);
		changeBasesFatter(random.nextFloat() * currentHeight, 0, delta);
		changeBasesThinner(random.nextFloat() * currentHeight, 0, delta);
		changeBasesThinner(random.nextFloat() * currentHeight, 0, delta);
		changeBasesThinner(random.nextFloat() * currentHeight, 0, delta);
		changeBasesThinner(random.nextFloat() * currentHeight, 0, delta);
		final float[] radiusesEnd = radiuses.clone();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				long beginTime = System.currentTimeMillis();
				long timeEscape = 0;
				while ((timeEscape = System.currentTimeMillis() - beginTime) < 800) {
					float rate = timeEscape / 800.0f;
					currentHeight = heightBegin - (heightBegin - heightEnd) * rate;
					for(int i = 0; i < VERTICAL_PRICISION; i++){
						radiuses[i] = radiusesBegin[i] - (radiusesBegin[i] - radiusesEnd[i]) * rate; 
					}		
					genVerticesRandom();
					fastEstimateNormals();
					updateVertexBuffer();
					updateNormalBuffer();
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		
	}
	


	public void draw(GL10 gl) {

	}

	public void draw() {
		
	}

	public void setShape(float[] bases, float height) {
		this.radiuses = bases;
		this.currentHeight = height;
		varUsedForEllipseToRegular = 1.0f;
		genVerticesFromBases();
		fastEstimateNormals();
		updateNormalBuffer();
		updateVertexBuffer();
	}

	public void onResume() {
		
	}
	public float getMaxWidth() {
		float result = 0;
		for (float f : radiuses) {
			if (f > result) {
				result = f;
			}
		}
		return result * 16;
	}
	
	public float getHeightReal(){
		return currentHeight * 16;
	}
}
