package SingaporeJoggingSpotApp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * I declare that this code was written by me.
 * I will not copy or allow others to copy my code.
 * I understand that copying code is considered as plagiarism.
 *
 * Chai Yong Chen, 21007175, Jul 26, 2022 11:19:26 PM
 */

public class SingaporeJoggingSpotApp extends Application {
	
	private BorderPane pane = new BorderPane();
	private HBox topHPane = new HBox();	
	private VBox topVPane = new VBox();	
	private HBox bottomHPane = new HBox();
	
	private Label labelJoggingSpotApp = new Label("Jogging Spots Application");
	private TextArea textareaJoggingSpotRecords = new TextArea();
	
	private CheckBox boxPark = new CheckBox("Park");
	private CheckBox boxParkConnector = new CheckBox("Park Connector");
	private CheckBox boxStadium = new CheckBox("Stadium");
	
	private Button buttonSelectAll = new Button ("Select All");
	private Button buttonClearAll = new Button("Clear All");
	private Button buttonAdd = new Button("Add Jogging Spot");
	private Button buttonEditDelete = new Button("Edit / Delete");
	private Button buttonRefresh = new Button("Refresh");
	
	private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306";
	private static final String DB_USERNAME = "root";
	private static final String DB_PASSWORD = "SecretPassw0rd";
	
	private ArrayList<JoggingSpot> js = new ArrayList<JoggingSpot>();
	
	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) {
		
		load();
		
		// Top first part
		labelJoggingSpotApp.setFont(new Font("Cambria",32));
		
		// Top second part
		topHPane.setSpacing(15);
		topHPane.setPadding(new Insets(5,5,5,5));
		topHPane.setAlignment(Pos.CENTER);
		topHPane.getChildren().addAll(boxPark, boxParkConnector, boxStadium, buttonSelectAll, buttonClearAll);
		
		// Final top part
		topVPane.setSpacing(10);
		topVPane.setPadding(new Insets(5,5,5,5));
		topVPane.setAlignment(Pos.CENTER);
		topVPane.getChildren().addAll(labelJoggingSpotApp, topHPane);
		
		// Central part
		textareaJoggingSpotRecords.setEditable(false);
		textareaJoggingSpotRecords.setPadding(new Insets(15,15,15,15));
		textareaJoggingSpotRecords.setFont(new Font("Consolas", 13));
		
		// Bottom part
		bottomHPane.setSpacing(5);
		bottomHPane.setPadding(new Insets(5,5,5,5));
		bottomHPane.setAlignment(Pos.CENTER);
		bottomHPane.getChildren().addAll(buttonRefresh, buttonAdd, buttonEditDelete);
		
		// Border Pane
		pane.setTop(topVPane);
		pane.setCenter(textareaJoggingSpotRecords);
		pane.setBottom(bottomHPane);
		
		Scene mainScene = new Scene(pane);
		primaryStage.setScene(mainScene);

		primaryStage.setTitle("Singapore Jogging Spot");
		primaryStage.setWidth(650);
		primaryStage.setHeight(400);
		
		primaryStage.show();
		
		// Everytime refresh the textfield
		EventHandler<ActionEvent> handleGetRecords = (ActionEvent e) -> doGetRecord();
		boxPark.setOnAction(handleGetRecords);
		boxParkConnector.setOnAction(handleGetRecords);
		boxStadium.setOnAction(handleGetRecords);
		buttonRefresh.setOnAction(handleGetRecords);
		
		EventHandler<ActionEvent> handleSelectAll = (ActionEvent e) -> doSelectAll();
		buttonSelectAll.setOnAction(handleSelectAll);
		
		EventHandler<ActionEvent> handleClearAll = (ActionEvent e) -> doClearAll();
		buttonClearAll.setOnAction(handleClearAll);
		
		EventHandler<ActionEvent> handleAddJogSpot = (ActionEvent e) -> (new AddJogSpot()).start(new Stage());
		buttonAdd.setOnAction(handleAddJogSpot);
		
		EventHandler<ActionEvent> handleEditDeleteJogSpot = (ActionEvent e) -> (new EditDeleteJogSpot()).start(new Stage());
		buttonEditDelete.setOnAction(handleEditDeleteJogSpot);

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
	
	private void doGetRecord() {
		
		load();
		String output = "";
		
		if (boxPark.isSelected()) {
			output += String.format("PARK\n%-8s %-40s %-5s\n", "ID", "NAME", "SEAVIEW");	//Header
			for (JoggingSpot j : js) {
				if (j instanceof Park) {
					output += j.display();
				}
			}
			output += "\n";
		}
		
		if (boxParkConnector.isSelected()) {
			output += String.format("PARK CONNECTOR\n%-8s %-40s %-5s\n", "ID", "NAME", "DISTANCE");	//Header
			for (JoggingSpot j : js) {
				if (j instanceof ParkConnector) {
					output += j.display();
				}
			}
			output += "\n";
		}
		
		if (boxStadium.isSelected()) {
			output += String.format("STADIUM\n%-8s %-40s %-5s\n", "ID", "NAME", "CLOSING TIME"); //Header			
			for (JoggingSpot j : js){
				if (j instanceof Stadium) {
					output += j.display();
				}
			}
			for (JoggingSpot j: js) {
				if (j instanceof Stadium) {
					output += ((Stadium) j).announceUnavailability(j.getId());
				}
			}
			output += "\n";
		}
		
		textareaJoggingSpotRecords.setText(output);	
		
	}
	
	private void doSelectAll() {
		boxPark.setSelected(true);
		boxParkConnector.setSelected(true);
		boxStadium.setSelected(true);
		doGetRecord();
	}

	private void doClearAll() {
		boxPark.setSelected(false);
		boxParkConnector.setSelected(false);
		boxStadium.setSelected(false);
		textareaJoggingSpotRecords.clear();
	}
}
