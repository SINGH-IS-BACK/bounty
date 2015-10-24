package util;

import java.util.ArrayList;
import java.util.List;

import play.Logger;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public class TwitterConfig {

	static String twitter_consumer_key = "Pd8pk0LbVhD5jAFfbcRaCCaaU";//= "wkxLLMPGUNMXZcsfJ5af6nf3M";
	static String twitter_consumer_secret = "77MsnLpl88UqskxEJKTTN72zKONkrCTL7MmSEv1xSRFAlPtm1Y";//= "I48dd33bi7u94t5932UWxoQnurUIvJPLCDzHNurn5UjpGPWWah";
	static Twitter twitter;
	
	public TwitterConfig(String twitter_access_token, String twitter_access_secret) throws IllegalStateException, TwitterException{
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(twitter_consumer_key, twitter_consumer_secret);
        twitter.setOAuthAccessToken(new AccessToken(twitter_access_token, twitter_access_secret));
	}
	
	public String getTwitterName() throws TwitterException{
		User twitterUser = twitter.showUser(twitter.getId());
		return twitterUser.getName();
		
	}
	
	public String getTwitterID() throws TwitterException {
		return String.valueOf(twitter.getId());
	}
	
	public String getEmailAddress() throws TwitterException {
		return "";
	}
	
	public String getImageURL() throws TwitterException{
		User user1 = twitter.showUser(twitter.getId());
        return user1.getOriginalProfileImageURL();
    }

	public List<String> getFriendIds() throws TwitterException{
		List<String> friendIds = new ArrayList<String>();
         
		long cursor = -1;
        IDs ids;
        System.out.println("Listing following ids.");
        do {
            ids = twitter.getFriendsIDs(cursor);
            for (long id : ids.getIDs()) {
            	friendIds.add(String.valueOf(id));
            }
        } while ((cursor = ids.getNextCursor()) != 0);
        return friendIds;
	}
}
