package SingaporeJoggingSpotApp;


/**
 * I declare that this code was written by me.
 * I will not copy or allow others to copy my code.
 * I understand that copying code is considered as plagiarism.
 *
 * Chai Yong Chen, 21007175, Jun 15, 2022 9:10:27 PM
 */

public class ParkConnector extends JoggingSpot {

	private double distance;
	
	public ParkConnector(String id, String name, double distance) {
		super(id, name);
		this.distance = distance;
	}

	public double getDistance() {
		return distance;
	}

	@Override
	public String display() {
		String output = "";
		output += String.format("%-8s %-40s %-5.1fkm\n", getId(), getName(), getDistance());
		
		return output;		
	}
	
	
}
