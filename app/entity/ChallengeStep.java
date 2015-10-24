package entity;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ChallengeStep {
	private String stepDesc;
	private String imageURL;
	private String heading;
	private StepType type;
	public static enum StepType {QRCODE, PIC, SHARE_FACEBOOK, SHARE_TWITTER, SHARE_BOTH, SHARE_ANY;
		
	public static boolean contains(String test) {
			StepType[] stepTypes = StepType.values();
			for (StepType stepType : stepTypes) {
				if (stepType.name().equals(test)) {
					return true;
				}
			}
			return false;
		}
	}
	
	public StepType getType() {
		return type;
	}
	public void setType(StepType type) {
		this.type = type;
	}
	public String getHeading() {
		return heading;
	}
	public void setHeading(String heading) {
		this.heading = heading;
	}
	public String getStepDesc() {
		return stepDesc;
	}
	public void setStepDesc(String stepDesc) {
		this.stepDesc = stepDesc;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public JsonNode toJson(){
		ObjectNode result = Json.newObject();
		result.put("description", getStepDesc());
		result.put("imageURL", getImageURL());
		result.put("heading", getHeading());
		if(getType() != null)
			result.put("type", getType().name());
		return result;
	}
}
