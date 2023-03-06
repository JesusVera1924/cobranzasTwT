package com.twt.appcobranzas.model;

public class RowRecibo {
    private int id_recibo;
    private String fch_emision;
    private String cod_cli;
    private String nom_cli;
    private String cod_vend;
    private String vendedor;
    private String cod_mov;
    private String num_mov;
    private String tipo_cobro;
    private double valor;
    private String pto_vta;
    private int estado;
    private double suma;

    public RowRecibo(int id_recibo, String fch_emision, String cod_cli, String nom_cli, String cod_vend, String cod_mov, String num_mov,
                     String tipo_cobro, double valor, String pto_vta, int estado, double suma){
        this.setId_recibo(id_recibo);
        this.setFch_emision(fch_emision);
        this.setCod_cli(cod_cli);
        this.setNom_cli(nom_cli);
        this.setCod_vend(cod_vend);
        this.setCod_mov(cod_mov);
        this.setNum_mov(num_mov);
        this.setTipo_cobro(tipo_cobro);
        this.setValor(valor);
        this.setPto_vta(pto_vta);
        this.setEstado(estado);
        this.setSuma(suma);
    }

    public RowRecibo() {
        super();
    }


    public int getId_recibo() {
        return id_recibo;
    }

    public void setId_recibo(int id_recibo) {
        this.id_recibo = id_recibo;
    }

    public String getCod_cli() {
        return cod_cli;
    }

    public void setCod_cli(String cod_cli) {
        this.cod_cli = cod_cli;
    }

    public String getNom_cli() {
        return nom_cli;
    }

    public void setNom_cli(String nom_cli) {
        this.nom_cli = nom_cli;
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

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getPto_vta() {
        return pto_vta;
    }

    public void setPto_vta(String pto_vta) {
        this.pto_vta = pto_vta;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getFch_emision() {
        return fch_emision;
    }

    public void setFch_emision(String fch_emision) {
        this.fch_emision = fch_emision;
    }

    public String getCod_vend() {
        return cod_vend;
    }

    public void setCod_vend(String cod_vend) {
        this.cod_vend = cod_vend;
    }

    public double getSuma() {
        return suma;
    }

    public void setSuma(double suma) {
        this.suma = suma;
    }

    public String getTipo_cobro() {
        return tipo_cobro;
    }

    public void setTipo_cobro(String tipo_cobro) {
        this.tipo_cobro = tipo_cobro;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }
}
