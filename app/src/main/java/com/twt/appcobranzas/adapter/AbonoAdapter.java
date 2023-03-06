package com.twt.appcobranzas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import com.twt.appcobranzas.R;
import com.twt.appcobranzas.model.Cobros_Pagos;

//import static com.devecua.android.Utils.decodeSampledBitmapFromResource;

/**
 * Created by DEVECUA on 19/11/2015.
 */
public class AbonoAdapter extends BaseAdapter {

    private Context context;
    private List<Cobros_Pagos> items;


    public AbonoAdapter(Context context, List<Cobros_Pagos> items){
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
            rowView = inflater.inflate(R.layout.abonos_list, parent, false);
        }

        Cobros_Pagos item = this.items.get(position);

        // Set data into the view.
        Button btDelete  = (Button) rowView.findViewById(R.id.btn_borrar);
        btDelete.setTag(position);
        btDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Integer index = (Integer) v.getTag();
                items.remove(index.intValue());
                notifyDataSetChanged();
            }
        });


        TextView tvFPago = (TextView)  rowView.findViewById(R.id.txt_fpago);
        TextView tvFch = (TextView)  rowView.findViewById(R.id.txt_fch);
        TextView tvCta = (TextView)  rowView.findViewById(R.id.txt_cta);
        TextView tvChe = (TextView)  rowView.findViewById(R.id.txt_che);
        TextView tvBanco = (TextView)  rowView.findViewById(R.id.txt_banco);
        TextView tvAbono = (TextView)  rowView.findViewById(R.id.txt_valor_abonado);

        tvFPago.setText(item.getFpago());
        tvFch.setText(item.getFch_pago());
        tvCta.setText(item.getCuenta());
        tvChe.setText(item.getCheque());
        tvBanco.setText(item.getBanco());
        tvAbono.setText(String.valueOf(item.getValor()));


        return rowView;
    }

}
