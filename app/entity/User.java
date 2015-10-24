package entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import entity.UserGiftMap.UserGiftStatus;

public class User {

	private String userId;
	private String name;
	private String url;
	private String mobileNumber;
	private String emailId;
	private List<Merchant> merchants;
	private List<String> friendIds;
	private String accountIdModo;
	private String inviteCode;
	private int inviteCounts;
	private String fbUserName;
	private String twitterId;
	private double AmountWon;
	private Set<String> completedChallengeIds;
	private List<Activity> activities;
	private List<UserGiftMap> wonGifts;
	private boolean verifiedUser;
	
	public User(){
		this.merchants = new ArrayList<Merchant>();
		this.friendIds = new ArrayList<String>();
		this.completedChallengeIds = new HashSet<String>();
		this.wonGifts = new ArrayList<UserGiftMap>();
		this.activities = new ArrayList<Activity>();
		this.inviteCounts = 0;
	}
	
	public User(String name, String mobileNumber){
		this.name = name;
		this.mobileNumber = mobileNumber;
		this.merchants = new ArrayList<Merchant>();
		this.friendIds = new ArrayList<String>();
		this.completedChallengeIds = new HashSet<String>();
		this.activities = new ArrayList<Activity>();
		this.inviteCounts = 0;
		this.wonGifts = new ArrayList<UserGiftMap>();
	}
	
	public double getAmountWon() {
		return AmountWon;
	}

	public void setAmountWon(double amountWon) {
		AmountWon = amountWon;
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String id) {
		this.userId = id;
	}
	public String getName() {
		return name;
	}
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public List<Merchant> getMerchants() {
		return merchants;
	}
	public void setMerchants(List<Merchant> merchants) {
		this.merchants = merchants;
	}
	
	public String getAccountIdModo() {
		return accountIdModo;
	}
	public void setAccountIdModo(String accountIdModo) {
		this.accountIdModo = accountIdModo;
	}
	public String getFbUserName() {
		return fbUserName;
	}
	public void setFbUserName(String fbUserName) {
		this.fbUserName = fbUserName;
	}
	public String getTwitterId() {
		return twitterId;
	}
	public void setTwitterId(String twitterId) {
		this.twitterId = twitterId;
	}
	public List<Activity> getActivities() {
		return activities;
	}
	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}
	public boolean isVerifiedUser() {
		return verifiedUser;
	}
	public void setVerifiedUser(boolean verifiedUser) {
		this.verifiedUser = verifiedUser;
	}
	
	public String getInviteCode() {
		return inviteCode;
	}
	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}
	public int getInviteCounts() {
		return inviteCounts;
	}
	public void setInviteCounts(int inviteCounts) {
		this.inviteCounts = inviteCounts;
	}
	public List<String> getFriendIds() {
		return friendIds;
	}
	public void setFriendIds(List<String> friendIds) {
		this.friendIds = friendIds;
	}
	public Set<String> getCompletedChallengeIds() {
		return completedChallengeIds;
	}
	public void setCompletedChallengeIds(Set<String> completedChallengeIds) {
		this.completedChallengeIds = completedChallengeIds;
	}

	public JsonNode toJson(){
		ObjectNode result = Json.newObject();
		result.put("userId", getUserId());
		result.put("name", getName());
		result.put("url", getUrl());
		result.put("mobileNumber", getMobileNumber());
		result.put("email", getEmailId());
		result.put("inviteCode", getInviteCode());
		result.put("verifiedFlag", isVerifiedUser());
		result.put("amountWon", getAmountWon());
		ArrayNode challengesArr = new ArrayNode(JsonNodeFactory.instance);
		for(String challengeDesc : getCompletedChallengeIds()){
			challengesArr.add(challengeDesc);
		}
		result.put("completedChallenges", challengesArr);
		/*ArrayNode activitiesArr = new ArrayNode(JsonNodeFactory.instance);
		for(Activity activitie : getActivities()){
			activitiesArr.add(activitie.toJson());
		}
		result.put("Activities", activitiesArr);*/
		ArrayNode wonGiftsArr = new ArrayNode(JsonNodeFactory.instance);
		for(UserGiftMap giftmap : getWonGifts()){
			if(giftmap.getUserGiftStatus() != UserGiftStatus.USED){
				wonGiftsArr.add(giftmap.toJson());
			}
		}
		result.put("giftIds", wonGiftsArr);
		ArrayNode friendsArr = new ArrayNode(JsonNodeFactory.instance);
		for(String friend : getFriendIds()){
			friendsArr.add(friend);
		}
		result.put("friendIds", friendsArr);
		return result;
	}

	public List<UserGiftMap> getWonGifts() {
		return wonGifts;
	}

	public void setWonGifts(List<UserGiftMap> wonGifts) {
		this.wonGifts = wonGifts;
	}
	
}
