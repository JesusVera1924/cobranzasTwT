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
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.model.RowFactura;
import com.twt.appcobranzas.model.Vendedor;
import com.twt.appcobranzas.utils.DbHelper;
import com.twt.appcobranzas.utils.Utils;

/**
 * Created by DEVECUA on 11/12/2015.
 */
@SuppressWarnings("deprecation")
public class FacturasNotasActivity extends AppCompatActivity {

    String TAG="FacturasNotasActivity";
    ListView listView;
    Context context;
    ArrayList<RowFactura> items, aux;
    RowFactura lista;
    ArrayList<RowFactura> ncredito;
    TextView cod, valores, val, abonos, titulo;
    CheckBox chkFact;
    DbHelper dbSql =null;
    String cd, nom, pers, dato, cod_cli, cod_ven;
    String tnc1, tnc2, tcobro, numero, vinc, codaux;
    Vendedor list;
    Double parcial1 = 0.00, ab, i1, i10, saldo, abono, val1, val2;
    int sec, recibo;
    private Double sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facturas_notas);
        context = this;
        dbSql = new DbHelper(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        sum = .0;
        vinc = "";
        cd = getIntent().getExtras().getString("cod");
        nom = getIntent().getExtras().getString("nomb");
        pers = getIntent().getExtras().getString("persona");
        codaux = getIntent().getExtras().getString("codaux");
        dato = getIntent().getExtras().getString("dato");
        ab = getIntent().getExtras().getDouble("abono");
        cod_cli = getIntent().getExtras().getString("cliente");
        cod_ven = getIntent().getExtras().getString("vendedor");
        tnc1 = getIntent().getExtras().getString("titulo2");
        tnc2 = getIntent().getExtras().getString("titulo3");
        Bundle bundle = getIntent().getExtras();
        list = bundle.getParcelable("item");
        ncredito = getIntent().getParcelableArrayListExtra("aux");
        i1 = .0; i10 = .0; saldo = .0; abono = .0;

        //widget
        listView = (ListView) this.findViewById(R.id.lsv_lista_facturas);
        cod      = (TextView) this.findViewById(R.id.txt_cliente);
        val      = (TextView) this.findViewById(R.id.txt_valor_total);
        valores  = (TextView) this.findViewById(R.id.txt_valor);
        abonos   = (TextView) this.findViewById(R.id.txt_valor_abono);
        chkFact  = (CheckBox) this.findViewById(R.id.chk_fac);
        titulo   = (TextView) this.findViewById(R.id.tvTit);

        cod.setText(cd + " - " + nom);
        if(ab<0){
            double temp = ab*(-1);
            valores.setText(String.valueOf(temp));
        }else{
            valores.setText(String.valueOf(ab));
        }

        if(dato.equals("NC")){
            titulo.setText(tnc1);
            new TaskGetFacturas().execute();
            tcobro = "NC";
        }else if(dato.equals("NG")){
            titulo.setText(tnc2);
            new TaskGetFacturas().execute();
            tcobro = "NG";
        }else if(dato.equals("NA")){
            titulo.setText(tnc2);
            new TaskGetFacturas().execute();
            tcobro = "NA";
        }

        for(RowFactura row: ncredito){
            numero = row.getNum_mov();
            if(dato.equals("NA"))
                vinc = row.getCod_vin();
            else
                vinc = "";
        }

        onBackPressed();
    }

    private void doCobro(){
        if(Double.valueOf(valores.getText().toString())<Double.valueOf(abonos.getText().toString()) || Double.valueOf(valores.getText().toString())>Double.valueOf(abonos.getText().toString())){
            CharSequence text = "NO CUADRAN LOS VALORES DE LAS(s) NC SELECCIONADA(s) CON EL VALOR A ABONAR ...!!";
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
        if(Double.valueOf(abonos.getText().toString())==0){
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
                lista.setCod_vin(o.getCod_vin());
                lista.setCod_pto(o.getCod_pto());
                lista.setFec_emis(o.getFec_emis());
                lista.setFec_venc(o.getFec_venc());
                lista.setSdo_mov(o.getSdo_mov());
                lista.setVal_mov(o.getVal_mov());
                lista.setIva(o.getIva());
                lista.setBase(o.getBase());
                lista.setSts_mov(o.getSts_mov());
                lista.setCod_bco(o.getCod_bco());
                lista.setNum_cuenta(o.getNum_cuenta());
                lista.setAbono(Double.valueOf(o.getAbono()));
                aux.add(lista);
            }

        }
        if(ab<0){
            double temp = ab-(-1);
            ab = temp;
            ab = Utils.Redondear(sum,2);
        }else{
            ab = Utils.Redondear(sum,2);
        }
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
                lista.setCod_vin(o.getCod_vin());
                lista.setCod_pto(o.getCod_pto());
                lista.setFec_emis(o.getFec_emis());
                lista.setFec_venc(o.getFec_venc());
                lista.setSdo_mov(o.getSdo_mov());
                lista.setVal_mov(o.getVal_mov());
                lista.setIva(o.getIva());
                lista.setBase(o.getBase());
                lista.setSts_mov(o.getSts_mov());
                lista.setCod_bco(o.getCod_bco());
                lista.setNum_cuenta(o.getNum_cuenta());
                lista.setAbono(Double.valueOf(o.getAbono()));
                aux.add(lista);
            }

        }
    }

    public class FacturaAdapter extends BaseAdapter {

        //public Double iva1;
        //public Double iva10;

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
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.facturasvalor_list, parent, false);

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
            //iva1 = .0; iva10 = .0;
            RowFactura item = items.get(position);

            // Set data into the view.
            /*holder.chkFact.setChecked(item.isSeleccion());
            holder.txtTipo.setText(item.getCod_mov());
            holder.txtFactura.setText(item.getNum_mov());
            if(item.getCod_mov().equals("DC")){
                holder.txtFch_emi.setText(item.getFec_venc());
                holder.txtRelacion.setText("");
                holder.txtPorc.setText("");
            }else if(item.getCod_mov().equals("CH")){
                holder.txtFch_emi.setText(item.getFec_venc());
                holder.txtRelacion.setText("");
                holder.txtPorc.setText("");
            }else if(item.getCod_mov().equals("DB")){
                holder.txtFch_emi.setText(item.getFec_venc());
                holder.txtRelacion.setText("");
                holder.txtPorc.setText("");
            }else if(item.getCod_mov().equals("FC")){
                holder.txtRelacion.setText(item.getNum_rel().trim());
                holder.txtFch_emi.setText(item.getFec_emis());
            }

            if(pers.equals("0")){

            }else{
                if(item.getNum_rel().trim().equals("000000")) {
                    holder.txtRelacion.setTextColor(getResources().getColor(R.color.colorAccent));
                    holder.txtPorc.setTextColor(getResources().getColor(R.color.colorAccent));
                }else{
                    holder.txtRelacion.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    holder.txtPorc.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }

            holder.txtValor.setText(String.valueOf(item.getVal_mov()));
            holder.txtValor_saldo.setText(String.valueOf(Utils.Redondear(item.getSdo_mov(),2)));
            holder.txtAbono.setText(String.valueOf(Utils.Redondear(item.getSdo_mov(),2)));*/

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
                        items.get(position).setSeleccion(true);
                        holder.txtAbono.setVisibility(View.VISIBLE);
                        sumar();

                        /*val1 = Double.valueOf(abonos.getText().toString());
                        val2 = Double.valueOf(valores.getText().toString());

                        if(val1>val2){
                            Toast toast = Toast.makeText(getBaseContext(), "No puede exceder el valor seleccionado de FC al valor seleccionado de NC..!", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }

                        sumar();*/

                    } else {
                        items.get(position).setSeleccion(false);
                        holder.txtAbono.setVisibility(View.INVISIBLE);
                        items.get(position).setAbono(0.00);
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

    public class TaskGetFacturas extends AsyncTask<String, Void, String>{
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
            String TAG="TaskGetFacturas";
            SQLiteDatabase db = null;
            Cursor q = null;
            try {
                db = dbSql.getReadableDatabase();
                String[] args = new String[] {cd};

                /******** OTROS VENDEDORES *********/
                /*String SQL ="SELECT * FROM DOC_WS WHERE cod_ref=? AND sdo_mov>0 AND cod_ven='"+cod_ven+"' " +
                            "ORDER BY cod_mov, fec_ven, num_mov";*/

                /******** DAVID V001-V012-V017 *********/
                String SQL = "SELECT * FROM DOC_WS WHERE cod_ref=? AND sdo_mov>0 " +
                             "ORDER BY cod_mov, fec_ven, num_mov";

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

                        saldo = sdo_mov;
                        abono = saldo;

                        par = saldo;
                        parcial1 = parcial1 + par;
                        items.add(new RowFactura(cod_emp,cod_mov,cod_pto,cod_ref,cod_ven,dias,emi,ven,num_mov,
                                                 num_rel,saldo,val_mov,iva,base,est,cdb,ncta,sel,sel2,abono));
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
            FacturaAdapter myListAdapter = new FacturaAdapter();
            listView.setAdapter(myListAdapter);
            System.out.println("mi lista " + listView.getAdapter().getCount());
        }

    }

    public void validaSeleccion(){
        try {
            if(listView.getAdapter().getCount()==0){
                CharSequence text = "NO SE PUEDE APLICAR DEBIDO A QUE NO HAY FACTURAS, REGRESE AL MENU ANTERIOR ....!!";
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
                calcular();
                setCobros(ncredito);
                recibo = getCountSecRecibos();
                setCobrosDoc(aux);
                updateFacturas(aux);
                if(dato.equals("NC")){
                    updateNotasCredito(ncredito);
                }else if(dato.equals("NG")){
                    updateNotasCreditoInterna(ncredito);
                }else if(dato.equals("NA")){
                    updateNotasCreditoInternaApp(ncredito);
                }
                updateCobrosPagosNc(ncredito);

                Intent intent = new Intent(getApplicationContext(), NotasCreditoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("item", list);
                intent.putExtras(bundle);
                intent.putExtra("cod_cli", cd);
                intent.putExtra("nomb_cli", nom);
                intent.putExtra("dato", dato);
                intent.putExtra("persona", pers);
                intent.putExtra("codaux", codaux);
                intent.putExtra("titulo2", tnc1);
                intent.putExtra("titulo3", tnc2);
                startActivity(intent);
                CharSequence text = "EL PAGO SE REALIZO SATISFACTORIAMENTE ....!!";
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateNotasCreditoInterna(List<RowFactura> lista){
        List<RowFactura> result = lista;
        double saldo = 0.00;
        SQLiteDatabase db = null;
        try {
            String SQL = "";
            for (RowFactura row : result) {
                saldo = 0.00;
                DecimalFormat pre = new DecimalFormat("0.00");
                if (row.getSdo_mov() < 0)
                    saldo = row.getSdo_mov() + row.getAbono();
                else
                    saldo = row.getSdo_mov() - row.getAbono();
                double s = Utils.Redondear(saldo, 2);

                if(list.getCod_vend().equals("V999")){
                    SQL = "UPDATE DOC_WS SET sdo_mov=" + s + " " +
                            "WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+list.getCod_emp()+"'";
                }else if(list.getSeleccion().equals("V") && list.getCod_vend()!=row.getCod_ven()){
                    SQL = "UPDATE DOC_WS SET sdo_mov=" + s + " " +
                            "WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+list.getCod_emp()+"' AND cod_ven='" + row.getCod_ven() + "'";
                }else{
                    SQL = "UPDATE DOC_WS SET sdo_mov=" + s + " " +
                            "WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+list.getCod_emp()+"' AND cod_ven='" + list.getCod_vend() + "'";
                }
                /*if(list.getCod_vend().equals("V001") || list.getCod_vend().equals("V012") || list.getCod_vend().equals("V017"))
                    SQL = "UPDATE DOC_WS SET sdo_mov=" + s + " " +
                          "WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+list.getCod_emp()+"'";
                else
                    SQL = "UPDATE DOC_WS SET sdo_mov=" + s + " " +
                          "WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+list.getCod_emp()+"' AND cod_ven='" + list.getCod_vend() + "'";
                */
                Log.e("updateF",SQL);
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }finally {
            db.close();
        }
    }

    public void updateNotasCredito(List<RowFactura> lista){
        List<RowFactura> result = lista;
        double saldo = 0.00;
        SQLiteDatabase db = null;
        try {
            String SQL = "";
            for (RowFactura row : result) {
                saldo = 0.00;
                DecimalFormat pre = new DecimalFormat("0.00");
                if(row.getSdo_mov()<0)
                    saldo = row.getSdo_mov()+row.getAbono();
                else
                    saldo = row.getSdo_mov()-row.getAbono();
                double s = Utils.Redondear(saldo,2);

                if(list.getCod_vend().equals("V999")){
                    SQL = "UPDATE DOC_WS SET sdo_mov=" + s + " " +
                            "WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+list.getCod_emp()+"'";
                }else if(list.getSeleccion().equals("V") && list.getCod_vend()!=row.getCod_ven()){
                    SQL = "UPDATE DOC_WS SET sdo_mov=" + s + " " +
                            "WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+list.getCod_emp()+"' AND cod_ven='"+row.getCod_ven()+"'";
                }else{
                    SQL = "UPDATE DOC_WS SET sdo_mov=" + s + " " +
                            "WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+list.getCod_emp()+"' AND cod_ven='"+list.getCod_vend()+"'";
                }
                /*if(list.getCod_vend().equals("V001") || list.getCod_vend().equals("V012") || list.getCod_vend().equals("V017"))
                    SQL = "UPDATE DOC_WS SET sdo_mov=" + s + " " +
                          "WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+list.getCod_emp()+"'";
                else
                    SQL = "UPDATE DOC_WS SET sdo_mov=" + s + " " +
                          "WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+list.getCod_emp()+"' AND cod_ven='"+list.getCod_vend()+"'";
                */
                Log.e("updateF",SQL);
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }finally {
            db.close();
        }
    }

    public void updateNotasCreditoInternaApp(List<RowFactura> lista){
        List<RowFactura> result = lista;
        double saldo = 0.00;
        SQLiteDatabase db = null;
        try {
            for (RowFactura row : result) {
                saldo = 0.00;
                DecimalFormat pre = new DecimalFormat("0.00");
                if(row.getSdo_mov()<0)
                    saldo = row.getSdo_mov()+row.getAbono();
                else
                    saldo = row.getSdo_mov()-row.getAbono();
                double s = Utils.Redondear(saldo,2);
                String SQL = "UPDATE DOC_LOCAL SET sdo_mov=" + s + " WHERE num_mov='" + row.getNum_mov() + "' " +
                             "AND cod_emp='"+list.getCod_emp()+"' AND cod_ven='"+list.getCod_vend()+"'";
                Log.e("updateF",SQL);
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }finally {
            db.close();
        }
    }

    public void updateCobrosPagosNc(List<RowFactura> lista){
        List<RowFactura> result = lista;
        String SQL = "";
        SQLiteDatabase db = null;
        try {
            for (RowFactura row : result) {
                if(row.getSdo_mov()==row.getAbono()){
                    SQL = "UPDATE COBROS_PAGOS SET status='-' " +
                          "WHERE cheque='" + row.getNum_mov() + "' AND cod_emp='"+list.getCod_emp()+"'";
                }else {
                    SQL = "UPDATE COBROS_PAGOS SET status='P' " +
                          "WHERE cheque='" + row.getNum_mov() + "' AND cod_emp='"+list.getCod_emp()+"'";
                }
                Log.e("updateNc",SQL);
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }finally {
            db.close();
        }

    }

    public void updateFacturas(List<RowFactura> lista){
        List<RowFactura> result = lista;
        double saldo = 0.00;
        SQLiteDatabase db = null;
        try {
            String SQL = "";
            for (RowFactura row : result) {
                saldo = 0.00;
                DecimalFormat pre = new DecimalFormat("0.00");
                if(row.getSdo_mov()<0)
                    saldo = row.getSdo_mov()+row.getAbono();
                else
                    saldo = row.getSdo_mov()-row.getAbono();
                double s = Utils.Redondear(saldo,2);

                if(list.getCod_vend().equals("V999")){
                    SQL = "UPDATE DOC_WS SET sdo_mov=" + s + " " +
                            "WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+list.getCod_emp()+"' AND cod_ven='"+cod_ven+"'";
                }else if(list.getSeleccion().equals("V") && list.getCod_vend()!=row.getCod_ven()){
                    SQL = "UPDATE DOC_WS SET sdo_mov=" + s + " " +
                            "WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+list.getCod_emp()+"' AND cod_ven='"+codaux+"'";
                }else{
                    SQL = "UPDATE DOC_WS SET sdo_mov=" + s + " " +
                            "WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+list.getCod_emp()+"' AND cod_ven='"+list.getCod_vend()+"'";
                }

                /*if(list.getCod_vend().equals("V001") || list.getCod_vend().equals("V012") || list.getCod_vend().equals("V017"))
                    SQL = "UPDATE DOC_WS SET sdo_mov=" + s + " " +
                          "WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+list.getCod_emp()+"'";
                else
                    SQL = "UPDATE DOC_WS SET sdo_mov=" + s + " " +
                          "WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+list.getCod_emp()+"' AND cod_ven='"+list.getCod_vend()+"'";
                */
                Log.e("updateF",SQL);
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }finally {
            db.close();
        }
    }

    public void setCobrosDoc(List<RowFactura> lista){
        List<RowFactura> result = lista;
        SQLiteDatabase db = null;
        String SQL = "";
        try {
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = dateFormat.format(date);
            for (RowFactura row : result) {
                if (row.getCod_mov().equals("FC")) {

                }else if(row.getCod_mov().equals("CH")){

                }
                if(list.getCod_vend().equals("V999")){
                    SQL = "INSERT INTO COBROS_DOC (cod_emp,id_recibo,fch_emision,cod_cli,nom_cli,cod_vend,cod_vin,vendedor,cod_mov,num_mov," +
                            "fch_emi,fch_ven,tipo_cobro,num_doc,cod_bco,num_cuenta,valor,pto_vta,estado) VALUES " +
                            "('" +list.getCod_emp()+ "','" + sec + "',DateTime('now','localtime'),'" + cod_cli + "','" + nom + "','" + list.getCod_vend() +
                            "','" + vinc + "','" + cod_ven + "','" +row.getCod_mov() + "','" + row.getNum_mov() + "','" +
                            formattedDate+"','" + formattedDate + "','" + tcobro + "','" + numero + "','" + row.getCod_bco() + "','" +
                            row.getNum_cuenta()+ "','" + row.getAbono() + "','" + row.getCod_pto() + "','" + 0 + "')";
                }else if(list.getSeleccion().equals("V") && list.getCod_vend()!=row.getCod_ven()){
                    SQL = "INSERT INTO COBROS_DOC (cod_emp,id_recibo,fch_emision,cod_cli,nom_cli,cod_vend,cod_vin,vendedor,cod_mov,num_mov," +
                            "fch_emi,fch_ven,tipo_cobro,num_doc,cod_bco,num_cuenta,valor,pto_vta,estado) VALUES " +
                            "('" +list.getCod_emp()+ "','" + sec + "',DateTime('now','localtime'),'" + cod_cli + "','" + nom + "','" + list.getCod_vend() +
                            "','" + vinc + "','" + cod_ven + "','" +row.getCod_mov() + "','" + row.getNum_mov() + "','" +
                            formattedDate+"','" + formattedDate + "','" + tcobro + "','" + numero + "','" + row.getCod_bco() + "','" +
                            row.getNum_cuenta()+ "','" + row.getAbono() + "','" + row.getCod_pto() + "','" + 0 + "')";
                }else{
                    SQL = "INSERT INTO COBROS_DOC (cod_emp,id_recibo,fch_emision,cod_cli,nom_cli,cod_vend,cod_vin,vendedor,cod_mov,num_mov," +
                            "fch_emi,fch_ven,tipo_cobro,num_doc,cod_bco,num_cuenta,valor,pto_vta,estado) VALUES " +
                            "('" +list.getCod_emp()+ "','" + sec + "',DateTime('now','localtime'),'" + cod_cli + "','" + nom + "','" + list.getCod_vend() +
                            "','" + vinc + "','','" +row.getCod_mov() + "','" + row.getNum_mov() + "','" +
                            formattedDate+"','" + formattedDate + "','" + tcobro + "','" + numero + "','" + row.getCod_bco() + "','" +
                            row.getNum_cuenta()+ "','" + row.getAbono() + "','" + row.getCod_pto() + "','" + 0 + "')";
                }

                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
            if(recibo>0)
                setSecuencia("RNC");
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }finally {
            db.close();
        }
    }

    public void setCobros(List<RowFactura> lista) {
        List<RowFactura> result = lista;
        String SQL = "";
        SQLiteDatabase db = null;
        try {
            sec = getMaxRec();
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = dateFormat.format(date);
            for (RowFactura row : result) {
                if(list.getCod_vend().equals("V999")){
                    SQL = "INSERT INTO COBROS_PAGOS (cod_emp,id_recibo,fpago,banco,cuenta,cheque,fch_pago,valor,cod_ref,cod_vend,cod_vin,vendedor," +
                            "fch_registro,girador,cod_rec,status) VALUES " +
                            "('" +list.getCod_emp()+ "','" + sec + "','"+tcobro+"','-','"+numero+"','-','"+formattedDate+"','" + row.getAbono() +
                            "','" + cod_cli + "','" + list.getCod_vend() + "','" + row.getCod_vin() + "','" + cod_ven +
                            "',DateTime('now','localtime'),'-','"+tcobro+"','-')";
                }else if(list.getSeleccion().equals("V") && list.getCod_vend()!=row.getCod_ven()){
                    SQL = "INSERT INTO COBROS_PAGOS (cod_emp,id_recibo,fpago,banco,cuenta,cheque,fch_pago,valor,cod_ref,cod_vend,cod_vin,vendedor," +
                            "fch_registro,girador,cod_rec,status) VALUES " +
                            "('" +list.getCod_emp()+ "','" + sec + "','"+tcobro+"','-','"+numero+"','-','"+formattedDate+"','" + row.getAbono() +
                            "','" + cod_cli + "','" + row.getCod_ven() + "','" + row.getCod_vin() + "','" + cod_ven +
                            "',DateTime('now','localtime'),'-','"+tcobro+"','-')";
                }else{
                    SQL = "INSERT INTO COBROS_PAGOS (cod_emp,id_recibo,fpago,banco,cuenta,cheque,fch_pago,valor,cod_ref,cod_vend,cod_vin,vendedor," +
                            "fch_registro,girador,cod_rec,status) VALUES " +
                            "('" +list.getCod_emp()+ "','" + sec + "','"+tcobro+"','-','"+numero+"','-','"+formattedDate+"','" + row.getAbono() +
                            "','" + cod_cli + "','" + cod_ven + "','" + row.getCod_vin() + "','',DateTime('now','localtime'),'-','"+tcobro+"','-')";
                }
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }finally {
            db.close();
        }
    }

    public void getNotaCreditoAplicada(List<RowFactura> lista){
        List<RowFactura> result = lista;
        double sal = .0;
        SQLiteDatabase db = null;
        try {
            for (RowFactura row : result) {
                sal = .0;
                sal = row.getSdo_mov()+row.getAbono();
                String SQL = "INSERT INTO DOC_LOCAL (cod_emp,cod_mov,cod_pto,cod_ref,cod_ven,dias_pend,fec_emi,fec_ven,num_mov,num_rel,sdo_mov,val_mov,iva,base,sts_mov,cod_bco,num_cuenta) VALUES (" +
                        "'"+list.getCod_emp()+"','"+row.getCod_mov()+"','02','" + row.getCod_ref() + "','" + row.getCod_ven() + "'," + 0 + ",'"+row.getFec_emis()+"','"+row.getFec_venc()+"','" + row.getNum_mov() + "','" + sec + "','" +
                        Utils.Redondear(sal, 2) + "','" + row.getAbono() + "','" + row.getIva() + "','" + row.getBase() + "','" + row.getSts_mov() + "','"+row.getCod_bco().trim()+"','"+row.getNum_cuenta()+"')";
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }finally {
            db.close();
        }
    }

    public void setSecuencia(String id){
        int s = sec+1;
        SQLiteDatabase db = null;
        try {
            String SQL = "UPDATE SECUENCIA SET secuencia="+s+" " +
                         "WHERE tipo='"+id+"' AND cod_emp='"+list.getCod_emp()+"' AND cod_ven='"+list.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }finally {
            db.close();
        }
    }

    public int getMaxRec(){
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT MAX(secuencia) FROM SECUENCIA " +
                        "WHERE tipo='RNC' AND cod_emp='"+list.getCod_emp()+"' AND cod_ven='"+list.getCod_vend()+"'";
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

    public int getCountSecRecibos(){
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT COUNT(*) FROM COBROS_PAGOS " +
                        "WHERE fpago='NC' OR fpago='NG' OR fpago='NA' AND cod_emp='"+list.getCod_emp()+"'";
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
                Intent intent = new Intent(getApplicationContext(), NotasCreditoActivity.class);
                intent.putExtra("titulo2", tnc1);
                intent.putExtra("titulo3",tnc2);
                intent.putExtra("nomb_cli", nom);
                intent.putExtra("cod_cli", cd);
                intent.putExtra("dato", dato);
                intent.putExtra("persona", pers);
                intent.putExtra("codaux",codaux);
                Bundle bundle = new Bundle();
                bundle.putParcelable("item", list);
                intent.putExtras(bundle);
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
