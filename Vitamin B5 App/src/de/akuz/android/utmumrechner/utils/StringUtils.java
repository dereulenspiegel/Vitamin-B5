package de.akuz.android.utmumrechner.utils;

public class StringUtils {
	
	public static boolean isEmtpy(String in){
		if(in == null){
			return true;
		}
		if(in.trim().length() == 0){
			return true;
		}
		return false;
	}

}
