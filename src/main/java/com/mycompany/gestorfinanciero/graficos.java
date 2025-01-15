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
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class graficos extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    private TextField presupuestoField,inversionesField,salarioField,comidaField, entretenimientoField,higieneYsaludField,viajesField,finanzasField,otrosField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gestión de Presupuesto");

        Button calculateButton = new Button("Calcular");

        PieChart pieChart = new PieChart();

        calculateButton.setOnAction(e -> {
            double budget = Double.parseDouble(presupuestoField.getText());
            double inversiones = Double.parseDouble(inversionesField.getText());
            double salario = Double.parseDouble(salarioField.getText());
            double comida = Double.parseDouble(comidaField.getText());
            double entretenimiento = Double.parseDouble(entretenimientoField.getText());
            double higieneYsalud = Double.parseDouble(higieneYsaludField.getText());
            double viajes = Double.parseDouble(viajesField.getText());
            double finanzas = Double.parseDouble(finanzasField.getText());
            double otros = Double.parseDouble(otrosField.getText());

            double income = inversiones + salario;
            double expenses = comida + entretenimiento + higieneYsalud + viajes + finanzas + otros;

            PieChart.Data expenseData = new PieChart.Data("Gastos", expenses);
            PieChart.Data incomeData = new PieChart.Data("Ingresos", income);

            pieChart.getData().setAll(expenseData, incomeData);
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(calculateButton, pieChart);

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
