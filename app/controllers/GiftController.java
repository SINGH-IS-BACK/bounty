package controllers;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import util.Authentication;
import util.EmailService;
import util.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dao.GiftDAO;
import dao.MasterUserDAO;
import dao.MerchantDAO;
import dao.UserDAO;
import entity.Gift;
import entity.Gift.GiftStatus;
import entity.User;
import entity.UserGiftMap;

public class GiftController extends BaseController{

	public static Result getGiftImage(String giftId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try{
			Gift gift = GiftDAO.getInstance().findGiftById(giftId);
			String merchantURL = MerchantDAO.getInstance().getMerchantURLLogo(gift.getMerchantId());
			return ok(merchantURL);
		}catch (Exception e) {
			return generateBadRequest("Invalid giftID");
		}
	}
	
	public static Result addAccessToken() {
		String key = Utils.generateBigCode();
		String secret = Utils.generateBigCode();
		String accessToken = Authentication.calculateHMAC(key, secret);
		
		try{
			MasterUserDAO.getInstance().addAccessToken(key, accessToken);
			ObjectNode result = Json.newObject();
			result.put("API-Key", key);
			result.put("API-Secret", secret);
			result.put("APi-Token", accessToken);
			return ok(result);
		}
		catch(Exception e){
			return generateBadRequest("Failed to add a new access Token");
		}
	}
	
	public static Result getAccessToken(){
		if(!Utils.checkJsonInput(request())){
			Logger.info("Access Token : Bad request data for watch request "+request().body());
			return generateBadRequest("Bad input json");
		}
		JsonNode jsonReq = request().body().asJson();
		String key = jsonReq.get("API-Key").asText();
		String secret = jsonReq.get("API-Secret").asText();
		String accessToken = MasterUserDAO.getInstance().findKey(key);
		if(accessToken == null)
			return generateBadRequest("Kindly provide a valid Key");
		if(accessToken.equals(Authentication.calculateHMAC(key, secret))){
			ObjectNode result = Json.newObject();
			result.put("API-Token", accessToken);
			return ok(result);
		}
		else{
			return generateBadRequest("Invalid Key Secret combination");
		}
	}
	
	public static Result addGiftCard(){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		if(!Utils.checkJsonInput(request())){
			Logger.info("Add Gift Card : Bad request data for watch request "+request().body());
			return generateBadRequest("Bad input json");
		}
		Logger.info("Adding Gift Card");
		JsonNode jsonReq = request().body().asJson();
		Gift gift = new Gift();
		gift.setGiftName(jsonReq.get("giftName").asText());
		gift.setAmount(jsonReq.get("amount").asDouble());
		gift.setDescription(jsonReq.get("description").asText());
		gift.setMaxGifts(jsonReq.get("maxGift").asInt());
		//gift.setGiftLogoUrl(jsonReq.get("giftUrl").asText());
		gift.setGiftStatus(GiftStatus.ADDED);
		gift.setGivenGiftCount(0);
		String merchantId = "";
		try {
				merchantId = Utils.safeStringFromJson(jsonReq,"merchantId");
				if(StringUtils.isNotEmpty(merchantId)){			
					MerchantDAO.getInstance().findMerchantById(merchantId);
					gift.setMerchantId(merchantId);
					gift.setGiftLogoUrl(MerchantDAO.getInstance().getMerchantURLLogo(gift.getMerchantId()));
				}
			} catch (Exception e) {
				return generateBadRequest("Invalid merchant Id");
			}
		
		String giftId = GiftDAO.getInstance().insertGift(gift);
		try {
			gift = GiftDAO.getInstance().findGiftById(giftId);
			Logger.info("Gift Card Added. GiftId" + giftId);
			return ok(gift.toJson());
		} catch (Exception e) {
			Logger.info("Gift Card Not Inserted Properly. Reason" + e.getMessage());
			return generateInternalServer("gift not inserted properly");
		}
	}
	
	public static Result getAllGifts(){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try {
			List<Gift> gifts = GiftDAO.getInstance().getAllGifts();
			ObjectNode result = Json.newObject();
			ArrayNode resultArr = new ArrayNode(JsonNodeFactory.instance);
			for(Gift gift: gifts){
				resultArr.add(gift.toJson());
			}
			result.put("gifts", resultArr);
			return ok(result);
		} catch (Exception e) {
			return generateInternalServer(e.getMessage());
		}
	}
	
	public static Result getGift(String giftId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try{
			Gift gift = GiftDAO.getInstance().findGiftById(giftId);
			return ok(gift.toJson());
		} catch(Exception e) {
			return generateInternalServer(e.getMessage());
		}
	}
	
	public static Result updateGift(String giftId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		if(!Utils.checkJsonInput(request())){
			Logger.info("Bad request data for Invite Code "+request().body());
			return generateBadRequest("Bad input json" + request().body());
		}
		Logger.info("Updating Gift Card. GiftId" + giftId);
		Gift gift;
		String giftStatus = "";
		try{
			gift = GiftDAO.getInstance().findGiftById(giftId);
			JsonNode jsonReq = request().body().asJson();
			giftStatus = jsonReq.get("giftStatus").asText();
			gift.setGiftStatus(Gift.GiftStatus.valueOf(giftStatus));
			GiftDAO.getInstance().updateGift(gift);
			Logger.info("Gift Card Updated. GiftId" + giftId);
		}
		catch(Exception e){
			Logger.info("Gift Card Update Failed. GiftId" + giftId);
			return generateBadRequest("Invalid Gift Status. Details:" + e);
		}
		// send email to merchant who has challenge with this gift card.
		try{
			if(giftStatus.equals("FUNDED")){
				EmailService.sendMail(giftId);
			}
		}
		catch(Exception e){
			Logger.error("Failed to send Email. GiftId" + giftId);
		}
		return ok(gift.toJson());
	}
	
	public static Result sendMailToMerchant(String giftId){
		try{
			EmailService.sendMail(giftId);
		}catch(Exception e){
			Logger.error("Failed to send Email. GiftId" + giftId);
		}
		return ok();
	}
	
	public static Result getUserGifts(String userId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try {
			User user = UserDAO.getInstance().findUserById(userId);
			ObjectNode result = Json.newObject();
			ArrayNode resultArr = new ArrayNode(JsonNodeFactory.instance);
			for(UserGiftMap userGiftMap : user.getWonGifts())
				resultArr.add(GiftDAO.getInstance().findGiftById(userGiftMap.getGiftId()).toJson());
			
			result.put("gifts", resultArr);
			return ok(result);
		} catch (Exception e) {
			return generateInternalServer(e.getMessage());
		}
	}
}
