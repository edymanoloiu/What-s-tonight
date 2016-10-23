package com.soft.edi.whatstonight;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edi.myapplication.backend.cityApi.model.City;
import com.example.edi.myapplication.backend.eventApi.model.Event;
import com.example.edi.myapplication.backend.eventApi.model.LocationInfo;
import com.soft.edi.whatstonight.asynctasks.CityAsyncGetter;
import com.soft.edi.whatstonight.asynctasks.EventAsyncDelete;
import com.soft.edi.whatstonight.asynctasks.EventAsyncUpdater;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EventDetails extends AppCompatActivity {

    private LinkedList<String> cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        final long evID = getIntent().getLongExtra("ID", 0);
        final String owner = getIntent().getStringExtra("owner");
        final String evName = getIntent().getStringExtra("eventName");
        final String eventDesc = getIntent().getStringExtra("evDesc");
        final String evDate = getIntent().getStringExtra("date");
        final String evTime = getIntent().getStringExtra("time");
        final String evCity = getIntent().getStringExtra("city");
        final String detLocation = getIntent().getStringExtra("location");
        final String evCategory = getIntent().getStringExtra("category");
        final int evMax = getIntent().getIntExtra("maxNo", 0);
        final int evPart = getIntent().getIntExtra("attendingNo", 0);

        TextView owTv = (TextView) findViewById(R.id.edit_owner_label);
        owTv.setText(owTv.getText() + " " + owner);
        EditText evDescTB = (EditText) findViewById(R.id.edit_ev_desc_tb);
        evDescTB.setText(eventDesc);
        EditText evNameTB = (EditText) findViewById(R.id.edit_ev_name_tb);
        evNameTB.setText(evName);
        EditText evDateTB = (EditText) findViewById(R.id.edit_date_tb);
        evDateTB.setText(evDate);
        EditText evTimeTB = (EditText) findViewById(R.id.edit_time_tb);
        evTimeTB.setText(evTime);
        EditText evLocationTB = (EditText) findViewById(R.id.edit_det_loc_tb);
        evLocationTB.setText(detLocation);

        //populate city spinner
        populateCitySpinner();
        Spinner spCity = (Spinner) findViewById(R.id.edit_city_spinner);
        int index = 1;
        for (int i = 0; i < cities.size(); i++)
            if (cities.get(i).equals(evCity)) {
                index = i;
                break;
            }
        spCity.setSelection(index);

        //populate spinner with categories
        List<Category> categories = new ArrayList<>(EnumSet.allOf(Category.class));
        LinkedList<String> arraySpinner = new LinkedList<>();
        int selectedIndex = 0;
        for (int i = 0; i < categories.size(); i++) {
            arraySpinner.add(categories.get(i).name());
            if (categories.get(i).name().equals(evCategory))
                selectedIndex = i;
        }

        Spinner s = (Spinner) findViewById(R.id.edit_cat_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);
        s.setSelection(selectedIndex);

        //set button on click event
        Button but = (Button) findViewById(R.id.edit_up_ev_button);
        but.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //check event name
                        String eventName = ((TextView) findViewById(R.id.edit_ev_name_tb)).getText().toString();
                        if (eventName.isEmpty()) {
                            Toast.makeText(getBaseContext(), "Please give name of this event", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //check event date
                        String date = ((TextView) findViewById(R.id.edit_date_tb)).getText().toString();
                        if (date.isEmpty()) {
                            Toast.makeText(getBaseContext(), "Please give a date for this event", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //parse event date and check it
                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        Date eventDate = null;
                        try {
                            eventDate = df.parse(date);
                        } catch (ParseException e) {
                            df = new SimpleDateFormat("dd.MM.yyyy");
                            try {
                                eventDate = df.parse(date);
                            } catch (ParseException e1) {
                                Toast.makeText(getBaseContext(), "Please give a correct date (e.q. 24/02/2016 or 24.02.2016)", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        Calendar nextWeek = Calendar.getInstance();
                        nextWeek.setTime(new Date());
                        nextWeek.add(Calendar.DATE, 7);
                        Calendar today = Calendar.getInstance();
                        today.setTime(new Date());
                        today.add(Calendar.DATE, -1);
                        if (today.getTime().after(eventDate) || nextWeek.getTime().before(eventDate)) {
                            Toast.makeText(getBaseContext(), "Please give a date in the next week", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //check event time
                        String time = ((TextView) findViewById(R.id.edit_time_tb)).getText().toString();
                        if (time.isEmpty()) {
                            Toast.makeText(getBaseContext(), "Please give a time for this event", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //parse time
                        df = new SimpleDateFormat("HH:mm");
                        Date eventTime = null;
                        try {
                            eventTime = df.parse(time);
                            String[] timeParts = time.split(":");
                            if (Integer.parseInt(timeParts[0]) > 24 || Integer.parseInt(timeParts[1]) > 59)
                                throw new ParseException("", 0);
                        } catch (ParseException e) {
                            Toast.makeText(getBaseContext(), "Please give a correct time format (e.g. 15:29)", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //check event city
                        Spinner citySpinner = (Spinner) findViewById(R.id.edit_city_spinner);
                        String city = citySpinner.getSelectedItem().toString();
                        if (city.isEmpty()) {
                            Toast.makeText(getBaseContext(), "Please give the city for this event", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //check detailed location
                        String detailedLocation = ((TextView) findViewById(R.id.edit_det_loc_tb)).getText().toString();
                        if (detailedLocation.isEmpty()) {
                            Toast.makeText(getBaseContext(), "Please give a detailed location for this event", Toast.LENGTH_LONG).show();
                            return;
                        }

                        //get category
                        Spinner mySpinner = (Spinner) findViewById(R.id.edit_cat_spinner);
                        String category = mySpinner.getSelectedItem().toString();

                        LocationInfo location = new LocationInfo();
                        location.setCity(city);
                        location.setDetailedLocation(detailedLocation);

                        Event event = new Event();
                        event.setOwnerName(owner);
                        event.setName(eventName);
                        event.setLocation(location);
                        event.setCategory(category);
                        event.setCreationDate(date);
                        event.setEventTime(time);
                        event.setId(evID);
                        event.setMaximumPeopleCount(evMax);
                        event.setParticipantsNo(evPart);

                        if (!((TextView) findViewById(R.id.edit_ev_desc_tb)).getText().toString().isEmpty())
                            event.setDescription(((TextView) findViewById(R.id.edit_ev_desc_tb)).getText().toString());

                        //update event if necessary
                        if (!event.getCategory().equals(evCategory) || !event.getCreationDate().equals(evDate) || !event.getDescription().equals(eventDesc) ||
                                !event.getEventTime().equals(evTime) || !event.getName().equals(evName) || !event.getLocation().getCity().equals(evCity) ||
                                !event.getLocation().getDetailedLocation().equals(detLocation))
                            new EventAsyncUpdater(event).execute();

                        Toast.makeText(getBaseContext(), "Event updated", Toast.LENGTH_SHORT).show();

                        //show events
                        Intent intent = new Intent(getBaseContext(), EventsActivity.class);
                        startActivity(intent);
                    }
                }
        );

        Button delBut = (Button) findViewById(R.id.edit_del_button);
        delBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EventAsyncDelete(evID).execute();

                Intent intent = new Intent(getBaseContext(), EventsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getSupportedCities() {
        cities = new LinkedList<>();
        try {
            List<City> citiesFromDB = new CityAsyncGetter().execute().get();
            if (citiesFromDB != null)
                for (City city : citiesFromDB) {
                    cities.add(city.getName());
                }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void populateCitySpinner() {
        getSupportedCities();
        Spinner s = (Spinner) findViewById(R.id.edit_city_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cities);
        s.setAdapter(adapter);
    }

}
