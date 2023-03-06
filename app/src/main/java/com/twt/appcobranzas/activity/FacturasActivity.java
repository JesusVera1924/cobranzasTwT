package com.twt.appcobranzas.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.model.Cobros_Doc;
import com.twt.appcobranzas.model.RowFactura;
import com.twt.appcobranzas.model.Vendedor;
import com.twt.appcobranzas.utils.DbHelper;
import com.twt.appcobranzas.utils.Utils;

/**
 * Created by DEVECUA on 11/12/2015.
 */
@SuppressWarnings("deprecation")
public class FacturasActivity extends AppCompatActivity {

    String TAG="FacturasActivity";
    ListView listView;
    Context  context;
    TextView cod, valores, val, abonos, ncredito, ng1, titulo, numerof;
    CheckBox chkFact;
    Spinner  vendedor;
    Button   btCobro;
    DbHelper dbSql =null;

    String cd, nom, dato, pers;
    Cobros_Doc micd;
    String tf, tr, aut, faut1, codaux;
    Date fac, fax, faut;
    Vendedor list;
    double parcial1 = 0.00, ab, nc1, ng2;
    int contador = 0, numd = 0;
    int contador2 = 0;
    boolean res = true;
    private Double sum;
    ArrayList<RowFactura> items, aux;
    RowFactura lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facturas);
        context = this;
        dbSql = new DbHelper(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        sum = .0;
        cd = getIntent().getExtras().getString("cod_cli");
        nom = getIntent().getExtras().getString("nomb_cli");
        pers = getIntent().getExtras().getString("persona");
        codaux = getIntent().getExtras().getString("codaux");
        dato = getIntent().getExtras().getString("dato");
        tf = getIntent().getExtras().getString("titulo");
        tr = getIntent().getExtras().getString("titulo1");
        aut = getIntent().getExtras().getString("aut");
        faut1 = getIntent().getExtras().getString("faut");
        System.out.println("fecha :: "+faut1);
        Bundle bundle = getIntent().getExtras();
        list = bundle.getParcelable("item");

        //widget
        listView = (ListView) this.findViewById(R.id.lsv_lista_facturas);
        cod      = (TextView) this.findViewById(R.id.txt_cliente);
        val      = (TextView) this.findViewById(R.id.txt_valor_total);
        valores  = (TextView) this.findViewById(R.id.txt_valor);
        abonos   = (TextView) this.findViewById(R.id.txt_valor_abono);
        ncredito = (TextView) this.findViewById(R.id.txt_nc_cliente);
        ng1      = (TextView) this.findViewById(R.id.txt_ng_cliente);
        numerof  = (TextView) this.findViewById(R.id.txt_factu);
        chkFact  = (CheckBox) this.findViewById(R.id.chk_fac);
        btCobro  = (Button)   this.findViewById(R.id.btn_generar_cobro);
        titulo   = (TextView) this.findViewById(R.id.tvTit);

        cod.setText(cd + " - " + nom);
        micd = new Cobros_Doc();


        /*if(dato.equals("RC")){
            titulo.setText(tf);
            nc1 = loadNcCobro();
            ng2 = loadNgCobro();
            new TaskGetFacturasCobro().execute();
        }else*/
        if(dato.equals("RK")){
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
        for (int i = 0; i < items.size(); i++) {
            RowFactura o = items.get(i);
            if (o.isSeleccion()){
                sum = sum + o.getAbono();
                lista = new RowFactura();
                lista.setCod_mov(o.getCod_mov());
                lista.setNum_mov(o.getNum_mov());
                lista.setCod_ref(o.getCod_ref());
                lista.setCod_ven(o.getCod_ven());
                lista.setCod_pto(o.getCod_pto());
                lista.setFec_emis(o.getFec_emis());
                lista.setFec_venc(o.getFec_venc());
                lista.setNum_rel(o.getNum_rel());
                lista.setSdo_mov(o.getSdo_mov());
                lista.setVal_mov(o.getVal_mov());
                lista.setIva(o.getIva());
                lista.setBase(o.getBase());
                lista.setSts_mov(o.getSts_mov());
                lista.setCod_bco(o.getCod_bco());
                lista.setNum_cuenta(o.getNum_cuenta());
                lista.setAbono(o.getAbono());
                aux.add(lista);
            }

        }
        ab = Utils.Redondear(sum,2);
        abonos.setText(String.valueOf(ab));
    }

    void calcular(){
        aux = new ArrayList<RowFactura>();
        for (int i = 0; i < items.size(); i++) {
            RowFactura o = items.get(i);
            if (o.isSeleccion()){
                lista = new RowFactura();
                lista.setCod_mov(o.getCod_mov());
                lista.setNum_mov(o.getNum_mov());
                lista.setCod_ref(o.getCod_ref());
                lista.setCod_ven(o.getCod_ven());
                lista.setCod_pto(o.getCod_pto());
                lista.setFec_emis(o.getFec_emis());
                lista.setFec_venc(o.getFec_venc());
                lista.setNum_rel(o.getNum_rel());
                lista.setSdo_mov(o.getSdo_mov());
                lista.setVal_mov(o.getVal_mov());
                lista.setIva(o.getIva());
                lista.setBase(o.getBase());
                lista.setSts_mov(o.getSts_mov());
                lista.setCod_bco(o.getCod_bco());
                lista.setNum_cuenta(o.getNum_cuenta());
                lista.setAbono(o.getAbono());
                aux.add(lista);
            }
        }
    }

    public class FacturaAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
        return items.get(position);
        }

        @Override
        public long getItemId(int position) {
        return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.facturasvalor_list, null);

                holder = new ViewHolder();
                holder.chkFact = (CheckBox) convertView.findViewById(R.id.chk_fac);
                holder.txtTipo = (TextView) convertView.findViewById(R.id.txt_cod_mov);
                holder.txtFactura = (TextView) convertView.findViewById(R.id.txt_num_factura);
                holder.txtFch_emi = (TextView) convertView.findViewById(R.id.txt_fecha_emision);
                holder.txtStatus = (TextView) convertView.findViewById(R.id.txt_status);
                holder.txtRelacion = (TextView) convertView.findViewById(R.id.txt_rel);
                holder.txtPorc = (TextView) convertView.findViewById(R.id.txt_porc);
                holder.txtValor = (TextView) convertView.findViewById(R.id.txt_valor_total);
                holder.txtValor_saldo = (TextView) convertView.findViewById(R.id.txt_valor_saldo);
                holder.edtAbono = (EditText) convertView.findViewById(R.id.et_abono);
                holder.txtAbono = (TextView) convertView.findViewById(R.id.txt_abono);

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.ref = position;
            RowFactura item = items.get(position);

            // Set data into the view.
            holder.chkFact.setChecked(item.isSeleccion());
            holder.txtTipo.setText(item.getCod_mov());
            holder.txtFactura.setText(item.getNum_mov());
            if(item.getCod_mov().equals("DC") || item.getCod_mov().equals("CH") || item.getCod_mov().equals("DB")){
                holder.txtFch_emi.setText(item.getFec_venc());
            }else if(item.getCod_mov().equals("FC")){
                holder.txtFch_emi.setText(item.getFec_emis());
            }

            holder.txtStatus.setText(item.getSts_mov());
            holder.txtRelacion.setText(item.getNum_rel().trim().toString());
            holder.txtPorc.setText("-");
            holder.txtValor.setText(String.valueOf(item.getVal_mov()));
            holder.txtValor_saldo.setText(String.valueOf(item.getSdo_mov()));
            holder.txtAbono.setText(String.valueOf(item.getSdo_mov()));

            if (item.isSeleccion()) {
                holder.txtAbono.setVisibility(View.VISIBLE);
            } else {
                holder.txtAbono.setVisibility(View.INVISIBLE);
            }


            holder.chkFact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.chkFact.isChecked()) {
                        if (dato.equals("RT")) {
                            numerof.setVisibility(View.INVISIBLE);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = null;
                            try {
                                date = formatter.parse(holder.txtFch_emi.getText().toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar c = Calendar.getInstance();
                            c.setTime(date);
                            fax = c.getTime();
                            fac = new Date();
                            numd = numeroDiasEntreDosFechas(fax, fac);

                            String cod1 = holder.txtTipo.getText().toString();
                            String num1 = holder.txtFactura.getText().toString();
                            res = validar(cod1, num1);

                            /*if(numd >= 65){
                                contador++;
                                Toast toast = Toast.makeText(getBaseContext(), "Factura no puede aplicar retencion, se excede del tiempo.", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                                toast.show();
                                return;
                            }*/

                            if(res){
                                Toast toast = Toast.makeText(getBaseContext(), "Factura no puede seleccionarse, ya esta incluida en un RC", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                                toast.show();
                                holder.chkFact.setChecked(false);
                                holder.txtAbono.setText("");
                                contador = 0;
                            }else{
                                holder.txtAbono.setText("");
                                holder.txtAbono.setEnabled(false);
                                contador++;
                                contador2++;
                            }
                        }
                        items.get(position).setSeleccion(true);
                        holder.txtAbono.setVisibility(View.VISIBLE);
                        contador2++;
                        numerof.setText(String.valueOf(contador2));
                        sumar();
                    } else {
                        if (dato.equals("RT")) {
                            if(res){
                                Toast toast = Toast.makeText(getBaseContext(), "Factura no puede seleccionarse, ya esta incluida en un RC", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                                toast.show();
                                holder.chkFact.setChecked(false);
                                holder.txtAbono.setText("");
                                contador = 0;
                            }else{
                                holder.txtAbono.setText("");
                                holder.txtAbono.setEnabled(false);
                                contador--;
                                contador2--;
                            }
                        }
                        items.get(position).setSeleccion(false);
                        holder.txtAbono.setVisibility(View.INVISIBLE);
                        contador2--;
                        numerof.setText(String.valueOf(contador2));
                        sumar();
                    }
                }

            });

            holder.txtAbono.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final RowFactura it = items.get(holder.ref);
                    // custom dialog
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.abono);
                    dialog.setTitle("Ingreso Abono - "+it.getCod_mov()+" "+it.getNum_mov());

                    // set the custom dialog components - text, image and button
                    TextView text1 = (TextView) dialog.findViewById(R.id.txtAb);
                    final EditText etext = (EditText) dialog.findViewById(R.id.etAb);

                    Button dialogButton = (Button) dialog.findViewById(R.id.abonoButtonOK);
                    etext.setText(String.valueOf(it.getSdo_mov()));

                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            holder.txtAbono.setText(etext.getText());

                            if(etext.getText().toString().isEmpty()){
                                Toast toast = Toast.makeText(getBaseContext(), "No puede estar vacio el campo ABONO..!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                                toast.show();
                                holder.txtAbono.setText(String.valueOf(it.getSdo_mov()));
                                it.setAbono(it.getSdo_mov());
                                return;
                            }

                            //RowFactura it = items.get(holder.ref);
                            it.setAbono(Double.valueOf(etext.getText().toString()));
                            if(it.getAbono()>it.getSdo_mov()){
                                Toast toast = Toast.makeText(getBaseContext(), "El abono de $"+it.getAbono()+" no puede ser mayor al SALDO..!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                                toast.show();
                                holder.txtAbono.setText(String.valueOf(it.getSdo_mov()));
                                it.setAbono(it.getSdo_mov());
                                return;
                            }
                            if(it.getAbono()==0){
                                Toast toast = Toast.makeText(getBaseContext(), "No puede ABONAR $0..!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                                toast.show();
                                holder.txtAbono.setText(String.valueOf(it.getSdo_mov()));
                                it.setAbono(it.getSdo_mov());
                                return;
                            }
                            items.set(holder.ref, it);
                            sumar();

                            double aux = Double.valueOf(holder.txtAbono.getText().toString());
                            if(aux==it.getSdo_mov()){

                            }else {
                                CharSequence text = "CAMBIO EFECTUADO ...!!";
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
                        }
                    });

                    dialog.show();
                }
            });

            holder.txtAbono.setText(String.valueOf(items.get(holder.ref).getAbono()));
            holder.txtAbono.setId(position);

            holder.txtAbono.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        int position = v.getId();
                        final TextView Caption = (TextView) v;
                        try {
                            items.get(position).setAbono(Double.valueOf(Caption.getText().toString()));
                        } catch (NumberFormatException e) {
                            items.get(position).setAbono(.0);
                            e.printStackTrace();
                        }
                    }

                }

            });


            return convertView;
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
            TextView txtAbono;
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
            SQLiteDatabase db = null;
            String SQL = "";
            Cursor q = null;
            try {
                db = dbSql.getReadableDatabase();
                String[] args = new String[] {cd};
                if(list.getCod_vend().equals("V999")) {
                    SQL = "SELECT * FROM DOC_WS " +
                            "WHERE com_emp='" + list.getCod_emp() + "' AND cod_ref=? AND sdo_mov>0 " +
                            "AND (cod_mov='FC' OR cod_mov='DC' OR cod_mov='ND' " +
                            "OR cod_mov='CR') ORDER BY cod_mov, fec_emi, num_mov";
                }else if(list.getSeleccion().equals("V")) {
                    SQL = "SELECT * FROM DOC_WS " +
                            "WHERE com_emp='" + list.getCod_emp() + "' AND cod_ref=? AND sdo_mov>0 " +
                            "AND (cod_mov='FC' OR cod_mov='DC' OR cod_mov='ND' " +
                            "OR cod_mov='CR') ORDER BY cod_mov, fec_emi, num_mov";
                }else{
                    SQL = "SELECT * FROM DOC_WS " +
                            "WHERE com_emp='" + list.getCod_emp() + "' AND cod_ref=? AND cod_ven='" + list.getCod_vend() + "' AND sdo_mov>0 " +
                            "AND (cod_mov='FC' OR cod_mov='DC' OR cod_mov='ND' " +
                            "OR cod_mov='CR') ORDER BY cod_mov, fec_emi, num_mov";
                }
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
                        String num_rel = q.getString(10).trim();
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
                //db.close();
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
        String SQL = "";
        double nc = 0.00;
        try {
            db = dbSql.getReadableDatabase();
            String[] args = new String[] {cd};
            if(list.getCod_vend().equals("V999")) {
                SQL = "SELECT SUM(sdo_mov) FROM DOC_WS " +
                        "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref=? AND cod_mov='NC'";
            }else if(list.getSeleccion().equals("V")) {
                SQL = "SELECT SUM(sdo_mov) FROM DOC_WS " +
                        "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref=? AND cod_mov='NC'";
            }else{
                SQL = "SELECT SUM(sdo_mov) FROM DOC_WS " +
                        "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref=? AND cod_ven='" + list.getCod_vend() + "' AND cod_mov='NC'";
            }

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
            String SQL = "";
            if(list.getCod_vend().equals("V999")) {
                SQL = "SELECT SUM(sdo_mov) FROM DOC_WS " +
                        "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND cod_mov='NG'";
            }else if(list.getSeleccion().equals("V")) {
                SQL = "SELECT SUM(sdo_mov) FROM DOC_WS " +
                        "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND cod_mov='NG'";
            }else{
                SQL = "SELECT SUM(sdo_mov) FROM DOC_WS " +
                      "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND cod_ven='"+list.getCod_vend()+"' AND cod_mov='NG'";
            }
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
            SQLiteDatabase db = null;
            Cursor q = null;
            try {
                db = dbSql.getReadableDatabase();
                String[] args = new String[] {cd};
                String SQL = "";
                if(list.getCod_vend().equals("V999")) {
                    SQL ="SELECT * FROM DOC_WS " +
                            "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND sdo_mov!=0 " +
                            "AND ((cod_mov='CH' AND (sts_mov='G' OR sts_mov='C')) OR cod_mov='CR') " +
                            "ORDER BY cod_mov, fec_emi, num_mov";
                }else if(list.getSeleccion().equals("V")) {
                    SQL ="SELECT * FROM DOC_WS " +
                            "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND cod_ven='"+codaux+"' AND sdo_mov!=0 " +
                            "AND ((cod_mov='CH' AND (sts_mov='G' OR sts_mov='C')) OR cod_mov='CR') " +
                            "ORDER BY cod_mov, fec_emi, num_mov";
                }else{
                    SQL ="SELECT * FROM DOC_WS " +
                            "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND cod_ven='"+list.getCod_vend()+"' AND sdo_mov!=0 " +
                            "AND ((cod_mov='CH' AND (sts_mov='G' OR sts_mov='C')) OR cod_mov='CR') " +
                            "ORDER BY cod_mov, fec_emi, num_mov";
                }
                items = new ArrayList<RowFactura>();
                q = db.rawQuery(SQL, args);
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
                        String num_rel = q.getString(10).trim();
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
                //db.close();
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
            String SQL = "";
            if(list.getCod_vend().equals("V999")) {
                SQL ="SELECT SUM(sdo_mov) FROM DOC_WS " +
                        "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND cod_mov='NC'";
            }else if(list.getSeleccion().equals("V")) {
                SQL = "SELECT SUM(sdo_mov) FROM DOC_WS " +
                      "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND cod_ven='"+codaux+"' AND cod_mov='NC'";
            }else{
                SQL ="SELECT SUM(sdo_mov) FROM DOC_WS " +
                        "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND cod_ven='"+list.getCod_vend()+"' AND cod_mov='NC'";
            }

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
            String SQL = "";
            if(list.getCod_vend().equals("V999")) {
                SQL ="SELECT SUM(sdo_mov) FROM DOC_WS " +
                        "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND cod_mov='NG'";
            }else if(list.getSeleccion().equals("V")) {
                SQL ="SELECT SUM(sdo_mov) FROM DOC_WS " +
                        "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND cod_ven='"+codaux+"' AND cod_mov='NG'";
            }else{
                SQL ="SELECT SUM(sdo_mov) FROM DOC_WS " +
                        "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND cod_ven='"+list.getCod_vend()+"' AND cod_mov='NG'";
            }
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
            SQLiteDatabase db = null;
            Cursor q = null;
            try {
                db = dbSql.getReadableDatabase();
                String[] args = new String[] {cd};
                String SQL = "";
                if(list.getCod_vend().equals("V999")) {
                    SQL ="SELECT * FROM DOC_WS " +
                            "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND sdo_mov!=0 " +
                            "AND ((cod_mov='CH' AND NOT (sts_mov='G' OR sts_mov='C')) OR cod_mov='CR' " +
                            "OR cod_mov='DB') ORDER BY cod_mov, fec_emi, num_mov";
                }else if(list.getSeleccion().equals("V")) {
                    SQL ="SELECT * FROM DOC_WS " +
                            "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND cod_ven='"+codaux+"' AND sdo_mov!=0 " +
                            "AND ((cod_mov='CH' AND NOT (sts_mov='G' OR sts_mov='C')) OR cod_mov='CR' " +
                            "OR cod_mov='DB') ORDER BY cod_mov, fec_emi, num_mov";
                }else{
                    SQL ="SELECT * FROM DOC_WS " +
                            "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND cod_ven='"+list.getCod_vend()+"' AND sdo_mov!=0 " +
                            "AND ((cod_mov='CH' AND NOT (sts_mov='G' OR sts_mov='C')) OR cod_mov='CR' " +
                            "OR cod_mov='DB') ORDER BY cod_mov, fec_emi, num_mov";
                }
                items = new ArrayList<RowFactura>();
                q = db.rawQuery(SQL, args);
                double par = 0.00;
                if (q.moveToFirst()){
                    do {
                        String cod_emp = q.getString(1);
                        String cod_mov = q.getString(2);
                        String cod_pto  = q.getString(3);
                        String cod_ref = q.getString(                                                                4);
                        String cod_ven = q.getString(5);
                        int dias = q.getInt(6);
                        String emi = q.getString(7);
                        String ven = q.getString(8);
                        String num_mov = q.getString(9);
                        String num_rel = q.getString(10).trim();
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
                //db.close();
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
            String SQL = "";
            if(list.getCod_vend().equals("V999")) {
                SQL = "SELECT SUM(sdo_mov) FROM DOC_WS " +
                        "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref=? AND cod_mov='NC'";
            }else if(list.getSeleccion().equals("V")) {
                SQL = "SELECT SUM(sdo_mov) FROM DOC_WS " +
                        "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref=? AND cod_ven='" + codaux + "' AND cod_mov='NC'";
            }else {
                SQL = "SELECT SUM(sdo_mov) FROM DOC_WS " +
                      "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref=? AND cod_ven='" + list.getCod_vend() + "' AND cod_mov='NC'";
            }
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
            String SQL = "";
            if(list.getCod_vend().equals("V999")) {
                SQL = "SELECT SUM(sdo_mov) FROM DOC_WS " +
                        "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref=? AND cod_mov='NG'";
            }else if(list.getSeleccion().equals("V999")) {
                SQL = "SELECT SUM(sdo_mov) FROM DOC_WS " +
                        "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref=? AND cod_ven='" + codaux + "' AND cod_mov='NG'";
            }else {
                SQL = "SELECT SUM(sdo_mov) FROM DOC_WS " +
                      "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref=? AND cod_ven='" + list.getCod_vend() + "' AND cod_mov='NG'";
            }
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
                String SQL = "";
                if(list.getCod_vend().equals("V999")) {
                    SQL = "SELECT * FROM DOC_WS " +
                            "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref=? AND sdo_mov!=0 " +
                            "AND base>0 AND iva>0 AND cod_mov='FC' " +
                            "AND (num_rel='000000' OR num_rel='') ORDER BY cod_mov, fec_emi, num_mov";
                }else if(list.getSeleccion().equals("V")) {
                    SQL = "SELECT * FROM DOC_WS " +
                            "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref=? AND cod_ven='" + codaux + "' AND sdo_mov!=0 " +
                            "AND base>0 AND iva>0 AND cod_mov='FC' " +
                            "AND (num_rel='000000' OR num_rel='') ORDER BY cod_mov, fec_emi, num_mov";
                }else {
                    SQL = "SELECT * FROM DOC_WS " +
                            "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref=? AND cod_ven='" + list.getCod_vend() + "' AND sdo_mov!=0 " +
                            "AND base>0 AND iva>0 AND cod_mov='FC' " +
                            "AND (num_rel='000000' OR num_rel='') ORDER BY cod_mov, fec_emi, num_mov";
                }
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
                //db.close();
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

    public Cobros_Doc cobrosDoc(String cod, String num){
        SQLiteDatabase db = null;
        Cursor q = null;
        Cobros_Doc v = new Cobros_Doc();
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM COBROS_DOC " +
                        "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_mov='"+cod.toUpperCase().trim()+"' AND num_mov='"+num.trim()
                       +"' AND tipo_cobro='RC' AND estado=0";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    v.setCod_emp(q.getString(1));
                    v.setId_recibo(q.getInt(2));
                    v.setFch_emision(q.getString(3));
                    v.setCod_cli(q.getString(4));
                    v.setNom_cli(q.getString(5));
                    v.setCod_vend(q.getString(6));
                    v.setCod_vin(q.getString(7));
                    v.setVendedor2(q.getString(8));
                    v.setCod_mov(q.getString(9));
                    v.setNum_mov(q.getString(10));
                    v.setFch_emi(q.getString(11));
                    v.setFch_ven(q.getString(12));
                    v.setTipo_cobro(q.getString(13));
                    v.setNum_doc(q.getString(14));
                    v.setCod_bco(q.getString(15));
                    v.setNum_cuenta(q.getString(16));
                    v.setValor(q.getDouble(17));
                    v.setPto_vta(q.getString(18));
                    v.setEstado(q.getInt(19));
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            //db.close();
        }
        return v;
    }

    public boolean validar(String cod, String num){
        boolean result = true;
        micd = cobrosDoc(cod, num);
        if(micd.getId_recibo()==0){
            result = false;
        }else{
            if(micd.getCod_mov().equals(cod) && micd.getNum_mov().equals(num)){
                result = true;
            }else{
                result = false;
            }
        }
        return result;
    }

    public static int numeroDiasEntreDosFechas(Date fecha1, Date fecha2){
        long startTime = fecha1.getTime();
        long endTime = fecha2.getTime();
        long diffTime = endTime - startTime;
        long diffDays = diffTime / (1000 * 60 * 60 * 24);
        return (int)diffDays;
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
                        intent.putExtra("aut", aut);
                        intent.putExtra("faut", faut1);
                        intent.putExtra("codaux", codaux);
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
                    intent.putExtra("codaux", codaux);
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
            case R.id.mFacturas:
                doCobro();
                return true;
            case R.id.mVolver:
                Intent intent = new Intent(getApplicationContext(), ClientesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("lista", list);
                intent.putExtras(bundle);
                intent.putExtra("codigo",cd);
                intent.putExtra("codaux",codaux);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
