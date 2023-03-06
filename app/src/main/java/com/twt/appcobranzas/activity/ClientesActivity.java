package com.twt.appcobranzas.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.adapter.ClienteAdapter;
import com.twt.appcobranzas.model.Banco;
import com.twt.appcobranzas.model.BancoDep;
import com.twt.appcobranzas.model.RowCliente;
import com.twt.appcobranzas.model.Vendedor;
import com.twt.appcobranzas.utils.DbHelper;
import com.twt.appcobranzas.utils.Utils;

/**
 * Created by DEVECUA on 11/12/2015.
 */
@SuppressWarnings("deprecation")
public class ClientesActivity extends AppCompatActivity  {

    String TAG="ClientesActivity";
    ListView listView;
    Context context;
    public List<RowCliente> itemsC;
    List<Banco>    bco_ch;
    List<BancoDep> bco_dp;
    Vendedor lista;
    Button   bt_buscar;
    TextView cod;
    EditText cliente;
    Spinner  vendedor;
    ArrayAdapter<String>     adapter;
    public ArrayList<String> vendedores;
    int cont;
    String id, nom, pers, codigo, aut, faut1, codaux;
    String rcobro = "RC", rchequec = "RK", rchequep = "RQ", retencion = "RT";
    String ncredito = "NC", ngint = "NG", ngintapp = "NA";
    String tnc1="NOTAS DE CREDITO - COBROS MARSELLA", tnc2="NOTAS DE CREDITO INTERNAS - COBROS MARSELLA";
    String tf="FACTURAS - COBROS MARSELLA", tr="RETENCIONES - COBROS MARSELLA";
    DbHelper dbSql =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clientes);
        context = this;
        dbSql = new DbHelper(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        codigo = getIntent().getExtras().getString("codigo");
        codaux = getIntent().getExtras().getString("codaux");
        lista = bundle.getParcelable("lista");
        aut = "";
        faut1 = Utils.ConvertirDateToStringShort(new Date());

        //widget
        listView    = (ListView) this.findViewById(R.id.lv_clientes);
        bt_buscar   = (Button)   this.findViewById(R.id.btn_buscar);
        cod         = (TextView) this.findViewById(R.id.txt_cli);
        cliente     = (EditText) this.findViewById(R.id.et_cli);
        vendedor    = (Spinner)  this.findViewById(R.id.spn_ven);

        registerForContextMenu(listView);

        vendedores = new ArrayList<String>();
        bco_ch = new ArrayList<Banco>();
        bco_ch = consulta();
        bco_dp = new ArrayList<BancoDep>();
        bco_dp = consulta1();
        cliente.setText(codigo);

        selectItem();
        onBackPressed();

        if(bco_ch.isEmpty() || bco_dp.isEmpty()){
            CharSequence text = "DEBE SINCRONIZAR LOS PARAMETROS PARA CONTINUAR, VUELVA AL MENU A LA OPCION SINCRONIZAR DATOS ....!!";
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

        if(lista.getSeleccion().equals("T")){
            cont = getCountT();
        }else{
            cont = getCount();
        }
        //cont = getCount();

        if(cont == 0){
            CharSequence text = "NO HAY DATOS, DEBE SINCRONIZAR LA INFORMACION ....!!";
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
            if (lista.getSeleccion().equals("V")) {
                vendedores.add(lista.getCod_vend());
                if (!lista.getVend1().isEmpty())
                    vendedores.add(lista.getVend1());
                if (!lista.getVend2().isEmpty())
                    vendedores.add(lista.getVend2());
                if (!lista.getVend3().isEmpty())
                    vendedores.add(lista.getVend3());
                if (!lista.getVend4().isEmpty())
                    vendedores.add(lista.getVend4());
                if (!lista.getVend5().isEmpty())
                    vendedores.add(lista.getVend5());

                adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_list, vendedores);
                adapter.setDropDownViewResource(R.layout.simple_spinner_list);
                vendedor.setAdapter(adapter);

                vendedor.setVisibility(View.VISIBLE);
            } else {
                vendedor.setVisibility(View.INVISIBLE);
            }

            if (lista.getSeleccion().equals("V") && !codaux.isEmpty()) {
                vendedor.setSelection(obtenerPosicionItem(vendedor, codaux));
                busquedaClientes(codaux);
            } else if(lista.getSeleccion().equals("T")){
                busquedaClientesT();
            }else {
                busquedaClientes();
            }
        }

        bt_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lista.getSeleccion().equals("V")){
                    busquedaClientes(codaux);
                } else if(lista.getSeleccion().equals("T")){
                    busquedaClientesT();
                }else{
                    busquedaClientes();
                }
            }
        });
    }

    public static int obtenerPosicionItem(Spinner spinner, String ven) {
        int posicion = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(ven)) {
                posicion = i;
            }
        }

        return posicion;
    }

    private void selectItem(){
        vendedor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                codaux = vendedor.getSelectedItem().toString();
                System.out.println("vendedor::"+codaux);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    void busquedaClientes(){
        SQLiteDatabase db = null;
        Cursor q = null;
        String criterio = cliente.getText().toString();
        try {
            db = dbSql.getReadableDatabase();
            String[] args = new String[] {criterio.trim(),"%"+criterio.trim().toUpperCase()+"%"};
            String SQL ="SELECT * FROM CLIENTES " +
                        "WHERE cod_emp='"+lista.getCod_emp()+"' AND vendedor='"+lista.getCod_vend()+"' AND (cliente = ? OR n_cliente LIKE ?) " +
                        "ORDER BY cliente";
            itemsC = new ArrayList();
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
                    itemsC.add(new RowCliente(emp,codc,nom1,nom2,dire,ciud,prov,ruc,telf,vend,desc,fpag,mai1,mai2,pers));
                }while (q.moveToNext());
            }
            listView.setAdapter(new ClienteAdapter(context, itemsC));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
    }

    void busquedaClientesT(){
        SQLiteDatabase db = null;
        Cursor q = null;
        String criterio = cliente.getText().toString();
        try {
            db = dbSql.getReadableDatabase();
            String[] args = new String[] {criterio.trim(),"%"+criterio.trim().toUpperCase()+"%"};
            String SQL = "SELECT * FROM CLIENTES " +
                         "WHERE cod_emp='"+lista.getCod_emp()+"' AND (cliente = ? OR n_cliente LIKE ?) " +
                         "ORDER BY cliente";
            itemsC = new ArrayList();
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
                    itemsC.add(new RowCliente(emp,codc,nom1,nom2,dire,ciud,prov,ruc,telf,vend,desc,fpag,mai1,mai2,pers));
                }while (q.moveToNext());
            }
            listView.setAdapter(new ClienteAdapter(context, itemsC));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
    }

    void busquedaClientes(String ven){
        SQLiteDatabase db = null;
        Cursor q = null;
        String SQL = "";
        String criterio = cliente.getText().toString();
        try {
            db = dbSql.getReadableDatabase();
            String[] args = new String[] {criterio.trim(),"%"+criterio.trim().toUpperCase()+"%"};
            SQL = "SELECT * FROM CLIENTES " +
                  "WHERE cod_emp='"+lista.getCod_emp()+"' AND vendedor='"+ven+"' AND (cliente = ? OR n_cliente LIKE ?) " +
                  "ORDER BY cliente";
            itemsC = new ArrayList();
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
                    itemsC.add(new RowCliente(emp,codc,nom1,nom2,dire,ciud,prov,ruc,telf,vend,desc,fpag,mai1,mai2,pers));
                }while (q.moveToNext());
            }
            listView.setAdapter(new ClienteAdapter(context, itemsC));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
    }

    public int getCount(){
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT COUNT(*) FROM CLIENTES WHERE cod_emp='"+lista.getCod_emp()+"' AND vendedor='"+lista.getCod_vend()+"'";
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

    public int getCountT(){
        int c = 0;
        SQLiteDatabase db = null;
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT COUNT(*) FROM CLIENTES WHERE cod_emp='"+lista.getCod_emp()+"'";
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

    public List<Banco> consulta(){
        SQLiteDatabase db = null;
        List<Banco> listab = new ArrayList<>();
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM BANCOS_CH WHERE cod_emp = '"+lista.getCod_emp()+"' ORDER BY nom_banco";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
                    Banco b = new Banco();
                    b.setNom_banco(q.getString(1));
                    listab.add(b);
                } while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return listab;
    }

    public List<BancoDep> consulta1(){
        SQLiteDatabase db = null;
        List<BancoDep> listad = new ArrayList<>();
        Cursor q = null;
        try {
            db = dbSql.getReadableDatabase();
            String SQL ="SELECT * FROM BANCOS_DP WHERE cod_emp = '"+lista.getCod_emp()+"'  ORDER BY nom_banco";
            q = db.rawQuery(SQL,null);
            if (q.moveToFirst()) {
                do {
                    BancoDep b = new BancoDep();
                    b.setNom_banco(q.getString(1));
                    listad.add(b);
                } while (q.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            q.close();
            db.close();
        }
        return listad;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        int mPosition;
        if(v.getId() == R.id.lv_clientes)
        {
            mPosition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
            id = this.itemsC.get(mPosition).getCliente();
            nom = this.itemsC.get(mPosition).getN_cliente().toUpperCase();
            pers = this.itemsC.get(mPosition).getPersona();
            menu.setHeaderTitle(id+" - "+nom);
            this.getMenuInflater().inflate(R.menu.menu_list, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public  boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.mRCobro:
                doFacturas();
                return true;
            case R.id.mRChequeCanje:
                doChequeCanje();
                return true;
            case R.id.mRChequeProtesta:
                doChequeProtesta();
                return true;
            case R.id.mRetenciones:
                doRetenciones();
                return true;
            case R.id.mNotaCredito:
                doNotaCredito();
                return true;
            case R.id.mNotaCreditoInterna:
                doNotaCreditoInterna();
                return true;
            case R.id.mNotaCreditoInternaAPP:
                doNotaCreditoInternaApp();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    void doFacturas(){
        Intent cobro = new Intent(ClientesActivity.this, FacturasFCActivity.class);
        cobro.putExtra("cod_cli", id);
        cobro.putExtra("nomb_cli", nom);
        cobro.putExtra("persona", pers);
        cobro.putExtra("codaux", codaux);
        cobro.putExtra("dato", rcobro);
        cobro.putExtra("titulo",tf);
        cobro.putExtra("titulo1",tr);
        Bundle bundle = new Bundle();
        bundle.putParcelable("item", lista);
        cobro.putExtras(bundle);
        startActivity(cobro);
    }

    void doChequeCanje(){
        Intent cheque = new Intent(ClientesActivity.this, FacturasActivity.class);
        cheque.putExtra("cod_cli", id);
        cheque.putExtra("nomb_cli", nom);
        cheque.putExtra("persona", pers);
        cheque.putExtra("codaux", codaux);
        cheque.putExtra("dato", rchequec);
        cheque.putExtra("titulo",tf);
        cheque.putExtra("titulo1",tr);
        cheque.putExtra("aut", aut);
        cheque.putExtra("faut", faut1);
        Bundle bundlec = new Bundle();
        bundlec.putParcelable("item", lista);
        cheque.putExtras(bundlec);
        startActivity(cheque);
    }

    void doChequeProtesta(){
        Intent chequep = new Intent(ClientesActivity.this, FacturasActivity.class);
        chequep.putExtra("cod_cli", id);
        chequep.putExtra("nomb_cli", nom);
        chequep.putExtra("persona", pers);
        chequep.putExtra("codaux", codaux);
        chequep.putExtra("dato", rchequep);
        chequep.putExtra("titulo",tf);
        chequep.putExtra("titulo1",tr);
        chequep.putExtra("aut", aut);
        chequep.putExtra("faut", faut1);
        Bundle bundlep = new Bundle();
        bundlep.putParcelable("item", lista);
        chequep.putExtras(bundlep);
        startActivity(chequep);
    }

    void doRetenciones(){
        if(pers.equals("0")){
            CharSequence text = "CLIENTE NO ESTA PERMITIDO PARA RETENCIONES ...!!";
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
            Intent retenc = new Intent(ClientesActivity.this, FacturasActivity.class);
            retenc.putExtra("cod_cli", id);
            retenc.putExtra("nomb_cli", nom);
            retenc.putExtra("persona", pers);
            retenc.putExtra("codaux", codaux);
            retenc.putExtra("dato", retencion);
            retenc.putExtra("titulo",tf);
            retenc.putExtra("titulo1",tr);
            retenc.putExtra("aut", aut);
            retenc.putExtra("faut", faut1);
            System.out.println("fecha :: "+faut1);
            Bundle bundler = new Bundle();
            bundler.putParcelable("item", lista);
            retenc.putExtras(bundler);
            startActivity(retenc);
        }

    }

    void doNotaCredito(){
        Intent ncred = new Intent(ClientesActivity.this, NotasCreditoActivity.class);
        ncred.putExtra("cod_cli", id);
        ncred.putExtra("nomb_cli", nom);
        ncred.putExtra("persona", pers);
        ncred.putExtra("codaux", codaux);
        ncred.putExtra("dato", ncredito);
        ncred.putExtra("titulo2",tnc1);
        ncred.putExtra("titulo3",tnc2);
        Bundle bundlenc = new Bundle();
        bundlenc.putParcelable("item", lista);
        ncred.putExtras(bundlenc);
        startActivity(ncred);
    }

    void doNotaCreditoInterna(){
        Intent nicred = new Intent(ClientesActivity.this, NotasCreditoActivity.class);
        nicred.putExtra("cod_cli", id);
        nicred.putExtra("nomb_cli", nom);
        nicred.putExtra("persona", pers);
        nicred.putExtra("codaux", codaux);
        nicred.putExtra("dato", ngint);
        nicred.putExtra("titulo2",tnc1);
        nicred.putExtra("titulo3",tnc2);
        Bundle bundlenic = new Bundle();
        bundlenic.putParcelable("item", lista);
        nicred.putExtras(bundlenic);
        startActivity(nicred);
    }

    void doNotaCreditoInternaApp(){
        Intent nicred = new Intent(ClientesActivity.this, NotasCreditoActivity.class);
        nicred.putExtra("cod_cli", id);
        nicred.putExtra("nomb_cli", nom);
        nicred.putExtra("persona", pers);
        nicred.putExtra("codaux", codaux);
        nicred.putExtra("dato", ngintapp);
        nicred.putExtra("titulo2",tnc1);
        nicred.putExtra("titulo3",tnc2);
        Bundle bundlenic = new Bundle();
        bundlenic.putParcelable("item", lista);
        nicred.putExtras(bundlenic);
        startActivity(nicred);
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
