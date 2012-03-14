package de.akuz.android.utmumrechner.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtils {
	
	private final static String SHA1_ALGORITHM = "SHA-1";
	private final static String HASH_CHARSET = "iso-8859-1";
	
	public static boolean isEmtpy(String in){
		if(in == null){
			return true;
		}
		if(in.trim().length() == 0){
			return true;
		}
		return false;
	}
	
	private static String convertToHex(byte[] data){
		StringBuffer buf = new StringBuffer();
		for(int i=0;i<data.length;i++){
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do{
				if((0 <= halfbyte) && (halfbyte <= 9)){
					buf.append((char)('0'+halfbyte));
				} else {
					buf.append((char)(('a')+(halfbyte-10)));
				}
			} while(two_halfs++ < 1);
		}
		return buf.toString();
	}
	
	public static String hashSha1(String in) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		MessageDigest md;
		md = MessageDigest.getInstance(SHA1_ALGORITHM);
		byte[] sha1Hash = new byte[40];
		md.update(in.getBytes(HASH_CHARSET),0,in.length());
		sha1Hash = md.digest();
		return convertToHex(sha1Hash);
	}

}
