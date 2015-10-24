package walletManager;

import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import dao.MasterUserDAO;
import play.Logger;
import play.libs.WS;
import play.libs.WS.Response;
import play.libs.WS.WSRequestHolder;
import scala.util.parsing.json.JSONArray;

public class ModoPayments {

	public static final String KEY = "4fb12ba2d0e771ea7121ba654a8be3ca";
	public static final String URL = "https://api.sbx.gomo.do/YiiModo/api_v2/";
	
	public static String getToken(){
		String secret = "fddb12618e702add1cbb248dae35d0071e5c7c9301ec0707a57bac55e3323fca";
		String credRaw = KEY + ":" + secret;
		byte[] encodedBytes = Base64.encodeBase64(credRaw.getBytes());
		String encodedCred = new String(encodedBytes);
		String dataToPass = "credentials="+encodedCred;
		String feedUrl = URL + "token";
		WSRequestHolder req = WS.url(feedUrl);
		Response response = req.setContentType("application/x-www-form-urlencoded").post(dataToPass).get(10000);
		String token = response.asJson().get("response_data").get("access_token").asText();
		System.out.println("Token-->" +token);
		System.out.println("Response-->" +credRaw);
		
		return token;
	}
	
	public static void addCard(String cardNum, String expiry, String zipCode, String ccv) throws Exception{
		String token = MasterUserDAO.getInstance().getToken();
		String accId = MasterUserDAO.getInstance().getMasterAcc();
		String userData = "consumer_key="+KEY
					+"&access_token="+token
					+"&account_id="+accId
					+"&card_number="+cardNum
					+"&expiry="+expiry
					+"&zip_code="+zipCode
					+"&card_security="+ccv;
		String feedUrl = URL +"card/add";
		WSRequestHolder req = WS.url(feedUrl);
		Response response = req.setContentType("application/x-www-form-urlencoded").post(userData).get(10000);
		try{
			if(response.asJson().get("status_code").asInt()!=0){
				throw new Exception("Adding card failed");
			}
		}catch(Exception e){
			throw new Exception("Error while adding card");
		}
	}
	
	public static JsonNode getMerchantList(){
		String token = MasterUserDAO.getInstance().getToken();
		String userData = "consumer_key="+KEY
				+"&access_token="+token;
		String feedUrl = URL +"merchant/list";
		WSRequestHolder req = WS.url(feedUrl);
		Response response = req.setContentType("application/x-www-form-urlencoded").post(userData).get(10000);
		return response.asJson();
	}
	
	public static String registerUserAtModo(String phone) throws Exception{
		String token = MasterUserDAO.getInstance().getToken();
		String password = "RetailLabs";
		String userData = "consumer_key="+KEY
				+"&access_token="+token
				+"&phone=" +phone
				+"&password=" +password 
		        +"&should_send_password=0"
		        +"&should_send_modo_descript=0"
		        +"&is_modo_terms_agree=1";
		String feedUrl = URL +"people/register";
		WSRequestHolder req = WS.url(feedUrl);
		Response response = req.setContentType("application/x-www-form-urlencoded").post(userData).get(10000);
		String accId = "";
		try{
			JsonNode result = response.asJson();
			if(result.get("status_code").asInt()!=0){
				throw new Exception("Master user registration failed");
			}
			accId = result.get("response_data").get("account_id").asText();
			System.out.println("Master account id-->" +accId);
			MasterUserDAO.getInstance().addMasterAccId(accId);
		}catch(Exception e){
			throw new Exception("Master user registration failed");
		}
		return accId;
	}
	
