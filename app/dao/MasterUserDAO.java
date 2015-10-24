package dao;

import org.bson.types.ObjectId;

import play.Logger;
import walletManager.ModoPayments;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class MasterUserDAO extends MongoDAOBase{

	public static final Long DAY  = (long) 86400000;
	
	private static final String TRUMP_TOKEN_COLLECTION = "trumpTokenCollection";
	private static final String KEY = "key";
	private static final String ACCESS_TOKEN = "accessToken";
	
	
	private static final String TOKEN_COLLECTION = "tokenCollection";
	private static final String TOKEN = "token";
	private static final String TIME = "time";
	
	private static final String MASTER_ACCID_COLLECTION = "masterAcc";
	private static final String ACCID = "accId";

	//private 
	private static MasterUserDAO instance;
	
	private MasterUserDAO(){
		super();
	}
	
	public static MasterUserDAO getInstance(){
		if(instance == null){
			instance = new MasterUserDAO();
		}
		return instance;
	}
	
	public void addAccessToken(String key, String accessToken){
		DBCollection tokenColl = db.getCollection(TRUMP_TOKEN_COLLECTION);
		BasicDBObject document = new BasicDBObject();
		document.put(KEY, key);
		document.put(ACCESS_TOKEN, accessToken);
		tokenColl.insert(document);
	}
	
	public String findKey(String key){
		DBCollection tokenColl = db.getCollection(TRUMP_TOKEN_COLLECTION);
		BasicDBObject query = new BasicDBObject(KEY, key);
	    //query.put(KEY, new ObjectId(key));
	    DBCursor cursor = tokenColl.find(query);
		if(!cursor.hasNext()){
			return null;
		}	
		return safeString(cursor.next(), ACCESS_TOKEN);
	}
	
	public void addToken(String token){
		DBCollection tokenColl = db.getCollection(TOKEN_COLLECTION);
		BasicDBObject document = new BasicDBObject();
		document.put(TOKEN, token);
		document.put(TIME, System.currentTimeMillis());
		tokenColl.insert(document);
	}
	
	public String getToken(){
		DBCollection tokenColl = db.getCollection(TOKEN_COLLECTION);
		if(tokenColl.count() == 0){
			String token = ModoPayments.getToken();
			MasterUserDAO.getInstance().addToken(token);
			return token;
		}
		Long previousTime = Long.valueOf(tokenColl.findOne().get(TIME).toString());
		Long currentTime = System.currentTimeMillis();
		if((currentTime - previousTime) > DAY){
			Logger.info("MODO TOKEN UPDATED. Current Time: " + currentTime + " Last Updated Time :" + previousTime);
			String token = ModoPayments.getToken();
			BasicDBObject newDocument = new BasicDBObject();
			//newDocument.append("$set", new BasicDBObject().append(TOKEN, token));
			//newDocument.append("$set", new BasicDBObject().append(TIME, System.currentTimeMillis()));
			
			newDocument.put(TOKEN, token);
			newDocument.put(TIME, System.currentTimeMillis());
			
			
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put(TOKEN, tokenColl.findOne().get(TOKEN));
			tokenColl.update(searchQuery, newDocument);
		}
		return tokenColl.findOne().get(TOKEN).toString();
	    
	}
	
	public void addMasterAccId(String accountId){
		DBCollection accColl = db.getCollection(MASTER_ACCID_COLLECTION);
		BasicDBObject document = new BasicDBObject();
		document.put(ACCID, accountId);
		accColl.insert(document);
	}
	
	public String getMasterAcc(){
		DBCollection accColl = db.getCollection(MASTER_ACCID_COLLECTION);
		return accColl.findOne().get(ACCID).toString();
	}
	
	
}
