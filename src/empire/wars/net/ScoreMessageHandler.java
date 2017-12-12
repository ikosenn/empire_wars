package empire.wars.net;

import empire.wars.EmpireWars;

/**
 * Handles how scores are update on the network
 * @author peculiaryak
 *
 */
public class ScoreMessageHandler {
	Message msgPacket;
	protected EmpireWars ew;
	
	public ScoreMessageHandler(Message msg, EmpireWars ew) {
		this.msgPacket = msg;
		this.ew = ew;
		this.handle();
	}
	
	public void handle() {
		int score = Integer.parseInt(msgPacket.getMsg());
		
		if (msgPacket.getMsgType().equals("REDSCORE")) {
			this.ew.getScore().setRedTeam(score);
		} else if (msgPacket.getMsgType().equals("BLUESCORE")) {
			this.ew.getScore().setBlueTeam(score);
		}
	}
}
