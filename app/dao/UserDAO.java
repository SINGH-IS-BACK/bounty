package dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import entity.Activity;
import entity.Merchant;
import entity.User;
import entity.UserGiftMap;

public class UserDAO extends MongoDAOBase{

	private static final String USER_COLLECTION = "userCollection";
	private static final String NAME = "name";
	private static final String MOBILE_NUMBER = "mobileNumber";
	private static final String EMAIL = "email";
	private static final String URL = "url";
	private static final String MERCHANTS = "merchantsIFollow";
	private static final String FRIENDS = "myFriends";
	private static final String ACCOUNTID_MODO = "modoActivityId";
	private static final String INVITE_CODE = "inviteCode";
	private static final String INVITE_COUNT = "inviteCount";
	private static final String FB_USER_NAME = "fbUserName";
	private static final String TWITTER_ID = "twitterId";
	private static final String COMPLETED_CHALLENGES = "completedChallenges";
	private static final String ACTIVITIES = "activities";
	private static final String VERIFIED_USER = "verifiedUser";
	private static final String AMOUNT_WON = "amountWon";
	private static final String MERCHANTID_TAG = "merchantId";
	private static final String FRIENDID_TAG = "friendUserId";
	private static final String CHALLENGEID_TAG = "challengeId";
	private static final String ACTIVITYID_TAG = "activityId";
	private static final String GIFTS = "gifts";
	private static final String GIFTID_TAG = "giftId";
	private static final String MODOGIFTID_TAG = "modoGift";
	private static final String REMAINING_AMOUNT = "remainingAmount";
	private static final String USER_GIFT_STATUS = "userGiftStatus";
	
	
	private static UserDAO instance;
	
	private UserDAO(){
		super();
	}
	
	public static UserDAO getInstance(){
		if(instance == null){
			instance = new UserDAO();
		}
		return instance;
	}
	
	public String insertUser(User user){
		DBCollection userColl = db.getCollection(USER_COLLECTION);
		BasicDBObject document = generateQuery(user);
		userColl.insert(document);
		return document.get(ID).toString();
	}

	public void updateUser(User user) throws Exception{
		DBCollection userColl = db.getCollection(USER_COLLECTION);
		BasicDBObject updateQ = generateQuery(user); 
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(ID, new ObjectId(user.getUserId()));
		userColl.update(whereQuery, new BasicDBObject().append("$set", updateQ));
	}
	
	public User findUserById(String userId ) throws Exception{
		DBCollection userColl = db.getCollection(USER_COLLECTION);
	    BasicDBObject query = new BasicDBObject();
	    query.put(ID, new ObjectId(userId));
	    DBCursor cursor = userColl.find(query);
		if(!cursor.hasNext()){
			throw new Exception("User not found");
		}	
	    return convertToUser(cursor.next());
	}
	
	public User findUserByFB_ID(String FBUserName ) throws Exception{
		DBCollection userColl = db.getCollection(USER_COLLECTION);
	    BasicDBObject query = new BasicDBObject(FB_USER_NAME, FBUserName);
	    DBCursor cursor = userColl.find(query);
		if(!cursor.hasNext()){
			return null;
		}	
	    return convertToUser(cursor.next());
	}
	
