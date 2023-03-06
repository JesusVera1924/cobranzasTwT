package com.twt.appcobranzas.model;

import java.util.Date;

/**
 * Created by Devecua on 02/05/2016.
 */
public class Abonos {
    private int id_recibo;
    private String fpago;
    private String cuenta;
    private String banco;
    private double valor;
    private Date fch_registro;

    public Abonos(String fpago, String cuenta, String banco, double valor){
        this.fpago = fpago;
        this.cuenta = cuenta;
        this.banco = banco;
        this.valor = valor;
    }

    public Abonos(){

    }

    public int getId_recibo() {
        return id_recibo;
    }

    public void setId_recibo(int id_recibo) {
        this.id_recibo = id_recibo;
    }

    public String getFpago() {
        return fpago;
    }

    public void setFpago(String fpago) {
        this.fpago = fpago;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Date getFch_registro() {
        return fch_registro;
    }

    public void setFch_registro(Date fch_registro) {
        this.fch_registro = fch_registro;
    }
}
