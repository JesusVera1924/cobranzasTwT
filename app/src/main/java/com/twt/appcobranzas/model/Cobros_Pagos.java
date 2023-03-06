package com.twt.appcobranzas.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Devecua on 02/05/2016.
 */
public class Cobros_Pagos implements Parcelable {
    private int id_recibo;
    private String cod_emp;
    private String fpago;
    private String banco;
    private String cuenta;
    private String cheque;
    private String fch_pago;
    private double valor;
    private String cod_ref;
    private String cod_vend;
    private String cod_vin;
    private String vendedor2;
    private Date fch_registro;
    private String girador;
    private String cod_rec;
    private String status;

    public Cobros_Pagos(int id_recibo, String cod_emp,  String fpago, String banco, String cuenta, String cheque,
                        String fch_pago, double valor, String cod_ref, String cod_vend, String cod_vin, String vendedor2,
                        Date fch_registro, String girador, String cod_rec, String status){
        this.id_recibo = id_recibo;
        this.cod_emp = cod_emp;
        this.fpago = fpago;
        this.banco = banco;
        this.cuenta = cuenta;
        this.cheque = cheque;
        this.fch_pago = fch_pago;
        this.valor = valor;
        this.cod_ref = cod_ref;
        this.cod_vend = cod_vend;
        this.cod_vin = cod_vin;
        this.vendedor2 = vendedor2;
        this.fch_registro = fch_registro;
        this.girador = girador;
        this.cod_rec = cod_rec;
        this.status = status;
    }

    public Cobros_Pagos(){

    }

    public Cobros_Pagos(Parcel in) {
        id_recibo = in.readInt();
        cod_emp = in.readString();
        fpago = in.readString();
        banco = in.readString();
        cuenta = in.readString();
        cheque = in.readString();
        fch_pago = in.readString();
        valor = in.readDouble();
        cod_ref = in.readString();
        cod_vend = in.readString();
        cod_vin = in.readString();
        vendedor2 = in.readString();
        girador = in.readString();
        cod_rec = in.readString();
        status = in.readString();
    }

    public int getId_recibo() {
        return id_recibo;
    }

    public void setId_recibo(int id_recibo) {
        this.id_recibo = id_recibo;
    }

    public String getFpago() {
        return fpago;
    }

    public void setFpago(String fpago) {
        this.fpago = fpago;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getCod_ref() {
        return cod_ref;
    }

    public void setCod_ref(String cod_ref) {
        this.cod_ref = cod_ref;
    }

    public String getCod_vend() {
        return cod_vend;
    }

    public void setCod_vend(String cod_vend) {
        this.cod_vend = cod_vend;
    }

    public Date getFch_registro() {
        return fch_registro;
    }

    public void setFch_registro(Date fch_registro) {
        this.fch_registro = fch_registro;
    }

    public String getGirador() {
        return girador;
    }

    public void setGirador(String girador) {
        this.girador = girador;
    }

    public String getCod_rec() {
        return cod_rec;
    }

    public void setCod_rec(String cod_rec) {
        this.cod_rec = cod_rec;
    }

    public String getCheque() {
        return cheque;
    }

    public void setCheque(String cheque) {
        this.cheque = cheque;
    }

    public String getFch_pago() {
        return fch_pago;
    }

    public void setFch_pago(String fch_pago) {
        this.fch_pago = fch_pago;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_recibo);
        dest.writeString(cod_emp);
        dest.writeString(fpago);
        dest.writeString(banco);
        dest.writeString(cuenta);
        dest.writeString(cheque);
        dest.writeString(fch_pago);
        dest.writeDouble(valor);
        dest.writeString(cod_ref);
        dest.writeString(cod_vend);
        dest.writeString(cod_vin);
        dest.writeString(vendedor2);
        dest.writeString(girador);
        dest.writeString(cod_rec);
        dest.writeString(status);
    }

    public static final Creator<Cobros_Pagos> CREATOR = new Creator<Cobros_Pagos>()
    {
        public Cobros_Pagos createFromParcel(Parcel in)
        {
            return new Cobros_Pagos(in);
        }
        public Cobros_Pagos[] newArray(int size)
        {
            return new Cobros_Pagos[size];
        }
    };

    public String toString(){
        return "Pago: "+fpago+"("+status+")"+"\nCod.Bco. "+banco+"\n#Cta. "+cuenta+"\n#Doc. "+cheque+"\nAbono: $"+valor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
