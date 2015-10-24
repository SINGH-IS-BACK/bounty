package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import util.Constants;
import util.Utils;
import walletManager.ModoPayments;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dao.ActivityDAO;
import dao.ChallengeDAO;
import dao.GiftDAO;
import dao.MerchantDAO;
import dao.SmallClusterDAO;
import dao.UserDAO;
import entity.Activity;
import entity.Challenge;
import entity.ChallengeStep;
import entity.Comment;
import entity.Gift;
import entity.Gift.GiftStatus;
import entity.Location;
import entity.Merchant;
import entity.SmallCluster;
import entity.User;
import entity.UserGiftMap;

public class ChallengeController extends BaseController{

	public static String QRCodes[] ={ "UkwtMTAwMQ==", "UkwtMTAwMg==" ,"UkwtMTAwMw==",
		"UkwtMTAwNA==","UkwtMTAwNQ==","UkwtMTAwNg==","UkwtMTAwNw==","UkwtMTAwOA==","UkwtMTAwOQ==",
		"UkwtMTAxMA==","UkwtMTAxMQ==","UkwtMTAxMg==","UkwtMTAxMw==","UkwtMTAxNA==","UkwtMTAxNQ==",
		"UkwtMTAxNg==","UkwtMTAxNw==","UkwtMTAxOA==","UkwtMTAxOQ==","UkwtMTAyMA==","UkwtMTAyMQ==",
		"UkwtMTAyMg==","UkwtMTAyMw==","UkwtMTAyNA==","UkwtMTAyNQ==","UkwtMTAyNg==","UkwtMTAyNw==",
		"UkwtMTAyOA==","UkwtMTAyOQ==","UkwtMTAzMA==" };
	

	static int uniqueNum = 0;
	public static int generateSeqNumber(){
		 return uniqueNum++;
	}
	
	
	
	
	public static Result deleteChallengeVirtually(Challenge challenge){
		try{
			Logger.info("Deleting Challenge Virtually" + challenge.getChallengeId());
			ArrayList<SmallCluster> smallClusters = SmallClusterDAO.getInstance().getAllSmallClusters();
			for(SmallCluster smallCluster : smallClusters){
				smallCluster.setChallenges(Challenge.remove(smallCluster.getChallenges(), challenge.getChallengeId()));
				if(smallCluster.getChallenges().size() == 0){
					SmallCluster.deleteSmallCluster(smallCluster);
				}else{
					SmallClusterDAO.getInstance().updateSmallCluster(smallCluster);
				}
			}
			Logger.info("Challenge Virtually Deleted" + challenge.getChallengeId());
			return ok();
		}
		catch(Exception e){
			Logger.warn("Delete Challenge failed" + challenge.getChallengeId() + "  Reason :"+ e.getMessage());
			return generateInternalServer("Delete Challenge failed \n" + e.getMessage());
		}
	}
	
	public static Result deleteChallenge(String challengeId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try{
			Logger.info("Deleting Challenge" + challengeId);
			Challenge challenge = ChallengeDAO.getInstance().findChallengeById(challengeId);
			deleteChallengeVirtually(challenge);
			ChallengeDAO.getInstance().deleteChallengeById(challengeId);
			Logger.info("Challenge Deleted" + challengeId);
			return ok();
		}
		catch(Exception e){
			return generateInternalServer("Delete Challenge failed \n" + e.getMessage());
		}
	}
	
