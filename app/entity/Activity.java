package entity;

import java.util.ArrayList;
import java.util.List;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dao.UserDAO;

public class Activity {

	private String activityId;
	private String fromUser;
	private String fromMerchant;
	private ActivityType activityType;
	private boolean likableFlag;
	private int likeCount;
	private String description;
	private long createTime;
	public static enum ActivityType { WIN_GIFT, JOIN, SEND_GIFT, REDEEM_GIFT, LIKE, COMMENT }
	private List<Comment> comments = new ArrayList<Comment>();
	private List<String> likedBy = new ArrayList<String>();
	
	
	public List<String> getLikedBy() {
		return likedBy;
	}
	public void setLikedBy(List<String> likedBy) {
		this.likedBy = likedBy;
	}
	public String getActivityId() {
		return activityId;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	public String getFromMerchant() {
		return fromMerchant;
	}
	public void setFromMerchant(String fromMerchant) {
		this.fromMerchant = fromMerchant;
	}
	public ActivityType getActivityType() {
		return activityType;
	}
	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}
	public boolean isLikableFlag() {
		return likableFlag;
	}
	public void setLikableFlag(boolean likableFlag) {
		this.likableFlag = likableFlag;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public JsonNode toJson(String userId){
		ObjectNode result = Json.newObject();
		result.put("fromUser", getFromUser());
		result.put("activityId", getActivityId());
		result.put("activityType", getActivityType().toString());
		result.put("description", getDescription());
		result.put("likeCount", getLikedBy().size());
		result.put("createTime", getCreateTime());
		ArrayNode commentsArr = new ArrayNode(JsonNodeFactory.instance);
		for(Comment commentDesc : getComments()){
			commentsArr.add(commentDesc.toJson());
		}
		result.put("comments", commentsArr);
		try{
			User user = UserDAO.getInstance().findUserById(userId);
			ArrayNode likedByArr = new ArrayNode(JsonNodeFactory.instance);
			for(String likedBy : getLikedBy()){
				if(user.getFriendIds().contains(likedBy) || userId.equals(likedBy))
					likedByArr.add(likedBy);
			}
			result.put("likedBy", likedByArr);
		}catch(Exception e){
		}
		return result;
	}
}
