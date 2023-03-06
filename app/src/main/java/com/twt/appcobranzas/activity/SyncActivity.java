package com.twt.appcobranzas.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.model.Banco;
import com.twt.appcobranzas.model.BancoDep;
import com.twt.appcobranzas.model.ClientesRel;
import com.twt.appcobranzas.model.CobrosRet;
import com.twt.appcobranzas.model.Cobros_Doc;
import com.twt.appcobranzas.model.Cobros_Pagos;
import com.twt.appcobranzas.model.Geolocalizacion;
import com.twt.appcobranzas.model.Retenciones;
import com.twt.appcobranzas.model.RowCliente;
import com.twt.appcobranzas.model.RowFactura;
import com.twt.appcobranzas.model.RowFacturaNC;
import com.twt.appcobranzas.model.Secuenciales;
import com.twt.appcobranzas.model.Vendedor;
import com.twt.appcobranzas.model.VendedorWS;
import com.twt.appcobranzas.utils.DbHelper;
import com.twt.appcobranzas.utils.Utils;

/**
 * Created by DEVECUA on 11/12/2015.
 */
@SuppressWarnings("deprecation")
public class SyncActivity extends AppCompatActivity  {

    String TAG="SyncActivity";
    Context context;
    public List<RowCliente> items;
    public List<RowFactura> items1;
    public List<RowFacturaNC> items8, itemsR;
    public ArrayList<BancoDep> arr = new ArrayList<BancoDep>();
    List<Banco> items2;
    List<BancoDep> items11;
    List<Cobros_Doc> items3, items9, cob, cob1;
    List<Cobros_Pagos> items4, pag;
    List<Retenciones> items5;
    List<CobrosRet> items6, ret;
    List<ClientesRel> items7;
    List<Secuenciales> items10, sec;
    List<VendedorWS> listaVendedor;
    Vendedor lista, vend;
    VendedorWS vendedor, nrecibos;
    String cd;
    Button btClientes, btCobros, btBancos, btRetenciones, btTemporal, btGeocali;
    DbHelper dbSql =null;
    int contador, contador1, res, num, c1, r1;
    boolean state;
    private HttpClient httpclient;