	//get nearby challenges
	/*
	public static Result getNearbyChallenges(String userId, int start, int count) {
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		
		int startC = 0;
		try {
			User user = UserDAO.getInstance().findUserById(userId);
			List<Challenge> challenges = ChallengeDAO.getInstance().getAllActiveChallenges();
			ObjectNode result = Json.newObject();
			ArrayNode resultArr = new ArrayNode(JsonNodeFactory.instance);
			for(Challenge challenge: challenges){
				if(!checkValidLocalChallenge(challenge, user)){
					continue;
				}
				if(start > 0 && startC<start){
					startC++;
				}else{
					resultArr.add(challenge.toJson(userId));
				}
				if(count != -1 && resultArr.size()==count){
					break;
				}
			}
			result.put("challenges", resultArr);
			return ok(result);
		} catch (Exception e) {
			return generateInternalServer(e.getMessage());
		}
	}
	
	public static Result getAllChallenges(int start, int count) {
		if(!Utils.checkJsonInput(request())){
			Logger.info("Bad request data for watch request "+request().body());
			return generateBadRequest("Bad input json");
		}
		JsonNode jsonReq = request().body().asJson();
		try {
			String userId = Utils.safeStringFromJson(jsonReq,"userId");
			List<Challenge> challenges = ChallengeDAO.getInstance().getAllActiveChallenges();
			ObjectNode result = Json.newObject();
			ArrayNode resultArr = new ArrayNode(JsonNodeFactory.instance);
			for(Challenge challenge: challenges){
				resultArr.add(challenge.toJson(userId));
			}
			result.put("challenges", resultArr);
			return ok(result);
		} catch (Exception e) {
			return generateInternalServer(e.getMessage());
		}
	}
	*/
	
	public static Result getAllChallenges() {
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try {
			Logger.info("Get All Challenges");
			List<Challenge> challenges = ChallengeDAO.getInstance().getAllActiveChallenges();
			ObjectNode result = Json.newObject();
			ArrayNode resultArr = new ArrayNode(JsonNodeFactory.instance);
			for(Challenge challenge: challenges){
				resultArr.add(challenge.toJson(""));
			}
			result.put("challenges", resultArr);
			return ok(result);
		} catch (Exception e) {
			return generateInternalServer(e.getMessage());
		}
	}
	
	public static boolean checkValidLocalChallenge(Challenge challenge, User user){
		Gift gift = challenge.getGift();
		if(gift.getMaxGifts() <= gift.getGivenGiftCount() || gift.getGiftStatus() == GiftStatus.CLOSED || challenge.IsExpired()){
			return false;
		}
		if(challenge.getEndDate() != 0 && challenge.getEndDate() < System.currentTimeMillis()) {
			challenge.IsExpired(true);
			ChallengeDAO.getInstance().updateChallenge(challenge);
			deleteChallengeVirtually(challenge);
			return false;
		}
		if(challenge.getStartDate()!=0 && challenge.getStartDate() > System.currentTimeMillis()) {
			return false;
		}
		if(gift.getGiftStatus() != Gift.GiftStatus.FUNDED){
			return false;	
		}
		return true;
	}
	
	public static Result validateChallenge(String challengeId, String userId) {
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try {
			Challenge ch = ChallengeDAO.getInstance().findChallengeById(challengeId);
			System.out.println(ch.getEndDate());
			System.out.println(System.currentTimeMillis());
			Gift gift = ch.getGift();
			if(gift.getMaxGifts() <= gift.getGivenGiftCount() || gift.getGiftStatus() == GiftStatus.CLOSED || ch.IsExpired()){
				return generateBadRequest("The gift card for these challenges are Over. Keep Calm and Take another Challenge");
			}
			if(ch.getEndDate() != 0 && ch.getEndDate() < System.currentTimeMillis()) {
				ch.IsExpired(true);
				ChallengeDAO.getInstance().updateChallenge(ch);
				deleteChallengeVirtually(ch);
				return generateBadRequest("The Date for the challenge has Expired. Keep Calm and Take another Challenge");
			}
			if(ch.getStartDate() > System.currentTimeMillis()) {
				return generateBadRequest("The Challenge has not yet started. Keep Calm and Take another Challenge");
			}
			if(gift.getGiftStatus() != Gift.GiftStatus.FUNDED){
				return generateBadRequest("The gift for this Challenge is not Funded. Keep Calm and Take another Challenge");	
			}
			
			User user = UserDAO.getInstance().findUserById(userId);
			Set<String> completedChallenges = user.getCompletedChallengeIds();
			for(String challenge : completedChallenges) {
				if(challenge.equals(challengeId)){
					return generateBadRequest("Challenge already completed");
				}
			}
			return ok(gift.toJson());
		} catch (Exception e) {
			return generateBadRequest("invalid userid or challengeId" + e);
		}
	}
	