	public static ArrayList<String> sendGift(String amt, String emailId, String merchantId) throws Exception{
		String token = MasterUserDAO.getInstance().getToken();
		String accIdSender = MasterUserDAO.getInstance().getMasterAcc();
		String userData = "consumer_key="+KEY
				+"&access_token="+token
				+"&account_id="+accIdSender
				+"&gift_amount="+amt
				+"&receiver_other_id="+emailId
                +"&other_id_type=email";
		if(StringUtils.isNotEmpty(merchantId)){
			userData = userData +"&merchant_id="+merchantId;
		}
		String feedUrl = URL +"gift/send";
		WSRequestHolder req = WS.url(feedUrl);
		Response response = req.setContentType("application/x-www-form-urlencoded").post(userData).get(10000);
		ArrayList<String> giftIduserId = new ArrayList<String>();
		try{
			JsonNode result = response.asJson();
			String debug = result.get("status_code").asText();
			debug += "\nUser Data : " + userData + "\nURL:" + feedUrl;
			if(result.get("status_code").asInt()!=0){
				Logger.error("Send Gift Failed. Debug UserData : " + userData.toString() + "  Result : " + result.toString());
				throw new Exception("send card failed" + debug);
			}
			giftIduserId.add(result.get("response_data").get("gift_id").asText());
			giftIduserId.add(result.get("response_data").get("account_id").asText());
			Logger.info("Send Gift succedd UserData : " + userData.toString() + "  Result : " + result.toString());
			}catch(Exception e){
				Logger.error("Send Gift Failed. Debug UserData : " + userData.toString() + "  Result : " + response.asJson().toString());
				throw new Exception("send card failed " + e);
		}
		return giftIduserId;
	}
	
	public static String getCardsUsers(String phone) throws Exception{
		String token = MasterUserDAO.getInstance().getToken();
		String userData = "consumer_key="+KEY
				+"&access_token="+token
				+"&phone="+phone;
		String feedUrl = URL +"people/lookup_phone";
		WSRequestHolder req = WS.url(feedUrl);
		Response response = req.setContentType("application/x-www-form-urlencoded").post(userData).get(10000);
		
		String accId = "";
		try{
			JsonNode result = response.asJson();
			if(result.get("status_code").asInt()!=0){
				throw new Exception("get user card info failed");
			}
			accId = result.get("response_data").get("account_id").asText();
			System.out.println("Account id-->" +accId);
		}catch(Exception e){
			throw new Exception("get user card info failed");
		}
		return accId;
		//{"status_code":0,"more_info_url":"http://developer.modopayments.com/v3/docs/response_code_0","developer_message":"Request to look up an account id was successful.","user_message":"Success","debug":[],"response_data":{"account_id":"14df7f93f2f24518a0ebfb00ad966c88"}}
	}
	
	public static boolean removeGift(String modogiftId, String modoAccId) throws Exception{
		String token = MasterUserDAO.getInstance().getToken();
		String userData = "consumer_key="+KEY
				+"&access_token="+token
				+"&account_id="+modoAccId
				+"&gift_id="+modogiftId;
		String feedUrl = URL +"gift/delete";
		WSRequestHolder req = WS.url(feedUrl);
		Response response = req.setContentType("application/x-www-form-urlencoded").post(userData).get(10000);
		try{
			JsonNode result = response.asJson();
			if(result.get("status_code").asInt()!=0){
				throw new Exception("delete gift card failed");
			}
		}catch(Exception e){
			throw new Exception("delete gift card failed");
		}
		return true;
	}
	
	public static boolean checkout(String modoAccId, String checkoutCode) throws Exception{
		String token = MasterUserDAO.getInstance().getToken();
		String userData = "consumer_key="+KEY
				+"&access_token="+token
				+"&account_id="+modoAccId
				+"&checkout_code="+checkoutCode;
		String feedUrl = URL +"location/user_checkout";
		WSRequestHolder req = WS.url(feedUrl);
		Response response = req.setContentType("application/x-www-form-urlencoded").post(userData).get(10000);
		try{
			JsonNode result = response.asJson();
			if(result.get("status_code").asInt()!=0){
				throw new Exception("checkout failed");
			}
		}catch(Exception e){
			throw new Exception("checkout failed");
		}
		return true;
	}
	
