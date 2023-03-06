package com.twt.appcobranzas.model;

/**
 * Created by Devecua on 31/08/2016.
 */
public class VendedorWS {
    private String empresa;
    private String cod_ref;
    private String vendedor;
    private String seleccion;
    private String vendedor1;
    private String vendedor2;
    private String vendedor3;
    private String vendedor4;
    private String vendedor5;
    private int    recibo;
    private int    retenc;
    private int    ncredi;
    private int    ncinte;
    private int    ult_descarga;
    private int    ult_recibo;
    private int    status;

    public VendedorWS(String empresa, String cod_ref, String vendedor, String seleccion, String vendedor1, String vendedor2, String vendedor3,
                      String vendedor4, String vendedor5, int recibo, int retenc, int ncredi, int ncinte, int ult_descarga, int ult_recibo, int status){
        this.empresa = empresa;
        this.cod_ref = cod_ref;
        this.vendedor = vendedor;
        this.seleccion = seleccion;
        this.vendedor1 = vendedor1;
        this.vendedor2 = vendedor2;
        this.vendedor3 = vendedor3;
        this.vendedor4 = vendedor4;
        this.vendedor5 = vendedor5;
        this.recibo = recibo;
        this.retenc = retenc;
        this.ncredi = ncredi;
        this.ncinte = ncinte;
        this.ult_descarga = ult_descarga;
        this.ult_recibo = ult_recibo;
        this.status = status;
    }

    public VendedorWS(){

    }

    public String getCod_ref() {
        return cod_ref;
    }

    public void setCod_ref(String cod_ref) {
        this.cod_ref = cod_ref;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUlt_descarga() {
        return ult_descarga;
    }

    public void setUlt_descarga(int ult_descarga) {
        this.ult_descarga = ult_descarga;
    }

    public int getUlt_recibo() {
        return ult_recibo;
    }

    public void setUlt_recibo(int ult_recibo) {
        this.ult_recibo = ult_recibo;
    }

    public int getRecibo() {
        return recibo;
    }

    public void setRecibo(int recibo) {
        this.recibo = recibo;
    }

    public int getRetenc() {
        return retenc;
    }

    public void setRetenc(int retenc) {
        this.retenc = retenc;
    }

    public int getNcredi() {
        return ncredi;
    }

    public void setNcredi(int ncredi) {
        this.ncredi = ncredi;
    }

    public int getNcinte() {
        return ncinte;
    }

    public void setNcinte(int ncinte) {
        this.ncinte = ncinte;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getSeleccion() {
        return seleccion;
    }

    public void setSeleccion(String seleccion) {
        this.seleccion = seleccion;
    }

    public String getVendedor1() {
        return vendedor1;
    }

    public void setVendedor1(String vendedor1) {
        this.vendedor1 = vendedor1;
    }

    public String getVendedor2() {
        return vendedor2;
    }

    public void setVendedor2(String vendedor2) {
        this.vendedor2 = vendedor2;
    }

    public String getVendedor3() {
        return vendedor3;
    }

    public void setVendedor3(String vendedor3) {
        this.vendedor3 = vendedor3;
    }

    public String getVendedor4() {
        return vendedor4;
    }

    public void setVendedor4(String vendedor4) {
        this.vendedor4 = vendedor4;
    }

    public String getVendedor5() {
        return vendedor5;
    }

    public void setVendedor5(String vendedor5) {
        this.vendedor5 = vendedor5;
    }
}