	public static Result finishChallenge(String challengeId, String userId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try {
			Logger.info("Finish Challenge ChallengeId : " + challengeId + "User Id : " + userId);
			Challenge ch = ChallengeDAO.getInstance().findChallengeById(challengeId);
			Gift gift = ch.getGift();
			User user = UserDAO.getInstance().findUserById(userId);
			if(!checkValidLocalChallenge(ch,user)){
				return generateBadRequest("This Challenge is Over. Keep Calm and Take another Challenge");
			}
			Set<String> completedChallenges = user.getCompletedChallengeIds();
			for(String challenge : completedChallenges){
				if(challenge.equals(challengeId)){
					return generateBadRequest("Challenge already completed");
				}
			}
			ArrayList<String> GiftIdUserId;
			try{
				if(StringUtils.isNotEmpty(gift.getMerchantId())){
					Merchant merchant = MerchantDAO.getInstance().findMerchantById(gift.getMerchantId());
					GiftIdUserId = ModoPayments.sendGift(String.valueOf(gift.getAmount()),user.getEmailId(), merchant.getMerchantIdModo());
				}
				else{
					GiftIdUserId = ModoPayments.sendGift(String.valueOf(gift.getAmount()),user.getEmailId(), gift.getMerchantId());
				}
			}
			catch(Exception e){
				Logger.error("Failed to do send Gift Card from Modo. ChallengeId : " + challengeId + "User Id : " + userId);
				throw new Exception("Failed to do send Gift Card from Modo. Reason: " + e.getMessage());
			}
			String modoGiftId = GiftIdUserId.get(0);
			String accId = GiftIdUserId.get(1);
			//set modo acc id
			//String accId = ModoPayments.getCardsUsers(user.getMobileNumber());
			user.setAccountIdModo(accId);
			//add to won gifts
			UserGiftMap ug = new UserGiftMap();
			ug.setGiftId(gift.getGiftId());
			ug.setModoGiftId(modoGiftId);
			ug.setRemainingAmount(gift.getAmount());
			ug.setUserGiftStatus(UserGiftMap.UserGiftStatus.GIVEN);
			user.getWonGifts().add(ug);
			user.setAmountWon(user.getAmountWon() + gift.getAmount());
			//add activity
			Activity act = new Activity();
			act.setActivityType(Activity.ActivityType.WIN_GIFT);
			act.setCreateTime(System.currentTimeMillis());
			act.setFromUser(user.getUserId());
			act.setLikableFlag(true);
			act.setLikeCount(0);
			act.setDescription(user.getName() + " completed " + ch.getTitle() + " and won $" +gift.getAmount());
			String actId = ActivityDAO.getInstance().insertActivity(act);
			//Add in completed challenge and activities
			List<Activity> activities = user.getActivities();
			act.setActivityId(actId);
			activities.add(act);
			Set<String> completed = user.getCompletedChallengeIds();
			completed.add(challengeId);
			UserDAO.getInstance().updateUser(user);
			//update given gift count
			int c = gift.getGivenGiftCount();
			c++;
			gift.setGivenGiftCount(c);
			GiftDAO.getInstance().updateGift(gift);
			if(gift.getMaxGifts() == gift.getGivenGiftCount()){
				gift.setGiftStatus(GiftStatus.CLOSED);
				GiftDAO.getInstance().updateGift(gift);
				for(Challenge challenge : ChallengeDAO.getInstance().getAllActiveChallenges()){
					if(challenge.getGift().getGiftId().equals(gift.getGiftId())){
						challenge.IsExpired(true);
						ChallengeDAO.getInstance().updateChallenge(challenge);
						deleteChallengeVirtually(challenge);
					}
				}
				//updateChallenge(ch, false);
			}
			
			return ok(gift.toJson());
		} catch (Exception e) {
			Logger.warn("Finish Challenge FAILED. ChallengeId : " + challengeId + "User Id : " + userId);
			return generateBadRequest("invalid userid or challengeId" + e);
		}
	}

