package com.mycompany.gestorfinanciero;

public class Categoria {
    private String nombre;
    private boolean balance;

    public Categoria(String nombre, boolean balance) {
        this.nombre = nombre;
        this.balance = balance;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isBalance() {
        return balance;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setBalance(boolean balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Categoria{" + "nombre=" + nombre + ", balance=" + balance + '}';
    }
}
