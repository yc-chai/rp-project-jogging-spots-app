package SingaporeJoggingSpotApp;


/**
 * I declare that this code was written by me.
 * I will not copy or allow others to copy my code.
 * I understand that copying code is considered as plagiarism.
 *
 * Chai Yong Chen, 21007175, Jul 26, 2022 10:21:11 PM
 */

public abstract class JoggingSpot {

	private String id;
	private String name;
	
	public JoggingSpot(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public abstract String display();

}
