package com.example.petsproject;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.petsproject.data.PetContract.PetEntry;

public class PetCursorAdapter extends CursorAdapter {
    /**
     * {@link PetCursorAdapter} is an adapter for a list or grid view
     * that uses a {@link Cursor} of pet data as its data source. This adapter knows
     * how to create list items for each row of pet data in the {@link Cursor}.
     */
    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.name);
        TextView summaryTextView = view.findViewById(R.id.summary);
        System.out.println(cursor.getCount());
        if (cursor.getCount() != 0) {
            String nameData = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_NAME));
            String summaryData = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_BREED));
            nameTextView.setText(nameData);
            System.out.println(summaryData.getClass().getName());
            if (!summaryData.isEmpty()) {
                summaryTextView.setText(summaryData);
            } else {
                summaryTextView.setText(R.string.unkown_breed);
            }
        }
    }
}
