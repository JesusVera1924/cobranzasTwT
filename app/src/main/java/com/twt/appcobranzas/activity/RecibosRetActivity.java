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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.adapter.RetencionesAdapter;
import com.twt.appcobranzas.model.CobrosRet;
import com.twt.appcobranzas.model.FacturaTemp;
import com.twt.appcobranzas.model.RowRetenciones;
import com.twt.appcobranzas.model.Vendedor;
import com.twt.appcobranzas.utils.DbHelper;

/**
 * Created by DEVECUA on 11/12/2015.
 */
@SuppressWarnings("deprecation")
public class RecibosRetActivity extends AppCompatActivity  {

    String TAG="RecibosRetActivity";
    ListView listView;
    Context context;
    public List<RowRetenciones> itemsR;
    ArrayList<CobrosRet> retenc, result;
    ArrayList<FacturaTemp> fact, result1;
    public List<CobrosRet> retencion, reten, listret;
    public List<FacturaTemp> facturas;
    public List<FacturaTemp> listafactura;
    Vendedor lista;
    Button bt_buscar;
    TextView cod;
    EditText recibo;
    String codigo,ret,fch,numero;
    int idr;
    double valor;
    DbHelper dbSql =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retenciones);
        context = this;
        dbSql = new DbHelper(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        lista = bundle.getParcelable("lista");
        //widget
        listView  = (ListView) this.findViewById(R.id.lv_retenciones);
        bt_buscar = (Button) this.findViewById(R.id.btn_buscar);
        cod       = (TextView) this.findViewById(R.id.txt_cli);
        recibo    = (EditText) this.findViewById(R.id.et_recibo);

        registerForContextMenu(listView);
        onBackPressed();

        int cont = getCount();
        if(cont == 0){
            CharSequence text = "NO HAY DATOS DE RETENCIONES PARA MOSTRAR ...!!";
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
            loadRetenciones();
        }

        bt_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rec = recibo.getText().toString();
                if(rec.equals(""))
                    loadRetenciones();
                else
                    busquedaRetenciones();
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

    void loadRetenciones(){
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM COBROS_RET " +
                        "WHERE cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"' AND estado=0 ORDER BY id_recibo";
            itemsR = new ArrayList();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    RowRetenciones cd = new RowRetenciones();
                    cd.setCod_emp(q.getString(1));
                    cd.setId_recibo(q.getInt(2));
                    cd.setFch_ret(q.getString(3));
                    cd.setCod_ref(q.getString(4));
                    cd.setNum_mov(q.getString(5));
                    cd.setCod_mov(q.getString(6));
                    cd.setFecha(q.getString(7));
                    cd.setEstab(q.getString(8));
                    cd.setPto_emi(q.getString(9));
                    cd.setSecuencia(q.getString(10));
                    cd.setNumaut(q.getString(11));
                    cd.setCod_retencion(q.getString(12));
                    cd.setPrc_retencion(q.getDouble(13));
                    cd.setValor(q.getDouble(14));
                    cd.setCod_ven(q.getString(15));
                    cd.setVendedor(q.getString(16));
                    cd.setCod_pto(q.getString(17));
                    cd.setEstado(q.getInt(18));
                    itemsR.add(cd);
                }while (q.moveToNext());
            }
            listView.setAdapter(new RetencionesAdapter(context, itemsR));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
    }

    public List<CobrosRet> load_cobrosdoc(int recibo){
        SQLiteDatabase db = null;
        Cursor q = null;
        retencion = new ArrayList<CobrosRet>();
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM COBROS_RET " +
                        "WHERE id_recibo="+recibo+" AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"'";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    CobrosRet cd = new CobrosRet();
                    cd.setCod_emp(q.getString(1));
                    cd.setId_recibo(q.getInt(2));
                    cd.setFch_ret(q.getString(3));
                    cd.setCod_ref(q.getString(4));
                    cd.setNum_mov(q.getString(5));
                    cd.setCod_mov(q.getString(6));
                    cd.setFecha(q.getString(7));
                    cd.setEstab(q.getString(8));
                    cd.setPto_emi(q.getString(9));
                    cd.setSecuencia(q.getString(10));
                    cd.setNumaut(q.getString(11));
                    cd.setCod_retencion(q.getString(12));
                    cd.setPrc_retencion(q.getDouble(13));
                    cd.setValor(q.getDouble(14));
                    cd.setCod_ven(q.getString(15));
                    cd.setVendedor2(q.getString(16));
                    cd.setCod_pto(q.getString(17));
                    cd.setEstado(q.getInt(18));
                    retencion.add(cd);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return retencion;
    }

    public List<FacturaTemp> load_facturas(List<CobrosRet> var)  {
        SQLiteDatabase db = null;
        Cursor q = null;
        String SQL = "";
        List<CobrosRet> recibo = var;
        facturas = new ArrayList<FacturaTemp>();
        try {
            for (CobrosRet row : recibo) {
                db = dbSql.getReadableDatabase();
                /*SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel," +
                             "       dw.sdo_mov,dw.val_mov,cr.valor " +
                             "FROM DOC_WS dw, COBROS_RET cr " +
                             "WHERE cr.cod_emp=dw.cod_emp AND cr.cod_ref=dw.cod_ref AND cr.num_mov=dw.num_mov AND dw.num_mov='" + row.getNum_mov() +"' " +
                             "AND cr.cod_emp='"+lista.getCod_emp()+"' AND cr.cod_ven='"+lista.getCod_vend()+"'";*/
                if(lista.getCod_vend().equals("V999")){
                    SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel,dw.sdo_mov," +
                            "       dw.val_mov,cr.valor FROM DOC_WS dw, COBROS_RET cr " +
                            "WHERE dw.cod_emp=cr.cod_emp AND cr.cod_ref=dw.cod_ref AND dw.cod_ven=cr.vendedor AND dw.num_mov=cr.num_mov " +
                            "AND dw.cod_emp='"+lista.getCod_emp()+"' AND dw.num_mov='" + row.getNum_mov() +"' AND cr.id_recibo="+row.getId_recibo()+
                            " AND cr.cod_ven='"+lista.getCod_vend()+"' AND cr.estado!=1";
                }else if(lista.getSeleccion().equals("V") && lista.getCod_vend()!=row.getVendedor2()){
                    SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel,dw.sdo_mov," +
                            "       dw.val_mov,cr.valor FROM DOC_WS dw, COBROS_RET cr " +
                            "WHERE dw.cod_emp=cr.cod_emp AND cr.cod_ref=dw.cod_ref AND dw.cod_ven=cr.vendedor AND dw.num_mov=cr.num_mov " +
                            "AND dw.cod_emp='"+lista.getCod_emp()+"' AND dw.num_mov='" + row.getNum_mov() +"' AND cr.id_recibo="+row.getId_recibo()+
                            " AND cr.cod_ven='"+lista.getCod_vend()+"' AND cr.estado!=1";
                }else{
                    SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov,dw.num_rel,dw.sdo_mov," +
                            "       dw.val_mov,cr.valor FROM DOC_WS dw, COBROS_RET cr " +
                            "WHERE dw.cod_emp=cr.cod_emp AND cr.cod_ref=dw.cod_ref AND dw.cod_ven=cr.cod_ven AND dw.num_mov=cr.num_mov " +
                            "AND dw.cod_emp='"+lista.getCod_emp()+"' AND dw.num_mov='" + row.getNum_mov() +"' AND cr.id_recibo="+row.getId_recibo()+
                            " AND cr.cod_ven='"+lista.getCod_vend()+"' AND cr.estado!=1";
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
                        System.out.println(fact.getNum_mov() + " " + fact.getSdo_mov() + " " + fact.getVal_mov()+ " " + fact.getValor());
                        facturas.add(fact);
                    } while (q.moveToNext());
                }
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

    void busquedaRetenciones() {
        SQLiteDatabase db = null;
        Cursor q = null;
        int criterio = Integer.valueOf(recibo.getText().toString());
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM COBROS_RET " +
                        "WHERE id_recibo="+criterio+" AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"' " +
                        "AND estado=0 ORDER BY id_recibo";
            itemsR = new ArrayList();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    RowRetenciones cd = new RowRetenciones();
                    cd.setCod_emp(q.getString(1));
                    cd.setId_recibo(q.getInt(2));
                    cd.setFch_ret(q.getString(3));
                    cd.setCod_ref(q.getString(4));
                    cd.setNum_mov(q.getString(5));
                    cd.setCod_mov(q.getString(6));
                    cd.setFecha(q.getString(7));
                    cd.setEstab(q.getString(8));
                    cd.setPto_emi(q.getString(9));
                    cd.setSecuencia(q.getString(10));
                    cd.setNumaut(q.getString(11));
                    cd.setCod_retencion(q.getString(12));
                    cd.setPrc_retencion(q.getDouble(13));
                    cd.setValor(q.getDouble(14));
                    cd.setCod_ven(q.getString(15));
                    cd.setCod_pto(q.getString(16));
                    cd.setEstado(q.getInt(17));
                    System.out.println(cd.getId_recibo()+" "+cd.getCod_mov()+" "+cd.getNum_mov()+" "+cd.getValor()+" "+" "+cd.getEstado());
                    itemsR.add(cd);
                }while (q.moveToNext());
            }
            listView.setAdapter(new RetencionesAdapter(context, itemsR));
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
            String SQL ="SELECT COUNT(*) FROM COBROS_RET " +
                        "WHERE cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"'";
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

    public ArrayList<CobrosRet> load_retenc(int recibo){
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT DISTINCT cd.* FROM COBROS_RET cd " +
                        "WHERE cd.id_recibo="+recibo+" AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_ven='"+lista.getCod_vend()+"'";
            retenc = new ArrayList<CobrosRet>();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    CobrosRet cd = new CobrosRet();
                    cd.setCod_emp(q.getString(1));
                    cd.setId_recibo(q.getInt(2));
                    cd.setFch_ret(q.getString(3));
                    cd.setCod_ref(q.getString(4));
                    cd.setNum_mov(q.getString(5));
                    cd.setCod_mov(q.getString(6));
                    cd.setFecha(q.getString(7));
                    cd.setEstab(q.getString(8));
                    cd.setPto_emi(q.getString(9));
                    cd.setSecuencia(q.getString(10));
                    cd.setNumaut(q.getString(11));
                    cd.setCod_retencion(q.getString(12));
                    cd.setPrc_retencion(q.getDouble(13));
                    cd.setValor(q.getDouble(14));
                    cd.setCod_ven(q.getString(15));
                    cd.setCod_pto(q.getString(16));
                    cd.setEstado(q.getInt(17));
                    System.out.println(cd.getId_recibo()+" "+cd.getCod_mov()+" "+cd.getNum_mov()+" "+cd.getValor()+" "+" "+cd.getEstado());
                    retenc.add(cd);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return retenc;
    }

    public ArrayList<FacturaTemp> load_docs(String recibo){
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL = "SELECT dw.cod_emp,dw.cod_mov,dw.cod_pto,dw.cod_ref,dw.cod_ven,dw.num_mov," +
                         "       dw.num_rel,dw.sdo_mov,dw.val_mov,dw.iva,dw.base FROM DOC_WS dw " +
                         "WHERE dw.num_mov='" + recibo +"' AND dw.cod_emp='"+lista.getCod_emp()+"' AND dw.cod_ven='"+lista.getCod_vend()+"'";
            fact = new ArrayList<FacturaTemp>();
            q = db.rawQuery(SQL, null);
            if (q.moveToFirst()) {
                do {
                    FacturaTemp factura = new FacturaTemp();
                    factura.setCod_emp(q.getString(0));
                    factura.setCod_mov(q.getString(1));
                    factura.setCod_pto(q.getString(2));
                    factura.setCod_ref(q.getString(3));
                    factura.setCod_ven(q.getString(4));
                    factura.setNum_mov(q.getString(5));
                    factura.setNum_rel(q.getString(6));
                    factura.setSdo_mov(q.getDouble(7));
                    factura.setVal_mov(q.getDouble(8));
                    factura.setIva(q.getDouble(9));
                    factura.setBase(q.getDouble(10));
                    System.out.println(factura.getNum_mov() + " " + factura.getSdo_mov() + " " + factura.getVal_mov() + " " + factura.getIva()+ " " + factura.getBase());
                    fact.add(factura);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return fact;
    }

    public void deleteRetenciones(int recibo){
        SQLiteDatabase db = null;
        try {
            String SQL ="DELETE FROM COBROS_RET " +
                        "WHERE id_recibo="+recibo+" AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
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
        String SQL = "";
        try {
            SQLiteDatabase db = null;
            for (FacturaTemp row: items) {
                saldo = 0.00;
                DecimalFormat pre = new DecimalFormat("0.00");
                if(row.getSdo_mov()<0)
                    saldo = row.getSdo_mov()-row.getValor();
                else
                    saldo = row.getSdo_mov()+row.getValor();
                String s = pre.format(saldo);
                System.out.println("factura numero " + row.getNum_mov() + " actualizada con valor " + saldo);
                /*String SQL = "UPDATE DOC_WS SET sdo_mov=" + Double.valueOf(s) +", num_rel='000000', val_mov=" + Double.valueOf(s) +
                        " WHERE num_mov='" + row.getNum_mov() + "' AND cod_ven='"+lista.getCod_vend()+"'";*/
                if(lista.getCod_vend().equals("V999")){
                    SQL = "UPDATE DOC_WS SET sdo_mov="+ Double.valueOf(s) +", num_rel='000000' " +
                            "WHERE num_mov='" + row.getNum_mov() + "' " +
                            "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+row.getCod_ven()+"'";
                }else if(lista.getSeleccion().equals("V") && lista.getCod_vend()!=row.getCod_ven()){
                    SQL = "UPDATE DOC_WS SET sdo_mov="+ Double.valueOf(s) +", num_rel='000000' " +
                            "WHERE num_mov='" + row.getNum_mov() + "' " +
                            "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+row.getCod_ven()+"'";
                }else{
                    SQL = "UPDATE DOC_WS SET sdo_mov="+ Double.valueOf(s) +", num_rel='000000' " +
                            "WHERE num_mov='" + row.getNum_mov() + "' " +
                            "AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"'";
                }
                /*String SQL = "UPDATE DOC_WS SET num_rel='000000' " +
                             "WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"'";
                Log.e("updateF",SQL);*/
                Log.e("updateF",SQL);
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Exception Error de actualizacion.. " + e.getMessage());
        }

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
        CharSequence text = "DATOS DE LA RETENCION ELIMINADOS ...!!";
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
        loadRetenciones();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        int mPosition;
        if(v.getId() == R.id.lv_retenciones)
        {
            mPosition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
            idr    = this.itemsR.get(mPosition).getId_recibo();
            codigo = this.itemsR.get(mPosition).getCod_ref();
            ret    = this.itemsR.get(mPosition).getEstab()+"-"+this.itemsR.get(mPosition).getPto_emi()+"-"+this.itemsR.get(mPosition).getSecuencia();
            fch    = this.itemsR.get(mPosition).getFecha().toString();
            numero = this.itemsR.get(mPosition).getNum_mov();
            valor  = this.itemsR.get(mPosition).getValor();
            menu.setHeaderTitle(idr+" - Cod. Cliente "+codigo);
            this.getMenuInflater().inflate(R.menu.menu_list1, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public  boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.mCorreo:
                Intent correo = new Intent(RecibosRetActivity.this, MailRetActivity.class);
                correo.putExtra("id_recibo", idr);
                correo.putExtra("num_retencion", ret);
                correo.putExtra("cod_cli", codigo);
                correo.putExtra("fecha", fch);
                correo.putExtra("numero", numero);
                correo.putExtra("valor", valor);
                result = load_retenc(idr);
                result1 = load_docs(numero);
                System.out.println("el tamaño de la lista es " + result.size()+" y "+result1.size());
                correo.putParcelableArrayListExtra("listretenciones", result);
                correo.putParcelableArrayListExtra("listdocs", result1);
                Bundle bundle = new Bundle();
                bundle.putParcelable("item", lista);
                correo.putExtras(bundle);
                startActivity(correo);
                return true;
            case R.id.mDetalle:
                listret = load_cobrosdoc(idr);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecibosRetActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View convertView = (View) inflater.inflate(R.layout.customretenciones, null);
                alertDialog.setView(convertView);
                alertDialog.setTitle("Detalles Pago Retenciones # "+idr);
                ListView lv = (ListView) convertView.findViewById(R.id.listViewRet);
                ArrayAdapter<CobrosRet> adapter = new ArrayAdapter<CobrosRet>(this,android.R.layout.simple_list_item_1,listret);
                lv.setAdapter(adapter);
                alertDialog.show();
                return true;
            case R.id.mEliminar:
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(this);
                alertDialog1.setTitle("Eliminar Datos de la Retención");
                alertDialog1.setMessage("Esta seguro de eliminar todos los datos de la retención "+idr+"..?");
                alertDialog1.setCancelable(false);
                alertDialog1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        reten = load_cobrosdoc(idr);
                        System.out.println("el tamaño de cobros es "+reten.size());
                        listafactura = load_facturas(reten);
                        System.out.println("el tamaño facturas es " + listafactura.size());
                        updateFacturas(listafactura);
                        deleteRetenciones(idr);
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


}
