package com.accurascandemo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.accurascandemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qtm-kalpesh on 4/12/15.
 */
public class StringDropDownAdapter extends BaseAdapter {

    private final Context context;
    private List<String> rowItem = new ArrayList<>();

    public StringDropDownAdapter(Context context, List<String> rowItem) {
        this.context = context;
        this.rowItem = rowItem;
    }

    private class ViewHolder {
        TextView txtTitleName;
    }

    @Override
    public int getCount() {
        return rowItem.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("BaseAdapter", "getView");
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.dropdown_list_item, null);
            holder = new ViewHolder();
            holder.txtTitleName = (TextView) convertView.findViewById(R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtTitleName.setText(rowItem.get(position));

        return convertView;
    }
}
