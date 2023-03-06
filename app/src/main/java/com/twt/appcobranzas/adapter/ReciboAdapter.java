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
import com.twt.appcobranzas.model.RowRecibo;
import com.twt.appcobranzas.utils.Utils;

//import static com.devecua.android.Utils.decodeSampledBitmapFromResource;

/**
 * Created by DEVECUA on 19/11/2015.
 */
public class ReciboAdapter extends BaseAdapter {

    private Context context;
    private List<RowRecibo> items;


    public ReciboAdapter(Context context, List<RowRecibo> items){
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
            rowView = inflater.inflate(R.layout.recibos_list, parent, false);
        }

        // Set data into the view.
        ImageView ivItem = (ImageView) rowView.findViewById(R.id.icon);
        TextView tvRecibo  = (TextView)  rowView.findViewById(R.id.txt_idrecibo);
        TextView tvCod_c = (TextView)  rowView.findViewById(R.id.txt_codcliente);
        TextView tvNom_c = (TextView)  rowView.findViewById(R.id.txt_nomcliente);
        TextView tvValor = (TextView)  rowView.findViewById(R.id.txt_valorrecibo);
        TextView tvEmail = (TextView)  rowView.findViewById(R.id.txt_emailrecibo);

        RowRecibo item = this.items.get(position);

            tvRecibo.setText(String.valueOf(item.getId_recibo()));
            tvCod_c.setText(item.getCod_cli());
            tvNom_c.setText(item.getNom_cli());
            tvValor.setText(String.valueOf(Utils.Redondear(item.getSuma(),2)));
            if(item.getEstado()==0)
                tvEmail.setText("No");
            else if(item.getEstado()==2)
                tvEmail.setText("Si");

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
