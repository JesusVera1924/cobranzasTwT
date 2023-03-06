package com.twt.appcobranzas.model;

/**
 * Created by Devecua on 04/05/2016.
 */
public class ClientesRel {
    private String empresa;
    private String cod_ref;
    private String cod_vin;
    private int ord_vin;

    public ClientesRel(String empresa, String cod_ref, String cod_vin, int ord_vin){
        this.empresa = empresa;
        this.cod_ref = cod_ref;
        this.cod_vin = cod_vin;
        this.ord_vin = ord_vin;
    }

    public ClientesRel(){}

    public String getCod_ref() {
        return cod_ref;
    }

    public void setCod_ref(String cod_ref) {
        this.cod_ref = cod_ref;
    }

    public String getCod_vin() {
        return cod_vin;
    }

    public void setCod_vin(String cod_vin) {
        this.cod_vin = cod_vin;
    }

    public int getOrd_vin() {
        return ord_vin;
    }

    public void setOrd_vin(int ord_vin) {
        this.ord_vin = ord_vin;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }
}
