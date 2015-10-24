package entity;

import java.util.ArrayList;
import java.util.List;

public class Invite {
	private String InviteCodeId;
	private List<String> newinviteCodes = new ArrayList<String>();
	private List<String> giveninviteCodes = new ArrayList<String>();
	private List<String> usedinviteCodes = new ArrayList<String>();
	
	public String getInviteCodeId() {
		return InviteCodeId;
	}
	public void setInviteCodeId(String inviteCodeId) {
		InviteCodeId = inviteCodeId;
	}
	public List<String> getNewinviteCodes() {
		return newinviteCodes;
	}
	public void setNewinviteCodes(List<String> newinviteCodes) {
		this.newinviteCodes = newinviteCodes;
	}
	public List<String> getGiveninviteCodes() {
		return giveninviteCodes;
	}
	public void setGiveninviteCodes(List<String> giveninviteCodes) {
		this.giveninviteCodes = giveninviteCodes;
	}
	public List<String> getUsedinviteCodes() {
		return usedinviteCodes;
	}
	public void setUsedinviteCodes(List<String> usedinviteCodes) {
		this.usedinviteCodes = usedinviteCodes;
	}

	
	
	/*public String getInviteCode(){
		inviteCodes = MasterUserDAO.getInstance().get
	}*/
}
