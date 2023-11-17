/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.sql.Timestamp;


/**
 *
 * @author wynvern
 */
public class Notas {
    private int id;
    private int idEntidade;
    private String tipo;
    private Timestamp data; 
    private String numeroNota;
    private boolean pendurado;
    private Float precoTotal;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the idEntidade
     */
    public int getIdEntidade() {
        return idEntidade;
    }

    /**
     * @param idEntidade the idEntidade to set
     */
    public void setIdEntidade(int idEntidade) {
        this.idEntidade = idEntidade;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the data
     */
    public Timestamp getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Timestamp data) {
        this.data = data;
    }

    /**
     * @return the numeroNota
     */
    public String getNumeroNota() {
        return numeroNota;
    }

    /**
     * @param numeroNota the numeroNota to set
     */
    public void setNumeroNota(String numeroNota) {
        this.numeroNota = numeroNota;
    }

    /**
     * @return the pendurado
     */
    public boolean isPendurado() {
        return pendurado;
    }

    /**
     * @param pendurado the pendurado to set
     */
    public void setPendurado(boolean pendurado) {
        this.pendurado = pendurado;
    }

    /**
     * @return the precoTotal
     */
    public Float getPrecoTotal() {
        return precoTotal;
    }

    /**
     * @param precoTotal the precoTotal to set
     */
    public void setPrecoTotal(Float precoTotal) {
        this.precoTotal = precoTotal;
    }
}
