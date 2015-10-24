package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import util.FacebookConfig;
import util.TwitterConfig;
import util.Utils;
import walletManager.ModoPayments;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dao.ActivityDAO;
import dao.InviteDAO;
import dao.UserDAO;
import entity.Activity;
import entity.Invite;
import entity.User;
import entity.UserGiftMap;
import entity.UserGiftMap.UserGiftStatus;

public class UserController extends BaseController{

	public static Result addMasterUser(){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try {
			ModoPayments.registerUserAtModo("8312148851");
			ModoPayments.addCard("4124939999999990","1220", "95131", "123");
		} catch (Exception e) {
			return generateInternalServer(e.getMessage());
		}
		return generateOkTrue();
	}

	public static Result createInviteCode(int numberOfInvites){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		Invite inviteCode = new Invite();
		List<String> inviteCodes = new ArrayList<String>();
		for(int i = 0 ; i < numberOfInvites ; i++){
			String str = Utils.generateCode();
			if(str.length() == 6)
				inviteCodes.add(str);
		}
		inviteCode.setNewinviteCodes(inviteCodes);
		try{
			InviteDAO.getInstance().insertInviteCode(inviteCode);
		} catch (Exception e) {
			return generateInternalServer("Invited Not generated properly" + e);
		}
	return generateOkTrue();
	}

	public static Result generateInviteCode(int numberOfInvites){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try{
		ArrayList<Invite> invites = InviteDAO.getInstance().getAllInviteCodes();
		if(invites.size() == 0){
			return createInviteCode(numberOfInvites);
		}
		Invite inviteCode = invites.get(0);
		List<String> inviteCodes = inviteCode.getNewinviteCodes();
		for(int i = 0 ; i < numberOfInvites ; i++){
			inviteCodes.add(Utils.generateCode());
		}
		inviteCode.setNewinviteCodes(inviteCodes);
		InviteDAO.getInstance().updateInviteCode(inviteCode);
		} catch (Exception e) {
			return generateInternalServer("Invited Not generated properly" + e);
		}
		return generateOkTrue();
	}

	public static Result getInviteCode(){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		String newInviteCode = "";
		ObjectNode result = Json.newObject();
		try{
			ArrayList<Invite> Invites = InviteDAO.getInstance().getAllInviteCodes();
			if(Invites.size() == 0)
				return generateInternalServer("No Invite for this application");
			Invite invite = Invites.get(0);
			if(invite.getNewinviteCodes().size() == 0)
				return generateInternalServer("No more invites left");
			newInviteCode = invite.getNewinviteCodes().remove(0);
			invite.getGiveninviteCodes().add(newInviteCode);
			InviteDAO.getInstance().updateInviteCode(invite);
			result.put("inviteCode", newInviteCode);
		} catch (Exception e) {
			return generateInternalServer("Invite Not generated properly" + e);
		}
		return ok(result);
	}

	public static Result useInviteCode(){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		if(!Utils.checkJsonInput(request())){
			Logger.info("Use Invite Code. Bad request data for Invite Code "+request().body());
			return generateBadRequest("Bad input json" + request().body());
		}
		
		JsonNode jsonReq = request().body().asJson();
		String inviteCode = jsonReq.get("inviteCode").asText();
		try{
			ArrayList<Invite> Invites = InviteDAO.getInstance().getAllInviteCodes();
			if(Invites.size() == 0)
				return generateInternalServer("No Invite for this application");
			Invite invite = Invites.get(0);
			if(!invite.getGiveninviteCodes().contains(inviteCode))
				return generateInternalServer("Invalid Invite Code");
			invite.getGiveninviteCodes().remove(inviteCode);
			invite.getUsedinviteCodes().add(inviteCode);
			InviteDAO.getInstance().updateInviteCode(invite);
		} catch (Exception e) {
			return generateInternalServer("Invite Not generated properly" + e);
		}
		return generateOkTrue();
	}

	public static Result updateEmailID(String UserId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		if(!Utils.checkJsonInput(request())){
			Logger.info("Update Email ID. Bad request data for Invite Code "+request().body());
			return generateBadRequest("Bad input json" + request().body());
		}
		User user = new User();
		try{
			user = UserDAO.getInstance().findUserById(UserId);
		}
		catch(Exception e){
			return generateBadRequest("User not found");
		}
		JsonNode jsonReq = request().body().asJson();
		String emailId = jsonReq.get("emailId").asText();
		
		user.setEmailId(emailId);
		try{
			UserDAO.getInstance().updateUser(user);
		}
		catch(Exception e){
			return generateBadRequest("User not updated.");
		}
		return generateOkTrue();
		
	}
	//register using fb/twitter
	
