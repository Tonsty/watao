package org.zju.cadcg.watao.type;

import android.graphics.Bitmap;



public class WTDecorator extends WTObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5128086078955266986L;
	
	private String url;
	private float price;
	private float width;
	public int temp;
	public String idAfter;
	public String idBefore;
	/**
	 * 1、2、3分别代表青花，颜色釉，釉上彩
	 */
	private int type;

	

	public String getUrl() {
		return url;
	}
	public float getPrice() {
		return price;
	}
	public int getType() {
		return type;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width){
		this.width = width;
	}
}