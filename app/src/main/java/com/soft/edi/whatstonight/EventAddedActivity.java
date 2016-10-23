package com.soft.edi.whatstonight;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edi.myapplication.backend.cityApi.model.City;
import com.example.edi.myapplication.backend.eventApi.model.Event;
import com.example.edi.myapplication.backend.eventApi.model.LocationInfo;
import com.soft.edi.whatstonight.asynctasks.CityAsyncGetter;
import com.soft.edi.whatstonight.asynctasks.CityAsyncSetter;
import com.soft.edi.whatstonight.asynctasks.EventAsyncSetter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class EventAddedActivity extends AppCompatActivity {

    private String userName;
    private boolean havePerm = true;
    private String cityName;
    private LinkedList<String> cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_added);

        userName = getIntent().getStringExtra("userName");

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        locationManager.getBestProvider(criteria, true);
        Boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSEnabled && !isNetworkEnabled) {
            Toast.makeText(getBaseContext(), "No possibility to get location", Toast.LENGTH_SHORT);
            Button citButton = (Button) findViewById(R.id.currentCityBut);
            citButton.setEnabled(false);
        } else {
            //once  you  know  the  name  of  the  LocationProvider,  you  can  call getLastKnownPosition() to  find  out  where  you  were  recently.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            if (havePerm) {
                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
                if (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location == null)
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses.size() > 0)
                        cityName = addresses.get(0).getLocality().toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //populate spinner with categories
        populateCitySpinner();

        //populate spinner with categories
        List<Category> categories = new ArrayList<>(EnumSet.allOf(Category.class));
        LinkedList<String> arraySpinner = new LinkedList<>();
        for (Category cat : categories)
            arraySpinner.add(cat.name());

        Spinner s = (Spinner) findViewById(R.id.categorySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);

        //set on add even listener
        Button but = (Button) findViewById(R.id.addEventBut);
        but.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //check event name
                        String eventName = ((TextView) findViewById(R.id.eventNameTB)).getText().toString();
                        if (eventName.isEmpty()) {
                            Toast.makeText(getBaseContext(), "Please give name of this event", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //check event date
                        String date = ((TextView) findViewById(R.id.dateTB)).getText().toString();
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
                        String time = ((TextView) findViewById(R.id.timeTB)).getText().toString();
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
                        Spinner citySpinner = (Spinner) findViewById(R.id.citySpinner);
                        String city = citySpinner.getSelectedItem().toString();
                        if (city.isEmpty()) {
                            Toast.makeText(getBaseContext(), "Please give the city for this event", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //check event maximum number of participants
                        String maxNoStr = ((TextView) findViewById(R.id.maxTB)).getText().toString();
                        if (maxNoStr.isEmpty()) {
                            Toast.makeText(getBaseContext(), "Please give a maximum number of participants for this event", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //check detailed location
                        String detailedLocaation = ((TextView) findViewById(R.id.detailedLocationTB)).getText().toString();
                        if (detailedLocaation.isEmpty()) {
                            Toast.makeText(getBaseContext(), "Please give a detailed location for this event", Toast.LENGTH_LONG).show();
                            return;
                        }

                        //get category
                        Spinner mySpinner = (Spinner) findViewById(R.id.categorySpinner);
                        String category = mySpinner.getSelectedItem().toString();

                        LocationInfo location = new LocationInfo();
                        location.setCity(city);
                        location.setDetailedLocation(detailedLocaation);

                        Event event = new Event();
                        event.setOwnerName(userName);
                        event.setName(eventName);
                        event.setLocation(location);
                        event.setCategory(category);
                        event.setMaximumPeopleCount(Integer.parseInt(maxNoStr));
                        event.setCreationDate(date);
                        event.setEventTime(time);

                        if (!((TextView) findViewById(R.id.eventDescriptionTB)).getText().toString().isEmpty())
                            event.setDescription(((TextView) findViewById(R.id.eventDescriptionTB)).getText().toString());

                        new EventAsyncSetter(event).execute();

                        Toast.makeText(getBaseContext(), "New event added", Toast.LENGTH_SHORT).show();

                        //show events
                        Intent intent = new Intent(getBaseContext(), EventsActivity.class);
                        startActivity(intent);
                    }
                }
        );

        Button locationBut = (Button) findViewById(R.id.currentCityBut);
        locationBut.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int selectedIndex = 0;
                        if (!cities.contains(cityName)) {
                            insertNewCityToDB(cityName);
                            populateCitySpinner();
                        }
                        for (int i = 0; i < cities.size(); i++)
                            if (cities.get(i).equals(cityName)) {
                                selectedIndex = i;
                                break;
                            }
                        Spinner s = (Spinner) findViewById(R.id.citySpinner);
                        s.setSelection(selectedIndex);
                    }
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    havePerm = true;
                } else {
                    havePerm = false;
                }
                return;
            }
        }
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
        Spinner s = (Spinner) findViewById(R.id.citySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cities);
        s.setAdapter(adapter);
    }

    private void insertNewCityToDB(String cityName) {
        City city = new City();
        city.setName(cityName);
        try {
            new CityAsyncSetter(city).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