    LocationManager locationManager;
    double longitudeBest, latitudeBest;
    double longitudeGPS, latitudeGPS;
    double longitudeNetwork, latitudeNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sincronizar);
        context=this;
        dbSql = new DbHelper(context);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        lista = bundle.getParcelable("lista");
        Utils.URLWS = lista.getUrl().trim();
        System.out.println("mi url es "+lista.getUrl());
        //widget
        btClientes    = (Button) this.findViewById(R.id.btn_clientes);
        btCobros      = (Button) this.findViewById(R.id.btn_cobros);
        btTemporal    = (Button) this.findViewById(R.id.btn_cobrostmp);
        btGeocali    = (Button) this.findViewById(R.id.btn_geocal);
        btBancos      = (Button) this.findViewById(R.id.btn_banco);
        btRetenciones = (Button) this.findViewById(R.id.btn_data);

        state = false;
        new TaskGetVendedores().execute();
        new TaskGetSecuencias().execute();
        new TaskGetDescarga().execute();

        items8 = busquedaNotas();
        vend   = users(lista.getCod_vend());

        onBackPressed();

        btClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = true;
                int contador = getCountRecibos();
                int contador1 = getCountRetenciones();
                if (vendedor == null) {
                    CharSequence text = "NO EXISTE VENDEDOR EN BASE DE DATOS MARSELLA...!!";
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
                if (!Utils.isOnline(context)) {
                    CharSequence text = "NO HAY CONEXION DISPONIBLE PARA SINCRONIZAR DATOS...!!";
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
                if (vendedor.getStatus() != 1) {
                    CharSequence text = "SU USUARIO VENDEDOR ESTA DESHABILITADO...!!";
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
                if(res==0) {
                    if (contador > 0 || contador1 > 0) {
                        CharSequence text = "NO SE PERMITE SINCRONIZAR DATOS, TIENE RECIBOS/RETENCIONES PENDIENTES DE SINCRONIZAR...!!";
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
                    } else {
                        if(lista.getSeleccion().equals("V")){
                            deleteClientesV();
                            deleteFacturasV();
                            deleteClienteRel();
                        }else if(lista.getSeleccion().equals("T")){
                            deleteClientesT();
                            deleteFacturasT();
                            deleteClienteRel();
                        }else{
                            deleteClientes();
                            deleteFacturas();
                            deleteClienteRel();
                        }

                        new TaskGetClienteRel().execute();
                        new TaskGetCliente().execute();

                        updateSecRecibos();
                        updateSecRetenciones();
                        updateSecNcreditos();
                        updateSecNcinterna();

                        //toggleGPSUpdates();
                    }
                }else{
                    CharSequence text = "NO TIENE LOS PERMISOS NECESARIOS PARA VOLVER A SINCRONIZAR HOJA DE RUTA...!!";
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
        });

        btBancos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vendedor==null) {
                    CharSequence text = "NO EXISTE VENDEDOR EN BASE DE DATOS MARSELLA...!!";
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
                if (vendedor.getStatus()!=1) {
                    CharSequence text = "SU USUARIO VENDEDOR ESTA DESHABILITADO...!!";
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
                if (!Utils.isOnline(context)) {
                    CharSequence text = "NO HAY CONEXION DISPONIBLE PARA SINCRONIZAR DATOS...!!";
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
                }else {
                    deleteBanco();
                    deleteBancoDp();
                    deleteRetenciones();
                    new TaskGetBanco().execute();
                    new TaskGetCuenta().execute();
                    new TaskGetRetenciones().execute();
                }
            }
        });

        btCobros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contador = vendedor.getUlt_descarga()+1;
                contador1 = getCountPendientes();
                if (vendedor.getStatus()!=1) {
                    CharSequence text = "SU USUARIO VENDEDOR ESTA DESHABILITADO...!!";
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
                if (!Utils.isOnline(context)) {
                    CharSequence text = "NO HAY CONEXION DISPONIBLE PARA SINCRONIZAR DATOS...!!";
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
                if(res==0) {
                    if (contador1 > 0) {
                        AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                        alertDialog1.setTitle("Notas de Crédito Pendientes");
                        alertDialog1.setMessage("Esta seguro de sincronizar con notas de crédito pendientes de aplicar..?");
                        alertDialog1.setCancelable(false);
                        alertDialog1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                aceptar();
                                new HttpPostCobrosAsyncTask().execute(Utils.URLWS + Utils.URLWS_RECIBOS);
                                new HttpPostRetencionesAsyncTask().execute(Utils.URLWS + Utils.URLWS_RETENCIONES);
                                new HttpPostSecuencias().execute(Utils.URLWS + Utils.URLWS_UPDATESEC);

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("item", lista);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                //deleteNotas();
                                //setSecuenciaDesc();
                            }
                        });
                        alertDialog1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                cancelar();
                            }
                        });
                        alertDialog1.show();
                    } else {
                        new HttpPostCobrosAsyncTask().execute(Utils.URLWS + Utils.URLWS_RECIBOS);
                        new HttpPostRetencionesAsyncTask().execute(Utils.URLWS + Utils.URLWS_RETENCIONES);
                        new HttpPostSecuencias().execute(Utils.URLWS + Utils.URLWS_UPDATESEC);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("item", lista);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        //deleteNotas();
                        //setSecuenciaDesc();
                    }
                }else{
                    CharSequence text = "NO TIENE LOS PERMISOS NECESARIOS PARA VOLVER A ENVIAR INFORMACION...!!";
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
        });

        btGeocali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    /*ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);*/
                }


                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                getGeocalizacion(new Geolocalizacion(0, "02", "R003", "0", "0", new Date(), ""));
            }
        });

        btTemporal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contador = vendedor.getUlt_descarga()+1;
                contador1 = getCountPendientes();
                if (vendedor.getStatus()!=1) {
                    CharSequence text = "SU USUARIO VENDEDOR ESTA DESHABILITADO...!!";
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
                if (!Utils.isOnline(context)) {
                    CharSequence text = "NO HAY CONEXION DISPONIBLE PARA SINCRONIZAR DATOS...!!";
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
                if(res==0) {
                    if (contador1 > 0) {
                        AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                        alertDialog1.setTitle("Notas de Crédito Pendientes");
                        alertDialog1.setMessage("Esta seguro de sincronizar con notas de crédito pendientes de aplicar..?");
                        alertDialog1.setCancelable(false);
                        alertDialog1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                aceptar();
                                new HttpPostCobrosAsyncTask().execute(Utils.URLWS + Utils.URLWS_RECIBOS2);
                                new HttpPostRetencionesAsyncTask().execute(Utils.URLWS + Utils.URLWS_RETENCIONES2);
                                new HttpPostSecuencias().execute(Utils.URLWS + Utils.URLWS_UPDATESEC);

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("item", lista);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                //deleteNotas();
                                //setSecuenciaDesc();
                            }
                        });
                        alertDialog1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                cancelar();
                            }
                        });
                        alertDialog1.show();
                    } else {
                        new HttpPostCobrosAsyncTask().execute(Utils.URLWS + Utils.URLWS_RECIBOS2);
                        new HttpPostRetencionesAsyncTask().execute(Utils.URLWS + Utils.URLWS_RETENCIONES2);
                        new HttpPostSecuencias().execute(Utils.URLWS + Utils.URLWS_UPDATESEC);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("item", lista);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        //deleteNotas();
                        //setSecuenciaDesc();
                    }
                }else{
                    CharSequence text = "NO TIENE LOS PERMISOS NECESARIOS PARA VOLVER A ENVIAR INFORMACION...!!";
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
        });

        btRetenciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle("Elminar Datos");
                alertDialog1.setMessage("Esta seguro de eliminar todos los datos..?");
                alertDialog1.setCancelable(false);
                alertDialog1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        aceptar2();
                        List<Cobros_Doc> cob = load_cobrosdoc();
                        List<CobrosRet> ret = load_cobrosret();
                        updateDoc(cob);
                        updatePagos(cob);
                        updateRet(ret);

                        if(lista.getSeleccion().equals("V")){
                            deleteNotasV();
                            deleteFacturasV();
                            deleteClientesV();
                        }else if(lista.getSeleccion().equals("T")){
                            deleteNotasT();
                            deleteClientesT();
                            deleteFacturasT();
                        }else {
                            deleteNotas();
                            deleteClientes();
                            deleteFacturas();
                        }

                    }
                });
                alertDialog1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        cancelar();
                    }
                });
                alertDialog1.show();

            }
        });

        /*btRetenciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contador = getMaxDesc();
                if (vendedor.getStatus()!=1) {
                    CharSequence text = "SU USUARIO VENDEDOR ESTA DESHABILITADO...!!";
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
                if (!Utils.isOnline(context)) {
                    CharSequence text = "NO HAY CONEXION DISPONIBLE PARA SINCRONIZAR DATOS...!!";
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
                }else {
                    new HttpPostRetencionesAsyncTask().execute(Utils.URLWS+Utils.URLWS_RETENCIONES);
                }
            }
        });*/
    }

    public void cancelar() {
        finish();
        CharSequence text = "HA CANCELADO LA OPERACION ...!!";
        int duration = Toast.LENGTH_LONG;

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_ok,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView textToast = (TextView) layout.findViewById(R.id.text_toast);
        textToast.setText(text);

        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(layout);
        int offsetX = 20;
        int offsetY = 25;
        toast.setGravity(Gravity.CENTER_VERTICAL, offsetX, offsetY);
        toast.show();
        return;
    }

    public void aceptar() {
        CharSequence text = "HA DECIDIDO SINCRONIZAR CON NOTAS DE CREDITO PENDIENTES POR APLICAR ...!!";
        int duration = Toast.LENGTH_LONG;

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_ok,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView textToast = (TextView) layout.findViewById(R.id.text_toast);
        textToast.setText(text);

        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(layout);
        int offsetX = 20;
        int offsetY = 25;
        toast.setGravity(Gravity.CENTER_VERTICAL, offsetX, offsetY);
        toast.show();
    }

    public void aceptar2() {
        CharSequence text = "DATOS BORRADOS ...!!";
        int duration = Toast.LENGTH_LONG;

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_ok,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView textToast = (TextView) layout.findViewById(R.id.text_toast);
        textToast.setText(text);

        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(layout);
        int offsetX = 20;
        int offsetY = 25;
        toast.setGravity(Gravity.CENTER_VERTICAL, offsetX, offsetY);
        toast.show();
    }

    public List<RowFacturaNC> busquedaNotas() {
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM DOC_LOCAL " +
                        "WHERE sdo_mov<0 AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"' AND val_mov>0 " +
                        "ORDER BY num_mov";
                        //"GROUP BY cod_ref";
            itemsR = new ArrayList();
            q = db.rawQuery(SQL,null);
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
                    String fch_emi = q.getString(9);
                    Date emi = Utils.ConvertirShortStringToDate(fch_emi);
                    String fch_ven = q.getString(10);
                    Date ven = Utils.ConvertirShortStringToDate(fch_ven);
                    String num_mov = q.getString(11);
                    String num_rel = q.getString(12);
                    double sdo_mov = q.getDouble(13)*-1;
                    double val_mov = q.getDouble(14);
                    double iva = q.getDouble(15);
                    double base = q.getDouble(16);
                    String est = q.getString(17);
                    String cdb = q.getString(18);
                    String ncta = q.getString(19);
                    boolean sel = false;
                    double ab = 0;
                    itemsR.add(new RowFacturaNC(cod_emp,cod_mov,cod_pto,cod_ref,cod_vin,cod_ven,vendedor,dias,fch_emi,fch_ven,num_mov,num_rel,sdo_mov,val_mov,iva,base,est,cdb,ncta,sel,ab));
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return itemsR;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dbSql==null)
            dbSql = new DbHelper(context);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    public class TaskGetCliente extends AsyncTask<String, Void, String>{
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            String title = "Cargando Datos...";
            String msg = "Clientes y Documentos .. Espere por Favor";
            SpannableString ss1=  new SpannableString(title);
            ss1.setSpan(new RelativeSizeSpan(2f), 0, ss1.length(), 0);
            ss1.setSpan(new ForegroundColorSpan(Color.rgb(224,18,51)), 0, ss1.length(), 0);
            SpannableString ss2=  new SpannableString(msg);
            ss2.setSpan(new RelativeSizeSpan(2f), 0, ss2.length(), 0);
            ss2.setSpan(new ForegroundColorSpan(Color.rgb(0, 94, 255)), 0, ss2.length(), 0);

            pDialog = new ProgressDialog(context);
            pDialog.setTitle(ss1);
            pDialog.setMessage(ss2);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
            System.out.println("paso onPreExecute Clientes  ");
        }

        @Override
        protected String doInBackground(String... params) {
            String TAG="TaskGetCliente";
            int tamaño = 0;
            int tamaño1 = 0;
            String re = "", apfac = "";
            Calendar cal = Calendar.getInstance();
            cal.set(2020, 3, 31);
            StringBuilder cli, fac;
            String vendedor = lista.getCod_vend(); // luis
            String URL= Utils.URLWS+Utils.URLWS_CLIENTES+lista.getCod_emp()+"&codven="+lista.getCod_vend()+"&select="+vend.getSeleccion();
            HttpURLConnection connection = null;
            fac = new StringBuilder();
            try {
                Log.v(TAG, URL);
                URL url = new URL(URL);
                connection = (HttpURLConnection) url.openConnection();
                //connection.setRequestProperty ("Authorization", Utils.basicAuth);
                connection.setConnectTimeout(60*1000);
                connection.setReadTimeout(300*1000);
                int statusCode = connection.getResponseCode();
                if (statusCode == 200) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    String result= Utils.convertStreamToString(in);
                    try {
                        JSONArray json = new JSONArray(result);
                        items = new ArrayList<RowCliente>();
                        items1 = new ArrayList<RowFactura>();
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject jsonobject = json.getJSONObject(i);
                            String emp  = jsonobject.getString("empresa");
                            String codc = jsonobject.getString("cliente");
                            String nom1 = jsonobject.getString("ncliente");
                            String nom2 = jsonobject.getString("ncliente_aux");
                            String dirc = jsonobject.getString("direccion");
                            String ciud = jsonobject.getString("ciudad");
                            String prov = jsonobject.getString("provincia");
                            String ced  = jsonobject.getString("ruc");
                            String telf = jsonobject.getString("telefono");
                            String ven = jsonobject.getString("vendedor");
                            /*String vend = "";
                            if(ven==null)
                                vend = vendedor; //luis
                            else
                                vend = jsonobject.getString("vendedor");
                            */
                            double desc = jsonobject.getDouble("descuento");
                            String fpag = jsonobject.getString("forma_pago");
                            String mai1 = jsonobject.getString("mail1");
                            String mai2 = jsonobject.getString("mail2");
                            String pers = jsonobject.getString("persona");
                            cd = codc;
                            //Log.i(TAG,String.valueOf(i));
                            items.add(new RowCliente(emp,codc,nom1,nom2,dirc,ciud,prov,ced,telf,ven,desc,fpag,mai1,mai2,pers));
                        }
                        tamaño = items.size();
                        //facturaSincronizar();
                        String URL1= Utils.URLWS+Utils.URLWS_DOCUMENTOS+lista.getCod_emp()+"&codven="+lista.getCod_vend()+"&select="+vend.getSeleccion();
                        HttpURLConnection connection1 = null;
                        try {
                            Log.v(TAG, URL1);
                            URL url1 = new URL(URL1);
                            connection1 = (HttpURLConnection) url1.openConnection();
                            connection1.setConnectTimeout(60*1000);
                            connection1.setReadTimeout(30*1000);
                            int statusCode1 = connection1.getResponseCode();
                            if (statusCode1 == 200) {
                                InputStream in1 = new BufferedInputStream(connection1.getInputStream());
                                String result1 = Utils.convertStreamToString(in1);
                                try {
                                    JSONArray json1 = new JSONArray(result1);
                                    for (int i = 0; i < json1.length(); i++) {
                                        apfac = "";
                                        JSONObject jsonobject = json1.getJSONObject(i);
                                        String cod_emp = jsonobject.getString("cod_emp");
                                        String cod_mov = jsonobject.getString("cod_mov");
                                        String cod_pto = jsonobject.getString("cod_pto");
                                        String cod_ref = jsonobject.getString("cod_ref");
                                        String cod_ven = jsonobject.getString("cod_ven");
                                        int dias = jsonobject.getInt("dias_pendiente");
                                        String emi = jsonobject.getString("fec_emi");
                                        //Date femi = Utils.ConvertirShortStringToDate(emi);
                                        String ven = jsonobject.getString("fec_ven");
                                        //Date fven = Utils.ConvertirShortStringToDate(fch_ven);
                                        String num_mov = jsonobject.getString("num_mov");
                                        String num_rel = jsonobject.getString("num_rel");
                                        double sdo_mov = jsonobject.getDouble("sdo_mov");
                                        double val_mov = jsonobject.getDouble("val_mov");
                                        double iva = jsonobject.getDouble("iva");
                                        double base = jsonobject.getDouble("base");
                                        String est = jsonobject.getString("sts_mov");
                                        String cdb = jsonobject.getString("cod_bco");
                                        String ncta = jsonobject.getString("num_cta");
                                        boolean sel = false;
                                        boolean sel2 = false;
                                        double ab = sdo_mov;
                                        //Log.i(TAG,String.valueOf(i));
                                        apfac = String.valueOf(i);

                                        items1.add(new RowFactura(cod_emp, cod_mov, cod_pto, cod_ref, cod_ven, dias, emi, ven, num_mov,
                                                                      num_rel, sdo_mov, val_mov, iva, base, est, cdb, ncta, sel, sel2, ab));
                                        }
                                    tamaño1 = items1.size();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.i(TAG, " ERROR JSON Facturas");
                                    fac.append("Error cargando dato factura " + apfac);
                                }
                            } else {
                                Log.e(TAG," ERROR Conexion getFactura aqui");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG," Exception Error Facturas... "+e.getMessage() );
                        } finally {
                            connection.disconnect();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG," ERROR JSON Cliente");
                    }
                } else {
                    Log.e(TAG,"ERROR Conexion getCliente");
                }
            }   catch (Exception e) {
                    e.printStackTrace();
                Log.e(TAG," Exception Error Cliente... "+e.getMessage() );
            } finally {
                connection.disconnect();
            }
            if(fac.length()>0){
                re = "ERROR Facturas";
            }else{
                if(tamaño>0 && tamaño1>0){
                    re = "";
                }else if(tamaño>0 && tamaño1<0){
                    re = "OK Clientes - ERROR Facturas";
                }else if(tamaño<0 && tamaño1>0){
                    re = "ERROR Clientes - OK Facturas";
                }
            }
            return re;
        }

        @Override
        protected void onPostExecute(String resp) {
            if(pDialog != null) {
                Log.v(TAG, resp);
                if (resp.length()<=0) {
                    getCliente(items);
                    getFactura(items1);
                    pDialog.dismiss();

                    CharSequence text = "SINCRONIZACION EXITOSA ....!!";
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

                }else{
                    pDialog.dismiss();
                    CharSequence text = "ERROR NO SE PUDO SINCRONIZAR ....!! "+resp;
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
                    Log.e(TAG, text.toString());
                }
            }

        }

    }

    public void getCliente(List<RowCliente> lista){
        List<RowCliente> result = lista;
        try {
            SQLiteDatabase db = null;
            for (RowCliente row : result) {
                String SQL = "INSERT INTO CLIENTES (cod_emp,cliente,n_cliente,n_clienteaux,direccion,ciudad,provincia,ruc,telefono,vendedor,descuento,forma_pago,mail1,mail2,persona) " +
                             "VALUES ('"+ row.getEmpresa()+"','" + row.getCliente() + "','" + row.getN_cliente() + "','" + row.getN_clienteaux()+ "','" + row.getDireccion() + "','" + row.getCiudad() + "','" +
                                      row.getProvincia() + "','" + row.getRuc()+ "','" + row.getTelefono() + "','" + row.getVendedor() + "','" + row.getDescuento()+ "','" +
                                      row.getForma_pago() + "','" + row.getMail1()+ "','" + row.getMail2() + "','" +row.getPersona() + "')";
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);

            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }

    }

    public void getFactura(List<RowFactura> lista){
        List<RowFactura> result = lista;
        try {
            SQLiteDatabase db = null;
            for (RowFactura row : result) {
                String SQL = "INSERT INTO DOC_WS (cod_emp,cod_mov,cod_pto,cod_ref,cod_ven,dias_pend,fec_emi,fec_ven,num_mov,num_rel,sdo_mov,val_mov,iva,base,sts_mov,cod_bco,num_cuenta) VALUES ("+"'" +
                             row.getCod_emp() + "','" + row.getCod_mov() + "','" + row.getCod_pto() + "','" + row.getCod_ref() + "','" + row.getCod_ven() + "','" +
                             row.getDias_pendiente()+ "','" + row.getFec_emis() + "','" + row.getFec_venc() + "','" + row.getNum_mov() + "','" + row.getNum_rel() + "','" +
                             row.getSdo_mov() + "','" + row.getVal_mov() + "','" + row.getIva() + "','" + row.getBase() + "','" + row.getSts_mov() + "','" + row.getCod_bco() + "','" + row.getNum_cuenta() + "')";
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);

            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }
    public void getGeocalizacion(Geolocalizacion api){
               try {
            SQLiteDatabase db = null;
             {
                 Date d = new Date();
                 // Crear un objeto SimpleDateFormat con el patrón de formato deseado
                 SimpleDateFormat formatoFechaHora = new SimpleDateFormat("dd/MM/yyyyTHH:mm:ss");
                 // Formatear la fecha y hora en una cadena de caracteres usando el objeto SimpleDateFormat
                 String fechaHoraFormateada = formatoFechaHora.format(d);

                 String SQL = "INSERT INTO geolocalizacion(id, cod_emp, cod_ven, latutid, longitud, fch_reg, tip_geo) " +
                         "VALUES ( '" + fechaHoraFormateada    +"'','" +
                        api.getCod_emp() + "','" + api.getCod_ven() + "','"  + api.getLatitud() + "','" + api.getLongitud() + "'," +
                        "'" + api.getFch_reg()+ "','" + api.getTip_geo()+ "')";
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);

            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public class TaskGetBanco extends AsyncTask<String, Void, Integer>{
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Cargando datos de Bancos..");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
            System.out.println("pasooo2 onPreExecute Banco ");
        }
        @Override
        protected Integer doInBackground(String... params) {
            String TAG="TaskGetBanco";
            int tamaño2 = 0;
            String URL= Utils.URLWS+Utils.URLWS_BANCOS+lista.getCod_emp();
            System.out.println(URL);
            HttpURLConnection connection = null;
            try {
                //Log.v(TAG, URL);
                URL url = new URL(URL);
                connection = (HttpURLConnection) url.openConnection();
                //connection.setRequestProperty ("Authorization", Utils.basicAuth);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                int statusCode = connection.getResponseCode();
                if (statusCode == 200) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    String result= Utils.convertStreamToString(in);
                    try {
                        JSONArray json = new JSONArray(result);
                        items2 = new ArrayList<Banco>();
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject jsonobject = json.getJSONObject(i);
                            String emp = jsonobject.getString("empresa");
                            String cod = jsonobject.getString("cod_ref");
                            String nomb = jsonobject.getString("nom_banco");
                            Log.i(TAG,String.valueOf(i));
                            items2.add(new Banco(emp, cod, nomb));
                        }
                        tamaño2 = items2.size();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG," ERROR JSON ");
                    }
                } else {
                    Log.e(TAG," ERROR getBanco");
                }
            }   catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG," Exception Error . "+e.getMessage() );
            } finally {
                connection.disconnect();
            }
            return tamaño2;
        }

        @Override
        protected void onPostExecute(Integer a) {
            if(pDialog != null){
                pDialog.dismiss();
                if (a > 0) {
                    CharSequence text = "SINCRONIZACION EXITOSA BANCOS....!!";
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
                    getBanco(items2);
                }else{
                    CharSequence text = "ERROR NO SE PUDO SINCRONIZAR ....!!";
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
                }
            }

        }

    }

    public class TaskGetCuenta extends AsyncTask<String, Void, Integer>{
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Cargando datos de Bancos Deposito..");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
            System.out.println("pasooo2 onPreExecute Banco Deposito");
        }
        @Override
        protected Integer doInBackground(String... params) {
            String TAG="TaskGetBanco";
            int tamaño2 = 0;
            String URL= Utils.URLWS+Utils.URLWS_CUENTAS+lista.getCod_emp();
            System.out.println(URL);
            HttpURLConnection connection = null;
            try {
                //Log.v(TAG, URL);
                URL url = new URL(URL);
                connection = (HttpURLConnection) url.openConnection();
                //connection.setRequestProperty ("Authorization", Utils.basicAuth);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                int statusCode = connection.getResponseCode();
                if (statusCode == 200) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    String result= Utils.convertStreamToString(in);
                    try {
                        JSONArray json = new JSONArray(result);
                        arr = new ArrayList<BancoDep>();
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject jsonobject = json.getJSONObject(i);
                            String emp = jsonobject.getString("empresa");
                            String cod = jsonobject.getString("cod_banco");
                            String cue = jsonobject.getString("num_cuenta");
                            String nomb = jsonobject.getString("nom_banco");
                            Log.i(TAG,String.valueOf(i));
                            arr.add(new BancoDep(emp, cod, cue, nomb));
                        }
                        tamaño2 = arr.size();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG," ERROR JSON ");
                    }
                } else {
                    Log.e(TAG," ERROR getBancoDep");
                }
            }   catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG," Exception Error . "+e.getMessage() );
            } finally {
                connection.disconnect();
            }
            return tamaño2;
        }

        @Override
        protected void onPostExecute(Integer a) {
            if(pDialog != null){
                pDialog.dismiss();
                if (a > 0) {
                    CharSequence text = "SINCRONIZACION EXITOSA CUENTAS DEPOSITO....!!";
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
                    getBancoDp(arr);
                }else{
                    CharSequence text = "ERROR NO SE PUDO SINCRONIZAR ....!!";
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
                }
            }

        }

    }

    public void getBanco(List<Banco> lista){
        List<Banco> result = lista;
        try {
            SQLiteDatabase db = null;
            for (Banco row : result) {
                String SQL = "INSERT INTO BANCOS_CH (cod_emp,cod_bco,nom_banco) VALUES ('"
                           + row.getEmpresa() +"','" + row.getCod_ref() + "','" + row.getNom_banco() +"')";
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);

            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public void getBancoDp(ArrayList<BancoDep> lista){
        ArrayList<BancoDep> result = lista;
        try {
            SQLiteDatabase db = null;
            for (BancoDep row : result) {
                String SQL = "INSERT INTO BANCOS_DP (cod_emp,cod_bco,nom_banco,num_cuenta) VALUES ('"+ row.getEmpresa()
                           +"','" + row.getCod_banco() + "','" + row.getNom_banco() + "','" + row.getNum_cuenta() +"')";
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);

            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public void deleteBanco(){
        try {
            SQLiteDatabase db = null;
            String SQL = "DELETE FROM BANCOS_CH WHERE cod_emp='"+lista.getCod_emp()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public void deleteBancoDp(){
        try {
            SQLiteDatabase db = null;
            String SQL = "DELETE FROM BANCOS_DP WHERE cod_emp='"+lista.getCod_emp()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public void deleteClientes(){
        try {
            SQLiteDatabase db = null;
            String SQL = "DELETE FROM CLIENTES WHERE cod_emp='"+lista.getCod_emp()+"' AND vendedor='"+lista.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public void deleteClientesV(){
        try {
            SQLiteDatabase db = null;
            String SQL = "DELETE FROM CLIENTES WHERE cod_emp='"+lista.getCod_emp()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public void deleteClientesT(){
        try {
            SQLiteDatabase db = null;
            String SQL = "DELETE FROM CLIENTES";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public void deleteFacturas() {
        try {
            SQLiteDatabase db = null;
            String SQL = "DELETE FROM DOC_WS WHERE cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public void deleteFacturasV() {
        try {
            SQLiteDatabase db = null;
            String SQL = "DELETE FROM DOC_WS WHERE cod_emp='"+lista.getCod_emp()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public void deleteFacturasT() {
        try {
            SQLiteDatabase db = null;
            String SQL = "DELETE FROM DOC_WS";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public String POSTCobros(String url){
        System.out.println(url);
        InputStream inputStream = null;
        String result = "";
        int n1 = 0;
        int n2 = 0;
        int n3 = 0;

        try {
            cob  = load_cobrosdoc();
            cob1 = load_cobpend();
            pag  = load_cobrospagos();

            if(cob.size()==0)
                n1 = 0;
            else
                n1 = cob.size();

            if(cob1.size()==0)
                n2 = 0;
            else
                n3 = cob1.size();

            if(pag.size()==0)
                n3 = 0;
            else
                n3 = pag.size();

            c1 = n1+n2+n3;

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONArray jsonArray = new JSONArray();
            String temp = ".";
            int temp1 = 0;
            String cobro = "DOC";
            String pago = "PAG";
            String auxiliar = "";

            //add data;
            for (Cobros_Doc row1 : cob) {
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("empresa", row1.getCod_emp());
                jsonParam.put("id_recibo", row1.getId_recibo());
                jsonParam.put("cod_mov", row1.getCod_mov());
                jsonParam.put("num_mov", row1.getNum_mov());
                jsonParam.put("tipo_cobro", row1.getTipo_cobro());
                jsonParam.put("cod_bco", row1.getCod_bco());
                if(row1.getNum_cuenta().equals(null)) {
                    auxiliar = "-";
                }else {
                    auxiliar = row1.getNum_cuenta();
                }
                jsonParam.put("cuenta", auxiliar);
                jsonParam.put("fpago", temp);
                jsonParam.put("banco", temp);
                jsonParam.put("cheque", temp);
                jsonParam.put("fch_pago", row1.getFch_emision());
                jsonParam.put("valor", row1.getValor());
                jsonParam.put("cod_ref", row1.getCod_cli());
                jsonParam.put("cod_vend", row1.getCod_vend());
                jsonParam.put("vendedor2", row1.getVendedor2());
                jsonParam.put("fch_registro", temp);
                jsonParam.put("girador", temp);
                jsonParam.put("vinculado", row1.getCod_vin());
                jsonParam.put("pago_cheque",temp);
                jsonParam.put("estado", row1.getEstado());
                jsonParam.put("pto_vta", row1.getPto_vta());
                jsonParam.put("tipo", cobro);
                jsonParam.put("num_doc", row1.getNum_doc());
                jsonParam.put("descarga", contador);

                System.out.println(jsonParam.toString());
                jsonArray.put(jsonParam);
            }
            for (Cobros_Doc row3 : cob1) {
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("empresa", row3.getCod_emp());
                jsonParam.put("id_recibo", row3.getId_recibo());
                jsonParam.put("cod_mov", row3.getCod_mov());
                jsonParam.put("num_mov", row3.getNum_mov());
                jsonParam.put("tipo_cobro", "NG");
                jsonParam.put("cod_bco", row3.getCod_bco());
                if(row3.getNum_cuenta().equals(null)) {
                    auxiliar = "-";
                }else {
                    auxiliar = row3.getNum_cuenta();
                }
                jsonParam.put("cuenta", auxiliar);
                jsonParam.put("fpago", temp);
                jsonParam.put("banco", temp);
                jsonParam.put("cheque", temp);
                jsonParam.put("fch_pago", row3.getFch_emision());
                jsonParam.put("valor", row3.getValor());
                jsonParam.put("cod_ref", row3.getCod_cli());
                jsonParam.put("cod_vend", row3.getCod_vend());
                jsonParam.put("vendedor2", row3.getVendedor2());
                jsonParam.put("fch_registro", temp);
                jsonParam.put("girador", temp);
                jsonParam.put("vinculado", row3.getNom_cli().trim());
                jsonParam.put("pago_cheque",temp);
                jsonParam.put("estado", row3.getEstado());
                jsonParam.put("pto_vta", row3.getPto_vta());
                jsonParam.put("tipo", cobro);
                jsonParam.put("num_doc", row3.getNum_doc());
                jsonParam.put("descarga", contador);

                System.out.println(jsonParam.toString());
                jsonArray.put(jsonParam);
            }
            for (Cobros_Pagos row2 : pag) {
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("empresa", row2.getCod_emp());
                jsonParam.put("id_recibo", row2.getId_recibo());
                jsonParam.put("cod_mov", temp);
                jsonParam.put("num_mov", temp);
                jsonParam.put("tipo_cobro", row2.getCod_rec());
                jsonParam.put("cod_bco", temp);
                jsonParam.put("fpago", row2.getFpago());
                jsonParam.put("banco", row2.getBanco());
                jsonParam.put("cuenta", row2.getCuenta());
                jsonParam.put("cheque", row2.getCheque());
                jsonParam.put("fch_pago", row2.getFch_pago());
                jsonParam.put("valor", row2.getValor());
                jsonParam.put("cod_ref", row2.getCod_ref());
                jsonParam.put("cod_vend", row2.getCod_vend());
                jsonParam.put("vendedor", row2.getVendedor2());
                jsonParam.put("fch_registro", row2.getFch_registro().toString());
                jsonParam.put("girador", row2.getGirador());
                jsonParam.put("vinculado", row2.getCod_vin());
                jsonParam.put("pago_cheque",row2.getStatus());
                jsonParam.put("estado", temp1);
                jsonParam.put("pto_vta", temp);
                jsonParam.put("tipo", pago);
                jsonParam.put("num_doc", temp);
                jsonParam.put("descarga", contador);

                System.out.println(jsonParam.toString());
                jsonArray.put(jsonParam);
            }

            // 4. convert JSONObject to JSON to String
            json = jsonArray.toString();
            System.out.println(json);
            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json,"UTF-8");

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json;charset=UTF-8");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
            //httpResponse.setStatusCode(RESULT_OK);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
                System.out.println("respuesta " + result);
                if(result.equals("OK Recibos")){
                    //updateDoc(cob);
                    //updatePagos(cob);
                    //setSecuenciaDesc();
                }
            } else
                result = "Error no sincronizado..!";
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private class HttpPostCobrosAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            String title = "Enviando Datos...";
            String msg = "Recibos de Cobros .. Espere por Favor";
            SpannableString ss1=  new SpannableString(title);
            ss1.setSpan(new RelativeSizeSpan(2f), 0, ss1.length(), 0);
            ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, ss1.length(), 0);
            SpannableString ss2=  new SpannableString(msg);
            ss2.setSpan(new RelativeSizeSpan(2f), 0, ss2.length(), 0);
            ss2.setSpan(new ForegroundColorSpan(Color.GREEN), 0, ss2.length(), 0);

            pDialog = new ProgressDialog(context);
            pDialog.setTitle(ss1);
            pDialog.setMessage(ss2);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
            System.out.println("paso onPreExecute WS Recibos  ");
        }

        @Override
        protected String doInBackground(String... urls) {
            return POSTCobros(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if(pDialog != null) {
                pDialog.dismiss();
                if (result.equals("OK Recibos")) {
                    Toast toast = Toast.makeText(getBaseContext(), "Datos de Recibos Enviados..!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (result.equals("OK Recibos2")) {
                    Toast toast = Toast.makeText(getBaseContext(), "Datos Temporales de Recibos Enviados..!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                    toast.show();
                }else {
                    Toast toast = Toast.makeText(getBaseContext(), "No hay Datos de Recibos para Sincronizar..!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        }
    }

    private class HttpPostRetencionesAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            String title = "Enviando Datos...";
            String msg = "Retenciones .. Espere por Favor";
            SpannableString ss1=  new SpannableString(title);
            ss1.setSpan(new RelativeSizeSpan(2f), 0, ss1.length(), 0);
            ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, ss1.length(), 0);
            SpannableString ss2=  new SpannableString(msg);
            ss2.setSpan(new RelativeSizeSpan(2f), 0, ss2.length(), 0);
            ss2.setSpan(new ForegroundColorSpan(Color.GREEN), 0, ss2.length(), 0);

            pDialog = new ProgressDialog(context);
            pDialog.setTitle(ss1);
            pDialog.setMessage(ss2);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
            System.out.println("paso onPreExecute WS Retenciones  ");
        }

        @Override
        protected String doInBackground(String... urlws) {
            return POSTRetenciones(urlws[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if(pDialog != null) {
                pDialog.dismiss();
                if (result.equals("OK Save Retenciones..!!!")) {
                    Toast toast = Toast.makeText(getBaseContext(), "Datos de Retenciones Enviados..!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                    toast.show();
                }else if (result.equals("OK Save Retenciones2..!!!")) {
                    Toast toast = Toast.makeText(getBaseContext(), "Datos de Retenciones Enviados..!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getBaseContext(), "No hay Datos de Retenciones para Sincronizar..!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }

        }
    }

    public String POSTRetenciones(String url){
        System.out.println(url);
        InputStream inputStream = null;
        String result = "";
        int n = 0;
        try {

            ret = load_cobrosret();

            if(ret.size()==0)
                n = 0;
            else
                n = ret.size();

            r1 = n;
            //List<CobrosRet> cob = load_cobrosret();

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject

            JSONArray jsonArray = new JSONArray();
            //add data;

            int conta = 1;
            for (CobrosRet row : ret) {
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("empresa", row.getCod_emp());
                jsonParam.put("id_recibo", row.getId_recibo());
                jsonParam.put("fch_ret", row.getFch_ret());
                jsonParam.put("cod_ref", row.getCod_ref());
                jsonParam.put("num_mov", row.getNum_mov());
                jsonParam.put("cod_mov", row.getCod_mov());
                jsonParam.put("fecha", row.getFecha());
                jsonParam.put("estab", row.getEstab());
                jsonParam.put("ptoemi", row.getPto_emi());
                jsonParam.put("secuencia", row.getSecuencia());
                jsonParam.put("autorizacion", row.getNumaut());
                jsonParam.put("cod_retencion", row.getCod_retencion());
                jsonParam.put("prc_retencion", row.getPrc_retencion());
                jsonParam.put("valor", row.getValor());
                jsonParam.put("cod_ven", row.getCod_ven());
                jsonParam.put("vendedor2", row.getVendedor2());
                jsonParam.put("cod_pto", row.getCod_pto());
                jsonParam.put("orden", conta);
                jsonParam.put("descarga", contador);
                System.out.println(jsonParam.toString());
                conta++;
                jsonArray.put(jsonParam);
            }

            // 4. convert JSONObject to JSON to String
            json = jsonArray.toString();
            System.out.println(json);
            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json,"UTF-8");

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json;charset=UTF-8");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
            //httpResponse.setStatusCode(RESULT_OK);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
                System.out.println("respuesta " + result);
                if(result.equals("OK Save Retenciones..!!!")){
                    //updateRet(cob);
                    //setSecuenciaDesc();
                }
            } else
                result = "Error no sincronizado..!";
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private class HttpPostSecuencias extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            System.out.println("paso onPreExecute WS Secuencias  ");
        }

        @Override
        protected String doInBackground(String... urls) {
            return POSTSecuencias(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if(pDialog != null) {
                pDialog.dismiss();
                if (result.equals("OK Update Secuencias")) {
                    Toast toast = Toast.makeText(getBaseContext(), "Secuencias Actualizadas..!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getBaseContext(), "No se pudo sincronizar secuencias..!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        }
    }

    public String POSTSecuencias(String url){
        int rec=0, ret=0, ncr=0, nap=0;
        InputStream inputStream = null;
        String result = "";
        try {

            sec = load_secuencias();

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject

            JSONArray jsonArray = new JSONArray();
            //add data;

            for (Secuenciales row : sec) {
                if(row.getTipo().equals("REC"))
                    rec = row.getSecuencial()-1;
                else if(row.getTipo().equals("RET"))
                    ret = row.getSecuencial()-1;
                else if(row.getTipo().equals("RNC"))
                    ncr = row.getSecuencial()-1;
                else if(row.getTipo().equals("NSC"))
                    nap = row.getSecuencial()-1;
            }

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("empresa", lista.getCod_emp());
            jsonParam.put("cod_ref", lista.getCod_vend());
            jsonParam.put("vendedor", lista.getNombre_vend());
            jsonParam.put("recibo", rec);
            jsonParam.put("retenc", ret);
            jsonParam.put("ncredi", ncr);
            jsonParam.put("ncinte", nap);
            jsonArray.put(jsonParam);

            // 4. convert JSONObject to JSON to String
            json = jsonArray.toString();
            System.out.println(json);
            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json,"UTF-8");

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json;charset=UTF-8");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
            //httpResponse.setStatusCode(RESULT_OK);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
                System.out.println("respuesta " + result);
                if(result.equals("OK Update Secuencias..!!!")){
                    //updateRet(cob);
                    //setSecuenciaDesc();
                }
            } else
                result = "Error no sincronizado..!";
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public List<Cobros_Doc> load_cobpend(){
        items9 = new ArrayList<Cobros_Doc>();
        for(RowFacturaNC row : items8){
            Cobros_Doc cb = new Cobros_Doc();
            cb.setCod_emp(row.getCod_emp());
            cb.setId_recibo(0);
            Date d = new Date();
            cb.setFch_emision(Utils.ConvertirDateToStringShort(d));
            cb.setCod_cli(row.getCod_ref());
            cb.setNom_cli(row.getCod_vin().trim());
            cb.setCod_vend(row.getCod_ven());
            cb.setVendedor2(row.getVendedor());
            cb.setCod_mov("NA");
            cb.setNum_mov(row.getNum_mov());
            cb.setFch_emi(row.getFec_emis());
            cb.setFch_ven(row.getFec_venc());
            cb.setTipo_cobro("");
            cb.setNum_doc(row.getNum_mov());
            cb.setCod_bco(row.getCod_bco());
            cb.setNum_cuenta(row.getNum_cuenta());
            cb.setValor(row.getSdo_mov());
            cb.setPto_vta(row.getCod_pto());
            cb.setEstado(0);
            items9.add(cb);
        }
        return items9;
    }

    public List<Cobros_Doc> load_cobrosdoc(){
        SQLiteDatabase db = null;
        Cursor q = null;
        items3 = new ArrayList<Cobros_Doc>();
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM COBROS_DOC WHERE cod_emp='"+lista.getCod_emp()+"' AND cod_vend='"+lista.getCod_vend()+"'";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    Cobros_Doc cd = new Cobros_Doc();
                    cd.setCod_emp(q.getString(1));
                    cd.setId_recibo(q.getInt(2));
                    cd.setFch_emision(q.getString(3));
                    Date reg = Utils.ConvertirShortStringToDate(cd.getFch_emision());
                    String reg1 = Utils.ConvertirDateToStringShort(reg);
                    cd.setFch_emision(reg1);
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
                    cd.setEstado(q.getInt(19));
                    items3.add(cd);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return items3;
    }

    public List<Cobros_Pagos> load_cobrospagos(){
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT DISTINCT cp.cod_emp,cp.id_recibo,cp.fpago,cp.banco,cp.cuenta,cp.cheque,cp.fch_pago,cp.valor," +
                        "                cp.cod_ref,cp.cod_vend,cp.cod_vin,cp.vendedor,cp.fch_registro,cp.girador,cp.cod_rec,cp.status " +
                        "FROM COBROS_PAGOS cp, COBROS_DOC cd " +
                        "WHERE cp.cod_emp=cd.cod_emp AND cp.id_recibo=cd.id_recibo " +
                        "AND cd.cod_emp='"+lista.getCod_emp()+"' AND cd.cod_vend='"+lista.getCod_vend()+"'";
            items4 = new ArrayList<Cobros_Pagos>();
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    String emp = q.getString(0);
                    int idrecibo = q.getInt(1);
                    String fpago = q.getString(2);
                    String banco  = q.getString(3);
                    String cuenta = q.getString(4);
                    String cheque = q.getString(5);
                    String fch_pago = q.getString(6);
                    double valor = q.getDouble(7);
                    String cod_ref = q.getString(8);
                    String cod_vend = q.getString(9);
                    String cod_vin = q.getString(10);
                    String vendedor = q.getString(11);
                    String fch_reg = q.getString(12);
                    Date reg = Utils.ConvertirShortStringToDate(fch_reg);
                    String girador = q.getString(13);
                    String cod_rec = q.getString(14);
                    String estado = q.getString(15);
                    items4.add(new Cobros_Pagos(idrecibo,emp,fpago,banco,cuenta,cheque,fch_pago,valor,cod_ref,cod_vend,
                                                cod_vin,vendedor,reg,girador,cod_rec,estado));
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return items4;
    }

    public void updateDoc(List<Cobros_Doc> cobros){
        List<Cobros_Doc> cob = cobros;
        try {
            SQLiteDatabase db = null;
            for (Cobros_Doc row: cob) {
                String SQL = "DELETE FROM COBROS_DOC " +
                             "WHERE cod_emp='"+lista.getCod_emp()+"' AND id_recibo="+row.getId_recibo() +" AND cod_vend='"+lista.getCod_vend()+"'";
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }

    }

    public void updatePagos(List<Cobros_Doc> cobros){
        List<Cobros_Doc> cob = cobros;
        try {
            SQLiteDatabase db = null;
            for (Cobros_Doc row: cob) {
                String SQL = "DELETE FROM COBROS_PAGOS " +
                             "WHERE cod_emp='"+lista.getCod_emp()+"' AND id_recibo="+row.getId_recibo() +" AND cod_vend='"+lista.getCod_vend()+"'";
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }

    }

    public List<CobrosRet> load_cobrosret(){
        SQLiteDatabase db = null;
        Cursor q = null;
        items6 = new ArrayList<CobrosRet>();
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM COBROS_RET WHERE estado=0 AND cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"'";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()){
                do {
                    CobrosRet cd = new CobrosRet();
                    cd.setCod_emp(q.getString(1));
                    cd.setId_recibo(q.getInt(2));
                    cd.setFch_ret(q.getString(3));
                    cd.setCod_ref(q.getString(4));
                    cd.setNum_mov(q.getString(5));
                    cd.setCod_mov(q.getString(6));
                    cd.setFecha(q.getString(7));
                    cd.setEstab(q.getString(8));
                    cd.setPto_emi(q.getString(9));
                    cd.setSecuencia(q.getString(10));
                    cd.setNumaut(q.getString(11));
                    cd.setCod_retencion(q.getString(12));
                    cd.setPrc_retencion(q.getDouble(13));
                    cd.setValor(q.getDouble(14));
                    cd.setCod_ven(q.getString(15));
                    cd.setVendedor2(q.getString(16));
                    cd.setCod_pto(q.getString(17));
                    cd.setEstado(q.getInt(18));
                    items6.add(cd);
                }while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return items6;
    }

    public List<Secuenciales> load_secuencias(){
        SQLiteDatabase db = null;
        Cursor q = null;
        items10 = new ArrayList<Secuenciales>();
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM SECUENCIA WHERE cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"'";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
                    Secuenciales cd = new Secuenciales();
                    cd.setId(q.getInt(0));
                    cd.setCod_emp(q.getString(1));
                    cd.setTipo(q.getString(2));
                    cd.setSecuencial(q.getInt(3));
                    cd.setCod_ven(q.getString(4));
                    items10.add(cd);
                } while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return items10;
    }

    public void updateRet(List<CobrosRet> retenciones){
        List<CobrosRet> cob = retenciones;
        try {
            SQLiteDatabase db = null;
            for (CobrosRet row: cob) {
                String SQL = "DELETE FROM COBROS_RET " +
                             "WHERE cod_emp='"+lista.getCod_emp()+"' AND id_recibo="+row.getId_recibo()+" AND cod_ven='"+lista.getCod_vend()+"'";
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }

    }

    public int getCountRecibos(){
        int recibo = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT COUNT(*) FROM COBROS_DOC " +
                        "WHERE cod_emp='"+lista.getCod_emp()+"' AND estado=0 AND cod_vend='"+lista.getCod_vend()+"'";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
                    recibo = q.getInt(0);
                } while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return recibo;
    }

    public int getCountRetenciones(){
        int recibo = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT COUNT(*) FROM COBROS_RET " +
                        "WHERE cod_emp='"+lista.getCod_emp()+"' AND estado=0 AND cod_ven='"+lista.getCod_vend()+"'";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
                    recibo = q.getInt(0);
                } while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return recibo;
    }

    public class TaskGetRetenciones extends AsyncTask<String, Void, String>{
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            String TAG="TaskGetRetenciones";
            String URL= Utils.URLWS+Utils.URLWS_RETENCION+lista.getCod_emp();
            HttpURLConnection connection = null;
            try {
                //Log.v(TAG, URL);
                URL url = new URL(URL);
                connection = (HttpURLConnection) url.openConnection();
                //connection.setRequestProperty ("Authorization", Utils.basicAuth);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                int statusCode = connection.getResponseCode();
                if (statusCode == 200) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    String result= Utils.convertStreamToString(in);
                    try {
                        JSONArray json = new JSONArray(result);
                        items5 = new ArrayList<Retenciones>();
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject jsonobject = json.getJSONObject(i);
                            String emp = jsonobject.getString("empresa");
                            String cod  = jsonobject.getString("cod_retencion");
                            String nomb = jsonobject.getString("nom_retencion");
                            Double porc = jsonobject.getDouble("prc_retencion");
                            Double limr = jsonobject.getDouble("lim_ret");
                            String sts = jsonobject.getString("sts_rxt");
                            String fec = jsonobject.getString("fec_rxt");
                            String fex = jsonobject.getString("fex_rxt");
                            Double porx = jsonobject.getDouble("por_rxt");
                            Log.i(TAG,String.valueOf(i));
                            items5.add(new Retenciones(emp, cod, nomb, porc, limr, sts, fec, fex, porx));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG," ERROR JSON ");
                        //obj = null;
                    }
                } else {
                    Log.e(TAG," ERROR getRetenciones");
                }
            }   catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG," Exception Error . "+e.getMessage() );
            } finally {
                connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String a) {
            if(pDialog != null)
                pDialog.dismiss();
            getRetenciones(items5);
        }

    }

    public void getRetenciones(List<Retenciones> lista){
        List<Retenciones> result = lista;
        try {
            SQLiteDatabase db = null;
            for (Retenciones row : result) {
                String SQL = "INSERT INTO RETENCIONES (empresa,cod_retencion,nom_retencion,prc_retencion,sts_rxt,lim_ret,fec_rxt,fex_rxt,por_rxt) " +
                             "VALUES ('"+ row.getEmpresa() +"','"+ row.getCod_retencion() +"','"+ row.getNom_retencion() +"','" + row.getPrc_retencion() +"','"+
                                      row.getSts_rxt() +"','"+ row.getLim_ret() +"','"+ row.getFec_rxt() +"','" + row.getFex_rxt() +"','"+
                                      row.getPor_rxt() +"')";
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);
            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public void deleteRetenciones(){
        try {
            SQLiteDatabase db = null;
            String SQL = "DELETE FROM RETENCIONES";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public void deleteNotas(){
        try {
            SQLiteDatabase db = null;
            String SQL = "DELETE FROM DOC_LOCAL " +
                         "WHERE cod_emp='"+lista.getCod_emp()+"' AND cod_ven='"+lista.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public void deleteNotasV(){
        try {
            SQLiteDatabase db = null;
            String SQL = "DELETE FROM DOC_LOCAL " +
                         "WHERE cod_emp='"+lista.getCod_emp()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public void deleteNotasT(){
        try {
            SQLiteDatabase db = null;
            String SQL = "DELETE FROM DOC_LOCAL";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
        }
    }

    public class TaskGetVendedores extends AsyncTask<String, Void, String>{
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            String TAG="TaskGetVendedores";
            String URL= Utils.URLWS+Utils.URLWS_VENDEDOR+lista.getCod_emp()+"&codven="+lista.getCod_vend();
            System.out.println("url vendedor: "+URL);
            HttpURLConnection connection = null;
            try {
                //Log.v(TAG, URL);
                URL url = new URL(URL);
                connection = (HttpURLConnection) url.openConnection();
                //connection.setRequestProperty ("Authorization", Utils.basicAuth);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                int statusCode = connection.getResponseCode();
                if (statusCode == 200) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    String result= Utils.convertStreamToString(in);
                    try {
                        JSONObject json = new JSONObject(result);
                        vendedor = new VendedorWS();
                        String emp = json.getString("empresa");
                        String cod = json.getString("cod_ref");
                        String nomb = json.getString("vendedor");
                        String selec = "";
                        String ven1 = "";
                        String ven2 = "";
                        String ven3 = "";
                        String ven4 = "";
                        String ven5 = "";
                        int recibo = 0;
                        int retenc = 0;
                        int ncredi = 0;
                        int ninter = 0;
                        int udesc = json.getInt("descarga");
                        int urec = 0;
                        int sta = json.getInt("status");
                        //Log.i(TAG,String.valueOf(i));
                        vendedor = new VendedorWS(emp, cod, nomb, selec, ven1, ven2, ven3, ven4, ven5, recibo, retenc, ncredi, ninter, udesc, urec, sta);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG," ERROR JSON ");
                        //obj = null;
                    }
                } else {
                    Log.e(TAG," ERROR getVendedor");
                }
            }   catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG," Exception Error . "+e.getMessage() );
            } finally {
                connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String a) {
            if(pDialog != null)
                pDialog.dismiss();
        }

    }

    public class TaskGetSecuencias extends AsyncTask<String, Void, Integer>{
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }
        @Override
        protected Integer doInBackground(String... params) {
            String TAG="TaskGetSecuencias";
            int size = 0;
            String URL= Utils.URLWS+Utils.URLWS_SECUENCIAS+lista.getCod_emp()+"&codven="+lista.getCod_vend();
            System.out.println("url vendedor: "+URL);
            HttpURLConnection connection = null;
            try {
                //Log.v(TAG, URL);
                URL url = new URL(URL);
                connection = (HttpURLConnection) url.openConnection();
                //connection.setRequestProperty ("Authorization", Utils.basicAuth);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                int statusCode = connection.getResponseCode();
                if (statusCode == 200) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    String result= Utils.convertStreamToString(in);
                    try {
                        JSONObject json = new JSONObject(result);
                        nrecibos = new VendedorWS();
                        String emp = json.getString("empresa");
                        String cod = json.getString("cod_ref");
                        String nomb = json.getString("vendedor");
                        String selc = json.getString("seleccion");
                        String ven1 = json.getString("vendedor1");
                        String ven2 = json.getString("vendedor2");
                        String ven3 = json.getString("vendedor3");
                        String ven4 = json.getString("vendedor4");
                        String ven5 = json.getString("vendedor5");
                        int recibo = json.getInt("recibo");
                        int retenc = json.getInt("retenc");
                        int ncredi = json.getInt("ncredi");
                        int ninter = json.getInt("ncinte");
                        int udesc = 0;
                        int urec = 0;
                        int sta = 1;
                        //Log.i(TAG,String.valueOf(i));
                        nrecibos = new VendedorWS(emp, cod, nomb, selc, ven1, ven2, ven3, ven4, ven5, recibo, retenc, ncredi, ninter, udesc, urec, sta);

                        size = nrecibos==null ? 0 : 1;

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG," ERROR JSON ");
                        //obj = null;
                    }
                } else {
                    Log.e(TAG," ERROR getVendedor");
                }
            }   catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG," Exception Error . "+e.getMessage() );
            } finally {
                connection.disconnect();
            }
            return size;
        }

        @Override
        protected void onPostExecute(Integer a) {
            if(pDialog != null)
                pDialog.dismiss();

            if (a == 1) {
                updateSeleccion(nrecibos);
            }else {

            }
        }

    }

    public class TaskGetClienteRel extends AsyncTask<String, Void, Integer>{
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            System.out.println("pasooo3 onPreExecute Clientes Rel ");
        }
        @Override
        protected Integer doInBackground(String... params) {
            String TAG="TaskGetClientesRel";
            int tamañorel = 0;
            String URL= Utils.URLWS+Utils.URLWS_CLIENTESREL+lista.getCod_emp();
            HttpURLConnection connection = null;
            try {
                //Log.v(TAG, URL);
                URL url = new URL(URL);
                connection = (HttpURLConnection) url.openConnection();
                //connection.setRequestProperty ("Authorization", Utils.basicAuth);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                int statusCode = connection.getResponseCode();
                if (statusCode == 200) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    String result= Utils.convertStreamToString(in);
                    try {
                        JSONArray json = new JSONArray(result);
                        items7 = new ArrayList<ClientesRel>();
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject jsonobject = json.getJSONObject(i);
                            String emp = jsonobject.getString("empresa");
                            String cod = jsonobject.getString("cod_ref");
                            String nomb = jsonobject.getString("cod_vin");
                            int ord = jsonobject.getInt("ord_vin");
                            items7.add(new ClientesRel(emp, cod, nomb, ord));
                        }
                        tamañorel = items7.size();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG,"ERROR JSON");
                    }
                } else {
                    Log.e(TAG," ERROR getClientesRel");
                }
            }   catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG," Exception Error . "+e.getMessage() );
            } finally {
                connection.disconnect();
            }
            return tamañorel;
        }

        @Override
        protected void onPostExecute(Integer a) {
            if(pDialog != null){
                pDialog.dismiss();
                if (a > 0) {
                    Log.e(TAG,"Si hay datos para guardar");
                    getClienteRel(items7);
                }else{
                    Log.e(TAG,"ERROR Sin datos que mostrar");
                }
            }

        }

    }

    public class TaskGetDescarga extends AsyncTask<String, Void, Integer>{
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            System.out.println("pasooo4 onPreExecute Descarga ");
        }
        @Override
        protected Integer doInBackground(String... params) {
            String TAG="TaskGetDescarga";
            String URL= Utils.URLWS+Utils.URLWS_DESCARGAS+lista.getCod_emp()+"&codven="+lista.getCod_vend();
            System.out.println("mi url "+Utils.URLWS+Utils.URLWS_DESCARGAS+lista.getCod_vend());
            HttpURLConnection connection = null;
            try {
                //Log.v(TAG, URL);
                URL url = new URL(URL);
                connection = (HttpURLConnection) url.openConnection();
                //connection.setRequestProperty ("Authorization", Utils.basicAuth);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                int statusCode = connection.getResponseCode();
                if (statusCode == 200) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    String result= Utils.convertStreamToString(in);
                    try {
                        //JSONObject json = new JSONObject(result);
                        //res = json.getInt("resultado");
                        res = Integer.valueOf(result);

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Log.i(TAG," ERROR NumberFormat ResultDescarga");
                    } /*catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG," ERROR JSON ");
                    }*/
                } else {
                    Log.e(TAG," ERROR getDescarga");
                }
            }   catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG," Exception Error . "+e.getMessage() );
            } finally {
                connection.disconnect();
            }
            return res;
        }

        @Override
        protected void onPostExecute(Integer a) {
            if(pDialog != null){
                pDialog.dismiss();
                if (a == 0) {
                    Log.e(TAG,"Tiene permisos..");
                }else if (a == 1){
                    Log.e(TAG,"No tiene permisos..");
                }
            }

        }

    }

    public void getClienteRel(List<ClientesRel> lista){
        List<ClientesRel> result = lista;
        try {
            SQLiteDatabase db = null;
            for (ClientesRel row : result) {
                String SQL = "INSERT INTO CLIENTES_REL (cod_emp,cod_ref,cod_vin,ord_vin) " +
                             "VALUES ('"+row.getEmpresa()+"','" + row.getCod_ref() + "','"+ row.getCod_vin() +"'," + row.getOrd_vin() +")";
                db = dbSql.getWritableDatabase();
                db.execSQL(SQL);

            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error INSERT CLIENTESREL. " + e.getMessage());
        }
    }

    public void deleteClienteRel(){
        try {
            SQLiteDatabase db = null;
            String SQL = "DELETE FROM CLIENTES_REL WHERE cod_emp='"+lista.getCod_emp()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error DELETE CLIENTES_REL. " + e.getMessage());
        }
    }

    public int getCountPendientes(){
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT COUNT(*) FROM COBROS_PAGOS WHERE cod_emp='"+lista.getCod_emp()+"' AND fpago='NA' AND status='P'";
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

    public void updateSecRecibos(){
        int s = nrecibos.getRecibo()+1;
        try {
            SQLiteDatabase db = null;
            String SQL = "UPDATE SECUENCIA SET secuencia="+s
                       +" WHERE cod_emp='"+lista.getCod_emp()+"' AND tipo='REC' AND cod_ven='"+lista.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("nueva secuencia "+s);
    }

    public void updateSecRetenciones(){
        int s = nrecibos.getRetenc()+1;
        try {
            SQLiteDatabase db = null;
            String SQL = "UPDATE SECUENCIA SET secuencia="+s
                       +" WHERE cod_emp='"+lista.getCod_emp()+"' AND tipo='RET' AND cod_ven='"+lista.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("nueva secuencia "+s);
    }

    public void updateSecNcreditos(){
        int s = nrecibos.getNcredi()+1;
        try {
            SQLiteDatabase db = null;
            String SQL = "UPDATE SECUENCIA SET secuencia="+s
                       +" WHERE cod_emp='"+lista.getCod_emp()+"' AND tipo='RNC' AND cod_ven='"+lista.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("nueva secuencia "+s);
    }

    public void updateSecNcinterna(){
        int s = nrecibos.getNcinte()+1;
        try {
            SQLiteDatabase db = null;
            String SQL = "UPDATE SECUENCIA SET secuencia="+s
                       +" WHERE cod_emp='"+lista.getCod_emp()+"' AND tipo='NSC' AND cod_ven='"+lista.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("nueva secuencia "+s);
    }

    public void updateSeleccion(VendedorWS ven){
        SQLiteDatabase db = null;
        String SQL = "";
        try {
            SQL = "UPDATE VENDEDOR SET seleccion='"+ven.getSeleccion()+"', vend1='"+ven.getVendedor1()+"', vend2='"+ven.getVendedor2()
                +"', vend3='"+ven.getVendedor3()+"', vend4='"+ven.getVendedor4()+"', vend5='"+ven.getVendedor5()+"'"
                +" WHERE cod_emp='"+lista.getCod_emp()+"' AND cod_vend='"+lista.getCod_vend()+"'";
            db = dbSql.getWritableDatabase();
            db.execSQL(SQL);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, " Exception Error . " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Vendedor users(String codigo){
        SQLiteDatabase db = null;
        Cursor q = null;
        Vendedor v = new Vendedor();
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM VENDEDOR WHERE cod_emp='"+lista.getCod_emp()+"' AND cod_vend='"+codigo+"'";
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

    /*private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Activar Localizacion")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación.")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void toggleGPSUpdates() {
        if (!checkLocation())
            return;

        if (!state) {
            locationManager.removeUpdates(locationListenerGPS);
            System.out.println("state false");
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1 * 10 * 1000, 10, locationListenerGPS);
            System.out.println("state true");
        }
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SyncActivity.this, "GPS Provider update", Toast.LENGTH_SHORT).show();
                    System.out.println("Latitud :: "+latitudeGPS);
                    System.out.println("Longitud :: "+longitudeGPS);
                }
            });
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }
        @Override
        public void onProviderDisabled(String s) {
        }
    };*/

    public void toggleGPSUpdates() {
        LocationManager locationManager = (LocationManager) SyncActivity.this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //makeUseOfNewLocation(location);
                System.out.println("Latitud:: "+location.getLatitude()+ "// Longitud:: "+location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        int permissionCheck = ContextCompat.checkSelfPermission(SyncActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("item", lista);
                intent.putExtras(bundle);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
