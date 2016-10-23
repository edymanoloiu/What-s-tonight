package com.soft.edi.whatstonight.asynctasks;

import android.os.AsyncTask;

import com.example.edi.myapplication.backend.eventApi.EventApi;
import com.example.edi.myapplication.backend.eventApi.model.Event;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

/**
 * Created by Edi on 24.09.2016.
 */

public class EventGetSingleEventAsync extends AsyncTask<Void, Void, Event> {
    private static EventApi myApiService = null;
    private long ID;

    public EventGetSingleEventAsync(long id) {
        ID = id;
    }

    @Override
    protected Event doInBackground(Void... params) {
        if (myApiService == null) { // Only do this once
            EventApi.Builder builder = new EventApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://whats-tonight.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            return myApiService.get(ID).execute();
        } catch (IOException e) {
            return null;
        }
    }
}
