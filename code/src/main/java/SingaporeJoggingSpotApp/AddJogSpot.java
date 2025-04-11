package SingaporeJoggingSpotApp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

/**
 * I declare that this code was written by me.
 * I will not copy or allow others to copy my code.
 * I understand that copying code is considered as plagiarism.
 *
 * Chai Yong Chen, 21007175, Jul 27, 2022 10:46:48 PM
 */

public class AddJogSpot extends Application {

	private BorderPane pane = new BorderPane();
	private VBox centralVbox = new VBox();
	private VBox bottomVbox = new VBox();
	private HBox bottomHbox = new HBox();

	// To centralVBox
	private VBox nameVbox = new VBox();
	private VBox categoryVbox = new VBox();
	private VBox otherCaptureVbox = new VBox();
	private HBox stadiumHBox = new HBox();

	private Label labelName = new Label("Name: ");
	private Label labelCategory = new Label("Category: ");
	private Label labelOtherCapture = new Label();
	private Label labelColon = new Label(":");
	private Label labelHours = new Label("HRS");
	private Label labelSubmitButtonMessage = new Label();

	private TextField textfieldName = new TextField();	
	private TextField textfieldDistance = new TextField();
	private TextField textfieldClosingTimeHrs = new TextField();
	private TextField textFieldClosingTimeMin = new TextField();

	private Button buttonClose = new Button("Close");
	private Button buttonSubmit = new Button("Submit");

	private ChoiceBox<String> choiceboxCategory = new ChoiceBox<String>();
	private ChoiceBox<String> choiceboxHasSeaview = new ChoiceBox<String>();

	private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306";
	private static final String DB_USERNAME = "root";
	private static final String DB_PASSWORD = "SecretPassw0rd";

	private static final String DISTANCE_FIELD_REGEX = "(\\d+.\\d+|\\d+)";
	private static final String CLOSING_TIME_HRS_REGEX = "[0-9]|[0-1][0-9]|2[0-3]";
	private static final String CLOSING_TIME_MIN_REGEX = "[0-9]|[0-5][0-9]";

	private ArrayList<JoggingSpot> js = new ArrayList<JoggingSpot>();

	public void start(Stage primaryStage) {

		textfieldName.setPrefColumnCount(10);
		textfieldName.setMaxWidth(200);

		choiceboxCategory.setMaxWidth(200);
		choiceboxCategory.getItems().addAll("Park", "Park Connector", "Stadium");

		// Park
		choiceboxHasSeaview.setMaxWidth(200);
		choiceboxHasSeaview.getItems().addAll("Yes", "No");

		// Park Connector
		textfieldDistance.setPrefColumnCount(10);
		textfieldDistance.setMaxWidth(200);

		// Stadium
		textfieldClosingTimeHrs.setPrefColumnCount(5);
		textfieldClosingTimeHrs.setMaxWidth(80);
		textFieldClosingTimeMin.setPrefColumnCount(5);
		textFieldClosingTimeMin.setMaxWidth(80);
		stadiumHBox.setSpacing(5);
		stadiumHBox.setAlignment(Pos.CENTER);
		stadiumHBox.getChildren().addAll(textfieldClosingTimeHrs, labelColon, textFieldClosingTimeMin, labelHours);

		// centralVbox Part
		nameVbox.setPadding(new Insets(10,10,10,10));
		nameVbox.setAlignment(Pos.CENTER);
		nameVbox.getChildren().addAll(labelName, textfieldName);

		categoryVbox.setPadding(new Insets(10,10,10,10));		
		categoryVbox.setAlignment(Pos.CENTER);
		categoryVbox.getChildren().addAll(labelCategory, choiceboxCategory);

		otherCaptureVbox.setVisible(false);
		otherCaptureVbox.setPadding(new Insets(10,10,10,10));
		otherCaptureVbox.setAlignment(Pos.CENTER);
		otherCaptureVbox.getChildren().add(labelOtherCapture);

		centralVbox.getChildren().addAll(nameVbox, categoryVbox, otherCaptureVbox);

		// bottomVbox part
		bottomHbox.setSpacing(10);
		bottomHbox.setAlignment(Pos.CENTER);
		bottomHbox.getChildren().addAll(buttonClose ,buttonSubmit);

		bottomHbox.setPadding(new Insets(10,10,10,10));
		bottomVbox.setAlignment(Pos.CENTER);
		bottomVbox.getChildren().add(bottomHbox);

		pane.setCenter(centralVbox);
		pane.setBottom(bottomVbox);

		Scene mainScene = new Scene(pane);
		primaryStage.setScene(mainScene);

		primaryStage.setTitle("Add Jog Spot");
		primaryStage.setWidth(300);
		primaryStage.setHeight(350);

		primaryStage.show();

		EventHandler<ActionEvent> handleSelectCategory = (ActionEvent e) -> doShowOtherCapture();
		choiceboxCategory.setOnAction(handleSelectCategory);

		EventHandler<ActionEvent> handleClose = (ActionEvent e) -> primaryStage.close();
		buttonClose.setOnAction(handleClose);

		EventHandler<ActionEvent> handleAdd = (ActionEvent e) -> doAdd();
		buttonSubmit.setOnAction(handleAdd);

	}

