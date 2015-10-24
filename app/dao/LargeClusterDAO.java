package dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import entity.Location;
import entity.LargeCluster;
import entity.SmallCluster;

public class LargeClusterDAO extends MongoDAOBase{

	private static final String LARGECLUSTER_COLLECTION = "largeClusterCollection";
	private static final String CENTER = "center";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	private static final String SMALLCLUSTERID_TAG = "smallclusterId";
	private static final String SMALLCLUSTERS = "smallclusters";
	
	private static LargeClusterDAO instance;
	
	private LargeClusterDAO(){
		super();
	}
	
	public static LargeClusterDAO getInstance(){
		if(instance == null){
			instance = new LargeClusterDAO();
		}
		return instance;
	}
	
	public void deleteLargeClusterById(String id) throws Exception {
		DBCollection chColl = db.getCollection(LARGECLUSTER_COLLECTION);
	    BasicDBObject query = new BasicDBObject();
	    query.put(ID, new ObjectId(id));
	    if(findLargeClusterById(id) != null){
	    	chColl.remove(query);
	    }
	    else
	    	throw new Exception("Large Cluster does not exist");
	}
	
	public String insertLargeCluster(LargeCluster largeClusterDAO){
		DBCollection chColl = db.getCollection(LARGECLUSTER_COLLECTION);
		BasicDBObject document = generateQuery(largeClusterDAO);
		chColl.insert(document);
		return document.get(ID).toString();
	}
	
	public LargeCluster findLargeClusterById(String id) throws Exception {
		DBCollection chColl = db.getCollection(LARGECLUSTER_COLLECTION);
	    BasicDBObject query = new BasicDBObject();
	    query.put(ID, new ObjectId(id));
	    DBCursor cursor = chColl.find(query);
		if(!cursor.hasNext()){
			throw new Exception("Large Cluster not found");
		}	
	    return convertToLargeCluster(cursor.next());
	}
	
	public void updateLargecluster(LargeCluster largeCluster){
		DBCollection chColl = db.getCollection(LARGECLUSTER_COLLECTION);
	    BasicDBObject updateQ = generateQuery(largeCluster); 
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(ID, new ObjectId(largeCluster.getClusterId()));
		chColl.update(whereQuery, new BasicDBObject().append("$set", updateQ));
	}
	
	public ArrayList<LargeCluster> getAllLargeClusters() throws Exception{
		ArrayList<LargeCluster> largeClusters = new ArrayList<LargeCluster>();
		DBCollection chColl = db.getCollection(LARGECLUSTER_COLLECTION);
		BasicDBObject orderBy = new BasicDBObject();
		orderBy.put("$natural", -1);
		DBCursor cursor = chColl.find().sort(orderBy);
		while(cursor.hasNext()){
			largeClusters.add(convertToLargeCluster(cursor.next()));
		}
		return largeClusters;
	}
	
	
	private BasicDBObject generateQuery(LargeCluster largeCluster) {
		BasicDBObject document = new BasicDBObject();
		List<DBObject> smallclusters = new ArrayList<DBObject>();
		for(SmallCluster ch: largeCluster.getSmallClusters()){
			BasicDBObject smallcluster = new BasicDBObject();
			smallcluster.put(SMALLCLUSTERID_TAG, ch.getSmallClusterId());
			smallclusters.add(smallcluster);
		}
		document.put(SMALLCLUSTERS, smallclusters);
		BasicDBObject center_obj = new BasicDBObject();
		Location center = largeCluster.getCenter();
		center_obj.put(LATITUDE, center.getLatitude());
		center_obj.put(LONGITUDE, center.getLongitude());
		
		document.put(CENTER, center_obj);	
		return document;
	}
	
	@SuppressWarnings("unchecked")
	private LargeCluster convertToLargeCluster(DBObject merObj) throws Exception {
		LargeCluster largeCluster = new LargeCluster();
		
		largeCluster.setClusterId(merObj.get(ID).toString());
		Location center = new Location();
		DBObject centerObj = (DBObject)merObj.get(CENTER);
		
		center.setLatitude(safeDouble(centerObj, LATITUDE));
		center.setLongitude(safeDouble(centerObj, LONGITUDE));
		largeCluster.setCenter(center);
		
		List<SmallCluster> smallclusters = new ArrayList<SmallCluster>();
		for(DBObject smallcluster: (List<DBObject>)merObj.get(SMALLCLUSTERS)){
			String smallclusterId = safeString(smallcluster,SMALLCLUSTERID_TAG);
			if(SmallClusterDAO.getInstance().findSmallClusterById(smallclusterId) != null){
				smallclusters.add(SmallClusterDAO.getInstance().findSmallClusterById(smallclusterId));
			}
		}
		largeCluster.setSmallClusters(smallclusters);

		return largeCluster;
	}	
}
