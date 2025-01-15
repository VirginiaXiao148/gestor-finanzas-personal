/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestorfinanciero;

/**
 *
 * @author JIANG XIAO QI
 */


import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class verIngresos extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Ver ingresos");

        Button calcularButton = new Button("Calcular");
        
        VBox gastosVB = new VBox(20);  // Cambiar el espacio entre los elementos a 20
        final PieChart pieChart = new PieChart();

        javafx.event.EventHandler<ActionEvent> calcular = new javafx.event.EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                // Inicializar la lista de ingresos
                ArrayList<Ingresos> ingresos = new ArrayList<>();
                
                SQLiteDatabaseExample example = new SQLiteDatabaseExample();
                try {
                    // Obtenemos las categorias de la base de datos
                    example.connect();
                    ingresos = example.listarIngresosPorCategorias();
                } catch (Exception ex) {
                    ex.printStackTrace(); // Mostrar la traza de la excepción
                    System.out.println("Error, no se ha podido acceder a la base de datos");
                }

                // Preparar los datos para el gráfico circular
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

                for (Ingresos ingreso : ingresos) {
                    String ingresoCategoria = ingreso.getCategoria();
                    double ingresoCantidad = ingreso.getImporte();
                    pieChartData.add(new PieChart.Data(ingresoCategoria, ingresoCantidad));
                }

                // Actualizar el gráfico solo si hay datos
                if (!pieChartData.isEmpty()) {
                    pieChart.getData().setAll(pieChartData);
                } else {
                    System.out.println("No hay datos de ingresos para mostrar.");
                }
            }
        };

        calcularButton.setOnAction(calcular);

        gastosVB.getChildren().addAll(calcularButton, pieChart);

        Scene scene = new Scene(gastosVB, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}