	public User findUserByTwitterID(String TwitterID) throws Exception{
		DBCollection userColl = db.getCollection(USER_COLLECTION);
	    BasicDBObject query = new BasicDBObject(TWITTER_ID, TwitterID);
	    DBCursor cursor = userColl.find(query);
		if(!cursor.hasNext()){
			return null;
		}	
	    return convertToUser(cursor.next());
	}
	
	
	private BasicDBObject generateQuery(User user) {
		BasicDBObject document = new BasicDBObject();
		document.put(NAME, user.getName());
		document.put(MOBILE_NUMBER, user.getMobileNumber());
		document.put(EMAIL, user.getEmailId());
		document.put(URL, user.getUrl());
		//merchants
		List<DBObject> merchants = new ArrayList<DBObject>();
		for(Merchant m: user.getMerchants()){
			BasicDBObject merchant = new BasicDBObject();
			merchant.put(MERCHANTID_TAG, m.getMerchantId());
			merchants.add(merchant);
		}
		document.put(MERCHANTS, merchants);
		//friends
		List<DBObject> friends = new ArrayList<DBObject>();
		for(String userId: user.getFriendIds()){
			BasicDBObject friend = new BasicDBObject();
			friend.put(FRIENDID_TAG, userId);
			friends.add(friend);
		}
		document.put(FRIENDS, friends);
		document.put(ACCOUNTID_MODO, user.getAccountIdModo());
		document.put(INVITE_CODE, user.getInviteCode());
		document.put(INVITE_COUNT, user.getInviteCounts());
		document.put(FB_USER_NAME, user.getFbUserName());
		document.put(TWITTER_ID, user.getTwitterId());
		document.put(AMOUNT_WON, user.getAmountWon());
		//completed challenges
		List<DBObject> completedChallenges = new ArrayList<DBObject>();
		String[] comCh = user.getCompletedChallengeIds().toArray(new String[user.getCompletedChallengeIds().size()]);
		for(String chId: comCh){
			BasicDBObject comChallenge = new BasicDBObject();
			comChallenge.put(CHALLENGEID_TAG, chId);
			completedChallenges.add(comChallenge);
		}
		document.put(COMPLETED_CHALLENGES,completedChallenges);
		//activities
		List<DBObject> activities = new ArrayList<DBObject>();
		for(Activity act: user.getActivities()){
			BasicDBObject activity = new BasicDBObject();
			activity.put(ACTIVITYID_TAG, act.getActivityId());
			activities.add(activity);
		}
		document.put(ACTIVITIES, activities);
		//Gifts
		List<DBObject> gifts = new ArrayList<DBObject>();
		for(UserGiftMap giftMs: user.getWonGifts()){
			BasicDBObject gift = new BasicDBObject();
			gift.put(GIFTID_TAG, giftMs.getGiftId());
			gift.put(MODOGIFTID_TAG, giftMs.getModoGiftId());
			gift.put(REMAINING_AMOUNT, giftMs.getRemainingAmount());
			gift.put(USER_GIFT_STATUS, giftMs.getUserGiftStatus().name());
			gifts.add(gift);
		}
		document.put(GIFTS, gifts);
		document.put(VERIFIED_USER, user.isVerifiedUser());
		return document;
	}

	@SuppressWarnings("unchecked")
	private User convertToUser(DBObject userObj) throws Exception {
		String name = safeString(userObj,NAME);
		String mobileNum = safeString(userObj,MOBILE_NUMBER);
		User u = new User(name,mobileNum);
		u.setUserId(userObj.get(ID).toString());
		u.setEmailId(safeString(userObj,EMAIL));
		u.setUrl(safeString(userObj,URL));
		//merchants
		List<Merchant> merchants = new ArrayList<Merchant>();
		for(DBObject merchant: (List<DBObject>)userObj.get(MERCHANTS)){
			String merchantId = safeString(merchant,MERCHANTID_TAG);
			merchants.add(MerchantDAO.getInstance().findMerchantById(merchantId));
		}
		u.setMerchants(merchants);
		//friends
		List<String> friendIds = new ArrayList<String>();
		for(DBObject friend: (List<DBObject>)userObj.get(FRIENDS)){
			friendIds.add(safeString(friend,FRIENDID_TAG));
		}
		u.setFriendIds(friendIds);
		u.setAccountIdModo(safeString(userObj,ACCOUNTID_MODO));
		u.setInviteCode(safeString(userObj,INVITE_CODE));
		u.setInviteCounts(safeInt(userObj,INVITE_COUNT));
		u.setFbUserName(safeString(userObj,FB_USER_NAME));
		u.setTwitterId(safeString(userObj,TWITTER_ID));
		u.setAmountWon(safeDouble(userObj,AMOUNT_WON));
		//completed challenges
		Set<String> comCh = new HashSet<String>();
		for(DBObject challenge: (List<DBObject>)userObj.get(COMPLETED_CHALLENGES)){
			comCh.add(safeString(challenge,CHALLENGEID_TAG));
		}
		u.setCompletedChallengeIds(comCh);
		//activity list
		List<Activity> activities = new ArrayList<Activity>();
		for(DBObject activity: (List<DBObject>)userObj.get(ACTIVITIES)){
			String activityId = safeString(activity,ACTIVITYID_TAG);
			activities.add(ActivityDAO.getInstance().findActivityById(activityId));
		}
		u.setActivities(activities);
		//gifts
		List<UserGiftMap> wonGifts = new ArrayList<UserGiftMap>();
		for(DBObject gift: (List<DBObject>)userObj.get(GIFTS)){
			UserGiftMap ug = new UserGiftMap();
			ug.setGiftId(safeString(gift,GIFTID_TAG));
			ug.setModoGiftId(safeString(gift,MODOGIFTID_TAG));
			ug.setRemainingAmount(safeDouble(gift,REMAINING_AMOUNT));
			String giftType = safeString(gift,USER_GIFT_STATUS);
			ug.setUserGiftStatus(UserGiftMap.UserGiftStatus.valueOf(giftType));
			wonGifts.add(ug);
		}
		u.setWonGifts(wonGifts);
		return u;
	}
}
