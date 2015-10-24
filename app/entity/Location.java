package entity;

import play.Logger;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Location {
	private double latitude;
	private double longitude;
	
	
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public JsonNode toJson(){
		ObjectNode result = Json.newObject();
		result.put("latitude", getLatitude());
		result.put("longitude", getLongitude());
		return result;
	}
	
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}
	
	private double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}

	public double distance(Location Blocation){
		
		double lat1 = this.getLatitude();
		double lon1 = this.getLongitude();
		double lat2 = Blocation.getLatitude();
		double lon2 =  Blocation.getLongitude();
		//double latitudeDifference = Math.abs(this.getLatitude() - Blocation.getLatitude());
		//double longitudeDifference = Math.abs(this.getLongitude() - Blocation.getLongitude());
		//return Math.sqrt(latitudeDifference*latitudeDifference + longitudeDifference*longitudeDifference);
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;	//in Miles
		dist = dist * 1.609344;		//in Kms
		return dist;
	}
}