package com.twt.appcobranzas.model;

import java.util.Date;

/**
 * Created by Devecua on 14/04/2016.
 */
public class Facturas {
    private String cod_emp;
    private String cod_mov;
    private String cod_pto;
    private String cod_ref;
    private String cod_ven;
    private int dias_pendiente;
    private Date fec_emi;
    private Date fec_ven;
    private String num_mov;
    private String num_rel;
    private double sdo_mov;
    private double val_mov;
    private double iva;
    private double base;
    private String sts_mov;
    private String cod_bco;
    private String num_cuenta;
    private boolean seleccion = false;
    private double abono;

    public Facturas(String cod_emp, String cod_mov, String cod_pto, String cod_ref, String cod_ven,int dias_pendiente, Date fec_emi, Date fec_ven,
                    String num_mov, String num_rel, double sdo_mov, double val_mov, double iva, double base, String sts_mov, String cod_bco, String num_cuenta,
                    boolean sel, double abono){
        this.setCod_emp(cod_emp);
        this.setCod_mov(cod_mov);
        this.setCod_pto(cod_pto);
        this.setCod_ref(cod_ref);
        this.setCod_ven(cod_ven);
        this.setDias_pendiente(dias_pendiente);
        this.setFec_emi(fec_emi);
        this.setFec_ven(fec_ven);
        this.setNum_mov(num_mov);
        this.setNum_rel(num_rel);
        this.setSdo_mov(sdo_mov);
        this.setVal_mov(val_mov);
        this.setIva(iva);
        this.setBase(base);
        this.setSts_mov(sts_mov);
        this.setCod_bco(cod_bco);
        this.setNum_cuenta(num_cuenta);
        this.setSeleccion(sel);
        this.setAbono(abono);
    }

    public Facturas(){

    }

    public String getCod_pto() {
        return cod_pto;
    }

    public void setCod_pto(String cod_pto) {
        this.cod_pto = cod_pto;
    }

    public String getCod_ref() {
        return cod_ref;
    }

    public void setCod_ref(String cod_ref) {
        this.cod_ref = cod_ref;
    }

    public int getDias_pendiente() {
        return dias_pendiente;
    }

    public void setDias_pendiente(int dias_pendiente) {
        this.dias_pendiente = dias_pendiente;
    }

    public Date getFec_emi() {
        return fec_emi;
    }

    public void setFec_emi(Date fec_emi) {
        this.fec_emi = fec_emi;
    }

    public Date getFec_ven() {
        return fec_ven;
    }

    public void setFec_ven(Date fec_ven) {
        this.fec_ven = fec_ven;
    }

    public String getNum_mov() {
        return num_mov;
    }

    public void setNum_mov(String num_mov) {
        this.num_mov = num_mov;
    }

    public double getSdo_mov() {
        return sdo_mov;
    }

    public void setSdo_mov(double sdo_mov) {
        this.sdo_mov = sdo_mov;
    }

    public double getVal_mov() {
        return val_mov;
    }

    public void setVal_mov(double val_mov) {
        this.val_mov = val_mov;
    }

    public boolean isSeleccion() {
        return seleccion;
    }

    public void setSeleccion(boolean seleccion) {
        this.seleccion = seleccion;
    }

    public double getAbono() {
        return abono;
    }

    public void setAbono(double abono) {
        this.abono = abono;
    }

    public String getCod_mov() {
        return cod_mov;
    }

    public void setCod_mov(String cod_mov) {
        this.cod_mov = cod_mov;
    }

    public String getSts_mov() {
        return sts_mov;
    }

    public void setSts_mov(String sts_mov) {
        this.sts_mov = sts_mov;
    }

    public String getCod_ven() {
        return cod_ven;
    }

    public void setCod_ven(String cod_ven) {
        this.cod_ven = cod_ven;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public String getNum_rel() {
        return num_rel;
    }

    public void setNum_rel(String num_rel) {
        this.num_rel = num_rel;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public String getCod_bco() {
        return cod_bco;
    }

    public void setCod_bco(String cod_bco) {
        this.cod_bco = cod_bco;
    }

    public String getNum_cuenta() {
        return num_cuenta;
    }

    public void setNum_cuenta(String num_cuenta) {
        this.num_cuenta = num_cuenta;
    }
}