	private void doShowOtherCapture() {

		if (choiceboxCategory.getValue().equals("Park")) {
			labelOtherCapture.setText("Has Seaview");

			otherCaptureVbox.getChildren().removeAll(textfieldDistance, stadiumHBox);
			otherCaptureVbox.getChildren().add(choiceboxHasSeaview);
			otherCaptureVbox.setVisible(true);

		} else if (choiceboxCategory.getValue().equals("Park Connector")) {
			labelOtherCapture.setText("Distance (km):");

			otherCaptureVbox.getChildren().removeAll(choiceboxHasSeaview, stadiumHBox);
			otherCaptureVbox.getChildren().add(textfieldDistance);
			otherCaptureVbox.setVisible(true);

		} else if (choiceboxCategory.getValue().equals("Stadium")) {
			labelOtherCapture.setText("Closing Time (in 24-Hour Clock):");

			otherCaptureVbox.getChildren().removeAll(choiceboxHasSeaview, textfieldDistance);
			otherCaptureVbox.getChildren().addAll(stadiumHBox);
			otherCaptureVbox.setVisible(true);
		}

	}

	private void doAdd() {

		DBUtil.init(JDBC_URL, DB_USERNAME, DB_PASSWORD);

		// Check the whether correct input
		if (doCheckField()) {

			// Auto create ID
			String id = idGenerator();
			String name = textfieldName.getText();
			boolean hasSeaview;
			double distance;

			// INSERT INTO sql
			String sql;

			// Park
			if (choiceboxCategory.getValue().equals("Park")) {

				// Check seaview from Yes/No to boolean
				if (choiceboxHasSeaview.getValue().equals("Yes")) {
					hasSeaview = true;
				} else {
					hasSeaview = false;
				}

				sql = "INSERT INTO singapore_jogging_spot_app.jogging_spot(ID,Name,Category,HasSeaview) VALUES('" +id+ "','" +name+ "','Park'," +hasSeaview+ ");";
				int RowAffeted = DBUtil.execSQL(sql);

				if (RowAffeted == 1) {
					labelSubmitButtonMessage.setTextFill(Color.web("#000000"));
					labelSubmitButtonMessage.setText("Sucessfully Add!  ID: " +id );
				} else {
					labelSubmitButtonMessage.setTextFill(Color.web("#ff0000"));
					labelSubmitButtonMessage.setText("Error! Please inform the administrator for asistance");
				}

				// Park Connector
			} else if (choiceboxCategory.getValue().equals("Park Connector")) {

				// Convert String to double
				distance = Double.parseDouble(textfieldDistance.getText());

				sql = "INSERT INTO singapore_jogging_spot_app.jogging_spot(ID,Name,Category,DistanceKm) VALUES('" +id+ "','" +name+ "','Park Connector'," +distance+ ");";
				int RowAffeted = DBUtil.execSQL(sql);

				if (RowAffeted == 1) {
					labelSubmitButtonMessage.setTextFill(Color.web("#000000"));
					labelSubmitButtonMessage.setText("Sucessfully Add!  ID: " +id );
				} else {
					labelSubmitButtonMessage.setTextFill(Color.web("#ff0000"));
					labelSubmitButtonMessage.setText("Error! Please inform the administrator for asistance");
				}

				// Stadium
			} else if (choiceboxCategory.getValue().equals("Stadium")) {

				// Convert hours field and mintues field into LocalTime
				String closingTimeInString = textfieldClosingTimeHrs.getText() + ":" + textFieldClosingTimeMin.getText() + ":00";
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("H:m:s");
				LocalTime closingTime = LocalTime.parse(closingTimeInString, dtf);

				sql = "INSERT INTO singapore_jogging_spot_app.jogging_spot(ID,Name,Category,ClosingTime) VALUES('" +id+ "','" +name+ "','Stadium','" +closingTime+ "');";
				int RowAffeted = DBUtil.execSQL(sql);

				if (RowAffeted == 1) {
					labelSubmitButtonMessage.setTextFill(Color.web("#000000"));
					labelSubmitButtonMessage.setText("Sucessfully Add!  ID: " +id );
				} else {
					labelSubmitButtonMessage.setTextFill(Color.web("#ff0000"));
					labelSubmitButtonMessage.setText("Error! Please inform the administrator for asistance");
				}
			}

		}
		
		// Reset all
		textfieldName.clear();
		choiceboxCategory.setValue("");
		choiceboxHasSeaview.setValue("");
		textfieldDistance.clear();
		textfieldClosingTimeHrs.clear();
		textFieldClosingTimeMin.clear();

		// Remove buttons because the label should display top of the buttons
		bottomVbox.getChildren().removeAll(labelSubmitButtonMessage, bottomHbox);
		bottomVbox.getChildren().addAll(labelSubmitButtonMessage, bottomHbox);

	}

