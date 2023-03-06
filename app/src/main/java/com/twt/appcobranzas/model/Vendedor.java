package com.twt.appcobranzas.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Devecua on 20/04/2016.
 */
public class Vendedor implements Parcelable {
    private int    id;
    private String cod_emp;
    private String cod_vend;
    private String nombre_vend;
    private String correo_vend;
    private String pin;
    private String url;
    private String seleccion;
    private String vend1;
    private String vend2;
    private String vend3;
    private String vend4;
    private String vend5;
    private int    estado;

    public Vendedor(int id, String cod_emp, String cod_vend, String nombre_vend, String correo_vend,
                    String pin, String url, String seleccion, String vend1, String vend2, String vend3, String vend4, String vend5, int estado){
        this.id = id;
        this.cod_emp = cod_emp;
        this.cod_vend = cod_vend;
        this.nombre_vend = nombre_vend;
        this.correo_vend = correo_vend;
        this.pin = pin;
        this.url = url;
        this.seleccion = seleccion;
        this.vend1 = vend1;
        this.vend2 = vend2;
        this.vend3 = vend3;
        this.vend4 = vend4;
        this.vend5 = vend5;
        this.estado = estado;
    }

    public Vendedor(){

    }

    public Vendedor(Parcel in) {
        cod_emp = in.readString();
        cod_vend = in.readString();
        nombre_vend = in.readString();
        correo_vend = in.readString();
        pin = in.readString();
        url = in.readString();
        seleccion = in.readString();
        vend1 = in.readString();
        vend2 = in.readString();
        vend3 = in.readString();
        vend4 = in.readString();
        vend5 = in.readString();
        estado = in.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCod_vend() {
        return cod_vend;
    }

    public void setCod_vend(String cod_vend) {
        this.cod_vend = cod_vend;
    }

    public String getNombre_vend() {
        return nombre_vend;
    }

    public void setNombre_vend(String nombre_vend) {
        this.nombre_vend = nombre_vend;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cod_emp);
        dest.writeString(cod_vend);
        dest.writeString(nombre_vend);
        dest.writeString(correo_vend);
        dest.writeString(pin);
        dest.writeString(url);
        dest.writeString(seleccion);
        dest.writeString(vend1);
        dest.writeString(vend2);
        dest.writeString(vend3);
        dest.writeString(vend4);
        dest.writeString(vend5);
        dest.writeInt(estado);
    }

    public static final Creator<Vendedor> CREATOR = new Creator<Vendedor>()
    {
        public Vendedor createFromParcel(Parcel in)
        {
            return new Vendedor(in);
        }
        public Vendedor[] newArray(int size)
        {
            return new Vendedor[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCorreo_vend() {
        return correo_vend;
    }

    public void setCorreo_vend(String correo_vend) {
        this.correo_vend = correo_vend;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public String getSeleccion() {
        return seleccion;
    }

    public void setSeleccion(String seleccion) {
        this.seleccion = seleccion;
    }

    public String getVend1() {
        return vend1;
    }

    public void setVend1(String vend1) {
        this.vend1 = vend1;
    }

    public String getVend2() {
        return vend2;
    }

    public void setVend2(String vend2) {
        this.vend2 = vend2;
    }

    public String getVend3() {
        return vend3;
    }

    public void setVend3(String vend3) {
        this.vend3 = vend3;
    }

    public String getVend4() {
        return vend4;
    }

    public void setVend4(String vend4) {
        this.vend4 = vend4;
    }

    public String getVend5() {
        return vend5;
    }

    public void setVend5(String vend5) {
        this.vend5 = vend5;
    }
}
