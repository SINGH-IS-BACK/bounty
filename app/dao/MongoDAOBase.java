package dao;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import util.Config;

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoDAOBase {

	private MongoClient mongo;
	protected DB db;
	protected static final String ID = "_id";
	
	protected MongoDAOBase(){
		try {
			String DB = Config.getMongoDB();
			mongo = new MongoClient(Config.getMongoHost(), Config.getMongoPort());
			/*String DB = "modo-dev";
			mongo = new MongoClient("localhost", 27017);*/
			db = mongo.getDB(DB);
		} catch (Exception e) {
			Logger.error("Could not connect to MongoDB " +e);
		}
	}
	
	protected String safeString(DBObject dbObj, String key){
		Object strObj = dbObj.get(key);
		if(strObj == null){
			//Logger.warn("Failed to get String data from database for key : "+key);
			return "";
		}else{
			return StringUtils.defaultString((String)strObj);
		}	
	}
	
	protected long safeLong(DBObject dbObj, String key){
		Object strObj = dbObj.get(key);
		if(strObj == null){
			//Logger.warn("Failed to get Long data from database for key : "+key);
			return 0;
		}else{
			try{
				return (long)strObj;				
			}catch(Exception e){
				return 0;
			}
		}	
	}
	
	protected int safeInt(DBObject dbObj, String key){
		Object strObj = dbObj.get(key);
		if(strObj == null){
			//Logger.warn("Failed to get Int data from database for key : "+key);
			return 0;
		}else{
			try{
				return (int)strObj;				
			}catch(Exception e){
				return 0;
			}
		}	
	}
	
	protected double safeDouble(DBObject dbObj, String key){
		Object strObj = dbObj.get(key);
		if(strObj == null){
			//Logger.warn("Failed to get Double data from database for key : "+key);
			return 0;
		}else{
			try{
				return (double)strObj;				
			}catch(Exception e){
				return 0;
			}
		}	
	}
	
	protected boolean safeBoolean(DBObject dbObj, String key){
		Object boolObj = dbObj.get(key);
		if(boolObj == null){
			//Logger.warn("Failed to get Boolean data from database for key : "+key);
			return false;
		}else{
			try{
				return (boolean)boolObj;
			}catch(Exception e){
				return false;
			}
		}	
	}
}
