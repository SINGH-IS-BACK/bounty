package util;

import java.util.List;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import dao.ChallengeDAO;
import dao.GiftDAO;
import dao.MerchantDAO;

import entity.Challenge;
import entity.Gift;
import entity.Merchant;
 
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
 
public class EmailService {
   
	private static final String FROM_DOMAIN = "Win Bounty <admin@winbounty.co>";
	private static final String DOMAIN = "winbounty.co";
	private static final String API_KEY = "key-2e3pb-4d00n244cjr2qtfilxs53ie7k4";

    
    public void send(String to, String subject, String msg) throws Exception {
    	
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api", API_KEY));
        WebResource webResource = client.resource("https://api.mailgun.net/v2/" + DOMAIN +  "/messages");
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        formData.add("from", FROM_DOMAIN);
        formData.add("to", to);
        formData.add("subject", subject);
        formData.add("text", msg);
        ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
        
        if(clientResponse.getStatus() != 200){
        	Logger.error("Error while sending email as text. Client response:" +clientResponse);
			throw new Exception("Could not send mail" + clientResponse.getStatus() + clientResponse.toString());
		}//String output = clientResponse.getEntity(String.class);
    }
    
    public static void sendMail(String giftId) throws Exception {
		Gift gift = GiftDAO.getInstance().findGiftById(giftId);
		List<Challenge> challenges = ChallengeDAO.getInstance().getAllActiveChallenges();
		for(Challenge challenge : challenges){
			// challenge with this gift Id and having merchant Id
			if(challenge.getGift().getGiftId().equals(giftId) && StringUtils.isNotEmpty(challenge.getMerchantId())){
				Merchant merchant = MerchantDAO.getInstance().findMerchantById(challenge.getMerchantId());
				EmailService emailService = new EmailService();
				String message = emailService.createMessage(challenge, gift, merchant);
				emailService.send(merchant.getEmail() , "Challenge Details - Win Bounty", message);
			}
		}
	}
    
    public String createMessage(Challenge challenge, Gift gift, Merchant merchant){
    	String message = "Hi " + merchant.getMerchantName() + ",";
    	message += "\n\nYour Challenge has been successfully added. \n\n CHALLENGE DETAILS";
    	message += "\nChallenge Name      :  " + challenge.getTitle();
    	message += "\nChallenge ID          :  " + challenge.getChallengeId();
    	message += "\nChallenge QR Code  :  " + challenge.getQRCode();
    	message += "\nYou can visit this website https://www.the-qrcode-generator.com/ and convert this QR Code to image";
    	
    	message += "\n\n GIFT DETAILS";
    	message += "\nGift Amount      :" + gift.getAmount();
    	message += "\nNumber of Gifts : " + gift.getMaxGifts();
    	message += "\nGift Status       : " + gift.getGiftStatus();
    	if(gift.getGiftStatus().name() == "ADDED"){
    		message += "\n\nTHIS CHALLENGE WILL ONLY BE AVAILABLE TO CUSTOMERS ONCE ITS FUNDED.";
    		message += "\nTo Pay visit this link : " + "http://winbounty.co/merchant/payment/" + gift.getGiftId();
        }
    	else if(gift.getGiftStatus().name() == "FUNDED"){
    		message += "\n\nThis challenge has been funded. It will be available to customers after starting date";
    	}
    	
    	message += "\n\n Cheers, \nAdministrator \nWin Bounty";
    	return message;
    }
}