	public static Result updateFriends(String UserId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		if(!Utils.checkJsonInput(request())){
			Logger.info("Update Friends. Bad request data for register user "+request().body());
	    	return generateBadRequest("Bad input json" + request().body());
		}
		JsonNode jsonReq = request().body().asJson();
		String accessToken = jsonReq.get("accessToken").asText();
		String accessSecret = jsonReq.get("accessSecret").asText();
	
		User user = new User();
		try{
			user = UserDAO.getInstance().findUserById(UserId);
			if(!user.getTwitterId().isEmpty()){
				TwitterConfig twitter = new TwitterConfig(accessToken, accessSecret);
	    		updateFriends(twitter, user);
	    		user = UserDAO.getInstance().findUserByTwitterID(twitter.getTwitterID());
	    		return ok(user.toJson());
		  	}
			else{
				FacebookConfig fb = new FacebookConfig(accessToken);
	    		updateFriends(fb, user);
	    		user = UserDAO.getInstance().findUserByFB_ID(fb.getFacebookUserName());
	    		return ok(user.toJson());
	    	}
		}
		catch(Exception e){
			return generateBadRequest("User not found" + e);
		}
	}
	
	public static void updateFriends(FacebookConfig fb, User user) throws Exception{
		List<String> allFriendIds = fb.getFriendIds();
		List<String> newfriendIds = new ArrayList<String>();
		for (int i = 0 ; i < allFriendIds.size() ; i++){
			User friend = UserDAO.getInstance().findUserByFB_ID(allFriendIds.get(i));
			if(friend != null){
				newfriendIds.add(friend.getUserId());
			}
		}
		user.setFriendIds(newfriendIds);
    	UserDAO.getInstance().updateUser(user);
    }
	
	
	public static void updateFriends(TwitterConfig twitter, User user) throws Exception{
		List<String> allFriendIds = twitter.getFriendIds();
		List<String> newfriendIds = new ArrayList<String>();
		for (int i = 0 ; i < allFriendIds.size() ; i++){
			User friend = UserDAO.getInstance().findUserByTwitterID(allFriendIds.get(i));
			if(friend != null){
				newfriendIds.add(friend.getUserId());
			}
		}
		user.setFriendIds(newfriendIds);
    	UserDAO.getInstance().updateUser(user);
    }
	
	
	public static Result registerUser(){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		if(!Utils.checkJsonInput(request())){
			Logger.info("Register User. Bad request data for register user "+request().body());
	    	return generateBadRequest("Bad input json" + request().body());
		}
		JsonNode jsonReq = request().body().asJson();
		String socialNetwork = jsonReq.get("socialNetwork").asText();
		String accessToken = jsonReq.get("accessToken").asText();
		String accessSecret = jsonReq.get("accessSecret").asText();
	
		User user = new User();
		List<String> friendIds = new ArrayList<String>();
		//user.setMobileNumber(mobileNumber);
		
		try
        {
			List<String> allFriendIds;
			if(socialNetwork.equals("facebook"))
	    	{
	    	    FacebookConfig fb = new FacebookConfig(accessToken);
	    		User user1 = UserDAO.getInstance().findUserByFB_ID(fb.getFacebookUserName());
	    		if(user1 != null){
	    			updateFriends(fb, user1);
	    			user1 = UserDAO.getInstance().findUserByFB_ID(fb.getFacebookUserName());
	    			return ok(user1.toJson());
	    		}
	    	    user.setName(fb.getFacebookName());
	    		user.setEmailId(fb.getEmailAddress());
	    		user.setFbUserName(fb.getFacebookUserName());
	    		user.setUrl(fb.getImageURL());
	    		user.setInviteCode(Utils.generateCode());
	    		allFriendIds = fb.getFriendIds();
	    	}
	    	else if(socialNetwork.equals("twitter")){
	    		TwitterConfig twitter = new TwitterConfig(accessToken, accessSecret);
	    		User user1 = UserDAO.getInstance().findUserByTwitterID(twitter.getTwitterID());
	    		if(user1 != null){
	    			updateFriends(twitter, user1);
	    			user1 = UserDAO.getInstance().findUserByTwitterID(twitter.getTwitterID());
	    			return ok(user1.toJson());
	    		}
	    		user.setName(twitter.getTwitterName());
	    		user.setEmailId(twitter.getEmailAddress());
	    		allFriendIds = twitter.getFriendIds();
		    	//friendIds = twitter.getFriendIds();
	    		user.setInviteCode(Utils.generateCode());
	    		user.setTwitterId(twitter.getTwitterID());
	    		user.setUrl(twitter.getImageURL());
	    	}
	    	else{
	    		return generateBadRequest("Social Network Not Available");
	    	}
			Activity act = new Activity();
			act.setActivityType(Activity.ActivityType.JOIN);
			act.setCreateTime(System.currentTimeMillis());
			act.setFromUser(user.getUserId());
			act.setLikableFlag(true);
			act.setLikeCount(0);
			act.setDescription(user.getName() + " has joined Trump");
			act.setCreateTime(System.currentTimeMillis());
			String actId = ActivityDAO.getInstance().insertActivity(act);
			//Add in completed challenge and activities
			List<Activity> activities = user.getActivities();
			act.setActivityId(actId);
			activities.add(act);
			user.setActivities(activities);
			String userId = UserDAO.getInstance().insertUser(user);
	    	user = UserDAO.getInstance().findUserById(userId);
	    	
	    	//usercheck.getActivities().get(usercheck.getActivities().size()-1).setFromUser(userId);
	    	//update activity with userID
	    	//update friend's friendslist with user ID
	    	Activity activity = ActivityDAO.getInstance().findActivityById(actId);
	    	activity.setFromUser(userId);
	    	ActivityDAO.getInstance().updateActivity(activity);
	    	
	    	if(socialNetwork.equals("facebook"))
	    	{
	    		for (int i = 0 ; i < allFriendIds.size() ; i++){
	    			User friend = UserDAO.getInstance().findUserByFB_ID(allFriendIds.get(i));
	    			if(friend != null){
	    				friendIds.add(allFriendIds.get(i));
	    				List<String> friendFriendIds = friend.getFriendIds();
	    				friendFriendIds.add(user.getUserId());
	    				friend.setFriendIds(friendFriendIds);
	    				UserDAO.getInstance().updateUser(friend);
	    			}
	    		}
	    	}
	    	else if(socialNetwork.equals("twitter"))
	    	{
	    		for (int i = 0 ; i < allFriendIds.size() ; i++){
    			User friend = UserDAO.getInstance().findUserByTwitterID(allFriendIds.get(i));
    			if(friend != null){
    				friendIds.add(friend.getUserId());
    				List<String> friendFriendIds = friend.getFriendIds();
    				friendFriendIds.add(user.getUserId());
    				friend.setFriendIds(friendFriendIds);
    				UserDAO.getInstance().updateUser(friend);
    				}
    			}
	    	}
	    	user.setFriendIds(friendIds);
	    	UserDAO.getInstance().updateUser(user);
	    	user = UserDAO.getInstance().findUserById(userId);
	    	Logger.info("New User has been registered : " + userId);
			return ok(user.toJson());
		} catch (Exception e) {
			Logger.info("Failed to add new user : " + e);
			return generateInternalServer("user not inserted properly" + e);
		}
	}
	
