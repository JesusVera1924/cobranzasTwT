package com.twt.appcobranzas.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by DEVECUA on 11/12/2015.
 */
public class Utils {

    public static String URLWS = "";
    public static final String URLWS_CLIENTES     = "service_cobranza/clientes?codemp=";
    public static final String URLWS_DOCUMENTOS   = "service_cobranza/documentos?codemp=";
    public static final String URLWS_BANCOS       = "service_cobranza/bancos?empr=";
    public static final String URLWS_CUENTAS      = "service_cobranza/cuentas?empr=";
    public static final String URLWS_RETENCIONES  = "service_cobranza/retenciones";
    public static final String URLWS_RETENCIONES2 = "service_cobranza/retenciones2";
    public static final String URLWS_VENDEDOR     = "service_cobranza/vendedor?codemp=";
    public static final String URLWS_SECUENCIAS   = "service_cobranza/secuencias?codemp=";
    public static final String URLWS_RECIBOS      = "service_cobranza/recibos";
    public static final String URLWS_RECIBOS2     = "service_cobranza/recibos2";
    public static final String URLWS_UPDATESEC    = "service_cobranza/uptsecuencias";
    public static final String URLWS_RETENCION    = "service_cobranza/cod_retenciones?codemp=";
    public static final String URLWS_CLIENTESREL  = "service_cobranza/clientes_rel?empr=";
    public static final String URLWS_DESCARGAS    = "service_cobranza/descarga?codemp=";
    public static final String dateFormatStringCorto  = "yyyy-MM-dd";
    public static final String dateFormatStringCorto1 = "dd-MM-yyyy";
    public static final String dateFormatStringCorto2 = "dd-MM-yyyy";
    public static final String basicAuth = "Basic " + new String(Base64.encode("admin:d3v3cua".getBytes(), Base64.NO_WRAP));


    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            Log.v("Utils ERROR", e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public static boolean isOnline(Context context) {
        boolean online=false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi.isConnected()) {
            online=true;
        }
        return online;
    }

    public static Double Redondear(Double nD, int nDec){
        return Math.round(nD* Math.pow(10,nDec))/ Math.pow(10,nDec);
    }

    /*public static boolean spaOnline(Context context){
        boolean online = false;
        HttpURLConnection connection = null;
        //ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            URL url = new URL(URLONLINE);
            connection = (HttpURLConnection) url.openConnection();
            int statusCode = connection.getResponseCode();
            System.out.println(statusCode);
            if (statusCode == 200)
                online = true;
            else
                online = false;
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return online;
    }*/

    public static Date ConvertirShortStringToDate(String dtStart) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormatStringCorto);
        Date fecha = null;

        try {
            fecha = new java.sql.Date(format.parse(dtStart).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return fecha;
    }

    public static String ConvertirDateToStringShort(Date dtStart) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormatStringCorto1);
        String fecha = null;

        try {
            fecha = format.format(dtStart);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fecha;
    }

    public static Date ConvertirShortStringToDate2(String dtStart) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormatStringCorto1);
        Date fecha = null;

        try {
            fecha = new java.sql.Date(format.parse(dtStart).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return fecha;
    }
}
