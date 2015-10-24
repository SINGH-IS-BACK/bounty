package dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import play.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import entity.Activity;
import entity.Challenge;
import entity.Merchant;

public class MerchantDAO extends MongoDAOBase{

	private static final String MERCHANT_COLLECTION = "merchantCollection";
	private static final String NAME = "name";
	private static final String MERCHANTID_MODO = "merchantIdModo";
	private static final String ACTIVITYID_TAG = "activityId";
	private static final String CHALLENGEID_TAG = "challengeId";
	private static final String CHALLENGES = "challenges";
	private static final String ACTIVITIES = "activities";
	private static final String PHONE = "phone";
	private static final String EMAIL = "email";
	private static final String LOGO = "logo";
	private static final String ADDRESS = "address";
	private static final String URL = "url";
	
	private static MerchantDAO instance;
	
	private MerchantDAO(){
		super();
	}
	
	public static MerchantDAO getInstance(){
		if(instance == null){
			instance = new MerchantDAO();
		}
		return instance;
	}

	public String insertMerchant(Merchant merchant){
		DBCollection merchantColl = db.getCollection(MERCHANT_COLLECTION);
		BasicDBObject document = generateQuery(merchant);
		merchantColl.insert(document);
		return document.get(ID).toString();
	}

	private BasicDBObject generateQuery(Merchant merchant) {
		BasicDBObject document = new BasicDBObject();
		document.put(NAME, merchant.getMerchantName());
		document.put(MERCHANTID_MODO, merchant.getMerchantIdModo());
		//challenges
		List<DBObject> challenges = new ArrayList<DBObject>();
		for(Challenge ch: merchant.getChallenges()){
			BasicDBObject challenge = new BasicDBObject();
			challenge.put(CHALLENGEID_TAG, ch.getChallengeId());
			challenges.add(challenge);
		}
		document.put(CHALLENGES, challenges);
		//activities
		List<DBObject> activities = new ArrayList<DBObject>();
		for(Activity act: merchant.getActivities()){
			BasicDBObject activity = new BasicDBObject();
			activity.put(ACTIVITYID_TAG, act.getActivityId());
			activities.add(activity);
		}
		document.put(ACTIVITIES, activities);
		document.put(PHONE, merchant.getPhone());
		document.put(EMAIL, merchant.getEmail());
		document.put(LOGO, merchant.getMerchantLogoUrl());
		document.put(ADDRESS, merchant.getMerchantAddress());
		return document;
	}
	
	public Merchant findMerchantById(String merchantId) throws Exception{
		DBCollection merColl = db.getCollection(MERCHANT_COLLECTION);
	    BasicDBObject query = new BasicDBObject();
	    query.put(ID, new ObjectId(merchantId));
	    DBCursor cursor = merColl.find(query);
		if(!cursor.hasNext()){
			throw new Exception("Merchant not found");
		}	
	    return convertToMerchant(cursor.next());
	}
	
	public String getMerchantName(String merchantId) throws Exception{
		DBCollection merColl = db.getCollection(MERCHANT_COLLECTION);
	    BasicDBObject query = new BasicDBObject();
	    query.put(ID, new ObjectId(merchantId));
	    DBCursor cursor = merColl.find(query);
		if(!cursor.hasNext()){
			throw new Exception("Merchant not found");
		}	
		return safeString(cursor.next(),NAME);
	}
	
	public void updateMerchant(Merchant merchant){
		DBCollection merColl = db.getCollection(MERCHANT_COLLECTION);
		BasicDBObject updateQ = generateQuery(merchant); 
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(ID, new ObjectId(merchant.getMerchantId()));
		merColl.update(whereQuery, new BasicDBObject().append("$set", updateQ));
	}
	
	public boolean IsModoMerchantAlreadyAdded(String modoMerchantId){
		DBCollection merColl = db.getCollection(MERCHANT_COLLECTION);
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(MERCHANTID_MODO, modoMerchantId);
		DBCursor cursor = merColl.find(whereQuery);
		if(!cursor.hasNext()){
			return false;
		}	
	    return true;
	}

	public String getMerchantURLLogo(String merchantId) throws Exception{
		DBCollection merColl = db.getCollection(MERCHANT_COLLECTION);
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(ID, new ObjectId(merchantId));
		DBCursor cursor = merColl.find(whereQuery);
		if(!cursor.hasNext()){
			return "";
		}
		return safeString(cursor.next(),LOGO);
	}
	
	public String IsMerchantAlreadyAdded(String merchantName) throws Exception{
		DBCollection merColl = db.getCollection(MERCHANT_COLLECTION);
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(NAME, merchantName);
		DBCursor cursor = merColl.find(whereQuery);
		if(!cursor.hasNext()){
			return "";
		}
		return convertToMerchant(cursor.next()).getMerchantId();
	}
	
	public List<Merchant> getAllMerchants() throws Exception{
		List<Merchant> merchants = new ArrayList<Merchant>();
		DBCollection merColl = db.getCollection(MERCHANT_COLLECTION);
		BasicDBObject orderBy = new BasicDBObject();
		orderBy.put("$natural", -1);
		DBCursor cursor = merColl.find().sort(orderBy);
		/*if(start > 0){
			cursor.skip(start);
		}
		if(count > 0){
			cursor.limit(count);
		}*/
		while(cursor.hasNext()){
			merchants.add(convertToMerchant(cursor.next()));
		}
		return merchants;
	}
	
	@SuppressWarnings("unchecked")
	private Merchant convertToMerchant(DBObject merObj) throws Exception {
		Merchant m = new Merchant();
		m.setMerchantId(merObj.get(ID).toString());
		m.setMerchantName(safeString(merObj,NAME));
		m.setMerchantIdModo(safeString(merObj,MERCHANTID_MODO));
		//activity list
		List<Activity> activities = new ArrayList<Activity>();
		for(DBObject activity: (List<DBObject>)merObj.get(ACTIVITIES)){
			String activityId = safeString(activity,ACTIVITYID_TAG);
			activities.add(ActivityDAO.getInstance().findActivityById(activityId));
		}
		m.setActivities(activities);
		//Challenges
		List<Challenge> challenges = new ArrayList<Challenge>();
		for(DBObject challenge: (List<DBObject>)merObj.get(CHALLENGES)){
			String challengeId = safeString(challenge,CHALLENGEID_TAG);
			if(ChallengeDAO.getInstance().findChallengeById(challengeId) != null){
				challenges.add(ChallengeDAO.getInstance().findChallengeById(challengeId));
			}
			else{
				Logger.info("Challenge not found : Challenge ID :" + challengeId +" Merchant ID:" + merObj.get(ID).toString());
			}
		}
		m.setChallenges(challenges);
		m.setMerchantLogoUrl(safeString(merObj,LOGO));
		m.setMerchantAddress(safeString(merObj,ADDRESS));
		m.setEmail(safeString(merObj,EMAIL));
		m.setPhone(safeString(merObj,PHONE));
		return m;
	}	
}
