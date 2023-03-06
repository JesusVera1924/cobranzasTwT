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

import java.util.ArrayList;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.model.Secuenciales;
import com.twt.appcobranzas.model.Vendedor;
import com.twt.appcobranzas.utils.DbHelper;

/**
 * Created by DEVECUA on 11/12/2015.
 */
public class VendedorActivity extends AppCompatActivity {

    String TAG="VendedorActivity";
    Context context;
    Button btnGuardar, btnVerificar;
    String res;
    boolean result = true;
    EditText cod, nomb, correo, urlc, pin, adm;
    DbHelper dbSql =null;
    final static String pass = "1234";
    Vendedor vendedor;
    public ArrayList<Secuenciales> arr = new ArrayList<Secuenciales>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingreso_vendedor);
        context=this;
        dbSql = new DbHelper(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        //Bundle bundle = getIntent().getExtras();
        //vendedor = bundle.getParcelable("item");

        //widget
        btnGuardar = (Button) this.findViewById(R.id.btGuardar);
        btnVerificar = (Button) this.findViewById(R.id.btVerificar);
        cod        = (EditText) this.findViewById(R.id.etCod);
        nomb       = (EditText) this.findViewById(R.id.etNombre);
        correo     = (EditText) this.findViewById(R.id.etCorreo);
        urlc       = (EditText) this.findViewById(R.id.etUrl);
        pin        = (EditText) this.findViewById(R.id.etPin);
        adm        = (EditText) this.findViewById(R.id.etAdm);

        cod.setText("");
        nomb.setText("");
        correo.setText("");
        pin.setText("");
        urlc.setText("http://www.cojapan.com.ec:8088/wscojapan/rest/");

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String pin_admin = adm.getText().toString().trim();
                    String codigo = cod.getText().toString().trim();
                    String nombre = nomb.getText().toString().trim();
                    String mail = correo.getText().toString().trim();
                    String pin_user = pin.getText().toString().trim();
                    String url = urlc.getText().toString().trim();
                    if(codigo.equals("")||nombre.equals("")||mail.equals("")||pin_user.equals("")||url.equals("")){
                        CharSequence text = "FALTAN DATOS POR INGRESAR ....!!";
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
                    if(pin_admin.equals("")){
                        CharSequence text = "INGRESE UN PIN DE ADMINISTRADOR ....!!";
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
                    if (!verificar(pin_admin, result)) {
                        CharSequence text = "PIN DE ADMINISTRADOR INCORRECTO ....!!";
                        int duration = Toast.LENGTH_LONG;

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
                        adm.setText("");
                        return;
                    }
                    Intent intent = new Intent(VendedorActivity.this, PinActivity.class);
                    startActivity(intent);
                    vendedor = users(codigo.toUpperCase());
                    if(vendedor.getCod_vend()==null){
                        new TaskSaveData().execute();
                        loadItems();
                        insertSecuencia(arr);
                        adm.setText("");
                    }else{
                        deleteVendedor(codigo.toUpperCase());
                        deleteSecuencia(codigo.toUpperCase());
                        loadItems();
                        insertSecuencia(arr);
                        new TaskSaveData().execute();
                        adm.setText("");
                    }
                    //deleteVendedor();
                    //new TaskSaveData().execute();
                    //adm.setText("");
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        btnVerificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigo = cod.getText().toString().trim();
                vendedor = users(codigo.toUpperCase());
                if(vendedor.getCod_vend()==null){
                    nomb.setText("");
                    correo.setText("");
                    pin.setText("");
                    urlc.setText("http://www.cojapan.com.ec:8088/wscojapan/rest/");
                }else{
                    nomb.setText(vendedor.getNombre_vend());
                    correo.setText(vendedor.getCorreo_vend());
                    urlc.setText(vendedor.getUrl());
                    pin.setText("");
                }
            }
        });

        onBackPressed();

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

    public boolean verificar(String a, boolean r){
        res = a;
        System.out.println(res);
        if(res.equals(pass) && r == true){
            return true;
        }
        else{
            return false;
        }
    }

    void loadItems(){
        String codigo = cod.getText().toString().trim();
        System.out.println("codven "+codigo);

        Secuenciales obj = new Secuenciales();
        obj.setCod_emp("02");
        obj.setTipo("REC");
        obj.setSecuencial(1001);
        obj.setCod_ven(codigo.toUpperCase().trim());
        arr.add(obj);

        obj = new Secuenciales();
        obj.setCod_emp("02");
        obj.setTipo("RET");
        obj.setSecuencial(1001);
        obj.setCod_ven(codigo.toUpperCase().trim());
        arr.add(obj);

        obj = new Secuenciales();
        obj.setCod_emp("02");
        obj.setTipo("DOC");
        obj.setSecuencial(1001);
        obj.setCod_ven(codigo.toUpperCase().trim());
        arr.add(obj);

        obj = new Secuenciales();
        obj.setCod_emp("02");
        obj.setTipo("NSC");
        obj.setSecuencial(1001);
        obj.setCod_ven(codigo.toUpperCase().trim());
        arr.add(obj);

        obj = new Secuenciales();
        obj.setCod_emp("02");
        obj.setTipo("RNC");
        obj.setSecuencial(1001);
        obj.setCod_ven(codigo.toUpperCase().trim());
        arr.add(obj);

        obj = new Secuenciales();
        obj.setCod_emp("02");
        obj.setTipo("DESC");
        obj.setSecuencial(1);
        obj.setCod_ven(codigo.toUpperCase().trim());
        arr.add(obj);
    }

    public void insertSecuencia(ArrayList<Secuenciales> lista){
        ArrayList<Secuenciales> result = lista;
        try {
            SQLiteDatabase db = null;
            for (Secuenciales row : result) {
                String SQL = "INSERT INTO SECUENCIA (cod_emp,tipo,secuencia,cod_ven) VALUES " +
                             "('"+row.getCod_emp()+"','" + row.getTipo() + "'," + row.getSecuencial() + ",'" + row.getCod_ven() + "')";
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }


    public class TaskSaveData extends AsyncTask<String, Void,  String > {
        ProgressDialog pDialog;
        String c = cod.getText().toString().toUpperCase();
        String n = nomb.getText().toString().toUpperCase();
        String co = correo.getText().toString().toUpperCase();
        String u = urlc.getText().toString().toLowerCase();
        String p = pin.getText().toString();

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Guardando datos..");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String TAG="TaskSaveData";
            try {
                String SQL = "INSERT INTO VENDEDOR (cod_emp,cod_vend,nombre_vend,correo_vend,pin,url,estado) " +
                        "VALUES ('02','" + c.toUpperCase().trim() + "','" + n.toUpperCase().trim() + "','" +
                        co.toLowerCase().trim() + "','" + p + "','" + u + "',0)";
                SQLiteDatabase db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
                db.close();

            } catch (Exception e) {
                Log.e(TAG, " Exception Error . " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String a) {
            if(pDialog != null){
                pDialog.dismiss();
                CharSequence text = "DATOS GUARDADOS ....!!";
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
            }
        }

    }

    public void deleteVendedor(String codigo){
        try {
            SQLiteDatabase db = null;
            String SQL = "DELETE FROM VENDEDOR WHERE cod_emp='02' AND cod_vend='"+codigo.toUpperCase().trim()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public void deleteSecuencia(String codigo){
        try {
            SQLiteDatabase db = null;
            String SQL = "DELETE FROM SECUENCIA WHERE cod_emp='02' AND cod_ven='"+codigo.toUpperCase().trim()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public Vendedor users(String codigo){
        SQLiteDatabase db = null;
        Cursor q = null;
        Vendedor v = new Vendedor();
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM VENDEDOR WHERE cod_emp='02' AND cod_vend='"+codigo.toUpperCase().trim()+"'";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    v.setId(q.getInt(0));
                    v.setCod_emp(q.getString(1));
                    v.setCod_vend(q.getString(2));
                    v.setNombre_vend(q.getString(3));
                    v.setCorreo_vend(q.getString(4));
                    v.setPin(q.getString(5));
                    v.setUrl(q.getString(6));
                    v.setSeleccion(q.getString(7));
                    v.setVend1(q.getString(8));
                    v.setVend2(q.getString(9));
                    v.setVend3(q.getString(10));
                    v.setVend4(q.getString(11));
                    v.setVend5(q.getString(12));
                    v.setEstado(q.getInt(13));
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
                Intent intent = new Intent(VendedorActivity.this, PinActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