	/*
	public static Result expireChallenge(String challengeId){
		try{
			Challenge challenge = ChallengeDAO.getInstance().findChallengeById(challengeId);
			challenge.IsExpired(true);
			ChallengeDAO.getInstance().updateChallenge(challenge);
			deleteChallengeVirtually(challenge);
			return ok();
		}
		catch(Exception e){
			return badRequest();
		}
	}*/
	
	public static Result getChallenge(String challengeId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		Challenge chAdded;
		try {
			chAdded = ChallengeDAO.getInstance().findChallengeById(challengeId);
			return ok(chAdded.toJson(""));
		} catch (Exception e) {
			return generateBadRequest("invalid challenge id");
		}
	}
	
	public static Result likeChallenge(String challengeId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		
		//get that challenge
		//add like 
		//add user who liked
		//save challenge
		//add activity
		if(!Utils.checkJsonInput(request())){
			return generateBadRequest("Bad input json");
		}
		JsonNode jsonReq = request().body().asJson();
		Challenge challenge;
		User user;
		try {
			challenge = ChallengeDAO.getInstance().findChallengeById(challengeId);
		} catch (Exception e1) {
			return generateBadRequest("invalid challenge id");
		}
		
		String userId = "";
		try{
			userId = Utils.safeStringFromJson(jsonReq,"userId");
			Logger.info("Like Challenge ChallengeId : " + challengeId + "User Id : " + userId);
			user = UserDAO.getInstance().findUserById(userId);
		} catch (Exception e1) {
			return generateBadRequest("invalid user id " + userId);
		}
		List<String> likedByList = challenge.getLikedBy();
		try {
			if(likedByList.contains(userId)){
				likedByList.remove(userId);
				challenge.setLikedBy(likedByList);
				ChallengeDAO.getInstance().updateChallenge(challenge);
				challenge = ChallengeDAO.getInstance().findChallengeById(challengeId);
				return ok(challenge.toJson(userId));
			}
			else{	
				likedByList.add(userId);
				challenge.setLikedBy(likedByList);
				ChallengeDAO.getInstance().updateChallenge(challenge);
				challenge = ChallengeDAO.getInstance().findChallengeById(challengeId);
			}
		} catch (Exception e) {
			return generateInternalServer("challenge not inserted properly");
		}
		try{
		Activity act = new Activity();
		act.setActivityType(Activity.ActivityType.LIKE);
		act.setCreateTime(System.currentTimeMillis());
		act.setFromUser(user.getUserId());
		act.setLikableFlag(false);
		act.setLikeCount(0);
		act.setDescription(user.getName() + " liked " + challenge.getTitle());
		String actId = ActivityDAO.getInstance().insertActivity(act);
		//Add in completed challenge and activities
		List<Activity> activities = user.getActivities();
		act.setActivityId(actId);
		activities.add(act);
		UserDAO.getInstance().updateUser(user);
		} catch (Exception e) {
			return generateInternalServer("activity not tracked");
		}
		return ok(challenge.toJson(userId));
		
	}
	
