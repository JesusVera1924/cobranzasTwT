package com.twt.appcobranzas.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.adapter.ReciboAdapter;
import com.twt.appcobranzas.model.BancoDep;
import com.twt.appcobranzas.model.Cobros_Doc;
import com.twt.appcobranzas.model.Cobros_Pagos;
import com.twt.appcobranzas.model.FacturaTemp;
import com.twt.appcobranzas.model.RowNotasCredito;
import com.twt.appcobranzas.model.RowRecibo;
import com.twt.appcobranzas.model.Vendedor;
import com.twt.appcobranzas.utils.DbHelper;
import com.twt.appcobranzas.utils.Utils;

/**
 * Created by DEVECUA on 11/12/2015.
 */
@SuppressWarnings("deprecation")
public class RecibosActivity extends AppCompatActivity  {

    String TAG="RecibosActivity";
    ListView listView;
    Context context;
    public List<RowRecibo> itemsR;
    RowNotasCredito itemsN;
    ArrayList<Cobros_Pagos> pagos, result;
    ArrayList<Cobros_Doc> doc, result1;
    List<BancoDep> bco_dp;
    public List<Cobros_Doc> cobros, cob, cob2, cobros2;
    public List<Cobros_Pagos> pago;
    public List<FacturaTemp> facturas, nc, ncredito, facturas2, ncredito2, nc2;
    public List<FacturaTemp> listafactura, listafactura2;
    Vendedor lista;
    Button   bt_buscar;
    TextView cod;
    EditText recibo;
    Spinner  sprecibos;
    String   nom, fch, numero, codigo, tcob, vori;
    int      idr, contador, idna, contador2;
    double   valor;
    DbHelper dbSql =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recibos);
        context = this;
        dbSql = new DbHelper(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        lista = bundle.getParcelable("lista");
        contador2 = 0;
        //widget
        listView  = (ListView) this.findViewById(R.id.lv_recibos);
        bt_buscar = (Button) this.findViewById(R.id.btn_buscar);
        cod       = (TextView) this.findViewById(R.id.txt_cli);
        recibo    = (EditText) this.findViewById(R.id.et_recibo);
        sprecibos = (Spinner) this.findViewById(R.id.sp_trecibo);

        registerForContextMenu(listView);
        onBackPressed();

        int cont = getCount();
        if(cont == 0){
            CharSequence text = "NO HAY DATOS DE RECIBOS PARA MOSTRAR ...!!";
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
        }else {
            //loadCobros();
        }

        bt_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sprecibos.getSelectedItem().toString().substring(0,3).equals("RCE")){
                    busquedaCobros2();
                }else{
                    busquedaCobros();
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

    void loadCobros(){
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT DISTINCT cd.id_recibo,DATE(cd.fch_emision),cd.cod_cli,cd.nom_cli,cd.tipo_cobro,cd.estado," +
                    "(SELECT SUM(valor) FROM COBROS_DOC WHERE NOT cod_mov='NG' AND NOT cd.cod_mov='NC' AND id_recibo=cd.id_recibo) " +
                    "FROM COBROS_DOC cd " +
                    "WHERE cd.cod_vend='"+lista.getCod_vend()+"' AND NOT cd.cod_mov='NG' AND NOT cd.cod_mov='NC' " +
                    "AND cd.estado!=1 ORDER BY cd.id_recibo";
            itemsR = new ArrayList();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    int recibo  = q.getInt(0);
                    String fch = q.getString(1);
                    String codc = q.getString(2);
                    String nomc = q.getString(3);
                    String codv = "";
                    String codm = "";
                    String num = "";
                    String tip = q.getString(4);
                    double valor = .0;
                    String ptov = "";
                    int est = q.getInt(5);
                    double val = q.getDouble(6);
                    itemsR.add(new RowRecibo(recibo,fch,codc,nomc,codv,codm,num,tip,valor,ptov,est,val));
                }while (q.moveToNext());
            }
            listView.setAdapter(new ReciboAdapter(context, itemsR));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }

    }

    public List<Cobros_Doc> load_cobrosdoc(int recibo, String cod){
        SQLiteDatabase db = null;
        Cursor q = null;
        cobros = new ArrayList<Cobros_Doc>();
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM COBROS_DOC " +
                    "WHERE cod_emp='"+lista.getCod_emp()+"' AND id_recibo="+recibo+" AND cod_vend='"+lista.getCod_vend()+"'"+
                    " AND NOT cod_mov='NG' AND NOT cod_mov='NC' AND tipo_cobro='"+cod+"' AND estado!=1";
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
            Log.e("load_cobrosdoc",SQL);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return cobros;
    }

    public List<Cobros_Pagos> load_cobrospago(int recibo, String cod){
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT DISTINCT cp.* FROM COBROS_PAGOS cp " +
                    "WHERE cp.cod_emp='"+lista.getCod_emp()+"' AND cp.id_recibo="+recibo+" " +
                    "AND cp.cod_vend='"+lista.getCod_vend()+"' AND cp.cod_rec='"+cod+"'";
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
                    pago.add(new Cobros_Pagos(idrecibo, emp, fpago, banco, cuenta, cheque, fch_pago, valor, cod_ref,
                            cod_vend, cod_vin, vendedor, reg, girador, cod_rec, estado));
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

    public List<FacturaTemp> load_facturas(List<Cobros_Doc> var){
        SQLiteDatabase db = null;
        Cursor q = null;
        String SQL = "";
        List<Cobros_Doc> recibo = var;
        System.out.println("size recibo "+recibo.size());
        facturas = new ArrayList<FacturaTemp>();
        try {
            for (Cobros_Doc row : recibo) {
                db = dbSql.getReadableDatabase();
                if(lista.getCod_vend().equals("V999")){
                    SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel,dw.sdo_mov," +
                            "       dw.val_mov,cd.valor FROM DOC_WS dw, COBROS_DOC cd " +
                            "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.vendedor AND dw.num_mov=cd.num_mov " +
                            "AND dw.cod_emp='"+lista.getCod_emp()+"' AND dw.num_mov='" + row.getNum_mov() +"' AND cd.id_recibo="+row.getId_recibo()+
                            " AND cd.cod_vend='"+lista.getCod_vend()+"' AND cd.estado!=1";
                }else if(lista.getSeleccion().equals("V") && lista.getCod_vend()!=row.getVendedor2()){
                    SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel,dw.sdo_mov," +
                            "       dw.val_mov,cd.valor FROM DOC_WS dw, COBROS_DOC cd " +
                            "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.vendedor AND dw.num_mov=cd.num_mov " +
                            "AND dw.cod_emp='"+lista.getCod_emp()+"' AND dw.num_mov='" + row.getNum_mov() +"' AND cd.id_recibo="+row.getId_recibo()+
                            " AND cd.cod_vend='"+lista.getCod_vend()+"' AND cd.estado!=1";
                }else{
                    SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel,dw.sdo_mov," +
                            "       dw.val_mov,cd.valor FROM DOC_WS dw, COBROS_DOC cd " +
                            "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.cod_vend AND dw.num_mov=cd.num_mov " +
                            "AND dw.cod_emp='"+lista.getCod_emp()+"' AND dw.num_mov='" + row.getNum_mov() +"' AND cd.id_recibo="+row.getId_recibo()+
                            " AND cd.cod_vend='"+lista.getCod_vend()+"' AND cd.estado!=1";
                }
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
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return facturas;
    }

    void busquedaCobros(){
        SQLiteDatabase db = null;
        Cursor q = null;
        String SQL="";
        int criterio = 0;
        String criterio2 = "";

        try {
            db = dbSql.getReadableDatabase();
            if(recibo.getText().toString().length()>0){
                criterio = Integer.valueOf(recibo.getText().toString());
                criterio2 = sprecibos.getSelectedItem().toString().substring(0, 2);
                SQL="SELECT DISTINCT cd.id_recibo,DATE(cd.fch_emision),cd.cod_cli,cd.nom_cli,cd.tipo_cobro,cd.estado,\n" +
                        "(SELECT DISTINCT SUM(valor) FROM COBROS_DOC " +
                        " WHERE cod_emp='"+lista.getCod_emp()+"' AND tipo_cobro!='NG' AND tipo_cobro!='NC'\n" +
                        " AND cod_emp= cd.cod_emp AND id_recibo=cd.id_recibo AND cod_vend=cd.cod_vend)\n" +
                        "FROM COBROS_DOC cd " +
                        "WHERE cd.cod_emp='"+lista.getCod_emp()+"' AND cd.tipo_cobro='"+criterio2+"' AND cd.id_recibo="+criterio +"\n" +
                        "AND cd.cod_vend='"+lista.getCod_vend()+"'\n" +
                        "AND NOT cd.cod_mov='NG' AND NOT cd.cod_mov='NC'\n" +
                        "AND cd.estado!=1 ORDER BY cd.id_recibo";
            }else{
                criterio2 = sprecibos.getSelectedItem().toString().substring(0, 2);
                SQL="SELECT DISTINCT cd.id_recibo,DATE(cd.fch_emision),cd.cod_cli,cd.nom_cli,cd.tipo_cobro,cd.estado,\n" +
                        "(SELECT DISTINCT SUM(valor) FROM COBROS_DOC " +
                        " WHERE cod_emp='"+lista.getCod_emp()+"' AND tipo_cobro!='NG' AND tipo_cobro!='NC'\n" +
                        " AND cod_emp=cd.cod_emp AND id_recibo=cd.id_recibo AND cod_vend=cd.cod_vend)\n" +
                        "FROM COBROS_DOC cd " +
                        "WHERE cd.cod_emp='"+lista.getCod_emp()+"' AND cd.tipo_cobro='"+criterio2+"'"+"\n" +
                        "AND cd.cod_vend='"+lista.getCod_vend()+"'\n" +
                        "AND NOT cd.cod_mov='NG' AND NOT cd.cod_mov='NC'\n" +
                        "AND cd.estado!=1 ORDER BY cd.id_recibo";
            }
            itemsR = new ArrayList();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    int recibo  = q.getInt(0);
                    String fch = q.getString(1);
                    String codc = q.getString(2);
                    String nomc = q.getString(3);
                    String codv = "";
                    String codm = "";
                    String num = "";
                    String tip = q.getString(4);
                    double valor = .0;
                    String ptov = "";
                    int est = q.getInt(5);
                    double val = q.getDouble(6);
                    itemsR.add(new RowRecibo(recibo,fch,codc,nomc,codv,codm,num,tip,valor,ptov,est,val));
                }while (q.moveToNext());
            }
            listView.setAdapter(new ReciboAdapter(context, itemsR));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }

    }

    void busquedaCobros2(){
        SQLiteDatabase db = null;
        Cursor q = null;
        String SQL="";

        try {
            db = dbSql.getReadableDatabase();
            SQL="SELECT DISTINCT cd.id_recibo,DATE(cd.fch_emision),cd.cod_cli,cd.nom_cli,cd.tipo_cobro,cd.estado,\n" +
                    "(SELECT DISTINCT SUM(valor) FROM COBROS_DOC " +
                    " WHERE cod_emp='"+lista.getCod_emp()+"' AND tipo_cobro!='NG' AND tipo_cobro!='NC'\n" +
                    " AND cod_emp=cd.cod_emp AND id_recibo=cd.id_recibo AND cod_vend=cd.cod_vend)\n" +
                    "FROM COBROS_DOC cd, COBROS_PAGOS cp\n" +
                    "WHERE cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"' " +
                    "AND cp.banco='-' AND cd.cod_emp=cp.cod_emp AND cd.id_recibo=cp.id_recibo\n" +
                    "AND cd.cod_cli=cp.cod_ref\n" +
                    "AND cp.fpago='EF' AND NOT cd.cod_mov='NG' AND NOT cd.cod_mov='NC'\n" +
                    "AND NOT cp.fpago='NG' AND NOT cp.fpago='NC' AND cd.estado!=1\n" +
                    "ORDER BY cd.id_recibo";
            itemsR = new ArrayList();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    int recibo  = q.getInt(0);
                    String fch = q.getString(1);
                    String codc = q.getString(2);
                    String nomc = q.getString(3);
                    String codv = "";
                    String codm = "";
                    String num = "";
                    String tip = q.getString(4);
                    double valor = .0;
                    String ptov = "";
                    int est = q.getInt(5);
                    double val = q.getDouble(6);
                    itemsR.add(new RowRecibo(recibo,fch,codc,nomc,codv,codm,num,tip,valor,ptov,est,val));
                }while (q.moveToNext());
            }
            listView.setAdapter(new ReciboAdapter(context, itemsR));
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
                    "WHERE cod_emp='"+lista.getCod_emp()+"' AND cod_vend='"+lista.getCod_vend()+"' AND estado!=1";
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

    public int getCount(int id){
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT COUNT(*) FROM COBROS_DOC " +
                    "WHERE cod_emp='"+lista.getCod_emp()+"' AND id_recibo="+id+" AND cod_vend='"+lista.getCod_vend()+"' AND estado!=1";
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

    public int getCount2(String id){
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT COUNT(*) FROM COBROS_DOC " +
                    "WHERE cod_emp='"+lista.getCod_emp()+"' AND num_doc="+id+" AND cod_vend='"+lista.getCod_vend()
                    +"' AND tipo_cobro='NA' AND estado!=1";
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

    public ArrayList<Cobros_Pagos> load_cobrospagos(int recibo, String cod){
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT DISTINCT cp.* FROM COBROS_PAGOS cp " +
                    "WHERE cp.cod_emp='"+lista.getCod_emp()+"' AND cp.id_recibo="+recibo+" " +
                    "AND cp.cod_vend='"+lista.getCod_vend()+"' AND cp.cod_rec='"+cod+"'";
            pagos = new ArrayList<Cobros_Pagos>();
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
                    pagos.add(new Cobros_Pagos(idrecibo, emp, fpago, banco, cuenta, cheque, fch_pago, valor, cod_ref, cod_vend,
                            cod_vin, vendedor, reg, girador, cod_rec, estado));
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return pagos;
    }

    public ArrayList<Cobros_Doc> load_docs(int recibo, String cod){
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT DISTINCT cd.* FROM COBROS_DOC cd " +
                    "WHERE cd.cod_emp='"+lista.getCod_emp()+"' AND cd.id_recibo="+recibo+" " +
                    "AND cd.cod_vend='"+lista.getCod_vend()+"' AND cd.estado!=1 AND cd.tipo_cobro='"+cod+"'";
            doc = new ArrayList<Cobros_Doc>();
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
                    doc.add(cd);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return doc;
    }

    public List<FacturaTemp> load_notas(int recibo, String ven){
        SQLiteDatabase db = null;
        String SQL = "";
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            if(lista.getCod_vend().equals("V999")){
                SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel,dw.sdo_mov," +
                        "       dw.val_mov,cd.valor " +
                        "FROM DOC_LOCAL dw, COBROS_DOC cd " +
                        "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.vendedor AND dw.num_rel=cd.id_recibo AND dw.num_rel='" + recibo +
                        "' AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
            }else if(lista.getSeleccion().equals("V") && lista.getCod_vend() != ven){
                SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel,dw.sdo_mov," +
                        "       dw.val_mov,cd.valor " +
                        "FROM DOC_LOCAL dw, COBROS_DOC cd " +
                        "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.vendedor AND dw.num_rel=cd.id_recibo AND dw.num_rel='" + recibo +
                        "' AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
            }else{
                SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel,dw.sdo_mov," +
                        "       dw.val_mov,cd.valor " +
                        "FROM DOC_LOCAL dw, COBROS_DOC cd " +
                        "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.cod_vend AND dw.num_rel=cd.id_recibo AND dw.num_rel='" + recibo +
                        "' AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
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
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return nc;
    }

    public void deleteReciboPagos(int recibo, String cod){
        SQLiteDatabase db = null;
        try {
            String SQL ="DELETE FROM COBROS_PAGOS " +
                    "WHERE cod_emp='"+lista.getCod_emp()+"' AND id_recibo="+recibo+" AND cod_rec='"+cod+"' AND cod_vend='"+lista.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
            Log.e("deleteReciboPagos",SQL);
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
                    "WHERE cod_emp='"+lista.getCod_emp()+"' AND num_rel="+recibo+" AND cod_ven='"+lista.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
            Log.e("deleteNotasCredito",SQL);
            db.execSQL(SQL);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void deleteReciboCobros(int recibo, int cont, String cod){
        int aux = cont - 1;
        SQLiteDatabase db = null;
        try {
            //String SQL ="DELETE FROM COBROS_DOC WHERE id_recibo="+recibo+" AND cod_vend='"+lista.getCod_vend()+"' LIMIT "+aux;
            String SQL ="DELETE FROM COBROS_DOC " +
                    "WHERE id IN (SELECT id FROM COBROS_DOC " +
                    "             WHERE id_recibo="+recibo+" AND tipo_cobro='"+cod+"' " +
                    "             AND cod_emp='"+lista.getCod_emp()+"' AND cod_vend='"+lista.getCod_vend()+"' LIMIT "+aux+")";
            db = dbSql.getWritableDatabase();
            Log.e("deleteReciboCobros",SQL);
            db.execSQL(SQL);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void deleteReciboCobros1(int recibo, String cod){
        SQLiteDatabase db = null;
        String SQL ="";
        try {
            SQL ="UPDATE COBROS_DOC SET estado = 1 " +
                    "WHERE id_recibo="+recibo+" AND tipo_cobro='"+cod+"' " +
                    "AND cod_emp='"+lista.getCod_emp()+"' AND cod_vend='"+lista.getCod_vend()+"'";

            db = dbSql.getWritableDatabase();
            Log.e("deleteReciboCobros1",SQL);
            db.execSQL(SQL);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void updateFacturas(List<FacturaTemp> fa){
        List<FacturaTemp> items = fa;
        double saldo = 0.00;
        SQLiteDatabase db = null;
        String SQL = "";
        try {
            for (FacturaTemp row: items) {
                saldo = 0.00;
                if(row.getSdo_mov()<0)
                    saldo = row.getSdo_mov()-row.getValor();
                else
                    saldo = row.getSdo_mov()+row.getValor();
                double s = Utils.Redondear(saldo,2);
                System.out.println("factura numero " + row.getNum_mov() + " actualizada con valor " + saldo);
                if(lista.getCod_vend().equals("V999")){
                    SQL = "UPDATE DOC_WS SET sdo_mov="+ s +" " +
                            "WHERE num_mov='" + row.getNum_mov() + "' " +
                            "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+row.getCod_ven()+"'";
                }else if(lista.getSeleccion().equals("V") && lista.getCod_vend()!=row.getCod_ven()){
                    SQL = "UPDATE DOC_WS SET sdo_mov="+ s +" " +
                            "WHERE num_mov='" + row.getNum_mov() + "' " +
                            "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+row.getCod_ven()+"'";
                }else{
                    SQL = "UPDATE DOC_WS SET sdo_mov="+ s +" " +
                            "WHERE num_mov='" + row.getNum_mov() + "' " +
                            "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"'";
                }

                Log.e("updateF",SQL);
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Exception Error de actualizacion.. " + e.getMessage());
        }finally {
            db.close();
        }
    }

    public void updateNotas(List<FacturaTemp> fa){
        List<FacturaTemp> items = fa;
        double saldo = 0.00;
        SQLiteDatabase db = null;
        String SQL = "";
        try {
            for (FacturaTemp row: items) {
                saldo = 0.00;
                if(row.getSdo_mov()<0)
                    saldo = row.getSdo_mov()-row.getValor();
                else
                    saldo = row.getSdo_mov()+row.getValor();
                double s = Utils.Redondear(saldo,2);
                double nuevosaldo = (-1)*(s);
                System.out.println("factura numero " + row.getNum_mov() + " actualizada con valor " + nuevosaldo);
                if(lista.getCod_vend().equals("V999")){
                    SQL = "UPDATE DOC_WS SET sdo_mov="+ nuevosaldo +" " +
                            "WHERE num_mov='" + row.getNum_mov() +
                            "' AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+row.getCod_ven()+"'";
                }else if(lista.getSeleccion().equals("V") && lista.getCod_vend()!=row.getCod_ven()){
                    SQL = "UPDATE DOC_WS SET sdo_mov="+ nuevosaldo +" " +
                            "WHERE num_mov='" + row.getNum_mov() +
                            "' AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+row.getCod_ven()+"'";
                }else{
                    SQL = "UPDATE DOC_WS SET sdo_mov="+ nuevosaldo +" " +
                            "WHERE num_mov='" + row.getNum_mov() +
                            "' AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"'";
                }
                Log.e("updateNotas",SQL);
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Exception Error de actualizacion nota credito.. " + e.getMessage());
        }
    }

    public void updateEfectivo(int recibo, String bc, String cta, String numero){
        String SQL = "";
        try {
            SQLiteDatabase db = null;
            SQL = "UPDATE COBROS_PAGOS SET banco='"+bc+"', cuenta='"+cta+"', cheque='"+numero+"' " +
                    "WHERE id_recibo='"+recibo+
                    "' AND fpago='EF' AND cod_emp='"+lista.getCod_emp()+"' AND cod_vend='"+lista.getCod_vend()+"'";

            Log.e("updateEfc",SQL);
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Exception Error de actualizacion efectivo.. " + e.getMessage());
        }
    }

    public List<Cobros_Doc> load_cobrosdoc2(String recibo){
        SQLiteDatabase db = null;
        Cursor q = null;
        cobros2 = new ArrayList<Cobros_Doc>();
        idna = 0;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM COBROS_DOC " +
                    "WHERE num_doc="+recibo+" AND cod_emp='"+lista.getCod_emp()+"' AND cod_vend='"+lista.getCod_vend()+"' " +
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
                    idna = cd.getId_recibo();
                    cobros2.add(cd);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return cobros2;
    }

    public List<FacturaTemp> load_facturas2(List<Cobros_Doc> var){
        SQLiteDatabase db = null;
        Cursor q = null;
        String SQL = "";
        List<Cobros_Doc> recibo = var;
        facturas2 = new ArrayList<FacturaTemp>();
        try {
            for (Cobros_Doc row : recibo) {
                db = dbSql.getReadableDatabase();
                if(lista.getCod_vend().equals("V999")){
                    SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov," +
                            "       dw.num_rel,dw.sdo_mov,dw.val_mov,cd.valor " +
                            "FROM DOC_WS dw, COBROS_DOC cd " +
                            "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.vendedor AND dw.num_mov=cd.num_mov AND dw.num_mov='" + row.getNum_mov() +"' " +
                            "AND cd.id_recibo="+row.getId_recibo() +" AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
                }else if(lista.getSeleccion().equals("V") && lista.getCod_vend()!=row.getVendedor2()){
                    SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov," +
                            "       dw.num_rel,dw.sdo_mov,dw.val_mov,cd.valor " +
                            "FROM DOC_WS dw, COBROS_DOC cd " +
                            "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.vendedor AND dw.num_mov=cd.num_mov AND dw.num_mov='" + row.getNum_mov() +"' " +
                            "AND cd.id_recibo="+row.getId_recibo() +" AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
                }else{
                    SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov," +
                            "       dw.num_rel,dw.sdo_mov,dw.val_mov,cd.valor " +
                            "FROM DOC_WS dw, COBROS_DOC cd " +
                            "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.cod_vend AND dw.num_mov=cd.num_mov AND dw.num_mov='" + row.getNum_mov() +"' " +
                            "AND cd.id_recibo="+row.getId_recibo() +" AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
                }
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
                        facturas2.add(fact);
                    } while (q.moveToNext());
                }
                Log.e("load_facturas2",SQL);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return facturas2;
    }

    public List<FacturaTemp> load_notas2(String recibo, String ven){
        SQLiteDatabase db = null;
        String SQL = "";
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            if(lista.getCod_vend().equals("V999")){
                SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel,dw.sdo_mov," +
                        "       dw.val_mov,cd.valor " +
                        "FROM DOC_LOCAL dw, COBROS_DOC cd " +
                        "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.vendedor AND dw.num_mov=cd.num_doc " +
                        "AND dw.num_mov='" + recibo +"' AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
            }else if(lista.getSeleccion().equals("V") && lista.getCod_vend() != ven){
                SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel,dw.sdo_mov," +
                        "       dw.val_mov,cd.valor " +
                        "FROM DOC_LOCAL dw, COBROS_DOC cd " +
                        "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.vendedor AND dw.num_mov=cd.num_doc " +
                        "AND dw.num_mov='" + recibo +"' AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
            }else{
                SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel,dw.sdo_mov," +
                        "       dw.val_mov,cd.valor " +
                        "FROM DOC_LOCAL dw, COBROS_DOC cd " +
                        "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.cod_vend AND dw.num_mov=cd.num_doc " +
                        "AND dw.num_mov='" + recibo +"' AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
            }

            nc2 = new ArrayList<FacturaTemp>();
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
                    nc2.add(fact);
                }while (q.moveToNext());
            }
            Log.e("load_notas2",SQL);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return nc2;
    }

    public void updateFacturas2(List<FacturaTemp> fa){
        List<FacturaTemp> items = fa;
        double saldo = 0.00;
        SQLiteDatabase db = null;
        String SQL = "";
        try {
            for (FacturaTemp row: items) {
                if(row.getSdo_mov()<0)
                    saldo = row.getSdo_mov()-row.getValor();
                else
                    saldo = row.getSdo_mov()+row.getValor();
                double s = Utils.Redondear(saldo,2);
                if(lista.getCod_vend().equals("V999")){
                    SQL = "UPDATE DOC_WS SET sdo_mov="+ s +" " +
                            "WHERE num_mov='" + row.getNum_mov() + "' " +
                            "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+row.getCod_ven()+"'";
                }else if(lista.getSeleccion().equals("V") && lista.getCod_vend()!=row.getCod_ven()){
                    SQL = "UPDATE DOC_WS SET sdo_mov="+ s +" " +
                            "WHERE num_mov='" + row.getNum_mov() + "' " +
                            "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+row.getCod_ven()+"'";
                }else{
                    SQL = "UPDATE DOC_WS SET sdo_mov="+ s +" " +
                            "WHERE num_mov='" + row.getNum_mov() + "' " +
                            "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"'";
                }
                Log.e("updateF",SQL);
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Exception Error de actualizacion.. " + e.getMessage());
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
        String SQL = "";
        try {
            for (FacturaTemp row: items) {
                if(aux.equals(row.getNum_mov())){
                    fat = load_napp(row.getNum_mov(),row.getCod_ven());
                    if(fat.getSdo_mov()<0)
                        saldo = fat.getSdo_mov()-fat.getValor();
                    else
                        saldo = fat.getSdo_mov()+fat.getValor();
                    double s = Utils.Redondear(saldo,2);
                    if(s>0)
                        nuevosaldo = (-1)*(s);
                    else
                        nuevosaldo = s;

                    if(lista.getCod_vend().equals("V999")){
                        SQL = "UPDATE DOC_LOCAL SET sdo_mov="+ nuevosaldo +" " +
                                "WHERE num_mov='" + row.getNum_mov() +
                                "' AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+row.getCod_ven()+"'";
                    }else if(lista.getSeleccion().equals("V") && lista.getCod_vend()!=row.getCod_ven()){
                        SQL = "UPDATE DOC_LOCAL SET sdo_mov="+ nuevosaldo +" " +
                                "WHERE num_mov='" + row.getNum_mov() +
                                "' AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+row.getCod_ven()+"'";
                    }else{
                        SQL = "UPDATE DOC_LOCAL SET sdo_mov="+ nuevosaldo +" " +
                                "WHERE num_mov='" + row.getNum_mov() +
                                "' AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"'";
                    }

                    Log.e("updateNotas2",SQL);
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

                    if(lista.getCod_vend().equals("V999")){
                        SQL = "UPDATE DOC_LOCAL SET sdo_mov="+ nuevosaldo +" " +
                                "WHERE num_mov='" + row.getNum_mov() +
                                "' AND cod_ven='"+row.getCod_ven()+"'";
                    }else if(lista.getSeleccion().equals("V") && lista.getCod_vend()!=row.getCod_ven()){
                        SQL = "UPDATE DOC_LOCAL SET sdo_mov="+ nuevosaldo +" " +
                                "WHERE num_mov='" + row.getNum_mov() +
                                "' AND cod_ven='"+row.getCod_ven()+"'";
                    }else{
                        SQL = "UPDATE DOC_LOCAL SET sdo_mov="+ nuevosaldo +" " +
                                "WHERE num_mov='" + row.getNum_mov() +
                                "' AND cod_ven='"+lista.getCod_vend()+"'";
                    }

                    Log.e("updateNotas2",SQL);
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

    public FacturaTemp load_napp(String recibo, String ven){
        SQLiteDatabase db = null;
        Cursor q = null;
        String SQL = "";
        FacturaTemp fact = new FacturaTemp();
        try {
            db = dbSql.getReadableDatabase();
            if(lista.getCod_vend().equals("V999")){
                SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel," +
                        "       dw.sdo_mov,dw.val_mov,cd.valor " +
                        "FROM DOC_LOCAL dw, COBROS_DOC cd " +
                        "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.cod_ven AND dw.num_mov=cd.num_doc AND dw.num_mov='" + recibo +"' " +
                        "AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
            }if(lista.getSeleccion().equals("V") && lista.getCod_vend()!=ven){
                SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel," +
                        "       dw.sdo_mov,dw.val_mov,cd.valor " +
                        "FROM DOC_LOCAL dw, COBROS_DOC cd " +
                        "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.cod_vend AND dw.num_mov=cd.num_doc AND dw.num_mov='" + recibo +"' " +
                        "AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
            }else{
                SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel," +
                        "       dw.sdo_mov,dw.val_mov,cd.valor " +
                        "FROM DOC_LOCAL dw, COBROS_DOC cd " +
                        "WHERE dw.cod_emp=cd.cod_emp AND dw.cod_ven=cd.cod_vend AND dw.num_mov=cd.num_doc AND dw.num_mov='" + recibo +"' " +
                        "AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
            }

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

    public void deleteReciboPagos2(String recibo){
        SQLiteDatabase db = null;
        try {
            String SQL ="DELETE FROM COBROS_PAGOS " +
                    "WHERE cuenta="+recibo+" AND fpago='NA' AND cod_emp='"+lista.getCod_emp()+"' AND cod_vend='"+lista.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
            Log.e("deleteReciboPagos2",SQL);
            db.execSQL(SQL);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void deleteReciboCobros2(String recibo){
        SQLiteDatabase db = null;
        try {
            String SQL ="DELETE FROM COBROS_DOC " +
                    "WHERE num_doc='"+recibo+"' AND tipo_cobro='NA' AND cod_emp='"+lista.getCod_emp()+"' AND cod_vend='"+lista.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
            Log.e("deleteReciboCobros2",SQL);
            db.execSQL(SQL);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    void busquedaNotas2(String idr) {
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM DOC_LOCAL " +
                    "WHERE num_rel="+idr+" AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+
                    "' AND cod_mov='NA' AND val_mov>=0";
            itemsN = new RowNotasCredito();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    String cod_emp = q.getString(1);
                    String cod_mov = q.getString(2);
                    String cod_pto  = q.getString(3);
                    String cod_ref = q.getString(4);
                    String cod_vin = q.getString(5);
                    String vendedor = q.getString(6);
                    String cod_ven = q.getString(7);
                    int dias = q.getInt(8);
                    String fch_emi = q.getString(9);
                    Date emi = Utils.ConvertirShortStringToDate(fch_emi);
                    String fch_ven = q.getString(10);
                    Date ven = Utils.ConvertirShortStringToDate(fch_ven);
                    String num_mov = q.getString(11);
                    String num_rel = q.getString(12);
                    double sdo_mov = q.getDouble(13);
                    double val_mov = q.getDouble(14);
                    double iva = q.getDouble(15);
                    double base = q.getDouble(16);
                    String est = q.getString(17);
                    String cdb = q.getString(18);
                    String ncta = q.getString(19);
                    boolean sel = false;
                    double ab = q.getDouble(12);
                    itemsN = new RowNotasCredito(cod_emp,cod_mov,cod_pto,cod_ref,cod_ven,dias,emi,ven,num_mov,num_rel,
                            sdo_mov,val_mov,iva,base,est,cdb,ncta,sel,ab);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
    }

    public List<BancoDep> consulta1(){
        SQLiteDatabase db = null;
        List<BancoDep> listad = new ArrayList<>();
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM BANCOS_DP WHERE cod_emp='"+lista.getCod_emp()+"' ORDER BY nom_banco";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
                    BancoDep b = new BancoDep();
                    System.out.println(q.getString(2));
                    b.setNom_banco(q.getString(2));
                    listad.add(b);
                } while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return listad;
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
        CharSequence text = "DATOS DEL RECIBO ELIMINADOS ...!!";
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
        //loadCobros();
        busquedaCobros();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        int mPosition;
        if(v.getId() == R.id.lv_recibos)
        {
            mPosition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
            idr    = this.itemsR.get(mPosition).getId_recibo();
            nom    = this.itemsR.get(mPosition).getNom_cli().toUpperCase();
            codigo = this.itemsR.get(mPosition).getCod_cli();
            fch    = this.itemsR.get(mPosition).getFch_emision().toString();
            numero = this.itemsR.get(mPosition).getNum_mov();
            vori   = this.itemsR.get(mPosition).getVendedor();
            valor  = this.itemsR.get(mPosition).getValor();
            tcob   = this.itemsR.get(mPosition).getTipo_cobro();
            menu.setHeaderTitle(idr+" - "+nom);
            if(sprecibos.getSelectedItem().toString().substring(0,3).equals("RCE"))
                this.getMenuInflater().inflate(R.menu.menu_list1_1, menu);
            else
                this.getMenuInflater().inflate(R.menu.menu_list1, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public  boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.mCorreo:
                Intent correo = new Intent(RecibosActivity.this, MailActivity.class);
                correo.putExtra("id_recibo", idr);
                correo.putExtra("nomb_cli", nom);
                correo.putExtra("cod_cli", codigo);
                correo.putExtra("fecha", fch);
                correo.putExtra("numero", numero);
                correo.putExtra("valor", valor);
                correo.putExtra("cobro", tcob);
                result = load_cobrospagos(idr, tcob);
                result1 = load_docs(idr, tcob);
                correo.putParcelableArrayListExtra("listpagos", result);
                correo.putParcelableArrayListExtra("listcobros", result1);
                Bundle bundle = new Bundle();
                bundle.putParcelable("item", lista);
                correo.putExtras(bundle);
                startActivity(correo);
                //updateReciboCorreo(idr,tcob);
                return true;
            case R.id.mDetalle:
                pago = load_cobrospago(idr,tcob);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecibosActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View convertView = (View) inflater.inflate(R.layout.customrecibos, null);
                alertDialog.setView(convertView);
                cob = load_cobrosdoc(idr,tcob);
                StringBuilder aux = resumenFactura();
                alertDialog.setTitle("Detalles Pago Recibo # "+idr+"\n");
                ListView lv = (ListView) convertView.findViewById(R.id.listViewRec);
                TextView res = (TextView) convertView.findViewById(R.id.res_fra);
                res.setText("Facturas Seleccionadas en este Recibo "+aux.toString().replaceAll("\\n", "-"));
                ArrayAdapter<Cobros_Pagos> adapter = new ArrayAdapter<Cobros_Pagos>(this,android.R.layout.simple_list_item_1,pago);
                lv.setAdapter(adapter);
                alertDialog.show();
                return true;
            case R.id.mEliminar:
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(this);
                alertDialog1.setTitle("Eliminar Datos del Recibo");
                alertDialog1.setMessage("Esta seguro de eliminar todos los datos del recibo "+idr+"..?");
                alertDialog1.setCancelable(false);
                alertDialog1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        cob = load_cobrosdoc(idr,tcob);
                        listafactura = load_facturas(cob);
                        ncredito = load_notas(idr, vori);
                        contador = getCount(idr);
                        updateFacturas(listafactura);
                        updateNotas(ncredito);

                        /*Borrado Notas Credito NA*/
                        busquedaNotas2(String.valueOf(idr).trim());
                        contador2 = getCount2(itemsN.getNum_mov());

                        if(contador2>0) {
                            cob2 = load_cobrosdoc2(itemsN.getNum_mov());
                            listafactura2 = load_facturas2(cob2);
                            ncredito2 = load_notas2(itemsN.getNum_mov(), vori);

                            updateFacturas2(listafactura2);
                            updateNotas2(ncredito2);
                            deleteReciboCobros2(itemsN.getNum_mov());
                            deleteReciboPagos2(itemsN.getNum_mov());
                        }
                        /*************************/

                        deleteNotasCredito(idr);

                        deleteReciboCobros(idr,contador,tcob);
                        deleteReciboCobros1(idr,tcob);
                        deleteReciboPagos(idr,tcob);

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
            case R.id.mCruzarDep:

                bco_dp = new ArrayList<BancoDep>();
                bco_dp = consulta1();

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.cruce_deposito);
                dialog.setTitle("Cruce de Depositos");

                // set the custom dialog components - text, image and button
                TextView text1 = (TextView) dialog.findViewById(R.id.text1);
                TextView text2 = (TextView) dialog.findViewById(R.id.text1_1);
                TextView text3 = (TextView) dialog.findViewById(R.id.text1_2);
                final EditText etext = (EditText) dialog.findViewById(R.id.etext1_1);
                final EditText etext2 = (EditText) dialog.findViewById(R.id.etext1_2);
                final Spinner  banco = (Spinner)  dialog.findViewById(R.id.sp_banco1);

                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);

                ArrayAdapter<BancoDep> adaptador2 = new ArrayAdapter<BancoDep>(context, R.layout.simple_spinner_list, bco_dp);
                adaptador2.setDropDownViewResource(R.layout.simple_spinner_list);
                banco.setAdapter(adaptador2);

                etext2.requestFocus();

                banco.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (banco.getSelectedItem().toString().equals("GUAYAQUIL")) {
                            String bco = cNumero(banco.getSelectedItem().toString());
                            etext.setEnabled(false);
                            etext.setText(bco);
                        } else if (banco.getSelectedItem().toString().equals("PICHINCHA")) {
                            String bco = cNumero(banco.getSelectedItem().toString());
                            etext.setEnabled(false);
                            etext.setText(bco);
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        int var = 15;
                        String aux = banco.getSelectedItem().toString();
                        String cod_bco = bco_dp(aux);
                        String cuenta = etext.getText().toString();
                        etext2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(var)});
                        String numero = etext2.getText().toString();
                        updateEfectivo(idr, cod_bco, cuenta, numero);
                        CharSequence text = "CRUCE DE DEPOSITO EFECTUADO SATISFACTORIAMENTE ...!!";
                        int duration = Toast.LENGTH_SHORT;

                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.custom_toast_ok,
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
                    }
                });

                dialog.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public String cNumero(String banco){
        SQLiteDatabase db = null;
        String b = "";
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String[] args = new String[] {banco};
            String SQL ="SELECT num_cuenta FROM BANCOS_DP WHERE cod_emp='"+lista.getCod_emp()+"' AND nom_banco=?";
            q = db.rawQuery(SQL,args);
            if (q.moveToFirst()) {
                do {
                    System.out.println(q.getString(0));
                    b = q.getString(0);
                } while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return b;
    }

    public String bco_dp(String banco){
        SQLiteDatabase db = null;
        Cursor q = null;
        String dato = "";
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT cod_bco FROM BANCOS_DP WHERE cod_emp='"+lista.getCod_emp()+"' AND nom_banco='"+banco+"'";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
                    System.out.println(q.getString(0));
                    dato = q.getString(0);
                } while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return dato;
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

    public StringBuilder resumenFactura(){
        StringBuilder message = new StringBuilder();
        for(Cobros_Doc row: cob){
            if(row.getCod_mov().equals("NG")){

            }else{
                message.append(row.getNum_mov());
                message.append("\n");
            }
        }
        return message;
    }

}
