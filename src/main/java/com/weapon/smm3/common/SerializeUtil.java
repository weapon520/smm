package com.weapon.smm3.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.ArrayUtils;

import java.io.UnsupportedEncodingException;

public class SerializeUtil {
	
	private static final Gson gson = new GsonBuilder().serializeNulls().create();

	public static byte[] serialize(Object object) {
		if(object != null){
			try {
				return gson.toJson(object).getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return ArrayUtils.EMPTY_BYTE_ARRAY;
	}

	public static <T> T unserialize(byte[] bytes, Class<T> t) {
		if(ArrayUtils.isNotEmpty(bytes)){
			try {
				return gson.fromJson(new String(bytes, "UTF-8"), t);
			} catch (Exception e) {
			}
		}
		return null;
	}
}
