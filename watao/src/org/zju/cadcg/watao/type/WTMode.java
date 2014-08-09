package org.zju.cadcg.watao.type;

public enum WTMode {
	VIEW,SHAPE,DEROCATE,INTERACT_VIEW,FIRE;
	
	
	private static WTMode[] list = new WTMode[]{VIEW,SHAPE,DEROCATE,INTERACT_VIEW,FIRE};
	public static WTMode getWTMode(int i){
		return list[i];
	}
}