	public static Result addCommentOnChallenge(String challengeId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		//get that challenge
		//add comment
		//save challenge
		if(!Utils.checkJsonInput(request())){
			return generateBadRequest("Bad input json");
		}
		JsonNode jsonReq = request().body().asJson();
		Challenge challenge;
		try {
			challenge = ChallengeDAO.getInstance().findChallengeById(challengeId);
		} catch (Exception e1) {
			return generateBadRequest("invalid challenge id");
		}
		Comment comment = new Comment();
		String userId = "";
		User user = new User();
		try {
			comment.setText(Utils.safeStringFromJson(jsonReq,"text"));
			comment.setTime(System.currentTimeMillis());
			userId = Utils.safeStringFromJson(jsonReq,"userId");
			Logger.info("Add Challenge ChallengeId : " + challengeId + "User Id : " + userId);
			user = UserDAO.getInstance().findUserById(userId);
		} catch (Exception e1) {
			return generateBadRequest("invalid user id");
		}
		comment.setUserId(userId);
		List<Comment> comments = challenge.getComments();
		comments.add(comment);
		challenge.setComments(comments);
		ChallengeDAO.getInstance().updateChallenge(challenge);
		try {
			challenge = ChallengeDAO.getInstance().findChallengeById(challengeId);
		} catch (Exception e) {
			return generateInternalServer("challenge not inserted properly");
		}
		
		try{
		Activity act = new Activity();
		act.setActivityType(Activity.ActivityType.COMMENT);
		act.setCreateTime(System.currentTimeMillis());
		act.setFromUser(user.getUserId());
		act.setLikableFlag(false);
		act.setLikeCount(0);
		act.setDescription(user.getName() + " commented on " + challenge.getTitle());
		String actId = ActivityDAO.getInstance().insertActivity(act);
		//Add in completed challenge and activities
		List<Activity> activities = user.getActivities();
		act.setActivityId(actId);
		activities.add(act);
		UserDAO.getInstance().updateUser(user);
		} catch (Exception e) {
			return generateInternalServer("activity not tracked");
		}
		return ok(challenge.toJson(userId));
	}

