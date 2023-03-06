package com.twt.appcobranzas.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by DEVECUA on 23/11/2015.
 */
public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 14;
    public static final String DATABASE_NAME = "/mnt/sdcard/marsellacobros.db";

    private static final String SQL_CREATE_CLIENTES     = "CREATE TABLE CLIENTES (id INTEGER PRIMARY KEY AUTOINCREMENT, cod_emp TEXT, " +
                                                          "cliente TEXT, n_cliente TEXT, n_clienteaux TEXT, direccion TEXT, ciudad TEXT, " +
                                                          "provincia TEXT, ruc TEXT, telefono TEXT, vendedor TEXT, descuento REAL, " +
                                                          "forma_pago TEXT, mail1 TEXT, mail2 TEXT, persona TEXT)";
    private static final String SQL_CREATE_DOC_WS       = "CREATE TABLE DOC_WS (id INTEGER PRIMARY KEY AUTOINCREMENT, cod_emp TEXT, " +
                                                          "cod_mov TEXT, cod_pto TEXT, cod_ref TEXT, cod_ven TEXT, dias_pend INTEGER, " +
                                                          "fec_emi TEXT, fec_ven TEXT, num_mov TEXT, num_rel TEXT, sdo_mov REAL, " +
                                                          "val_mov REAL, iva REAL, base REAL, sts_mov TEXT, cod_bco TEXT, num_cuenta TEXT)";
    private static final String SQL_CREATE_DOC_LOCAL    = "CREATE TABLE DOC_LOCAL (id  INTEGER  PRIMARY KEY AUTOINCREMENT, cod_emp TEXT, " +
                                                          "cod_mov TEXT, cod_pto TEXT, cod_ref TEXT, cod_vin TEXT, cod_ven TEXT, vendedor TEXT, " +
                                                          "dias_pend INTEGER, fec_emi TEXT, fec_ven TEXT, num_mov TEXT, num_rel TEXT, " +
                                                          "sdo_mov REAL, val_mov REAL, iva REAL, base REAL, sts_mov TEXT, cod_bco TEXT, " +
                                                          "num_cuenta TEXT)";
    private static final String SQL_CREATE_VENDEDOR     = "CREATE TABLE VENDEDOR (id INTEGER PRIMARY KEY AUTOINCREMENT, cod_emp TEXT, " +
                                                          "cod_vend TEXT, nombre_vend TEXT, correo_vend TEXT, pin TEXT, url TEXT, " +
                                                          "seleccion TEXT, vend1 TEXT, vend2 TEXT, vend3 TEXT, vend4 TEXT, vend5 TEXT, estado INTEGER)";
    private static final String SQL_CREATE_SECUENCIA    = "CREATE TABLE SECUENCIA (id INTEGER  PRIMARY KEY AUTOINCREMENT, cod_emp TEXT, " +
                                                          "tipo TEXT, secuencia INTEGER, cod_ven TEXT)";
    private static final String SQL_CREATE_COBROS_PAGOS = "CREATE TABLE COBROS_PAGOS (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                          "cod_emp TEXT, id_recibo  INTEGER, fpago TEXT, banco TEXT, cuenta TEXT, cheque TEXT, " +
                                                          "fch_pago TEXT, valor REAL, cod_ref TEXT, cod_vend TEXT, cod_vin TEXT, vendedor TEXT, " +
                                                          "fch_registro INTEGER, girador TEXT, cod_rec TEXT, status TEXT)";
    private static final String SQL_CREATE_COBROS_DOC   = "CREATE TABLE COBROS_DOC (id INTEGER  PRIMARY KEY AUTOINCREMENT, " +
                                                          "cod_emp TEXT, id_recibo  INTEGER, fch_emision TEXT, cod_cli TEXT, nom_cli TEXT, " +
                                                          "cod_vend TEXT, cod_vin TEXT, vendedor TEXT, cod_mov TEXT, num_mov TEXT, fch_emi TEXT, " +
                                                          "fch_ven TEXT, tipo_cobro TEXT, num_doc TEXT, cod_bco TEXT, num_cuenta TEXT, " +
                                                          "valor REAL, pto_vta TEXT, estado INTEGER)";
    private static final String SQL_CREATE_COBROS_RET   = "CREATE TABLE COBROS_RET (id INTEGER  PRIMARY KEY AUTOINCREMENT, " +
                                                          "cod_emp TEXT, id_recibo  INTEGER, fch_ret TEXT, cod_ref TEXT, num_mov TEXT, " +
                                                          "cod_mov TEXT, fecha TEXT, estab TEXT, pto_emi TEXT, secuencia TEXT, num_aut TEXT, " +
                                                          "cod_retencion TEXT, prc_retencion REAL, valor REAL, cod_ven TEXT, vendedor TEXT, " +
                                                          "cod_pto TEXT, estado INTEGER)";
    private static final String SQL_CREATE_GEOLOCALIZA  = "CREATE TABLE GEOLOCALIZACION (id INTEGER  PRIMARY KEY AUTOINCREMENT, " +
                                                          "empresa TEXT, cod_ven INTEGER, latitud TEXT, longitud TEXT, fch_reg TEXT, " +
                                                          "tip_geo TEXT)";
    private static final String SQL_CREATE_BANCO_CH     = "CREATE TABLE BANCOS_CH (cod_emp TEXT, cod_bco TEXT, nom_banco TEXT)";
    private static final String SQL_CREATE_BANCO_DP     = "CREATE TABLE BANCOS_DP (cod_emp TEXT, cod_bco TEXT, nom_banco TEXT, num_cuenta TEXT)";
    private static final String SQL_CREATE_COD_RET      = "CREATE TABLE RETENCIONES (empresa TEXT, cod_retencion  TEXT, nom_retencion TEXT, " +
                                                          "prc_retencion REAL, sts_rxt TEXT, lim_ret REAL, fec_rxt TEXT, fex_rxt TEXT, por_rxt REAL)";
    private static final String SQL_CREATE_CLIENTES_REL = "CREATE TABLE CLIENTES_REL (cod_emp TEXT, cod_ref  TEXT, cod_vin TEXT, ord_vin INTEGER)";
    private static final String SQL_CREATE_CONFIG       = "CREATE TABLE CONFIGURACIONES (id INTEGER  PRIMARY KEY AUTOINCREMENT, " +
                                                          "correo TEXT, clave INTEGER)";
    private static final String SQL_CREATE_MAILS        = "CREATE TABLE MAILS (id INTEGER PRIMARY KEY AUTOINCREMENT, correo TEXT)";

    private static final String SQL_DROP_CLIENTES       = "DROP TABLE IF EXISTS CLIENTES";
    private static final String SQL_DROP_DOC_WS         = "DROP TABLE IF EXISTS DOC_WS";
    private static final String SQL_DROP_SECUENCIA      = "DROP TABLE IF EXISTS SECUENCIA";
    private static final String SQL_DROP_BANCO_CH       = "DROP TABLE IF EXISTS BANCOS_CH";
    private static final String SQL_DROP_BANCO_DP       = "DROP TABLE IF EXISTS BANCOS_DP";
    private static final String SQL_DROP_COD_RET        = "DROP TABLE IF EXISTS RETENCIONES";
    private static final String SQL_DROP_CLIENTES_REL   = "DROP TABLE IF EXISTS CLIENTES_REL";
    private static final String SQL_DROP_GEOLOCALIZA    = "DROP TABLE IF EXISTS GEOLOCALIZACION";
    private static final String SQL_DROP_CONFIG         = "DROP TABLE IF EXISTS CONFIGURACIONES";
    private static final String SQL_DROP_MAILS          = "DROP TABLE IF EXISTS MAILS";
    private static final String SQL_DROP_COBROS_RET     = "DROP TABLE IF EXISTS COBROS_RET";
    private static final String SQL_DROP_COBROS_DOC     = "DROP TABLE IF EXISTS COBROS_DOC";
    private static final String SQL_DROP_COBROS_PAGOS   = "DROP TABLE IF EXISTS COBROS_PAGOS";
    private static final String SQL_DROP_VENDEDOR       = "DROP TABLE IF EXISTS VENDEDOR";
    private static final String SQL_DROP_DOC_LOCAL      = "DROP TABLE IF EXISTS DOC_LOCAL";

    public DbHelper(Context ctx){
        super(ctx, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CLIENTES);
        db.execSQL(SQL_CREATE_DOC_WS);
        db.execSQL(SQL_CREATE_DOC_LOCAL);
        db.execSQL(SQL_CREATE_VENDEDOR);
        db.execSQL(SQL_CREATE_SECUENCIA);
        db.execSQL(SQL_CREATE_BANCO_CH);
        db.execSQL(SQL_CREATE_BANCO_DP);
        db.execSQL(SQL_CREATE_COBROS_PAGOS);
        db.execSQL(SQL_CREATE_COBROS_DOC);
        db.execSQL(SQL_CREATE_COD_RET);
        db.execSQL(SQL_CREATE_CLIENTES_REL);
        db.execSQL(SQL_CREATE_COBROS_RET);
        db.execSQL(SQL_CREATE_GEOLOCALIZA);
        db.execSQL(SQL_CREATE_CONFIG);
        db.execSQL(SQL_CREATE_MAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_CLIENTES);
        db.execSQL(SQL_CREATE_CLIENTES);
        db.execSQL(SQL_DROP_DOC_WS);
        db.execSQL(SQL_CREATE_DOC_WS);
        db.execSQL(SQL_DROP_SECUENCIA);
        db.execSQL(SQL_CREATE_SECUENCIA);
        db.execSQL(SQL_DROP_DOC_LOCAL);
        db.execSQL(SQL_CREATE_DOC_LOCAL);
        db.execSQL(SQL_DROP_COBROS_DOC);
        db.execSQL(SQL_CREATE_COBROS_DOC);
        db.execSQL(SQL_DROP_COBROS_PAGOS);
        db.execSQL(SQL_CREATE_COBROS_PAGOS);
        db.execSQL(SQL_DROP_COBROS_RET);
        db.execSQL(SQL_CREATE_COBROS_RET);
        db.execSQL(SQL_DROP_GEOLOCALIZA);
        db.execSQL(SQL_CREATE_GEOLOCALIZA);
        db.execSQL(SQL_DROP_CLIENTES_REL);
        db.execSQL(SQL_CREATE_CLIENTES_REL);
        db.execSQL(SQL_DROP_COD_RET);
        db.execSQL(SQL_CREATE_COD_RET);
        db.execSQL(SQL_DROP_CONFIG);
        db.execSQL(SQL_CREATE_CONFIG);
        db.execSQL(SQL_DROP_BANCO_CH);
        db.execSQL(SQL_CREATE_BANCO_CH);
        db.execSQL(SQL_DROP_BANCO_DP);
        db.execSQL(SQL_CREATE_BANCO_DP);
        db.execSQL(SQL_DROP_MAILS);
        db.execSQL(SQL_CREATE_MAILS);
        db.execSQL(SQL_DROP_VENDEDOR);
        db.execSQL(SQL_CREATE_VENDEDOR);
    }
}
