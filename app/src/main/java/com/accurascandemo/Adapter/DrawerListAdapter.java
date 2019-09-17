package com.accurascandemo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.accurascandemo.R;

import java.util.ArrayList;

/**
 * Created by latitude on 6/10/17.
 */

public class DrawerListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> menuItemList;

    public DrawerListAdapter(Context context, ArrayList<String> navItems) {
        mContext = context;
        menuItemList = navItems;
    }

    @Override
    public int getCount() {
        return menuItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_item, null);
            holder = new ViewHolder();
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvTitle.setText(menuItemList.get(position));

        switch (position){

            case 0:
                holder.tvTitle.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.about, 0, 0, 0);
                break;

            case 1:
                holder.tvTitle.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.document, 0, 0, 0);
                break;

            case 2:
                holder.tvTitle.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.privacy, 0, 0, 0);
                break;

            case 3:
                holder.tvTitle.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.contact, 0, 0, 0);
                break;

        }

        return convertView;
    }

    private class ViewHolder {
        TextView tvTitle;
    }
}