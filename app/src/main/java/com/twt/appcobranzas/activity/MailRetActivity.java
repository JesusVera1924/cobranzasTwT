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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.model.Clientes;
import com.twt.appcobranzas.model.CobrosRet;
import com.twt.appcobranzas.model.FacturaTemp;
import com.twt.appcobranzas.model.Vendedor;
import com.twt.appcobranzas.utils.DbHelper;
import com.twt.appcobranzas.utils.SendMail;

/**
 * Created by Devecua on 29/04/2016.
 */
public class MailRetActivity extends AppCompatActivity {
    String TAG = "MailRetActivity";
    Context context;
    List<CobrosRet> items;
    List<FacturaTemp> items1;
    Clientes cli;
    Button btEnviar;
    EditText destinatarios1, destinatarios2, destinatarios3, destinatarios4, asunto, mensaje;
    Vendedor l;
    DbHelper dbSql = null;
    String ret, fch, numero, codigo;
    int recibo;
    double valor;
    StringBuilder message, message1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail);
        context = this;
        dbSql = new DbHelper(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        recibo = getIntent().getExtras().getInt("id_recibo");
        ret = getIntent().getExtras().getString("num_retencion");
        fch = getIntent().getExtras().getString("fecha");
        numero = getIntent().getExtras().getString("numero");
        codigo = getIntent().getExtras().getString("cod_cli");
        valor = getIntent().getExtras().getDouble("valor");
        Bundle bundle = getIntent().getExtras();
        l = bundle.getParcelable("item");
        items = getIntent().getParcelableArrayListExtra("listretenciones");
        items1 = getIntent().getParcelableArrayListExtra("listdocs");

        //widget
        destinatarios1  = (EditText) this.findViewById(R.id.et_mail1);
        destinatarios2  = (EditText) this.findViewById(R.id.et_mail2);
        destinatarios3  = (EditText) this.findViewById(R.id.et_mail3);
        destinatarios4 = (EditText) this.findViewById(R.id.et_mail4);
        asunto         = (EditText) this.findViewById(R.id.et_asunto);
        mensaje        = (EditText) this.findViewById(R.id.et_mensaje);

        btEnviar = (Button) this.findViewById(R.id.btn_enviar);

        onBackPressed();cli = clientes(codigo);

        destinatarios1.setText(cli.getMail1());
        destinatarios2.setText("e_sotomayor@cojapan.com.ec");
        destinatarios4.setText(l.getCorreo_vend()+","+"astiages1932@hotmail.com");
        destinatarios1.setEnabled(false);
        destinatarios2.setEnabled(false);
        destinatarios4.setEnabled(false);
        asunto.setText(fch + " - Recibo # " + recibo);
        mensajeMail();
        mensajeMail2();

        mensaje.setText(message.toString()+"\n"+message1.toString());

        btEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dest1 = destinatarios1.getText().toString();
                if(dest1.equals("")){
                    CharSequence text = "INGRESE POR LO MENOS UN DESTINATARIO PARA EL CORREO ....!!";
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
                    new SendMailTask().execute();
                }
            }
        });

    }

    public void mensajeMail(){
        message = new StringBuilder();
        for(CobrosRet row: items){
            message.append("Retención # "+row.getEstab()+"-"+row.getPto_emi()+"-"+row.getSecuencia()+" realizada el "+row.getFecha()+" reteniendo el "+
                           row.getPrc_retencion()+"% por un valor de "+row.getValor()+".");
            message.append("\n");
        }
    }

    public void mensajeMail2(){
        message1 = new StringBuilder();
        for(FacturaTemp row: items1){
                message1.append("Retención a "+row.getCod_mov()+" # "+row.getNum_mov()+" con valor BASE "+
                                row.getBase()+" y valor IVA "+row.getIva()+".");
                message1.append("\n");
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

    private class SendMailTask extends AsyncTask<String, Void, Boolean> {
        String receptor1 = destinatarios1.getText().toString();
        String receptor2 = destinatarios2.getText().toString();
        String receptor3 = destinatarios3.getText().toString();
        String receptor4 = destinatarios4.getText().toString();;
        String asun = asunto.getText().toString();
        String dest_final = "";
        boolean resultado = false;

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Enviando correo..");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
            System.out.println("paso onPreExecute Correo  ");
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            if(receptor3.isEmpty()){
                dest_final = receptor1.trim()+","+receptor2.trim();
            }else{
                dest_final = receptor1.trim()+","+receptor2.trim()+","+receptor3.trim();
            }
            resultado = SendMail.send(dest_final, receptor4, asun, message.toString().replaceAll("\\n", "<br />"), message1.toString().replaceAll("\\n", "<br />"));
            return resultado;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Boolean result) {
            if(pDialog != null){
                pDialog.dismiss();
                if(result){
                    Toast toast = Toast.makeText(getBaseContext(), "Email Enviado..!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                    toast.show();
                    Intent intent = new Intent(getApplicationContext(), RecibosRetActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("lista", l);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    Toast toast = Toast.makeText(getBaseContext(), "Email NO pudo ser enviado, intentelo nuevamente..!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                    toast.show();
                    Intent intent = new Intent(getApplicationContext(), RecibosRetActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("lista", l);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }

        }
    }

    public Clientes clientes(String codigo){
        SQLiteDatabase db = null;
        Cursor q = null;
        Clientes v = new Clientes();
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM CLIENTES WHERE cod_emp='"+l.getCod_emp()+"' AND cliente='"+codigo+"'";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    v.setEmpresa(q.getString(1));
                    v.setCliente(q.getString(2));
                    v.setN_cliente(q.getString(3));
                    v.setN_clienteaux(q.getString(4));
                    v.setDireccion(q.getString(5));
                    v.setCiudad(q.getString(6));
                    v.setProvincia(q.getString(7));
                    v.setRuc(q.getString(8));
                    v.setTelefono(q.getString(9));
                    v.setVendedor(q.getString(10));
                    v.setDescuento(q.getDouble(11));
                    v.setForma_pago(q.getString(12));
                    v.setMail1(q.getString(13));
                    v.setMail2(q.getString(14));
                    v.setPersona(q.getString(15));
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return v;
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
                Intent intent = new Intent(getApplicationContext(), RecibosRetActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("lista", l);
                intent.putExtras(bundle);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
