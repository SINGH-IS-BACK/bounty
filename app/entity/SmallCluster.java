package entity;

import java.util.ArrayList;
import java.util.List;

import play.Logger;
import play.libs.Json;
import util.Constants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.ChallengeController;

import dao.ChallengeDAO;
import dao.LargeClusterDAO;
import dao.SmallClusterDAO;

public class SmallCluster {
	
	private String smallClusterId;
	private Location center;
	private List<Challenge> challenges;
	
	public Location getCenter() {
		return center;
	}
	public String getSmallClusterId() {
		return smallClusterId;
	}
	public void setSmallClusterId(String smallClusterId) {
		this.smallClusterId = smallClusterId;
	}
	public void setCenter(Location center) {
		this.center = center;
	}
	public List<Challenge> getChallenges() {
		return challenges;
	}
	public void setChallenges(List<Challenge> challenges) {
		this.challenges = challenges;
	}
	
	public JsonNode toJsonWithChallengeDetails(String userId){
		ObjectNode result = Json.newObject();
		result.put("clusterId", getSmallClusterId());
		result.put("center", center.toJson());
		ArrayNode challengeArr = new ArrayNode(JsonNodeFactory.instance);
		User user = new User();
		int count = 0;
		for(Challenge challenge : getChallenges()){
			if(ChallengeController.checkValidLocalChallenge(challenge, user)){
				challengeArr.add(challenge.toJson(userId));
				count++;
			}
		}
		result.put("numberOfChallenges", count);
		result.put("challenges", challengeArr);
		return result;
	}
	
	
	public JsonNode toJsonWithoutChallengeDetails(){
		ObjectNode result = Json.newObject();
		result.put("clusterId", getSmallClusterId());
		result.put("center", center.toJson());
		User user = new User();
		int count = 0;
		for(Challenge challenge : getChallenges()){
			if(ChallengeController.checkValidLocalChallenge(challenge, user)){
				count++;
			}
		}
		result.put("numberOfChallenges", count);
		if(count == 0)
			return null;
		return result;
	}
	
	public static List<SmallCluster> remove(List<SmallCluster> smallClusters, String smallClusterId){
		ArrayList<SmallCluster> updatedSmallClusters = new ArrayList<SmallCluster>();
		for(SmallCluster smallCluster : smallClusters){
			if(!smallCluster.getSmallClusterId().equals(smallClusterId)){
				updatedSmallClusters.add(smallCluster);
			}
		}
		return updatedSmallClusters;
	}
	
	public static void  deleteSmallCluster(SmallCluster smallCluster) throws Exception{
		try{
			String smallClusterId = smallCluster.getSmallClusterId();
			SmallClusterDAO.getInstance().deleteSmallClusterById(smallClusterId);
			ArrayList<LargeCluster> largeClusters = LargeClusterDAO.getInstance().getAllLargeClusters();
			
			for(LargeCluster largeCluster : largeClusters){
				//largeCluster.getSmallClusters().remove(smallClusterId);
				largeCluster.setSmallClusters(remove(largeCluster.getSmallClusters(), smallClusterId));
				if(largeCluster.getSmallClusters().size() == 0){
					LargeCluster.deleteLargeCluster(largeCluster);
				}
				else{
					LargeClusterDAO.getInstance().updateLargecluster(largeCluster);
				}
			}
		}
		catch(Exception e){
			throw new Exception ("Delete Small Cluster failed" + e.getMessage());
		}
	}
	
	public static void addSmallCluster(Challenge challenge) throws Exception{
		Logger.info("Adding Small Cluster. ChallengeId : " + challenge.getChallengeId());
		try{
			SmallCluster smallCluster = new SmallCluster();
			ArrayList<Challenge> challenges = new ArrayList<Challenge>();
			Location center = new Location();
			center.setLatitude(challenge.getCenter().getLatitude());
			center.setLongitude(challenge.getCenter().getLongitude());
			smallCluster.setCenter(center);

			ArrayList<Challenge> challengeList = ChallengeDAO.getInstance().getAllActiveChallenges();
			for(Challenge allchallenge : challengeList){
				if(smallCluster.getCenter().distance( allchallenge.getCenter() ) < Constants.SMALLCLUSTER_RADIUS ){
					challenges.add(allchallenge);
				}
			}
			smallCluster.setChallenges(challenges);
		
			String smallClusterID = SmallClusterDAO.getInstance().insertSmallCluster(smallCluster);
			smallCluster = SmallClusterDAO.getInstance().findSmallClusterById(smallClusterID);
			int numberOfLargeClusterhavingSmallCluster = 0;
			ArrayList<LargeCluster> largeClusters = LargeClusterDAO.getInstance().getAllLargeClusters();
			for(LargeCluster largeCluster : largeClusters){
				if(smallCluster.getCenter().distance(largeCluster.getCenter()) < Constants.LARGECLUSTER_RADIUS){
					numberOfLargeClusterhavingSmallCluster++;
					largeCluster.getSmallClusters().add(smallCluster);
					LargeClusterDAO.getInstance().updateLargecluster(largeCluster);
				}
			}
			if(numberOfLargeClusterhavingSmallCluster == 0){
				LargeCluster.addLargeCluster(smallCluster);
			}
		}
		catch(Exception e){
			Logger.error("Adding Small Cluster Failed. : " + e.getMessage());
			throw new Exception ("Adding Small Cluster failed" + e.getMessage());
		}
	}
}
