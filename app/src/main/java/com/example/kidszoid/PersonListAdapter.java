package com.example.kidszoid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PersonListAdapter extends ArrayAdapter<Person> {
    private Context mcontext;
    int mResource;
    public PersonListAdapter (Context context, int resource, ArrayList<Person> objects){
        super(context, resource, objects);
        mcontext = context;
        mResource = resource;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        String time = getItem(position).getTime();

        Person person = new Person(name, time);
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate(mResource, parent,false);

        TextView t_name = (TextView) convertView.findViewById(R.id.textview1);
        TextView t_time = (TextView) convertView.findViewById(R.id.textview2);

        t_name.setText(name);
        t_time.setText(time);
        return convertView;

    }
}
