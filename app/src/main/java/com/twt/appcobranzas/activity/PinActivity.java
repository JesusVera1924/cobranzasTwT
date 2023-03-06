package com.twt.appcobranzas.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.twt.appcobranzas.BuildConfig;
import com.twt.appcobranzas.R;
import com.twt.appcobranzas.model.Mails;
import com.twt.appcobranzas.model.Vendedor;
import com.twt.appcobranzas.utils.DbHelper;

/**
 * Created by DEVECUA on 15/12/2015.
 */
public class PinActivity extends Activity {

    String TAG="PinActivity";
    Context context;
    Button btnEntrar, btnConf;
    EditText n1, cod;
    List<Vendedor> items, venta;
    Vendedor lista, vendedores;
    public ArrayList<Mails> mail = new ArrayList<Mails>();
    TextView version;
    String vers;
    int vend;
    DbHelper dbSql =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingreso_pin);
        context=this;
        dbSql = new DbHelper(context);

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }

        //widget
        btnEntrar = (Button) this.findViewById(R.id.btEntrar);
        btnConf   = (Button) this.findViewById(R.id.btConf);
        n1        = (EditText) this.findViewById(R.id.etN1);
        cod       = (EditText) this.findViewById(R.id.etC);
        version   = (TextView) this.findViewById(R.id.txt_version);
        loadMails();
        getCorreo(mail);

        vend = getCountVendedor();

        vers = getVersionName();
        version.setText("Version. "+vers);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck== PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (vend==0) {
                        CharSequence text = "NO HAY DATOS DE VENDEDOR, POR FAVOR CREAR UN PERFIL PARA ACCEDER A LA APLICACION USANDO EL BOTON DE CONFIGURACION ....!!";
                        int duration = Toast.LENGTH_LONG ;

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
                        n1.setText("");
                        return;
                    }
                    String pin = n1.getText().toString().trim();
                    String codigo = cod.getText().toString().trim().toUpperCase();
                    if(codigo.equals("")){
                        CharSequence text = "INGRESE UN CODIGO DE VENDEDOR ....!!";
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
                    if(pin.equals("")){
                        CharSequence text = "INGRESE UN PIN DE 4 DIGITOS ....!!";
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
                    venta = load_users(codigo);
                    for (Vendedor ro: venta){
                        lista = new Vendedor();
                        lista.setId(ro.getId());
                        lista.setCod_emp(ro.getCod_emp());
                        lista.setCod_vend(ro.getCod_vend());
                        lista.setNombre_vend(ro.getNombre_vend());
                        lista.setCorreo_vend(ro.getCorreo_vend());
                        lista.setPin(ro.getPin());
                        lista.setUrl(ro.getUrl());
                        lista.setSeleccion(ro.getSeleccion());
                        lista.setVend1(ro.getVend1());
                        lista.setVend2(ro.getVend2());
                        lista.setVend3(ro.getVend3());
                        lista.setVend4(ro.getVend4());
                        lista.setVend5(ro.getVend5());
                        lista.setEstado(ro.getEstado());
                    }
                    if (!verificar_users(codigo, pin)) {
                        CharSequence text = "CODIGO VENDEDOR ó PIN INCORRECTO ....!!";
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
                        n1.setText("");
                        return;
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("item", lista);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    n1.setText("");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        btnConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PinActivity.this, VendedorActivity.class);
                startActivity(intent);
            }
        });

    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS, Manifest.permission.RECEIVE_SMS,
                                                                    Manifest.permission.READ_SMS, Manifest.permission.READ_EXTERNAL_STORAGE,
                                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                                                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                                                                    101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    void loadMails(){
        Mails obj = new Mails();
        obj.setCorreo("jimmy_ubilla@cojapan.com.ec");
        mail.add(obj);
    }

    public List<Vendedor> load_users(String codigo){
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM VENDEDOR WHERE cod_vend='"+codigo+"'";
            items = new ArrayList();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    int    id   =  q.getInt(0);
                    String emp  =  q.getString(1);
                    String cod  =  q.getString(2);
                    String nom  =  q.getString(3);
                    String mail =  q.getString(4);
                    String pin  =  q.getString(5);
                    String url  =  q.getString(6);
                    String sel  =  q.getString(7);
                    String ven1 = q.getString(8);
                    String ven2 = q.getString(9);
                    String ven3 = q.getString(10);
                    String ven4 = q.getString(11);
                    String ven5 = q.getString(12);
                    int est  = q.getInt(13);
                    items.add(new Vendedor(id,emp,cod,nom,mail,pin,url,sel,ven1,ven2,ven3,ven4,ven5,est));
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return items;
    }

    public Vendedor users(String codigo){
        SQLiteDatabase db = null;
        Cursor q = null;
        Vendedor v = new Vendedor();
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM VENDEDOR WHERE cod_vend='"+codigo+"'";
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

    public void getCorreo(ArrayList<Mails> lista){
        ArrayList<Mails> result = lista;
        SQLiteDatabase db = null;
        try {

            for (Mails row : result) {
                String SQL = "INSERT INTO MAILS (correo) VALUES ("+"'" + row.getCorreo() + "'"+")";
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }finally {
            db.close();
        }
    }

    public int getCountVendedor(){
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT COUNT(*) FROM VENDEDOR";
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

    public String getVersionName(){
        return BuildConfig.VERSION_NAME;
    }

    public boolean verificar_users(String cod, String pin){
        try {
            vendedores = users(cod);
            //a simple plan text password verification
            String us = vendedores.getCod_vend();
            String cl = vendedores.getPin();
            if(us.equals(cod.trim()) && cl.equals(pin.trim())){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
