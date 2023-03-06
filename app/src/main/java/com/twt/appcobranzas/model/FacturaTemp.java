package com.twt.appcobranzas.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FacturaTemp implements Parcelable {
    private String cod_emp;
    private String cod_mov;
    private String cod_pto;
    private String cod_ref;
    private String cod_ven;
    private String cod_vin;
    private String num_mov;
    private String num_rel;
    private double sdo_mov;
    private double val_mov;
    private double valor;
    private double iva;
    private double base;

    public FacturaTemp(String cod_emp, String cod_mov, String cod_pto, String cod_ref, String cod_ven,
                       String num_mov, String num_rel, double sdo_mov, double val_mov, double valor, double iva, double base){
        this.setCod_emp(cod_emp);
        this.setCod_mov(cod_mov);
        this.setCod_pto(cod_pto);
        this.setCod_ref(cod_ref);
        this.setCod_ven(cod_ven);
        this.setNum_mov(num_mov);
        this.setNum_rel(num_rel);
        this.setSdo_mov(sdo_mov);
        this.setVal_mov(val_mov);
        this.setValor(valor);
        this.setIva(iva);
        this.setBase(base);
    }

    public FacturaTemp(){

    }

    public FacturaTemp(Parcel in) {
        cod_emp = in.readString();
        cod_mov = in.readString();
        cod_pto = in.readString();
        cod_ref = in.readString();
        cod_ven = in.readString();
        num_mov = in.readString();
        num_rel = in.readString();
        sdo_mov = in.readDouble();
        val_mov = in.readDouble();
        valor = in.readDouble();
        iva = in.readDouble();
        base = in.readDouble();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cod_emp);
        dest.writeString(cod_mov);
        dest.writeString(cod_pto);
        dest.writeString(cod_ref);
        dest.writeString(cod_ven);
        dest.writeString(num_mov);
        dest.writeString(num_rel);
        dest.writeDouble(sdo_mov);
        dest.writeDouble(val_mov);
        dest.writeDouble(valor);
        dest.writeDouble(iva);
        dest.writeDouble(base);
    }

    public static final Creator<FacturaTemp> CREATOR = new Creator<FacturaTemp>()
    {
        public FacturaTemp createFromParcel(Parcel in)
        {
            return new FacturaTemp(in);
        }
        public FacturaTemp[] newArray(int size)
        {
            return new FacturaTemp[size];
        }
    };

    public String getCod_mov() {
        return cod_mov;
    }

    public void setCod_mov(String cod_mov) {
        this.cod_mov = cod_mov;
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

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
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

    public String getCod_vin() {
        return cod_vin;
    }

    public void setCod_vin(String cod_vin) {
        this.cod_vin = cod_vin;
    }
}
