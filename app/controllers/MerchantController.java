package controllers;

import java.util.List;


import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import util.Utils;
import walletManager.ModoPayments;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dao.MerchantDAO;
import entity.Merchant;

public class MerchantController extends BaseController{
	
	private static final String MERCHANTS_TAG = "merchants";

	public static Result syncModoMerchants(){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		JsonNode merchantsObj = ModoPayments.getMerchantList();
		ArrayNode merchants = (ArrayNode)merchantsObj.get("response_data");
		for(JsonNode merchantObj: merchants){
			Merchant merchant = new Merchant();
			String merchantName = merchantObj.get("merchant_name").asText();
			merchant.setMerchantName(merchantName);
			String merchantModoId = merchantObj.get("merchant_id").asText();
			merchant.setMerchantIdModo(merchantModoId);
			if(!MerchantDAO.getInstance().IsModoMerchantAlreadyAdded(merchantModoId)){				
				MerchantDAO.getInstance().insertMerchant(merchant);
			}
		}
		return generateOkTrue();
	}
	
	public static Result updateMerchant(String merchantId){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		if(!Utils.checkJsonInput(request())){
			Logger.info("Bad request data for watch request "+request().body());
			return generateBadRequest("Bad input json");
		}
		JsonNode jsonReq = request().body().asJson();
		Merchant merchant = new Merchant();
		try {
			merchant = MerchantDAO.getInstance().findMerchantById(merchantId);
			String url = jsonReq.get("logo").asText();
			merchant.setMerchantLogoUrl(url);
			MerchantDAO.getInstance().updateMerchant(merchant);
			merchant = MerchantDAO.getInstance().findMerchantById(merchantId);
		} catch (Exception e) {
			generateInternalServer(e.getMessage());
		}
		return ok(merchant.toJson());
	}
	
	public static Result addNewMerchant(){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		if(!Utils.checkJsonInput(request())){
			Logger.info("Bad request data for watch request "+request().body());
			return generateBadRequest("Bad input json");
		}
		JsonNode jsonReq = request().body().asJson();
		Merchant merchant = new Merchant();
		merchant.setMerchantName(jsonReq.get("merchantName").asText());
		merchant.setMerchantLogoUrl(jsonReq.get("logo").asText());
		merchant.setMerchantAddress(jsonReq.get("address").asText());
		merchant.setPhone(jsonReq.get("phone").asText());
		merchant.setEmail(jsonReq.get("email").asText());
		ObjectNode result = Json.newObject();
		try {
			String merchantId = MerchantDAO.getInstance().IsMerchantAlreadyAdded(merchant.getMerchantName());
			if(!StringUtils.isEmpty(merchantId)){
				merchant.setMerchantId(merchantId);
				MerchantDAO.getInstance().updateMerchant(merchant);
			}else{
				merchantId = MerchantDAO.getInstance().insertMerchant(merchant);
			}
			merchant = MerchantDAO.getInstance().findMerchantById(merchantId);
		} catch (Exception e) {
			generateInternalServer(e.getMessage());
		}
		result.put("merchant", merchant.toJson());
		return ok(result);
	}
	
	public static Result getAllMerchants(){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try {
			List<Merchant> merchants = MerchantDAO.getInstance().getAllMerchants();
			ObjectNode result = Json.newObject();
			ArrayNode resultArr = new ArrayNode(JsonNodeFactory.instance);
			for(Merchant merchant: merchants){
				if(StringUtils.isNotEmpty(merchant.getMerchantIdModo())){
					resultArr.add(merchant.toJson());
				}
			}
			result.put(MERCHANTS_TAG, resultArr);
			return ok(result);
		} catch (Exception e) {
			return generateInternalServer(e.getMessage());
		}
	}
	
	public static Result getModoMerchants(){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		try {
			List<Merchant> merchants = MerchantDAO.getInstance().getAllMerchants();
			ObjectNode result = Json.newObject();
			ArrayNode resultArr = new ArrayNode(JsonNodeFactory.instance);
			for(Merchant merchant: merchants){
				if(StringUtils.isNotEmpty(merchant.getMerchantIdModo())){					
					resultArr.add(merchant.toJson());
				}
			}
			result.put(MERCHANTS_TAG, resultArr);
			return ok(result);
		} catch (Exception e) {
			return generateInternalServer(e.getMessage());
		}
	}
}
