package entity;

import org.apache.commons.lang3.StringUtils;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dao.MerchantDAO;

public class Gift {

	private String giftId;
	private String giftName;
	private double amount;
	private String description;
	private String merchantId;
	private int maxGifts;
	private int givenGiftCount;
	private String GiftLogoUrl;
	private GiftStatus giftStatus;
	public static enum GiftStatus {ADDED, FUNDED, CLOSED}

	//TODO add gift image
	public String getGiftName() {
		return giftName;
	}
	public String getGiftLogoUrl() {
		return GiftLogoUrl;
	}
	public void setGiftLogoUrl(String giftLogoUrl) {
		GiftLogoUrl = giftLogoUrl;
	}
	public GiftStatus getGiftStatus() {
		return giftStatus;
	}
	public void setGiftStatus(GiftStatus giftStatus) {
		this.giftStatus = giftStatus;
	}
	public void setGiftName(String giftName) {
		this.giftName = giftName;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getGiftId() {
		return giftId;
	}
	public void setGiftId(String giftId) {
		this.giftId = giftId;
	}
	public int getMaxGifts() {
		return maxGifts;
	}
	public void setMaxGifts(int maxGifts) {
		this.maxGifts = maxGifts;
	}
	public int getGivenGiftCount() {
		return givenGiftCount;
	}
	public void setGivenGiftCount(int givenGiftCount) {
		this.givenGiftCount = givenGiftCount;
	}
	public JsonNode toJson() {
		ObjectNode result = Json.newObject();
		result.put("giftId", getGiftId());
		result.put("giftName", getGiftName());
		result.put("amount", getAmount());
		result.put("description", getDescription());
		result.put("maxGift", getMaxGifts());
		result.put("givenGift", getGivenGiftCount());
		if(getGiftStatus() != null){
			result.put("giftStatus", getGiftStatus().name());
		}
		result.put("giftUrl", getGiftLogoUrl());
		if(StringUtils.isNotEmpty(getMerchantId())){
			try {
				result.put("merchantId", getMerchantId());
				result.put("merchant", MerchantDAO.getInstance().getMerchantName(getMerchantId()));
			} catch (Exception e) {
			}
		}
		return result;
	}
	
}
