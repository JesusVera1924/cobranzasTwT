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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.model.CobrosRet;
import com.twt.appcobranzas.model.Retenciones;
import com.twt.appcobranzas.model.RowFactura;
import com.twt.appcobranzas.model.Vendedor;
import com.twt.appcobranzas.utils.DbHelper;
import com.twt.appcobranzas.utils.Utils;

/**
 * Created by Devecua on 29/04/2016.
 */
public class CobroRetActivity extends AppCompatActivity {
    String                     TAG = "CobroRetActivity";
    ListView                   listView;
    Context                    context;
    List<CobrosRet>            items;
    List<Retenciones>          retenc;
    ArrayAdapter<Retenciones>  adaptador;
    RowFactura                 lista;
    ArrayList<RowFactura>      list;
    CobrosRet                  lista1;
    TextView                   cod, base, iva, abonado, pret;
    Button                     btAgregar;
    Spinner                    retencion;
    EditText                   parcial,estab,ptoemi,secuencia, numaut;
    DatePicker                 fch;
    Vendedor                   l;
    DbHelper                   dbSql = null;
    String                     cd, nom, cod_cli, cod_ven, dato, pers, sp;
    String                     tf, tr, auxsec, numero, aut, faut1, codaux;
    Date                       date, date2, fecha1, fecha2, faut;
    int                        sec, contador, day, month, year;
    Double                     ab, sum = .0, iva1, iva10;
    Calendar                   cal1 = Calendar.getInstance();
    Calendar                   cal2 = Calendar.getInstance();
    Retenciones                retc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cobrosret);
        context = this;
        dbSql = new DbHelper(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        cd      = getIntent().getExtras().getString("cod");
        nom     = getIntent().getExtras().getString("nomb");
        pers    = getIntent().getExtras().getString("persona");
        codaux  = getIntent().getExtras().getString("codaux");
        ab      = getIntent().getExtras().getDouble("abono");
        cod_cli = getIntent().getExtras().getString("cliente");
        cod_ven = getIntent().getExtras().getString("vendedor");
        dato    = getIntent().getExtras().getString("dato");
        tf      = getIntent().getExtras().getString("titulo");
        tr      = getIntent().getExtras().getString("titulo1");
        aut     = getIntent().getExtras().getString("aut");
        faut1    = getIntent().getExtras().getString("faut");

        Bundle bundle = getIntent().getExtras();
        l    = bundle.getParcelable("item");
        list = getIntent().getParcelableArrayListExtra("aux");

        items = new ArrayList<CobrosRet>();

        //widget
        listView  =  (ListView) this.findViewById(R.id.lsv_lista_ret);
        cod       =  (TextView) this.findViewById(R.id.txt_cliente);
        base      =  (TextView) this.findViewById(R.id.txt_base);
        iva       =  (TextView) this.findViewById(R.id.txt_iva);
        abonado   =  (TextView) this.findViewById(R.id.txt_abonado);
        pret      =  (TextView) this.findViewById(R.id.txt_pret);
        parcial   =  (EditText) this.findViewById(R.id.et_parcial);
        estab     =  (EditText) this.findViewById(R.id.et_estab);
        ptoemi    =  (EditText) this.findViewById(R.id.et_ptoemi);
        secuencia =  (EditText) this.findViewById(R.id.et_secuencia);
        numaut    =  (EditText) this.findViewById(R.id.et_numauto);
        fch       =  (DatePicker) this.findViewById(R.id.rt_fch);
        retencion =  (Spinner) this.findViewById(R.id.sp_retencion);
        btAgregar =  (Button) this.findViewById(R.id.btn_agregar);

        retenc = new ArrayList<Retenciones>();
        retenc = consulta();
        adaptador = new ArrayAdapter<Retenciones>(this, R.layout.simple_spinner_list1, retenc);
        adaptador.setDropDownViewResource(R.layout.simple_spinner_list1);
        retencion.setAdapter(adaptador);

        factura();
        cod.setText(cd + " - " + nom);
        base.setText(String.valueOf(lista.getBase()));
        iva.setText(String.valueOf(lista.getIva()));
        parcial.setEnabled(false);
        estab.setText("001");
        ptoemi.setText("002");
        numaut.setText(aut);

        faut = Utils.ConvertirShortStringToDate2(faut1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(faut);
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);

        fch.updateDate(year, month, day);

        onBackPressed();

        for(RowFactura row: list){
            numero = row.getNum_mov();
        }

        btAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retenciones miret = prcRet(sp);
                String val = parcial.getText().toString();
                if (pers.equals("1")) {
                    if (miret.getCod_retencion().substring(0, 1).equals("7")) {
                        CharSequence text = "RETENCION NO PERMITIDA ....!!";
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
                    for (CobrosRet row : items) {
                        if (miret.getCod_retencion().equals(row.getCod_retencion())) {
                            if (items.size() >= 1) {
                                CharSequence text = "NO SE PERMITEN INGRESAR MAS REGISTROS ...!!";
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
                if (pers.equals("2") || pers.equals("3")) {
                    for (CobrosRet row : items) {
                        if (miret.getCod_retencion().equals(row.getCod_retencion())) {
                            if (items.size() >= 1) {
                                CharSequence text = "NO SE PERMITEN INGRESAR MAS REGISTROS ...!!";
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
                if (secuencia.getText().length() == 0) {
                    CharSequence text = "CAMPO SECUENCIA NO DEBE ESTAR VACIO ....!!";
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
                if (numaut.getText().length() < 10) {
                    CharSequence text = "CAMPO No AUTORIZACION NO DEBE ESTAR VACIO o TENER MENOS DE 10 DIGITOS ....!!";
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

                fecha1 = Utils.ConvertirShortStringToDate(lista.getFec_emis());
                long auxiliar = fch.getCalendarView().getDate();
                fecha2 =  new Date(auxiliar);
                if (fecha2.before(fecha1)) {
                    CharSequence text = "LA FECHA INGRESADA PARA LA RETENCION ES ANTERIOR A LA FECHA DE LA FACTURA ....!!";
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
                if (val.equals("")) {
                    CharSequence text = "INGRESE UNA CANTIDAD A ABONAR ....!!";
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
                new TaskGetAbono().execute();
                secuencia.setText("");
            }
        });


        retencion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sp = String.valueOf(retencion.getSelectedItem());
                Retenciones miret = prcRet(sp);
                if(pers.equals("1")){
                    if(miret.getCod_retencion().substring(0,1).equals("7")){
                        CharSequence text = "RETENCION NO PERMITIDA ....!!";
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
                        Retenciones r = prcRet(sp);
                        if (sp.contentEquals(r.getNom_retencion())) {
                            if(r.getSts_rxt().equals("1")){
                                long a = fch.getCalendarView().getDate();
                                date2 =  new Date(a);
                                cal1.setTime(date2);

                                date = Utils.ConvertirShortStringToDate(r.getFec_rxt());
                                cal2.setTime(date);

                                if(cal1.before(cal2) || cal1.equals(cal2)) {
                                    iva1 = lista.getBase() * (r.getPor_rxt() / 100);
                                    iva10 = r.getPor_rxt();
                                }else if(cal1.after(cal2)) {
                                    iva1 = lista.getBase() * (r.getPrc_retencion() / 100);
                                    iva10 = r.getPrc_retencion();
                                }
                            }else{
                                iva1 = lista.getBase() * (r.getPrc_retencion()/100);
                                iva10 = r.getPrc_retencion();
                            }

                            pret.setText(String.valueOf(iva10));
                            parcial.setText(String.valueOf(iva1));
                        }
                    }
                }else if(pers.equals("2") || pers.equals("3")){
                    Retenciones r = prcRet(sp);
                    if (sp.contentEquals(r.getNom_retencion())) {
                        if(r.getCod_retencion().substring(0,1).equals("7")){
                            double p = Utils.Redondear((lista.getIva() * r.getPrc_retencion()) / 100,2);
                            pret.setText(String.valueOf(r.getPrc_retencion()));
                            parcial.setText(String.valueOf(p));
                        }else{
                            double p = Utils.Redondear((lista.getBase() * r.getPrc_retencion()) / 100,2);
                            pret.setText(String.valueOf(r.getPrc_retencion()));
                            parcial.setText(String.valueOf(p));
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }

        );
    }

    private void doPago(){
        try {
            contador = countRet();
            if(contador>0){
                CharSequence text = "DOCUMENTO SELECCIONADO YA TIENE APLICADA LA RETENCION DENTRO DE LA APP ....!!";
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
            if(estab.getText().length()!=3 && ptoemi.getText().length()!=3){
                CharSequence text = "CAMPO ESTABLECIMIENTO / PTO EMISION DEBE TENER 3 DIGITOS ....!!";
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

            if(pers.equals("3")){
                if(items.size() <= 1){
                    CharSequence text = "CONTRIBUYENTE ESPECIAL, FALTA REGISTROS POR APLICAR ....!!";
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

            if (listView != null && items.size() > 0) {
                setRetenciones();
                updateFacturas(list);
                Intent intent = new Intent(getApplicationContext(), FacturasActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("item", l);
                intent.putExtras(bundle);
                intent.putExtra("cod_cli", cd);
                intent.putExtra("nomb_cli", nom);
                intent.putExtra("persona", pers);
                intent.putExtra("codaux", codaux);
                intent.putExtra("dato", dato);
                intent.putExtra("titulo", tf);
                intent.putExtra("titulo1", tr);
                intent.putExtra("aut", aut);
                intent.putExtra("faut", faut1);
                startActivity(intent);
                CharSequence text = "LA RETENCION SE APLICO SATISFACTORIAMENTE ....!!";
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
            } else {
                CharSequence text = "NO HA INGRESADO NINGUN VALOR ....!!";
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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sumar() {
        sum = .0;
        if (items.size() != 0) {
            for (int i = 0; i < items.size(); i++) {
                CobrosRet rows = items.get(i);
                sum = sum + rows.getValor();
            }
            ab = Utils.Redondear(sum,2);
            abonado.setText(String.valueOf(ab));
        } else {
            abonado.setText("0.00");
        }

    }

    void factura(){
        for (RowFactura row: list){
            lista = new RowFactura();
            lista.setCod_mov(row.getCod_mov());
            lista.setNum_mov(row.getNum_mov());
            lista.setCod_ref(row.getCod_ref());
            lista.setCod_ven(row.getCod_ven());
            lista.setCod_pto(row.getCod_pto());
            lista.setFec_emis(row.getFec_emis());
            lista.setIva(row.getIva());
            lista.setBase(row.getBase());
        }
    }

    public class AbonoAdapter extends BaseAdapter {

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
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.retenciones_list, parent, false);
            }

            final CobrosRet item = items.get(position);

            // Set data into the view.
            Button btDelete  = (Button) rowView.findViewById(R.id.btn_borrar);
            btDelete.setTag(position);
            btDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Integer index = (Integer) v.getTag();
                    items.remove(index.intValue());
                    sumar();
                    notifyDataSetChanged();
                }
            });

            TextView tvNum = (TextView)  rowView.findViewById(R.id.txt_numero);
            TextView tvCod = (TextView)  rowView.findViewById(R.id.txt_codigo);
            TextView tvPrc = (TextView)  rowView.findViewById(R.id.txt_ret);
            TextView tvFch = (TextView)  rowView.findViewById(R.id.txt_fch);
            TextView tvAbono = (TextView)  rowView.findViewById(R.id.txt_valor_abonado);

            tvNum.setText(item.getEstab()+"-"+item.getPto_emi()+"-"+item.getSecuencia());
            tvCod.setText(item.getCod_retencion());
            tvPrc.setText(String.valueOf(item.getPrc_retencion()));
            tvFch.setText(item.getFch_ret());
            tvAbono.setText(String.valueOf(item.getValor()));

            return rowView;
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

    @Override
    public void onBackPressed() {
    }

    public class TaskGetAbono extends AsyncTask<Double, Void, Double> {
        ProgressDialog pDialog;

        long   dateTime = fch.getCalendarView().getDate();
        Date date = new Date(dateTime);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = dateFormat.format(date);
        String ret = retencion.getSelectedItem().toString();
        String establec = estab.getText().toString();
        String ptoemision = ptoemi.getText().toString();
        String secuenc = secuencia.getText().toString();
        String autorizacion = numaut.getText().toString();
        double abono = Double.valueOf(parcial.getText().toString());

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Agregando abono..");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
        }

        @Override
        protected Double doInBackground(Double... params) {
            String TAG="TaskGetAbono";
            lista1 = new CobrosRet();
            try {
                auxsec = secuenc;
                String doc = String.format("%09d", Integer.parseInt(secuenc));
                retc = prcRet(ret);
                lista1.setCod_retencion(retc.getCod_retencion());
                lista1.setPrc_retencion(retc.getPrc_retencion());
                lista1.setEstab(establec);
                lista1.setPto_emi(ptoemision);
                lista1.setSecuencia(doc);
                lista1.setNumaut(autorizacion);
                lista1.setFch_ret(formattedDate);
                lista1.setValor(abono);
                items.add(lista1);

                aut = lista1.getNumaut();
                faut1 = lista1.getFch_ret();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG," ERROR Abono Ret");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Double a) {
            if(pDialog != null)
                pDialog.dismiss();
            AbonoAdapter adapter = new AbonoAdapter();
            listView.setAdapter(adapter);
            sumar();
        }

    }

    public List<Retenciones> consulta(){
        SQLiteDatabase db = null;
        List<Retenciones> lista = new ArrayList<>();
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM RETENCIONES WHERE empresa = '"+ l.getCod_emp() +"' ORDER BY nom_retencion";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
                    Retenciones b = new Retenciones();
                    b.setEmpresa(q.getString(0));
                    b.setCod_retencion(q.getString(1));
                    b.setNom_retencion(q.getString(2));
                    lista.add(b);
                } while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return lista;
    }

    public Retenciones prcRet(String ret){
        SQLiteDatabase db = null;
        Retenciones r = new Retenciones();
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String[] args = new String[] {ret};
            String SQL ="SELECT * FROM RETENCIONES WHERE empresa = '"+ l.getCod_emp() +"' AND nom_retencion=?";
            q = db.rawQuery(SQL,args);
            if (q.moveToFirst()) {
                do {
                    r.setEmpresa(q.getString(0));
                    r.setCod_retencion(q.getString(1));
                    r.setNom_retencion(q.getString(2));
                    r.setPrc_retencion(q.getDouble(3));
                    r.setLim_ret(q.getDouble(4));
                    r.setSts_rxt(q.getString(5));
                    r.setFec_rxt(q.getString(6));
                    r.setFex_rxt(q.getString(7));
                    r.setPor_rxt(q.getDouble(8));
                } while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return r;
    }

    public void setRetenciones(){
        SQLiteDatabase db = null;
        String SQL = "";
        try {
            AbonoAdapter adapter = (AbonoAdapter) listView.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                CobrosRet rows = (CobrosRet) adapter.getItem(i);
                sec = getMaxRet();
                if(l.getCod_vend().equals("V999")) {
                    SQL = "INSERT INTO COBROS_RET (cod_emp,id_recibo,fch_ret,cod_ref,num_mov,cod_mov,fecha,estab,pto_emi,secuencia,num_aut," +
                            "cod_retencion,prc_retencion,valor,cod_ven,vendedor,cod_pto,estado) VALUES " +
                            "('" + l.getCod_emp() + "','" + sec + "','" + rows.getFch_ret() + "','" + lista.getCod_ref() + "','" + lista.getNum_mov() +
                            "','" + lista.getCod_mov() + "',DateTime('now','localtime'),'" + rows.getEstab() + "','" + rows.getPto_emi() +
                            "','" + rows.getSecuencia() + "','" + rows.getNumaut() + "','" + rows.getCod_retencion() + "','" + rows.getPrc_retencion() +
                            "','" + rows.getValor() + "','" + l.getCod_vend() + "','"+ cod_ven +"','" + lista.getCod_pto() + "',0)";
                }else if(l.getSeleccion().equals("V")) {
                    SQL = "INSERT INTO COBROS_RET (cod_emp,id_recibo,fch_ret,cod_ref,num_mov,cod_mov,fecha,estab,pto_emi,secuencia,num_aut," +
                            "cod_retencion,prc_retencion,valor,cod_ven,vendedor,cod_pto,estado) VALUES " +
                            "('" + l.getCod_emp() + "','" + sec + "','" + rows.getFch_ret() + "','" + lista.getCod_ref() + "','" + lista.getNum_mov() +
                            "','" + lista.getCod_mov() + "',DateTime('now','localtime'),'" + rows.getEstab() + "','" + rows.getPto_emi() +
                            "','" + rows.getSecuencia() + "','" + rows.getNumaut() + "','" + rows.getCod_retencion() + "','" + rows.getPrc_retencion() +
                            "','" + rows.getValor() + "','" + l.getCod_vend() + "','"+ cod_ven +"','" + lista.getCod_pto() + "',0)";
                }else{
                    SQL = "INSERT INTO COBROS_RET (cod_emp,id_recibo,fch_ret,cod_ref,num_mov,cod_mov,fecha,estab,pto_emi,secuencia,num_aut," +
                            "cod_retencion,prc_retencion,valor,cod_ven,vendedor,cod_pto,estado) VALUES " +
                            "('" + l.getCod_emp() + "','" + sec + "','" + rows.getFch_ret() + "','" + lista.getCod_ref() + "','" + lista.getNum_mov() +
                            "','" + lista.getCod_mov() + "',DateTime('now','localtime'),'" + rows.getEstab() + "','" + rows.getPto_emi() +
                            "','" + rows.getSecuencia() + "','" + rows.getNumaut() + "','" + rows.getCod_retencion() + "','" + rows.getPrc_retencion() +
                            "','" + rows.getValor() + "','" + l.getCod_vend() + "','','" + lista.getCod_pto() + "',0)";
                }
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
            setSecuencia("RET");
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }finally {
            db.close();
        }

    }

    public int getMaxRet(){
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT MAX(secuencia) FROM SECUENCIA " +
                        "WHERE tipo='RET' AND cod_emp='"+l.getCod_emp()+"' AND cod_ven='"+l.getCod_vend()+"'";
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

    public int countRet(){
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT COUNT(*) FROM COBROS_RET " +
                        "WHERE num_mov='"+numero+"'"+" AND cod_emp='"+l.getCod_emp()+"' AND cod_ven='"+l.getCod_vend()+"'";
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

    public void setSecuencia(String id){
        int s = sec+1;
        SQLiteDatabase db = null;
        try {
            String SQL = "UPDATE SECUENCIA SET secuencia="+s+" " +
                         "WHERE tipo='"+id+"' AND cod_emp='"+l.getCod_emp()+"' AND cod_ven='"+l.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }finally {
            db.close();
        }
    }

    public void updateFacturas(List<RowFactura> lista){
        List<RowFactura> result = lista;
        double saldo = 0.00;
        String doc = "";
        SQLiteDatabase db = null;
        try {
            AbonoAdapter adapter = (AbonoAdapter) listView.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                CobrosRet rows = (CobrosRet) adapter.getItem(i);
                doc = rows.getSecuencia().substring(3,rows.getSecuencia().length());
            }
            for (RowFactura row : result) {
                saldo = 0.00;
                DecimalFormat pre = new DecimalFormat("0.00");
                if(row.getSdo_mov()<0)
                    saldo = row.getSdo_mov()+Double.valueOf(abonado.getText().toString());
                else
                    saldo = row.getSdo_mov()-Double.valueOf(abonado.getText().toString());
                double s = Utils.Redondear(saldo,2);
                /*String SQL = "UPDATE DOC_WS SET sdo_mov=" + s +",num_rel='" + doc +
                        "' WHERE num_mov='" + row.getNum_mov() + "' AND cod_ven='"+cod_ven+"'";*/
                String SQL = "UPDATE DOC_WS SET sdo_mov=" + s +",num_rel='" + doc +
                             "' WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+l.getCod_emp()+"' AND cod_ven='"+cod_ven+"'";
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.mGrabar:
                doPago();
                return true;
            case R.id.mSalir:
                Intent intent = new Intent(getApplicationContext(), FacturasActivity.class);
                intent.putExtra("nomb_cli", nom);
                intent.putExtra("cod_cli", cd);
                intent.putExtra("persona", pers);
                intent.putExtra("codaux", codaux);
                Bundle bundle = new Bundle();
                bundle.putParcelable("item", l);
                intent.putExtra("dato", dato);
                intent.putExtra("titulo", tf);
                intent.putExtra("titulo1",tr);
                intent.putExtra("aut", aut);
                intent.putExtra("faut", faut1);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
