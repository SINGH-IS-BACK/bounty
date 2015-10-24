package controllers;

import java.util.ArrayList;
import java.util.List;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import util.Constants;
import util.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dao.LargeClusterDAO;
import dao.SmallClusterDAO;
import entity.LargeCluster;
import entity.Location;
import entity.SmallCluster;


public class ClusterController extends BaseController{
	private static final String SMALLCLUSTER_TAG = "smallCluster";
	
	public static Result getSmallCluster(){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		if(!Utils.checkJsonInput(request())){
			Logger.info("Bad request data for watch request "+request().body());
			return generateBadRequest("Bad input json");
		}
		JsonNode jsonReq = request().body().asJson();
		try{
			String userId = Utils.safeStringFromJson(jsonReq,"userId");
			String smallClusterId = Utils.safeStringFromJson(jsonReq,"clusterId");
			SmallCluster smallCluster = SmallClusterDAO.getInstance().findSmallClusterById(smallClusterId);
			return ok(smallCluster.toJsonWithChallengeDetails(userId));
		}
		catch(Exception e){
			return generateInternalServer("Fail to find Small Cluster Id \n" + e.getMessage());
		}
	}
	
	public static Result getLargeCluster(){
		if(!Utils.checkCredentials(request())){
			return unauthorized();
		}
		if(!Utils.checkJsonInput(request())){
			Logger.info("Bad request data for watch request "+request().body());
			return generateBadRequest("Bad input json");
		}
		JsonNode jsonReq = request().body().asJson();
		Location location = new Location();
		try{
			Double longitude = jsonReq.get("longitude").asDouble();
			Double latitude =jsonReq.get("latitude").asDouble();
			location.setLatitude(latitude);
			location.setLongitude(longitude);
		}
		catch(Exception e){
			return generateBadRequest("Longitude or latitude not Valid :" + e.toString());
		}
		Double min = Double.MAX_VALUE;
		LargeCluster nearestLargeCluster = new LargeCluster();
		try{
			ArrayList<LargeCluster> largeClusters = LargeClusterDAO.getInstance().getAllLargeClusters();
			for(LargeCluster largeCluster : largeClusters){
				if(min > largeCluster.getCenter().distance(location)  && (largeCluster.getCenter().distance(location) < Constants.LARGECLUSTER_RADIUS)){
					min = largeCluster.getCenter().distance(location);
					nearestLargeCluster = largeCluster;
				}
			}
			if(min == Double.MAX_VALUE){
				return noContent();
			}else{
				if(nearestLargeCluster.toJson(location) == null){
					return noContent();
				}
				return ok(nearestLargeCluster.toJson(location));
			}
		}
		catch(Exception e){
			return generateInternalServer("Invalid Large Cluster Id \n" + e.getMessage());
		}
		
	}
	
	public static Result getAllSmallCluster(String LargeClusterID){
		try {
			if(!Utils.checkCredentials(request())){
				return unauthorized();
			}
			//go through only small clusters in this large Cluster
			List<SmallCluster> smallClusters = SmallClusterDAO.getInstance().getAllSmallClusters();
			ObjectNode result = Json.newObject();
			ArrayNode resultArr = new ArrayNode(JsonNodeFactory.instance);
			for(SmallCluster smallCluster: smallClusters){
				if(smallCluster.toJsonWithoutChallengeDetails() != null){
					resultArr.add(smallCluster.toJsonWithoutChallengeDetails());
				}
			}
			result.put(SMALLCLUSTER_TAG, resultArr);
			return ok(result);
		} catch (Exception e) {
			return generateInternalServer(e.getMessage());
		}
	}
}