	public static ArrayList<String> offerLookup(String accModo) throws Exception{
		String token = MasterUserDAO.getInstance().getToken();
		String userData = "consumer_key="+KEY
				+"&access_token="+token
				+"&account_id="+accModo
				+"&status_filter= [\"live\"]";//,\"used\"]";
		
		String feedUrl = URL +"offer/lookup";
		WSRequestHolder req = WS.url(feedUrl);
		Response response = req.setContentType("application/x-www-form-urlencoded").post(userData).get(10000);
		try{
			JsonNode result = response.asJson();
			String debug = result.get("status_code").asText();
			debug += "\nUser Data : " + userData + "\nURL:" + feedUrl;
			if(result.get("status_code").asInt()!=0){
				Logger.error("Offer Lookup Failed. Debug UserData : " + userData.toString() + "  Result : " + result.toString());
				throw new Exception("Failed to look Offers" + debug);
			}
			
			String user_message = result.get("user_message").asText();
			//System.out.println(user_message);
			JsonNode gifts = result.get("response_data").get("gifts");
			ArrayList<String> list = new ArrayList<String>();
		       for(int i=0; i<gifts.size(); i++){
		       list.add(gifts.get(i).get("gift_id").asText());
		    }
			return list;
		}catch(Exception e){
			Logger.error("Offer Lookup Failed. UserData : " + userData.toString() + "  Result : " + response.asJson().toString());
			throw new Exception("send card failed " + e);
		}
		//return gifts;
	}
	
	
	public static String[] visit(String accModo, String merchantId, String giftId) throws Exception{
		String token = MasterUserDAO.getInstance().getToken();
		String callbackUrl = "http://jointrump/callBack";
		String userData = "consumer_key="+KEY
				+"&access_token="+token
				+"&account_id="+accModo
				+"&funding_sources= {\"gifts\":{ \""+giftId+"\": \"0\" }}";
				//+"&gift_ids=[\""+giftId+"\"]"
				//+"&callback_url="+callbackUrl;
		if(StringUtils.isNotEmpty(merchantId)){
				userData = userData +"&merchant_id="+merchantId;
		}
		String feedUrl = URL +"location/visit";
		WSRequestHolder req = WS.url(feedUrl);
		Response response = req.setContentType("application/x-www-form-urlencoded").post(userData).get(10000);
		String[] res = new String[2];
		try{
			JsonNode result = response.asJson();
			if(result.get("status_code").asInt()!=0){
				Logger.error("Visit Call Failed. Debug UserData : " + userData.toString() + "  Result : " + result.toString());
				throw new Exception("visit call failed");
			}
			String barCode = result.get("response_data").get("barcode_image_data").asText();
			res[0] = barCode;
			String checkoutCode = result.get("response_data").get("checkout_code").asText();
			res[1] = checkoutCode;
			Logger.info("Visit Call successfull. UserData : " + userData.toString() + "  Result : " + result.toString());
		}catch(Exception e){
			Logger.error("Visit Call Failed. userData" + userData.toString() + "  Response : " + response.asJson().toString());
			throw new Exception("visit call failed");
		}
		return res;
	}
	
	public static void main(String[] args) {
		try {
			//getToken();
			//System.out.println(getMerchantList());
			//visit("182f20aa-a8cc-4673-924e-a0137b3effbd","7500c098-27b0-471d-9ed0-3f406e266f09", "9d01e151-432c-48de-8aa1-034e45142e13");
			//sendGift("15","4087184494");
			//getCardsUsers("4087184494");
			//System.out.println(offerLookup("11bc599b84868810b072d722101a89","182f20aa-a8cc-4673-924e-a0137b3effbd"));
			//removeGift("75bff4a11e2a4167be619d067fae063d","14df7f93f2f24518a0ebfb00ad966c88");
			//getMerchantList
			//String[] str = visit("14df7f93f2f24518a0ebfb00ad966c88","9318b095a1b54893afe83cb5ef469aa7");
			//System.out.println(str[0]);
			//System.out.println(str[1]);
			//checkout("14df7f93f2f24518a0ebfb00ad966c88","0396226378");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
