package dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import entity.Gift;

public class GiftDAO extends MongoDAOBase {

	private static final String GIFT_COLLECTION = "giftCollection";
	private static final String NAME = "name";
	private static final String DESC = "description";
	private static final String AMOUNT = "amount";
	private static final String MERCHANT_ID = "merchantId";
	private static final String MAXGIFTS = "maxCount";
	private static final String USEDGIFTS = "givenCount";
	private static final String GIFT_LOGO_URL = "giftLogoUrl";
	private static final String GIFT_STATUS = "giftStatus";
	
	private static GiftDAO instance;
	
	private GiftDAO(){
		super();
	}
	
	public static GiftDAO getInstance(){
		if(instance == null){
			instance = new GiftDAO();
		}
		return instance;
	}

	public String insertGift(Gift gift){
		DBCollection giftColl = db.getCollection(GIFT_COLLECTION);
		BasicDBObject document = generateQuery(gift);
		giftColl.insert(document);
		return document.get(ID).toString();
	}
	
	public void updateGift(Gift gift){
		DBCollection giftColl = db.getCollection(GIFT_COLLECTION);
		BasicDBObject updateQ = generateQuery(gift); 
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(ID, new ObjectId(gift.getGiftId()));
		giftColl.update(whereQuery, new BasicDBObject().append("$set", updateQ));
	}

	private BasicDBObject generateQuery(Gift gift) {
		BasicDBObject document = new BasicDBObject();
		document.put(NAME, gift.getGiftName());
		document.put(DESC, gift.getDescription());
		document.put(AMOUNT, gift.getAmount());
		if(gift.getMerchantId()!=null){			
			document.put(MERCHANT_ID, gift.getMerchantId());
		}
		document.put(MAXGIFTS, gift.getMaxGifts());
		document.put(USEDGIFTS, gift.getGivenGiftCount());
		document.put(GIFT_LOGO_URL, gift.getGiftLogoUrl());
		document.put(GIFT_STATUS, gift.getGiftStatus().name());
		return document;
	}
	
	public Gift findGiftById(String giftId) throws Exception{
		DBCollection chColl = db.getCollection(GIFT_COLLECTION);
	    BasicDBObject query = new BasicDBObject();
	    query.put(ID, new ObjectId(giftId));
	    DBCursor cursor = chColl.find(query);
		if(!cursor.hasNext()){
			throw new Exception("Gift not found");
		}	
	    return convertToGift(cursor.next());
	}

	private Gift convertToGift(DBObject gObj) throws Exception {
		Gift g = new Gift();
		g.setGiftId(gObj.get(ID).toString());
		g.setGiftName(safeString(gObj,NAME));
		g.setAmount(safeDouble(gObj,AMOUNT));
		g.setDescription(safeString(gObj,DESC));
		String merchantId = safeString(gObj,MERCHANT_ID);
		if(StringUtils.isNotEmpty(merchantId)){			
			g.setMerchantId(merchantId);
		}
		else{
			g.setMerchantId("");
		}
		g.setMaxGifts(safeInt(gObj,MAXGIFTS));
		g.setGivenGiftCount(safeInt(gObj,USEDGIFTS));
		g.setGiftLogoUrl(safeString(gObj, GIFT_LOGO_URL));
		String giftType = safeString(gObj,GIFT_STATUS);
		if(StringUtils.isNotEmpty(giftType)){
			g.setGiftStatus(Gift.GiftStatus.valueOf(giftType));
		}
		return g;
	}
	
	public List<Gift> getAllGifts() throws Exception{
		List<Gift> gifts = new ArrayList<Gift>();
		DBCollection gColl = db.getCollection(GIFT_COLLECTION);
		BasicDBObject orderBy = new BasicDBObject();
		orderBy.put("$natural", -1);
		DBCursor cursor = gColl.find().sort(orderBy);
		while(cursor.hasNext()){
			gifts.add(convertToGift(cursor.next()));
		}
		return gifts;
	}
}
