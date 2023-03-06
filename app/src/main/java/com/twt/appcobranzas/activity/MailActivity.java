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
import com.twt.appcobranzas.model.Cobros_Doc;
import com.twt.appcobranzas.model.Cobros_Pagos;
import com.twt.appcobranzas.model.Vendedor;
import com.twt.appcobranzas.utils.DbHelper;
import com.twt.appcobranzas.utils.SendMail;

/**
 * Created by Devecua on 29/04/2016.
 */
public class MailActivity extends AppCompatActivity {
    String TAG = "MailActivity";
    Context context;
    List<Cobros_Pagos> items;
    List<Cobros_Doc> items1;
    Clientes cli;
    Button btEnviar;
    EditText destinatarios1, destinatarios2, destinatarios3, destinatarios4, asunto, mensaje;
    Vendedor l;
    DbHelper dbSql = null;
    String nom, fch, numero, codigo, cobro;
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
        cobro = getIntent().getExtras().getString("cobro");
        nom = getIntent().getExtras().getString("nomb_cli");
        fch = getIntent().getExtras().getString("fecha");
        numero = getIntent().getExtras().getString("numero");
        codigo = getIntent().getExtras().getString("cod_cli");
        valor = getIntent().getExtras().getDouble("valor");
        Bundle bundle = getIntent().getExtras();
        l = bundle.getParcelable("item");
        items = getIntent().getParcelableArrayListExtra("listpagos");
        items1 = getIntent().getParcelableArrayListExtra("listcobros");

        //widget
        destinatarios1  = (EditText) this.findViewById(R.id.et_mail1);
        destinatarios2  = (EditText) this.findViewById(R.id.et_mail2);
        destinatarios3  = (EditText) this.findViewById(R.id.et_mail3);
        destinatarios4 = (EditText) this.findViewById(R.id.et_mail4);
        asunto         = (EditText) this.findViewById(R.id.et_asunto);
        mensaje        = (EditText) this.findViewById(R.id.et_mensaje);

        btEnviar = (Button) this.findViewById(R.id.btn_enviar);

        onBackPressed();
        cli = clientes(codigo);

        destinatarios1.setText(cli.getMail1());
        destinatarios2.setText("e_sotomayor@cojapan.com.ec");
        destinatarios4.setText(l.getCorreo_vend()+","+"astiages1932@hotmail.com");
        destinatarios1.setEnabled(true);
        destinatarios2.setEnabled(true);
        destinatarios4.setEnabled(true);
        asunto.setText(fch + " - Recibo # " + recibo + " - " + nom);
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
                    updateReciboCorreo(recibo, cobro);
                }
            }
        });

    }

    public void mensajeMail(){
        message = new StringBuilder();
        String banco="";
        for(Cobros_Pagos row: items){
            if(!row.getBanco().equals("-")){
                banco = "BCO. "+bco_ch(row.getBanco());
            }

            if(!banco.isEmpty()){
                message.append("Pago realizado el "+row.getFch_pago()+", en "+banco+
                        " :: CTA# "+row.getCuenta()+" - "+row.getFpago()+"# "+row.getCheque()+" por un valor de "+row.getValor()+".");
                message.append("\n");
            }else{
                message.append("Pago realizado el "+row.getFch_pago()+", en "+row.getFpago()+
                        " :: # "+row.getCuenta()+" :: por un valor de "+row.getValor()+".");
                message.append("\n");
            }

        }
    }

    public void mensajeMail2(){
        message1 = new StringBuilder();
        for(Cobros_Doc row: items1){
            if(row.getCod_mov().equals("NG")){
                message1.append("Se generó Nota de Crédito Interna # "+row.getNum_mov()+" con codigo de movimiento "+row.getCod_mov()+" con valor de "+row.getValor()+".");
                message1.append("\n");
            }else{
                message1.append("Pago realizado a Factura # "+row.getNum_mov()+" con codigo de movimiento "+row.getCod_mov()+" con valor de "+row.getValor()+".");
                message1.append("\n");
            }
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
                    Intent intent = new Intent(getApplicationContext(), RecibosActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("lista", l);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    Toast toast = Toast.makeText(getBaseContext(), "Email NO pudo ser enviado, intentelo nuevamente..!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                    toast.show();
                    Intent intent = new Intent(getApplicationContext(), RecibosActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("lista", l);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }

        }
    }

    public String bco_ch(String banco){
        SQLiteDatabase db = null;
        Cursor q = null;
        String dato = "";
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT nom_banco FROM BANCOS_CH WHERE cod_bco='"+banco+"'";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
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

    public Clientes clientes(String codigo){
        SQLiteDatabase db = null;
        Cursor q = null;
        Clientes v = new Clientes();
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM CLIENTES WHERE cliente='"+codigo+"'";
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

    public void updateReciboCorreo(int rec, String cob){
        SQLiteDatabase db = null;
        try {
            String SQL ="UPDATE COBROS_DOC SET estado = 2 WHERE id_recibo="+rec+" " +
                        "AND tipo_cobro='"+cob+"' AND cod_emp='"+l.getCod_emp()+"' AND cod_vend='"+l.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
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
                Intent intent = new Intent(getApplicationContext(), RecibosActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("lista", l);
                intent.putExtras(bundle);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}