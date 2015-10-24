package dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import play.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import entity.Challenge;
import entity.ChallengeStep;
import entity.ChallengeStep.StepType;
import entity.Comment;
import entity.Gift;
import entity.Location;

public class ChallengeDAO extends MongoDAOBase{
	
	private static final String CHALLENGE_COLLECTION = "challengeCollection";
	private static final String TITLE = "title";
	private static final String DESC = "description";
	private static final String START_DATE = "startDate";
	private static final String END_DATE = "endDate";
	private static final String CENTER = "center";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	private static final String IS_LOCAL = "isLocalFlag";
	private static final String CHALLENGE_TYPE = "challengeType";
	private static final String IS_ENALBLED = "isEnableFlag";
	private static final String GIFT_ID = "giftId";
	private static final String MERCHANT_ID = "merchantId";
	private static final String QRCode = "QRCode";
	private static final String IMGURL = "imgUrl";
	private static final String DISCLAIMER = "disclaimer";
	private static final String SHARE_REQUIRED = "shareFlag";
	private static final String PIC_REQUIRED = "picFlag";
	private static final String COMMENTS = "comments";
	private static final String COMMENT_TIME = "commentTime";
	private static final String COMMENT_TEXT = "commentText";
	private static final String COMMENT_USERID = "commentUserId";
	private static final String LIKEDBY = "likedBy";
	private static final String LIKEDBY_DESC = "likedByDesc";
	private static final String IS_EXPIRED = "isExpired";
	private static final String STEP_DESC = "stepDesc";
	private static final String STEPS = "steps";
	private static final String STEP_HEADER = "stepHeader";
	private static final String STEP_IMAGEURL = "stepImageURL";
	private static final String STEP_TYPE = "stepType";
	
	private static ChallengeDAO instance;
	
	private ChallengeDAO(){
		super();
	}
	
	public static ChallengeDAO getInstance(){
		if(instance == null){
			instance = new ChallengeDAO();
		}
		return instance;
	}
	
	public String insertChallenge(Challenge challenge){
		DBCollection chColl = db.getCollection(CHALLENGE_COLLECTION);
		BasicDBObject document = generateQuery(challenge);
		chColl.insert(document);
		
		return document.get(ID).toString();
	}

	private BasicDBObject generateQuery(Challenge challenge) {
		BasicDBObject document = new BasicDBObject();
		document.put(TITLE, challenge.getTitle());
		document.put(DESC, challenge.getDescription());
		document.put(START_DATE, challenge.getStartDate());
		document.put(END_DATE, challenge.getEndDate());
		BasicDBObject center_obj = new BasicDBObject();
		Location center = challenge.getCenter();
		center_obj.put(LATITUDE, center.getLatitude());
		center_obj.put(LONGITUDE, center.getLongitude());
		document.put(CENTER, center_obj);	
		document.put(IS_LOCAL, challenge.isLocalFlag());
		document.put(CHALLENGE_TYPE, challenge.getType().name());
		document.put(IS_ENALBLED, challenge.getEnableFlag());
		document.put(QRCode, challenge.getQRCode());
		document.put(GIFT_ID, challenge.getGift().getGiftId());
		if(challenge.getMerchantId()!=null){			
			document.put(MERCHANT_ID, challenge.getMerchantId());
		}
		document.put(SHARE_REQUIRED, challenge.isShareRequired());
		document.put(PIC_REQUIRED, challenge.isPicRequired());
		document.put(IMGURL, challenge.getChallengeImgUrl());
		document.put(DISCLAIMER, challenge.getDisclaimer());
		document.put(IS_EXPIRED, challenge.IsExpired());
		List<DBObject> comments = new ArrayList<DBObject>();
		for(Comment comment : challenge.getComments()){
			BasicDBObject commentObj = new BasicDBObject();
			commentObj.put(COMMENT_TIME, comment.getTime());
			commentObj.put(COMMENT_TEXT, comment.getText());
			commentObj.put(COMMENT_USERID, comment.getUserId());
			comments.add(commentObj);
		}
		document.put(COMMENTS, comments);
		List<DBObject> likes = new ArrayList<DBObject>();
		for(String likedBy : challenge.getLikedBy()){
			BasicDBObject likesObj = new BasicDBObject();
			likesObj.put(LIKEDBY_DESC, likedBy);
			likes.add(likesObj);
		}
		document.put(LIKEDBY, likes);
		//Add step
		List<DBObject> steps = new ArrayList<DBObject>();
		for(ChallengeStep step: challenge.getSteps()){
			BasicDBObject stepObj = new BasicDBObject();
			stepObj.put(STEP_HEADER, step.getHeading());
			stepObj.put(STEP_DESC, step.getStepDesc());
			stepObj.put(STEP_IMAGEURL, step.getImageURL());
			if(step.getType() != null){
				stepObj.put(STEP_TYPE, step.getType().name());
			}
			steps.add(stepObj);
		}
		document.put(STEPS, steps);
		return document;
	}
	
	public void deleteChallengeById(String id) throws Exception {
		DBCollection chColl = db.getCollection(CHALLENGE_COLLECTION);
	    BasicDBObject query = new BasicDBObject();
	    query.put(ID, new ObjectId(id));
	    if(findChallengeById(id) != null){
	    	chColl.remove(query);
	    }
	    else{
	    	throw new Exception("Challenge does not exist");
	    }
	}
	
