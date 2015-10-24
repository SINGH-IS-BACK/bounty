package entity;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UserGiftMap {
	private String modoGiftId;
	private String giftId;
	private double remainingAmount;
	private UserGiftStatus userGiftStatus;
	public static enum UserGiftStatus { GIVEN, PARTIAL, USED }

	public String getModoGiftId() {
		return modoGiftId;
	}
	public void setModoGiftId(String modoGiftId) {
		this.modoGiftId = modoGiftId;
	}
	public String getGiftId() {
		return giftId;
	}
	public void setGiftId(String giftId) {
		this.giftId = giftId;
	}
	public double getRemainingAmount() {
		return remainingAmount;
	}
	public void setRemainingAmount(double remainingAmount) {
		this.remainingAmount = remainingAmount;
	}
	public UserGiftStatus getUserGiftStatus() {
		return userGiftStatus;
	}
	public void setUserGiftStatus(UserGiftStatus userGiftStatus) {
		this.userGiftStatus = userGiftStatus;
	}

	public JsonNode toJson(){
		ObjectNode result = Json.newObject();
		result.put("giftId", getGiftId());
		result.put("remainingAmount", getRemainingAmount());
		result.put("GiftStatus", getUserGiftStatus().toString());
		return result;
	
	}
}
