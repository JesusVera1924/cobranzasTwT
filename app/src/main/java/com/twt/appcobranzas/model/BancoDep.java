package com.twt.appcobranzas.model;

/**
 * Created by DEVECUA on 17/05/2016.
 */
public class BancoDep {
    private String empresa;
    private String cod_banco;
    private String nom_banco;
    private String num_cuenta;

    public BancoDep(String empresa, String cod_banco, String num_cta, String nom_banco){
        this.empresa = empresa;
        this.cod_banco = cod_banco;
        this.num_cuenta = num_cta;
        this.nom_banco = nom_banco;
    }

    public BancoDep(){}

    public String getCod_banco() {
        return cod_banco;
    }

    public void setCod_banco(String cod_banco) {
        this.cod_banco = cod_banco;
    }

    public String getNom_banco() {
        return nom_banco;
    }

    public void setNom_banco(String nom_banco) {
        this.nom_banco = nom_banco;
    }

    public String getNum_cuenta() {
        return num_cuenta;
    }

    public void setNum_cuenta(String num_cuenta) {
        this.num_cuenta = num_cuenta;
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
