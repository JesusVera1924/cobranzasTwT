package com.twt.appcobranzas.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Devecua on 06/05/2016.
 */
public class Cobros_Doc implements Parcelable {
    private int id_recibo;
    private String cod_emp;
    private String fch_emision;
    private String cod_cli;
    private String nom_cli;
    private String cod_vend;
    private String cod_vin;
    private String vendedor2;
    private String cod_mov;
    private String num_mov;
    private String fch_emi;
    private String fch_ven;
    private String tipo_cobro;
    private String num_doc;
    private String cod_bco;
    private String num_cuenta;
    private double valor;
    private String pto_vta;
    private int estado;

    public Cobros_Doc(int id_recibo, String cod_emp, String fch_emision, String cod_cli, String nom_cli, String cod_vend,
                      String cod_vin, String vendedor2, String cod_mov, String num_mov, String fch_emi, String fch_ven, String tipo_cobro,
                      String num_doc, String cod_bco, String num_cuenta, double valor, String pto_vta, int estado){
        this.id_recibo = id_recibo;
        this.cod_emp = cod_emp;
        this.fch_emision = fch_emision;
        this.cod_cli = cod_cli;
        this.nom_cli = nom_cli;
        this.cod_vend = cod_vend;
        this.cod_vin = cod_vin;
        this.vendedor2 = vendedor2;
        this.cod_mov = cod_mov;
        this.num_mov = num_mov;
        this.fch_emision = fch_emi;
        this.fch_ven = fch_ven;
        this.tipo_cobro = tipo_cobro;
        this.num_doc = num_doc;
        this.cod_bco = cod_bco;
        this.num_cuenta = num_cuenta;
        this.valor = valor;
        this.pto_vta = pto_vta;
        this.estado = estado;
    }

    public Cobros_Doc(){

    }

    public Cobros_Doc(Parcel in) {
        id_recibo = in.readInt();
        cod_emp = in.readString();
        fch_emision = in.readString();
        cod_cli = in.readString();
        nom_cli = in.readString();
        cod_vend = in.readString();
        cod_vin = in.readString();
        vendedor2 = in.readString();
        cod_mov = in.readString();
        num_mov = in.readString();
        fch_emi = in.readString();
        fch_ven = in.readString();
        tipo_cobro = in.readString();
        num_doc = in.readString();
        cod_bco = in.readString();
        num_cuenta = in.readString();
        valor = in.readDouble();
        pto_vta = in.readString();
        estado = in.readInt();
    }

    public int getId_recibo() {
        return id_recibo;
    }

    public void setId_recibo(int id_recibo) {
        this.id_recibo = id_recibo;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_recibo);
        dest.writeString(cod_emp);
        dest.writeString(fch_emision);
        dest.writeString(cod_cli);
        dest.writeString(nom_cli);
        dest.writeString(cod_vend);
        dest.writeString(cod_vin);
        dest.writeString(vendedor2);
        dest.writeString(cod_mov);
        dest.writeString(num_mov);
        dest.writeString(fch_emi);
        dest.writeString(fch_ven);
        dest.writeString(tipo_cobro);
        dest.writeString(num_doc);
        dest.writeString(cod_bco);
        dest.writeString(num_cuenta);
        dest.writeDouble(valor);
        dest.writeString(pto_vta);
        dest.writeInt(estado);
    }

    public static final Creator<Cobros_Doc> CREATOR = new Creator<Cobros_Doc>()
    {
        public Cobros_Doc createFromParcel(Parcel in)
        {
            return new Cobros_Doc(in);
        }
        public Cobros_Doc[] newArray(int size)
        {
            return new Cobros_Doc[size];
        }
    };

    public String getTipo_cobro() {
        return tipo_cobro;
    }

    public void setTipo_cobro(String tipo_cobro) {
        this.tipo_cobro = tipo_cobro;
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

    public String getFch_emi() {
        return fch_emi;
    }

    public void setFch_emi(String fch_emi) {
        this.fch_emi = fch_emi;
    }

    public String getFch_ven() {
        return fch_ven;
    }

    public void setFch_ven(String fch_ven) {
        this.fch_ven = fch_ven;
    }

    public String getNum_doc() {
        return num_doc;
    }

    public void setNum_doc(String num_doc) {
        this.num_doc = num_doc;
    }

    public String getCod_vin() {
        return cod_vin;
    }

    public void setCod_vin(String cod_vin) {
        this.cod_vin = cod_vin;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public String getVendedor2() {
        return vendedor2;
    }

    public void setVendedor2(String vendedor2) {
        this.vendedor2 = vendedor2;
    }
}
