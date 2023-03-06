package com.twt.appcobranzas.model;

/**
 * Created by Devecua on 31/08/2016.
 */
public class Retenciones {
    private String empresa;
    private String cod_retencion;
    private String nom_retencion;
    private Double prc_retencion;
    private Double lim_ret;
    private String sts_rxt;
    private String fec_rxt;
    private String fex_rxt;
    private Double por_rxt;


    public Retenciones(String empresa, String cod_retencion, String nom_retencion, Double prc_retencion,
                       Double lim_ret, String sts_rxt, String fec_rxt, String fex_rxt, Double por_rxt){
        this.empresa = empresa;
        this.cod_retencion = cod_retencion;
        this.nom_retencion = nom_retencion;
        this.prc_retencion = prc_retencion;
        this.lim_ret = lim_ret;
        this.sts_rxt = sts_rxt;
        this.fec_rxt = fec_rxt;
        this.fex_rxt = fex_rxt;
        this.por_rxt = por_rxt;
    }

    public Retenciones(){

    }

    public String getCod_retencion() {
        return cod_retencion;
    }

    public void setCod_retencion(String cod_retencion) {
        this.cod_retencion = cod_retencion;
    }

    public String getNom_retencion() {
        return nom_retencion;
    }

    public void setNom_retencion(String nom_retencion) {
        this.nom_retencion = nom_retencion;
    }

    public Double getPrc_retencion() {
        return prc_retencion;
    }

    public void setPrc_retencion(double prc_retencion) {
        this.prc_retencion = prc_retencion;
    }

    public String toString(){
        return nom_retencion;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public Double getLim_ret() {
        return lim_ret;
    }

    public void setLim_ret(Double lim_ret) {
        this.lim_ret = lim_ret;
    }

    public String getFec_rxt() {
        return fec_rxt;
    }

    public void setFec_rxt(String fec_rxt) {
        this.fec_rxt = fec_rxt;
    }

    public String getFex_rxt() {
        return fex_rxt;
    }

    public void setFex_rxt(String fex_rxt) {
        this.fex_rxt = fex_rxt;
    }

    public Double getPor_rxt() {
        return por_rxt;
    }

    public void setPor_rxt(Double por_rxt) {
        this.por_rxt = por_rxt;
    }

    public String getSts_rxt() {
        return sts_rxt;
    }

    public void setSts_rxt(String sts_rxt) {
        this.sts_rxt = sts_rxt;
    }
}
