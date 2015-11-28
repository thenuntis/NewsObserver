package com.jack.newsobserver.adapter;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorTreeAdapter;

import com.jack.newsobserver.helper.DatabaseHelper;

public class DrawerCursorAdapter extends SimpleCursorTreeAdapter {
    private final Context ctx;

    public DrawerCursorAdapter(Context context, Cursor cursor, int groupLayout, String[] groupFrom, int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
        super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
        this.ctx=context;

    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        DatabaseHelper mDatabaseHelper = DatabaseHelper.getInstance(ctx);
        int id = groupCursor.getInt(groupCursor.getColumnIndex("_id"));
        return mDatabaseHelper.getTopicDataByCategory(id);
    }

}
