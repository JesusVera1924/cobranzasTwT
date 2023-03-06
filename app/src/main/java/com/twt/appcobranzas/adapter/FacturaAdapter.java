package com.twt.appcobranzas.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.model.RowFactura;

/**
 * Created by DEVECUA on 19/11/2015.
 */
public class FacturaAdapter extends BaseAdapter {

    private Context context;
    private List<RowFactura> items;
    ViewHolder holder;

    public FacturaAdapter(Context context, List<RowFactura> items){
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
            rowView = inflater.inflate(R.layout.facturas_list, null);

            holder = new ViewHolder();
            holder.txtTipo = (TextView) rowView.findViewById(R.id.txt_cod_mov);
            holder.txtFactura = (TextView) rowView.findViewById(R.id.txt_num_factura);
            holder.txtFch_emi = (TextView) rowView.findViewById(R.id.txt_fecha_emision);
            holder.txtValor = (TextView) rowView.findViewById(R.id.txt_valor_total);
            holder.txtValor_saldo = (TextView) rowView.findViewById(R.id.txt_valor_saldo);
            holder.edtAbono = (EditText) rowView.findViewById(R.id.et_abono);
            rowView.setTag(holder);
        }else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.ref = position;
        final RowFactura item = items.get(position);

        /*final EditText etAbono = (EditText) rowView.findViewById(R.id.et_abono);
        etAbono.setTag(item);
        if(item.getAbono() != 0.00){
            etAbono.setText(String.valueOf(item.getAbono()));
        }
        else {
            etAbono.setText("");
        }*/

        // Set data into the view.
        CheckBox chkFactura = (CheckBox) rowView.findViewById(R.id.chk_fac);
        /*TextView tvTipo  = (TextView)  rowView.findViewById(R.id.txt_cod_mov);
        TextView tvFactura  = (TextView)  rowView.findViewById(R.id.txt_num_factura);
        TextView tvFecha_emi = (TextView)  rowView.findViewById(R.id.txt_fecha_emision);
        TextView tvValor = (TextView)  rowView.findViewById(R.id.txt_valor_total);
        TextView tvValor_saldo = (TextView)  rowView.findViewById(R.id.txt_valor_saldo);*/

        chkFactura.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setSeleccion(isChecked);
            }
        });

        chkFactura.setChecked(item.isSeleccion());
        holder.txtTipo.setText(item.getCod_mov());
        holder.txtFactura.setText(item.getNum_mov());
        holder.txtFch_emi.setText(item.getFec_emi().toString());
        holder.txtValor.setText(String.valueOf(item.getVal_mov()));
        holder.txtValor_saldo.setText(String.valueOf(item.getSdo_mov()));
        holder.edtAbono.setText(String.valueOf(item.getAbono()));
        holder.edtAbono.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Double val=.0;
                try {
                    val = new Double(s.toString());
                } catch (NumberFormatException e){
                    val=.0;
                }
                RowFactura it =  items.get(holder.ref);
                it.setAbono(val);
                items.set(holder.ref, it);
            }
        });


        //RowFactura item = this.items.get(position);

        return rowView;
    }

    /*private class MyTextWatcher implements TextWatcher{

        private View view;
        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //do nothing
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //do nothing

        }
        public void afterTextChanged(Editable s) {
                String abono = s.toString().trim();
                double quantity = abono.equals("") ? 0.00 : Double.valueOf(abono);
                int pos = abono.length();

                EditText etAux = (EditText) view.findViewById(R.id.et_abono);
                RowFactura fact = (RowFactura) etAux.getTag();
                Selection.setSelection(s,pos);

                if(fact.getAbono() != quantity){

                    fact.setAbono(quantity);
                    if(fact.getAbono() != 0.00){
                        etAux.setText(String.valueOf(fact.getAbono()));
                        int posicion = etAux.getText().length();
                        Editable aux = etAux.getText();
                        Selection.setSelection(aux,posicion);
                    }
                    else {
                        etAux.setText("");
                    }
                }
            return;
        }
    }*/

    private class ViewHolder {
        TextView txtTipo;
        TextView txtFactura;
        TextView txtFch_emi;
        TextView txtValor;
        TextView txtValor_saldo;
        EditText edtAbono;
        int      ref;
    }
}
