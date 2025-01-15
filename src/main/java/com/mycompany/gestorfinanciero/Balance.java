package com.mycompany.gestorfinanciero;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.chart.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;

public class Balance extends Application {
    private SQLiteDatabaseExample db;
    private VBox mainLayout;
    private TableView<BalanceEntry> balanceTable;
    private PieChart gastosPieChart;
    private Label totalBalanceLabel;
    
    @Override
    public void start(Stage stage) {
        db = new SQLiteDatabaseExample();
        stage.setTitle("Balance Financiero");
        
        // Crear el layout principal
        mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");
        
        // Crear las secciones principales
        createHeaderSection();
        createBalanceTable();
        createChartsSection();
        
        // Actualizar datos
        updateBalanceData();
        
        Scene scene = new Scene(mainLayout, 1000, 800);
        stage.setScene(scene);
        stage.show();
    }
    
    private void createHeaderSection() {
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Balance Financiero");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        totalBalanceLabel = new Label();
        totalBalanceLabel.setStyle("-fx-font-size: 18px;");
        
        headerBox.getChildren().addAll(titleLabel, totalBalanceLabel);
        mainLayout.getChildren().add(headerBox);
    }
    
    private void createBalanceTable() {
        balanceTable = new TableView<>();
        balanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Crear columnas
        TableColumn<BalanceEntry, String> categoriaCol = new TableColumn<>("Categoría");
        categoriaCol.setCellValueFactory(cellData -> cellData.getValue().categoriaProperty());
        
        TableColumn<BalanceEntry, Double> presupuestoCol = new TableColumn<>("Presupuesto");
        presupuestoCol.setCellValueFactory(cellData -> cellData.getValue().presupuestoProperty().asObject());
        presupuestoCol.setCellFactory(col -> new TableCell<BalanceEntry, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f €", item));
                }
            }
        });
        
        TableColumn<BalanceEntry, Double> gastadoCol = new TableColumn<>("Gastado");
        gastadoCol.setCellValueFactory(cellData -> cellData.getValue().gastadoProperty().asObject());
        gastadoCol.setCellFactory(col -> new TableCell<BalanceEntry, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f €", item));
                    if (item > 0) {
                        setStyle("-fx-text-fill: #e74c3c;"); // Rojo para gastos
                    }
                }
            }
        });
        
        TableColumn<BalanceEntry, Double> balanceCol = new TableColumn<>("Balance");
        balanceCol.setCellValueFactory(cellData -> cellData.getValue().balanceProperty().asObject());
        balanceCol.setCellFactory(col -> new TableCell<BalanceEntry, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f €", item));
                    if (item < 0) {
                        setStyle("-fx-text-fill: #e74c3c;"); // Rojo para balance negativo
                    } else {
                        setStyle("-fx-text-fill: #27ae60;"); // Verde para balance positivo
                    }
                }
            }
        });
        
        balanceTable.getColumns().addAll(categoriaCol, presupuestoCol, gastadoCol, balanceCol);
        mainLayout.getChildren().add(balanceTable);
    }
    
    private void createChartsSection() {
        HBox chartsBox = new HBox(20);
        chartsBox.setAlignment(Pos.CENTER);
        
        // Gráfico de gastos por categoría
        gastosPieChart = new PieChart();
        gastosPieChart.setTitle("Distribución de Gastos");
        gastosPieChart.setLabelsVisible(true);
        
        chartsBox.getChildren().add(gastosPieChart);
        mainLayout.getChildren().add(chartsBox);
    }
    
    private void updateBalanceData() {
        ObservableList<BalanceEntry> balanceData = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        double totalBalance = 0.0;
        
        // Obtener todas las categorías
        ArrayList<Categoria> categorias = db.listCategorias();
        
        for (Categoria categoria : categorias) {
            String nombreCategoria = categoria.getNombre();
            try {
                int categoriaId = db.obtenerCategoriaId(nombreCategoria);
                
                // Obtener el presupuesto de la categoría
                double presupuesto = db.obtenerPresupuestoPorCategoria(categoriaId);
                
                // Obtener gastos/ingresos según el tipo de categoría
                double importe = db.obtenerTotalTransaccionesPorCategoria(categoriaId, !categoria.isBalance());
                double balance = categoria.isBalance() ? importe : presupuesto - importe;
                
                balanceData.add(new BalanceEntry(nombreCategoria, presupuesto, importe, balance));
                
                if (importe > 0) {
                    pieChartData.add(new PieChart.Data(nombreCategoria, importe));
                }
                
                if (!categoria.isBalance()) {
                    totalBalance -= importe;
                } else {
                    totalBalance += importe;
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        balanceTable.setItems(balanceData);
        gastosPieChart.setData(pieChartData);
        
        // Actualizar el label del balance total
        totalBalanceLabel.setText(String.format("Balance Total: %.2f €", totalBalance));
        totalBalanceLabel.setStyle(totalBalance >= 0 ? 
            "-fx-font-size: 18px; -fx-text-fill: #27ae60;" : 
            "-fx-font-size: 18px; -fx-text-fill: #e74c3c;");
    }    
    
    // Clase auxiliar para los datos del balance
    public static class BalanceEntry {
        private final javafx.beans.property.StringProperty categoria;
        private final javafx.beans.property.DoubleProperty presupuesto;
        private final javafx.beans.property.DoubleProperty gastado;
        private final javafx.beans.property.DoubleProperty balance;
        
        public BalanceEntry(String categoria, double presupuesto, double gastado, double balance) {
            this.categoria = new javafx.beans.property.SimpleStringProperty(categoria);
            this.presupuesto = new javafx.beans.property.SimpleDoubleProperty(presupuesto);
            this.gastado = new javafx.beans.property.SimpleDoubleProperty(gastado);
            this.balance = new javafx.beans.property.SimpleDoubleProperty(balance);
        }
        
        public javafx.beans.property.StringProperty categoriaProperty() { return categoria; }
        public javafx.beans.property.DoubleProperty presupuestoProperty() { return presupuesto; }
        public javafx.beans.property.DoubleProperty gastadoProperty() { return gastado; }
        public javafx.beans.property.DoubleProperty balanceProperty() { return balance; }
    }
}