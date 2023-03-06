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
import com.twt.appcobranzas.model.RowRetenciones;

//import static com.devecua.android.Utils.decodeSampledBitmapFromResource;

/**
 * Created by DEVECUA on 19/11/2015.
 */
public class RetencionesAdapter extends BaseAdapter {

    private Context context;
    private List<RowRetenciones> items;


    public RetencionesAdapter(Context context, List<RowRetenciones> items){
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
            rowView = inflater.inflate(R.layout.retenc_list, parent, false);
        }

        // Set data into the view.
        ImageView ivItem = (ImageView) rowView.findViewById(R.id.icon);
        TextView tvRecibo  = (TextView)  rowView.findViewById(R.id.txt_idrecibo);
        TextView tvCod_c = (TextView)  rowView.findViewById(R.id.txt_codcliente);
        TextView tvNum_m = (TextView)  rowView.findViewById(R.id.txt_nummov);
        TextView tvCod_m = (TextView)  rowView.findViewById(R.id.txt_codmov);
        TextView tvNum_r = (TextView)  rowView.findViewById(R.id.txt_numret);
        TextView tvValor = (TextView)  rowView.findViewById(R.id.txt_valorret);

        RowRetenciones item = this.items.get(position);

            tvRecibo.setText(String.valueOf(item.getId_recibo()));
            tvCod_c.setText(item.getCod_ref());
            tvNum_m.setText(item.getNum_mov());
            tvCod_m.setText(item.getCod_mov());
            tvNum_r.setText(item.getEstab()+"-"+item.getPto_emi()+"-"+item.getSecuencia());
            tvValor.setText(String.valueOf(item.getValor()));

        Picasso.with(context)
                .load(R.drawable.docu)
                .placeholder(R.drawable.error1)   // optional
                .error(R.drawable.error1)      // optional
                .resize(350, 300)                        // optional
                .rotate(0)                             // optional
                .into(ivItem);

        return rowView;
    }
}
