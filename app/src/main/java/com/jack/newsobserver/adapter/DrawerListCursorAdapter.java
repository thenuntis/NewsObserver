package com.jack.newsobserver.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.jack.newsobserver.R;

public class DrawerListCursorAdapter extends CursorAdapter {


    private final String field;

    public DrawerListCursorAdapter(Context context, Cursor c, int flags,String s) {
        super(context, c, flags);
        this.field = s;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.simple_drawer_listview_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.findViewById(R.id.drawer_ltr_listview_item);
        String mName = cursor.getString(cursor.getColumnIndex(field));
        textView.setText(mName);
    }

}
