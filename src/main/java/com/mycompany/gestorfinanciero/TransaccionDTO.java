package com.mycompany.gestorfinanciero;

import java.util.Date;

public class TransaccionDTO {
    private int id;
    private String categoria;
    private String descripcion;
    private double importe;
    private Date fecha;
    private boolean esGasto;
    
    public TransaccionDTO(int id, String categoria, String descripcion, 
                         double importe, Date fecha, boolean esGasto) {
        this.id = id;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.importe = importe;
        this.fecha = fecha;
        this.esGasto = esGasto;
    }
    
    // Getters
    public int getId() { return id; }
    public String getCategoria() { return categoria; }
    public String getDescripcion() { return descripcion; }
    public double getImporte() { return importe; }
    public Date getFecha() { return fecha; }
    public boolean isEsGasto() { return esGasto; }
}