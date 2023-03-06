package com.twt.appcobranzas.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.adapter.NotasCreditoAdapter;
import com.twt.appcobranzas.model.Cobros_Doc;
import com.twt.appcobranzas.model.Cobros_Pagos;
import com.twt.appcobranzas.model.FacturaTemp;
import com.twt.appcobranzas.model.RowNotasCredito;
import com.twt.appcobranzas.model.Vendedor;
import com.twt.appcobranzas.utils.DbHelper;
import com.twt.appcobranzas.utils.Utils;

/**
 * Created by DEVECUA on 11/12/2015.
 */
@SuppressWarnings("deprecation")
public class RecibosNotasCreditoActivity extends AppCompatActivity  {

    String TAG="RecibosNotasCreditoActivity";
    ListView listView;
    Context context;
    public List<RowNotasCredito> itemsR;
    public List<Cobros_Pagos> pago, pagos;
    public List<Cobros_Doc> cobro, cobros, cob;
    public List<FacturaTemp> facturas, nc, ncredito;
    public List<FacturaTemp> listafactura;
    Vendedor lista;
    Button bt_buscar;
    TextView cod;
    EditText recibo;
    String idr, codi;
    String fch, numero, codigo1, auxi;
    double valor;
    int criterio;
    DbHelper dbSql =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notascredito);
        context = this;
        dbSql = new DbHelper(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        lista = bundle.getParcelable("lista");
        codi = getIntent().getExtras().getString("doc1");
        //widget
        listView  = (ListView) this.findViewById(R.id.lv_notas);
        bt_buscar = (Button) this.findViewById(R.id.btn_buscar);
        cod       = (TextView) this.findViewById(R.id.txt_cli);
        recibo    = (EditText) this.findViewById(R.id.et_recibo);

        registerForContextMenu(listView);
        onBackPressed();

        int cont = getCount();
        int cont1 = getCount1();
        int cont2 = getCount2();

        if(codi.equals("NC")){
            if(cont == 0){
                CharSequence text = "NO HAY DATOS DE NOTAS DE CREDITO PARA MOSTRAR ...!!";
                int duration = Toast.LENGTH_SHORT;

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast_error,
                        (ViewGroup) findViewById(R.id.toast_layout_root));

                TextView textToast = (TextView) layout.findViewById(R.id.text_toast);
                textToast.setText(text);

                Toast toast = new Toast(context);
                toast.setDuration(duration);
                toast.setView(layout);
                int offsetX = 5;
                int offsetY = 30;
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, offsetX, offsetY);
                toast.show();
                return;
            }else{
                loadNotas();
            }
        }else if(codi.equals("NG")){
            if(cont1 == 0){
                CharSequence text = "NO HAY DATOS DE NOTAS DE CREDITO INTERNAS PARA MOSTRAR ...!!";
                int duration = Toast.LENGTH_SHORT;

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast_error,
                        (ViewGroup) findViewById(R.id.toast_layout_root));

                TextView textToast = (TextView) layout.findViewById(R.id.text_toast);
                textToast.setText(text);

                Toast toast = new Toast(context);
                toast.setDuration(duration);
                toast.setView(layout);
                int offsetX = 5;
                int offsetY = 30;
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, offsetX, offsetY);
                toast.show();
                return;
            }else{
                loadNotas1();
            }
        }else if(codi.equals("NA")){
            if(cont2 == 0){
                CharSequence text = "NO HAY DATOS DE NOTAS DE CREDITO INTERNAS APP PARA MOSTRAR ...!!";
                int duration = Toast.LENGTH_SHORT;

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast_error,
                        (ViewGroup) findViewById(R.id.toast_layout_root));

                TextView textToast = (TextView) layout.findViewById(R.id.text_toast);
                textToast.setText(text);

                Toast toast = new Toast(context);
                toast.setDuration(duration);
                toast.setView(layout);
                int offsetX = 5;
                int offsetY = 30;
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, offsetX, offsetY);
                toast.show();
                return;
            }else{
                loadNotas2();
            }
        }

        bt_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rec = recibo.getText().toString();
                if(rec.equals("")){
                    if (codi.equals("NC")) {
                        loadNotas();
                    } else if (codi.equals("NG")) {
                        loadNotas1();
                    } else if (codi.equals("NA")) {
                        loadNotas2();
                    }
                }else {
                    if (codi.equals("NC")) {
                        busquedaNotas();
                    } else if (codi.equals("NG")) {
                        busquedaNotas1();
                    } else if (codi.equals("NA")) {
                        busquedaNotas2();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dbSql==null)
            dbSql = new DbHelper(context);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

    }

    void loadNotas(){
        SQLiteDatabase db = null;
        Cursor q = null;
        String SQL = "";
        try {
            db = dbSql.getReadableDatabase();
            /*SQL ="SELECT DISTINCT cd.id,DATE(cd.fch_emision),cd.num_doc,cd.id_recibo,cd.valor FROM COBROS_DOC cd " +
                        "WHERE cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"' " +
                        "AND cd.tipo_cobro='NC' AND cd.estado!=1 " +
                        "ORDER BY cd.num_doc";*/
            SQL = "SELECT cd.id_recibo,DATE(cd.fch_emision),cd.num_doc,\n" +
                    "                (SELECT DISTINCT SUM(valor) FROM COBROS_DOC\n" +
                    "                 WHERE cod_emp='"+lista.getCod_emp()+"' AND tipo_cobro!='RC'\n" +
                    "                 AND cod_emp= cd.cod_emp AND id_recibo=cd.id_recibo AND cod_vend=cd.cod_vend)\n" +
                    "FROM COBROS_DOC cd\n" +
                    "WHERE cd.cod_emp='"+lista.getCod_emp()+"' AND cd.tipo_cobro='NC' \n" +
                    "AND cd.cod_vend='"+lista.getCod_vend()+"' AND cd.estado!=1 \n" +
                    "GROUP BY 1,2,3,4\n"+
                    "ORDER BY 1";
            itemsR = new ArrayList();
            q = db.rawQuery(SQL,null);
            System.out.println("suma :: "+q.getColumnCount());
            if (q.moveToFirst()){
                do {
                    String cod_emp = "";
                    String cod_mov = "NC";
                    String cod_pto  = "";
                    String cod_ref = "";
                    String cod_ven = "";
                    int dias = 0;
                    String fch_emi = q.getString(1);
                    Date emi = Utils.ConvertirShortStringToDate(fch_emi);
                    Date ven = new Date();
                    String num_mov = q.getString(2);
                    String num_rel = q.getString(0);
                    double sdo_mov = .0;
                    double val_mov = Utils.Redondear(q.getDouble(3),2);
                    double iva = .0;
                    double base = .0;
                    String est = "";
                    String cdb = "";
                    String ncta = "";
                    boolean sel = false;
                    double ab = .0;
                    itemsR.add(new RowNotasCredito(cod_emp,cod_mov,cod_pto,cod_ref,cod_ven,dias,emi,ven,num_mov,num_rel,sdo_mov,val_mov,iva,base,est,cdb,ncta,sel,ab));
                }while (q.moveToNext());
            }
            listView.setAdapter(new NotasCreditoAdapter(context, itemsR));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
    }

    void loadNotas1(){
        SQLiteDatabase db = null;
        Cursor q = null;
        String SQL = "";
        try {
            db = dbSql.getReadableDatabase();
            /*SQL ="SELECT DISTINCT cd.id,DATE(cd.fch_emision),cd.num_doc,cd.id_recibo,cd.valor FROM COBROS_DOC cd " +
                        "WHERE cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"' " +
                        "AND cd.tipo_cobro='NG' AND cd.estado!=1 " +
                        "ORDER BY cd.num_doc";*/
            SQL = "SELECT cd.id_recibo,DATE(cd.fch_emision),cd.num_doc,\n" +
                    "                (SELECT DISTINCT SUM(valor) FROM COBROS_DOC\n" +
                    "                 WHERE cod_emp='"+lista.getCod_emp()+"' AND tipo_cobro!='RC'\n" +
                    "                 AND cod_emp= cd.cod_emp AND id_recibo=cd.id_recibo AND cod_vend=cd.cod_vend)\n" +
                    "FROM COBROS_DOC cd\n" +
                    "WHERE cd.cod_emp='"+lista.getCod_emp()+"' AND cd.tipo_cobro='NG' \n" +
                    "AND cd.cod_vend='"+lista.getCod_vend()+"' AND cd.estado!=1 \n" +
                    "GROUP BY 1,2,3,4\n"+
                    "ORDER BY 1";
            itemsR = new ArrayList();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    String cod_emp = "";
                    String cod_mov = "NG";
                    String cod_pto  = "";
                    String cod_ref = "";
                    String cod_ven = "";
                    int dias = 0;
                    String fch_emi = q.getString(1);
                    Date emi = Utils.ConvertirShortStringToDate(fch_emi);
                    Date ven = new Date();
                    String num_mov = q.getString(2);
                    String num_rel = q.getString(0);
                    double sdo_mov = .0;
                    double val_mov = Utils.Redondear(q.getDouble(3),2);
                    double iva = .0;
                    double base = .0;
                    String est = "";
                    String cdb = "";
                    String ncta = "";
                    boolean sel = false;
                    double ab = .0;
                    itemsR.add(new RowNotasCredito(cod_emp,cod_mov,cod_pto,cod_ref,cod_ven,dias,emi,ven,num_mov,num_rel,sdo_mov,val_mov,iva,base,est,cdb,ncta,sel,ab));
                }while (q.moveToNext());
            }
            listView.setAdapter(new NotasCreditoAdapter(context, itemsR));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
    }

    void loadNotas2(){
        SQLiteDatabase db = null;
        Cursor q = null;
        String SQL = "";
        try {
            db = dbSql.getReadableDatabase();
            /*String SQL ="SELECT DISTINCT DATE(cd.fch_emision),cd.num_doc,cd.id_recibo,(SELECT SUM(valor) " +
                        "FROM COBROS_PAGOS WHERE cd.tipo_cobro='NA' AND id_recibo=cd.id_recibo) FROM COBROS_DOC cd " +
                        "WHERE cd.cod_vend='"+lista.getCod_vend()+"' AND (cd.tipo_cobro='NA') ORDER BY cd.num_doc";*/
            SQL = "SELECT cd.id_recibo,DATE(cd.fch_emision),cd.num_doc,\n" +
                    "                (SELECT DISTINCT SUM(valor) FROM COBROS_DOC\n" +
                    "                 WHERE cod_emp='"+lista.getCod_emp()+"' AND tipo_cobro!='RC'\n" +
                    "                 AND cod_emp= cd.cod_emp AND id_recibo=cd.id_recibo AND cod_vend=cd.cod_vend)\n" +
                    "FROM COBROS_DOC cd\n" +
                    "WHERE cd.cod_emp='"+lista.getCod_emp()+"' AND cd.tipo_cobro='NA' \n" +
                    "AND cd.cod_vend='"+lista.getCod_vend()+"' AND cd.estado!=1 \n" +
                    "GROUP BY 1,2,3,4\n"+
                    "ORDER BY 1";

            /*SQL ="SELECT DISTINCT cd.id,DATE(cd.fch_emision),cd.num_doc,cd.id_recibo," +
                        "       (SELECT SUM(valor) FROM COBROS_PAGOS WHERE cod_emp='"+lista.getCod_emp()+"' " +
                        "        AND tipo_cobro='NA' AND cod_emp=cd.cod_emp AND id_recibo=cd.id_recibo) " +
                        "FROM COBROS_DOC cd " +
                        "WHERE cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"' " +
                        "AND cd.tipo_cobro='NA' AND cd.estado!=1 " +
                        "ORDER BY cd.num_doc";*/
            itemsR = new ArrayList();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    String cod_emp = "";
                    String cod_mov = "NA";
                    String cod_pto  = "";
                    String cod_ref = "";
                    String cod_ven = "";
                    int dias = 0;
                    String fch_emi = q.getString(1);
                    Date emi = Utils.ConvertirShortStringToDate(fch_emi);
                    Date ven = new Date();
                    String num_mov = q.getString(2);
                    String num_rel = q.getString(0);
                    double sdo_mov = .0;
                    double val_mov = Utils.Redondear(q.getDouble(3),2);
                    double iva = .0;
                    double base = .0;
                    String est = "";
                    String cdb = "";
                    String ncta = "";
                    boolean sel = false;
                    double ab = .0;
                    itemsR.add(new RowNotasCredito(cod_emp,cod_mov,cod_pto,cod_ref,cod_ven,dias,emi,ven,num_mov,num_rel,sdo_mov,val_mov,iva,base,est,cdb,ncta,sel,ab));
                }while (q.moveToNext());
            }
            listView.setAdapter(new NotasCreditoAdapter(context, itemsR));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
    }

    void busquedaNotas() {
        SQLiteDatabase db = null;
        Cursor q = null;
        String SQL = "";
        String criterio = recibo.getText().toString();
        try {
            db = dbSql.getReadableDatabase();
            /*SQL ="SELECT * FROM COBROS_DOC " +
                        "WHERE num_doc="+criterio+" AND cod_emp='"+lista.getCod_emp()+"' AND cod_vend='"+lista.getCod_vend()+"' " +
                        "AND tipo_cobro='NC' AND estado!=1";*/
            SQL = "SELECT cd.id_recibo,DATE(cd.fch_emision),cd.num_doc,\n" +
                    "                (SELECT DISTINCT SUM(valor) FROM COBROS_DOC\n" +
                    "                 WHERE cod_emp='"+lista.getCod_emp()+"' AND tipo_cobro!='RC'\n" +
                    "                 AND cod_emp= cd.cod_emp AND id_recibo=cd.id_recibo AND cod_vend=cd.cod_vend)\n" +
                    "FROM COBROS_DOC cd\n" +
                    "WHERE cd.cod_emp='"+lista.getCod_emp()+"' AND cd.tipo_cobro='NC' AND cd.num_mov="+criterio+"\n" +
                    "AND cd.cod_vend='"+lista.getCod_vend()+"' AND cd.estado!=1 \n" +
                    "GROUP BY 1,2,3,4\n"+
                    "ORDER BY 1";
            itemsR = new ArrayList();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    String cod_emp = "";
                    String cod_mov = "NC";
                    String cod_pto  = "";
                    String cod_ref = "";
                    String cod_ven = "";
                    String cod_vin = "";
                    int dias = 0;
                    String fch_emi = q.getString(1);
                    Date emi = Utils.ConvertirShortStringToDate(fch_emi);
                    Date ven = new Date();
                    String num_mov = q.getString(2);
                    String num_rel = q.getString(0);
                    double sdo_mov = .0;
                    double val_mov = q.getDouble(3);
                    double iva = .0;
                    double base = .0;
                    String est = "";
                    String cdb = "";
                    String ncta = "";
                    boolean sel = false;
                    double ab = .0;
                    itemsR.add(new RowNotasCredito(cod_emp,cod_mov,cod_pto,cod_ref,cod_ven,dias,emi,ven,num_mov,num_rel,sdo_mov,val_mov,iva,base,est,cdb,ncta,sel,ab));
                }while (q.moveToNext());
            }
            listView.setAdapter(new NotasCreditoAdapter(context, itemsR));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
    }

    void busquedaNotas1() {
        SQLiteDatabase db = null;
        Cursor q = null;
        String SQL = "";
        String criterio = recibo.getText().toString();
        try {
            db = dbSql.getReadableDatabase();
            /*SQL ="SELECT * FROM COBROS_DOC " +
                        "WHERE num_doc="+criterio+" AND cod_emp='"+lista.getCod_emp()+"' AND cod_vend='"+lista.getCod_vend()
                       +"' AND tipo_cobro='NG' AND estado!=1";*/
            SQL = "SELECT cd.id_recibo,DATE(cd.fch_emision),cd.num_doc,\n" +
                    "                (SELECT DISTINCT SUM(valor) FROM COBROS_DOC\n" +
                    "                 WHERE cod_emp='"+lista.getCod_emp()+"' AND tipo_cobro!='RC'\n" +
                    "                 AND cod_emp= cd.cod_emp AND id_recibo=cd.id_recibo AND cod_vend=cd.cod_vend)\n" +
                    "FROM COBROS_DOC cd\n" +
                    "WHERE cd.cod_emp='"+lista.getCod_emp()+"' AND cd.tipo_cobro='NG' AND cd.num_doc="+criterio+"\n" +
                    "AND cd.cod_vend='"+lista.getCod_vend()+"' AND cd.estado!=1 \n" +
                    "GROUP BY 1,2,3,4\n"+
                    "ORDER BY 1";
            itemsR = new ArrayList();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    String cod_emp = "";
                    String cod_mov = "NG";
                    String cod_pto  = "";
                    String cod_ref = "";
                    String cod_ven = "";
                    String cod_vin = "";
                    int dias = 0;
                    String fch_emi = q.getString(1);
                    Date emi = Utils.ConvertirShortStringToDate(fch_emi);
                    Date ven = new Date();
                    String num_mov = q.getString(2);
                    String num_rel = q.getString(0);
                    double sdo_mov = .0;
                    double val_mov = q.getDouble(3);
                    double iva = .0;
                    double base = .0;
                    String est = "";
                    String cdb = "";
                    String ncta = "";
                    boolean sel = false;
                    double ab = .0;
                    itemsR.add(new RowNotasCredito(cod_emp,cod_mov,cod_pto,cod_ref,cod_ven,dias,emi,ven,num_mov,num_rel,sdo_mov,val_mov,iva,base,est,cdb,ncta,sel,ab));
                }while (q.moveToNext());
            }
            listView.setAdapter(new NotasCreditoAdapter(context, itemsR));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
    }

    void busquedaNotas2() {
        SQLiteDatabase db = null;
        Cursor q = null;
        String SQL = "";
        String criterio = recibo.getText().toString();
        try {
            db = dbSql.getReadableDatabase();
            /*SQL ="SELECT * FROM DOC_LOCAL " +
                        "WHERE num_mov="+criterio+" AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+
                        "' AND cod_mov='NA' AND val_mov>0";*/
            SQL = "SELECT cd.id_recibo,DATE(cd.fch_emision),cd.num_doc,\n" +
                    "                (SELECT DISTINCT SUM(valor) FROM COBROS_DOC\n" +
                    "                 WHERE cod_emp='"+lista.getCod_emp()+"' AND tipo_cobro!='RC'\n" +
                    "                 AND cod_emp= cd.cod_emp AND id_recibo=cd.id_recibo AND cod_vend=cd.cod_vend)\n" +
                    "FROM COBROS_DOC cd\n" +
                    "WHERE cd.cod_emp='"+lista.getCod_emp()+"' AND cd.tipo_cobro='NA' AND cd.num_doc="+criterio+"\n" +
                    "AND cd.cod_vend='"+lista.getCod_vend()+"' AND cd.estado!=1 \n" +
                    "GROUP BY 1,2,3,4\n"+
                    "ORDER BY 1";
            itemsR = new ArrayList();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    String cod_emp = "";
                    String cod_mov = "NA";
                    String cod_pto  = "";
                    String cod_ref = "";
                    String cod_ven = "";
                    int dias = 0;
                    String fch_emi = q.getString(1);
                    Date emi = Utils.ConvertirShortStringToDate(fch_emi);
                    Date ven = new Date();
                    String num_mov = q.getString(2);
                    String num_rel = q.getString(0);
                    double sdo_mov = .0;
                    double val_mov = Utils.Redondear(q.getDouble(3),2);
                    double iva = .0;
                    double base = .0;
                    String est = "";
                    String cdb = "";
                    String ncta = "";
                    boolean sel = false;
                    double ab = .0;
                    itemsR.add(new RowNotasCredito(cod_emp,cod_mov,cod_pto,cod_ref,cod_ven,dias,emi,ven,num_mov,num_rel,sdo_mov,val_mov,iva,base,est,cdb,ncta,sel,ab));
                }while (q.moveToNext());
            }
            listView.setAdapter(new NotasCreditoAdapter(context, itemsR));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
    }

    public int getCount(){
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT COUNT(*) FROM COBROS_DOC " +
                        "WHERE cod_emp='"+lista.getCod_emp()+"' AND cod_vend='"+lista.getCod_vend()+"' AND tipo_cobro='NC' AND estado!=1";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
                    c = q.getInt(0);
                } while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return c;
    }

    public int getCount1(){
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT COUNT(*) FROM COBROS_DOC " +
                        "WHERE cod_emp='"+lista.getCod_emp()+"' AND cod_vend='"+lista.getCod_vend()+"' AND tipo_cobro='NG' AND estado!=1";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
                    c = q.getInt(0);
                } while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return c;
    }

    public int getCount2(){
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT COUNT(*) FROM DOC_LOCAL " +
                        "WHERE cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"' AND cod_mov='NA' AND sts_mov='0'";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
                    c = q.getInt(0);
                } while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return c;
    }

    public List<Cobros_Pagos> load_cobrospago(int recibo, String cod){
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT DISTINCT cp.* FROM COBROS_PAGOS cp WHERE cp.id_recibo="+recibo+
                        " AND cp.cod_emp='"+lista.getCod_emp()+"' AND cp.cod_vend='"+lista.getCod_vend()+"' and cp.fpago='"+cod+"'";
            pago = new ArrayList<Cobros_Pagos>();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    int id = q.getInt(0);
                    String emp = q.getString(1);
                    int idrecibo = q.getInt(2);
                    String fpago = q.getString(3);
                    String banco  = q.getString(4);
                    String cuenta = q.getString(5);
                    String cheque = q.getString(6);
                    String fch_pago = q.getString(7);
                    double valor = q.getDouble(8);
                    String cod_ref = q.getString(9);
                    String cod_vend = q.getString(10);
                    String cod_vin = q.getString(11);
                    String vendedor = q.getString(12);
                    String fch_reg = q.getString(13);
                    Date reg = Utils.ConvertirShortStringToDate(fch_reg);
                    String girador = q.getString(14);
                    String cod_rec = q.getString(15);
                    String estado = q.getString(16);
                    pago.add(new Cobros_Pagos(idrecibo, emp, fpago, banco, cuenta, cheque, fch_pago, valor, cod_ref, cod_vend, cod_vin,
                                              vendedor, reg, girador, cod_rec, estado));
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return pago;
    }

    public List<Cobros_Doc> load_cobrosdoc(int rel, int recibo){
        SQLiteDatabase db = null;
        Cursor q = null;
        cobros = new ArrayList<Cobros_Doc>();
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM COBROS_DOC " +
                        "WHERE id_recibo="+rel+" AND num_doc="+recibo+" " +
                        "AND cod_emp='"+lista.getCod_emp()+"' AND cod_vend='"+lista.getCod_vend()+"'";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    Cobros_Doc cd = new Cobros_Doc();
                    cd.setCod_emp(q.getString(1));
                    cd.setId_recibo(q.getInt(2));
                    cd.setFch_emision(q.getString(3));
                    cd.setCod_cli(q.getString(4));
                    cd.setNom_cli(q.getString(5));
                    cd.setCod_vend(q.getString(6));
                    cd.setCod_vin(q.getString(7));
                    cd.setVendedor2(q.getString(8));
                    cd.setCod_mov(q.getString(9));
                    cd.setNum_mov(q.getString(10));
                    cd.setFch_emi(q.getString(11));
                    cd.setFch_ven(q.getString(12));
                    cd.setTipo_cobro(q.getString(13));
                    cd.setNum_doc(q.getString(14));
                    cd.setCod_bco(q.getString(15));
                    cd.setNum_cuenta(q.getString(16));
                    cd.setValor(q.getDouble(17));
                    cd.setPto_vta(q.getString(18));
                    cd.setEstado(q.getInt(19));
                    cobros.add(cd);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return cobros;
    }

    public void updateFacturas(List<FacturaTemp> fa){
        List<FacturaTemp> items = fa;
        double saldo = 0.00;
        SQLiteDatabase db = null;
        try {
            String SQL = "";
            for (FacturaTemp row: items) {
                if(row.getSdo_mov()<0)
                    saldo = row.getSdo_mov()-row.getValor();
                else
                    saldo = row.getSdo_mov()+row.getValor();
                double s = Utils.Redondear(saldo,2);
                if(lista.getCod_vend().equals("V017") || lista.getCod_vend().equals("V012") || lista.getCod_vend().equals("V001"))
                    SQL = "UPDATE DOC_WS SET sdo_mov="+ s +" WHERE num_mov='" + row.getNum_mov() + "' " +
                          "AND cod_emp='"+lista.getCod_emp()+"'";
                else if(lista.getCod_vend().equals("V999"))
                    SQL = "UPDATE DOC_WS SET sdo_mov="+ s +" WHERE num_mov='" + row.getNum_mov() + "' " +
                          "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+row.getCod_ven()+"'";
                else
                    SQL = "UPDATE DOC_WS SET sdo_mov="+ s +" WHERE num_mov='" + row.getNum_mov() + "' " +
                            "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"'";

                Log.e("updateF",SQL);
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Exception Error de actualizacion.. " + e.getMessage());
        }
        /*finally {
            db.close();
        }*/
    }

    public void updateNotas(List<FacturaTemp> fa){
        List<FacturaTemp> items = fa;
        FacturaTemp fat = new FacturaTemp();
        String aux = "";
        double saldo = 0.00;
        double nuevosaldo = 0.00;
        SQLiteDatabase db = null;
        try {
            String SQL = "";
            for (FacturaTemp row: items) {
                if(aux.equals(row.getNum_mov())) {
                    fat = load_notcr(row.getNum_mov());
                    if (fat.getSdo_mov() < 0)
                        saldo = fat.getSdo_mov() - fat.getValor();
                    else
                        saldo = fat.getSdo_mov() + fat.getValor();
                    double s = Utils.Redondear(saldo, 2);
                    if (s > 0)
                        nuevosaldo = (-1) * (s);
                    else
                        nuevosaldo = s;

                    if(lista.getCod_vend().equals("V017") || lista.getCod_vend().equals("V012") || lista.getCod_vend().equals("V001"))
                        SQL = "UPDATE DOC_WS SET sdo_mov=" + nuevosaldo + " WHERE num_mov='" + row.getNum_mov() + "' " +
                              "AND cod_emp='"+lista.getCod_emp()+"'";
                    else if(lista.getCod_vend().equals("V999"))
                        SQL = "UPDATE DOC_WS SET sdo_mov=" + nuevosaldo + " WHERE num_mov='" + row.getNum_mov() + "' " +
                              "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='" + row.getCod_ven() + "'";
                    else
                        SQL = "UPDATE DOC_WS SET sdo_mov=" + nuevosaldo + " WHERE num_mov='" + row.getNum_mov() + "' " +
                                "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='" + lista.getCod_vend() + "'";

                    Log.e("updateNotas", SQL);
                    db = dbSql.getWritableDatabase();
                    db.execSQL(SQL);
                }else{
                    if(row.getSdo_mov()<0)
                        saldo = row.getSdo_mov()-row.getValor();
                    else
                        saldo = row.getSdo_mov()+row.getValor();
                    double s = Utils.Redondear(saldo,2);
                    if(s>0)
                        nuevosaldo = (-1)*(s);
                    else
                        nuevosaldo = s;

                    if(lista.getCod_vend().equals("V017") || lista.getCod_vend().equals("V012") || lista.getCod_vend().equals("V001"))
                        SQL = "UPDATE DOC_WS SET sdo_mov=" + nuevosaldo + " WHERE num_mov='" + row.getNum_mov() + "' " +
                              "AND cod_emp='"+lista.getCod_emp()+"'";
                    else if(lista.getCod_vend().equals("V999"))
                        SQL = "UPDATE DOC_WS SET sdo_mov=" + nuevosaldo + " WHERE num_mov='" + row.getNum_mov() + "' " +
                              "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='" + row.getCod_ven() + "'";
                    else
                        SQL = "UPDATE DOC_WS SET sdo_mov=" + nuevosaldo + " WHERE num_mov='" + row.getNum_mov() + "' " +
                                "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='" + lista.getCod_vend() + "'";

                    Log.e("updateNotas", SQL);
                    db = dbSql.getWritableDatabase();
                    db.execSQL(SQL);
                }
                aux = row.getNum_mov();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Exception Error de actualizacion nota credito.. " + e.getMessage());
        }finally {
            db.close();
        }
    }

    public void updateNotas2(List<FacturaTemp> fa){
        List<FacturaTemp> items = fa;
        FacturaTemp fat = new FacturaTemp();
        String aux = "";
        double saldo = 0.00;
        double nuevosaldo = 0.00;
        SQLiteDatabase db = null;
        try {
            String SQL = "";
            for (FacturaTemp row: items) {
                if(aux.equals(row.getNum_mov())){
                    fat = load_napp(row.getNum_mov());
                    if(fat.getSdo_mov()<0)
                        saldo = fat.getSdo_mov()-fat.getValor();
                    else
                        saldo = fat.getSdo_mov()+fat.getValor();
                    double s = Utils.Redondear(saldo,2);
                    if(s>0)
                        nuevosaldo = (-1)*(s);
                    else
                        nuevosaldo = s;
                    if(lista.getCod_vend().equals("V017") || lista.getCod_vend().equals("V012") || lista.getCod_vend().equals("V001"))
                        SQL = "UPDATE DOC_LOCAL SET sdo_mov="+ nuevosaldo +" WHERE num_mov='" + row.getNum_mov() + "' " +
                              "AND cod_emp='"+lista.getCod_emp()+"'";
                    else if(lista.getCod_vend().equals("V999"))
                        SQL = "UPDATE DOC_LOCAL SET sdo_mov="+ nuevosaldo +" WHERE num_mov='" + row.getNum_mov() + "' " +
                              "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+row.getCod_ven()+"'";
                    else
                    SQL = "UPDATE DOC_LOCAL SET sdo_mov="+ nuevosaldo +" WHERE num_mov='" + row.getNum_mov() + "' " +
                            "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"'";


                    Log.e("updateNotas",SQL);
                    db = dbSql.getWritableDatabase();
                    db.execSQL(SQL);
                }else{
                    if(row.getSdo_mov()<0)
                        saldo = row.getSdo_mov()-row.getValor();
                    else
                        saldo = row.getSdo_mov()+row.getValor();
                    double s = Utils.Redondear(saldo,2);
                    if(s>0)
                        nuevosaldo = (-1)*(s);
                    else
                        nuevosaldo = s;

                    if(lista.getCod_vend().equals("V017") || lista.getCod_vend().equals("V012") || lista.getCod_vend().equals("V001"))
                        SQL = "UPDATE DOC_LOCAL SET sdo_mov="+ nuevosaldo +" WHERE num_mov='" + row.getNum_mov() + "'" +
                              "AND cod_emp='"+lista.getCod_emp()+"'";
                    else if(lista.getCod_vend().equals("V999"))
                        SQL = "UPDATE DOC_LOCAL SET sdo_mov="+ nuevosaldo +" WHERE num_mov='" + row.getNum_mov() + "' " +
                              "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+row.getCod_ven()+"'";
                    else
                    SQL = "UPDATE DOC_LOCAL SET sdo_mov="+ nuevosaldo +" WHERE num_mov='" + row.getNum_mov() + "' " +
                            "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"'";

                    Log.e("updateNotas",SQL);
                    db = dbSql.getWritableDatabase();
                    db.execSQL(SQL);
                }
                aux = row.getNum_mov();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Exception Error de actualizacion nota credito.. " + e.getMessage());
        }finally {
            db.close();
        }
    }

    public void deleteReciboPagos(int recibo){
        SQLiteDatabase db = null;
        try {
            String SQL ="DELETE FROM COBROS_PAGOS " +
                        "WHERE id_recibo="+recibo+" AND cod_emp='"+lista.getCod_emp()+
                        "' AND cod_vend='"+lista.getCod_vend()+"' AND cod_rec='"+codi+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void deleteNotasCredito(int recibo){
        SQLiteDatabase db = null;
        try {
            String SQL ="DELETE FROM DOC_LOCAL " +
                        "WHERE num_rel="+recibo+" AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void deleteReciboCobros(int recibo){
        SQLiteDatabase db = null;
        try {
            String SQL ="DELETE FROM COBROS_DOC " +
                        "WHERE id_recibo="+recibo+" AND cod_emp='"+lista.getCod_emp()+
                        "' AND cod_vend='"+lista.getCod_vend()+"' AND tipo_cobro='"+codi+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public List<Cobros_Doc> load_cobrosdoc1(int recibo){
        SQLiteDatabase db = null;
        Cursor q = null;
        cobros = new ArrayList<Cobros_Doc>();
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM COBROS_DOC " +
                        "WHERE id_recibo="+recibo+" AND cod_emp='"+lista.getCod_emp()+"' AND cod_vend='"+lista.getCod_vend()+"' " +
                        "AND (tipo_cobro='NC' OR tipo_cobro='NG')";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    Cobros_Doc cd = new Cobros_Doc();
                    cd.setCod_emp(q.getString(1));
                    cd.setId_recibo(q.getInt(2));
                    cd.setFch_emision(q.getString(3));
                    cd.setCod_cli(q.getString(4));
                    cd.setNom_cli(q.getString(5));
                    cd.setCod_vend(q.getString(6));
                    cd.setCod_vin(q.getString(7));
                    cd.setVendedor2(q.getString(8));
                    cd.setCod_mov(q.getString(9));
                    cd.setNum_mov(q.getString(10));
                    cd.setFch_emi(q.getString(11));
                    cd.setFch_ven(q.getString(12));
                    cd.setTipo_cobro(q.getString(13));
                    cd.setNum_doc(q.getString(14));
                    cd.setCod_bco(q.getString(15));
                    cd.setNum_cuenta(q.getString(16));
                    cd.setValor(q.getDouble(17));
                    cd.setPto_vta(q.getString(18));
                    cd.setEstado(q.getInt(19));
                    cobros.add(cd);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return cobros;
    }

    public List<Cobros_Doc> load_cobrosdoc2(int recibo){
        SQLiteDatabase db = null;
        Cursor q = null;
        cobros = new ArrayList<Cobros_Doc>();
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM COBROS_DOC " +
                        "WHERE id_recibo="+recibo+" AND cod_emp='"+lista.getCod_emp()+"' AND cod_vend='"+lista.getCod_vend()+"' " +
                        "AND tipo_cobro='NA'";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    Cobros_Doc cd = new Cobros_Doc();
                    cd.setCod_emp(q.getString(1));
                    cd.setId_recibo(q.getInt(2));
                    cd.setFch_emision(q.getString(3));
                    cd.setCod_cli(q.getString(4));
                    cd.setNom_cli(q.getString(5));
                    cd.setCod_vend(q.getString(6));
                    cd.setCod_vin(q.getString(7));
                    cd.setVendedor2(q.getString(8));
                    cd.setCod_mov(q.getString(9));
                    cd.setNum_mov(q.getString(10));
                    cd.setFch_emi(q.getString(11));
                    cd.setFch_ven(q.getString(12));
                    cd.setTipo_cobro(q.getString(13));
                    cd.setNum_doc(q.getString(14));
                    cd.setCod_bco(q.getString(15));
                    cd.setNum_cuenta(q.getString(16));
                    cd.setValor(q.getDouble(17));
                    cd.setPto_vta(q.getString(18));
                    cd.setEstado(q.getInt(19));
                    cobros.add(cd);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return cobros;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_salir, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.mVolver:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("item", lista);
                intent.putExtras(bundle);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        int mPosition;
        if(v.getId() == R.id.lv_notas)
        {
            mPosition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
            idr    = this.itemsR.get(mPosition).getNum_mov();
            codigo1= this.itemsR.get(mPosition).getCod_ref();
            fch    = this.itemsR.get(mPosition).getFec_emi().toString();
            numero = this.itemsR.get(mPosition).getNum_rel();
            valor  = this.itemsR.get(mPosition).getVal_mov();
            menu.setHeaderTitle(idr);
            this.getMenuInflater().inflate(R.menu.menu_list1_2, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public  boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.mDetalle:
                if(codi.equals("NC") || codi.equals("NG")){
                    pagos = load_cobrospago(Integer.parseInt(numero),codi);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecibosNotasCreditoActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View convertView = (View) inflater.inflate(R.layout.customnotas, null);
                    alertDialog.setView(convertView);
                    cobro = load_cobrosdoc1(Integer.parseInt(numero));
                    StringBuilder aux = resumenFactura();
                    alertDialog.setTitle("Detalles Pago NC # "+idr+"\n");
                    ListView lv = (ListView) convertView.findViewById(R.id.listViewNot);
                    TextView res = (TextView) convertView.findViewById(R.id.res_not);
                    res.setText("Facturas Seleccionadas en este Recibo "+aux.toString().replaceAll("\\n", "-"));
                    ArrayAdapter<Cobros_Pagos> adapter = new ArrayAdapter<Cobros_Pagos>(this,android.R.layout.simple_list_item_1,pago);
                    lv.setAdapter(adapter);
                    alertDialog.show();
                }else if(codi.equals("NA")){
                    pagos = load_cobrospago(Integer.parseInt(numero), codi);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecibosNotasCreditoActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View convertView = (View) inflater.inflate(R.layout.customnotas, null);
                    alertDialog.setView(convertView);
                    cobro = load_cobrosdoc2(Integer.parseInt(numero));
                    StringBuilder aux = resumenFactura();
                    alertDialog.setTitle("Detalles Pago NC # "+idr+"\n");
                    ListView lv = (ListView) convertView.findViewById(R.id.listViewNot);
                    TextView res = (TextView) convertView.findViewById(R.id.res_not);
                    res.setText("Facturas Seleccionadas en este Recibo "+aux.toString().replaceAll("\\n", "-"));
                    ArrayAdapter<Cobros_Pagos> adapter = new ArrayAdapter<Cobros_Pagos>(this,android.R.layout.simple_list_item_1,pago);
                    lv.setAdapter(adapter);
                    alertDialog.show();
                }
                return true;
            case R.id.mEliminar:
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(this);
                alertDialog1.setTitle("Eliminar Datos de la NC");
                alertDialog1.setMessage("Esta seguro de eliminar todos los datos de la NC aplicada "+idr+"..?");
                alertDialog1.setCancelable(false);
                alertDialog1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        if(codi.equals("NC") || codi.equals("NG")) {
                            cob = load_cobrosdoc1(Integer.parseInt(numero));
                            listafactura = load_facturas(cob);
                            ncredito = load_notas(idr);
                        }else if(codi.equals("NA")){
                            cob = load_cobrosdoc2(Integer.parseInt(numero));
                            listafactura = load_facturas(cob);
                            ncredito = load_notas2(idr);
                        }
                        updateFacturas(listafactura);
                        if(codi.equals("NC") || codi.equals("NG")) {
                            updateNotas(ncredito);
                        }else if(codi.equals("NA")){
                            updateNotas2(ncredito);
                        }
                        deleteReciboCobros(Integer.parseInt(numero));
                        deleteReciboPagos(Integer.parseInt(numero));
                        aceptar();
                    }
                });
                alertDialog1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        cancelar();
                    }
                });
                alertDialog1.show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public StringBuilder resumenFactura(){
        StringBuilder message = new StringBuilder();
        for(Cobros_Doc row: cobro){
                message.append(row.getNum_mov());
                message.append("\n");
        }
        return message;
    }

    public void cancelar() {
        finish();
        CharSequence text = "HA CANCELADO LA OPERACION ...!!";
        int duration = Toast.LENGTH_LONG;

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_ok,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView textToast = (TextView) layout.findViewById(R.id.text_toast);
        textToast.setText(text);

        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(layout);
        int offsetX = 20;
        int offsetY = 25;
        toast.setGravity(Gravity.CENTER_VERTICAL, offsetX, offsetY);
        toast.show();
        return;
    }

    public void aceptar() {
        CharSequence text = "DATOS DE LA NC ELIMINADOS ...!!";
        int duration = Toast.LENGTH_LONG;

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_ok,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView textToast = (TextView) layout.findViewById(R.id.text_toast);
        textToast.setText(text);

        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(layout);
        int offsetX = 20;
        int offsetY = 25;
        toast.setGravity(Gravity.CENTER_VERTICAL, offsetX, offsetY);
        toast.show();
        if (codi.equals("NC")) {
            loadNotas();
        } else if (codi.equals("NG")) {
            loadNotas1();
        } else if (codi.equals("NA")) {
            loadNotas2();
        }
    }

    public List<FacturaTemp> load_notas(String recibo){
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL = "";
            if(lista.getCod_vend().equals("V001") || lista.getCod_vend().equals("V017") || lista.getCod_vend().equals("V012"))
                SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven," +
                      "       dw.num_mov,dw.num_rel,dw.sdo_mov,dw.val_mov,cd.valor " +
                      "FROM DOC_WS dw, COBROS_DOC cd " +
                      "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.cod_vend AND dw.num_mov=cd.num_doc AND cd.num_doc='" + recibo +"' " +
                      "AND cd.cod_emp='"+lista.getCod_emp()+"'";
            else if(lista.getCod_vend().equals("V999"))
                SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven," +
                        "       dw.num_mov,dw.num_rel,dw.sdo_mov,dw.val_mov,cd.valor " +
                        "FROM DOC_WS dw, COBROS_DOC cd " +
                        "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.vendedor AND dw.num_mov=cd.num_doc AND cd.num_doc='" + recibo +"' " +
                        "AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
            else
                SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven," +
                      "       dw.num_mov,dw.num_rel,dw.sdo_mov,dw.val_mov,cd.valor " +
                      "FROM DOC_WS dw, COBROS_DOC cd " +
                      "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.cod_vend AND dw.num_mov=cd.num_doc AND cd.num_doc='" + recibo +"' " +
                      "AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";

            nc = new ArrayList<FacturaTemp>();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    FacturaTemp fact = new FacturaTemp();
                    fact.setCod_emp(q.getString(0));
                    fact.setCod_mov(q.getString(1));
                    fact.setCod_pto(q.getString(2));
                    fact.setCod_ref(q.getString(3));
                    fact.setCod_ven(q.getString(4));
                    fact.setNum_mov(q.getString(5));
                    fact.setNum_rel(q.getString(6));
                    fact.setSdo_mov(q.getDouble(7));
                    fact.setVal_mov(q.getDouble(8));
                    fact.setValor(q.getDouble(9));
                    nc.add(fact);
                }while (q.moveToNext());
            }
            Log.e("load_notas",SQL);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return nc;
    }

    public FacturaTemp load_notcr(String recibo){
        SQLiteDatabase db = null;
        Cursor q = null;
        FacturaTemp fact = new FacturaTemp();
        try {
            String SQL = "";
            db = dbSql.getReadableDatabase();
            if(lista.getCod_vend().equals("V001") || lista.getCod_vend().equals("V017") || lista.getCod_vend().equals("V012"))
                SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel," +
                      "       dw.sdo_mov,dw.val_mov,cd.valor FROM DOC_WS dw, COBROS_DOC cd " +
                      "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.cod_vend AND dw.num_mov=cd.num_doc AND cd.num_doc='" + recibo +"'" +
                      "AND cd.cod_emp='"+lista.getCod_emp()+"'";
            else if(lista.getCod_vend().equals("V999"))
                SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel," +
                      "       dw.sdo_mov,dw.val_mov,cd.valor FROM DOC_WS dw, COBROS_DOC cd " +
                      "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.vendedor AND dw.num_mov=cd.num_doc AND cd.num_doc='" + recibo +"' " +
                      "AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
            else
                SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel," +
                      "       dw.sdo_mov,dw.val_mov,cd.valor FROM DOC_WS dw, COBROS_DOC cd " +
                      "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.cod_vend AND dw.num_mov=cd.num_doc AND cd.num_doc='" + recibo +"' " +
                      "AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";

            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    fact.setCod_emp(q.getString(0));
                    fact.setCod_mov(q.getString(1));
                    fact.setCod_pto(q.getString(2));
                    fact.setCod_ref(q.getString(3));
                    fact.setCod_ven(q.getString(4));
                    fact.setNum_mov(q.getString(5));
                    fact.setNum_rel(q.getString(6));
                    fact.setSdo_mov(q.getDouble(7));
                    fact.setVal_mov(q.getDouble(8));
                    fact.setValor(q.getDouble(9));
                }while (q.moveToNext());
            }
            Log.e("load_notas",SQL);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return fact;
    }

    public List<FacturaTemp> load_notas2(String recibo){
        SQLiteDatabase db = null;
        Cursor q = null;
        String SQL = "";
        try {
            db = dbSql.getReadableDatabase();
            if(lista.getCod_vend().equals("V999")){
                SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel," +
                        "dw.sdo_mov,dw.val_mov,cd.valor FROM DOC_LOCAL dw, COBROS_DOC cd " +
                        "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.vendedor AND dw.num_mov=cd.num_doc " +
                        "AND dw.cod_vin=cd.cod_vin AND dw.num_mov='" + recibo +"'" +
                        " AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
            }else{
                SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel," +
                        "dw.sdo_mov,dw.val_mov,cd.valor FROM DOC_LOCAL dw, COBROS_DOC cd " +
                        "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.cod_vend AND dw.num_mov=cd.num_doc " +
                        "AND dw.cod_vin=cd.cod_vin AND dw.num_mov='" + recibo +"'" +
                        " AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
            }

            nc = new ArrayList<FacturaTemp>();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    FacturaTemp fact = new FacturaTemp();
                    fact.setCod_emp(q.getString(0));
                    fact.setCod_mov(q.getString(1));
                    fact.setCod_pto(q.getString(2));
                    fact.setCod_ref(q.getString(3));
                    fact.setCod_ven(q.getString(4));
                    fact.setNum_mov(q.getString(5));
                    fact.setNum_rel(q.getString(6));
                    fact.setSdo_mov(q.getDouble(7));
                    fact.setVal_mov(q.getDouble(8));
                    fact.setValor(q.getDouble(9));
                    nc.add(fact);
                }while (q.moveToNext());
            }
            Log.e("load_notas",SQL);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return nc;
    }

    public FacturaTemp load_napp(String recibo){
        SQLiteDatabase db = null;
        Cursor q = null;
        FacturaTemp fact = new FacturaTemp();
        try {
            db = dbSql.getReadableDatabase();
            String SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel," +
                         "       dw.sdo_mov,dw.val_mov,cd.valor " +
                         "FROM DOC_LOCAL dw, COBROS_DOC cd " +
                         "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.cod_vend AND dw.num_mov=cd.num_doc " +
                         "AND dw.num_mov='" + recibo +"' AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
            fact = new FacturaTemp();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    fact.setCod_emp(q.getString(0));
                    fact.setCod_mov(q.getString(1));
                    fact.setCod_pto(q.getString(2));
                    fact.setCod_ref(q.getString(3));
                    fact.setCod_ven(q.getString(4));
                    fact.setNum_mov(q.getString(5));
                    fact.setNum_rel(q.getString(6));
                    fact.setSdo_mov(q.getDouble(7));
                    fact.setVal_mov(q.getDouble(8));
                    fact.setValor(q.getDouble(9));
                }while (q.moveToNext());
            }
            Log.e("load_notas",SQL);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return fact;
    }

    public List<FacturaTemp> load_facturas(List<Cobros_Doc> var){
        SQLiteDatabase db = null;
        Cursor q = null;
        List<Cobros_Doc> recibo = var;
        System.out.println("size recibo "+recibo.size());
        facturas = new ArrayList<FacturaTemp>();
        try {
            String SQL = "";
            for (Cobros_Doc row : recibo) {
                db = dbSql.getReadableDatabase();
                if(lista.getCod_vend().equals("V001") || lista.getCod_vend().equals("V017") || lista.getCod_vend().equals("V012"))
                    SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel," +
                          "dw.sdo_mov,dw.val_mov,cd.valor FROM DOC_WS dw, COBROS_DOC cd " +
                          "WHERE dw.cod_emp=cd.cod_emp AND dw.num_mov=cd.num_mov AND dw.num_mov='" + row.getNum_mov() +"' " +
                          "AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.id_recibo="+row.getId_recibo();
                else if(lista.getCod_vend().equals("V999"))
                    SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel," +
                          "dw.sdo_mov,dw.val_mov,cd.valor FROM DOC_WS dw, COBROS_DOC cd " +
                          "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.vendedor AND dw.num_mov=cd.num_mov AND dw.num_mov='" + row.getNum_mov() +"' " +
                          "AND cd.id_recibo="+row.getId_recibo() +" AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
                else
                    SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel," +
                          "dw.sdo_mov,dw.val_mov,cd.valor FROM DOC_WS dw, COBROS_DOC cd " +
                          "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.cod_vend AND dw.num_mov=cd.num_mov AND dw.num_mov='" + row.getNum_mov() +"' " +
                          "AND cd.id_recibo="+row.getId_recibo() +" AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";

                q = db.rawQuery(SQL, null);
                if (q.moveToFirst()) {
                    do {
                        FacturaTemp fact = new FacturaTemp();
                        fact.setCod_emp(q.getString(0));
                        fact.setCod_mov(q.getString(1));
                        fact.setCod_pto(q.getString(2));
                        fact.setCod_ref(q.getString(3));
                        fact.setCod_ven(q.getString(4));
                        fact.setNum_mov(q.getString(5));
                        fact.setNum_rel(q.getString(6));
                        fact.setSdo_mov(q.getDouble(7));
                        fact.setVal_mov(q.getDouble(8));
                        fact.setValor(q.getDouble(9));
                        facturas.add(fact);
                    } while (q.moveToNext());
                }
                Log.e("load_facturas",SQL);
            }
            q.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } /*finally {
            q.close();
            db.close();
        }*/
        return facturas;
    }

}