	public Challenge findChallengeById(String id) throws Exception {
		DBCollection chColl = db.getCollection(CHALLENGE_COLLECTION);
	    BasicDBObject query = new BasicDBObject();
	    query.put(ID, new ObjectId(id));
	    DBCursor cursor = chColl.find(query);
		if(!cursor.hasNext()){
			return null;
		}	
		return convertToChallenge(cursor.next());
	}
	
	public void updateChallenge(Challenge challenge){
		DBCollection chColl = db.getCollection(CHALLENGE_COLLECTION);
		BasicDBObject updateQ = generateQuery(challenge); 
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(ID, new ObjectId(challenge.getChallengeId()));
		chColl.update(whereQuery, new BasicDBObject().append("$set", updateQ));
	}
	
	/*public ArrayList<Challenge> getAllChallenges() throws Exception{
		ArrayList<Challenge> challenges = new ArrayList<Challenge>();
		DBCollection chColl = db.getCollection(CHALLENGE_COLLECTION);
		BasicDBObject orderBy = new BasicDBObject();
		orderBy.put("$natural", -1);
		DBCursor cursor = chColl.find().sort(orderBy);
		while(cursor.hasNext()){
			challenges.add(convertToChallenge(cursor.next()));
		}
		return challenges;
	}*/
	
	public ArrayList<Challenge> getAllActiveChallenges() throws Exception{
		ArrayList<Challenge> challenges = new ArrayList<Challenge>();
		DBCollection chColl = db.getCollection(CHALLENGE_COLLECTION);
		BasicDBObject orderBy = new BasicDBObject();
		orderBy.put("$natural", -1);
		DBCursor cursor = chColl.find().sort(orderBy);
		while(cursor.hasNext()){
			Challenge challenge = convertToChallenge(cursor.next());
			if(!challenge.IsExpired())
				challenges.add(challenge);
		}
		return challenges;
	}
	
	@SuppressWarnings("unchecked")
	private Challenge convertToChallenge(DBObject chObj) throws Exception{	
		Challenge ch = new Challenge();
		ch.setChallengeId(chObj.get(ID).toString());
		ch.setTitle(safeString(chObj,TITLE));
		ch.setDescription(safeString(chObj,DESC));
		ch.setStartDate(safeLong(chObj,START_DATE));
		ch.setEndDate(safeLong(chObj,END_DATE));
		
		Location center = new Location();
		DBObject centerObj = (DBObject)chObj.get(CENTER);
		center.setLatitude(safeDouble(centerObj, LATITUDE));
		center.setLongitude(safeDouble(centerObj, LONGITUDE));
		ch.setCenter(center);
		
		ch.setLocalFlag(safeBoolean(chObj, IS_LOCAL));
		String challengeType = safeString(chObj,CHALLENGE_TYPE);
		ch.setType(Challenge.ChallengeType.valueOf(challengeType));
		ch.setEnableFlag(safeBoolean(chObj, IS_ENALBLED));
		ch.setQRCode(safeString(chObj,QRCode));
		String giftId = safeString(chObj,GIFT_ID);
		String merchantId = safeString(chObj,MERCHANT_ID);
		if(StringUtils.isNotEmpty(merchantId)){			
			ch.setMerchantId(merchantId);
		}
		else{
			ch.setMerchantId("");
		}
		ch.setGift((GiftDAO.getInstance().findGiftById(giftId)));
		ch.setChallengeImgUrl(safeString(chObj,IMGURL));
		ch.setShareRequired(safeBoolean(chObj, SHARE_REQUIRED));
		ch.setPicRequired(safeBoolean(chObj, PIC_REQUIRED));
		ch.setDisclaimer(safeString(chObj,DISCLAIMER));
		ch.IsExpired(safeBoolean(chObj,IS_EXPIRED));
		//steps
		List<Comment> comments = new ArrayList<Comment>();
		for(DBObject commentObj: (List<DBObject>)chObj.get(COMMENTS)){
			Comment comment = new Comment();
			
			comment.setTime(safeLong(commentObj,COMMENT_TIME));
			comment.setText(safeString(commentObj,COMMENT_TEXT));
			comment.setUserId(safeString(commentObj,COMMENT_USERID));
			comments.add(comment);
		}
		ch.setComments(comments);
		List<String> likes = new ArrayList<String>();
		for(DBObject likedBy: (List<DBObject>)chObj.get(LIKEDBY)){
			likes.add(safeString(likedBy,LIKEDBY_DESC));
		}
		ch.setLikedBy(likes);
		List<ChallengeStep> steps = new ArrayList<ChallengeStep>();
		for(DBObject step: (List<DBObject>)chObj.get(STEPS)){
			ChallengeStep challengeStep = new ChallengeStep();
			challengeStep.setHeading(safeString(step, STEP_HEADER));
			challengeStep.setImageURL(safeString(step, STEP_IMAGEURL));
			challengeStep.setStepDesc(safeString(step, STEP_DESC));
			String stepType = safeString(step,STEP_TYPE);
			if(StringUtils.isNotEmpty(stepType) && ChallengeStep.StepType.contains(stepType)){
				challengeStep.setType(ChallengeStep.StepType.valueOf(stepType));
			}
			steps.add(challengeStep);
		}
		ch.setSteps(steps);
		return ch;
	} 
}
