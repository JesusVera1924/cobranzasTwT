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
import android.text.InputFilter;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.model.Banco;
import com.twt.appcobranzas.model.BancoDep;
import com.twt.appcobranzas.model.ClientesRel;
import com.twt.appcobranzas.model.Cobros_Doc;
import com.twt.appcobranzas.model.Cobros_Pagos;
import com.twt.appcobranzas.model.RowCliente;
import com.twt.appcobranzas.model.RowFactura;
import com.twt.appcobranzas.model.Vendedor;
import com.twt.appcobranzas.utils.DbHelper;
import com.twt.appcobranzas.utils.SendMail2;
import com.twt.appcobranzas.utils.Utils;

/**
 * Created by Devecua on 29/04/2016.
 */
public class CobroActivity extends AppCompatActivity {
    String                  TAG = "CobroActivity";
    ListView                listView;
    Context                 context;
    List<Cobros_Pagos>      items;
    List<Banco>             bco_ch;
    List<BancoDep>          bco_dp;
    ArrayAdapter<Banco>     adaptador;
    ArrayAdapter<BancoDep>  adaptador2;
    ArrayList<Cobros_Pagos> docPagos, pagos;
    ArrayList<Cobros_Doc>   docCobros, cobro;
    ArrayList<ClientesRel>  cdvin, vinculado;
    ClientesRel             vincul;
    RowFactura lista;
    ArrayList<RowFactura>   list;
    Cobros_Pagos            lista1;
    TextView                cod, abonos, abonado, excedente;
    Button                  btAgregar;
    Spinner                 fpago, banco;
    CheckBox                chk_pago, chk_vinculado;
    EditText                parcial, cta, girador, docu;
    DatePicker              fch;
    Vendedor                l;
    RowCliente              cli;
    DbHelper                dbSql = null;
    String                  cd, nom, cod_cli, cod_ven, dato, pers, cod_bco, estado, cod_vin, codaux;
    String                  tf, tr, tcob, asunto, receptor1, receptor2, titulo, receptor4, dest_final;
    int                     sec, secD, secN, recibo, documento;
    double                  ab, sum = .0, ng_abonado, ng_abono, suma = .0, suma2 = .0, total = .0;
    boolean                 check;
    StringBuilder           message, message1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cobros);
        context = this;
        dbSql = new DbHelper(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        cd      = getIntent().getExtras().getString("cod");
        nom     = getIntent().getExtras().getString("nomb");
        pers    = getIntent().getExtras().getString("persona");
        codaux = getIntent().getExtras().getString("codaux");
        ab      = getIntent().getExtras().getDouble("abono");
        cod_cli = getIntent().getExtras().getString("cliente");
        cod_ven = getIntent().getExtras().getString("vendedor");
        dato    = getIntent().getExtras().getString("dato");
        tf      = getIntent().getExtras().getString("titulo");
        tr      = getIntent().getExtras().getString("titulo1");

        Bundle bundle = getIntent().getExtras();
        l    = bundle.getParcelable("item");
        list = getIntent().getParcelableArrayListExtra("aux");

        items = new ArrayList<Cobros_Pagos>();
        cod_vin = "";

        //widget
        listView      = (ListView) this.findViewById(R.id.lsv_lista_abonos);
        cod           = (TextView) this.findViewById(R.id.txt_cliente);
        abonos        = (TextView) this.findViewById(R.id.txt_abono);
        abonado       = (TextView) this.findViewById(R.id.txt_abonado);
        excedente     = (TextView) this.findViewById(R.id.txt_excedente);
        cta           = (EditText) this.findViewById(R.id.et_cta);
        docu          = (EditText) this.findViewById(R.id.et_docu);
        parcial       = (EditText) this.findViewById(R.id.et_parcial);
        girador       = (EditText) this.findViewById(R.id.et_girador);
        fpago         = (Spinner) this.findViewById(R.id.sp_fpago);
        banco         = (Spinner) this.findViewById(R.id.sp_banco);
        fch           = (DatePicker) this.findViewById(R.id.dp_fch);
        chk_pago      = (CheckBox) this.findViewById(R.id.chk_cheque);
        chk_vinculado = (CheckBox) this.findViewById(R.id.chk_cuentas);
        btAgregar     = (Button) this.findViewById(R.id.btn_agregar);

        bco_ch = new ArrayList<Banco>();
        bco_ch = consulta();
        bco_dp = new ArrayList<BancoDep>();
        bco_dp = consulta1();
        adaptador = new ArrayAdapter<Banco>(this, R.layout.simple_spinner_list, bco_ch);
        adaptador2 = new ArrayAdapter<BancoDep>(this, R.layout.simple_spinner_list, bco_dp);
        adaptador.setDropDownViewResource(R.layout.simple_spinner_list);
        adaptador2.setDropDownViewResource(R.layout.simple_spinner_list);
        banco.setAdapter(adaptador);

        cod.setText(cd + " - " + nom);
        girador.setText(nom);
        abonos.setText(String.valueOf(ab));
        abonado.setText("0.00");
        excedente.setText("0.00");
        receptor1 = "jimmy_ubilla@cojapan.com.ec";
        receptor4 = l.getCorreo_vend();

        if(dato.equals("RC")){
            tcob = "RC";
        }else if(dato.equals("RK")){
            tcob = "RK";
        }else if(dato.equals("RQ")){
            tcob = "RQ";
        }

        onBackPressed();
        selectItem();
        checkCuenta();
        check = false;

        btAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = parcial.getText().toString();
                String ch = docu.getText().toString();
                String cuenta = cta.getText().toString();
                String bco = banco.getSelectedItem().toString();
                String cbc = bco_ch(bco);
                int conta = 0;
                if (ch.equals("")) {
                    CharSequence text = "INGRESE UN NUMERO DE DOCUMENTO ....!!";
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
                if (cuenta.equals("")) {
                    CharSequence text = "INGRESE UN NUMERO DE CUENTA ....!!";
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
                /*for(RowFactura row: list){
                    if(row.getNum_mov().equals(ch) && row.getNum_cuenta().equals(cuenta) && row.getCod_bco().equals(cbc)){
                        conta++;
                        break;
                    }
                }
                if (conta>0) {
                    CharSequence text = "# CHEQUE INGRESADO YA ESTA SELECCIONADO PARA REALIZAR EL COBRO ....!!";
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
                    conta--;
                    return;
                }*/
                new TaskGetAbono().execute();
                if (fpago.getSelectedItem().toString().equals("DC - DOCUMENTO")){
                    int a = addSecuenciaDoc("DOC");
                    docu.setText(String.valueOf(a));
                }

                //parcial.setText("");
                //selectItem();
                parcial.requestFocus();
            }
        });

        banco.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sp = String.valueOf(fpago.getSelectedItem());
                if (sp.contentEquals("CH - CHEQUE")) {
                    cta.setEnabled(true);
                    cta.setText("");
                }
                if (sp.contentEquals("DP - DEPOSITO")) {
                    if (banco.getSelectedItem().toString().contains("GUAYAQUIL")) {
                        String bco = cNumero(banco.getSelectedItem().toString());
                        cta.setEnabled(false);
                        cta.setText(bco);
                    } else if (banco.getSelectedItem().toString().contains("PICHINCHA")) {
                        String bco = cNumero(banco.getSelectedItem().toString());
                        cta.setEnabled(false);
                        cta.setText(bco);
                    }else if (banco.getSelectedItem().toString().contains("PACIFICO")) {
                        String bco = cNumero(banco.getSelectedItem().toString());
                        cta.setEnabled(false);
                        cta.setText(bco);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        }

        );
    }

    private void selectItem() {
        fpago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (fpago.getSelectedItem().toString().equals("EF - EFECTIVO")) {
                    cta.setEnabled(false);
                    cta.setText("-");
                    docu.setEnabled(false);
                    docu.setText("-");
                    girador.setText("-");
                    girador.setEnabled(false);
                    banco.setEnabled(false);
                    fch.setEnabled(false);
                    chk_pago.setEnabled(false);
                    chk_pago.setChecked(false);
                }
                if (fpago.getSelectedItem().toString().equals("CH - CHEQUE")) {
                    int var = 9;
                    banco.setAdapter(adaptador);
                    banco.setEnabled(true);
                    cta.setEnabled(true);
                    docu.setEnabled(true);
                    docu.setText("");
                    docu.setFilters(new InputFilter[]{new InputFilter.LengthFilter(var)});
                    girador.setText(nom);
                    girador.setEnabled(true);
                    fch.setEnabled(true);
                    chk_pago.setEnabled(true);
                    chk_pago.setChecked(false);
                    estado = "F";
                    checkCheque();
                }
                if (fpago.getSelectedItem().toString().equals("DP - DEPOSITO")) {
                    int var = 15;
                    cta.setEnabled(false);
                    docu.setEnabled(true);
                    docu.setText("");
                    docu.setFilters(new InputFilter[]{new InputFilter.LengthFilter(var)});
                    girador.setText("-");
                    girador.setEnabled(false);
                    banco.setAdapter(adaptador2);
                    banco.setEnabled(true);
                    fch.setEnabled(true);
                    chk_pago.setEnabled(false);
                    chk_pago.setChecked(false);
                }
                if (fpago.getSelectedItem().toString().equals("DC - DOCUMENTO")) {
                    secD = getMaxDoc();
                    cta.setEnabled(false);
                    cta.setText("--");
                    docu.setEnabled(false);
                    docu.setText(String.valueOf(secD));
                    girador.setText("-");
                    girador.setEnabled(true);
                    banco.setEnabled(false);
                    fch.setEnabled(true);
                    chk_pago.setEnabled(false);
                    chk_pago.setChecked(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void doPago(){
        try {
            ng_abono = Double.valueOf(abonos.getText().toString());
            ng_abonado = Double.valueOf(abonado.getText().toString());
            if (listView != null && items.size() > 0) {
                if (Double.valueOf(abonado.getText().toString()) < Double.valueOf(abonos.getText().toString())) {
                    CharSequence text = "EL VALOR ABONADO ES MENOR AL VALOR POR CANCELAR ....!!";
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
                setCobros();
                if(ng_abonado>ng_abono){
                    secN = getMaxNcr();
                    if(check)
                        getNotaCreditoInternaV();
                    else
                        getNotaCreditoInterna();
                    setCobrosDocNota();
                }
                recibo = getCountSecRecibos();
                documento = getCountSecDocumentos();
                setCobrosDoc(list);
                updateFacturas(list);
                //new SendMailTask().execute();
                if(dato.equals("RC")){
                    Intent intent = new Intent(getApplicationContext(), FacturasFCActivity.class);
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
                    startActivity(intent);
                }else{
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
                    startActivity(intent);
                }

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
            } else {
                CharSequence text = "NO HA INGRESADO NINGUN ABONO ....!!";
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

    private void checkCheque(){
        chk_pago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chk_pago.isChecked()) {
                    estado = "G";
                }
                else {
                    estado = "F";
                }
            }
        });
    }

    private void checkCuenta(){
        chk_vinculado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chk_vinculado.isChecked()) {
                    //cod_vin = getCodigoVinculado(cod_cli);
                    vinculado = codVinculado(cod_cli);
                    if(vinculado.isEmpty()){
                        chk_vinculado.setChecked(false);
                        
                        CharSequence text = "CLIENTE NO TIENE CODIGOS VINCULADOS...!!";
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
                        check = true;
                    }
                }else {
                    check = false;
                    cod_vin = "";
                }
            }
        });
    }

    private class SendMailTask extends AsyncTask<String, Void, Boolean> {
        boolean resultado = false;

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            int max = getMaxRecibo();
            docCobros = load_docs(max);
            docPagos = load_cobrospagos(max);
            asunto = "COBRO REALIZADO POR "+l.getCod_vend()+", "+tcob+" #"+max+", CODIGO #"+cod_cli+" "+nom;
            busquedaClientes();
            dest_final = receptor1.trim();
            mensajeMail2();
            mensajeMail();
            resultado = SendMail2.send(dest_final, receptor4, asunto, message.toString(), message1.toString());
            return resultado;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Boolean result) {
            if(pDialog != null){
                pDialog.dismiss();
                if(result) {
                    Toast toast = Toast.makeText(getBaseContext(), "Email Enviado con Datos de Recibo(s) Cobrado(s)..!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 0);
                    toast.show();
                    System.out.println("OK envioooooo");
                }else{
                    Toast toast = Toast.makeText(getBaseContext(), "Email NO pudo ser enviado, intentelo nuevamente..!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 0);
                    toast.show();
                    System.out.println("NO envioooooo");
                }

            }

        }
    }

    void sumar() {
        sum = .0;
        if (items.size() != 0) {
            for (int i = 0; i < items.size(); i++) {
                Cobros_Pagos rows = items.get(i);
                sum = sum + rows.getValor();
            }
            ab = Utils.Redondear(sum,2);
            abonado.setText(String.valueOf(ab));
            if(Double.valueOf(abonado.getText().toString())>Double.valueOf(abonos.getText().toString())){
                double exc = Double.valueOf(abonos.getText().toString())-Double.valueOf(abonado.getText().toString());
                excedente.setText(String.valueOf(Utils.Redondear(exc,2)));
            }else{
                excedente.setText("0.00");
            }
        } else {
            abonado.setText("0.00");
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.abonos_list, parent, false);
            }

            final Cobros_Pagos item = items.get(position);

            // Set data into the view.
            Button btDelete  = (Button) rowView.findViewById(R.id.btn_borrar);
            btDelete.setTag(position);
            btDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Cobros_Pagos it = items.get(position);
                    Integer index = (Integer) v.getTag();
                    items.remove(index.intValue());
                    sumar();
                    notifyDataSetChanged();
                    int a = delSecuenciaDoc("DOC");
                    if(it.getFpago().equals("DC")) {
                        docu.setText(String.valueOf(a));
                    }else{
                        docu.setText("-");
                    }
                    selectItem();
                }
            });


            TextView tvFPago = (TextView)  rowView.findViewById(R.id.txt_fpago);
            TextView tvFch = (TextView)  rowView.findViewById(R.id.txt_fch);
            TextView tvCta = (TextView)  rowView.findViewById(R.id.txt_cta);
            TextView tvChe = (TextView)  rowView.findViewById(R.id.txt_che);
            TextView tvBanco = (TextView)  rowView.findViewById(R.id.txt_banco);
            TextView tvAbono = (TextView)  rowView.findViewById(R.id.txt_valor_abonado);

            tvFPago.setText(item.getFpago());
            tvFch.setText(item.getFch_pago());
            tvCta.setText(item.getCuenta());
            tvChe.setText(item.getCheque());
            tvBanco.setText(item.getBanco());
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
        String     fp = fpago.getSelectedItem().toString();
        long       dateTime = fch.getCalendarView().getDate();
        Date       date = new Date(dateTime);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String     formattedDate = dateFormat.format(date);
        String     cuenta = cta.getText().toString();
        String     doc = docu.getText().toString();
        String     doc1 = docu.getText().toString();
        String     bco = banco.getSelectedItem().toString();
        double     abono = Double.valueOf(parcial.getText().toString());
        String     gir = girador.getText().toString();
        double     num = 0;

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
            int s = getMaxDoc();
            int cont = 0;
            String docu = "";
            String cuen = "";
            String doc1 = "";
            String cue1 = "";


            lista1 = new Cobros_Pagos();
            try {
                if(fp.equals("EF - EFECTIVO")){
                    cod_bco = "-";
                    estado = "-";
                }else if(fp.equals("DP - DEPOSITO")){
                    cod_bco = bco_dp(bco);
                    estado = "-";
                }else if(fp.equals("CH - CHEQUE")){
                    doc = String.format("%06d", Integer.parseInt(doc));
                    cod_bco = bco_ch(bco);
                    checkCheque();
                }else if(fp.equals("DC - DOCUMENTO")){
                    cod_bco = "-";
                    estado = "-";
                }

                if(fp.equals("CH - CHEQUE")){
                    for(RowFactura row: list){
                        if(row.getCod_mov().equals("CH")){
                            docu = row.getNum_mov().replaceFirst("^0*","");
                            cuen = row.getNum_cuenta().replaceFirst("^0*","");

                            doc1 = doc.replaceFirst("^0*","");
                            cue1 = cuenta.replaceFirst("^0*","");

                            if(docu.equals(doc1) && cuen.equals(cue1) && row.getCod_bco().equals(cod_bco)){
                                num++;
                                break;
                            }
                        }
                    }
                    for (Cobros_Pagos row : items) {
                        if(row.getFpago().equals("CH")) {
                            docu = row.getCheque().replaceFirst("^0*","");
                            cuen = row.getCuenta().replaceFirst("^0*","");

                            doc1 = doc.replaceFirst("^0*","");
                            cue1 = cuenta.replaceFirst("^0*","");

                            if (docu.equals(doc1) && cuen.equals(cue1) && row.getBanco().equals(cod_bco)) {
                                num++;
                                break;
                            }
                        }
                    }
                }else if(fp.equals("EF - EFECTIVO")){
                    for(Cobros_Pagos row: items){
                        if(row.getFpago().equals("EF")){
                            num++;
                            break;
                        }
                    }
                }else if(fp.equals("DP - DEPOSITO")){
                    if(items.isEmpty()){
                        doc1 = doc.replaceFirst("^0*","");
                        cue1 = cuenta.replaceFirst("^0*","");
                    }else {
                        for (Cobros_Pagos row : items) {
                            if (row.getFpago().equals("DP")) {
                                docu = row.getCheque().replaceFirst("^0*","");
                                cuen = row.getCuenta().replaceFirst("^0*","");

                                doc1 = doc.replaceFirst("^0*","");
                                cue1 = cuenta.replaceFirst("^0*","");

                                if (docu.equals(doc1) && cuen.equals(cue1) && row.getBanco().equals(cod_bco)) {
                                    num++;
                                    break;
                                }
                            }else {
                                if(cont == 0){
                                    doc1 = doc.replaceFirst("^0*","");
                                    cue1 = cuenta.replaceFirst("^0*","");
                                    cont++;
                                }
                            }
                        }
                    }
                }

                if(num==0) {
                    lista1.setFpago(fp.substring(0, 2));
                    lista1.setFch_pago(formattedDate);
                    if(fp.equals("DP - DEPOSITO")) {
                        lista1.setCuenta(cue1.replaceFirst("^0*",""));
                        lista1.setCheque(doc1.replaceFirst("^0*",""));
                    }else{
                        lista1.setCuenta(cuenta);
                        lista1.setCheque(doc);
                    }
                    lista1.setBanco(cod_bco);
                    lista1.setValor(abono);
                    lista1.setGirador(gir);
                    lista1.setStatus(estado);
                    items.add(lista1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG," ERROR Abono ");
            }
            return num;
        }

        @Override
        protected void onPostExecute(Double a) {
            if(pDialog != null)
                pDialog.dismiss();

            String docu = "";
            String cuen = "";
            String doc1 = "";
            String cue1 = "";
            if(a>0){
                if(fp.equals("EF - EFECTIVO")){
                    for(Cobros_Pagos row: items){
                        if(row.getFpago().equals("EF")){
                            Toast toast = Toast.makeText(context, "Solo se permite un abono en efectivo!", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                }                if(fp.equals("CH - CHEQUE")){
                    for(RowFactura row: list){
                        if(row.getCod_mov().equals("CH")) {
                            docu = row.getNum_mov().replaceFirst("^0*","");
                            cuen = row.getNum_cuenta().replaceFirst("^0*","");

                            doc1 = doc.replaceFirst("^0*","");
                            cue1 = cuenta.replaceFirst("^0*","");

                            if (docu.equals(doc1) && cuen.equals(cue1) && row.getCod_bco().equals(cod_bco)) {
                                Toast toast = Toast.makeText(context, "CHEQUE YA ESTA SELECCIONADO PARA REALIZAR EL COBRO..!",
                                        Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }
                    }
                }
                if(fp.equals("CH - CHEQUE")) {
                    for (Cobros_Pagos row : items) {
                        if(row.getFpago().equals("CH")){
                            docu = row.getCheque().replaceFirst("^0*","");
                            cuen = row.getCuenta().replaceFirst("^0*","");

                            doc1 = doc.replaceFirst("^0*","");
                            cue1 = cuenta.replaceFirst("^0*","");

                            if (docu.equals(doc1) && cuen.equals(cue1) && row.getBanco().equals(cod_bco)) {
                                Toast toast = Toast.makeText(context, "Documento ya ingresado en el ABONO..!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }
                    }
                }
                if(fp.equals("DP - DEPOSITO")){
                    for (Cobros_Pagos row : items) {
                        if(row.getFpago().equals("DP")) {
                            docu = row.getCheque().replaceFirst("^0*","");
                            cuen = row.getCuenta().replaceFirst("^0*","");

                            doc1 = doc.replaceFirst("^0*","");
                            cue1 = cuenta.replaceFirst("^0*","");

                            if (docu.equals(doc1) && cuen.equals(cue1) && row.getBanco().equals(cod_bco)) {
                                Toast toast = Toast.makeText(context, "Documento ya ingresado en el ABONO..!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }
                    }
                }
            }else{
                AbonoAdapter adapter = new AbonoAdapter();
                listView.setAdapter(adapter);
                sumar();
            }
        }

    }

    public String bco_ch(String banco){
        SQLiteDatabase db = null;
        Cursor q = null;
        String dato = "";
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT cod_bco FROM BANCOS_CH WHERE cod_emp='"+l.getCod_emp()+"' AND nom_banco='"+banco+"'";
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

    public String bco_dp(String banco){
        SQLiteDatabase db = null;
        Cursor q = null;
        String dato = "";
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT cod_bco FROM BANCOS_DP WHERE cod_emp='"+l.getCod_emp()+"' AND nom_banco='"+banco+"'";
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

    public List<Banco> consulta(){
        SQLiteDatabase db = null;
        List<Banco> lista = new ArrayList<>();
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM BANCOS_CH WHERE cod_emp='"+l.getCod_emp()+"' ORDER BY nom_banco";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
                    Banco b = new Banco();
                    b.setNom_banco(q.getString(2));
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

    public List<BancoDep> consulta1(){
        SQLiteDatabase db = null;
        List<BancoDep> lista = new ArrayList<>();
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM BANCOS_DP WHERE cod_emp='"+l.getCod_emp()+"' ORDER BY cod_bco";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
                    BancoDep b = new BancoDep();
                    b.setNom_banco(q.getString(2));
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

    public String cNumero(String banco){
        SQLiteDatabase db = null;
        String b = "";
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String[] args = new String[] {banco};
            String SQL ="SELECT num_cuenta FROM BANCOS_DP WHERE cod_emp='"+l.getCod_emp()+"' AND nom_banco=?";
            q = db.rawQuery(SQL,args);
            if (q.moveToFirst()) {
                do {
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

    public void setCobros() {
        String SQL="";
        SQLiteDatabase db = null;
        try {
            AbonoAdapter adapter = (AbonoAdapter) listView.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                Cobros_Pagos rows = (Cobros_Pagos) adapter.getItem(i);
                if (rows.getFpago().equals("EF") || rows.getFpago().equals("CH") || rows.getFpago().equals("DP")) {
                    sec = getMaxRec();
                    if(l.getCod_vend().equals("V999")){
                        SQL = "INSERT INTO COBROS_PAGOS (cod_emp,id_recibo,fpago,banco,cuenta,cheque,fch_pago,valor,cod_ref,cod_vend," +
                                "cod_vin,vendedor,fch_registro,girador,cod_rec,status) VALUES " +
                                "('" +l.getCod_emp()+ "','" + sec + "','" + rows.getFpago() + "','" + rows.getBanco() + "','" + rows.getCuenta() +
                                "','" + rows.getCheque() + "','" + rows.getFch_pago() + "','" +
                                rows.getValor() + "','" + cod_cli + "','" + l.getCod_vend() + "','','" + cod_ven +"',DateTime('now','localtime'),'" +
                                rows.getGirador() + "','" + tcob + "','" + rows.getStatus() + "')";
                    }else if(l.getSeleccion().equals("V")){
                        SQL = "INSERT INTO COBROS_PAGOS (cod_emp,id_recibo,fpago,banco,cuenta,cheque,fch_pago,valor,cod_ref,cod_vend," +
                                "cod_vin,vendedor,fch_registro,girador,cod_rec,status) VALUES " +
                                "('" +l.getCod_emp()+ "','" + sec + "','" + rows.getFpago() + "','" + rows.getBanco() + "','" + rows.getCuenta() +
                                "','" + rows.getCheque() + "','" + rows.getFch_pago() + "','" +
                                rows.getValor() + "','" + cod_cli + "','" + l.getCod_vend() + "','','" + cod_ven +"',DateTime('now','localtime'),'" +
                                rows.getGirador() + "','" + tcob + "','" + rows.getStatus() + "')";
                    }else{
                        SQL = "INSERT INTO COBROS_PAGOS (cod_emp,id_recibo,fpago,banco,cuenta,cheque,fch_pago,valor,cod_ref,cod_vend," +
                                "cod_vin,vendedor,fch_registro,girador,cod_rec,status) VALUES " +
                                "('" +l.getCod_emp()+ "','" + sec + "','" + rows.getFpago() + "','" + rows.getBanco() + "','" + rows.getCuenta() +
                                "','" + rows.getCheque() + "','" + rows.getFch_pago() + "','" +
                                rows.getValor() + "','" + cod_cli + "','" + cod_ven + "','','',DateTime('now','localtime'),'" +
                                rows.getGirador() + "','" + tcob + "','" + rows.getStatus() + "')";
                    }
                } else if (rows.getFpago().equals("DC")) {
                    sec = getMaxRec();
                    if(l.getCod_vend().equals("V999")) {
                        SQL = "INSERT INTO COBROS_PAGOS (cod_emp,id_recibo,fpago,banco,cuenta,cheque,fch_pago,valor,cod_ref,cod_vend," +
                                "cod_vin,vendedor,fch_registro,girador,cod_rec,status) VALUES " +
                                "('" +l.getCod_emp()+ "','" + sec + "','" + rows.getFpago() + "','" + rows.getBanco() + "','" + rows.getCuenta() +
                                "','" + rows.getCheque() + "','" + rows.getFch_pago() + "','" +
                                rows.getValor() + "','" + cod_cli + "','" + l.getCod_vend() + "','','" + cod_ven +"',DateTime('now','localtime'),'" +
                                rows.getGirador() + "','" + tcob + "','" + rows.getStatus() + "')";
                    }else if(l.getSeleccion().equals("V")) {
                        SQL = "INSERT INTO COBROS_PAGOS (cod_emp,id_recibo,fpago,banco,cuenta,cheque,fch_pago,valor,cod_ref,cod_vend," +
                                "cod_vin,vendedor,fch_registro,girador,cod_rec,status) VALUES " +
                                "('" +l.getCod_emp()+ "','" + sec + "','" + rows.getFpago() + "','" + rows.getBanco() + "','" + rows.getCuenta() +
                                "','" + rows.getCheque() + "','" + rows.getFch_pago() + "','" +
                                rows.getValor() + "','" + cod_cli + "','" + l.getCod_vend() + "','','" + cod_ven +"',DateTime('now','localtime'),'" +
                                rows.getGirador() + "','" + tcob + "','" + rows.getStatus() + "')";
                    }else{
                        SQL = "INSERT INTO COBROS_PAGOS (cod_emp,id_recibo,fpago,banco,cuenta,cheque,fch_pago,valor,cod_ref,cod_vend," +
                                "cod_vin,vendedor,fch_registro,girador,cod_rec,status) VALUES " +
                                "('" + l.getCod_emp() + "','" + sec + "','" + rows.getFpago() + "','" + rows.getBanco() + "','" + rows.getCuenta() +
                                "','" + rows.getCheque() + "','" + rows.getFch_pago() + "','" +
                                rows.getValor() + "','" + cod_cli + "','"+ cod_ven + "','','',DateTime('now','localtime'),'" +
                                rows.getGirador() + "','" + tcob + "','" + rows.getStatus() + "')";
                    }
                }
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }finally {
            db.close();
        }


    }

    public void setCobrosDoc(List<RowFactura> lista){
        List<RowFactura> result = lista;
        String temp = "";
        String SQL = "";
        SQLiteDatabase db = null;
        try {
            for (RowFactura row : result) {
                String femi = row.getFec_emis();
                String fven = row.getFec_venc();
                if(row.getCod_bco().equals(""))
                    row.setCod_bco("-");
                if(row.getNum_cuenta().equals(""))
                    row.setNum_cuenta("-");

                if(l.getCod_vend().equals("V999")){
                    SQL = "INSERT INTO COBROS_DOC (cod_emp,id_recibo,fch_emision,cod_cli,nom_cli,cod_vend,cod_vin,vendedor," +
                            "cod_mov,num_mov,fch_emi,fch_ven,tipo_cobro,num_doc,cod_bco,num_cuenta,valor,pto_vta,estado) VALUES " +
                            "('" +l.getCod_emp()+ "','" + sec + "',DateTime('now','localtime'),'" + cod_cli + "','" + nom + "','" +
                            l.getCod_vend() + "','','"+ cod_ven +"','" + row.getCod_mov() + "','" + row.getNum_mov() +
                            "','" + femi + "','" + fven + "','" + tcob + "','" + temp + "','" + row.getCod_bco() + "','" + row.getNum_cuenta() +
                            "','" + row.getAbono() + "','" +row.getCod_pto()+ "','" + 0 + "')";
                }else if(l.getSeleccion().equals("V")){
                    SQL = "INSERT INTO COBROS_DOC (cod_emp,id_recibo,fch_emision,cod_cli,nom_cli,cod_vend,cod_vin,vendedor," +
                            "cod_mov,num_mov,fch_emi,fch_ven,tipo_cobro,num_doc,cod_bco,num_cuenta,valor,pto_vta,estado) VALUES " +
                            "('" +l.getCod_emp()+ "','" + sec + "',DateTime('now','localtime'),'" + cod_cli + "','" + nom + "','" +
                            l.getCod_vend() + "','','"+ cod_ven +"','" + row.getCod_mov() + "','" + row.getNum_mov() +
                            "','" + femi + "','" + fven + "','" + tcob + "','" + temp + "','" + row.getCod_bco() + "','" + row.getNum_cuenta() +
                            "','" + row.getAbono() + "','" +row.getCod_pto()+ "','" + 0 + "')";
                }else{
                    SQL = "INSERT INTO COBROS_DOC (cod_emp,id_recibo,fch_emision,cod_cli,nom_cli,cod_vend,cod_vin,vendedor," +
                            "cod_mov,num_mov,fch_emi,fch_ven,tipo_cobro,num_doc,cod_bco,num_cuenta,valor,pto_vta,estado) VALUES " +
                            "('" +l.getCod_emp()+ "','" + sec + "',DateTime('now','localtime'),'" + cod_cli + "','" + nom + "','" + cod_ven +
                            "','','','" + row.getCod_mov() + "','" + row.getNum_mov() +
                            "','" + femi + "','" + fven + "','" + tcob + "','" + temp + "','" + row.getCod_bco() + "','" + row.getNum_cuenta() +
                            "','" + row.getAbono() + "','" +row.getCod_pto()+ "','" + 0 + "')";
                }
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
            if(recibo>0)
                setSecuencia("REC");
            if(documento>0)
                setSecuencia("DOC");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Exception Error COBROS DOC.. " + e.getMessage());
        }finally {
            db.close();
        }
    }

    public void getNotaCreditoInterna(){
        Date mifch = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fDate = dateFormat.format(mifch);
        String SQL = "";
        SQLiteDatabase db = null;
        try {
            double valor1 = Utils.Redondear((ng_abonado-ng_abono),2)*(-1);
            double valor = Utils.Redondear((ng_abonado-ng_abono),2);
            if(l.getCod_vend().equals("V999")){
                SQL = "INSERT INTO DOC_LOCAL (cod_emp,cod_mov,cod_pto,cod_ref,cod_vin,cod_ven,vendedor,dias_pend,fec_emi,fec_ven,num_mov," +
                        "num_rel,sdo_mov,val_mov,iva,base,sts_mov,cod_bco,num_cuenta) VALUES ("+
                        "'"+l.getCod_emp()+"','NA','02','" + cod_cli + "','" + cod_vin + "','" + l.getCod_vend()+ "','" + cod_ven + "'," + 0 + ",'"+
                        fDate+"','"+fDate+"','" + secN + "','" + sec  + "','" +
                        valor1 + "','" + valor + "','" + 0 + "','" + 0 + "','" + 0 + "','-','-')";
            }else if(l.getSeleccion().equals("V")){
                SQL = "INSERT INTO DOC_LOCAL (cod_emp,cod_mov,cod_pto,cod_ref,cod_vin,cod_ven,vendedor,dias_pend,fec_emi,fec_ven,num_mov," +
                        "num_rel,sdo_mov,val_mov,iva,base,sts_mov,cod_bco,num_cuenta) VALUES ("+
                        "'"+l.getCod_emp()+"','NA','02','" + cod_cli + "','" + cod_vin + "','" + l.getCod_vend()+ "','" + cod_ven + "'," + 0 + ",'"+
                        fDate+"','"+fDate+"','" + secN + "','" + sec  + "','" +
                        valor1 + "','" + valor + "','" + 0 + "','" + 0 + "','" + 0 + "','-','-')";
            }else{
                SQL = "INSERT INTO DOC_LOCAL (cod_emp,cod_mov,cod_pto,cod_ref,cod_vin,cod_ven,vendedor,dias_pend,fec_emi,fec_ven,num_mov," +
                        "num_rel,sdo_mov,val_mov,iva,base,sts_mov,cod_bco,num_cuenta) VALUES ("+
                        "'"+l.getCod_emp()+"','NA','02','" + cod_cli + "','" + cod_vin + "','" + cod_ven + "',''," + 0 + ",'"+
                        fDate+"','"+fDate+"','" + secN + "','" + sec  + "','" +
                        valor1 + "','" + valor + "','" + 0 + "','" + 0 + "','" + 0 + "','-','-')";
            }
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            setSecuenciaNota("NSC");
        } catch (Exception e) {
            Log.e(TAG, " Exception Error Nota Credito Interna " + e.getMessage());
        }finally {
            db.close();
        }

    }

    public void getNotaCreditoInternaV(){
        Date mifch = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fDate = dateFormat.format(mifch);
        SQLiteDatabase db = null;
        String SQL = "";
        try {
            double valor1 = Utils.Redondear((ng_abonado-ng_abono),2)*(-1);
            double valor = Utils.Redondear((ng_abonado-ng_abono),2);

            for(ClientesRel obj: vinculado) {
                if(l.getCod_vend().equals("V999")) {
                    SQL = "INSERT INTO DOC_LOCAL (cod_emp,cod_mov,cod_pto,cod_ref,cod_vin,cod_ven,vendedor,dias_pend,fec_emi," +
                            "fec_ven,num_mov,num_rel,sdo_mov,val_mov,iva,base,sts_mov,cod_bco,num_cuenta) VALUES (" +
                            "'" + l.getCod_emp() + "','NA','02','" + cod_cli + "','" + obj.getCod_vin() + "','" + l.getCod_vend() + "'," + "','" + cod_ven + "'," +
                            0 + ",'" + fDate + "','" + fDate + "','" + secN + "','" + sec + "','" +
                            valor1 + "','" + valor + "','" + 0 + "','" + 0 + "','" + 0 + "','-','-')";
                }else if(l.getSeleccion().equals("V")) {
                    SQL = "INSERT INTO DOC_LOCAL (cod_emp,cod_mov,cod_pto,cod_ref,cod_vin,cod_ven,vendedor,dias_pend,fec_emi," +
                            "fec_ven,num_mov,num_rel,sdo_mov,val_mov,iva,base,sts_mov,cod_bco,num_cuenta) VALUES (" +
                            "'" + l.getCod_emp() + "','NA','02','" + cod_cli + "','" + obj.getCod_vin() + "','" + l.getCod_vend() + "'," + "','" + cod_ven + "'," +
                            0 + ",'" + fDate + "','" + fDate + "','" + secN + "','" + sec + "','" +
                            valor1 + "','" + valor + "','" + 0 + "','" + 0 + "','" + 0 + "','-','-')";
                }else{
                    SQL = "INSERT INTO DOC_LOCAL (cod_emp,cod_mov,cod_pto,cod_ref,cod_vin,cod_ven,vendedor,dias_pend,fec_emi," +
                            "fec_ven,num_mov,num_rel,sdo_mov,val_mov,iva,base,sts_mov,cod_bco,num_cuenta) VALUES (" +
                            "'" + l.getCod_emp() + "','NA','02','" + cod_cli + "','" + obj.getCod_vin() + "','" + cod_ven + "',''," +
                            0 + ",'" + fDate + "','" + fDate + "','" + secN + "','" + sec + "','" +
                            valor1 + "','" + valor + "','" + 0 + "','" + 0 + "','" + 0 + "','-','-')";
                }
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
            setSecuenciaNota("NSC");
        } catch (Exception e) {
            Log.e(TAG, " Exception Error Nota Credito InternaV " + e.getMessage());
        }finally {
            db.close();
        }

        /*for(ClientesRel obj: vinculado) {
            String SQL = "INSERT INTO DOC_LOCAL (cod_emp,cod_mov,cod_pto,cod_ref,cod_vin,cod_ven,dias_pend,fec_emi,fec_ven,num_mov,num_rel,sdo_mov,val_mov,iva,base,sts_mov,cod_bco,num_cuenta) VALUES (" +
                         "'02','NA','02','" + cod_cli + "','" + obj.getCod_vin() + "','" + cod_ven + "'," + 0 + ",'" + fDate + "','" + fDate + "','" + secN + "','" + sec + "','" +
                         valor1 + "','" + valor + "','" + 0 + "','" + 0 + "','" + 0 + "','-','-')";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
        }*/

    }

    public void setCobrosDocNota(){
        SQLiteDatabase db = null;
        Date mifch = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String fDate = dateFormat.format(mifch);
        String aux = "NA";
        String SQL = "";
        try {
            double valor = Utils.Redondear((ng_abonado-ng_abono),2)*(-1);
            if(l.getCod_vend().equals("V999")) {
                SQL = "INSERT INTO COBROS_PAGOS (cod_emp,id_recibo,fpago,banco,cuenta,cheque,fch_pago,valor,cod_ref,cod_vend," +
                        "cod_vin,vendedor,fch_registro,girador,cod_rec,status) VALUES " +
                        "('" + l.getCod_emp() + "','" + sec + "','" + aux + "','-','-','" + secN + "','" + fDate + "'," + valor + ",'" + cod_cli +
                        "','" + l.getCod_vend() + "','','"+ cod_ven+ "',DateTime('now','localtime'),'-','" + tcob + "','P')";
            }else if(l.getSeleccion().equals("V")) {
                SQL = "INSERT INTO COBROS_PAGOS (cod_emp,id_recibo,fpago,banco,cuenta,cheque,fch_pago,valor,cod_ref,cod_vend," +
                        "cod_vin,vendedor,fch_registro,girador,cod_rec,status) VALUES " +
                        "('" + l.getCod_emp() + "','" + sec + "','" + aux + "','-','-','" + secN + "','" + fDate + "'," + valor + ",'" + cod_cli +
                        "','" + l.getCod_vend() + "','','"+ cod_ven+ "',DateTime('now','localtime'),'-','" + tcob + "','P')";
            }else {
                SQL = "INSERT INTO COBROS_PAGOS (cod_emp,id_recibo,fpago,banco,cuenta,cheque,fch_pago,valor,cod_ref,cod_vend," +
                        "cod_vin,vendedor,fch_registro,girador,cod_rec,status) VALUES " +
                        "('" + l.getCod_emp() + "','" + sec + "','" + aux + "','-','-','" + secN + "','" + fDate + "'," + valor + ",'" + cod_cli +
                        "','" + cod_ven + "','','',DateTime('now','localtime'),'-','" + tcob + "','P')";
            }
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
        } catch (Exception e) {
            Log.e(TAG, " Exception Error CobrosDocNota. " + e.getMessage());
        }finally{
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
                        "WHERE tipo='REC' AND cod_emp='"+l.getCod_emp()+"' AND cod_ven='"+l.getCod_vend()+"'";
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
        }
        System.out.println("secuencia "+c);
        return c;
    }

    public int getMaxDoc(){
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT MAX(secuencia) FROM SECUENCIA " +
                        "WHERE tipo='DOC' AND cod_emp='"+l.getCod_emp()+"' AND cod_ven='"+l.getCod_vend()+"'";
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
        }
        System.out.println("secuencia "+c);
        return c;
    }

    public int getMaxNcr(){
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT MAX(secuencia) FROM SECUENCIA " +
                        "WHERE tipo='NSC' AND cod_emp='"+l.getCod_emp()+"' AND cod_ven='"+l.getCod_vend()+"'";
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
        }
        System.out.println("secuencia "+c);
        return c;
    }

    public void setSecuencia(String id){
        String SQL = "";
        int s = 0;
        SQLiteDatabase db = null;
        try {
            if(id.equals("REC")){
                s = sec+1;
                SQL = "UPDATE SECUENCIA SET secuencia="+s+" WHERE tipo='"+id+"' AND cod_emp='"+l.getCod_emp()+"' AND cod_ven='"+l.getCod_vend()+"'";
            }else if(id.equals("DOC")){
                s = secD+1;
                SQL = "UPDATE SECUENCIA SET secuencia="+s+" WHERE tipo='"+id+"' AND cod_emp='"+l.getCod_emp()+"' AND cod_ven='"+l.getCod_vend()+"'";
            }
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }finally{
            db.close();
        }

    }

    public String getCodigoVinculado(String cod){
        String c = "";
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT cod_vin FROM CLIENTES_REL " +
                        "WHERE cod_emp='"+l.getCod_emp()+"' AND cod_ref='"+cod+"'";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
                    c = q.getString(3);
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

    public ArrayList<ClientesRel> codVinculado(String cod){
        cdvin = new ArrayList<ClientesRel>();
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM CLIENTES_REL " +
                        "WHERE cod_emp='"+l.getCod_emp()+"' AND cod_ref='"+cod+"'";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
                    vincul = new ClientesRel();
                    vincul.setEmpresa(q.getString(0));
                    vincul.setCod_ref(q.getString(1));
                    vincul.setCod_vin(q.getString(2));
                    vincul.setOrd_vin(q.getInt(3));
                    cdvin.add(vincul);
                } while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return cdvin;
    }

    public int addSecuenciaDoc(String id){
        String SQL = "";
        int s = 0;
        SQLiteDatabase db = null;
        try {
            if(id.equals("DOC")){
                secD = getMaxDoc();
                s = secD+1;
                SQL = "UPDATE SECUENCIA SET secuencia="+s+" WHERE tipo='"+id+"' AND cod_emp='"+l.getCod_emp()+"' AND cod_ven='"+l.getCod_vend()+"'";
            }
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }finally{
            db.close();
        }
        return s;
    }

    public int delSecuenciaDoc(String id){
        String SQL = "";
        int s = 0;
        SQLiteDatabase db = null;
        try {
            if(id.equals("DOC")){
                secD = getMaxDoc();
                s = secD-1;
                SQL = "UPDATE SECUENCIA SET secuencia="+s+" WHERE tipo='"+id+"' AND cod_emp='"+l.getCod_emp()+"' AND cod_ven='"+l.getCod_vend()+"'";
            }
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }finally{
            db.close();
        }
        return s;
    }

    public void setSecuenciaNota(String id){
        int s = secN+1;
        SQLiteDatabase db = null;
        try {
            String SQL = "UPDATE SECUENCIA SET secuencia="+s+" WHERE tipo='"+id+"' AND cod_emp='"+l.getCod_emp()+"' AND cod_ven='"+l.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }finally{
            db.close();
        }
    }

    public int getCountSecRecibos(){
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT COUNT(*) FROM COBROS_PAGOS " +
                        "WHERE fpago='EF' OR fpago='CH' OR fpago='DP' AND cod_emp='"+l.getCod_emp()+"' AND cod_vend='"+l.getCod_vend()+"'";
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

    public int getCountSecDocumentos() {
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL = "SELECT COUNT(*) FROM COBROS_PAGOS " +
                         "WHERE fpago='DC' AND cod_emp='"+l.getCod_emp()+"' AND cod_vend='"+l.getCod_vend()+"'";
            q = db.rawQuery(SQL, null);
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
        System.out.println("secuencia " + c);
        return c;
    }

    public void updateFacturas(List<RowFactura> lista){
        List<RowFactura> result = lista;
        double saldo = 0.00;
        SQLiteDatabase db = null;
        try {
            for (RowFactura row : result) {
                saldo = 0.00;
                if(row.getSdo_mov()<0)
                    saldo = row.getSdo_mov()+row.getAbono();
                else
                    saldo = row.getSdo_mov()-row.getAbono();
                String s = String.valueOf(Utils.Redondear(saldo,2));
                String SQL = "UPDATE DOC_WS SET sdo_mov=" + Double.valueOf(s) + " " +
                             "WHERE num_mov='" + row.getNum_mov() + "' AND cod_emp='"+l.getCod_emp()+"' AND cod_ven='"+cod_ven+"'";
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

    public int getMaxRecibo(){
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT MAX(id_recibo) FROM COBROS_DOC WHERE cod_emp='"+l.getCod_emp()+"'";
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

    public ArrayList<Cobros_Pagos> load_cobrospagos(int recibo){
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT DISTINCT cp.* FROM COBROS_PAGOS cp " +
                        "WHERE cp.id_recibo="+recibo+" AND cp.cod_emp='"+l.getCod_emp()+"' AND cp.cod_vend='"+l.getCod_vend()+"' " +
                        "ORDER BY cp.fpago";
            pagos = new ArrayList<Cobros_Pagos>();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    int id = q.getInt(0);
                    int idrecibo = q.getInt(1);
                    String emp = q.getString(2);
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

    public ArrayList<Cobros_Doc> load_docs(int recibo){
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT DISTINCT cd.* FROM COBROS_DOC cd " +
                        "WHERE cd.id_recibo="+recibo+" AND cd.cod_emp='"+l.getCod_emp()+"' AND cd.cod_vend='"+l.getCod_vend()+"' " +
                        "ORDER BY cd.cod_mov, cd.num_mov";
            cobro = new ArrayList<Cobros_Doc>();
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
                    cobro.add(cd);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return cobro;
    }

    void busquedaClientes(){
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String[] args = new String[] {cod_cli.trim()};
            String SQL ="SELECT * FROM CLIENTES " +
                        "WHERE cod_emp='"+l.getCod_emp()+"' AND vendedor='"+cod_ven+"' AND cliente = ? ";
            cli = new RowCliente();
            q = db.rawQuery(SQL,args);
            if (q.moveToFirst()){
                do {
                    String emp  = q.getString(1);
                    String codc = q.getString(2);
                    String nom1 = q.getString(3);
                    String nom2 = q.getString(4);
                    String dire = q.getString(5);
                    String ciud = q.getString(6);
                    String prov = q.getString(7);
                    String ruc  = q.getString(8);
                    String telf = q.getString(9);
                    String vend = q.getString(10);
                    double desc = q.getDouble(11);
                    String fpag = q.getString(12);
                    String mai1 = q.getString(13);
                    String mai2 = q.getString(14);
                    String pers = q.getString(15);
                    cli = new RowCliente(emp,codc,nom1,nom2,dire,ciud,prov,ruc,telf,vend,desc,fpag,mai1,mai2,pers);
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

    public void mensajeMail(){
        message = new StringBuilder();
        String banco="";
        suma2 = .0;
        message.append("<h4><b>FORMAS DE PAGO</b></h4><br><br>" +
                "<table border=\"2px\">" +
                "<tr>" +
                "<td width=100><b>TIPO DOC</b></td>" +
                "<td width=100><b># DOC.</b></td>" +
                "<td width=100><b>FECHA EMISION</b></td>" +
                "<td width=100><b>FECHA VENCIMIENTO</b></td>" +
                "<td width=100><b>BANCO</b></td>" +
                "<td width=100><b>CUENTA</b></td>" +
                "<td width=100><b>VALOR</b></td>" +
                "</tr>");
        for(Cobros_Pagos row: docPagos){
            /*if(!row.getBanco().equals("-")){
                banco = bco_ch(row.getBanco());
            }else{
                banco = "-";
            }*/
            message.append("<tr>" +
                    "<td width=100>" + row.getFpago() + "</td>" +
                    "<td width=100>" + row.getCheque() + "</td>" +
                    "<td width=100>" + row.getFch_pago() + "</td>" +
                    "<td width=100>" + row.getFch_pago() + "</td>" +
                    "<td width=100>" + row.getBanco() + "</td>" +
                    "<td width=100>" + row.getCuenta() + "</td>" +
                    "<td width=100 align=RIGHT>" + row.getValor() + "</td>" +
                    "</tr>");
            suma2 = suma2 + row.getValor();
        }
        message.append("<tr>"+
                "<td width=100></td>" +
                "<td width=100></td>" +
                "<td width=100></td>" +
                "<td width=100></td>" +
                "<td width=100></td>" +
                "<td width=100><b>SUBTOTAL:</b></td>"+
                "<td width=100 align=RIGHT><b>" +Utils.Redondear(suma2,2)+ "<b></td>"+
                "</tr>");
        message.append("</table><br><br>");
        total = suma - suma2;
        message.append("<table border=\"2px\">" +
                "<tr>" +
                "<td width=100></td>" +
                "<td width=100></td>" +
                "<td width=100></td>" +
                "<td width=100></td>" +
                "<td width=200><b>TOTAL PAGOS:</b></td>"+
                "<td width=100 align=RIGHT><b>" +Utils.Redondear(suma2,2)+ "<b></td>"+
                "</tr>"+
                "</table><br><br>" +

                "<table border=\"2px\">" +
                "<tr>" +
                "<td width=100></td>" +
                "<td width=100></td>" +
                "<td width=100></td>" +
                "<td width=100></td>" +
                "<td width=200><b>VALOR EXCEDENTE:</b></td>"+
                "<td width=100 align=RIGHT><b>" +Utils.Redondear(total,2)+ "<b></td>"+
                "</tr>"+
                "</table>");
    }

    public void mensajeMail2(){
        message1 = new StringBuilder();
        suma = .0;
        message1.append("<h3><b>DOCUMENTOS PENDIENTES</b></h3><br><br>" +

                "<table border=\"2px\">" +
                "<tr>" +
                "<td width=100><b>TIPO DOC</b></td>" +
                "<td width=100><b># DOC.</b></td>" +
                "<td width=100><b>FECHA EMISION</b></td>" +
                "<td width=100><b>FECHA VENCIMIENTO</b></td>" +
                "<td width=100><b>BANCO</b></td>" +
                "<td width=100><b>CUENTA</b></td>" +
                "<td width=100><b>ABONO</b></td>" +
                "</tr>");
        for(Cobros_Doc row: docCobros){
            if(row.getCod_mov().equals("NG")){

            }else {
                message1.append("<tr>" +
                        "<td width=100>" + row.getCod_mov() + "</td>" +
                        "<td width=100>" + row.getNum_mov() + "</td>" +
                        "<td width=100>" + row.getFch_emi() + "</td>" +
                        "<td width=100>" + row.getFch_ven() + "</td>" +
                        "<td width=100>" + row.getCod_bco() + "</td>" +
                        "<td width=100>" + row.getNum_cuenta() + "</td>" +
                        "<td width=100 align=RIGHT>" + row.getValor() + "</td>" +
                        "</tr>");
                 suma = suma + row.getValor();
            }
        }
        message1.append("<tr>"+
                        "<td width=100></td>" +
                        "<td width=100></td>" +
                        "<td width=100></td>" +
                        "<td width=100></td>" +
                        "<td width=100></td>" +
                        "<td width=100><b>SUBTOTAL:</b></td>"+
                        "<td width=100 align=RIGHT><b>" +Utils.Redondear(suma,2)+ "</b></td>"+
                        "</tr>");
        message1.append("</table><br><br>");
        message1.append("<table border=\"2px\">" +
                "<tr>" +
                "<td width=100></td>" +
                "<td width=100></td>" +
                "<td width=100></td>" +
                "<td width=100></td>" +
                "<td width=200><b>TOTAL DOCUMENTOS PENDIENTES:</b></td>"+
                "<td width=100 align=RIGHT><b>" +Utils.Redondear(suma,2)+ "</b></td>"+
                "</tr>"+
                "</table><br><br>");
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
                if(dato.equals("RC")){
                    Intent intent = new Intent(getApplicationContext(), FacturasFCActivity.class);
                    intent.putExtra("nomb_cli", nom);
                    intent.putExtra("cod_cli", cd);
                    intent.putExtra("persona", pers);
                    intent.putExtra("codaux", codaux);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("item", l);
                    intent.putExtra("dato", dato);
                    intent.putExtra("titulo", tf);
                    intent.putExtra("titulo1", tr);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    return true;
                }else{
                    Intent intent = new Intent(getApplicationContext(), FacturasActivity.class);
                    intent.putExtra("nomb_cli", nom);
                    intent.putExtra("cod_cli", cd);
                    intent.putExtra("persona", pers);
                    intent.putExtra("codaux", codaux);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("item", l);
                    intent.putExtra("dato", dato);
                    intent.putExtra("titulo", tf);
                    intent.putExtra("titulo1", tr);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
