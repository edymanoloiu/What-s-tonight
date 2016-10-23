package com.soft.edi.whatstonight.asynctasks;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.example.edi.myapplication.backend.eventApi.EventApi;
import com.example.edi.myapplication.backend.eventApi.model.Event;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Edi on 18.09.2016.
 */
public class EventAsyncGetter extends AsyncTask<Void, Void, List<Event>> {
    private static EventApi myApiService = null;
    private String category;
    private String city;
    private String ownerName;

    public EventAsyncGetter(@Nullable String category, @Nullable String city, @Nullable String ownerName) {
        this.category = category;
        this.city = city;
        this.ownerName = ownerName;
    }

    @Override
    protected List<Event> doInBackground(Void... params) {
        if (myApiService == null) { // Only do this once
            EventApi.Builder builder = new EventApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://whats-tonight.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            List<Event> events = myApiService.list().execute().getItems();
            List<Event> filter = new LinkedList<>();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Date eventDate = null;
            Calendar today = Calendar.getInstance();
            today.setTime(new Date());
            today.add(Calendar.DATE, -1);
            for (int i = 0; i < events.size(); i++) {
                try {
                    df = new SimpleDateFormat("dd/MM/yyyy");
                    eventDate = df.parse(events.get(i).getCreationDate());
                    if (today.getTime().before(eventDate))
                        filter.add(events.get(i));
                } catch (ParseException e) {
                    df = new SimpleDateFormat("dd.MM.yyyy");
                    try {
                        eventDate = df.parse(events.get(i).getCreationDate());
                        if (today.getTime().before(eventDate))
                            filter.add(events.get(i));
                    } catch (ParseException e1) {
                    }
                }
            }

            events = filter;
            filter = new LinkedList<>();

            Boolean useCat, useCity, useOwner;
            if (category != null && !category.equals("All"))
                useCat = true;
            else
                useCat = false;
            if (city != null && !city.isEmpty())
                useCity = true;
            else
                useCity = false;
            if (ownerName != null && !ownerName.isEmpty())
                useOwner = true;
            else
                useOwner = false;
            if (!useCat && !useCity && !useOwner)
                return events;
            for (Event ev : events) {
                if (useCat && useCity && useOwner)
                    if (ev.getCategory().equals(category) && ev.getLocation().getCity().equals(city) && ev.getOwnerName().toLowerCase().contains(ownerName.toLowerCase()))
                        filter.add(ev);
                if (useCat && useCity && !useOwner)
                    if (ev.getCategory().equals(category) && ev.getLocation().getCity().equals(city))
                        filter.add(ev);
                if (useCity && !useCat && !useOwner)
                    if (ev.getLocation().getCity().equals(city))
                        filter.add(ev);
                if (useCity && !useCat && useOwner)
                    if (ev.getLocation().getCity().equals(city) && ev.getOwnerName().toLowerCase().contains(ownerName.toLowerCase()))
                        filter.add(ev);
            }
            return filter;
        } catch (IOException e) {
            return Collections.EMPTY_LIST;
        }
    }
}
