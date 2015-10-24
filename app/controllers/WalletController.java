package controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import util.Utils;
import walletManager.ModoPayments;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.ActivityDAO;
import dao.GiftDAO;
import dao.MasterUserDAO;
import dao.MerchantDAO;
import dao.UserDAO;
import entity.Activity;
import entity.Gift;
import entity.Merchant;
import entity.User;
import entity.UserGiftMap;

public class WalletController extends BaseController{
	
		public static Result getCallBack(){
			ActivityDAO.getInstance().dumpData("getCallBack called");
			return ok();
		}
	
	public static Result callBack(){
		ActivityDAO.getInstance().dumpData("callBack called");
		Logger.info("Call Back "+request().body());
		ActivityDAO.getInstance().dumpData(request().body().toString());
		return ok();
	}
	public static Result getCheckoutCode(String userId, String giftId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try {
			Logger.info("Get Checkout Code. GiftId" + giftId + "  UserId : " + userId);
			User user = UserDAO.getInstance().findUserById(userId);
			Gift gift = GiftDAO.getInstance().findGiftById(giftId);
			List<UserGiftMap> userGifts = user.getWonGifts();
			String giftModoId = "";
			for(UserGiftMap userGift : userGifts){
				if(userGift.getGiftId().equals(giftId))
					giftModoId = userGift.getModoGiftId();
			}
			if(!StringUtils.isNotEmpty(giftModoId)){
				return generateBadRequest("Gift doesn't belong to this user");
			}
			Merchant merchant = MerchantDAO.getInstance().findMerchantById(gift.getMerchantId());
			if(!StringUtils.isNotEmpty(merchant.getMerchantIdModo())){
				Logger.error("Gift Card cannot be redeemed. No merchant ModoId. GiftId : " + giftId );
				return generateBadRequest("This gift can't be reedemed. Sorry for inconvenience");
			}
			String[] res = ModoPayments.visit(user.getAccountIdModo(), merchant.getMerchantIdModo(), giftModoId);
			ObjectNode result = Json.newObject();
			result.put("barcode", res[0]);
			result.put("checkoutCode", res[1]);
			return ok(result);
		} catch (Exception e) {
			return generateInternalServer(e.getMessage());
		}
	}
	
	public static Result redeemGiftCard(String userId, String giftId, String checkoutCode){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try {
			User user = UserDAO.getInstance().findUserById(userId);
			List<UserGiftMap> newWonList = new ArrayList<UserGiftMap>(); 
			for(UserGiftMap ug: user.getWonGifts()){
				if(!ug.getGiftId().equalsIgnoreCase(giftId)){
					newWonList.add(ug);
				}
			}
			user.setWonGifts(newWonList);
			UserDAO.getInstance().updateUser(user);
			ModoPayments.checkout(user.getAccountIdModo(), checkoutCode);
			return generateOkTrue();
		} catch (Exception e) {
			return generateInternalServer(e.getMessage());
		}
	}
	
	public static Result sendGiftCardFromOneUserToOther(String userId, String receiverId, String giftId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try {
			User sender = UserDAO.getInstance().findUserById(userId);
			User receiver = UserDAO.getInstance().findUserById(receiverId);
			Gift gift = GiftDAO.getInstance().findGiftById(giftId);
			//remove the gift from sender
			List<UserGiftMap> newWonList = new ArrayList<UserGiftMap>(); 
			String modoGiftId = "";
			for(UserGiftMap ug: sender.getWonGifts()){
				if(!ug.getGiftId().equalsIgnoreCase(giftId)){
					newWonList.add(ug);
				}else{
					modoGiftId = ug.getModoGiftId();
				}
			}
			sender.setWonGifts(newWonList);
			//delete gift
			ModoPayments.removeGift(modoGiftId, sender.getAccountIdModo());
			//add new gift
			ArrayList<String> GiftIdUserId;
			try{
				if(StringUtils.isNotEmpty(gift.getMerchantId())){
					Merchant merchant = MerchantDAO.getInstance().findMerchantById(gift.getMerchantId());
					GiftIdUserId = ModoPayments.sendGift(String.valueOf(gift.getAmount()),receiver.getEmailId(), merchant.getMerchantIdModo());
				}
				else{
					GiftIdUserId = ModoPayments.sendGift(String.valueOf(gift.getAmount()),receiver.getEmailId(), gift.getMerchantId());
				}
			}
			catch(Exception e){
				throw new Exception("Failed to do send Gift Card from Modo. Reason: " + e.getMessage());
			}
			
			modoGiftId = GiftIdUserId.get(0);
			String accId = GiftIdUserId.get(1);
			
			//set modo acc id of receiver
			//String accId = ModoPayments.getCardsUsers(receiver.getMobileNumber());
			receiver.setAccountIdModo(accId);
			//add to receiver won gifts
			UserGiftMap ug = new UserGiftMap();
			ug.setGiftId(gift.getGiftId());
			ug.setModoGiftId(modoGiftId);
			ug.setRemainingAmount(gift.getAmount());
			ug.setUserGiftStatus(UserGiftMap.UserGiftStatus.GIVEN);
			receiver.getWonGifts().add(ug);
			//add activity
			Activity act = new Activity();
			act.setActivityType(Activity.ActivityType.SEND_GIFT);
			act.setCreateTime(System.currentTimeMillis());
			act.setFromUser(sender.getName());
			act.setLikableFlag(true);
			act.setLikeCount(0);
			act.setDescription(sender.getName() + " sent $" +gift.getAmount() +" to " +receiver.getName());
			String actId = ActivityDAO.getInstance().insertActivity(act);
			List<Activity> activities = sender.getActivities();
			act.setActivityId(actId);
			activities.add(act);
			UserDAO.getInstance().updateUser(sender);
		} catch (Exception e) {
			return generateBadRequest("Invalid sender Id or receiver Id. Details:" + e.getMessage());
		}
		return generateOkTrue();
	}
	
	public static Result getMyGifts(String userId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try {
			User user = UserDAO.getInstance().findUserById(userId);
			ObjectNode result = Json.newObject();
			ArrayNode resultArr = new ArrayNode(JsonNodeFactory.instance);
			for(UserGiftMap gu : user.getWonGifts()){
				Gift gift = GiftDAO.getInstance().findGiftById(gu.getGiftId());
				resultArr.add(gift.toJson());
			}
			result.put("gifts", resultArr);
			return ok(result);
		} catch (Exception e) {
			return generateInternalServer(e.getMessage());
		}
	}
	
	public static Result generateToken(){
		String token1 = ModoPayments.getToken();
		Logger.info("Token1-->" + token1);
		//UserDAO.getInstance().addToken(token);
		String token = MasterUserDAO.getInstance().getToken();
		Logger.info("Token-->" + token);
		return generateOkTrue();
	}

}
