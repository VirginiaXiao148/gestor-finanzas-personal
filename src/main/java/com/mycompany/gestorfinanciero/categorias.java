package com.mycompany.gestorfinanciero;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.collections.FXCollections;

public class categorias extends Application {
    private SQLiteDatabaseExample db = new SQLiteDatabaseExample();
    private VBox ingresosBox;
    private VBox gastosBox;
    private TextField searchField;
    private ComboBox<String> tipoComboBox;
    private TextField nombreField;
    
    @Override
    public void start(Stage categorias) {
        categorias.setTitle("Gestión de Categorías");
        
        // Panel principal con diseño responsivo
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");

        // Panel superior con título
        Label titleLabel = new Label("Gestión de Categorías");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        mainLayout.setTop(titleLabel);
        BorderPane.setMargin(titleLabel, new Insets(0, 0, 20, 0));

        // Panel de formulario
        VBox formPanel = createFormPanel();
        mainLayout.setLeft(formPanel);

        // Panel de categorías
        VBox categoriasPanel = createCategoriasPanel();
        mainLayout.setCenter(categoriasPanel);
        BorderPane.setMargin(categoriasPanel, new Insets(0, 0, 0, 20));

        Scene scene = new Scene(mainLayout, 900, 600);
        categorias.setScene(scene);
        categorias.show();
        
        // Cargar categorías existentes
        loadExistingCategories();
    }

    private VBox createFormPanel() {
        VBox form = new VBox(15);
        form.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5;");
        form.setMinWidth(300);
        form.setPadding(new Insets(20));

        // Campos de entrada
        Label tipoLabel = new Label("Tipo de categoría:");
        tipoComboBox = new ComboBox<>(FXCollections.observableArrayList("Ingresos", "Gastos"));
        tipoComboBox.setMaxWidth(Double.MAX_VALUE);
        tipoComboBox.setPromptText("Seleccione tipo");

        Label nombreLabel = new Label("Nombre de la categoría:");
        nombreField = new TextField();
        nombreField.setPromptText("Nombre de la categoría");

        // Búsqueda
        Label searchLabel = new Label("Buscar categoría:");
        searchField = new TextField();
        searchField.setPromptText("Buscar...");
        searchField.textProperty().addListener((obs, oldText, newText) -> filterCategories(newText));

        // Botones con estilos
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button addButton = createStyledButton("Añadir", "#2ecc71");
        Button editButton = createStyledButton("Modificar", "#3498db");
        Button deleteButton = createStyledButton("Eliminar", "#e74c3c");

        buttonBox.getChildren().addAll(addButton, editButton, deleteButton);

        // Añadir elementos al formulario
        form.getChildren().addAll(
            tipoLabel, tipoComboBox,
            nombreLabel, nombreField,
            searchLabel, searchField,
            new Separator(),
            buttonBox
        );

        // Eventos de botones
        addButton.setOnAction(e -> handleAdd());
        editButton.setOnAction(e -> handleEdit());
        deleteButton.setOnAction(e -> handleDelete());

        return form;
    }

    private VBox createCategoriasPanel() {
        // Contenedor principal
        VBox mainContainer = new VBox(20);
        mainContainer.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5;");
    
        // Contenedor para las categorías con scroll
        VBox categoriasContainer = new VBox(20);
        
        // Configuración del ScrollPane
        ScrollPane scrollPane = new ScrollPane(categoriasContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        // Panel de Ingresos
        Label ingresosTitle = new Label("Ingresos");
        ingresosTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        ingresosTitle.setStyle("-fx-text-fill: #27ae60;");
        ingresosBox = new VBox(10);
        ingresosBox.setStyle("-fx-padding: 10; -fx-background-color: #f8f9fa; -fx-background-radius: 5;");
    
        // Panel de Gastos
        Label gastosTitle = new Label("Gastos");
        gastosTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        gastosTitle.setStyle("-fx-text-fill: #c0392b;");
        gastosBox = new VBox(10);
        gastosBox.setStyle("-fx-padding: 10; -fx-background-color: #f8f9fa; -fx-background-radius: 5;");
    
        // Agregar elementos al contenedor de categorías
        categoriasContainer.getChildren().addAll(
            ingresosTitle, ingresosBox,
            new Separator(),
            gastosTitle, gastosBox
        );
    
        // Establecer el tamaño máximo del ScrollPane
        scrollPane.setMaxHeight(500); // Ajusta este valor según necesites
        
        // Agregar el ScrollPane al contenedor principal
        mainContainer.getChildren().add(scrollPane);
        
        return mainContainer;
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

    private void loadExistingCategories() {
        try {
            for (Categoria c : db.listCategorias()) {
                addCategoryToUI(c.getNombre(), c.isBalance());
            }
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error", "No se pudieron cargar las categorías");
            e.printStackTrace();
        }
    }

    private void addCategoryToUI(String nombre, boolean isIngreso) {
        Label categoryLabel = createCategoryLabel(nombre);
        if (isIngreso) {
            ingresosBox.getChildren().add(categoryLabel);
        } else {
            gastosBox.getChildren().add(categoryLabel);
        }
    }

    private Label createCategoryLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
            "-fx-padding: 5 10;" +
            "-fx-background-color: #ecf0f1;" + 
            "-fx-background-radius: 3;" +
            "-fx-text-fill: #2c3e50;" +  // Color del texto
            "-fx-font-size: 14px;" +     // Tamaño del texto
            "-fx-font-family: System;"    // Fuente del texto
        );
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }    

    private void filterCategories(String searchText) {
        for (Label label : getAllCategoryLabels()) {
            label.setVisible(label.getText().toLowerCase().contains(searchText.toLowerCase()));
            label.setManaged(label.isVisible());
        }
    }

    private java.util.List<Label> getAllCategoryLabels() {
        java.util.List<Label> labels = new java.util.ArrayList<>();
        ingresosBox.getChildren().forEach(node -> {
            if (node instanceof Label) labels.add((Label) node);
        });
        gastosBox.getChildren().forEach(node -> {
            if (node instanceof Label) labels.add((Label) node);
        });
        return labels;
    }

    private void handleAdd() {
        if (tipoComboBox.getValue() == null || nombreField.getText().isEmpty()) {
            mostrarAlerta(AlertType.ERROR, "Error", "Debe seleccionar un tipo y escribir un nombre");
            return;
        }

        try {
            boolean isIngreso = tipoComboBox.getValue().equals("Ingresos");
            db.insertCategoria(nombreField.getText(), isIngreso);
            addCategoryToUI(nombreField.getText(), isIngreso);
            nombreField.clear();
            mostrarAlerta(AlertType.INFORMATION, "Éxito", "Categoría añadida correctamente");
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error", "No se pudo añadir la categoría");
            e.printStackTrace();
        }
    }

    private void handleEdit() {
        // Implementar lógica de edición
        mostrarAlerta(AlertType.INFORMATION, "Info", "Funcionalidad de edición en desarrollo");
    }

    private void handleDelete() {
        String nombreCategoria = nombreField.getText();
        if (nombreCategoria.isEmpty()) {
            mostrarAlerta(AlertType.ERROR, "Error", "Seleccione una categoría para eliminar");
            return;
        }

        try {
            db.deleteCategoriaByName(nombreCategoria);
            refreshCategories();
            mostrarAlerta(AlertType.INFORMATION, "Éxito", "Categoría eliminada correctamente");
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error", "No se pudo eliminar la categoría");
            e.printStackTrace();
        }
    }

    private void refreshCategories() {
        ingresosBox.getChildren().clear();
        gastosBox.getChildren().clear();
        loadExistingCategories();
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
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