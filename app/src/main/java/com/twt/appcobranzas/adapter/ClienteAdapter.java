package com.twt.appcobranzas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.model.RowCliente;

//import static com.devecua.android.Utils.decodeSampledBitmapFromResource;

/**
 * Created by DEVECUA on 19/11/2015.
 */
public class ClienteAdapter extends BaseAdapter {

    private Context context;
    private List<RowCliente> items;


    public ClienteAdapter(Context context, List<RowCliente> items){
        this.context=context;
        this.items  =items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
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
            rowView = inflater.inflate(R.layout.clientes_list, parent, false);
        }

        // Set data into the view.
        ImageView ivItem = (ImageView) rowView.findViewById(R.id.icon);
        TextView tvCed  = (TextView)  rowView.findViewById(R.id.ced_ruc);
        TextView tvCod_c = (TextView)  rowView.findViewById(R.id.cod_cli);
        TextView tvNom1 = (TextView)  rowView.findViewById(R.id.nomb1);
        TextView tvNom2 = (TextView)  rowView.findViewById(R.id.nomb2);
        TextView tvCiudad = (TextView)  rowView.findViewById(R.id.ciudad);
        TextView tvProvincia = (TextView)  rowView.findViewById(R.id.provincia);
        TextView tvDireccion = (TextView)  rowView.findViewById(R.id.direccion);
        TextView tvDescuento = (TextView)  rowView.findViewById(R.id.descuento);
        TextView tvFpago = (TextView)  rowView.findViewById(R.id.forma_pago);

        RowCliente item = this.items.get(position);

        tvCed.setText(item.getRuc());
        tvCod_c.setText(item.getCliente());
        tvNom1.setText(item.getN_cliente());
        tvNom2.setText(item.getN_clienteaux());
        tvCiudad.setText(item.getCiudad());
        tvProvincia.setText(item.getProvincia());
        tvDireccion.setText(item.getDireccion().trim());
        tvDescuento.setText(String.valueOf(item.getDescuento()));
        tvFpago.setText(item.getForma_pago().toUpperCase());

        Picasso.with(context)
                .load(R.drawable.ic_cli)
                .placeholder(R.drawable.error1)   // optional
                .error(R.drawable.error1)      // optional
                //.resize(250, 300)                        // optional
                .rotate(0)                             // optional
                .into(ivItem);

        return rowView;
    }

    public static String ucFirst(String cadena) {
        /*if (str == null || str.isEmpty()) {
            return str;
        } else {
            //La primera letra en mayuscula y las demas en minuscula.
            return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
        }*/
        char[] caracteres = cadena.toCharArray();
        caracteres[0] = Character.toUpperCase(caracteres[0]);

        // el -2 es para evitar una excepción al caernos del arreglo
        for (int i = 0; i < cadena.length()- 2; i++)
            // Es 'palabra'
            if (caracteres[i] == ' ' || caracteres[i] == '.' || caracteres[i] == ',')
                // Reemplazamos
                caracteres[i + 1] = Character.toUpperCase(caracteres[i + 1]);

        return new String(caracteres);
    }

}
