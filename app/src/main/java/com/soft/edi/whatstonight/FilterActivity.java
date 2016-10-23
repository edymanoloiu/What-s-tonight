package com.soft.edi.whatstonight;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.edi.myapplication.backend.cityApi.model.City;
import com.soft.edi.whatstonight.asynctasks.CityAsyncGetter;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FilterActivity extends AppCompatActivity {
    static SharedPreferences settings;
    static SharedPreferences.Editor editor;
    private LinkedList<String> cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        populateCitySpinner();

        populateCategorySpinner();

        //get latest filter
        settings = getSharedPreferences("whatstonight", 0);
        Map<String, ?> filterOptions = settings.getAll();
        String category = filterOptions.containsKey("category") ? (String) filterOptions.get("category") : null;
        String city = filterOptions.containsKey("city") ? (String) filterOptions.get("city") : null;
        String owner = filterOptions.containsKey("evOwner") ? (String) filterOptions.get("evOwner") : null;
        if (category != null) {
            List<Category> categories = new ArrayList<>(EnumSet.allOf(Category.class));
            LinkedList<String> arraySpinner = new LinkedList<>();
            arraySpinner.add("All");
            int index = 0;
            for (int i = 0; i < categories.size(); i++) {
                arraySpinner.add(categories.get(i).name());
            }
            for (int i = 0; i < arraySpinner.size(); i++)
                if (arraySpinner.get(i).equals(category)) {
                    index = i;
                    break;
                }

            Spinner s = (Spinner) findViewById(R.id.filter_cat_spinner);
            s.setSelection(index);
        }

        if (city != null) {
            int index = 0;
            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).equals(city)) {
                    index = i;
                    break;
                }
            }
            Spinner s = (Spinner) findViewById(R.id.filter_cities_spinner);
            s.setSelection(index);
        }

        if (owner != null) {
            TextView tv = (TextView) findViewById(R.id.filter_owner_tb);
            tv.setText(owner);
        }

        Button but = (Button) findViewById(R.id.filter_apply_button);
        but.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Spinner s = (Spinner) findViewById(R.id.filter_cat_spinner);
                        String category = s.getSelectedItem().toString();
                        Spinner citySpinner = (Spinner) findViewById(R.id.filter_cities_spinner);
                        String city = citySpinner.getSelectedItem().toString();
                        String evOwner = ((TextView) findViewById(R.id.filter_owner_tb)).getText().toString();

                        editor = settings.edit();
                        editor.putString("category", category);
                        editor.putString("city", city);
                        editor.putString("evOwner", evOwner);
                        editor.commit();

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
        Spinner s = (Spinner) findViewById(R.id.filter_cities_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cities);
        s.setAdapter(adapter);
    }

    private void populateCategorySpinner() {
        List<Category> categories = new ArrayList<>(EnumSet.allOf(Category.class));
        LinkedList<String> arraySpinner = new LinkedList<>();
        arraySpinner.add("All");
        for (int i = 0; i < categories.size(); i++) {
            arraySpinner.add(categories.get(i).name());
        }

        Spinner s = (Spinner) findViewById(R.id.filter_cat_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);
    }
}
