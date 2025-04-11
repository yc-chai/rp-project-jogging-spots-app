package SingaporeJoggingSpotApp;

// import java.util.jar.Attributes.Name;

/**
 * I declare that this code was written by me.
 * I will not copy or allow others to copy my code.
 * I understand that copying code is considered as plagiarism.
 *
 * Chai Yong Chen, 21007175, Jun 15, 2022 9:30:56 PM
 */

public class Park extends JoggingSpot{
	
	private boolean seaview;
	
	public Park(String id, String name, boolean seaview) {
		super(id, name);
		this.seaview = seaview;
	}
	
	public boolean getSeaview() {
		return seaview;
	}

	@Override
	public String display() {
		
		String output;
		
		if (seaview) {
			output = String.format("%-8s %-40s %-5s\n", getId(), getName(), "Yes");
		} else {
			output = String.format("%-8s %-40s %-5s\n", getId(), getName(), "No");
		}
		
		return output;
		
	}
		

}
