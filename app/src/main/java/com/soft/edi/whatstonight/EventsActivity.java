package com.soft.edi.whatstonight;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.edi.myapplication.backend.eventApi.model.Event;
import com.facebook.Profile;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class EventsActivity extends AppCompatActivity {

    static SharedPreferences settings;
    private String userName;
    private ListView lView;
    private ArrayList<Event> eventsList = new ArrayList<>();
    private MyListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get filter options
        settings = getSharedPreferences("whatstonight", 0);
        Map<String, ?> filterOptions = settings.getAll();


        userName = getIntent().getStringExtra("userName");
        lView = (ListView) findViewById(R.id.listview_search);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), EventAddedActivity.class);
                intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });

        updateListView(filterOptions);

        //search functionality
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    mAdapter = new MyListAdapter(getBaseContext(), eventsList, userName);
                    lView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                return true;
            }

            public void callSearch(String query) {
                ArrayList<Event> filteredEvents = new ArrayList<>();
                for (Event ev : eventsList)
                    if (ev.getName().toLowerCase().contains(query.toLowerCase()) || (ev.getDescription() != null && ev.getDescription().toLowerCase().contains(query.toLowerCase())))
                        filteredEvents.add(ev);
                mAdapter = new MyListAdapter(getBaseContext(), filteredEvents, userName);
                lView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        });

        //on row item click
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (userName == null)
                    userName = Profile.getCurrentProfile().getFirstName() + " " + Profile.getCurrentProfile().getLastName();
                Event event = (Event) lView.getItemAtPosition(position);
                if (event.getOwnerName() != null && event.getOwnerName().equals(userName)) {
                    Intent intent = new Intent(getBaseContext(), EventDetails.class);
                    intent.putExtra("owner", event.getOwnerName());
                    intent.putExtra("eventName", event.getName());
                    intent.putExtra("date", event.getCreationDate());
                    intent.putExtra("time", event.getEventTime());
                    intent.putExtra("city", event.getLocation().getCity());
                    intent.putExtra("location", event.getLocation().getDetailedLocation());
                    intent.putExtra("category", event.getCategory());
                    intent.putExtra("evDesc", event.getDescription());
                    intent.putExtra("ID", event.getId());
                    intent.putExtra("maxNo", event.getMaximumPeopleCount());
                    intent.putExtra("attendingNo", event.getParticipantsNo());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getBaseContext(), EventDetailsForNormalUser.class);
                    intent.putExtra("owner", event.getOwnerName());
                    intent.putExtra("eventName", event.getName());
                    intent.putExtra("date", event.getCreationDate());
                    intent.putExtra("time", event.getEventTime());
                    intent.putExtra("city", event.getLocation().getCity());
                    intent.putExtra("location", event.getLocation().getDetailedLocation());
                    intent.putExtra("category", event.getCategory());
                    intent.putExtra("evDesc", event.getDescription());
                    intent.putExtra("ID", event.getId());
                    intent.putExtra("attendingNo", event.getParticipantsNo());
                    intent.putExtra("maxNo", event.getMaximumPeopleCount());
                    startActivity(intent);
                }
            }
        });
    }

    private void updateListView(Map<String, ?> filterOptions) {
        //get all events from DB
        //ArrayList<Event> events = (ArrayList<Event>) getIntent().getSerializableExtra("list");
        /*String category = filterOptions.containsKey("category") ? (String) filterOptions.get("category") : null;
        String city = filterOptions.containsKey("city") ? (String) filterOptions.get("city") : null;
        String owner = filterOptions.containsKey("evOwner") ? (String) filterOptions.get("evOwner") : null;
        try {
            events = new EventAsyncGetter(category, city, owner).execute().get();
        } catch (InterruptedException e) {
            Toast.makeText(getBaseContext(), "Unable to get list of events", Toast.LENGTH_LONG);
            return;
        } catch (ExecutionException e) {
            Toast.makeText(getBaseContext(), "Unable to get list of events", Toast.LENGTH_LONG);
            return;
        }*/

        String listSerializedToJson = getIntent().getExtras().getString("list");
        Event[] events = new Gson().fromJson(listSerializedToJson, Event[].class);

        for (Event ev : events)
            if (ev.getName() != null && !ev.getName().isEmpty())
                eventsList.add(ev);

        //put elements into list view
        mAdapter = new MyListAdapter(this, eventsList, userName);
        lView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            Intent intent = new Intent(this, FilterActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_logout) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("LOGOUT", true);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
