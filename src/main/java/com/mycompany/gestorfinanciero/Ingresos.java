/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestorfinanciero;

/**
 *
 * @author JIANG XIAO QI
 */

public class Ingresos {
    private String nombre, categoria;
    private double importe;

    public Ingresos(String nombre, String categoria, double importe) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.importe = importe;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getImporte() {
        return importe;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    @Override
    public String toString() {
        return "Ingresos{" + "nombre=" + nombre + ", categoria=" + categoria + ", importe=" + importe + '}';
    }
    
}
