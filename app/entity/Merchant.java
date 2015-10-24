package entity;

import java.util.ArrayList;
import java.util.List;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Merchant {

	private String merchantId;
	private String merchantName;
	private String merchantIdModo;
	private String merchantLogoUrl;
	private String merchantAddress;
	private String phone;
	private String email;
	private String url;
		//TODO add merchant site url
	private List<Challenge> challenges;
	private List<Activity> activities;
	
	public Merchant(){
		challenges = new ArrayList<Challenge>();
		activities = new ArrayList<Activity>();
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	//TODO add payment info
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public String getMerchantIdModo() {
		return merchantIdModo;
	}
	public void setMerchantIdModo(String merchantIdModo) {
		this.merchantIdModo = merchantIdModo;
	}
	public List<Activity> getActivities() {
		return activities;
	}
	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}
	public List<Challenge> getChallenges() {
		return challenges;
	}
	public void setChallenges(List<Challenge> challenges) {
		this.challenges = challenges;
	}
	public String getMerchantLogoUrl() {
		return merchantLogoUrl;
	}
	public void setMerchantLogoUrl(String merchantLogoUrl) {
		this.merchantLogoUrl = merchantLogoUrl;
	}
	public String getMerchantAddress() {
		return merchantAddress;
	}
	public void setMerchantAddress(String merchantAddress) {
		this.merchantAddress = merchantAddress;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public JsonNode toJson(){
		ObjectNode result = Json.newObject();
		result.put("merchantId", getMerchantId());
		result.put("name", getMerchantName());
		result.put("logoUrl", getMerchantLogoUrl());
		result.put("address", getMerchantAddress());
		result.put("phone", getPhone());
		result.put("email", getEmail());
		//TODO return challenges
		/*ArrayNode challengeArr = new ArrayNode(JsonNodeFactory.instance);
		for(Challenge challenge : getChallenges()){
			challengeArr.add(challenge.toJson(""));
		}
		result.put("Challenges", challengeArr);*/
		return result;
	}
}
