package dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import entity.Invite;


public class InviteDAO extends MongoDAOBase{
	private static final String INVITECODE_COLLECTION = "inviteCodeCollection";
	private static final String NEWINVITECODE = "newInviteCode";
	private static final String GIVENINVITECODE = "givenInviteCode";
	private static final String USEDINVITECODE = "usedInviteCode";
	private static final String INVITECODE = "inviteCode";
	

	//private 
	private static InviteDAO instance;
	
	private InviteDAO(){
		super();
	}
	
	public static InviteDAO getInstance(){
		if(instance == null){
			instance = new InviteDAO();
		}
		return instance;
	}
	
	public String insertInviteCode(Invite inviteCode){
		DBCollection chColl = db.getCollection(INVITECODE_COLLECTION);
	    BasicDBObject insertQ = generateQuery(inviteCode); 
	    chColl.insert(insertQ);
		return insertQ.get(ID).toString();
	}
	
	
	public void updateInviteCode(Invite inviteCode){
		DBCollection chColl = db.getCollection(INVITECODE_COLLECTION);
	    BasicDBObject updateQ = generateQuery(inviteCode); 
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(ID, new ObjectId(inviteCode.getInviteCodeId()));
		chColl.update(whereQuery, new BasicDBObject().append("$set", updateQ));
	}

	private BasicDBObject generateQuery(Invite inviteCode) {
		BasicDBObject document = new BasicDBObject();
		List<DBObject> inviteCodesDBObject = new ArrayList<DBObject>();
		for(int i = 0 ; i < inviteCode.getNewinviteCodes().size() ; i++){
			BasicDBObject inviteCodeObj = new BasicDBObject();
			inviteCodeObj.put(INVITECODE, inviteCode.getNewinviteCodes().get(i));
			inviteCodesDBObject.add(inviteCodeObj);
		}
		document.put(NEWINVITECODE, inviteCodesDBObject);
		
		inviteCodesDBObject = new ArrayList<DBObject>();
		for(int i = 0 ; i < inviteCode.getGiveninviteCodes().size() ; i++){
			BasicDBObject inviteCodeObj = new BasicDBObject();
			inviteCodeObj.put(INVITECODE, inviteCode.getGiveninviteCodes().get(i));
			inviteCodesDBObject.add(inviteCodeObj);
		}
		document.put(GIVENINVITECODE, inviteCodesDBObject);
		
		inviteCodesDBObject = new ArrayList<DBObject>();
		for(int i = 0 ; i < inviteCode.getUsedinviteCodes().size() ; i++){
			BasicDBObject inviteCodeObj = new BasicDBObject();
			inviteCodeObj.put(INVITECODE, inviteCode.getUsedinviteCodes().get(i));
			inviteCodesDBObject.add(inviteCodeObj);
		}
		document.put(USEDINVITECODE, inviteCodesDBObject);
		
		return document;
	}
	
	public ArrayList<Invite> getAllInviteCodes() throws Exception{
		ArrayList<Invite> invitecodes = new ArrayList<Invite>();
		DBCollection chColl = db.getCollection(INVITECODE_COLLECTION);
		BasicDBObject orderBy = new BasicDBObject();
		orderBy.put("$natural", -1);
		DBCursor cursor = chColl.find().sort(orderBy);
		while(cursor.hasNext()){
			invitecodes.add(convertToInviteCode(cursor.next()));
		}
		return invitecodes;
	}
	
	@SuppressWarnings("unchecked")
	private Invite convertToInviteCode(DBObject chObj) throws Exception{	
		Invite inviteCode = new Invite();
		inviteCode.setInviteCodeId(chObj.get(ID).toString());
		ArrayList<String> inviteCodes = new ArrayList<String>();
		for(DBObject inviteCodeObj: (List<DBObject>)chObj.get(NEWINVITECODE)){
				inviteCodes.add(safeString(inviteCodeObj, INVITECODE));
		}
		inviteCode.setNewinviteCodes(inviteCodes);
		
		
		inviteCodes = new ArrayList<String>();
		for(DBObject inviteCodeObj: (List<DBObject>)chObj.get(GIVENINVITECODE)){
				inviteCodes.add(safeString(inviteCodeObj, INVITECODE));
		}
		inviteCode.setGiveninviteCodes(inviteCodes);
		inviteCodes = new ArrayList<String>();
		for(DBObject inviteCodeObj: (List<DBObject>)chObj.get(USEDINVITECODE)){
				inviteCodes.add(safeString(inviteCodeObj, INVITECODE));
		}
		inviteCode.setUsedinviteCodes(inviteCodes);
		return inviteCode;

	}


	
}
