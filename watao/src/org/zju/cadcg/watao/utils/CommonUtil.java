package org.zju.cadcg.watao.utils;

import java.lang.reflect.Field;

public class CommonUtil {
	public static int getResId(String name, Class<?> c){
		try {
			Field idField = c.getDeclaredField(name);
			return idField.getInt(idField);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