	// Generate a new ID
	private String idGenerator() {

		load();
		String finalID;

		// Get the integer part, because JXXXX
		int biggestJogSptID = Integer.parseInt(js.get(0).getId().substring(1,5));

		for (JoggingSpot j : js) {
			int jogspotID = Integer.parseInt(j.getId().substring(1,5));				
			if (biggestJogSptID == jogspotID) {
				biggestJogSptID++;
			}
		}

		// Add back the J infront of the number
		finalID = "J" + biggestJogSptID;
		return finalID;
	}

	private void load() {

		try {

			// Clear the ArrayList to reload every data.
			js.clear();

			DBUtil.init(JDBC_URL, DB_USERNAME, DB_PASSWORD);
			String sql = "SELECT * FROM singapore_jogging_spot_app.jogging_spot;";

			ResultSet rs = DBUtil.getTable(sql);

			String id;
			String name;

			while (rs.next()) {
				id = rs.getString("id");
				name = rs.getString("name");

				if (rs.getString("category").equalsIgnoreCase("Park")) {
					boolean seaview = rs.getBoolean("hasseaview");
					js.add(new Park(id, name, seaview));

				} else if (rs.getString("category").equalsIgnoreCase("stadium")) {
					String timeInString = rs.getString("closingtime");
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
					LocalTime closingTime = LocalTime.parse(timeInString, dtf);
					js.add(new Stadium(id, name, closingTime));

				} else if (rs.getString("category").equalsIgnoreCase("park connector")) {
					double distance = rs.getDouble("distancekm");
					js.add(new ParkConnector(id, name, distance));
				}
			}

			DBUtil.close();

		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage());
		}

	}

	private boolean doCheckField() {
		// If statement return false, if detect wrong input
		// Test name field, category choicebox value
		if (textfieldName.getText().isBlank() || 
				textfieldName.getText().isEmpty() ||
				choiceboxCategory.getValue() == null ||

				// If choiceboxCategory choose Park, check choiceboxHasSeaview whether is empty
				(choiceboxCategory.getValue().equals("Park") 
						&& choiceboxHasSeaview.getValue() == null) ||

				// In ParkConnector, the numbers should be in 1 decimal place
				(choiceboxCategory.getValue().equals("Park Connector") 
						&& !Pattern.matches(DISTANCE_FIELD_REGEX, textfieldDistance.getText())) ||

				// In Stadium, the closingTime should be in 24 hours form (0000-2359)
				(choiceboxCategory.getValue().equals("Stadium") 
						&& (!Pattern.matches(CLOSING_TIME_HRS_REGEX, textfieldClosingTimeHrs.getText()) 
							|| !Pattern.matches(CLOSING_TIME_MIN_REGEX, textFieldClosingTimeMin.getText())) )) {

			labelSubmitButtonMessage.setTextFill(Color.web("#ff0000"));
			labelSubmitButtonMessage.setText("Please check the fill again");
			return false;

		} else {
			return true;
		}
	}


}
