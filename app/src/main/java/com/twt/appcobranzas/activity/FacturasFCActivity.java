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
import java.util.Calendar;
import java.util.Date;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.model.CobrosRet;
import com.twt.appcobranzas.model.Cobros_Doc;
import com.twt.appcobranzas.model.Retenciones;
import com.twt.appcobranzas.model.RowFactura;
import com.twt.appcobranzas.model.Vendedor;
import com.twt.appcobranzas.utils.DbHelper;
import com.twt.appcobranzas.utils.Utils;

/**
 * Created by DEVECUA on 11/12/2015.
 */
@SuppressWarnings("deprecation")
public class FacturasFCActivity extends AppCompatActivity {

    String   TAG="FacturasActivity";
    ListView listView;
    Context  context;
    TextView cod, valores, val, abonos, ncredito, ng1, titulo, numerof;
    CheckBox chkFact;
    Button   btCobro;
    DbHelper dbSql =null;
    String   cd, nom, dato, pers, tf, tr, codaux;
    Cobros_Doc micd;
    CobrosRet  v;
    Retenciones ret1, ret2;
    RowFactura lista;
    Vendedor   list;
    Double     parcial1 = .0, ab, nc1, ng2, i1, i10, saldo, abono;

    int contador = 0, numd = 0;
    int contador2 = 0;
    boolean res = true;
    private Double sum;
    ArrayList<CobrosRet>  cret, cr;
    ArrayList<RowFactura> items, aux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facturas1);
        context = this;
        dbSql = new DbHelper(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        sum    = .0;
        cd     = getIntent().getExtras().getString("cod_cli");
        nom    = getIntent().getExtras().getString("nomb_cli");
        codaux = getIntent().getExtras().getString("codaux");
        pers   = getIntent().getExtras().getString("persona");
        dato   = getIntent().getExtras().getString("dato");
        tf     = getIntent().getExtras().getString("titulo");
        tr     = getIntent().getExtras().getString("titulo1");
        Bundle bundle = getIntent().getExtras();
        list = bundle.getParcelable("item");
        i1 = .0; i10 = .0; saldo = .0; abono = .0;

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

        titulo.setText(tf);
        cod.setText(cd + " - " + nom);

        micd = new Cobros_Doc();
        cret = new ArrayList<CobrosRet>();
        ret1 = new Retenciones();
        ret2 = new Retenciones();

        nc1 = loadNcCobro();
        ng2 = loadNgCobro();
        new TaskGetFacturasCobro().execute();

        if(pers.equals("1") || pers.equals("2")){
            ret1 = consulta("320");
        }else if(pers.equals("3")) {
            ret1 = consulta("320");
            ret2 = consulta("729");
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

    public class FacturaAdapter1 extends BaseAdapter {

        public Double   iva1;
        public Double   iva10;
        public String   aux;
        public String   aux1;
        public Date     date  = null;
        public Date     date1 = null;
        public Calendar cal1 = Calendar.getInstance();
        public Calendar cal2 = Calendar.getInstance();
        public Calendar cal3 = Calendar.getInstance();
        public Calendar cal4 = Calendar.getInstance();

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
                convertView = inflater.inflate(R.layout.facturasvalor1_list, null);

                holder = new ViewHolder();
                holder.chkFact = (CheckBox) convertView.findViewById(R.id.chk_fac);
                //holder.chkNorm = (CheckBox) convertView.findViewById(R.id.chk_nor);
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
            iva1 = .0; iva10 = .0;
            cal1.setTime(new Date());
            cal3.setTime(new Date());
            //cal4.set(2020, 2, 31);

            RowFactura item = items.get(position);

            // Set data into the view.
            holder.chkFact.setChecked(item.isSeleccion());
            //holder.chkNorm.setChecked(item.isSeleccion2());
            holder.txtTipo.setText(item.getCod_mov());
            holder.txtFactura.setText(item.getNum_mov());
            date = Utils.ConvertirShortStringToDate(item.getFec_emis());
            cal2.setTime(date);
            DecimalFormat df = new DecimalFormat("0.00");

            cal3.add(Calendar.YEAR, -1);

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
                if(!pers.equals("0")) {
                    if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                        if(pers.equals("1") || pers.equals("2")){
                            if(ret1.getSts_rxt().equals("1")){
                                date1 = Utils.ConvertirShortStringToDate(ret1.getFec_rxt());
                                cal4.setTime(date1);

                                if(cal2.before(cal4) || cal2.equals(cal4))
                                    iva1 = item.getBase() * (ret1.getPor_rxt()/100);
                                else if(cal2.after(cal4))
                                    iva1 = item.getBase() * (ret1.getPrc_retencion()/100);

                            }else{
                                iva1 = item.getBase() * (ret1.getPrc_retencion()/100);
                            }

                            holder.txtPorc.setText(String.valueOf(df.format(Utils.Redondear(iva1,2))));
                        }else if(pers.equals("3")){
                            if(ret1.getSts_rxt().equals("1")){
                                date1 = Utils.ConvertirShortStringToDate(ret1.getFec_rxt());
                                cal4.setTime(date1);

                                if(cal2.before(cal4) || cal2.equals(cal4)) {
                                    iva1 = item.getBase() * (ret1.getPor_rxt()/100);
                                    iva10 = item.getIva() * (ret2.getPrc_retencion()/100);
                                }else if(cal2.after(cal4)){
                                    iva1 = item.getBase() * (ret1.getPrc_retencion()/100);
                                    iva10 = item.getIva() * (ret2.getPrc_retencion()/100);
                                }

                            }else{
                                iva1 = item.getBase() * (ret1.getPrc_retencion()/100);
                                iva10 = item.getIva() * (ret2.getPrc_retencion()/100);
                            }

                            holder.txtPorc.setText(String.valueOf(df.format(Utils.Redondear(iva1+iva10,2))));
                        }else if(pers.equals("0")){
                            holder.txtPorc.setText(String.valueOf(df.format(Utils.Redondear(.0,2))));
                        }
                    } else if(cal2.get(Calendar.YEAR) == cal3.get(Calendar.YEAR)) {
                        if(pers.equals("1") || pers.equals("2")){
                            //iva1 = item.getBase() * (ret1.getPor_rxt()/100);
                            //holder.txtPorc.setText(String.valueOf(df.format(Utils.Redondear(iva1,2))));
                            if(ret1.getSts_rxt().equals("1")){
                                date1 = Utils.ConvertirShortStringToDate(ret1.getFec_rxt());
                                cal4.setTime(date1);

                                if(cal2.before(cal4) || cal2.equals(cal4))
                                    iva1 = item.getBase() * (ret1.getPor_rxt()/100);
                                else if(cal2.after(cal4))
                                    iva1 = item.getBase() * (ret1.getPrc_retencion()/100);

                            }else{
                                iva1 = item.getBase() * (ret1.getPrc_retencion()/100);
                            }
                            holder.txtPorc.setText(String.valueOf(df.format(Utils.Redondear(iva1,2))));
                        }else if(pers.equals("3")){
                            //iva1 = item.getBase() * (ret1.getPor_rxt()/100);
                            //iva10 = item.getIva() * (ret2.getPrc_retencion()/100);
                            //holder.txtPorc.setText(String.valueOf(df.format(Utils.Redondear(iva1+iva10,2))));
                            if(ret1.getSts_rxt().equals("1")){
                                date1 = Utils.ConvertirShortStringToDate(ret1.getFec_rxt());
                                cal4.setTime(date1);

                                if(cal2.before(cal4) || cal2.equals(cal4)) {
                                    iva1 = item.getBase() * (ret1.getPor_rxt()/100);
                                    iva10 = item.getIva() * (ret2.getPrc_retencion()/100);
                                }else if(cal2.after(cal4)){
                                    iva1 = item.getBase() * (ret1.getPrc_retencion()/100);
                                    iva10 = item.getIva() * (ret2.getPrc_retencion()/100);
                                }

                            }else{
                                iva1 = item.getBase() * (ret1.getPrc_retencion()/100);
                                iva10 = item.getIva() * (ret2.getPrc_retencion()/100);
                            }
                            holder.txtPorc.setText(String.valueOf(df.format(Utils.Redondear(iva1+iva10,2))));
                        }else if(pers.equals("0")){
                            holder.txtPorc.setText(String.valueOf(df.format(Utils.Redondear(.0,2))));
                        }
                    } else if(!item.getNum_rel().equals("000000")){
                        if(pers.equals("1") || pers.equals("2")){
                            //iva1 = item.getBase() * (ret1.getPor_rxt()/100);
                            //holder.txtPorc.setText(String.valueOf(df.format(Utils.Redondear(iva1,2))));
                            if(ret1.getSts_rxt().equals("1")){
                                date1 = Utils.ConvertirShortStringToDate(ret1.getFec_rxt());
                                cal4.setTime(date1);

                                if(cal2.before(cal4) || cal2.equals(cal4))
                                    iva1 = item.getBase() * (ret1.getPor_rxt()/100);
                                else if(cal2.after(cal4))
                                    iva1 = item.getBase() * (ret1.getPrc_retencion()/100);

                            }else{
                                iva1 = item.getBase() * (ret1.getPrc_retencion()/100);
                            }
                            holder.txtPorc.setText(String.valueOf(df.format(Utils.Redondear(iva1,2))));
                        }else if(pers.equals("3")){
                            //iva1 = item.getBase() * (ret1.getPor_rxt()/100);
                            //iva10 = item.getIva() * (ret2.getPrc_retencion()/100);
                            //holder.txtPorc.setText(String.valueOf(df.format(Utils.Redondear(iva1+iva10,2))));
                            if(ret1.getSts_rxt().equals("1")){
                                date1 = Utils.ConvertirShortStringToDate(ret1.getFec_rxt());
                                cal4.setTime(date1);

                                if(cal2.before(cal4) || cal2.equals(cal4)) {
                                    iva1 = item.getBase() * (ret1.getPor_rxt()/100);
                                    iva10 = item.getIva() * (ret2.getPrc_retencion()/100);
                                }else if(cal2.after(cal4)){
                                    iva1 = item.getBase() * (ret1.getPrc_retencion()/100);
                                    iva10 = item.getIva() * (ret2.getPrc_retencion()/100);
                                }

                            }else{
                                iva1 = item.getBase() * (ret1.getPrc_retencion()/100);
                                iva10 = item.getIva() * (ret2.getPrc_retencion()/100);
                            }
                            holder.txtPorc.setText(String.valueOf(df.format(Utils.Redondear(iva1+iva10,2))));
                        }else if(pers.equals("0")){
                            holder.txtPorc.setText(String.valueOf(df.format(Utils.Redondear(.0,2))));
                        }
                    }else{
                        holder.txtPorc.setText("0.00");
                    }
                }else{
                    holder.txtPorc.setText(String.valueOf(df.format(Utils.Redondear(.0,2))));
                    item.setReten(Double.valueOf(.0));
                }
                holder.txtFch_emi.setText(item.getFec_emis());
            }

            //item.setReten(Double.valueOf(holder.txtPorc.getText().toString()));
            items.set(holder.ref, item);

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

            holder.txtValor.setText(String.valueOf(df.format(item.getVal_mov())));

            cret = cobrosRet(item.getNum_mov());
            if(!cret.isEmpty()){
                for (CobrosRet row: cret){
                    if(row.getNum_mov().equals(item.getNum_mov())){
                        Double var = Double.valueOf(holder.txtPorc.getText().toString());
                        holder.txtValor_saldo.setText(String.valueOf(df.format(Utils.Redondear(item.getVal_mov() - var, 2))));
                    }
                }
            }else{
                holder.txtValor_saldo.setText(String.valueOf(df.format(Utils.Redondear(item.getSdo_mov(),2))));
            }

            holder.txtAbono.setText(String.valueOf(df.format(Utils.Redondear(item.getAbono(),2))));

            /*if(item.getNum_rel().equals("000000")){
                holder.chkNorm.setEnabled(true);
            }else{
                holder.chkNorm.setEnabled(false);
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
                    DecimalFormat dfo = new DecimalFormat("0.00");
                    cal1.setTime(new Date());
                    cal3.setTime(new Date());

                    date = Utils.ConvertirShortStringToDate(it.getFec_emis());
                    cal2.setTime(date);

                    cal3.add(Calendar.YEAR, -1);

                    int anio1 = cal1.get(Calendar.YEAR);
                    int anio2 = cal2.get(Calendar.YEAR);

                    if (holder.chkFact.isChecked()) {
                        if(!pers.equals("0")) {
                            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                                if (it.getNum_rel().equals("000000") && it.getCod_pto().equals("02")) {
                                    final DecimalFormat dfor = new DecimalFormat("0.00");
                                    final Dialog dialog = new Dialog(context);
                                    dialog.setContentView(R.layout.seleccion);
                                    dialog.setTitle("Seleccione");

                                    // set the custom dialog components - text, image and button
                                    Button dialogBtn1 = (Button) dialog.findViewById(R.id.conRT);
                                    Button dialogBtn2 = (Button) dialog.findViewById(R.id.sinRT);

                                    dialogBtn1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                            aux = holder.txtPorc.getText().toString().replace(",",".");
                                            aux1 = holder.txtValor_saldo.getText().toString().replace(",",".");
                                            Double var = Double.valueOf(aux);
                                            Double sdo = Double.valueOf(aux1);
                                            holder.txtAbono.setText(String.valueOf(dfor.format(Utils.Redondear(sdo - var, 2))));

                                            it.setAbono(Double.valueOf(Utils.Redondear(sdo - var, 2)));
                                            items.set(holder.ref, it);
                                            sumar();
                                        }
                                    });

                                    dialogBtn2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                            aux1 = holder.txtValor_saldo.getText().toString().replace(",",".");
                                            Double sdo = Double.valueOf(aux1);
                                            holder.txtAbono.setText(String.valueOf(dfor.format(Utils.Redondear(sdo, 2))));

                                            it.setAbono(Double.valueOf(Utils.Redondear(sdo, 2)));
                                            items.set(holder.ref, it);
                                            sumar();
                                        }
                                    });

                                    sumar();
                                    dialog.show();
                                } else {
                                    /*Double sdo = Double.valueOf(holder.txtValor_saldo.getText().toString());
                                    holder.txtAbono.setText(String.valueOf(dfo.format(Utils.Redondear(sdo, 2))));

                                    it.setAbono(Double.valueOf(holder.txtAbono.getText().toString()));
                                    items.set(holder.ref, it);
                                    sumar();*/
                                    aux = holder.txtValor_saldo.getText().toString().replace(",",".");
                                    Double sdo = Double.valueOf(aux);
                                    holder.txtAbono.setText(String.valueOf(dfo.format(Utils.Redondear(sdo, 2))));

                                    it.setAbono(Utils.Redondear(sdo, 2));
                                    items.set(holder.ref, it);
                                    sumar();
                                }
                            } if (cal2.get(Calendar.YEAR) == cal3.get(Calendar.YEAR)) {
                                if (it.getNum_rel().equals("000000") && it.getCod_pto().equals("02")) {
                                    final DecimalFormat dfor = new DecimalFormat("0.00");
                                    final Dialog dialog = new Dialog(context);
                                    dialog.setContentView(R.layout.seleccion);
                                    dialog.setTitle("Seleccione");

                                    // set the custom dialog components - text, image and button
                                    Button dialogBtn1 = (Button) dialog.findViewById(R.id.conRT);
                                    Button dialogBtn2 = (Button) dialog.findViewById(R.id.sinRT);

                                    dialogBtn1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                            aux = holder.txtPorc.getText().toString().replace(",",".");
                                            aux1 = holder.txtValor_saldo.getText().toString().replace(",",".");
                                            Double var = Double.valueOf(aux);
                                            Double sdo = Double.valueOf(aux1);
                                            holder.txtAbono.setText(String.valueOf(dfor.format(Utils.Redondear(sdo - var, 2))));

                                            it.setAbono(Double.valueOf(Utils.Redondear(sdo - var, 2)));
                                            items.set(holder.ref, it);
                                            sumar();
                                        }
                                    });

                                    dialogBtn2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                            aux1 = holder.txtValor_saldo.getText().toString().replace(",",".");
                                            Double sdo = Double.valueOf(aux1);
                                            holder.txtAbono.setText(String.valueOf(dfor.format(Utils.Redondear(sdo, 2))));

                                            it.setAbono(Double.valueOf(Utils.Redondear(sdo, 2)));
                                            items.set(holder.ref, it);
                                            sumar();
                                        }
                                    });

                                    sumar();
                                    dialog.show();
                                } else {
                                    aux = holder.txtValor_saldo.getText().toString().replace(",",".");
                                    Double sdo = Double.valueOf(aux);
                                    holder.txtAbono.setText(String.valueOf(dfo.format(Utils.Redondear(sdo, 2))));

                                    it.setAbono(Utils.Redondear(sdo, 2));
                                    items.set(holder.ref, it);
                                    sumar();
                                }
                            } else {
                                /*Double sdo = Double.valueOf(holder.txtValor_saldo.getText().toString());
                                holder.txtAbono.setText(String.valueOf(dfo.format(Utils.Redondear(sdo, 2))));

                                it.setAbono(Double.valueOf(holder.txtAbono.getText().toString()));
                                items.set(holder.ref, it);
                                sumar();*/

                                aux = holder.txtValor_saldo.getText().toString().replace(",",".");
                                Double sdo = Double.valueOf(aux);
                                holder.txtAbono.setText(String.valueOf(dfo.format(Utils.Redondear(sdo, 2))));

                                it.setAbono(Utils.Redondear(sdo, 2));
                                items.set(holder.ref, it);
                                sumar();
                            }
                        }else {
                            /*Double sdo = Double.valueOf(holder.txtValor_saldo.getText().toString());
                            holder.txtAbono.setText(String.valueOf(dfo.format(Utils.Redondear(sdo, 2))));

                            it.setAbono(Double.valueOf(holder.txtAbono.getText().toString()));
                            items.set(holder.ref, it);
                            sumar();*/

                            aux = holder.txtValor_saldo.getText().toString().replace(",",".");
                            Double sdo = Double.valueOf(aux);
                            holder.txtAbono.setText(String.valueOf(dfo.format(Utils.Redondear(sdo, 2))));

                            it.setAbono(Utils.Redondear(sdo, 2));
                            items.set(holder.ref, it);
                            sumar();
                        }

                        items.get(position).setSeleccion(true);
                        holder.txtAbono.setVisibility(View.VISIBLE);
                        contador2++;
                        numerof.setText(String.valueOf(contador2));

                        aux1 = holder.txtAbono.getText().toString().replace(",",".");

                        it.setAbono(Double.valueOf(aux1));
                        items.set(holder.ref, it);
                        sumar();
                    } else {
                        items.get(position).setSeleccion(false);
                        holder.txtAbono.setVisibility(View.INVISIBLE);
                        contador2--;
                        numerof.setText(String.valueOf(contador2));

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

    public Retenciones consulta(String cod){
        SQLiteDatabase db = null;
        Retenciones lista = new Retenciones();
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM RETENCIONES WHERE empresa = '"+ list.getCod_emp() +"' AND cod_retencion='"+cod+"'";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
                    lista.setEmpresa(q.getString(0));
                    lista.setCod_retencion(q.getString(1));
                    lista.setNom_retencion(q.getString(2));
                    lista.setPrc_retencion(q.getDouble(3));
                    lista.setLim_ret(q.getDouble(4));
                    lista.setSts_rxt(q.getString(5));
                    lista.setFec_rxt(q.getString(6));
                    lista.setFex_rxt(q.getString(7));
                    lista.setPor_rxt(q.getDouble(8));
                } while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            //db.close();
        }
        return lista;
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
            Cursor q = null;
            Date date = null;
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(new Date());
            try {
                db = dbSql.getReadableDatabase();
                String[] args = new String[] {cd};
                String SQL = "";
                if(list.getCod_vend().equals("V999")){
                    SQL = "SELECT * FROM DOC_WS " +
                            "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref=? " +
                            "AND sdo_mov>0 AND (cod_mov='FC' OR cod_mov='DC' OR cod_mov='ND' " +
                            "OR cod_mov='CR') ORDER BY cod_mov, fec_emi, num_mov";
                }else if(list.getSeleccion().equals("V")){
                    SQL = "SELECT * FROM DOC_WS " +
                            "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref = ? AND cod_ven='" + codaux + "' " +
                            "AND sdo_mov>0 AND (cod_mov='FC' OR cod_mov='DC' OR cod_mov='ND' " +
                            "OR cod_mov='CR') ORDER BY cod_mov, fec_emi, num_mov";
                }else {
                    SQL = "SELECT * FROM DOC_WS " +
                            "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref = ? AND cod_ven='" + list.getCod_vend() + "' " +
                            "AND sdo_mov>0 AND (cod_mov='FC' OR cod_mov='DC' OR cod_mov='ND' " +
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
                        date = Utils.ConvertirShortStringToDate(emi);
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
                        c2.setTime(date);

                        saldo = sdo_mov;
                        abono = saldo;

                        par = saldo;
                        parcial1 = parcial1 + par;

                        items.add(new RowFactura(cod_emp,cod_mov,cod_pto,cod_ref,cod_ven,dias,emi,ven,
                                                 num_mov,num_rel,saldo,val_mov,iva,base,est,cdb,ncta,sel,sel2,abono));
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
            FacturaAdapter1 myListAdapter = new FacturaAdapter1();
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
            String SQL = "";
            if(list.getCod_vend().equals("V999")){
                SQL = "SELECT SUM(sdo_mov) FROM DOC_WS " +
                        "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref=? AND cod_mov='NC'";
            }else if(list.getSeleccion().equals("V")){
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
                      "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref = ? AND cod_mov='NG'";
            }else if(list.getSeleccion().equals("V")) {
                SQL = "SELECT SUM(sdo_mov) FROM DOC_WS " +
                      "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref = ? AND cod_ven='" + codaux + "' AND cod_mov='NG'";
            }else{
                SQL = "SELECT SUM(sdo_mov) FROM DOC_WS " +
                      "WHERE cod_emp='" + list.getCod_emp() + "' AND cod_ref = ? AND cod_ven='" + list.getCod_vend() + "' AND cod_mov='NG'";
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

    public Cobros_Doc cobrosDoc(String cod, String num){
        SQLiteDatabase db = null;
        Cursor q = null;
        Cobros_Doc v = new Cobros_Doc();
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM COBROS_DOC " +
                        "WHERE cod_emp='"+list.getCod_emp()+"' AND cod_mov='"+cod.toUpperCase().trim()+
                        "' AND num_mov='"+num.trim()+"' AND tipo_cobro='RC' AND estado=0";
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
                FacturaAdapter1 adapter = (FacturaAdapter1) listView.getAdapter();
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
                        intent.putExtra("codaux",codaux);
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
                    intent.putExtra("codaux",codaux);
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

    public ArrayList<CobrosRet> cobrosRet(String num){
        SQLiteDatabase db = null;
        Cursor q = null;
        cr = new ArrayList<CobrosRet>();
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM COBROS_RET WHERE cod_mov='FC' AND num_mov='"+num.trim()
                    +"' AND cod_ven='"+list.getCod_vend()+"' AND estado=0";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    v = new CobrosRet();
                    v.setId(q.getInt(0));
                    v.setCod_emp(q.getString(1));
                    v.setId_recibo(q.getInt(2));
                    v.setFch_ret(q.getString(3));
                    v.setCod_ref(q.getString(4));
                    v.setNum_mov(q.getString(5));
                    v.setCod_mov(q.getString(6));
                    v.setFecha(q.getString(7));
                    v.setEstab(q.getString(8));
                    v.setPto_emi(q.getString(9));
                    v.setSecuencia(q.getString(10));
                    v.setNumaut(q.getString(11));
                    v.setCod_retencion(q.getString(12));
                    v.setPrc_retencion(q.getDouble(13));
                    v.setValor(q.getDouble(14));
                    v.setCod_ven(q.getString(15));
                    v.setVendedor2(q.getString(16));
                    v.setCod_pto(q.getString(17));
                    v.setEstado(q.getInt(18));
                    cr.add(v);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            //db.close();
        }
        return cr;
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
                intent.putExtra("codaux", codaux);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
