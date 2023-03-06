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

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.model.RowFactura;
import com.twt.appcobranzas.model.RowFacturaNC;
import com.twt.appcobranzas.model.Vendedor;
import com.twt.appcobranzas.utils.DbHelper;
import com.twt.appcobranzas.utils.Utils;

/**
 * Created by DEVECUA on 11/12/2015.
 */
@SuppressWarnings("deprecation")
public class NotasCreditoActivity extends AppCompatActivity {

    String TAG="NotasCreditoActivity";
    ListView listView;
    Context context;
    ArrayList<RowFactura> items, items1, aux;
    ArrayList<RowFacturaNC> items2;
    RowFactura lista;
    TextView cod, valores, val, abonos, titulo;
    CheckBox chkFact;
    Button btCobro;
    DbHelper dbSql =null;
    String cd, nom, pers, dato, cod_vin, codaux;
    String tnc1, tnc2;
    Vendedor list;
    double parcial1 = 0.00, parcialng1 = 0.00, parcialng2 = 0.00, ab;
    private Double sum;
    int contador=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ncredito);
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
        tnc1 = getIntent().getExtras().getString("titulo2");
        tnc2 = getIntent().getExtras().getString("titulo3");
        Bundle bundle = getIntent().getExtras();
        list = bundle.getParcelable("item");

        //FacturaAdapter myListAdapter = new FacturaAdapter();
        //widget
        listView = (ListView) this.findViewById(R.id.lsv_lista_facturas);
        cod      = (TextView) this.findViewById(R.id.txt_cliente);
        val      = (TextView) this.findViewById(R.id.txt_valor_total);
        valores  = (TextView) this.findViewById(R.id.txt_valor);
        abonos   = (TextView) this.findViewById(R.id.txt_valor_abono);
        chkFact  = (CheckBox) this.findViewById(R.id.chk_fac);
        btCobro  = (Button)   this.findViewById(R.id.btn_generar_cobro);
        titulo   = (TextView) this.findViewById(R.id.tvTit);

        cod.setText(cd + " - " + nom);
        cod_vin = getCodigoVinculado(cd);

        if(cod_vin == null || cod_vin.equals("")){
            cod_vin = "";
        }

        if(dato.equals("NC")){
            titulo.setText(tnc1);
            new TaskGetNotasCredito().execute();
        }else if(dato.equals("NG")){
            titulo.setText(tnc2);
            new TaskGetNotasCreditoInternasWS().execute();
        }else if(dato.equals("NA")){
            titulo.setText(tnc2);
            new TaskGetNotasCreditoInternasAPP().execute();
        }

        onBackPressed();

        btCobro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCobro();
            }
        });
    }

    private void doCobro(){
        if(Double.valueOf(abonos.getText().toString())==0){
            CharSequence text = "EL VALOR SELECCIONADO TIENE QUE SER MAYOR A 0.00 ...!!";
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
        if(contador==0){
            CharSequence text = "SELECCIONE UNA NOTA CREDITO PARA APLICAR EL COBRO ...!!";
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
        if(dato.equals("NC")){
            for (int i = 0; i < items.size(); i++) {
                RowFactura o = items.get(i);
                if (o.isSeleccion()){
                    sum = sum + o.getAbono();
                    lista = new RowFactura();
                    lista.setCod_mov(o.getCod_mov());
                    lista.setNum_mov(o.getNum_mov());
                    lista.setCod_ref(o.getCod_ref());
                    lista.setCod_ven(o.getCod_ven());
                    lista.setCod_vin("");
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
        }else if(dato.equals("NG")){
            for (int i = 0; i < items1.size(); i++) {
                RowFactura o = items1.get(i);
                if (o.isSeleccion()){
                    sum = sum + o.getAbono();
                    lista = new RowFactura();
                    lista.setCod_mov(o.getCod_mov());
                    lista.setNum_mov(o.getNum_mov());
                    lista.setCod_ref(o.getCod_ref());
                    lista.setCod_ven(o.getCod_ven());
                    lista.setCod_vin("");
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
        }else if(dato.equals("NA")){
            for (int i = 0; i < items2.size(); i++) {
                RowFacturaNC o = items2.get(i);
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
        }

        ab = Utils.Redondear(sum,2);
        abonos.setText(String.valueOf(ab));

    }

    void calcular(){
        aux = new ArrayList<RowFactura>();
        if(dato.equals("NC")){
            for (int i = 0; i < items.size(); i++) {
                RowFactura o = items.get(i);
                if (o.isSeleccion()){
                    lista = new RowFactura();
                    lista.setCod_mov(o.getCod_mov());
                    lista.setNum_mov(o.getNum_mov());
                    lista.setCod_ref(o.getCod_ref());
                    lista.setCod_ven(o.getCod_ven());
                    lista.setCod_vin("");
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
                    if(o.getAbono()<0){
                        double temp = o.getAbono()*(-1);
                        lista.setAbono(temp);
                    }else{
                        lista.setAbono(Double.valueOf(o.getAbono()));
                    }
                    aux.add(lista);
                }

            }
        }else if(dato.equals("NG")){
            for (int i = 0; i < items1.size(); i++) {
                RowFactura o = items1.get(i);
                if (o.isSeleccion()){
                    lista = new RowFactura();
                    lista.setCod_mov(o.getCod_mov());
                    lista.setNum_mov(o.getNum_mov());
                    lista.setCod_ref(o.getCod_ref());
                    lista.setCod_ven(o.getCod_ven());
                    lista.setCod_vin("");
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
                    if(o.getAbono()<0){
                        double temp = o.getAbono()*(-1);
                        lista.setAbono(temp);
                    }else{
                        lista.setAbono(Double.valueOf(o.getAbono()));
                    }
                    aux.add(lista);
                }

            }
        }else if(dato.equals("NA")){
            for (int i = 0; i < items2.size(); i++) {
                RowFacturaNC o = items2.get(i);
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
                    if(o.getAbono()<0){
                        double temp = o.getAbono()*(-1);
                        lista.setAbono(temp);
                    }else{
                        lista.setAbono(Double.valueOf(o.getAbono()));
                    }
                    aux.add(lista);
                }

            }
        }
    }

    public class FacturaAdapter extends BaseAdapter {
        public String aux1;

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
                //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //convertView = inflater.inflate(R.layout.facturasvalor_list, parent, false);
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
            holder.txtFch_emi.setText(item.getFec_emis());
            holder.txtStatus.setText(item.getSts_mov());
            holder.txtRelacion.setText(item.getNum_rel().toString());
            holder.txtPorc.setText("-");
            holder.txtValor.setText(String.valueOf(item.getVal_mov()));
            holder.txtValor_saldo.setText(String.valueOf(item.getSdo_mov()));
            holder.txtAbono.setText(String.valueOf(item.getSdo_mov()));

            /*if (item.isSeleccion()) {
                holder.edtAbono.setVisibility(View.VISIBLE);
            } else {
                holder.edtAbono.setVisibility(View.INVISIBLE);
            }*/
            if (item.isSeleccion()) {
                holder.txtAbono.setVisibility(View.VISIBLE);
            } else {
                holder.txtAbono.setVisibility(View.INVISIBLE);
            }

            holder.chkFact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final RowFactura it = items.get(holder.ref);
                    if (holder.chkFact.isChecked()) {
                        items.get(position).setSeleccion(true);
                        holder.txtAbono.setVisibility(View.VISIBLE);
                        contador++;

                        aux1 = holder.txtAbono.getText().toString().replace(",",".");
                        it.setAbono(Double.valueOf(aux1));
                        items.set(holder.ref, it);
                        sumar();
                    } else {
                        items.get(position).setSeleccion(false);
                        holder.txtAbono.setVisibility(View.INVISIBLE);
                        items.get(position).setAbono(0.00);
                        contador--;

                        aux1 = holder.txtAbono.getText().toString().replace(",",".");
                        it.setAbono(Double.valueOf(aux1));
                        items.set(holder.ref, it);
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
                            if(it.getAbono()==0){
                                Toast toast = Toast.makeText(getBaseContext(), "No puede ABONAR $0..!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                                toast.show();
                                holder.txtAbono.setText(String.valueOf(it.getSdo_mov()));
                                it.setAbono(it.getSdo_mov());
                                return;
                            }
                            if(it.getAbono()>0){
                                Toast toast = Toast.makeText(getBaseContext(), "Ingrese VALORES con signo NEGATIVO..!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                                toast.show();
                                holder.txtAbono.setText(String.valueOf(it.getSdo_mov()));
                                it.setAbono(it.getSdo_mov());
                                return;
                            }
                            if(it.getAbono()>it.getSdo_mov()){
                                //holder.txtAbono.setText(String.valueOf(it.getSdo_mov()));
                                //it.setAbono(it.getSdo_mov());
                                //return;
                            }else{
                                Toast toast = Toast.makeText(getBaseContext(), "El abono de $"+it.getAbono()+" no puede ser mayor al SALDO..!", Toast.LENGTH_LONG);
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

    public class FacturaAdapterNg extends BaseAdapter {

        @Override
        public int getCount() {
            return items1.size();
        }

        @Override
        public Object getItem(int position) {
            return items1.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //convertView = inflater.inflate(R.layout.facturasvalor_list, parent, false);
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
                //rowView = convertView;
                holder = (ViewHolder) convertView.getTag();
            }
            holder.ref = position;
            RowFactura item = items1.get(position);

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
            holder.txtAbono.setText(String.valueOf(item.getSdo_mov()));

            /*if (item.isSeleccion()) {
                holder.edtAbono.setVisibility(View.VISIBLE);
            } else {
                holder.edtAbono.setVisibility(View.INVISIBLE);
            }*/
            if (item.isSeleccion()) {
                holder.txtAbono.setVisibility(View.VISIBLE);
            } else {
                holder.txtAbono.setVisibility(View.INVISIBLE);
            }


            holder.chkFact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.chkFact.isChecked()) {
                        items1.get(position).setSeleccion(true);
                        holder.txtAbono.setVisibility(View.VISIBLE);
                        contador++;
                        sumar();
                    } else {
                        items1.get(position).setSeleccion(false);
                        holder.txtAbono.setVisibility(View.INVISIBLE);
                        items1.get(position).setAbono(0.00);
                        contador--;
                        sumar();
                    }
                }

            });

            /*holder.edtAbono.addTextChangedListener(new TextWatcher() {
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
                        sumar();
                    } catch (NumberFormatException e) {
                        val = .0;
                    }
                    RowFactura it = items1.get(holder.ref);
                    it.setAbono(val);
                    items1.set(holder.ref, it);
                    sumar();
                }
            });

            holder.edtAbono.setText(String.valueOf(items1.get(holder.ref).getAbono()));
            holder.edtAbono.setId(position);

            holder.edtAbono.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        int position = v.getId();
                        final EditText Caption = (EditText) v;
                        try {
                            items1.get(position).setAbono(Double.valueOf(Caption.getText().toString()));
                        } catch (NumberFormatException e) {
                            items1.get(position).setAbono(.0);
                            e.printStackTrace();
                        }
                    }

                }

            });*/

            holder.txtAbono.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final RowFactura it = items1.get(holder.ref);
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
                            if(it.getAbono()==0){
                                Toast toast = Toast.makeText(getBaseContext(), "No puede ABONAR $0..!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                                toast.show();
                                holder.txtAbono.setText(String.valueOf(it.getSdo_mov()));
                                it.setAbono(it.getSdo_mov());
                                return;
                            }
                            if(it.getAbono()>0){
                                Toast toast = Toast.makeText(getBaseContext(), "Ingrese VALORES con signo NEGATIVO..!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                                toast.show();
                                holder.txtAbono.setText(String.valueOf(it.getSdo_mov()));
                                it.setAbono(it.getSdo_mov());
                                return;
                            }
                            if(it.getAbono()>it.getSdo_mov()){
                                //holder.txtAbono.setText(String.valueOf(it.getSdo_mov()));
                                //it.setAbono(it.getSdo_mov());
                                //return;
                            }else{
                                Toast toast = Toast.makeText(getBaseContext(), "El abono de $"+it.getAbono()+" no puede ser mayor al SALDO..!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                                toast.show();
                                holder.txtAbono.setText(String.valueOf(it.getSdo_mov()));
                                it.setAbono(it.getSdo_mov());
                                return;
                            }
                            items1.set(holder.ref, it);
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

            holder.txtAbono.setText(String.valueOf(items1.get(holder.ref).getAbono()));
            holder.txtAbono.setId(position);

            holder.txtAbono.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        int position = v.getId();
                        final TextView Caption = (TextView) v;
                        try {
                            items1.get(position).setAbono(Double.valueOf(Caption.getText().toString()));
                        } catch (NumberFormatException e) {
                            items1.get(position).setAbono(.0);
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

    public class FacturaAdapterNgApp extends BaseAdapter {

        @Override
        public int getCount() {
            return items2.size();
        }

        @Override
        public Object getItem(int position) {
            return items2.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //convertView = inflater.inflate(R.layout.facturasvalor_list, parent, false);
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
            RowFacturaNC item = items2.get(position);

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
            holder.txtAbono.setText(String.valueOf(item.getSdo_mov()));

            /*if (item.isSeleccion()) {
                holder.edtAbono.setVisibility(View.VISIBLE);
            } else {
                holder.edtAbono.setVisibility(View.INVISIBLE);
            }*/
            if (item.isSeleccion()) {
                holder.txtAbono.setVisibility(View.VISIBLE);
            } else {
                holder.txtAbono.setVisibility(View.INVISIBLE);
            }

            holder.chkFact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.chkFact.isChecked()) {
                        items2.get(position).setSeleccion(true);
                        holder.txtAbono.setVisibility(View.VISIBLE);
                        contador++;
                        sumar();
                    } else {
                        items2.get(position).setSeleccion(false);
                        holder.txtAbono.setVisibility(View.INVISIBLE);
                        items2.get(position).setAbono(0.00);
                        contador--;
                        sumar();
                    }
                }

            });

            /*holder.edtAbono.addTextChangedListener(new TextWatcher() {
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
                        sumar();
                    } catch (NumberFormatException e) {
                        val = .0;
                    }
                    RowFactura it = items2.get(holder.ref);
                    it.setAbono(val);
                    items2.set(holder.ref, it);
                    sumar();
                }
            });

            holder.edtAbono.setText(String.valueOf(items2.get(holder.ref).getAbono()));
            holder.edtAbono.setId(position);

            holder.edtAbono.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        int position = v.getId();
                        final EditText Caption = (EditText) v;
                        try {
                            items2.get(position).setAbono(Double.valueOf(Caption.getText().toString()));
                        } catch (NumberFormatException e) {
                            items2.get(position).setAbono(.0);
                            e.printStackTrace();
                        }
                    }

                }

            });*/

            holder.txtAbono.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final RowFacturaNC it = items2.get(holder.ref);
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
                            if(it.getAbono()==0){
                                Toast toast = Toast.makeText(getBaseContext(), "No puede ABONAR $0..!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                                toast.show();
                                holder.txtAbono.setText(String.valueOf(it.getSdo_mov()));
                                it.setAbono(it.getSdo_mov());
                                return;
                            }
                            if(it.getAbono()>0){
                                Toast toast = Toast.makeText(getBaseContext(), "Ingrese VALORES con signo NEGATIVO..!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                                toast.show();
                                holder.txtAbono.setText(String.valueOf(it.getSdo_mov()));
                                it.setAbono(it.getSdo_mov());
                                return;
                            }
                            if(it.getAbono()>it.getSdo_mov()){
                                //holder.txtAbono.setText(String.valueOf(it.getSdo_mov()));
                                //it.setAbono(it.getSdo_mov());
                                //return;
                            }else{
                                Toast toast = Toast.makeText(getBaseContext(), "El abono de $"+it.getAbono()+" no puede ser mayor al SALDO..!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                                toast.show();
                                holder.txtAbono.setText(String.valueOf(it.getSdo_mov()));
                                it.setAbono(it.getSdo_mov());
                                return;
                            }
                            items2.set(holder.ref, it);
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

            holder.txtAbono.setText(String.valueOf(items2.get(holder.ref).getAbono()));
            holder.txtAbono.setId(position);

            holder.txtAbono.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        int position = v.getId();
                        final TextView Caption = (TextView) v;
                        try {
                            items2.get(position).setAbono(Double.valueOf(Caption.getText().toString()));
                        } catch (NumberFormatException e) {
                            items2.get(position).setAbono(.0);
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

    public class TaskGetNotasCredito extends AsyncTask<String, Void, String>{
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
            System.out.println("pasooo2 NC onPreExecute");
        }
        @Override
        protected String doInBackground(String... params) {
            String TAG="TaskGetNotasCredito";
            SQLiteDatabase db = null;
            Cursor q = null;
            String SQL = "";
            try {
                db = dbSql.getReadableDatabase();
                String[] args = new String[] {cd};
                if(list.getCod_vend().equals("V999")){
                    SQL = "SELECT * FROM DOC_WS " +
                          "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref = ? "+
                          " AND sdo_mov!=0 AND cod_mov='NC' " +
                          "ORDER BY cod_mov, fec_ven, num_mov";
                }else if(list.getSeleccion().equals("V")){
                    SQL = "SELECT * FROM DOC_WS " +
                          "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref = ? AND cod_ven='"+codaux+
                          "' AND sdo_mov!=0 AND cod_mov='NC' " +
                          "ORDER BY cod_mov, fec_ven, num_mov";
                }else{
                    SQL = "SELECT * FROM DOC_WS " +
                          "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND cod_ven='"+list.getCod_vend()+
                          "' AND sdo_mov!=0 AND cod_mov='NC' " +
                          "ORDER BY cod_mov, fec_ven, num_mov";
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
                        items.add(new RowFactura(cod_emp,cod_mov,cod_pto,cod_ref,cod_ven,dias,emi,ven,
                                                 num_mov,num_rel,sdo_mov,val_mov,iva,base,est,cdb,ncta,sel,sel2,ab));
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
            DecimalFormat pre = new DecimalFormat("0.00");
            valores.setText(pre.format(parcial1));
            FacturaAdapter myListAdapter = new FacturaAdapter();
            listView.setAdapter(myListAdapter);
        }

    }

    public class TaskGetNotasCreditoInternasWS extends AsyncTask<String, Void, String>{
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
            System.out.println("pasooo2 NG onPreExecute");
        }
        @Override
        protected String doInBackground(String... params) {
            String TAG="TaskGetNotasCInternas";
            SQLiteDatabase db = null;
            Cursor q = null;
            String SQL = "";
            try {
                db = dbSql.getReadableDatabase();
                String[] args = new String[] {cd};
                if(list.getCod_vend().equals("V999")){
                    SQL = "SELECT * FROM DOC_WS " +
                          "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND sdo_mov!=0 " +
                          "AND val_mov<0 AND cod_mov='NG' ORDER BY cod_mov, fec_ven, num_mov";
                }else if(list.getSeleccion().equals("V")){
                    SQL = "SELECT * FROM DOC_WS " +
                          "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND cod_ven='"+codaux+"' AND sdo_mov!=0 " +
                          "AND val_mov<0 AND cod_mov='NG' ORDER BY cod_mov, fec_ven, num_mov";
                }else{
                    SQL = "SELECT * FROM DOC_WS " +
                          "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref=? AND cod_ven='"+list.getCod_vend()+"' AND sdo_mov!=0 " +
                          "AND val_mov<0 AND cod_mov='NG' ORDER BY cod_mov, fec_ven, num_mov";
                }

                items1 = new ArrayList<RowFactura>();
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
                        parcialng1 = parcialng1 + par;
                        items1.add(new RowFactura(cod_emp,cod_mov,cod_pto,cod_ref,cod_ven,dias,emi,ven,
                                                  num_mov,num_rel,sdo_mov,val_mov,iva,base,est,cdb,ncta,sel,sel2,ab));
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
            DecimalFormat pre = new DecimalFormat("0.00");
            valores.setText(pre.format(parcialng1));
            FacturaAdapterNg myListAdapter = new FacturaAdapterNg();
            listView.setAdapter(myListAdapter);
        }

    }

    public class TaskGetNotasCreditoInternasAPP extends AsyncTask<String, Void, String>{
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
            System.out.println("pasooo2 NA onPreExecute");
        }

        @Override
        protected String doInBackground(String... params) {
            String TAG="TaskGetNCInternasAPP";
            SQLiteDatabase db = null;
            String SQL = "";
            Cursor q = null;
            try {
                db = dbSql.getReadableDatabase();
                //String[] args = new String[] {cd};
                SQL = "SELECT * FROM DOC_LOCAL " +
                            "WHERE cod_emp='"+list.getCod_emp()+"' AND (cod_ref='"+cd+"' OR cod_vin='"+cd+"') " +
                            "AND cod_ven='"+list.getCod_vend()+"' AND sdo_mov!=0 " +
                            "AND val_mov>0 AND sts_mov=0 AND (cod_mov='NA' OR cod_mov='NG') " +
                            "GROUP BY cod_ref, num_mov " +
                            "ORDER BY cod_mov, fec_ven, num_mov";
                items2 = new ArrayList<RowFacturaNC>();
                q = db.rawQuery(SQL, null);
                double par = 0.00;
                if (q.moveToFirst()){
                    do {
                        String cod_emp = q.getString(1);
                        String cod_mov = q.getString(2);
                        String cod_pto  = q.getString(3);
                        String cod_ref = q.getString(4);
                        String cod_vin = q.getString(5);
                        String cod_ven = q.getString(6);
                        String vendedor = q.getString(7);
                        int dias = q.getInt(8);
                        String emi = q.getString(9);
                        String ven = q.getString(10);
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
                        double ab = q.getDouble(13);
                        par = sdo_mov;
                        parcialng2 = parcialng2 + par;
                        items2.add(new RowFacturaNC(cod_emp,cod_mov,cod_pto,cod_ref,cod_vin,cod_ven,vendedor,dias,emi,ven,num_mov,num_rel,sdo_mov,val_mov,iva,base,est,cdb,ncta,sel,ab));
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
            DecimalFormat pre = new DecimalFormat("0.00");
            valores.setText(pre.format(parcialng2));
            FacturaAdapterNgApp myListAdapter = new FacturaAdapterNgApp();
            listView.setAdapter(myListAdapter);
        }

    }

    public String getCodigoVinculado(String cod){
        String c = "";
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT cod_vin FROM CLIENTES_REL WHERE cod_emp='"+list.getCod_emp()+"' AND cod_ref='"+cod+"'";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
                    c = q.getString(0);
                } while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
        }
        return c;
    }

    public void validaSeleccion(){
        try {
            if (listView != null) {
                if(dato.equals("NC")){
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
                }else if(dato.equals("NG")){
                    FacturaAdapterNg adapter = (FacturaAdapterNg) listView.getAdapter();
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
                }else if(dato.equals("NA")){
                    FacturaAdapterNgApp adapter = (FacturaAdapterNgApp) listView.getAdapter();
                    for (int i = 0; i < adapter.getCount(); i++) {
                        RowFacturaNC rows = (RowFacturaNC) adapter.getItem(i);
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
                }

                if(contador>1){
                    CharSequence text = "SOLO PUEDE SELECCIONAR UNA NOTA DE CREDITO ....!!";
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
                    calcular();
                    Intent intent = new Intent(getApplicationContext(), FacturasNotasActivity.class);
                    intent.putExtra("nomb", nom);
                    intent.putExtra("cod", cd);
                    intent.putExtra("persona", pers);
                    intent.putExtra("codaux", codaux);
                    intent.putExtra("abono", ab);
                    intent.putExtra("cliente",lista.getCod_ref());
                    intent.putExtra("vendedor",lista.getCod_ven());
                    intent.putExtra("titulo2",tnc1);
                    intent.putExtra("titulo3",tnc2);
                    intent.putParcelableArrayListExtra("aux", aux);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("item", list);
                    intent.putExtra("dato", dato);
                    intent.putExtras(bundle);
                    startActivity(intent);


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
