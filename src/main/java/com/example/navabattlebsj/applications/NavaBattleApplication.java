package com.example.navabattlebsj.applications;

import com.example.navabattlebsj.utils.Paths;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavaBattleApplication extends Application {

    private static Stage stageWindow;

    @Override
    public void start(Stage stage) {
        stageWindow = stage;
        setScene(Paths.MENU_VIEW, "NavaBattle");
    }

    public static <T> T setScene(String path, String title) {
        FXMLLoader loader = new FXMLLoader(NavaBattleApplication.class.getResource(path));
        try {
            Parent root = loader.load();
            stageWindow.setScene(new Scene(root));
            stageWindow.setTitle(title);
            stageWindow.show();
            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar la vista: " + path, e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}