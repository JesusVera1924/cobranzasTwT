package com.twt.appcobranzas.model;

/**
 * Created by Devecua on 01/09/2016.
 */
public class RowRetenciones {
    private int id;
    private String cod_emp;
    private int id_recibo;
    private String fch_ret;
    private String cod_ref;
    private String num_mov;
    private String cod_mov;
    private String fecha;
    private String estab;
    private String pto_emi;
    private String secuencia;
    private String numaut;
    private String cod_retencion;
    private double prc_retencion;
    private double valor;
    private String cod_ven;
    private String vendedor;
    private String cod_pto;
    private int estado;

    public RowRetenciones(int id, String cod_emp, int id_recibo, String fch_ret, String cod_ref, String num_mov, String cod_mov,
                          String fecha, String estab, String pto_emi, String secuencia, String numaut, String cod_retencion,
                          double prc_retencion, double valor, String cod_ven, String cod_pto, int estado){
        this.id = id;
        this.cod_emp = cod_emp;
        this.id_recibo = id_recibo;
        this.fch_ret = fch_ret;
        this.cod_ref = cod_ref;
        this.num_mov = num_mov;
        this.cod_mov = cod_mov;
        this.fecha = fecha;
        this.estab = estab;
        this.pto_emi = pto_emi;
        this.secuencia = secuencia;
        this.num_mov = num_mov;
        this.cod_retencion = cod_retencion;
        this.prc_retencion = prc_retencion;
        this.valor = valor;
        this.cod_ven = cod_ven;
        this.cod_pto = cod_pto;
        this.estado = estado;
    }

    public RowRetenciones(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_recibo() {
        return id_recibo;
    }

    public void setId_recibo(int id_recibo) {
        this.id_recibo = id_recibo;
    }

    public String getNum_mov() {
        return num_mov;
    }

    public void setNum_mov(String num_mov) {
        this.num_mov = num_mov;
    }

    public String getCod_mov() {
        return cod_mov;
    }

    public void setCod_mov(String cod_mov) {
        this.cod_mov = cod_mov;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstab() {
        return estab;
    }

    public void setEstab(String estab) {
        this.estab = estab;
    }

    public String getPto_emi() {
        return pto_emi;
    }

    public void setPto_emi(String pto_emi) {
        this.pto_emi = pto_emi;
    }

    public String getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(String secuencia) {
        this.secuencia = secuencia;
    }

    public String getCod_retencion() {
        return cod_retencion;
    }

    public void setCod_retencion(String cod_retencion) {
        this.cod_retencion = cod_retencion;
    }

    public double getPrc_retencion() {
        return prc_retencion;
    }

    public void setPrc_retencion(double prc_retencion) {
        this.prc_retencion = prc_retencion;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getCod_ven() {
        return cod_ven;
    }

    public void setCod_ven(String cod_ven) {
        this.cod_ven = cod_ven;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getCod_ref() {
        return cod_ref;
    }

    public void setCod_ref(String cod_ref) {
        this.cod_ref = cod_ref;
    }

    public String getCod_pto() {
        return cod_pto;
    }

    public void setCod_pto(String cod_pto) {
        this.cod_pto = cod_pto;
    }

    public String getFch_ret() {
        return fch_ret;
    }

    public void setFch_ret(String fch_ret) {
        this.fch_ret = fch_ret;
    }

    public String getNumaut() {
        return numaut;
    }

    public void setNumaut(String numaut) {
        this.numaut = numaut;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }
}
