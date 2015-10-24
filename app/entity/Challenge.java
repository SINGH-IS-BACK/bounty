package entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dao.UserDAO;

import play.libs.Json;

public class Challenge {

	private String challengeId;
	private String title;
	private String description;
	private long startDate;
	private long endDate;
	private Location center;
	private boolean localFlag;
	private ChallengeType type;
	private Boolean enableFlag;
	private Gift gift;
	private String merchantId;
	private String QRCode;
	private boolean isShareRequired;
	private boolean isPicRequired;
	private String challengeImgUrl;
	private String disclaimer;
	private Boolean isExpired;
	private List<Comment> comments = new ArrayList<Comment>();
	private List<String> likedBy = new ArrayList<String>();
	private List<ChallengeStep> steps = new ArrayList<ChallengeStep>();
	
	//TODO add shareText
	public static enum ChallengeType { QRCode }
	public String getChallengeId() {
		return challengeId;
	}
	public Boolean IsExpired() {
		return isExpired;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public void IsExpired(Boolean isExpired) {
		this.isExpired = isExpired;
	}

	public Location getCenter() {
		return center;
	}
	public void setCenter(Location center) {
		this.center = center;
	}

	public void setChallengeId(String challengeId) {
		this.challengeId = challengeId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isLocalFlag() {
		return localFlag;
	}
	public void setLocalFlag(boolean localFlag) {
		this.localFlag = localFlag;
	}
	public Boolean getEnableFlag() {
		return enableFlag;
	}
	public void setEnableFlag(Boolean enableFlag) {
		this.enableFlag = enableFlag;
	}
	public Gift getGift() {
		return gift;
	}
	public void setGift(Gift gift) {
		this.gift = gift;
	}
	public long getStartDate() {
		return startDate;
	}
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}
	public long getEndDate() {
		return endDate;
	}
	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}
	public String getChallengeImgUrl() {
		return challengeImgUrl;
	}
	public void setChallengeImgUrl(String challengeImgUrl) {
		this.challengeImgUrl = challengeImgUrl;
	}
	public String getDisclaimer() {
		return disclaimer;
	}
	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}
	public List<String> getLikedBy() {
		return likedBy;
	}
	public void setLikedBy(List<String> likedBy) {
		this.likedBy = likedBy;
	}
	public static List<Challenge> remove(List<Challenge> challenges, String challengeId){
		ArrayList<Challenge> updatedChallenges = new ArrayList<Challenge>();
		for(Challenge challenge : challenges){
			if(!challenge.getChallengeId().equals(challengeId)){
				updatedChallenges.add(challenge);
			}
		}
		return updatedChallenges;
	}
	
	public JsonNode toJson(String userId){
		ObjectNode result = Json.newObject();
		result.put("challengeId", getChallengeId());
		result.put("title", getTitle());
		result.put("description", getDescription());
		if(getStartDate() != 0)
			result.put("startDate", getStartDate());
		if(getEndDate() != 0)
			result.put("endDate", getEndDate());
		result.put("center", getCenter().toJson());
		result.put("gift", getGift().toJson());
		result.put("merchant", getMerchantId());
		result.put("isLocal", isLocalFlag());
		result.put("challengeType", getType().name());
		result.put("isEnabled", getEnableFlag());
		result.put("QRCode", getQRCode());
		result.put("isShareRequired", isShareRequired());
		result.put("isPicRequired", isPicRequired());
		result.put("imageUrl", getChallengeImgUrl());
		result.put("disclaimer", getDisclaimer());
		ArrayNode commentsArr = new ArrayNode(JsonNodeFactory.instance);
		for(Comment commentDesc : getComments()){
			commentsArr.add(commentDesc.toJson());
		}
		result.put("comments", commentsArr);
		ArrayNode likedByArr = new ArrayNode(JsonNodeFactory.instance);
		try{
			User user = UserDAO.getInstance().findUserById(userId);
			for(String likedBy : getLikedBy()){
				if(user.getFriendIds().contains(likedBy) || userId.equals(likedBy))
					likedByArr.add(likedBy);
			}
		}catch(Exception e){
		}
		result.put("likedBy", likedByArr);
		ArrayNode resultArr = new ArrayNode(JsonNodeFactory.instance);
		for(ChallengeStep step : getSteps()){
			resultArr.add(step.getStepDesc());
		}
		result.put("steps", resultArr);
		
		ArrayNode stepArr = new ArrayNode(JsonNodeFactory.instance);
		for(ChallengeStep challengeStep : getSteps()){
			stepArr.add(challengeStep.toJson());
		}
		result.put("challengeSteps", stepArr);
		return result;
	}
	public ChallengeType getType() {
		return type;
	}
	public void setType(ChallengeType type) {
		this.type = type;
	}
	public String getQRCode() {
		return QRCode;
	}
	public void setQRCode(String qRCode) {
		QRCode = qRCode;
	}
	public boolean isShareRequired() {
		return isShareRequired;
	}
	public void setShareRequired(boolean isShareRequired) {
		this.isShareRequired = isShareRequired;
	}
	public boolean isPicRequired() {
		return isPicRequired;
	}
	public void setPicRequired(boolean isPicRequired) {
		this.isPicRequired = isPicRequired;
	}
	public List<ChallengeStep> getSteps() {
		return steps;
	}
	public void setSteps(List<ChallengeStep> steps) {
		this.steps = steps;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
}
