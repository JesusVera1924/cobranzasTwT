package com.twt.appcobranzas.model;

/**
 * Created by Devecua on 02/05/2016.
 */
public class Recibos {
    private int id_recibo;
    private String cod_mov;
    private String num_mov;
    private String fpago;
    private String banco;
    private String cuenta;
    private String cheque;
    private String fch_pago;
    private double valor;
    private String cod_ref;
    private String cod_vend;
    private String fch_registro;
    private String girador;
    private String cod_rec;
    private int estado;
    private String pto_vta;
    private String tipo;

    public Recibos(int id_recibo, String cod_mov, String num_mov, String fpago, String banco, String cuenta, String cheque, String fch_pago, double valor, String cod_ref, String cod_vend,
                   String fch_registro, String girador, String cod_rec, int estado, String pto_vta, String tipo){
        this.id_recibo = id_recibo;
        this.cod_mov = cod_mov;
        this.num_mov = num_mov;
        this.fpago = fpago;
        this.banco = banco;
        this.cuenta = cuenta;
        this.cheque = cheque;
        this.fch_pago = fch_pago;
        this.valor = valor;
        this.cod_ref = cod_ref;
        this.cod_vend = cod_vend;
        this.fch_registro = fch_registro;
        this.girador = girador;
        this.cod_rec = cod_rec;
        this.estado = estado;
        this.pto_vta = pto_vta;
        this.tipo = tipo;
    }

    public Recibos(){

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

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getCod_ref() {
        return cod_ref;
    }

    public void setCod_ref(String cod_ref) {
        this.cod_ref = cod_ref;
    }

    public String getCod_vend() {
        return cod_vend;
    }

    public void setCod_vend(String cod_vend) {
        this.cod_vend = cod_vend;
    }

    public String getFch_registro() {
        return fch_registro;
    }

    public void setFch_registro(String fch_registro) {
        this.fch_registro = fch_registro;
    }

    public String getGirador() {
        return girador;
    }

    public void setGirador(String girador) {
        this.girador = girador;
    }

    public String getCod_rec() {
        return cod_rec;
    }

    public void setCod_rec(String cod_rec) {
        this.cod_rec = cod_rec;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getCheque() {
        return cheque;
    }

    public void setCheque(String cheque) {
        this.cheque = cheque;
    }

    public String getFch_pago() {
        return fch_pago;
    }

    public void setFch_pago(String fch_pago) {
        this.fch_pago = fch_pago;
    }

    public String getCod_mov() {
        return cod_mov;
    }

    public void setCod_mov(String cod_mov) {
        this.cod_mov = cod_mov;
    }

    public String getNum_mov() {
        return num_mov;
    }

    public void setNum_mov(String num_mov) {
        this.num_mov = num_mov;
    }

    public String getPto_vta() {
        return pto_vta;
    }

    public void setPto_vta(String pto_vta) {
        this.pto_vta = pto_vta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
