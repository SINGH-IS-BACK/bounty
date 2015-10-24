package util;

//import controllers.Application;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import org.apache.commons.codec.binary.Base64;
 
/**
 * Obejct contains security actions that can be applied to a specific action called from
 * a controller.
 */

public class Authentication{
	String MAC_HEADER = "hmac";
	String CONTENT_TYPE_HEADER = "content-type";
	String DATE_HEADER = "Date";
	String MD5 = "MD5";
	String HMACSHA1 = "HmacSHA1";
	
	 private final static String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	 
	 private final static String SECRET = "secretsecret";
	 private final static String USERNAME = "jos";
	
	
	public static String calculateHMAC(String secret, String data) {
		  try {
		   SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), HMAC_SHA1_ALGORITHM);
		   Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		   mac.init(signingKey);
		   byte[] rawHmac = mac.doFinal(data.getBytes());
		   String result = new String(Base64.encodeBase64(rawHmac));
		   return result;
		  } catch (Exception e) {
		   //Logger.warn("Unexpected error while creating hash: " + e.getMessage(), e);
		   throw new IllegalArgumentException();
		  }
		 }
		 
	public static String calculateMD5(String contentToEncode) throws NoSuchAlgorithmException {
		  MessageDigest digest = MessageDigest.getInstance("MD5");
		  digest.update(contentToEncode.getBytes());
		  String result = new String(Base64.encodeBase64(digest.digest()));
		  return result;
		 }
		
	
}

