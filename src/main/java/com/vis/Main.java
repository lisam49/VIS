package com.vis;

import com.vis.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;



public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            LoginView loginView = new LoginView();
            loginView.show(primaryStage);
        } catch (Exception ex) {
            System.err.println("Failed to start application: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
