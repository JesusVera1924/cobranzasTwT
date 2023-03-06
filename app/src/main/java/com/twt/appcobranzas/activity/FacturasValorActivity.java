package com.twt.appcobranzas.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.model.RowFactura;
import com.twt.appcobranzas.model.Vendedor;
import com.twt.appcobranzas.utils.DbHelper;
import com.twt.appcobranzas.utils.Utils;

/**
 * Created by DEVECUA on 11/12/2015.
 */
@SuppressWarnings("deprecation")
public class FacturasValorActivity extends AppCompatActivity {

    String TAG="FacturasValorActivity";
    ListView listView;
    Context context;
    ArrayList<RowFactura> items, aux, list2;
    RowFactura lista;
    TextView cod, valores, val, abonos, ncredito, ng1, titulo;
    CheckBox chkFact;
    Button btCobro;
    DbHelper dbSql =null;
    String cd, nom, dato, pers;
    String tf, tr;
    Vendedor list;
    double parcial1 = 0.00, ab, nc1, ng2;
    int contador=0;
    private Double sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facturasvalor);
        context = this;
        dbSql = new DbHelper(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        sum = .0;
        cd = getIntent().getExtras().getString("cod");
        nom = getIntent().getExtras().getString("nomb");
        pers = getIntent().getExtras().getString("persona");
        dato = getIntent().getExtras().getString("dato");
        tf = getIntent().getExtras().getString("titulo");
        tr = getIntent().getExtras().getString("titulo1");
        Bundle bundle = getIntent().getExtras();
        list = bundle.getParcelable("item");
        list2 = getIntent().getParcelableArrayListExtra("aux");

        //widget
        listView = (ListView) this.findViewById(R.id.lsv_lista_facturas);
        cod      = (TextView) this.findViewById(R.id.txt_cliente);
        val      = (TextView) this.findViewById(R.id.txt_valor_total);
        valores  = (TextView) this.findViewById(R.id.txt_valor);
        abonos   = (TextView) this.findViewById(R.id.txt_valor_abono);
        ncredito = (TextView) this.findViewById(R.id.txt_nc_cliente);
        ng1      = (TextView) this.findViewById(R.id.txt_ng_cliente);
        chkFact  = (CheckBox) this.findViewById(R.id.chk_fac);
        btCobro  = (Button)   this.findViewById(R.id.btn_generar_cobro);
        titulo   = (TextView) this.findViewById(R.id.tvTit);

        cod.setText(cd + " - " + nom);

        if(dato.equals("RC")){
            titulo.setText(tf);
            nc1 = loadNcCobro();
            ng2 = loadNgCobro();
            new TaskGetFacturasCobro().execute();
        }else if(dato.equals("RK")){
            titulo.setText(tf);
            nc1 = loadNcCheque();
            ng2 = loadNgCheque();
            new TaskGetFacturasCheque().execute();
        }else if(dato.equals("RQ")){
            titulo.setText(tf);
            nc1 = loadNcChequeP();
            ng2 = loadNgChequeP();
            new TaskGetFacturasChequeP().execute();
        }else if(dato.equals("RT")){
            titulo.setText(tr);
            new TaskGetFacturasRetenciones().execute();
        }

        ncredito.setText(String.valueOf(Utils.Redondear(nc1,2)));
        ng1.setText(String.valueOf(Utils.Redondear(ng2,2)));

        onBackPressed();

        btCobro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCobro();
            }
        });
    }

    private void doCobro(){
        if(Double.valueOf(abonos.getText().toString())==0 && !dato.equals("RT")){
            CharSequence text = "EL VALOR A ABONAR TIENE QUE SER MAYOR A 0.00 ...!!";
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
        }
        if(dato.equals("RT") && contador==0){
            CharSequence text = "SELECCIONE UNA FACTURA PARA APLICAR RETENCION ...!!";
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
        }
        validaSeleccion();
    }

    @Override
    public void onBackPressed() {
    }

    void sumar(){
        sum=.0;
        aux = new ArrayList<RowFactura>();
        for (int i = 0; i < list2.size(); i++) {
            RowFactura o = list2.get(i);
                lista = new RowFactura();
                lista.setCod_mov(o.getCod_mov());
                lista.setNum_mov(o.getNum_mov());
                lista.setCod_ref(o.getCod_ref());
                lista.setCod_ven(o.getCod_ven());
                lista.setCod_pto(o.getCod_pto());
                lista.setFec_emis(o.getFec_emis());
                lista.setFec_venc(o.getFec_venc());
                lista.setSdo_mov(o.getSdo_mov());
                lista.setVal_mov(o.getVal_mov());
                lista.setIva(o.getIva());
                lista.setBase(o.getBase());
                lista.setSts_mov(o.getSts_mov());
                lista.setCod_bco(o.getCod_bco());
                lista.setAbono(o.getAbono());
                sum = sum + o.getAbono();
                aux.add(lista);

        }
        System.out.println("suma de abonos "+sum);
        ab = Utils.Redondear(sum,2);
        abonos.setText(String.valueOf(ab));
    }

    void calcular(){
        aux = new ArrayList<RowFactura>();
        for (int i = 0; i < list2.size(); i++) {
            RowFactura o = list2.get(i);
                lista = new RowFactura();
                lista.setCod_mov(o.getCod_mov());
                lista.setNum_mov(o.getNum_mov());
                lista.setCod_ref(o.getCod_ref());
                lista.setCod_ven(o.getCod_ven());
                lista.setCod_pto(o.getCod_pto());
                lista.setFec_emis(o.getFec_emis());
                lista.setFec_venc(o.getFec_venc());
                lista.setSdo_mov(o.getSdo_mov());
                lista.setVal_mov(o.getVal_mov());
                lista.setIva(o.getIva());
                lista.setBase(o.getBase());
                lista.setSts_mov(o.getSts_mov());
                lista.setCod_bco(o.getCod_bco());
                lista.setAbono(Double.valueOf(o.getAbono()));
                aux.add(lista);

        }
    }

    public class FacturaAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (list2 != null && list2.size() != 0) {
                return list2.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return list2.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            View rowView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.facturasvalor_list, parent,false);

                holder = new ViewHolder();
                holder.chkFact = (CheckBox) rowView.findViewById(R.id.chk_fac);
                holder.txtTipo = (TextView) rowView.findViewById(R.id.txt_cod_mov);
                holder.txtFactura = (TextView) rowView.findViewById(R.id.txt_num_factura);
                holder.txtFch_emi = (TextView) rowView.findViewById(R.id.txt_fecha_emision);
                holder.txtStatus = (TextView) rowView.findViewById(R.id.txt_status);
                holder.txtRelacion = (TextView) rowView.findViewById(R.id.txt_rel);
                holder.txtPorc = (TextView) rowView.findViewById(R.id.txt_porc);
                holder.txtValor = (TextView) rowView.findViewById(R.id.txt_valor_total);
                holder.txtValor_saldo = (TextView) rowView.findViewById(R.id.txt_valor_saldo);
                holder.edtAbono = (EditText) rowView.findViewById(R.id.et_abono);

                rowView.setTag(holder);
            }else {
                rowView = convertView;
                holder = (ViewHolder) rowView.getTag();
            }
            holder.ref = position;
            RowFactura item = list2.get(position);

            // Set data into the view.

            holder.chkFact.setChecked(item.isSeleccion());
            holder.txtTipo.setText(item.getCod_mov());
            holder.txtFactura.setText(item.getNum_mov());
            holder.txtFch_emi.setText(item.getFec_emis());
            holder.txtStatus.setText(item.getSts_mov());
            holder.txtRelacion.setText(item.getNum_rel().toString());
            holder.txtPorc.setText("-");
            holder.txtValor.setText(String.valueOf(item.getVal_mov()));
            holder.txtValor_saldo.setText(String.valueOf(item.getSdo_mov()));

            holder.chkFact.setChecked(true);
            holder.chkFact.setEnabled(false);
            holder.chkFact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.chkFact.isChecked()) {
                        list2.get(position).setSeleccion(true);
                        holder.edtAbono.setText("");
                        holder.edtAbono.setId(position);
                        holder.edtAbono.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                            public void onFocusChange(View v, boolean hasFocus) {

                                if (!hasFocus) {
                                    final int position = v.getId();
                                    final EditText Caption = (EditText) v;
                                    list2.get(position).setAbono(Double.valueOf(Caption.getText().toString()));

                                }

                            }

                        });
                        holder.edtAbono.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                Double val = .0;
                                try {
                                    val = new Double(s.toString());
                                    //sumar();
                                } catch (NumberFormatException e) {
                                    val = .0;
                                }
                                RowFactura it = list2.get(holder.ref);
                                it.setAbono(val);
                                list2.set(holder.ref, it);
                                //sumar();
                            }
                        });
                    } else {
                        list2.get(position).setSeleccion(false);
                        list2.get(position).setAbono(0.00);

                    }
                }

            });

            /*holder.edtAbono.setText("");
            holder.edtAbono.setId(position);
            holder.edtAbono.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {

                    if (!hasFocus) {
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        list2.get(position).setAbono(Double.valueOf(Caption.getText().toString()));

                    }

                }

            });*/

            /*if(dato.equals("RT")){
                holder.edtAbono.setText("");
                holder.edtAbono.setEnabled(false);
            }else{
                holder.edtAbono.setText("");
                holder.edtAbono.setId(position);
            }


            holder.edtAbono.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {

                    if (!hasFocus) {
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        list2.get(position).setAbono(Double.valueOf(Caption.getText().toString()));

                    }

                }

            });*/

            return rowView;
        }

        private class ViewHolder {
            CheckBox chkFact;
            TextView txtTipo;
            TextView txtFactura;
            TextView txtFch_emi;
            TextView txtStatus;
            TextView txtRelacion;
            TextView txtPorc;
            TextView txtValor;
            TextView txtValor_saldo;
            EditText edtAbono;
            int      ref;
        }
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

    public class TaskGetFacturasCobro extends AsyncTask<String, Void, String>{
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Cargando datos..");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
            System.out.println("pasooo2 onPreExecute");
        }
        @Override
        protected String doInBackground(String... params) {
            String TAG="TaskGetFacturasCobros";
            try {
                items = new ArrayList<RowFactura>();
                double par = 0.00;
                for(RowFactura row: list2){
                        String cod_emp = row.getCod_emp();
                        String cod_mov = row.getCod_mov();
                        String cod_pto  = row.getCod_pto();
                        String cod_ref = row.getCod_ref();
                        String cod_ven = row.getCod_ven();
                        int dias = row.getDias_pendiente();
                        String emi = Utils.ConvertirDateToStringShort(row.getFec_emi());
                        //Date emi = Utils.ConvertirShortStringToDate(fch_emi);
                        String ven = Utils.ConvertirDateToStringShort(row.getFec_ven());
                        //Date ven = Utils.ConvertirShortStringToDate(fch_ven);
                        String num_mov = row.getNum_mov();
                        String num_rel = row.getNum_rel();
                        double sdo_mov = row.getSdo_mov();
                        double val_mov = row.getVal_mov();
                        double iva = row.getIva();
                        double base = row.getBase();
                        String est = row.getSts_mov();
                        String cdb = row.getCod_bco();
                        String ncta = row.getNum_cuenta();
                        boolean sel = false;
                        boolean sel2 = false;
                        double ab = row.getAbono();
                        par = sdo_mov;
                        parcial1 = parcial1 + par;
                        items.add(new RowFactura(cod_emp,cod_mov,cod_pto,cod_ref,cod_ven,dias,emi,ven,num_mov,
                                                 num_rel,sdo_mov,val_mov,iva,base,est,cdb,ncta,sel,sel2,ab));
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            } finally {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String a) {
            if(pDialog != null)
                pDialog.dismiss();
            valores.setText(Utils.Redondear(parcial1,2).toString());
            FacturaAdapter myListAdapter = new FacturaAdapter();
            listView.setAdapter(myListAdapter);
        }

    }

    public double loadNcCobro(){
        SQLiteDatabase db = null;
        Cursor q = null;
        double nc = 0.00;
        try {
            db = dbSql.getReadableDatabase();
            String[] args = new String[] {cd};
            String SQL ="SELECT SUM(sdo_mov) FROM DOC_WS WHERE cod_ref=? AND cod_ven='"+list.getCod_vend()+"' AND cod_mov='NC'";
            items = new ArrayList<RowFactura>();
            q = db.rawQuery(SQL,args);
            if (q.moveToFirst()){
                do {
                    nc = q.getDouble(0);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return nc;
    }

    public double loadNgCobro(){
        SQLiteDatabase db = null;
        Cursor q = null;
        double nc = 0.00;
        try {
            db = dbSql.getReadableDatabase();
            String[] args = new String[] {cd};
            String SQL ="SELECT SUM(sdo_mov) FROM DOC_WS WHERE cod_ref=? AND cod_ven='"+list.getCod_vend()+"' AND cod_mov='NG'";
            items = new ArrayList<RowFactura>();
            q = db.rawQuery(SQL,args);
            if (q.moveToFirst()){
                do {
                    nc = q.getDouble(0);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return nc;
    }

    public class TaskGetFacturasCheque extends AsyncTask<String, Void, String>{
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Cargando datos..");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
            System.out.println("pasooo2 onPreExecute");
        }
        @Override
        protected String doInBackground(String... params) {
            String TAG="TaskGetFacturasCheques";
            try {
                items = new ArrayList<RowFactura>();
                double par = 0.00;
                for(RowFactura row: list2){
                    String cod_emp = row.getCod_emp();
                    String cod_mov = row.getCod_mov();
                    String cod_pto  = row.getCod_pto();
                    String cod_ref = row.getCod_ref();
                    String cod_ven = row.getCod_ven();
                    int dias = row.getDias_pendiente();
                    String emi = Utils.ConvertirDateToStringShort(row.getFec_emi());
                    //Date emi = Utils.ConvertirShortStringToDate(fch_emi);
                    String ven = Utils.ConvertirDateToStringShort(row.getFec_ven());
                    //Date ven = Utils.ConvertirShortStringToDate(fch_ven);
                    String num_mov = row.getNum_mov();
                    String num_rel = row.getNum_rel();
                    double sdo_mov = row.getSdo_mov();
                    double val_mov = row.getVal_mov();
                    double iva = row.getIva();
                    double base = row.getBase();
                    String est = row.getSts_mov();
                    String cdb = row.getCod_bco();
                    String ncta = row.getNum_cuenta();
                    boolean sel = false;
                    boolean sel2 = false;
                    double ab = row.getAbono();
                    par = sdo_mov;
                    parcial1 = parcial1 + par;
                    items.add(new RowFactura(cod_emp,cod_mov,cod_pto,cod_ref,cod_ven,dias,emi,ven,num_mov,
                                             num_rel,sdo_mov,val_mov,iva,base,est,cdb,ncta,sel,sel2,ab));
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            } finally {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String a) {
            if(pDialog != null)
                pDialog.dismiss();
            valores.setText(Utils.Redondear(parcial1,2).toString());
            FacturaAdapter myListAdapter = new FacturaAdapter();
            listView.setAdapter(myListAdapter);
        }

    }

    public double loadNcCheque(){
        SQLiteDatabase db = null;
        Cursor q = null;
        double nc = 0.00;
        try {
            db = dbSql.getReadableDatabase();
            String[] args = new String[] {cd};
            String SQL ="SELECT SUM(sdo_mov) FROM DOC_WS WHERE cod_ref=? AND cod_ven='"+list.getCod_vend()+"' AND cod_mov='NC'";
            items = new ArrayList<RowFactura>();
            q = db.rawQuery(SQL,args);
            if (q.moveToFirst()){
                do {
                    nc = q.getDouble(0);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return nc;
    }

    public double loadNgCheque(){
        SQLiteDatabase db = null;
        Cursor q = null;
        double nc = 0.00;
        try {
            db = dbSql.getReadableDatabase();
            String[] args = new String[] {cd};
            String SQL ="SELECT SUM(sdo_mov) FROM DOC_WS WHERE cod_ref=? AND cod_ven='"+list.getCod_vend()+"' AND cod_mov='NG'";
            items = new ArrayList<RowFactura>();
            q = db.rawQuery(SQL,args);
            if (q.moveToFirst()){
                do {
                    nc = q.getDouble(0);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return nc;
    }

    public class TaskGetFacturasChequeP extends AsyncTask<String, Void, String>{
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Cargando datos..");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
            System.out.println("pasooo2 onPreExecute");
        }
        @Override
        protected String doInBackground(String... params) {
            String TAG="TaskGetFacturasCheques";
            try {
                items = new ArrayList<RowFactura>();
                double par = 0.00;
                for(RowFactura row: list2){
                    String cod_emp = row.getCod_emp();
                    String cod_mov = row.getCod_mov();
                    String cod_pto  = row.getCod_pto();
                    String cod_ref = row.getCod_ref();
                    String cod_ven = row.getCod_ven();
                    int dias = row.getDias_pendiente();
                    String emi = Utils.ConvertirDateToStringShort(row.getFec_emi());
                    //Date emi = Utils.ConvertirShortStringToDate(fch_emi);
                    String ven = Utils.ConvertirDateToStringShort(row.getFec_ven());
                    //Date ven = Utils.ConvertirShortStringToDate(fch_ven);
                    String num_mov = row.getNum_mov();
                    String num_rel = row.getNum_rel();
                    double sdo_mov = row.getSdo_mov();
                    double val_mov = row.getVal_mov();
                    double iva = row.getIva();
                    double base = row.getBase();
                    String est = row.getSts_mov();
                    String cdb = row.getCod_bco();
                    String ncta = row.getNum_cuenta();
                    boolean sel = false;
                    boolean sel2 = false;
                    double ab = row.getAbono();
                    par = sdo_mov;
                    parcial1 = parcial1 + par;
                    items.add(new RowFactura(cod_emp,cod_mov,cod_pto,cod_ref,cod_ven,dias,emi,ven,num_mov,
                                             num_rel,sdo_mov,val_mov,iva,base,est,cdb,ncta,sel,sel2,ab));
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            } finally {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String a) {
            if(pDialog != null)
                pDialog.dismiss();
            valores.setText(Utils.Redondear(parcial1,2).toString());
            FacturaAdapter myListAdapter = new FacturaAdapter();
            listView.setAdapter(myListAdapter);
        }

    }

    public double loadNcChequeP(){
        SQLiteDatabase db = null;
        Cursor q = null;
        double nc = 0.00;
        try {
            db = dbSql.getReadableDatabase();
            String[] args = new String[] {cd};
            String SQL ="SELECT SUM(sdo_mov) FROM DOC_WS WHERE cod_ref=? AND cod_ven='"+list.getCod_vend()+"' AND cod_mov='NC'";
            items = new ArrayList<RowFactura>();
            q = db.rawQuery(SQL,args);
            if (q.moveToFirst()){
                do {
                    nc = q.getDouble(0);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return nc;
    }

    public double loadNgChequeP(){
        SQLiteDatabase db = null;
        Cursor q = null;
        double nc = 0.00;
        try {
            db = dbSql.getReadableDatabase();
            String[] args = new String[] {cd};
            String SQL ="SELECT SUM(sdo_mov) FROM DOC_WS WHERE cod_ref=? AND cod_ven='"+list.getCod_vend()+"' AND cod_mov='NG'";
            items = new ArrayList<RowFactura>();
            q = db.rawQuery(SQL,args);
            if (q.moveToFirst()){
                do {
                    nc = q.getDouble(0);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return nc;
    }

    public class TaskGetFacturasRetenciones extends AsyncTask<String, Void, String>{
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Cargando datos..");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
            System.out.println("pasooo2 onPreExecute");
        }
        @Override
        protected String doInBackground(String... params) {
            String TAG="TaskGetFacturasCheques";
            SQLiteDatabase db = null;
            Cursor q = null;
            try {
                db = dbSql.getReadableDatabase();
                String[] args = new String[] {cd};
                String SQL ="SELECT * FROM DOC_WS WHERE cod_ref=? AND cod_ven='"+list.getCod_vend()+"' AND sdo_mov!=0 AND base>0 AND iva>0 AND cod_mov='FC' " +
                            "AND (num_rel='000000' OR num_rel='') ORDER BY cod_mov, fec_emi, num_mov";
                items = new ArrayList<RowFactura>();
                q = db.rawQuery(SQL,args);
                double par = 0.00;
                if (q.moveToFirst()){
                    do {
                        String cod_emp = q.getString(1);
                        String cod_mov = q.getString(2);
                        String cod_pto  = q.getString(3);
                        String cod_ref = q.getString(4);
                        String cod_ven = q.getString(5);
                        int dias = q.getInt(6);
                        String emi = q.getString(7);
                        String ven = q.getString(8);
                        String num_mov = q.getString(9);
                        String num_rel = q.getString(10);
                        double sdo_mov = q.getDouble(11);
                        double val_mov = q.getDouble(12);
                        double iva = q.getDouble(13);
                        double base = q.getDouble(14);
                        String est = q.getString(15);
                        String cdb = q.getString(16);
                        String ncta = q.getString(17);
                        boolean sel = false;
                        boolean sel2 = false;
                        double ab = q.getDouble(11);
                        par = sdo_mov;
                        parcial1 = parcial1 + par;
                        items.add(new RowFactura(cod_emp,cod_mov,cod_pto,cod_ref,cod_ven,dias,emi,ven,num_mov,
                                                 num_rel,sdo_mov,val_mov,iva,base,est,cdb,ncta,sel,sel2,ab));
                    }while (q.moveToNext());
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            } finally {
                q.close();
                db.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String a) {
            if(pDialog != null)
                pDialog.dismiss();
            DecimalFormat pre = new DecimalFormat("0.00");
            valores.setText(pre.format(parcial1));
            FacturaAdapter myListAdapter = new FacturaAdapter();
            listView.setAdapter(myListAdapter);
        }

    }

    public void validaSeleccion(){
        try {
            if (listView != null) {
                FacturaAdapter adapter = (FacturaAdapter) listView.getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    RowFactura rows = (RowFactura) adapter.getItem(i);
                    if (rows.isSeleccion()) {
                        if (rows.getAbono() > Math.abs(rows.getSdo_mov())) {
                            CharSequence text = "EL VALOR A ABONAR NO PUEDE SER MAYOR A EL SALDO DE LA FACTURA SELECCIONADA ....!!";
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
                        }

                    }
                }
                if(dato.equals("RT")){
                    calcular();
                    if(contador>1){
                        CharSequence text = "SOLO PUEDE SELECCIONAR UNA FACTURA PARA REALIZAR LA RETENCION ....!!";
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
                        Intent intent = new Intent(getApplicationContext(), CobroRetActivity.class);
                        intent.putExtra("nomb", nom);
                        intent.putExtra("cod", cd);
                        intent.putExtra("persona",pers);
                        intent.putExtra("abono", ab);
                        intent.putExtra("cliente",lista.getCod_ref());
                        intent.putExtra("vendedor",lista.getCod_ven());
                        intent.putExtra("titulo",tf);
                        intent.putExtra("titulo1",tr);
                        intent.putParcelableArrayListExtra("aux", aux);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("item", list);
                        intent.putExtra("dato", dato);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                }else{
                    calcular();
                    Intent intent = new Intent(getApplicationContext(), CobroActivity.class);
                    intent.putExtra("nomb", nom);
                    intent.putExtra("cod", cd);
                    intent.putExtra("persona",pers);
                    intent.putExtra("abono", ab);
                    intent.putExtra("cliente",lista.getCod_ref());
                    intent.putExtra("vendedor",lista.getCod_ven());
                    intent.putExtra("titulo",tf);
                    intent.putExtra("titulo1",tr);
                    intent.putParcelableArrayListExtra("aux", aux);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("item", list);
                    intent.putExtra("dato", dato);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.mGenerar:
                doCobro();
                return true;
            case R.id.mVolver:
                Intent intent = new Intent(getApplicationContext(), FacturasActivity.class);
                intent.putExtra("nomb_cli", nom);
                intent.putExtra("cod_cli", cd);
                intent.putExtra("persona", pers);
                Bundle bundle = new Bundle();
                bundle.putParcelable("item", list);
                intent.putExtras(bundle);
                intent.putExtra("dato",dato);
                intent.putExtra("titulo", tf);
                intent.putExtra("titulo1", tr);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
