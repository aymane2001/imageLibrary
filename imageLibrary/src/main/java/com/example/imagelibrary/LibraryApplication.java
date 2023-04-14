package com.example.imagelibrary;
import java.sql.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public class LibraryApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource("LibraryView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),1400,800);
        stage.setTitle("ImageLibrary");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, SQLException {

        launch();
        //décommenter pour utiliser les deux fonctions concernant jdbc
        //LibraryController libraryController = new LibraryController();

        //décommenter pour ajouter une ligne dans la table
        //libraryController.updateSql();

        //décommenter pour afficher les lignes de la table
        //libraryController.parcourirSql();
    }
}