package com.test.helloworld.androidtest2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.test.helloworld.androidtest2.R;

import java.util.List;

public class ContentResolverTestAdapter extends ArrayAdapter {

    private int resourceId;
    private List<List<String>> phones;

    private Context c;

    public ContentResolverTestAdapter(@NonNull Context context, int resource, List<List<String>> phones) {
        super(context, resource, phones);
        resourceId = resource;
        this.phones = phones;
        c = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        List<String> item = (List<String>) getItem(position);
        View v = convertView;
        ViewHoler vh = new ViewHoler();
        if (v == null) {
            v = LayoutInflater.from(c).inflate(R.layout.content_resolver_item, null);
            vh.tvName = v.findViewById(R.id.content_resolver_name_tv);
            vh.tvNumber = v.findViewById(R.id.content_resolver_number_tv);
            v.setTag(vh);
        } else {
            v = convertView;
            vh = (ViewHoler) v.getTag();
        }
        vh.tvName.setText(item.get(0));
        vh.tvNumber.setText(item.get(1));
        return v;
    }

    class ViewHoler {
        TextView tvName;
        TextView tvNumber;
    }
}
