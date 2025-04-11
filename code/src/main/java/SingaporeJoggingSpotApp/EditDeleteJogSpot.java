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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * I declare that this code was written by me.
 * I will not copy or allow others to copy my code.
 * I understand that copying code is considered as plagiarism.
 *
 * Chai Yong Chen, 21007175, Jul 29, 2022 11:11:02 AM
 */

public class EditDeleteJogSpot extends Application {

	private BorderPane pane = new BorderPane();
	private VBox centralVbox = new VBox();
	private VBox bottomVbox = new VBox();

	// To centralVbox
	private HBox searchHbox = new HBox();
	private VBox nameVbox = new VBox();
	private VBox categoryVbox = new VBox();
	private VBox otherCaptureVbox = new VBox();
	private HBox stadiumHBox = new HBox();

	// To bottomVbox
	private HBox buttonHbox = new HBox();

	private Label labelID = new Label("ID : ");
	private Label labelName = new Label("Name: ");
	private Label labelCategory = new Label("Category: ");
	private Label labelOtherCapture = new Label("");
	private Label labelColon = new Label(":");
	private Label labelButtonMessage = new Label("");

	private TextField textfieldID = new TextField();
	private TextField textfieldName = new TextField();
	private TextField textfieldDistance = new TextField();
	private TextField textfieldClosingTimeHrs = new TextField();
	private TextField textFieldClosingTimeMin = new TextField();

	private Button buttonSearch = new Button("Search");
	private Button buttonClose = new Button("Close");
	private Button buttonSaveChange = new Button("Save change");
	private Button buttonDelete = new Button("Delete");
	private Button buttonReset = new Button("Reset");

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

		// Only visible when there has result
		buttonReset.setDisable(true);
		buttonSaveChange.setDisable(true);
		buttonDelete.setDisable(true);

		labelButtonMessage.setVisible(false);

		textfieldID.setPrefColumnCount(10);
		textfieldID.setMaxWidth(150);

		textfieldName.setPrefColumnCount(10);
		textfieldName.setMaxWidth(200);
		textfieldName.setDisable(true);

		choiceboxCategory.setMaxWidth(200);
		choiceboxCategory.getItems().addAll("Park", "Park Connector", "Stadium");
		choiceboxCategory.setDisable(true);

		// centralVbox
		searchHbox.setAlignment(Pos.CENTER);
		searchHbox.setSpacing(5);
		searchHbox.setPadding(new Insets(10,10,10,10));
		searchHbox.getChildren().addAll(labelID, textfieldID, buttonSearch, buttonReset);

		nameVbox.setAlignment(Pos.CENTER);
		nameVbox.setSpacing(5);
		nameVbox.setPadding(new Insets(10,10,10,10));
		nameVbox.getChildren().addAll(labelName, textfieldName);

		categoryVbox.setSpacing(5);
		categoryVbox.setPadding(new Insets(10,10,10,10));
		categoryVbox.setAlignment(Pos.CENTER);
		categoryVbox.getChildren().addAll(labelCategory, choiceboxCategory);

		otherCaptureVbox.setVisible(false);
		otherCaptureVbox.setSpacing(5);
		otherCaptureVbox.setPadding(new Insets(10,10,10,10));
		otherCaptureVbox.setAlignment(Pos.CENTER);
		otherCaptureVbox.getChildren().add(labelOtherCapture);

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
		stadiumHBox.getChildren().addAll(textfieldClosingTimeHrs, labelColon, textFieldClosingTimeMin);

		centralVbox.setSpacing(5);
		centralVbox.setPadding(new Insets(10,10,10,10));
		centralVbox.setAlignment(Pos.TOP_CENTER);
		centralVbox.getChildren().addAll(searchHbox, nameVbox, categoryVbox, otherCaptureVbox);

		// bottomVbox
		buttonHbox.setSpacing(10);
		buttonHbox.setPadding(new Insets(10,10,10,10));
		buttonHbox.setAlignment(Pos.CENTER);
		buttonHbox.getChildren().addAll(buttonClose, buttonSaveChange, buttonDelete);

		bottomVbox.setPadding(new Insets(10,10,10,10));
		bottomVbox.setAlignment(Pos.BOTTOM_CENTER);
		bottomVbox.getChildren().addAll(labelButtonMessage, buttonHbox);

		pane.setCenter(centralVbox);
		pane.setBottom(bottomVbox);

		Scene mainScene = new Scene(pane);
		primaryStage.setScene(mainScene);

		primaryStage.setTitle("Modify Jog Spot");
		primaryStage.setWidth(350);
		primaryStage.setHeight(400);

		primaryStage.show();

		EventHandler<ActionEvent> handleSearch = (ActionEvent e) -> doSearch();
		buttonSearch.setOnAction(handleSearch);

