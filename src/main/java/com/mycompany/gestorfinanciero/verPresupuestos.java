/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestorfinanciero;

/**
 *
 * @author JIANG XIAO QI
 */


import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class verPresupuestos extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Ver presupuestos");

        // Directorio que contiene los ficheros
        String directorio = "./Gestion de finanzas";

        // Lista para almacenar los nombres de archivos
        ObservableList<String> archivos = FXCollections.observableArrayList();

        // Obtén los archivos en el directorio
        File carpetaFile = new File(directorio);
        // Verifica si el directorio existe
        if (carpetaFile.exists() && carpetaFile.isDirectory()) {
            File[] files = carpetaFile.listFiles();
            // Verifica si el directorio esta vacio
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        archivos.add(file.getName());
                    }
                }
            }
        }

        // Crea un ListView para mostrar los archivos
        ListView<String> listView = new ListView<>();
        listView.setItems(archivos);

        // Crea un TextField de solo lectura para mostrar el contenido del archivo
        TextField fileContentField = new TextField();
        fileContentField.setEditable(false);

        // Maneja eventos de selección en el ListView
        listView.getSelectionModel().selectedItemProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    String selectedFileName = newValue;
                    try {
                        // Carga el contenido del archivo seleccionado y muéstralo en el TextField
                        String filePath = Paths.get(directorio, selectedFileName).toString();
                        String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
                        fileContentField.setText(fileContent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        VBox vbox = new VBox(listView);
        Scene scene = new Scene(vbox, 300, 400);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    

}