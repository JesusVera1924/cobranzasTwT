package com.twt.appcobranzas.model;

/**
 * Created by Devecua on 04/05/2016.
 */
public class Banco {
    private String empresa;
    private String cod_ref;
    private String nom_banco;

    public Banco(String empresa, String cod_ref, String nom_banco){
        this.empresa = empresa;
        this.cod_ref = cod_ref;
        this.nom_banco = nom_banco;
    }

    public Banco(){}

    public String getCod_ref() {
        return cod_ref;
    }

    public void setCod_ref(String cod_ref) {
        this.cod_ref = cod_ref;
    }

    public String getNom_banco() {
        return nom_banco;
    }

    public void setNom_banco(String nom_banco) {
        this.nom_banco = nom_banco;
    }

    public String toString(){
        return nom_banco;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }
}