		EventHandler<ActionEvent> handleReset = (ActionEvent e) -> doReset();
		buttonReset.setOnAction(handleReset);

		EventHandler<ActionEvent> handleSelectCategory = (ActionEvent e) -> doShowOtherCapture();
		choiceboxCategory.setOnAction(handleSelectCategory);

		EventHandler<ActionEvent> handleClose = (ActionEvent e) -> primaryStage.close();
		buttonClose.setOnAction(handleClose);

		EventHandler<ActionEvent> handleSaveChange = (ActionEvent e) -> doSaveChange();
		buttonSaveChange.setOnAction(handleSaveChange);

		EventHandler<ActionEvent> handleDelete = (ActionEvent e) -> doDelete();
		buttonDelete.setOnAction(handleDelete);

	}

	private void doShowOtherCapture() {

		if (choiceboxCategory.getValue().equals("Park")) {

			labelOtherCapture.setText("Has Seaview");

			otherCaptureVbox.getChildren().removeAll(choiceboxHasSeaview, textfieldDistance, stadiumHBox);
			otherCaptureVbox.getChildren().add(choiceboxHasSeaview);
			otherCaptureVbox.setVisible(true);

		} else if (choiceboxCategory.getValue().equals("Park Connector")) {

			labelOtherCapture.setText("Distance (km):");

			otherCaptureVbox.getChildren().removeAll(choiceboxHasSeaview, textfieldDistance, stadiumHBox);
			otherCaptureVbox.getChildren().add(textfieldDistance);
			otherCaptureVbox.setVisible(true);

		} else if (choiceboxCategory.getValue().equals("Stadium")) {

			labelOtherCapture.setText("Closing Time (in 24HRS form):");

			otherCaptureVbox.getChildren().removeAll(choiceboxHasSeaview, textfieldDistance, stadiumHBox);
			otherCaptureVbox.getChildren().addAll(stadiumHBox);
			otherCaptureVbox.setVisible(true);
		}
	}

	private void doSearch() {

		load();
		boolean isFound = false;

		for (JoggingSpot j : js) {
			boolean hasId = textfieldID.getText().trim().toUpperCase().equals(j.getId());
			if (hasId) {

				isFound = true;
				labelButtonMessage.setVisible(false);
				
				textfieldID.setDisable(true);
				buttonSearch.setDisable(true);
				buttonReset.setDisable(false);
				textfieldName.setDisable(false);
				choiceboxCategory.setDisable(false);

				buttonSaveChange.setDisable(false);
				buttonDelete.setDisable(false);

				// Fill up the name into text field
				textfieldName.setText(j.getName());

				// Park
				if (j instanceof Park) {
					// Fill up category box
					choiceboxCategory.setValue("Park");

					// Detect should shown HasSeaview
					doShowOtherCapture();

					// Fill up HasSeaview box
					if (((Park) j).getSeaview() == true) {
						choiceboxHasSeaview.setValue("Yes");
					} else {
						choiceboxHasSeaview.setValue("No");
					}

					// ParkConnector
				} else if (j instanceof ParkConnector) {
					// Fill up category box
					choiceboxCategory.setValue("Park Connector");

					// Detect should shown distance
					doShowOtherCapture();

					// Fill up distance box
					double distance = ((ParkConnector) j).getDistance();
					textfieldDistance.setText(Double.toString(distance));

					// Stadium
				} else if (j instanceof Stadium) {
					// Fill up category box
					choiceboxCategory.setValue("Stadium");

					// Detect should shown distance
					doShowOtherCapture();

					// Fill up closing time box
					DateTimeFormatter dtfHour = DateTimeFormatter.ofPattern("HH");
					String timeHrs =  (((Stadium) j).getClosingTime().format(dtfHour));
					textfieldClosingTimeHrs.setText(timeHrs);

					DateTimeFormatter dtfMins = DateTimeFormatter.ofPattern("mm");
					String timeMins =  (((Stadium) j).getClosingTime().format(dtfMins));
					textFieldClosingTimeMin.setText(timeMins);
				}
			}
		}
		
		// If not in database
		if (!isFound) {
			labelButtonMessage.setVisible(true);
			labelButtonMessage.setText("The ID does not exists");
		}
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

	private void doSaveChange() {

		DBUtil.init(JDBC_URL, DB_USERNAME, DB_PASSWORD);

		// Check the whether correct input
		if (doCheckField()) {

			// Required fields
			String id = textfieldID.getText();
			String name = textfieldName.getText();
			String category;
			boolean hasSeaview;
			double distance;

			// UPDATE sql statement
			String editSQL;

			// Park
			if (choiceboxCategory.getValue().equals("Park")) {
				category = "Park";

				// HasSeaview, from Yes/No to boolean
				if (choiceboxHasSeaview.getValue().equals("Yes")) {
					hasSeaview = true;
				} else {
					hasSeaview = false;
				}

				editSQL = "UPDATE singapore_jogging_spot_app.jogging_spot SET name='" +name+ "', category='" +category+ "', hasseaview=" +hasSeaview+ ", distancekm=null, closingtime=null WHERE id='" +id+ "';";
				int RowAffeted = DBUtil.execSQL(editSQL);

				if (RowAffeted == 1) {
					labelButtonMessage.setVisible(true);
					labelButtonMessage.setTextFill(Color.web("#000000"));
					labelButtonMessage.setText("Sucessfully Change!");
				} else {
					labelButtonMessage.setVisible(true);
					labelButtonMessage.setTextFill(Color.web("#ff0000"));
					labelButtonMessage.setText("Error! Please inform the administrator for asistance");
				}

				// Park Connector
			} 	else if (choiceboxCategory.getValue().equals("Park Connector")) {
				category = "Park Connector";

				// Stirng to double
				distance = Double.parseDouble(textfieldDistance.getText());

				editSQL = "UPDATE singapore_jogging_spot_app.jogging_spot SET name='" +name+ "', category='" +category+ "', distancekm=" +distance+ ", hasseaview=null, closingtime=null WHERE id='" +id+ "';";
				int RowAffeted = DBUtil.execSQL(editSQL);

				if (RowAffeted == 1) {
					labelButtonMessage.setVisible(true);
					labelButtonMessage.setTextFill(Color.web("#000000"));
					labelButtonMessage.setText("Sucessfully Change!");
				} else {
					labelButtonMessage.setVisible(true);
					labelButtonMessage.setTextFill(Color.web("#ff0000"));
					labelButtonMessage.setText("Error! Please inform the administrator for asistance");
				}

				// Stadium
			} else if (choiceboxCategory.getValue().equals("Stadium")) {
				category = "Stadium";

				// getText from two textfield, convert to LocalTime
				String closingTimeInString = textfieldClosingTimeHrs.getText() + ":" + textFieldClosingTimeMin.getText() + ":00";
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("H:m:s");
				LocalTime closingTime = LocalTime.parse(closingTimeInString, dtf);

				editSQL = "UPDATE singapore_jogging_spot_app.jogging_spot SET name='" +name+ "', category='" +category+ "', closingtime='" +closingTime+ "', hasseaview=null, distancekm=null WHERE id='" +id+ "';";
				int RowAffeted = DBUtil.execSQL(editSQL);

				if (RowAffeted == 1) {
					labelButtonMessage.setVisible(true);
					labelButtonMessage.setTextFill(Color.web("#000000"));
					labelButtonMessage.setText("Sucessfully Change!");
				} else {
					labelButtonMessage.setVisible(true);
					labelButtonMessage.setTextFill(Color.web("#ff0000"));
					labelButtonMessage.setText("Error! Please inform the administrator for asistance");
				}
			}
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

			labelButtonMessage.setVisible(true);
			labelButtonMessage.setTextFill(Color.web("#ff0000"));
			labelButtonMessage.setText("Please check the fill again");
			return false;

		} else {
			return true;
		}
	}

	private void doDelete() {

		DBUtil.init(JDBC_URL, DB_USERNAME, DB_PASSWORD);

		String id = textfieldID.getText();
		String deleteSQL = "DELETE FROM singapore_jogging_spot_app.jogging_spot WHERE id='" +id+ "';";
		int rowsAffected = DBUtil.execSQL(deleteSQL);

		if (rowsAffected == 1) {
			labelButtonMessage.setVisible(true);
			labelButtonMessage.setTextFill(Color.web("#000000"));
			labelButtonMessage.setText("Sucessfully Deleted!");
			
			doReset();
			
		} else {
			labelButtonMessage.setVisible(true);
			labelButtonMessage.setTextFill(Color.web("#ff0000"));
			labelButtonMessage.setText("Error! Please inform the administrator for asistance");
		}
	}

	private void doReset() {
		
		// Search ID Hbox
		textfieldID.clear();
		textfieldID.setDisable(false);
		
		buttonSearch.setDisable(false);
		
		buttonReset.setDisable(true);
		
		// Name field Hbox
		textfieldName.clear();
		textfieldName.setDisable(true);
		
		// Category Hbox
		choiceboxCategory.setValue("");
		choiceboxCategory.setDisable(true);
		
		// Other Capture Hbox
		otherCaptureVbox.setVisible(false);
		
		buttonSaveChange.setDisable(true);
		buttonDelete.setDisable(true);
		
		
	}
}
