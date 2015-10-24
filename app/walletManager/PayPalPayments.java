package walletManager;

import java.net.HttpURLConnection;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.libs.WS;
import play.libs.WS.Response;
import play.libs.WS.WSRequestHolder;
import dao.MasterUserDAO;

import java.io.InputStreamReader;
import java.net.URL;
import java.io.OutputStreamWriter;

public class PayPalPayments {

	public static final String CLIENT_ID = "AdHHEhCPbF4eNbnEqDNjjaouuZsbvG18259pisEAoEwuUZbq_gdDnhAc3tFu";
	public static final String SECRET = "EHqwjxCSQzo9N169pb3LdcBiTLJes_zlybnLcWS5nBh43cYPzuOyQSBZEl6X";
	public static final String KEY = "4fb12ba2d0e771ea7121ba654a8be3ca";
	public static final String URL = "https://api.sbx.gomo.do/YiiModo/api_v2/";

	public static String getAccessToken() {
		try {
			
			//String str = "curl https://api.sandbox.paypal.com/v1/oauth2/token -H \"Accept: application/json\"  -H \"Accept-Language: en_US\" -H \"AdHHEhCPbF4eNbnEqDNjjaouuZsbvG18259pisEAoEwuUZbq_gdDnhAc3tFu:EHqwjxCSQzo9N169pb3LdcBiTLJes_zlybnLcWS5nBh43cYPzuOyQSBZEl6X\" -d \"grant_type=client_credentials\"";
			//Runtime.getRuntime().exec(str);
			
			
			
			String url = "https://api.sandbox.paypal.com/v1/oauth2/token";
			URL obj = new java.net.URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Accept-Language", "en_US");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			String userpass = CLIENT_ID + ":" + SECRET;
			// String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
			String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes("UTF-8")));
			conn.setRequestProperty ("Authorization", basicAuth);

			String data =  "{\"grant_type\":\"client_credentials\"}";
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);	
			out.close();

			new InputStreamReader(conn.getInputStream());
			return "A015LiVmlgihf1ezPTCRN.3bO4hLhI8RKftBdPSAs2x8868"; 
	    
		} catch (Exception e) {
		    e.printStackTrace();
		    return null;
		}
	}
	
	public static String getAccessToken2() {
		try {
			
			//$ curl https://api.sandbox.paypal.com/v1/oauth2/token -H "Accept: application/json"  -H "Accept-Language: en_US" -u "AdHHEhCPbF4eNbnEqDNjjaouuZsbvG18259pisEAoEwuUZbq_gdDnhAc3tFu:EHqwjxCSQzo9N169pb
			//	3LdcBiTLJes_zlybnLcWS5nBh43cYPzuOyQSBZEl6X" -d "grant_type=client_credentials"
			
			ProcessBuilder p=new ProcessBuilder("curl","--show-error", "--request","GET",
	                "--header","Accept: application/json", "--user", CLIENT_ID + ":" + SECRET);
			
			String url = "https://api.sandbox.paypal.com/v1/oauth2/token";
			URL obj = new java.net.URL(url);
			
			WSRequestHolder holder = WS.url(url);
			
			//String 
			ObjectNode dataNode = Json.newObject();
			dataNode.put("grant_type", "client_credentials");
			
			String userpass = CLIENT_ID + ":" + SECRET;
			// String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
			String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes("UTF-8")));
			
			System.out.println(basicAuth);
			Response response = holder.setHeader("Accept", "application/json").setAuth(CLIENT_ID, SECRET)
					.setHeader("Accept-Language", "en_US")
					.setContentType("application/x-www-form-urlencoded").post(dataNode).get(10000);
			
			System.out.println(response.getStatus());
			
			System.out.println("Body" + response.getBody());
			
			/*
			
			conn.setRequestProperty ("Authorization", basicAuth);

			String data =  "{\"grant_type\":\"client_credentials\"}";
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);	
			out.close();

			new InputStreamReader(conn.getInputStream());*/
			return "A015LiVmlgihf1ezPTCRN.3bO4hLhI8RKftBdPSAs2x8868"; 
	    
		} catch (Exception e) {
		    e.printStackTrace();
		    return null;
		}
	}
	
	
	private static String getApiRootUrlString() {
		// TODO Auto-generated method stub
		return null;
	}

	public static String createGiftCard() {
		String url = "https://api.sandbox.paypal.com/v1/gift-cards/gift-cards";
		WSRequestHolder holder = WS.url(url);
				
		//String 
		ObjectNode dataNode = Json.newObject();
		dataNode.put("product_id", "SPRSHJLE533FCG6CSDFLKJL32982438SJSF");
		ObjectNode amountNode = Json.newObject();
		amountNode.put("currency", "USD");
		amountNode.put("value", "23.00");
		dataNode.put("amount", amountNode);
		
		Response response = holder.setHeader("Accept", "application/json")
				.setHeader("Authorization", "Bearer A015bFCiBau--giSjWx1vEoqKtt5ILfvkE26qoP0iu5ELHc")
				.setHeader("Content-Length", "172")
				.setHeader("Content-Type", "application/json;charset=UTF-8")
				.setHeader("PayPal-Request-Id", "SDF8798797FSDFS").post(dataNode).get(10000);
		
		System.out.println(response.asJson());
		String cardNumber = "";//response.asJson().get("card_number").asText();
		//String security_code = response.asJson().get("security_code").asText();
		return cardNumber;		
	}
	
	public static String viewGiftCard() {
		String url = "https://api.sandbox.paypal.com/v1/wallet/gift-cards";
		WSRequestHolder holder = WS.url(url);
				
		//String 
/*		ObjectNode dataNode = Json.newObject();
		dataNode.put("product_id", "SPRSHJLE533FCG6CSDFLKJL32982438SJSF");
		ObjectNode amountNode = Json.newObject();
		amountNode.put("currency", "USD");
		amountNode.put("value", "23.00");
		dataNode.put("amount", amountNode);
	*/	
		Response response = holder.setHeader("Accept", "application/json")
				.setHeader("Authorization", "Bearer A015bFCiBau--giSjWx1vEoqKtt5ILfvkE26qoP0iu5ELHc")
				.setHeader("Content-Length", "172")
				.setHeader("Content-Type", "application/json;charset=UTF-8")
				.setHeader("PayPal-Request-Id", "SDF8798797FSDFS").get().get(10000);
		
		System.out.println(response.getStatus());
		System.out.println(response.asJson());
		String cardNumber = "";//response.asJson().get("card_number").asText();
		//String security_code = response.asJson().get("security_code").asText();
		return cardNumber;		
	}
	
	public static void main(String[] args) {
		try {
			//getAccessToken2();
			viewGiftCard();
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
