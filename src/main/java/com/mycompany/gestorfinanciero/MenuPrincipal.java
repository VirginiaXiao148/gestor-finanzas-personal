package com.mycompany.gestorfinanciero;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class MenuPrincipal extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gestor Financiero");
        
        // Panel principal
        VBox mainLayout = new VBox(30);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(40));
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");
        
        // Título
        Label titleLabel = new Label("Gestor Financiero");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        
        // Subtítulo
        Label subtitleLabel = new Label("Seleccione una opción");
        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 18));
        subtitleLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        // Contenedor para los botones
        VBox buttonContainer = new VBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setMaxWidth(400);
        
        // Crear botones
        Button presupuestoButton = createMenuButton("Crear Presupuesto", "#2ecc71");
        Button gastoButton = createMenuButton("Añadir Gasto", "#e74c3c");
        Button ingresoButton = createMenuButton("Añadir Ingreso", "#3498db");
        Button balance = createMenuButton("Ver Balance", "#f39c12");
        Button categoriasButton = createMenuButton("Gestionar Categorías", "#9b59b6");
        
        buttonContainer.getChildren().addAll(
            presupuestoButton,
            gastoButton,
            ingresoButton,
            balance,
            categoriasButton
        );
        
        mainLayout.getChildren().addAll(titleLabel, subtitleLabel, buttonContainer);
        
        // Eventos de los botones
        presupuestoButton.setOnAction(e -> abrirPresupuesto());
        gastoButton.setOnAction(e -> abrirGasto());
        ingresoButton.setOnAction(e -> abrirIngreso());
        balance.setOnAction(e -> abrirBalance());
        categoriasButton.setOnAction(e -> abrirCategorias());
        
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private Button createMenuButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(
            String.format("-fx-background-color: %s;" +
                         "-fx-text-fill: white;" +
                         "-fx-font-size: 16px;" +
                         "-fx-font-weight: bold;" +
                         "-fx-padding: 15 30;" +
                         "-fx-background-radius: 5;" +
                         "-fx-cursor: hand;", color)
        );
        button.setMaxWidth(Double.MAX_VALUE);
        
        // Efectos hover
        button.setOnMouseEntered(e -> 
            button.setStyle(String.format("-fx-background-color: derive(%s, 20%%);" +
                                        "-fx-text-fill: white;" +
                                        "-fx-font-size: 16px;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-padding: 15 30;" +
                                        "-fx-background-radius: 5;" +
                                        "-fx-cursor: hand;", color))
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(String.format("-fx-background-color: %s;" +
                                        "-fx-text-fill: white;" +
                                        "-fx-font-size: 16px;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-padding: 15 30;" +
                                        "-fx-background-radius: 5;" +
                                        "-fx-cursor: hand;", color))
        );
        
        return button;
    }
    
    private void abrirPresupuesto() {
        try {
            Stage presupuestoStage = new Stage();
            new presupuesto().start(presupuestoStage);
        } catch (Exception e) {
            mostrarError("Error al abrir presupuesto", e);
        }
    }
    
    private void abrirGasto() {
        try {
            Stage gastoStage = new Stage();
            new RegistroTransaccion(true).start(gastoStage);
        } catch (Exception e) {
            mostrarError("Error al abrir registro de gasto", e);
        }
    }
    
    private void abrirIngreso() {
        try {
            Stage ingresoStage = new Stage();
            new RegistroTransaccion(false).start(ingresoStage);
        } catch (Exception e) {
            mostrarError("Error al abrir registro de ingreso", e);
        }
    }

    private void abrirBalance() {
        try {
            Stage balanceStage = new Stage();
            new Balance().start(balanceStage);
        } catch (Exception e) {
            mostrarError("Error al abrir balance", e);
        }
    }
    
    private void abrirCategorias() {
        try {
            Stage categoriasStage = new Stage();
            new categorias().start(categoriasStage);
        } catch (Exception e) {
            mostrarError("Error al abrir categorías", e);
        }
    }
    
    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarError(String mensaje, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje + ": " + e.getMessage());
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