	public static Result verifyCode(String userId, String code){
		//use code to find sender and add new user to sender's friend list and add sender to new user's friend list
		return null;
	}
	
	//for twitter
	public static Result getFollowersToInvite(String userId){
		return null;
	}
	
	public static Result getLeaderboard(String userId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try {
			User user = UserDAO.getInstance().findUserById(userId);
			
			List<String> friendIds = user.getFriendIds();
			
			ArrayList<User> friends = new ArrayList<User>();
			friends.add(user);
			
			for(String friendId : friendIds){
				friends.add(UserDAO.getInstance().findUserById(friendId));
			}
			Collections.sort(friends, new Comparator<User>() {
		        @Override
				public int compare(User u1, User u2) {
					// TODO Auto-generated method stub
		        	if (u2.getAmountWon() == u1.getAmountWon())
						return 0;
		        	else if(u2.getAmountWon() > u1.getAmountWon())
		        		return 1;
		        	else if(u2.getAmountWon() < u1.getAmountWon())
		        		return -1;
		        	return 0;
				}
		    });
			ObjectNode result = Json.newObject();
			ArrayNode resultArr = new ArrayNode(JsonNodeFactory.instance);
			
			for(User friend : friends) {
				resultArr.add(friend.toJson());
			}
			result.put("LeaderBoard", resultArr);
			return ok(result);
		} catch (Exception e) {
			return generateInternalServer("Failed to retrieve FriendList " + e.getMessage());
		}
	}
	
	public static Result getUser(String userId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try {
			User user = UserDAO.getInstance().findUserById(userId);
			List<UserGiftMap> userGifts = user.getWonGifts();
			if(userGifts.size()>0 && StringUtils.isNotEmpty(user.getAccountIdModo())){
				ArrayList<String> liveGifts = ModoPayments.offerLookup(user.getAccountIdModo());
				for(UserGiftMap userGift : userGifts){
					if(liveGifts.contains(userGift.getModoGiftId())){
						userGift.setUserGiftStatus(UserGiftStatus.GIVEN);
					}
					else{
						userGift.setUserGiftStatus(UserGiftStatus.USED);
					}
				}
				user.setWonGifts(userGifts);
				UserDAO.getInstance().updateUser(user);
				user = UserDAO.getInstance().findUserById(userId);
			}
			
			
			return ok(user.toJson());
		} catch (Exception e) {
			return generateInternalServer(e.getMessage());
		}
	}
	
	//used to send gift card
	public static Result getFriends(String userId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try {
			User user = UserDAO.getInstance().findUserById(userId);

			List<String> friendIds = user.getFriendIds();
			ObjectNode result = Json.newObject();
			ArrayNode resultArr = new ArrayNode(JsonNodeFactory.instance);
			
			for(int i = 0 ; i < friendIds.size() ; i++) {
				User user1 = UserDAO.getInstance().findUserById(friendIds.get(i));
				resultArr.add(user1.toJson());
			}
			result.put("Friends", resultArr);
			return ok(result);
		} catch (Exception e) {
			return generateInternalServer(e.getMessage());
		}
	}
	
	public static Result addToWaitList(String userId){
		return null;
	}
}
