package com.mycompany.gestorfinanciero;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.Date;
import java.time.LocalDate;

public class RegistroTransaccion extends Application {
    private TextField montoField;
    private TextArea descripcionArea;
    private DatePicker fechaPicker;
    private VBox categoriasBox;
    private SQLiteDatabaseExample db = new SQLiteDatabaseExample();
    private boolean esGasto; // true para gasto, false para ingreso
    
    public RegistroTransaccion(boolean esGasto) {
        this.esGasto = esGasto;
    }
    
    @Override
    public void start(Stage transaccionStage) {
        transaccionStage.setTitle(esGasto ? "Registrar Gasto" : "Registrar Ingreso");
        
        // Panel principal
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");

        // Panel superior con título
        Label titleLabel = new Label(esGasto ? "Nuevo Gasto" : "Nuevo Ingreso");
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
        transaccionStage.setScene(scene);
        transaccionStage.show();
    }

    private VBox createFormPanel() {
        VBox form = new VBox(15);
        form.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5;");
        form.setMinWidth(300);
        form.setPadding(new Insets(20));

        // Campo de fecha
        Label fechaLabel = new Label("Fecha:");
        fechaPicker = new DatePicker(LocalDate.now());
        fechaPicker.setMaxWidth(Double.MAX_VALUE);

        // Campo de monto
        Label montoLabel = new Label("Monto:");
        montoField = new TextField();
        montoField.setPromptText("Ingrese el monto");

        // Campo de descripción
        Label descripcionLabel = new Label("Descripción:");
        descripcionArea = new TextArea();
        descripcionArea.setPromptText("Ingrese una descripción");
        descripcionArea.setPrefRowCount(3);
        
        String colorBoton = esGasto ? "#e74c3c" : "#2ecc71";
        Button guardarButton = createStyledButton("Guardar " + (esGasto ? "Gasto" : "Ingreso"), colorBoton);
        Button limpiarButton = createStyledButton("Limpiar Campos", "#95a5a6");

        // Eventos de botones
        guardarButton.setOnAction(e -> guardarTransaccion());
        limpiarButton.setOnAction(e -> limpiarCampos());

        form.getChildren().addAll(
            fechaLabel, fechaPicker,
            montoLabel, montoField,
            descripcionLabel, descripcionArea,
            new Separator(),
            guardarButton,
            limpiarButton
        );

        return form;
    }

    private ScrollPane createCategoriasPanel() {
        VBox categoriasContainer = new VBox(20);
        categoriasContainer.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5;");
        
        Label categoriasLabel = new Label("Seleccione una categoría:");
        categoriasLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        categoriasLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        categoriasBox = new VBox(10);
        
        // Toggle group para asegurar que solo se seleccione una categoría
        ToggleGroup categoryGroup = new ToggleGroup();
        
        try {
            db.connect();
            // Obtener categorías según el tipo (gasto o ingreso)
            for (Categoria categoria : db.listCategorias()) {
                if (categoria.isBalance() != esGasto) { // isBalance true para ingresos
                    RadioButton radioBtn = createCategoriaRadio(categoria.getNombre(), categoryGroup);
                    categoriasBox.getChildren().add(radioBtn);
                }
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar las categorías");
            e.printStackTrace();
        }

        categoriasContainer.getChildren().addAll(categoriasLabel, categoriasBox);

        ScrollPane scrollPane = new ScrollPane(categoriasContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        return scrollPane;
    }

    private RadioButton createCategoriaRadio(String categoria, ToggleGroup group) {
        RadioButton radio = new RadioButton(categoria);
        radio.setToggleGroup(group);
        radio.setStyle(
            "-fx-text-fill: #2c3e50;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 5px;"
        );
        return radio;
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

    private void guardarTransaccion() {
        try {
            // Validar que se haya seleccionado una categoría
            RadioButton selectedRadio = null;
            for (javafx.scene.Node node : categoriasBox.getChildren()) {
                if (node instanceof RadioButton) {
                    RadioButton radio = (RadioButton) node;
                    if (radio.isSelected()) {
                        selectedRadio = radio;
                        break;
                    }
                }
            }

            if (selectedRadio == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Por favor seleccione una categoría");
                return;
            }

            // Validar el monto
            if (montoField.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Por favor ingrese un monto");
                return;
            }

            double monto = Double.parseDouble(montoField.getText());
            String categoria = selectedRadio.getText();
            LocalDate fecha = fechaPicker.getValue();
            String descripcion = descripcionArea.getText();

            Date fechaSQL = Date.valueOf(fecha);

            // Aquí iría la lógica para guardar en la base de datos
            int categoriaId = db.obtenerCategoriaId(categoria);
            db.connect();
            db.insertTransaccion(categoriaId, descripcion, monto, fechaSQL, esGasto);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", 
                         (esGasto ? "Gasto" : "Ingreso") + " registrado correctamente");
            limpiarCampos();
            
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Por favor, ingrese un monto válido");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar la transacción: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        fechaPicker.setValue(LocalDate.now());
        montoField.clear();
        descripcionArea.clear();
        
        // Desseleccionar todas las categorías
        for (javafx.scene.Node node : categoriasBox.getChildren()) {
            if (node instanceof RadioButton) {
                ((RadioButton) node).setSelected(false);
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
}