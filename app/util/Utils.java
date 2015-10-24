package util;

import java.math.BigInteger;
import java.security.SecureRandom;

import play.mvc.Http.Request;

import com.fasterxml.jackson.databind.JsonNode;

import dao.MasterUserDAO;

public class Utils {

	public static String generateBigCode() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}
	
	public static String generateCode() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(30, random).toString(32);
	}

	public static boolean checkCredentials(Request req){
		try{
			String consumerKey = req.getHeader("API-Key");
			String accessToken = req.getHeader("API-Token");
			String expectedAccessToken = MasterUserDAO.getInstance().findKey(consumerKey);
			return accessToken.equals(expectedAccessToken);
		}catch(Exception e){
			return false;
		}
	}
	
	public static boolean checkJsonInput(Request req){
		try{
			JsonNode jsonReq = req.body().asJson();
			return !jsonReq.isNull();
		}catch(Exception e){
			return false;
		}
	}
	
	public static String safeStringFromJson(JsonNode node, String key, String default_key) {
		try {
			return node.get(key).asText();
		} catch (Exception e) {
			return default_key;
		}
	}
	
	public static String safeStringFromJson(JsonNode node, String key) {
		try {
			return node.get(key).asText();
		} catch (Exception e) {
			return "";
		}
	}

	public static long safeLongFromJson(JsonNode node, String key) {
		try {
			return node.get(key).asLong();
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static Double safeDoubleFromJson(JsonNode node, String key) {
		try {
			return node.get(key).asDouble();
		} catch (Exception e) {
			return 0.0;
		}
	}
	
	public static boolean safeBooleanFromJson(JsonNode node, String key) {
		try {
			return node.get(key).asBoolean();
		} catch (Exception e) {
			return false;
		}
	}
}
