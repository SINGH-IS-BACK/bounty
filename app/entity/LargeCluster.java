package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import play.libs.Json;
import util.Constants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dao.LargeClusterDAO;
import dao.SmallClusterDAO;

public class LargeCluster {
	private String clusterId;
	private Location center;
	private List<SmallCluster> smallClusters;
	
	public String getClusterId() {
		return clusterId;
	}
	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}
	public Location getCenter() {
		return center;
	}
	public void setCenter(Location center) {
		this.center = center;
	}
	public List<SmallCluster> getSmallClusters() {
		return smallClusters;
	}
	public void setSmallClusters(List<SmallCluster> smallClusters) {
		this.smallClusters = smallClusters;
	}
	
	public static void  deleteLargeCluster(LargeCluster largeCluster) throws Exception{
		try{
			LargeClusterDAO.getInstance().deleteLargeClusterById(largeCluster.getClusterId());
		}
		catch(Exception e){
			throw new Exception ("Delete Large Cluster failed" + e.getMessage());
		}
	}

	public static void addLargeCluster(SmallCluster smallCluster) throws Exception{
		//create a new large cluster with this small cluster as center
		// add all the small center which lies in this large cluster
		LargeCluster largeCluster = new LargeCluster();
		ArrayList<SmallCluster> internalSmallClusters = new ArrayList<SmallCluster>();
		//internalSmallClusters.add(smallCluster);
		//largeCluster.setSmallClusters(internalSmallClusters);
		Location center = new Location();
		center.setLatitude(smallCluster.getCenter().getLatitude());
		center.setLongitude(smallCluster.getCenter().getLongitude());
		largeCluster.setCenter(center);
		
		ArrayList<SmallCluster> SmallClusterList = SmallClusterDAO.getInstance().getAllSmallClusters();
	
		for(SmallCluster allsmallCluster : SmallClusterList){
			if(largeCluster.getCenter().distance( allsmallCluster.getCenter() ) < Constants.LARGECLUSTER_RADIUS ){
				internalSmallClusters.add(allsmallCluster);
			}
		}
		
		largeCluster.setSmallClusters(internalSmallClusters);
		LargeClusterDAO.getInstance().insertLargeCluster(largeCluster);
		
	}
	
	
	public JsonNode toJson(final Location userLocation){
		ObjectNode result = Json.newObject();
		result.put("clusterId", getClusterId());
		result.put("center", center.toJson());
		
		List<SmallCluster> smallClusters = getSmallClusters();
		Collections.sort(smallClusters, new Comparator<SmallCluster>() {
	        @Override
			public int compare(SmallCluster o1, SmallCluster o2) {
				// TODO Auto-generated method stub
	        	if (o1.getCenter().distance(userLocation) == o2.getCenter().distance(userLocation))
					return 0;
	        	else if(o1.getCenter().distance(userLocation) > o2.getCenter().distance(userLocation))
	        		return 1;
	        	else if(o1.getCenter().distance(userLocation) < o2.getCenter().distance(userLocation))
	        		return -1;
	        	return 0;
			}
	    });
		
		ArrayNode clusterArr = new ArrayNode(JsonNodeFactory.instance);
		int smallClusterCount = 0;
		for(SmallCluster smallCluster : getSmallClusters()){
			if(smallCluster.toJsonWithoutChallengeDetails() != null){
				smallClusterCount++;
				clusterArr.add(smallCluster.toJsonWithoutChallengeDetails());
			}
		}
		if(smallClusterCount == 0)
			return null;
		result.put("smallClusters", clusterArr);
		return result;
	}
}
