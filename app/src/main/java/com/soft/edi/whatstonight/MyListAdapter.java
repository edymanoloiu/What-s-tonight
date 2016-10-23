package com.soft.edi.whatstonight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edi.myapplication.backend.eventApi.model.Event;
import com.soft.edi.whatstonight.asynctasks.EventAsyncUpdater;

import java.util.ArrayList;
import java.util.LinkedList;

public class MyListAdapter extends ArrayAdapter<Event> {

    private Context context;
    private ArrayList<Event> events;
    private String userName;

    private LayoutInflater mInflater;
    private boolean mNotifyOnChange = true;

    public MyListAdapter(Context context, ArrayList<Event> mPersons, String userName) {
        super(context, R.layout.list_view_row);
        this.context = context;
        this.events = new ArrayList<>(mPersons);
        this.mInflater = LayoutInflater.from(context);
        this.userName = userName;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Event getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public int getPosition(Event item) {
        return events.indexOf(item);
    }

    @Override
    public int getViewTypeCount() {
        return 1; //Number of types + 1 !!!!!!!!
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        int type = getItemViewType(position);
        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
                case 1:
                    convertView = mInflater.inflate(R.layout.list_view_row, parent, false);
                    holder.button = (Button) convertView.findViewById(R.id.joinBut);
                    holder.name = (TextView) convertView.findViewById(R.id.event_name_label);
                    holder.description = (TextView) convertView.findViewById(R.id.event_desc_lab);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Event ev = events.get(position);
        holder.name.setText(events.get(position).getName());
        holder.description.setText(events.get(position).getDescription());
        holder.pos = position;
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (ev.getParticipantsList() == null || !ev.getParticipantsList().contains(userName)) {
                    ev.setParticipantsNo(ev.getParticipantsNo() + 1);
                    if (ev.getParticipantsList() == null)
                        ev.setParticipantsList(new LinkedList<String>());
                    LinkedList<String> part = (LinkedList<String>) ev.getParticipantsList();
                    part.add(userName);
                    ev.setParticipantsList(part);
                    new EventAsyncUpdater(ev).execute();
                    Toast.makeText(context, "You are now participating to " + ev.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "You are already participating to " + ev.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mNotifyOnChange = true;
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }

    //---------------static views for each row-----------//
    static class ViewHolder {
        Button button;
        TextView name;
        TextView description;
        int pos; //to store the position of the item within the list
    }
}
