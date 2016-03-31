package me.aeternussamurai.frequencymusicplayer.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Chase on 1/30/2016.
 */
public abstract class CursorRecyclerAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    protected boolean dataValid;
    protected Cursor cursor;
    protected int rowIDColumn;

    public CursorRecyclerAdapter(Cursor c){
        init(c);
    }

    private void init(Cursor c){
        boolean cursorPresent = c != null;
        cursor = c;
        dataValid = cursorPresent;
        rowIDColumn = cursorPresent ? c.getColumnIndexOrThrow("_id") : -1;
        setHasStableIds(true);
    }

    @Override
    public final void onBindViewHolder(T holder, int position){
        if(!dataValid) {
            throw new IllegalStateException("Cursor invalid");
        }
        if(!cursor.moveToPosition(position)){
            throw new IllegalStateException("Could not move to position " + position);
        }

        onBindViewHolder(holder, cursor);
    }

    public abstract void onBindViewHolder(T holder, Cursor c);

    public Cursor getCursor(){
        return cursor;
    }

    @Override
    public int getItemCount(){
        if(dataValid && cursor != null){
            return cursor.getCount();
        }else{
            return 0;
        }
    }

    @Override
    public long getItemId(int position){
        if(hasStableIds() && dataValid && cursor != null){
            if(cursor.moveToPosition(position)) {
                return cursor.getLong(rowIDColumn);
            } else {
                return RecyclerView.NO_ID;
            }
        } else {
            return RecyclerView.NO_ID;
        }
    }

    public void changeCursor(Cursor cursor){
        Cursor old = swapCursor(cursor);
        if(old != null){
            old.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor){
        if(newCursor == cursor){
            return null;
        }
        Cursor oldCursor = cursor;
        cursor = newCursor;
        if(newCursor != null){
            rowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            dataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        }else{
            rowIDColumn = -1;
            dataValid = false;
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;
    }

    public CharSequence convertToString(Cursor cursor) {
        return cursor == null ? "" : cursor.toString();
    }
}
