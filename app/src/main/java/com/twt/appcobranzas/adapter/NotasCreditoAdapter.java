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
import com.twt.appcobranzas.model.RowNotasCredito;

/**
 * Created by DEVECUA on 19/11/2015.
 */
public class NotasCreditoAdapter extends BaseAdapter {

    private Context context;
    private List<RowNotasCredito> items;
    ViewHolder holder;

    public NotasCreditoAdapter(Context context, List<RowNotasCredito> items){
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
            rowView = inflater.inflate(R.layout.notas_list, null);

            holder = new ViewHolder();
            holder.ivItem = (ImageView) rowView.findViewById(R.id.icon);
            holder.txtFactura = (TextView) rowView.findViewById(R.id.txt_num_mov);
            holder.txtNumrel = (TextView) rowView.findViewById(R.id.txt_num_rel);
            holder.txtFch_emi = (TextView) rowView.findViewById(R.id.txt_fch_emi);
            holder.txtValor = (TextView) rowView.findViewById(R.id.txt_valortot);
            holder.txtEst = (TextView) rowView.findViewById(R.id.txt_sts);
            rowView.setTag(holder);
        }else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.ref = position;
        final RowNotasCredito item = items.get(position);

        // Set data into the view.
        holder.txtFactura.setText(item.getNum_mov());
        holder.txtNumrel.setText(item.getNum_rel());
        holder.txtFch_emi.setText(item.getFec_emi().toString());
        holder.txtValor.setText(String.valueOf(item.getVal_mov()));

        if(item.getCod_mov().equals("NC"))
            holder.txtEst.setText("APLICADA");
        if(item.getCod_mov().equals("NG"))
            holder.txtEst.setText("APLICADA");
        if(item.getCod_mov().equals("NA"))
            holder.txtEst.setText("APLICADA");

        Picasso.with(context)
                .load(R.drawable.docu)
                .placeholder(R.drawable.error1)   // optional
                .error(R.drawable.error1)      // optional
                .resize(350, 300)                        // optional
                .rotate(0)                             // optional
                .into(holder.ivItem);

        return rowView;
    }

    private class ViewHolder {
        TextView txtNumrel;
        TextView txtFactura;
        TextView txtFch_emi;
        TextView txtValor;
        TextView txtEst;
        ImageView ivItem;
        int      ref;
    }
}
