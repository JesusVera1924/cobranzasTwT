package com.twt.appcobranzas.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class RowFactura implements Parcelable {
    private String cod_emp;
    private String cod_mov;
    private String cod_pto;
    private String cod_ref;
    private String cod_ven;
    private String cod_vin;
    private String vendedor;
    private int dias_pendiente;
    private Date fec_emi;
    private Date fec_ven;
    private String fec_emis;
    private String fec_venc;
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
    private boolean seleccion2 = false;
    private double abono;
    private double reten;


    public RowFactura(String cod_emp, String cod_mov, String cod_pto, String cod_ref, String cod_ven, int dias_pendiente, String fec_emis, String fec_venc,
                      String num_mov, String num_rel, double sdo_mov, double val_mov, double iva, double base, String sts_mov, String cod_bco, String num_cuenta,
                      boolean sel, boolean sel2, double abono){
        this.setCod_emp(cod_emp);
        this.setCod_mov(cod_mov);
        this.setCod_pto(cod_pto);
        this.setCod_ref(cod_ref);
        this.setCod_ven(cod_ven);
        this.setDias_pendiente(dias_pendiente);
        this.setFec_emis(fec_emis);
        this.setFec_venc(fec_venc);
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
        this.setSeleccion2(sel2);
        this.setAbono(abono);
    }

    public RowFactura(){

    }

    public RowFactura(Parcel in) {
        cod_emp = in.readString();
        cod_mov = in.readString();
        cod_pto = in.readString();
        cod_ref = in.readString();
        cod_ven = in.readString();
        cod_vin = in.readString();
        dias_pendiente = in.readInt();
        fec_emis = in.readString();
        fec_venc = in.readString();
        num_mov = in.readString();
        num_rel = in.readString();
        sdo_mov = in.readDouble();
        val_mov = in.readDouble();
        iva = in.readDouble();
        base = in.readDouble();
        sts_mov = in.readString();
        cod_bco = in.readString();
        num_cuenta = in.readString();
        abono = in.readDouble();
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
        dest.writeString(cod_vin);
        dest.writeInt(dias_pendiente);
        dest.writeString(fec_emis);
        dest.writeString(fec_venc);
        dest.writeString(num_mov);
        dest.writeString(num_rel);
        dest.writeDouble(sdo_mov);
        dest.writeDouble(val_mov);
        dest.writeDouble(iva);
        dest.writeDouble(base);
        dest.writeString(sts_mov);
        dest.writeString(cod_bco);
        dest.writeString(num_cuenta);
        dest.writeDouble(abono);
    }

    public static final Creator<RowFactura> CREATOR = new Creator<RowFactura>()
    {
        public RowFactura createFromParcel(Parcel in)
        {
            return new RowFactura(in);
        }
        public RowFactura[] newArray(int size)
        {
            return new RowFactura[size];
        }
    };

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

    public String getFec_emis() {
        return fec_emis;
    }

    public void setFec_emis(String fec_emis) {
        this.fec_emis = fec_emis;
    }

    public String getFec_venc() {
        return fec_venc;
    }

    public void setFec_venc(String fec_venc) {
        this.fec_venc = fec_venc;
    }

    public String getCod_vin() {
        return cod_vin;
    }

    public void setCod_vin(String cod_vin) {
        this.cod_vin = cod_vin;
    }

    public boolean isSeleccion2() {
        return seleccion2;
    }

    public void setSeleccion2(boolean seleccion2) {
        this.seleccion2 = seleccion2;
    }

    public double getReten() {
        return reten;
    }

    public void setReten(double reten) {
        this.reten = reten;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }
}
