package com.soft.edi.whatstonight.asynctasks;

import android.os.AsyncTask;

import com.example.edi.myapplication.backend.eventApi.EventApi;
import com.example.edi.myapplication.backend.eventApi.model.Event;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

/**
 * Created by Edi on 24.09.2016.
 */

public class EventAsyncUpdater extends AsyncTask<Void, Void, Boolean> {
    private static EventApi myApiService = null;
    private Event ev;

    public EventAsyncUpdater(Event event) {
        ev = event;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (myApiService == null) { // Only do this once
            EventApi.Builder builder = new EventApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://whats-tonight.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            myApiService.update(ev.getId(),ev).execute();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
