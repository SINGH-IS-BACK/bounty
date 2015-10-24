package dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import entity.Activity;
import entity.Comment;

public class ActivityDAO extends MongoDAOBase{

	//schema: fromUserId : <userId>, fromMerchantId : <merchantId>, activityType : <type>, likableFlag : <t/f>, likeCount : <int>, description: <desc>, createTime: <long>
	private static final String ACTIVITY_COLLECTION = "activityCollection";
	private static final String USER_NAME = "fromUserId";
	private static final String MERCHANT_NAME = "fromMerchantId";
	private static final String ACTIVITY_TYPE = "activityType";
	private static final String LIKE_FLAG = "likableFlag";
	private static final String LIKE_COUNT = "likeCount";
	private static final String DESC = "description";
	private static final String CREATE_TIME = "createTime";
	private static final String COMMENTS = "comments";
	private static final String COMMENT_TIME = "commentTime";
	private static final String COMMENT_TEXT = "commentText";
	private static final String COMMENT_USERID = "commentUserId";
	private static final String LIKEDBY = "likedBy";
	private static final String LIKEDBY_DESC = "likedByDesc";
	
	private static ActivityDAO instance;
	
	private ActivityDAO(){
		super();
	}
	
	public static ActivityDAO getInstance(){
		if(instance == null){
			instance = new ActivityDAO();
		}
		return instance;
	}
	
	public void dumpData(String dump){
		DBCollection activityColl = db.getCollection("dump");
		BasicDBObject document = new BasicDBObject();
		document.put("Data", dump);
		activityColl.insert(document);
	}
	
	public String insertActivity(Activity activity){
		DBCollection activityColl = db.getCollection(ACTIVITY_COLLECTION);
		BasicDBObject document = generateQuery(activity);
		activityColl.insert(document);
		return document.get(ID).toString();
	}

	public void updateActivity(Activity activity){
		DBCollection acColl = db.getCollection(ACTIVITY_COLLECTION);
	    BasicDBObject updateQ = generateQuery(activity);
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(ID, new ObjectId(activity.getActivityId()));
		acColl.update(whereQuery, new BasicDBObject().append("$set", updateQ));
	}

	private BasicDBObject generateQuery(Activity activity) {
		BasicDBObject document = new BasicDBObject();
		if(activity.getFromUser() != null){			
			document.put(USER_NAME, activity.getFromUser());
		}
		if(activity.getFromMerchant() != null){			
			document.put(MERCHANT_NAME, activity.getFromMerchant());
		}
		document.put(ACTIVITY_TYPE, activity.getActivityType().name());
		document.put(LIKE_FLAG, activity.isLikableFlag());
		document.put(LIKE_COUNT, activity.getLikedBy().size());
		document.put(DESC, activity.getDescription());
		document.put(CREATE_TIME, activity.getCreateTime());
		List<DBObject> comments = new ArrayList<DBObject>();
		for(Comment comment : activity.getComments()){
			BasicDBObject commentObj = new BasicDBObject();
			commentObj.put(COMMENT_TIME, comment.getTime());
			commentObj.put(COMMENT_TEXT, comment.getText());
			commentObj.put(COMMENT_USERID, comment.getUserId());
			comments.add(commentObj);
		}
		document.put(COMMENTS, comments);
		List<DBObject> likes = new ArrayList<DBObject>();
		for(String likedBy : activity.getLikedBy()){
			BasicDBObject likesObj = new BasicDBObject();
			likesObj.put(LIKEDBY_DESC, likedBy);
			likes.add(likesObj);
		}
		document.put(LIKEDBY, likes);
		return document;
	}

	public Activity findActivityById(String activityId) throws Exception{
		DBCollection actColl = db.getCollection(ACTIVITY_COLLECTION);
		BasicDBObject query = new BasicDBObject();
	    query.put(ID, new ObjectId(activityId));
	    DBCursor cursor = actColl.find(query);
		if(!cursor.hasNext()){
			throw new Exception("Activity not found");
		}	
	    return convertToActivity(cursor.next());
	}

	public List<Activity> getAllActivities(int start, int count) throws Exception{
		List<Activity> activities = new ArrayList<Activity>();
		DBCollection actColl = db.getCollection(ACTIVITY_COLLECTION);
		BasicDBObject orderBy = new BasicDBObject();
		orderBy.put("$natural", 1);
		DBCursor cursor = actColl.find().sort(orderBy);
		if(start > 0){
			cursor.skip(start);
		}
		if(count > 0){
			cursor.limit(count);
		}
		while(cursor.hasNext()){
			activities.add(convertToActivity(cursor.next()));
		}
		return activities;
	}
		
	private Activity convertToActivity(DBObject actObj) throws Exception {
		Activity act = new Activity();
		act.setActivityId(actObj.get(ID).toString());
		String userName = safeString(actObj,USER_NAME);
		if(StringUtils.isNotEmpty(userName)){
			act.setFromUser(userName);			
		}
		String merchantName = safeString(actObj,MERCHANT_NAME);
		if(StringUtils.isNotEmpty(merchantName)){
			act.setFromMerchant(merchantName);			
		}
		String actType = safeString(actObj,ACTIVITY_TYPE);
		act.setActivityType(Activity.ActivityType.valueOf(actType));
		act.setLikableFlag(safeBoolean(actObj, LIKE_FLAG));
		act.setLikeCount(safeInt(actObj, LIKE_COUNT));
		act.setDescription(safeString(actObj, DESC));
		act.setCreateTime(safeLong(actObj,CREATE_TIME));
		
		List<Comment> comments = new ArrayList<Comment>();
		for(DBObject commentObj: (List<DBObject>)actObj.get(COMMENTS)){
			Comment comment = new Comment();
			comment.setTime(safeLong(commentObj,COMMENT_TIME));
			comment.setText(safeString(commentObj,COMMENT_TEXT));
			comment.setUserId(safeString(commentObj,COMMENT_USERID));
			comments.add(comment);
	//		comments.add(safeString(comment,COMMENT_DESC));
		}
		act.setComments(comments);
		List<String> likes = new ArrayList<String>();
		for(DBObject likedBy: (List<DBObject>)actObj.get(LIKEDBY)){
			likes.add(safeString(likedBy,LIKEDBY_DESC));
		}
		act.setLikedBy(likes);
		
		return act;
	}
	
}

