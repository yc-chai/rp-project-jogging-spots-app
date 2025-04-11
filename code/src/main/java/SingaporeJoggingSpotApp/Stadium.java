package SingaporeJoggingSpotApp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * I declare that this code was written by me.
 * I will not copy or allow others to copy my code.
 * I understand that copying code is considered as plagiarism.
 *
 * Chai Yong Chen, 21007175, Jul 26, 2022 10:23:14 PM
 */

public class Stadium extends JoggingSpot implements Unavailability {

	private LocalTime closingTime;
	
	public Stadium(String id, String name, LocalTime closingTime) {
		super(id, name);
		this.closingTime = closingTime;
	}
	
	public LocalTime getClosingTime() {
		return closingTime;
	}

	@Override
	public String announceUnavailability(String id) {
		
		String output = "";
		
		try {
			
			DBUtil.init("jdbc:mysql://127.0.0.1:3306", "root" , "SecretPassw0rd");
			
			// Select the specified Stadium 
			String sql = "SELECT * FROM unavailability_date WHERE id='" + id + "';";
			ResultSet rs = DBUtil.getTable(sql);
			
			while(rs.next()) {
				
				LocalDate eventDate = rs.getDate("dateunavailable").toLocalDate();
				String event = rs.getString("event");
				
				LocalDate today = LocalDate.now();
				LocalDate dateAft2Weeks = eventDate.plusWeeks(2);
				
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM YYYY");
				String stringToday = today.format(dtf);
				String stringEventDate = eventDate.format(dtf);
				String stringDateAft2Weeks = dateAft2Weeks.format(dtf);
				
				boolean printHeader = false;
				
				// find the range date is not before eventday and date is not 2 weeks after event date.
				if (!today.isBefore(eventDate) && !today.isAfter(dateAft2Weeks)) {
					
					if (!printHeader) {
						output += "\n------------------------------------------------------------------------------\n";
						output += "Unavailability Date for " +id+ " :\n";
					}
					
					// If the event date is already started, return today 
					if (eventDate.isBefore(today)) {
						output += String.format("%-12s - %-12s   %-40s\n", stringToday, stringDateAft2Weeks, event);
					} else {
						output += String.format("%-12s - %-12s   %-40s\n", stringEventDate, stringDateAft2Weeks, event);
					}
				}			
		
			}
			
			DBUtil.close();
			
		} catch (SQLException sqlex) {
			System.out.println("Error : " + sqlex.getMessage());
		}
		
		return output;
	}

	@Override
	public String display() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm a");
		String stringClosingTime = getClosingTime().format(dtf);
		
		String output = String.format("%-8s %-40s %-5s\n", getId(), getName(), stringClosingTime);
		
		return output;
	}


}
