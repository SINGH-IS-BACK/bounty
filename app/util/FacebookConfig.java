package util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import play.Logger;

import facebook4j.*;
import facebook4j.auth.AccessToken;

public class FacebookConfig {
	
	static String facebook_client_id = "969804303034873";
	static String facebook_client_secret = "3446d7c148af30580dd9673404b5b5be";
	//static String facebook_access_token1 = "CAACEdEose0cBAOqQSa55OypvZBw46Cak8MzgzarED3ZC64RolyVdr6CcQbZAPhKCdAvI4GzbaOzk7qeVZCfoTcrefy0cEmOH50koQO35UnEKmeKBj3Au84IC6xEZBDLu8fUKKWiZBZCLpSXFfdL8GZAtulCLQ2ZAaSS11RcQcPN12aW68q0t4A5PSzBCNVQIEehJcVk9zHZAbdzZAM4NoCypeyu";
	static Facebook facebook;
	
	public FacebookConfig(String facebook_access_token){
		facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId(facebook_client_id, facebook_client_secret);
		facebook.setOAuthPermissions("email, public_profile, user_friends");
		facebook.setOAuthAccessToken(new AccessToken(facebook_access_token, null));

	}
	
	public String getFacebookName() throws FacebookException{
		return facebook.getName();
	}
	public String getFacebookUserName() throws FacebookException{
		return facebook.getId();
	}
	
	public String getEmailAddress() throws FacebookException{
		return facebook.getMe().getEmail();
	}
	public String getImageURL() throws FacebookException{
		URL largePic = facebook.getPictureURL(facebook.getId(), PictureSize.large);
		return largePic.toString();
	}

	public List<String> getFriendIds() throws FacebookException{
		ResponseList<Friend> friendList = facebook.getFriends();
		List<String> friendIds = new ArrayList<String>();
		for(int i = 0 ; i < friendList.size() ; i++){
			friendIds.add(friendList.get(i).getId());
		}
		return friendIds;
	}
}
