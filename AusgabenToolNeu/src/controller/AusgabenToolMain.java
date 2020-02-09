package controller;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main-Klasse.
 */
public class AusgabenToolMain extends Application {
	
	/**
	 * Main-Methode.
	 */
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		try {
			//Hauptfenster laden
			Parent root = FXMLLoader.load(getClass().getResource(FXMLResources.MAIN_FRAME_FXML));
	         
		    Scene scene = new Scene(root);
		    stage.setScene(scene);
		    stage.setTitle(Messages_DE.MAINFRAME_TITLE);
		    stage.setResizable(false);
		    stage.show();
		} catch (IOException e) {
	    	ExceptionAlert.show(Messages_DE.LOAD_EXCEPTION);
	    	e.printStackTrace();
	    }
	}

}
