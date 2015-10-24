package dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import entity.Challenge;
import entity.Location;
import entity.SmallCluster;

public class SmallClusterDAO extends MongoDAOBase{

	private static final String SMALLCLUSTER_COLLECTION = "smallClusterCollection";
	private static final String CENTER = "center";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	private static final String CHALLENGEID_TAG = "challengeId";
	private static final String CHALLENGES = "challenges";
	
	private static SmallClusterDAO instance;
	
	private SmallClusterDAO(){
		super();
	}
	
	public static SmallClusterDAO getInstance(){
		if(instance == null){
			instance = new SmallClusterDAO();
		}
		return instance;
	}
	
	public void deleteSmallClusterById(String id) throws Exception {
		DBCollection chColl = db.getCollection(SMALLCLUSTER_COLLECTION);
	    BasicDBObject query = new BasicDBObject();
	    query.put(ID, new ObjectId(id));
	    if(findSmallClusterById(id) != null){
	    	chColl.remove(query);
	    }
	    else
	    	throw new Exception("Small Cluster does not exist");
	}
	
	public String insertSmallCluster(SmallCluster smallClusterDAO){
		DBCollection chColl = db.getCollection(SMALLCLUSTER_COLLECTION);
		BasicDBObject document = generateQuery(smallClusterDAO);
		chColl.insert(document);
		return document.get(ID).toString();
	}
	
	public SmallCluster findSmallClusterById(String id) throws Exception {
		DBCollection chColl = db.getCollection(SMALLCLUSTER_COLLECTION);
	    BasicDBObject query = new BasicDBObject();
	    query.put(ID, new ObjectId(id));
	    DBCursor cursor = chColl.find(query);
		if(!cursor.hasNext()){
			return null;
		}	
	    return convertToSmallCluster(cursor.next());
	}
	
	public void updateSmallCluster(SmallCluster smallCluster){
		DBCollection chColl = db.getCollection(SMALLCLUSTER_COLLECTION);
	    BasicDBObject updateQ = generateQuery(smallCluster); 
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(ID, new ObjectId(smallCluster.getSmallClusterId()));
		chColl.update(whereQuery, new BasicDBObject().append("$set", updateQ));
	}
	
	public ArrayList<SmallCluster> getAllSmallClusters() throws Exception{
		ArrayList<SmallCluster> smallClusters = new ArrayList<SmallCluster>();
		DBCollection chColl = db.getCollection(SMALLCLUSTER_COLLECTION);
		BasicDBObject orderBy = new BasicDBObject();
		orderBy.put("$natural", -1);
		DBCursor cursor = chColl.find().sort(orderBy);
		while(cursor.hasNext()){
			smallClusters.add(convertToSmallCluster(cursor.next()));
		}
		return smallClusters;
	}
	
	
	private BasicDBObject generateQuery(SmallCluster smallCluster) {
		BasicDBObject document = new BasicDBObject();
		List<DBObject> challenges = new ArrayList<DBObject>();
		for(Challenge ch: smallCluster.getChallenges()){
			BasicDBObject challenge = new BasicDBObject();
			challenge.put(CHALLENGEID_TAG, ch.getChallengeId());
			challenges.add(challenge);
		}
		document.put(CHALLENGES, challenges);
		BasicDBObject center_obj = new BasicDBObject();
		Location center = smallCluster.getCenter();
		center_obj.put(LATITUDE, center.getLatitude());
		center_obj.put(LONGITUDE, center.getLongitude());
		
		document.put(CENTER, center_obj);	
		return document;
	}
	
	@SuppressWarnings("unchecked")
	private SmallCluster convertToSmallCluster(DBObject merObj) throws Exception {
		SmallCluster smallCluster = new SmallCluster();
		
		smallCluster.setSmallClusterId(merObj.get(ID).toString());
		Location center = new Location();
		DBObject centerObj = (DBObject)merObj.get(CENTER);
		
		center.setLatitude(safeDouble(centerObj, LATITUDE));
		center.setLongitude(safeDouble(centerObj, LONGITUDE));
		smallCluster.setCenter(center);
		
		List<Challenge> challenges = new ArrayList<Challenge>();
		for(DBObject challenge: (List<DBObject>)merObj.get(CHALLENGES)){
			String challengeId = safeString(challenge,CHALLENGEID_TAG);
			//Logger.info(ChallengeDAO.getInstance().findChallengeById(challengeId).toString());
			if(ChallengeDAO.getInstance().findChallengeById(challengeId) != null){
				challenges.add(ChallengeDAO.getInstance().findChallengeById(challengeId));
			}
		}
		smallCluster.setChallenges(challenges);

		return smallCluster;
	}	
}
