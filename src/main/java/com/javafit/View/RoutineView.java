package com.javafit.View;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.bson.Document;
import org.kordamp.bootstrapfx.scene.layout.Panel;

import com.javafit.Controller.CustomRoutineController;
import com.javafit.Controller.DashController;
import com.javafit.View.RegistrationView;
import com.jfoenix.controls.JFXButton;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;


public class RoutineView {
	
	private Scene routineScene;
	private String userName;
	private Stage routineStage;
	private MongoClient mongoClient;
	private MongoDatabase routinesDB;

	
	public RoutineView(String userName) throws IOException{
		this.userName = userName;
		this.routineStage = new Stage();
        this.routineScene = new Scene(loadFXML(), 956, 800);
        this.routineStage.setScene(this.routineScene);
		
		ScrollPane mainPane = (ScrollPane) this.routineScene.lookup("#mainPane");
        this.initializeMongoConnection();
        MongoCollection<Document> routinesCollection = this.routinesDB.getCollection("ROUTINES");
		FindIterable<Document> iterable = routinesCollection.find();
		MongoCursor<Document> cursor = iterable.iterator();
		GridPane gP = new GridPane();
		int rowIndex = 0;
		
		mainPane.setPadding(new Insets(25, 75, 25, 200));
		
		try {
			while(cursor.hasNext()) {
				
				
				Document obj = cursor.tryNext();
				AnchorPane newPane = FXMLLoader.load(RoutineView.class.getResource("/routinePane.fxml"));
				
				Label routineName = (Label) newPane.lookup("#routineName");
				Label userNameLabel = (Label) newPane.lookup("#userName");
				Label reps = (Label) newPane.lookup("#reps");
				Label mGroup = (Label) newPane.lookup("#mGroup");
				Label objs = (Label) newPane.lookup("#objs");
				Label location = (Label) newPane.lookup("#location");
				
				System.out.println("" + obj.get("name"));
				routineName.setText((String) obj.get("name"));
				userNameLabel.setText((String) obj.get("userName"));
				reps.setText((String) obj.get("reps"));
				mGroup.setText((String) obj.get("muscleGroup"));
				objs.setText("gain muscle");
				location.setText((String) obj.get("location"));
	
				gP.add(newPane, 0, rowIndex);
				rowIndex++;
			}
		} finally {
			cursor.close();
		}
		gP.setAlignment(Pos.CENTER);
	
		mainPane.setPannable(true);
		mainPane.setContent(gP);
		
		
        JFXButton dashBtn =  (JFXButton) this.routineScene.lookup("#dashBtn");
        dashBtn.setOnAction((ActionEvent event) -> {
        	Stage stage = (Stage) dashBtn.getScene().getWindow();
            stage.close();
            
            try {
				DashController dC = new DashController(this.userName);
			} catch (IOException e) {
				e.printStackTrace();
			}
        });
        
        JFXButton customBtn =  (JFXButton) this.routineScene.lookup("#customBtn");
        customBtn.setOnAction((ActionEvent event) -> {
        	Stage stage = (Stage) customBtn.getScene().getWindow();
            stage.close();
            
            try {
				CustomRoutineController crC = new CustomRoutineController(this.userName);
			} catch (IOException e) {
				e.printStackTrace();
			}
        });
        
        this.closeMongoConnection();
		this.routineStage.show();
	}
	
	private void initializeMongoConnection() {
        this.mongoClient = MongoClients.create(
                "mongodb+srv://ckleest:ckk@javafit-qy8fa.mongodb.net/test?retryWrites=true&w=majority");
        this.routinesDB = mongoClient.getDatabase("ROUTINES");
    }

    public void closeMongoConnection() {
        System.out.println("closing mongo client");
        this.mongoClient.close();
        System.out.println("successfully closed mongo connection");
    }
	
	private static Parent loadFXML() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RegistrationView.class.getResource("/routines.fxml"));
        return fxmlLoader.load();
    }
	
	
}