	public static Result addChallenge(String merchantId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		if(!Utils.checkJsonInput(request())){
			return generateBadRequest("Bad input json");
		}
		JsonNode jsonReq = request().body().asJson();
		Merchant merchant = new Merchant();
		Gift gift = new Gift();
		Challenge ch = new Challenge();
		try{
			String giftId = Utils.safeStringFromJson(jsonReq,"giftId");
			Logger.info("Adding Challenge. merchantId : " + merchantId + "  giftId : " + giftId);
			gift = GiftDAO.getInstance().findGiftById(giftId);
			ch.setGift(gift);
			ch.setShareRequired(true);
			ch.setPicRequired(true);
			
		}catch (Exception e1) {
			return generateBadRequest("invalid gift id");
		}
		try {
			//if(gift.getMerchantId() != "" && !gift.getMerchantId().equals(merchantId)){
			//	return generateBadRequest("Merchant ID : "+merchantId+" and the gift Merchant's ID : "+gift.getMerchantId()+" mismatch"); 
			//}
			merchant = MerchantDAO.getInstance().findMerchantById(merchantId);
		} catch (Exception e1) {
			return generateBadRequest("invalid merchant id");
		}
		Location center = new Location();
		try{
			ch.setTitle(jsonReq.get("title").asText());
			ch.setDescription(jsonReq.get("description").asText());
			ch.IsExpired(false);
			long startDate = Utils.safeLongFromJson(jsonReq, "startEpoch");
			if(startDate != 0){
				ch.setStartDate(startDate);
			}
			long endDate = Utils.safeLongFromJson(jsonReq, "endEpoch");
			if(endDate != 0){
				ch.setEndDate(endDate);
			}
			center.setLatitude(Utils.safeDoubleFromJson(jsonReq, "latitude"));
			center.setLongitude(Utils.safeDoubleFromJson(jsonReq, "longitude"));
		} catch (Exception e) {
			return generateBadRequest(e.toString());
		}
	
		ch.setCenter(center);
		ch.setLocalFlag(true);
		ch.setType(Challenge.ChallengeType.QRCode);
		ch.setEnableFlag(true);
		ch.setQRCode(Utils.generateBigCode());
		ch.setMerchantId(merchantId);
		String chId = "";
		try {
			List<ChallengeStep> steps = new ArrayList<ChallengeStep>();
			ChallengeStep challengeStep1 = new ChallengeStep();
			challengeStep1.setStepDesc(Utils.safeStringFromJson(jsonReq,"step1Desc"));
			challengeStep1.setHeading("Scan QR Code - CheckIn");
			challengeStep1.setImageURL("");
			challengeStep1.setType(ChallengeStep.StepType.QRCODE);
			steps.add(challengeStep1);
			
			ChallengeStep challengeStep2 = new ChallengeStep();
			challengeStep2.setStepDesc(Utils.safeStringFromJson(jsonReq,"step2Desc"));
			challengeStep2.setHeading("Take Your Picture");
			challengeStep2.setImageURL("");
			challengeStep2.setType(ChallengeStep.StepType.PIC);
			steps.add(challengeStep2);
			
			ChallengeStep challengeStep3 = new ChallengeStep();
			challengeStep3.setStepDesc(Utils.safeStringFromJson(jsonReq,"step3Desc"));
			String share = Utils.safeStringFromJson(jsonReq,"step3_share");
			challengeStep3.setHeading("Share");
			if(share.equals("Facebook")) {
				challengeStep3.setType(ChallengeStep.StepType.SHARE_FACEBOOK);
			}
			else if(share.equals("Twitter")) {
				challengeStep3.setType(ChallengeStep.StepType.SHARE_TWITTER);
			}
			else if(share.equals("Both")) {
				challengeStep3.setType(ChallengeStep.StepType.SHARE_BOTH);
			}
			else {
				challengeStep3.setType(ChallengeStep.StepType.SHARE_ANY);
			}
			challengeStep3.setImageURL("");
			steps.add(challengeStep3);
			
			//steps.add(Utils.safeStringFromJson(jsonReq,"step1Desc"));
			//steps.add(Utils.safeStringFromJson(jsonReq,"step2Desc"));
			//steps.add(Utils.safeStringFromJson(jsonReq,"step3Desc"));
			ch.setSteps(steps);
			ch.setChallengeImgUrl(Utils.safeStringFromJson(jsonReq,"imgUrl"));
			ch.setDisclaimer(Utils.safeStringFromJson(jsonReq,"disclaimer"));
			chId = ChallengeDAO.getInstance().insertChallenge(ch);
			Logger.info("Challenge Added ChallengeId : " + chId);
		} catch (Exception e) {
			return generateBadRequest("failed to add challenge" + e);
		}
		
		try {
			Challenge chAdded = ChallengeDAO.getInstance().findChallengeById(chId);
			merchant.getChallenges().add(chAdded);
			MerchantDAO.getInstance().updateMerchant(merchant);
			
			//if no small cluster include this challenge add a new small cluster
			if( updateSmallClustersContainingChallenge(chAdded) == 0){
				SmallCluster.addSmallCluster(chAdded);
			}

			String userId = "";
			return ok(chAdded.toJson(userId));
		} catch (Exception e) {
			return generateBadRequest("failed to add challenge in cluster " + e);
		}
	}
	
	public static int updateSmallClustersContainingChallenge(Challenge challenge) throws Exception{
		int numberOfClusterhavingChallenge = 0;
		ArrayList<SmallCluster> smallClusters = SmallClusterDAO.getInstance().getAllSmallClusters();
		for(SmallCluster smallCluster : smallClusters){
			if(smallCluster.getCenter().distance(challenge.getCenter()) < Constants.SMALLCLUSTER_RADIUS){
				numberOfClusterhavingChallenge++;
				smallCluster.getChallenges().add(challenge);
				SmallClusterDAO.getInstance().updateSmallCluster(smallCluster);
			}
		}
		Logger.info("numberOfClusterhavingChallenge" + numberOfClusterhavingChallenge);
		return numberOfClusterhavingChallenge;
	}
	
}
