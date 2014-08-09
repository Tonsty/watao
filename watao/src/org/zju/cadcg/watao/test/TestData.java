package org.zju.cadcg.watao.test;

public class TestData {
	static int[] data = new int[2];
	
	static public int[] getData(){
		return data;
	}
	
	static public void setData(int data1, int data2){
		data[0] = data1;
		data[1] = data2;
	}

	public static void setData1(int progress) {
		data[0] = progress;
	}
	
	public static void setData2(int progress) {
		data[1] = progress;
	}

}
