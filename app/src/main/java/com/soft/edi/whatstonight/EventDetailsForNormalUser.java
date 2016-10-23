package com.soft.edi.whatstonight;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class EventDetailsForNormalUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_for_normal_user);

        final long evID = getIntent().getLongExtra("ID", 0);
        final String owner = getIntent().getStringExtra("owner");
        final String evName = getIntent().getStringExtra("eventName");
        final String eventDesc = getIntent().getStringExtra("evDesc");
        final String evDate = getIntent().getStringExtra("date");
        final String evTime = getIntent().getStringExtra("time");
        final String evCity = getIntent().getStringExtra("city");
        final String detLocation = getIntent().getStringExtra("location");
        final String evCategory = getIntent().getStringExtra("category");
        int evMax = getIntent().getIntExtra("maxNo", 0);
        int evPart = getIntent().getIntExtra("attendingNo", 0);


        TextView owTv = (TextView) findViewById(R.id.non_owner_label);
        owTv.setText(owTv.getText() + " " + owner);
        TextView nameTv = (TextView) findViewById(R.id.non_evName_label);
        nameTv.setText(nameTv.getText() + " " + evName);
        TextView eventDescTv = (TextView) findViewById(R.id.non_evDesc_label);
        eventDescTv.setText(eventDescTv.getText() + " " + eventDesc);
        TextView evDateTv = (TextView) findViewById(R.id.non_date_label);
        evDateTv.setText(evDateTv.getText() + " " + evDate);
        TextView evTimeTv = (TextView) findViewById(R.id.non_time_label);
        evTimeTv.setText(evTimeTv.getText() + " " + evTime);
        TextView evCityTv = (TextView) findViewById(R.id.non_city_label);
        evCityTv.setText(evCityTv.getText() + " " + evCity);
        TextView evDetLocationTv = (TextView) findViewById(R.id.non_det_loc_label);
        evDetLocationTv.setText(evDetLocationTv.getText() + " " + detLocation);
        TextView evCatTv = (TextView) findViewById(R.id.non_cat_label);
        evCatTv.setText(evCatTv.getText() + " " + evCategory);
        TextView evMaxTv = (TextView) findViewById(R.id.non_max_label);
        evMaxTv.setText(evMaxTv.getText() + " " + evMax);
        TextView evPartTv = (TextView) findViewById(R.id.non_part_label);
        evPartTv.setText(evPartTv.getText() + " " + evPart);
    }
}
