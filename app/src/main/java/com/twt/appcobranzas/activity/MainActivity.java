package com.twt.appcobranzas.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.model.Vendedor;
import com.twt.appcobranzas.utils.Utils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context;
    Vendedor vendedor;
    String codigo="";
    String TAG = "MainActivity";
    int res = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        Bundle bundle = getIntent().getExtras();
        vendedor = bundle.getParcelable("item");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    public class TaskGetDescarga extends AsyncTask<String, Void, Integer> {
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
            String URL= Utils.URLWS+Utils.URLWS_DESCARGAS+vendedor.getCod_emp()+"&codven="+vendedor.getCod_vend();
            HttpURLConnection connection = null;
            try {
                //Log.v(TAG, URL);
                java.net.URL url = new URL(URL);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.clientes) {
            /*if(res==0) {
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
            } else {
                Intent intent = new Intent(getApplicationContext(), ClientesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("lista", vendedor);
                intent.putExtras(bundle);
                intent.putExtra("codigo", codigo);
                this.startActivity(intent);
                return true;
            }*/
            Intent intent = new Intent(getApplicationContext(), ClientesActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("lista", vendedor);
            intent.putExtras(bundle);
            intent.putExtra("codigo", codigo);
            intent.putExtra("codaux", "");
            this.startActivity(intent);
            return true;
        } else if (id == R.id.recibos) {
            Intent intent = new Intent(getApplicationContext(), RecibosActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("lista", vendedor);
            intent.putExtras(bundle);
            this.startActivity(intent);
            return true;
        }else if (id == R.id.retenciones) {
            Intent intent = new Intent(getApplicationContext(), RecibosRetActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("lista", vendedor);
            intent.putExtras(bundle);
            this.startActivity(intent);
            return true;
        }else if (id == R.id.notascredito) {
            Intent intent = new Intent(getApplicationContext(), RecibosNotasCreditoActivity.class);
            intent.putExtra("doc1","NC");
            Bundle bundle = new Bundle();
            bundle.putParcelable("lista", vendedor);
            intent.putExtras(bundle);
            this.startActivity(intent);
            return true;
        }else if (id == R.id.notascreditog) {
            Intent intent = new Intent(getApplicationContext(), RecibosNotasCreditoActivity.class);
            intent.putExtra("doc1","NG");
            Bundle bundle = new Bundle();
            bundle.putParcelable("lista", vendedor);
            intent.putExtras(bundle);
            this.startActivity(intent);
            return true;
        }else if (id == R.id.notascreditoapp) {
            Intent intent = new Intent(getApplicationContext(), RecibosNotasCreditoActivity.class);
            intent.putExtra("doc1","NA");
            Bundle bundle = new Bundle();
            bundle.putParcelable("lista", vendedor);
            intent.putExtras(bundle);
            this.startActivity(intent);
            return true;
        }else if(id == R.id.sincronizar){
            Intent intent = new Intent(getApplicationContext(), SyncActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("lista", vendedor);
            intent.putExtras(bundle);
            this.startActivity(intent);
            return true;
        }else if (id == R.id.salir) {
            Intent intent = new Intent(getApplicationContext(), PinActivity.class);
            this.startActivity(intent);
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
