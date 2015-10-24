package controllers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import util.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dao.ActivityDAO;
import dao.UserDAO;
import entity.Activity;
import entity.Comment;
import entity.Merchant;
import entity.User;

public class ActivityController extends BaseController{

	private static final String ACTIVITIES_TAG = "activities";

	/*
	public static Result getMyActivities(String userId, int start, int count){
		int startC = 0;
		try {
			User user = UserDAO.getInstance().findUserById(userId);
			List<Activity> activities = user.getActivities();
			ObjectNode result = Json.newObject();
			ArrayNode resultArr = new ArrayNode(JsonNodeFactory.instance);
			for(Activity act : activities){
				if(start > 0 && startC<start){
					startC++;
				}else{
					resultArr.add(act.toJson(userId));
				}
				if(count != -1 && resultArr.size()==count){
					break;
				}
			}
			result.put(ACTIVITIES_TAG, resultArr);
			return ok(result);
		} catch (Exception e) {
			return generateInternalServer(e.getMessage());
		}
	}*/

	public static Result getFriendActivities(String userId, int start, int count){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		int startC = 0;
		try {
			User user = UserDAO.getInstance().findUserById(userId);
			List<Activity> activities = user.getActivities();
			//get friends
			List<String> friendIds = user.getFriendIds();
			for(String friendId : friendIds){
				User friend = UserDAO.getInstance().findUserById(friendId);
				activities.addAll(friend.getActivities());
			}
			//get merchants
			List<Merchant> merchants = user.getMerchants();
			for(Merchant merchant : merchants){
				activities.addAll(merchant.getActivities());
			}
			
			Collections.sort(activities, new Comparator<Activity>(){
		        @Override
				public int compare(Activity o1, Activity o2) {
					// TODO Auto-generated method stub
					if (o2.getCreateTime() == o1.getCreateTime())
							return 0;
					else if(o2.getCreateTime() > o1.getCreateTime())
						return 1;
					else if(o2.getCreateTime() < o1.getCreateTime())
						return -1;
					return 0;
				}
		    });
			
			//TODO sort on activity time
			ObjectNode result = Json.newObject();
			ArrayNode resultArr = new ArrayNode(JsonNodeFactory.instance);
			for(Activity act : activities){
				if(start > 0 && startC<start){
					startC++;
				}else{
					resultArr.add(act.toJson(userId));
				}
				if(count != -1 && resultArr.size()==count){
					break;
				}
			}
			result.put(ACTIVITIES_TAG, resultArr);
			//result.put("activityCount", activities.size());
			return ok(result);
		} catch (Exception e) {
			return generateInternalServer(e.getMessage());
		}
	}
	
	public static Result likeActivity(String activityId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		if(!Utils.checkJsonInput(request())){
			Logger.info("Bad request data for watch request "+request().body());
			return generateBadRequest("Bad input json");
		}
		JsonNode jsonReq = request().body().asJson();
		Activity activity;
		try {
			activity = ActivityDAO.getInstance().findActivityById(activityId);
		} catch (Exception e1) {
			return generateBadRequest("invalid activity id");
		}
		String userId = "";
		try{
			userId = Utils.safeStringFromJson(jsonReq,"userId");
			UserDAO.getInstance().findUserById(userId);
		} catch (Exception e1) {
			return generateBadRequest("invalid user id " + userId);
		}
		Logger.info("likedBy" + userId);
		List<String> likedByList = activity.getLikedBy();
		if(likedByList.contains(userId)){
			likedByList.remove(userId);
		}
		else{
			likedByList.add(userId);
		}
		activity.setLikedBy(likedByList);
		try {
			ActivityDAO.getInstance().updateActivity(activity);
			activity = ActivityDAO.getInstance().findActivityById(activityId);
		} catch (Exception e) {
			return generateInternalServer("Activity not inserted properly");
		}
		return ok(activity.toJson(userId));
		
	}
	
	
	public static Result addCommentOnActivity(String activityId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		if(!Utils.checkJsonInput(request())){
			Logger.info("Bad request data for watch request "+request().body());
			return generateBadRequest("Bad input json");
		}
		JsonNode jsonReq = request().body().asJson();
		Activity activity;
		try {
			activity = ActivityDAO.getInstance().findActivityById(activityId);
		} catch (Exception e1) {
			return generateBadRequest("invalid activity id");
		}
		Comment comment = new Comment();
		String userId ="";
		try{
			comment.setText(Utils.safeStringFromJson(jsonReq,"text"));
			comment.setTime(System.currentTimeMillis());
			userId = Utils.safeStringFromJson(jsonReq,"userId");
		}
		catch(Exception e){
			return generateBadRequest(e.toString());
		}
		try {
			UserDAO.getInstance().findUserById(userId);
		} catch (Exception e1) {
			return generateBadRequest("invalid user id");
		}
		comment.setUserId(userId);
		List<Comment> comments = activity.getComments();
		comments.add(comment);
		activity.setComments(comments);
		try {
			ActivityDAO.getInstance().updateActivity(activity);
			activity = ActivityDAO.getInstance().findActivityById(activityId);
		} catch (Exception e) {
			return generateInternalServer("activity not inserted properly");
		}
		return ok(activity.toJson(userId));
		
	}
	
	//TODO
	/*
	public static Result likeActivity(String userId, String activityId){
		try {
			Activity act = ActivityDAO.getInstance().findActivityById(activityId);
			if(act.isLikableFlag()){
				act.g
			}else{
				return generateBadRequest("activity is not likable");
			} 
		} catch (Exception e) {
			return generateInternalServer(e.getMessage());
		}
		
		return null;
	}*/
}
