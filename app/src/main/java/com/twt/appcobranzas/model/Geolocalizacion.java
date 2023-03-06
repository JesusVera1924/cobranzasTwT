package com.twt.appcobranzas.model;

import java.util.Date;

/**
 * Created by dell on 09/02/2019.
 */

public class Geolocalizacion {
    private int    id;
    private String cod_emp;
    private String cod_ven;
    private String latitud;
    private String longitud;
    private Date fch_reg;
    private String tip_geo;

    public Geolocalizacion(){

    }

    public Geolocalizacion(int id, String cod_emp, String cod_ven, String latitud, String longitud, Date fch_reg, String tip_geo) {
        this.id = id;
        this.cod_emp = cod_emp;
        this.cod_ven = cod_ven;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fch_reg = fch_reg;
        this.tip_geo = tip_geo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public String getCod_ven() {
        return cod_ven;
    }

    public void setCod_ven(String cod_ven) {
        this.cod_ven = cod_ven;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public Date getFch_reg() {
        return fch_reg;
    }

    public void setFch_reg(Date fch_reg) {
        this.fch_reg = fch_reg;
    }

    public String getTip_geo() {
        return tip_geo;
    }

    public void setTip_geo(String tip_geo) {
        this.tip_geo = tip_geo;
    }
}
