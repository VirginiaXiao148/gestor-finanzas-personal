package com.mycompany.gestorfinanciero;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class presupuesto extends Application {
    private TextField presupuestoField;
    private VBox categoriasBox;
    private SQLiteDatabaseExample db = new SQLiteDatabaseExample();
    
    // Estructura para mantener los campos de cada categoría

    @Override
    public void start(Stage presupuestoStage) {
        presupuestoStage.setTitle("Nuevo Presupuesto");
        
        // Panel principal
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");

        // Panel superior con título
        Label titleLabel = new Label("Nuevo Presupuesto");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        mainLayout.setTop(titleLabel);
        BorderPane.setMargin(titleLabel, new Insets(0, 0, 20, 0));

        // Panel izquierdo para el formulario principal
        VBox formPanel = createFormPanel();
        mainLayout.setLeft(formPanel);

        // Panel central para las categorías
        ScrollPane categoriasScroll = createCategoriasPanel();
        mainLayout.setCenter(categoriasScroll);
        BorderPane.setMargin(categoriasScroll, new Insets(0, 0, 0, 20));

        Scene scene = new Scene(mainLayout, 900, 600);
        presupuestoStage.setScene(scene);
        presupuestoStage.show();
    }

    private VBox createFormPanel() {
        VBox form = new VBox(15);
        form.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5;");
        form.setMinWidth(300);
        form.setPadding(new Insets(20));

        Label presupuestoLabel = new Label("Presupuesto Total:");
        presupuestoField = new TextField();
        presupuestoField.setPromptText("Ingrese el presupuesto total");

        Button guardarButton = createStyledButton("Guardar Presupuesto", "#2ecc71");
        Button limpiarButton = createStyledButton("Limpiar Campos", "#e74c3c");

        // Eventos de botones
        guardarButton.setOnAction(e -> guardarPresupuesto());
        limpiarButton.setOnAction(e -> limpiarCampos());

        form.getChildren().addAll(
            presupuestoLabel,
            presupuestoField,
            new Separator(),
            guardarButton,
            limpiarButton
        );

        return form;
    }

    /* private ScrollPane createCategoriasPanel() {
        VBox categoriasContainer = new VBox(20);
        categoriasContainer.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5;");
        
        categoriasBox = new VBox(10);
        
        // Crear campos para cada categoría
        String[] categorias = {
            "Salario", "Inversiones", "Comida", "Entretenimiento",
            "Higiene y Salud", "Viajes", "Finanzas", "Otros"
        };
        
        for (String categoria : categorias) {
            HBox categoriaRow = createCategoriaRow(categoria);
            categoriasBox.getChildren().add(categoriaRow);
        }

        categoriasContainer.getChildren().add(categoriasBox);

        ScrollPane scrollPane = new ScrollPane(categoriasContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        return scrollPane;
    } */

    private ScrollPane createCategoriasPanel() {
        VBox categoriasContainer = new VBox(20);
        ScrollPane scrollPane = new ScrollPane();
        categoriasContainer.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5;");
        
        categoriasBox = new VBox(10);
        
        try {
            // Obtener todas las categorías de la base de datos
            ArrayList<String> categorias = db.obtenerNombresCategorias();
            
            for (String categoria : categorias) {
                HBox categoriaRow = createCategoriaRow(categoria);
                categoriasBox.getChildren().add(categoriaRow);
            }
            
            categoriasContainer.getChildren().add(categoriasBox);

            scrollPane = new ScrollPane(categoriasContainer);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar las categorías: " + e.getMessage());
            e.printStackTrace();
        }
        
        return scrollPane;
    }

    private HBox createCategoriaRow(String categoria) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10; -fx-background-color: #f8f9fa; -fx-background-radius: 5;");
    
        CategoriaInput input = new CategoriaInput(categoria);
        
        // Configurar el estilo del checkbox
        input.checkBox.setStyle(
            "-fx-text-fill: #2c3e50;" +    // Color del texto
            "-fx-font-size: 14px;" +       // Tamaño de la fuente
            "-fx-padding: 5px;"            // Padding para mejor espaciado
        );
        
        // Configurar el estilo del campo de texto
        input.field.setStyle(
            "-fx-pref-width: 150px;" +
            "-fx-background-color: white;" +
            "-fx-border-color: #ddd;" +
            "-fx-border-radius: 3px;"
        );
        input.field.setPrefWidth(200);
        
        row.getChildren().addAll(input.checkBox, input.field);
        
        // Añadir algo de espacio entre las filas
        VBox.setMargin(row, new Insets(0, 0, 5, 0));
        
        return row;
    }
    
    
    private class CategoriaInput {
        CheckBox checkBox;
        TextField field;
        
        CategoriaInput(String nombre) {
            checkBox = new CheckBox(nombre);
            checkBox.setStyle(
                "-fx-text-fill: #2c3e50;" +
                "-fx-font-size: 14px;"
            );
            
            field = new TextField();
            field.setPromptText("Ingrese monto");
            field.setDisable(true);
            
            // Habilitar/deshabilitar el campo según el checkbox
            checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                field.setDisable(!newVal);
                if (!newVal) {
                    field.clear();
                }
            });
        }
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(
            String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; " +
                         "-fx-padding: 10 20; -fx-background-radius: 5;", color)
        );
        button.setMaxWidth(Double.MAX_VALUE);
        
        // Efectos hover
        button.setOnMouseEntered(e -> 
            button.setStyle(String.format("-fx-background-color: derive(%s, 20%%); -fx-text-fill: white; " +
                                        "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;", color))
        );
        button.setOnMouseExited(e -> 
            button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; " +
                                        "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;", color))
        );
        
        return button;
    }

    private void guardarPresupuesto() {
        try {
            //db.connect();
            
            // Recorrer todos los hijos del categoriasBox
            for (javafx.scene.Node node : categoriasBox.getChildren()) {
                if (node instanceof HBox) {
                    HBox row = (HBox) node;
                    CheckBox checkBox = (CheckBox) row.getChildren().get(0);
                    TextField field = (TextField) row.getChildren().get(1);
                    
                    if (checkBox.isSelected()) {
                        String categoria = checkBox.getText();
                        double cantidad = Double.parseDouble(field.getText());
                        int categoriaId = db.obtenerCategoriaId(categoria);
                        
                        if (categoriaId != -1) {
                            db.insertarPresupuesto(categoriaId, cantidad);
                        }
                    }
                }
            }
            
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Presupuesto guardado correctamente");
            limpiarCampos();
            
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Por favor, ingrese valores numéricos válidos");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar el presupuesto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        presupuestoField.clear();
        
        // Limpiar todos los campos de categorías
        for (javafx.scene.Node node : categoriasBox.getChildren()) {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                CheckBox checkBox = (CheckBox) row.getChildren().get(0);
                TextField field = (TextField) row.getChildren().get(1);
                
                checkBox.setSelected(false);
                field.clear();
                field.setDisable(true);
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}